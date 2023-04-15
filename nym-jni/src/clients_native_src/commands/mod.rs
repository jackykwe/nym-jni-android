/*
 * nym/clients/native/src/commands/mod.rs
 *
 * Essentially the same as the above file (from the nym crate), minus some minor adjustments to make
 * it fit into nym_jni.
 *
 * This file is copied over and adapted because in the nym crate, it hides the init and run modules
 * with `pub(crate)`, thus they cannot be accessed from nym_jni. Also, we need to adapt
 * override_config() to use ConfigAndroid (nym_jni crate) instead of Config (nym crate).
 */

// I avoid reformatting nym code as far as possible
#![allow(clippy::expect_used)]

// ? Changed from `pub(crate)` -> `pub`
pub mod init;
pub mod run;

use anyhow::Context;

use crate::clients_native_src::client::config::{ConfigAndroid, SocketType};

// ? Copied wholesale
// Configuration that can be overridden.
pub struct OverrideConfig {
    nymd_validators: Option<String>,
    api_validators: Option<String>,
    disable_socket: bool,
    port: Option<u16>,
    fastmode: bool,
    no_cover: bool,
    // #[cfg(feature = "coconut")]
    // enabled_credentials_mode: bool,
}

// ? Copied wholesale, except:
// ? - returns `Result<ConfigAndroid, _>` instead of `ConfigAndroid`
// ? - uses anyhow's with_context() (allows for graceful failure in nym_jni) instead of expect
// ?   (which crashes the program)
pub fn override_config(
    mut config: ConfigAndroid,
    args: OverrideConfig,
) -> Result<ConfigAndroid, anyhow::Error> {
    if let Some(raw_validators) = args.nymd_validators {
        config
            .get_base_mut()
            .set_custom_validators(config::parse_validators(&raw_validators));
    } else if std::env::var(network_defaults::var_names::CONFIGURED).is_ok() {
        let raw_validators = std::env::var(network_defaults::var_names::NYMD_VALIDATOR)
            .expect("nymd validator not set");
        config
            .get_base_mut()
            .set_custom_validators(config::parse_validators(&raw_validators));
    }
    if let Some(raw_validators) = args.api_validators {
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

    if args.no_cover {
        config.get_base_mut().set_no_cover_traffic();
    }

    Ok(config)
}
