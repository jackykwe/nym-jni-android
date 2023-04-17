/*
 * nym/clients/native/src/commands/run.rs
 *
 * Adapted from the above file (from the nym crate) to fit Android ecosystem.
 *
 * This file is copied over and adapted because in the nym crate, it is not publicly visible (due to
 * `pub(crate)` in ./mod.rs), thus it cannot be accessed from nym_jni.
 */

// I avoid reformatting nym code as far as possible
#![allow(clippy::wildcard_imports)]
#![allow(dead_code)]

// Copyright 2021 - Nym Technologies SA <contact@nymtech.net>
// SPDX-License-Identifier: Apache-2.0

// ? Modified to use this crate's structs
use crate::clients_native_src::{
    client::{config::ConfigAndroid, SocketClientAndroid},
    commands::{override_config, OverrideConfig},
};
use nym_client::error::ClientError;

use config::NymConfig;
use log::*;
use version_checker::is_minor_version_compatible;

// ? To fit Android ecosystem: custom implementation
use jni::objects::JObject;
use jni::JNIEnv;

// ? Copied wholesale, except removal of `#[clap]` macros, making all fields `pub(crate)`
#[derive(Clone)]
pub(crate) struct Run {
    /// Id of the nym-mixnet-client we want to run.
    pub(crate) id: String,

    /// Comma separated list of rest endpoints of the nymd validators
    pub(crate) nymd_validators: Option<String>,

    /// Comma separated list of rest endpoints of the API validators
    pub(crate) api_validators: Option<String>,

    /// Id of the gateway we want to connect to. If overridden, it is user's responsibility to
    /// ensure prior registration happened
    pub(crate) gateway: Option<String>,

    /// Whether to not start the websocket
    pub(crate) disable_socket: bool,

    /// Port for the socket to listen on
    pub(crate) port: Option<u16>,

    /// Mostly debug-related option to increase default traffic rate so that you would not need to
    /// modify config post init
    pub(crate) fastmode: bool,

    /// Disable loop cover traffic and the Poisson rate limiter (for debugging only)
    pub(crate) no_cover: bool,
    // Set this client to work in a enabled credentials mode that would attempt to use gateway
    // with bandwidth credential requirement.
    // #[cfg(feature = "coconut")]
    // pub enabled_credentials_mode: bool,
}

// ? Copied wholesale
impl From<Run> for OverrideConfig {
    fn from(run_config: Run) -> Self {
        OverrideConfig {
            nymd_validators: run_config.nymd_validators,
            api_validators: run_config.api_validators,
            disable_socket: run_config.disable_socket,
            port: run_config.port,
            fastmode: run_config.fastmode,
            no_cover: run_config.no_cover,
            // #[cfg(feature = "coconut")]
            // enabled_credentials_mode: run_config.enabled_credentials_mode,
        }
    }
}

// ? Copied wholesale, except `Config` -> `ConfigAndroid`
// this only checks compatibility between config the binary. It does not take into consideration
// network version. It might do so in the future.
fn version_check(cfg: &ConfigAndroid) -> bool {
    let binary_version = env!("CARGO_PKG_VERSION");
    let config_version = cfg.get_base().get_version();
    if binary_version == config_version {
        true
    } else {
        warn!("The native-client binary has different version than what is specified in config file! {} and {}", binary_version, config_version);
        if is_minor_version_compatible(binary_version, config_version) {
            info!("but they are still semver compatible. However, consider running the `upgrade` command");
            true
        } else {
            error!("and they are semver incompatible! - please run the `upgrade` command before attempting `run` again");
            false
        }
    }
}

// ? Copied wholesale, except:
// ? - returns Result<_, anyhow::Error> instead of Result<_, ClientError>
// ? - Accepts a JNIEnv and JObject (NymRunForegroundService) to enable synchronisation with Android
// ?   application during setup and teardown
// ? - `Config` -> `ConfigAndroid`
// ? - uses `anyhow::bail!` to return early with an Err (for context: <https://stackoverflow.com/q/67656431>)
pub(crate) async fn execute(
    args: &Run,
    env: JNIEnv<'_>,
    nym_run_foreground_service: JObject<'_>,
) -> Result<(), anyhow::Error> {
    let id = &args.id;

    let mut config = match ConfigAndroid::load_from_file(Some(id)) {
        Ok(cfg) => cfg,
        Err(err) => {
            error!("Failed to load config for {}. Are you sure you have run `init` before? (Error was: {})", id, err);
            anyhow::bail!(ClientError::FailedToLoadConfig(id.to_string()));
        }
    };

    let override_config_fields = OverrideConfig::from(args.clone());
    config = override_config(config, override_config_fields)?;

    if config.get_base_mut().set_empty_fields_to_defaults() {
        warn!("some of the core config options were left unset. the default values are going to get used instead.");
    }

    if !version_check(&config) {
        error!("failed the local version check");
        anyhow::bail!(ClientError::FailedLocalVersionCheck);
    }

    if let Err(client_err) = SocketClientAndroid::new(config)
        .run_socket_forever(env, nym_run_foreground_service)
        .await
    {
        anyhow::bail!(client_err);
    }
    Ok(())
}
