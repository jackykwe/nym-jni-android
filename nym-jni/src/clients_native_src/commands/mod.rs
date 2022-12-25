/*
 * nym/clients/native/src/commands/mod.rs
 *
 * Essentially the same as the above file (from the nym crate), minus some minor adjustments to make
 * it fit into nym_jni.
 *
 * This file is copied over because it is hidden in the actual nym crate via `pub(crate)` and cannot
 * be accessed from nym_jni otherwise.
 */

pub mod init;
pub mod run;

use anyhow::Context;

use crate::clients_native_src::client::config::{ConfigAndroid, SocketType};

// ? Copied wholesale
// Configuration that can be overridden.
pub struct OverrideConfig {
    validators: Option<String>,
    disable_socket: bool,
    port: Option<u16>,
    fastmode: bool,
    // #[cfg(feature = "coconut")]
    // enabled_credentials_mode: bool,
}

// ? Adapted to suit Android architecture
pub fn override_config(
    mut config: ConfigAndroid,
    args: OverrideConfig,
) -> Result<ConfigAndroid, anyhow::Error> {
    if let Some(raw_validators) = args.validators {
        config
            .get_base_mut()
            .set_custom_validator_apis(config::parse_validators(&raw_validators));
    } else if std::env::var(network_defaults::var_names::CONFIGURED).is_ok() {
        let raw_validators = std::env::var(network_defaults::var_names::API_VALIDATOR)
            .with_context(|| "api validator not set")?;
        config
            .get_base_mut()
            .set_custom_validator_apis(config::parse_validators(&raw_validators));
    }

    if args.disable_socket {
        config = config.with_socket(SocketType::None);
    }

    if let Some(port) = args.port {
        config = config.with_port(port);
    }

    // #[cfg(feature = "coconut")]
    // {
    //     if args.enabled_credentials_mode {
    //         config.get_base_mut().with_disabled_credentials(false)
    //     }
    // }

    if args.fastmode {
        config.get_base_mut().set_high_default_traffic_volume();
    }

    Ok(config)
}
