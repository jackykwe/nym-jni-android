/*
 * nym/clients/native/src/commands/init.rs
 *
 * Adapted from the above file (from the nym crate) to fit Android ecosystem.
 *
 * This file is copied over and adapted because in the nym crate, it is not publicly visible (due to
 * `pub(crate)` in ./mod.rs), thus it cannot be accessed from nym_jni.
 */

// I avoid reformatting nym code as far as possible
#![allow(clippy::module_name_repetitions)]
#![allow(clippy::struct_excessive_bools)]

// Copyright 2021 - Nym Technologies SA <contact@nymtech.net>
// SPDX-License-Identifier: Apache-2.0

// ? Modified to use this crate's structs
use crate::clients_native_src::{
    client::config::ConfigAndroid,
    commands::{override_config, OverrideConfig},
};

use config::NymConfig;
use nymsphinx::addressing::clients::Recipient;
use serde::Serialize;
use std::fmt::Display;

// ? To fit Android ecosystem: custom implementation
use anyhow::Context;
use client_core::client::replies::reply_storage::fs_backend::Backend;

// ? Copied wholesale, except removal of `#[clap]` macros and making all fields `pub(crate)`
#[derive(Clone)]
pub(crate) struct Init {
    /// Id of the nym-mixnet-client we want to create config for.
    pub(crate) id: String,

    /// Id of the gateway we are going to connect to.
    pub(crate) gateway: Option<String>,

    /// Force register gateway. WARNING: this will overwrite any existing keys for the given id,
    /// potentially causing loss of access.
    pub(crate) force_register_gateway: bool,

    /// Comma separated list of rest endpoints of the nymd validators
    pub(crate) nymd_validators: Option<String>,

    /// Comma separated list of rest endpoints of the API validators
    pub(crate) api_validators: Option<String>,

    /// Whether to not start the websocket
    pub(crate) disable_socket: bool,

    /// Port for the socket (if applicable) to listen on in all subsequent runs
    pub(crate) port: Option<u16>,

    /// Mostly debug-related option to increase default traffic rate so that you would not need to
    /// modify config post init
    pub(crate) fastmode: bool,

    /// Disable loop cover traffic and the Poisson rate limiter (for debugging only)
    pub(crate) no_cover: bool,
    // Set this client to work in a enabled credentials mode that would attempt to use gateway
    // with bandwidth credential requirement.
    // #[cfg(feature = "coconut")]
    // pub(crate) enabled_credentials_mode: bool,
    /// Save a summary of the initialization to a json file
    pub(crate) output_json: bool,
}

// ? Copied wholesale
impl From<Init> for OverrideConfig {
    fn from(init_config: Init) -> Self {
        OverrideConfig {
            nymd_validators: init_config.nymd_validators,
            api_validators: init_config.api_validators,
            disable_socket: init_config.disable_socket,
            port: init_config.port,
            fastmode: init_config.fastmode,
            no_cover: init_config.no_cover,
            // #[cfg(feature = "coconut")]
            // enabled_credentials_mode: init_config.enabled_credentials_mode,
        }
    }
}

// ? Copied wholesale
#[derive(Debug, Serialize)]
pub struct InitResults {
    #[serde(flatten)]
    client_core: client_core::init::InitResults,
    client_listening_port: String,
}

// ? Copied wholesale, except `Config` -> `ConfigAndroid`
impl InitResults {
    fn new(config: &ConfigAndroid, address: &Recipient) -> Self {
        Self {
            client_core: client_core::init::InitResults::new(config.get_base(), address),
            client_listening_port: config.get_listening_port().to_string(),
        }
    }
}

// ? Copied wholesale
impl Display for InitResults {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        writeln!(f, "{}", self.client_core)?;
        write!(f, "Client listening port: {}", self.client_listening_port)
    }
}

// ? Copied wholesale, except:
// ? - returns Result<_, anyhow::Error> instead of Result<_, ClientError>
// ? - uses anyhow's with_context() instead of tap::TapFallible
// ? - `Config` -> `ConfigAndroid`
// ? - `println!` -> `log::info!`
// ? - and some modifications to specific lines
pub(crate) async fn execute(args: &Init) -> Result<(), anyhow::Error> {
    log::info!("Initialising client...");

    let id = &args.id;

    let already_init = ConfigAndroid::default_config_file_path(Some(id)).exists();
    if already_init {
        log::info!(
            "Client \"{}\" was already initialised before! \
            Config information will be overwritten (but keys will be kept)!",
            id
        );
    }

    // Usually you only register with the gateway on the first init, however you can force
    // re-registering if wanted.
    let user_wants_force_register = args.force_register_gateway;

    // If the client was already initialized, don't generate new keys and don't re-register with
    // the gateway (because this would create a new shared key).
    // Unless the user really wants to.
    let register_gateway = !already_init || user_wants_force_register;

    // Attempt to use a user-provided gateway, if possible
    let user_chosen_gateway_id = args.gateway.clone();

    // Load and potentially override config
    let mut config = override_config(ConfigAndroid::new(id), OverrideConfig::from(args.clone()))?;

    // Setup gateway by either registering a new one, or creating a new config from the selected
    // one but with keys kept, or reusing the gateway configuration.
    // ? required to explicitly state the `B` generic type parameter of setup_gateway, due to
    // ? interaction with the anyhow crate. I know that `B` == `Backend` from:
    // ? - the original return type of this `execute()` function is `Result<_, ClientError>`
    // ? - `setup_gateway()`'s return type is `Result<_, ClientCoreError<B>>`
    // ? - in the definition of `ClientError`, there is this line:
    // ?       `ClientCoreError(#[from] ClientCoreError<fs_backend::Backend>)`
    // ?   which tells me that `B` must be `fs_backend::Backend`.
    let gateway = client_core::init::setup_gateway::<Backend, ConfigAndroid, _>(
        register_gateway,
        user_chosen_gateway_id,
        config.get_base(),
    )
    .await
    .with_context(|| "Failed to setup gateway")?;

    config.get_base_mut().with_gateway_endpoint(gateway);

    config
        .save_to_file(None)
        .with_context(|| "Failed to save the config file")?;

    print_saved_config(&config);

    // ? required to explicitly state the `B` generic type parameter of setup_gateway, due to
    // ? interaction with the anyhow crate. Explained above.
    let address =
        client_core::init::get_client_address_from_stored_keys::<Backend, _>(config.get_base())?;
    let init_results = InitResults::new(&config, &address);
    log::info!("{}", init_results);

    // Output summary to a json file, if specified
    if args.output_json {
        client_core::init::output_to_json(&init_results, "client_init_results.json");
    }

    log::info!("\nThe address of this client is: {}\n", address);
    Ok(())
}

// ? Copied wholesale, except:
// ? - `Config` -> `ConfigAndroid`
// ? - `println!` -> `log::info!`
fn print_saved_config(config: &ConfigAndroid) {
    let config_save_location = config.get_config_file_save_location();
    log::info!("Saved configuration file to {:?}", config_save_location);
    log::info!("Using gateway: {}", config.get_base().get_gateway_id());
    log::debug!("Gateway id: {}", config.get_base().get_gateway_id());
    log::debug!("Gateway owner: {}", config.get_base().get_gateway_owner());
    log::debug!(
        "Gateway listener: {}",
        config.get_base().get_gateway_listener()
    );
    log::info!("Client configuration completed.\n");
}
