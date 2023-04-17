/*
 * nym/clients/native/src/commands/mod.rs
 *
 * Essentially the same as the above file (from the nym crate), minus some minor adjustments to make
 * it fit into nym_jni. Some lines were not copied over as they are not used on Android.
 *
 * This file is copied over and adapted because in the nym crate, it hides the init and run modules
 * with `pub(crate)`, thus they cannot be accessed from nym_jni. Also, we need to adapt
 * override_config() to use ConfigAndroid (nym_jni crate) instead of Config (nym crate).
 */

// I avoid reformatting nym code as far as possible
#![allow(clippy::expect_used)]

// Copyright 2021 - Nym Technologies SA <contact@nymtech.net>
// SPDX-License-Identifier: Apache-2.0

// ? Modified to use this crate's structs
use crate::clients_native_src::client::config::{ConfigAndroid, SocketType};

// ? To fit Android ecosystem: custom implementation
use anyhow::Context;

// ? Copied wholesale
pub(crate) mod init;
pub(crate) mod run;

// ? Copied wholesale
// Configuration that can be overridden.
pub(crate) struct OverrideConfig {
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
pub(crate) fn override_config(
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
