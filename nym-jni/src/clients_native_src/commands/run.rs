/*
 * nym/clients/native/src/commands/run.rs
 *
 * Adapted from the above file (from the nym crate) to fit Android architecture.
 *
 * This file is copied over because it is hidden in the actual nym crate via `pub(crate)` and cannot
 * be accessed from nym_jni otherwise.
 */

// Copyright 2021 - Nym Technologies SA <contact@nymtech.net>
// SPDX-License-Identifier: Apache-2.0

use anyhow::Context;
use config::NymConfig;
use version_checker::is_minor_version_compatible;

use crate::clients_native_src::client::config::ConfigAndroid;
use crate::clients_native_src::client::NymClientAndroid;
use crate::clients_native_src::commands::{override_config, OverrideConfig};

// ? Copied wholesale, except removal of `#[clap]` macros, `pub(crate) -> pub` and making all fields
// ? `pub`
#[derive(Clone)]
pub struct Run {
    /// Id of the nym-mixnet-client we want to run.
    pub id: String,

    /// Comma separated list of rest endpoints of the validators
    pub validators: Option<String>,

    /// Id of the gateway we want to connect to. If overridden, it is user's responsibility to
    /// ensure prior registration happened
    pub gateway: Option<String>,

    /// Whether to not start the websocket
    pub disable_socket: bool,

    /// Port for the socket to listen on
    pub port: Option<u16>,
    // Set this client to work in a enabled credentials mode that would attempt to use gateway
    // with bandwidth credential requirement.
    // #[cfg(feature = "coconut")]
    // enabled_credentials_mode: bool,
}

// ? Copied wholesale
impl From<Run> for OverrideConfig {
    fn from(run_config: Run) -> Self {
        OverrideConfig {
            validators: run_config.validators,
            disable_socket: run_config.disable_socket,
            port: run_config.port,
            fastmode: false,
            // #[cfg(feature = "coconut")]
            // enabled_credentials_mode: run_config.enabled_credentials_mode,
        }
    }
}

// ? Adapted to suit Android architecture
// this only checks compatibility between config the binary. It does not take into consideration
// network version. It might do so in the future.
fn version_check(cfg: &ConfigAndroid) -> bool {
    let binary_version = env!("CARGO_PKG_VERSION");
    let config_version = cfg.get_base().get_version();
    if binary_version == config_version {
        true
    } else {
        log::warn!("The native-client binary has different version than what is specified in config file! {} and {}", binary_version, config_version);
        if is_minor_version_compatible(binary_version, config_version) {
            log::info!("but they are still semver compatible. However, consider running the `upgrade` command");
            true
        } else {
            log::error!("and they are semver incompatible! - please run the `upgrade` command before attempting `run` again");
            false
        }
    }
}

// ? Adapted to suit Android architecture, and `pub(crate) -> pub`
pub async fn execute(args: &Run) -> Result<(), anyhow::Error> {
    let id = &args.id;

    let mut config = ConfigAndroid::load_from_file(Some(id)).with_context(|| {
        format!(
            "Failed to load config for {}. Are you sure you have run `init` before?",
            id
        )
    })?;

    let override_config_fields = OverrideConfig::from(args.clone());
    config = override_config(config, override_config_fields)?;

    if !version_check(&config) {
        return Err(anyhow::anyhow!("failed the local version check"));
    }

    NymClientAndroid::new(config).run_forever().await;

    Ok(())
}
