// Requires manual sync with nym-client

use client_core::config::GatewayEndpoint;
use client_core::error::ClientCoreError;
use config::NymConfig;
use futures::executor::block_on;
use jni::objects::{JClass, JObject, JString};
use jni::sys::{jbyteArray, jstring};
use jni::JNIEnv;
use std::fs::File;
use std::io::{BufWriter, Write};
use std::path::PathBuf;
use std::ptr::null_mut;

mod android_config; // renamed from config to android_config to avoid name clash
mod utils;

use android_config::{AndroidConfig, SocketType};
use utils::{
    get_non_nullable_string_fallible, get_nullable_integer_fallible, get_nullable_string_fallible,
};

use crate::android_config::STORAGE_ABS_PATH_ENV_VAR_NAME;

#[no_mangle]
#[allow(clippy::missing_const_for_fn)] // nursery warning inapplicable here
pub extern "C" fn Java_com_kaeonx_nymandroidport_NymHandlerKt_topLevelInitImpl() {
    // Consider tracing crate, used by nym-client, if the necessity arises.
    #[cfg(feature = "debug_logs")]
    android_logger::init_once(android_logger::Config::default().with_min_level(log::Level::Trace));
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_NymHandlerKt_nymInitImpl(
    env: JNIEnv,
    class: JClass,
    storage_abs_path: JString,
    id: JString,
    gateway: JString,
    force_register_gateway: bool,
    validators: JString,
    disable_socket: bool,
    port: JObject,
    fastmode: bool,
    // #[cfg(feature = "coconut")] enabled_credentials_mode: bool,
) {
    log::info!("Info logging, this works.");
    call_fallible!(
        Java_com_kaeonx_nymandroidport_NymHandlerKt_nymInitImpl_fallible,
        env,
        class,
        storage_abs_path,
        id,
        gateway,
        force_register_gateway,
        validators,
        disable_socket,
        port,
        fastmode
    )
}

#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_NymHandlerKt_nymInitImpl_fallible(
    env: JNIEnv,
    _: JClass,
    storage_abs_path: JString,
    id: JString,      // Id of the nym-mixnet-client we want to create config for.
    gateway: JString, // (Optional) Id of the gateway we are going to connect to.
    force_register_gateway: bool, // Force register gateway. WARNING: this will overwrite any existing keys for the given id, potentially causing loss of access.
    validators: JString, // (Optional) Comma separated list of rest endpoints of the validators
    disable_socket: bool, // Whether to not start the websocket
    port: JObject, // (Optional) Port for the socket (if applicable) to listen on in all subsequent runs
    fastmode: bool, // Mostly debug-related option to increase default traffic rate so that you would not need to modify config post init
                    // #[cfg(feature = "coconut")] enabled_credentials_mode: bool, // Set this client to work in a enabled credentials mode that would attempt to use gateway with bandwidth credential requirement.
) -> Result<(), String> {
    let storage_abs_path =
        get_non_nullable_string_fallible(env, storage_abs_path, "storage_abs_path")?;
    std::env::set_var(STORAGE_ABS_PATH_ENV_VAR_NAME, &storage_abs_path);
    let id = &get_non_nullable_string_fallible(env, id, "id")?;
    let gateway = get_nullable_string_fallible(env, gateway, "gateway")?;
    let validators = get_nullable_string_fallible(env, validators, "validators")?;
    let port = get_nullable_integer_fallible(env, port, "port")?;
    let port: Option<u16> = match port {
        None => None,
        Some(val) => Some(val.try_into().map_err(|err| {
            format!("port out of range, expected 0-65535, got {} ({})", val, err)
        })?),
    };

    log::info!("storage_abs_path is {}", storage_abs_path);

    let file_path = PathBuf::from(storage_abs_path);
    // let file_path = file_path.join(format!("{}.txt", id));
    let file_path = file_path.join(format!("{}.txt", id));
    let file = File::create(file_path.clone()).map_err(|err| {
        format!(
            "Rust: Unable to open file {} in write mode ({})",
            file_path.to_string_lossy(),
            err
        )
    })?;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //// START: nym_client::commands::init::execute()
    ////////////////////////////////////////////////////////////////////////////////////////////////
    let already_init = AndroidConfig::default_config_file_path(Some(id)).exists();
    if already_init {
        log::info!(
            "Client \"{}\" was already initialised before! \
            Config information will be overwritten (but keys will be kept)!",
            id
        );
    }

    // Usually you only register with the gateway on the first init, however you can force
    // re-registering if wanted.
    let user_wants_force_register = force_register_gateway;

    // If the client was already initialized, don't generate new keys and don't re-register with
    // the gateway (because this would create a new shared key).
    // Unless the user really wants to.
    let register_gateway = !already_init || user_wants_force_register;

    // Attempt to use a user-provided gateway, if possible
    let user_chosen_gateway_id: Option<&str> = gateway.as_deref();

    let mut config = AndroidConfig::new(id);
    // let override_config_fields = OverrideConfig::from(args.clone());
    // config = override_config(config, override_config_fields);
    ////////////////////////////////////////////////////////////////////////////////////////////////
    //// START: nym_client::commands::override_config()
    ////////////////////////////////////////////////////////////////////////////////////////////////
    if let Some(raw_validators) = validators {
        config
            .get_base_mut()
            .set_custom_validator_apis(config::parse_validators(&raw_validators));
    } else if std::env::var(network_defaults::var_names::CONFIGURED).is_ok() {
        let raw_validators = std::env::var(network_defaults::var_names::API_VALIDATOR)
            .map_err(|err| format!("api validator not set ({})", err))?;
        config
            .get_base_mut()
            .set_custom_validator_apis(config::parse_validators(&raw_validators));
    }
    if disable_socket {
        config = config.with_socket(SocketType::None);
    }
    if let Some(port) = port {
        config = config.with_port(port);
    }
    // #[cfg(feature = "coconut")]
    // {
    //     if args.enabled_credentials_mode {
    //         config.get_base_mut().with_disabled_credentials(false)
    //     }
    // }
    if fastmode {
        config.get_base_mut().set_high_default_traffic_volume();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    //// END: nym_client::commands::override_config()
    ////////////////////////////////////////////////////////////////////////////////////////////////

    let gateway = setup_gateway(id, register_gateway, user_chosen_gateway_id, &config);
    // using futures::executor::block_on() instead of .await
    let gateway =
        block_on(gateway).map_err(|err| format!("Failed to setup gateway\nError: {err}"))?;
    config.get_base_mut().with_gateway_endpoint(gateway);

    let config_save_location = config.get_config_file_save_location();
    config
        .save_to_file(None)
        .expect("Failed to save the config file");

    log::info!("Saved configuration file to {:?}", config_save_location);
    log::info!("Using gateway: {}", config.get_base().get_gateway_id());
    log::debug!("Gateway id: {}", config.get_base().get_gateway_id());
    log::debug!("Gateway owner: {}", config.get_base().get_gateway_owner());
    log::debug!(
        "Gateway listener: {}",
        config.get_base().get_gateway_listener()
    );
    log::debug!("Client configuration completed.");

    // Useless, prints to stdout but not visible from Android
    // client_core::init::show_address(config.get_base())
    //     .map_err(|err| format!("Failed to show address\nError: {err}"))?;

    // writeln!(writer, "package com.kaeonx.nymandroidport").map_err(|_| {
    //     format!(
    //         "Rust: Unable to write to file {}",
    //         file_path.to_string_lossy()
    //     )
    // })?;
    // writeln!(writer).map_err(|_| {
    //     format!(
    //         "Rust: Unable to write to file {}",
    //         file_path.to_string_lossy()
    //     )
    // })?;
    // writeln!(writer, "// Writing to Android storage from Rust!").map_err(|_| {
    //     format!(
    //         "Rust: Unable to write to file {}",
    //         file_path.to_string_lossy()
    //     )
    // })?;
    // writeln!(writer).map_err(|_| {
    //     format!(
    //         "Rust: Unable to write to file {}",
    //         file_path.to_string_lossy()
    //     )
    // })?;

    Ok(())
}

async fn setup_gateway(
    id: &str,
    register: bool,
    user_chosen_gateway_id: Option<&str>,
    config: &AndroidConfig,
) -> Result<GatewayEndpoint, ClientCoreError> {
    ////////////////////////////////////////////////////////////////////////////////////////////////
    //// START: nym_client::commands::init::setup_gateway()
    ////////////////////////////////////////////////////////////////////////////////////////////////
    if register {
        // Get the gateway details by querying the validator-api. Either pick one at random or use
        // the chosen one if it's among the available ones.
        log::info!("Configuring gateway");
        let gateway = client_core::init::query_gateway_details(
            config.get_base().get_validator_api_endpoints(),
            user_chosen_gateway_id,
        )
        .await?;
        log::debug!("Querying gateway gives: {}", gateway);

        // Registering with gateway by setting up and writing shared keys to disk
        log::trace!("Registering gateway");
        client_core::init::register_with_gateway_and_store_keys(gateway.clone(), config.get_base())
            .await?;
        log::debug!("Saved all generated keys");

        Ok(gateway.into())
    } else if user_chosen_gateway_id.is_some() {
        // Just set the config, don't register or create any keys
        // This assumes that the user knows what they are doing, and that the existing keys are
        // valid for the gateway being used
        println!("Using gateway provided by user, keeping existing keys");
        let gateway = client_core::init::query_gateway_details(
            config.get_base().get_validator_api_endpoints(),
            user_chosen_gateway_id,
        )
        .await?;
        log::debug!("Querying gateway gives: {}", gateway);
        Ok(gateway.into())
    } else {
        log::debug!("Not registering gateway, will reuse existing config and keys");
        let existing_config = AndroidConfig::load_from_file(Some(id)).map_err(|err| {
            log::error!(
                "Unable to configure gateway: {err}. \n
                Seems like the client was already initialized but it was not possible to read \
                the existing configuration file. \n
                CAUTION: Consider backing up your gateway keys and try force gateway registration, or \
                removing the existing configuration and starting over."
            );
            ClientCoreError::CouldNotLoadExistingGatewayConfiguration(err)
        })?;

        Ok(existing_config.get_base().get_gateway_endpoint().clone())
    }
}
