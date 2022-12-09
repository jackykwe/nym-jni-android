// Requires manual sync with nym-client

use std::path::PathBuf;

use client_core::config::GatewayEndpoint;
use client_core::error::ClientCoreError;
use config::NymConfig;
use jni::objects::{JClass, JObject, JString};
use jni::JNIEnv;
use network_defaults::setup_env;

mod android_config; // renamed from config to android_config to avoid name clash with config (crate dependency)
mod utils;

use android_config::{AndroidConfig, SocketType};
use utils::{
    get_non_nullable_string_fallible, get_nullable_integer_fallible, get_nullable_string_fallible,
};

use crate::android_config::STORAGE_ABS_PATH_FROM_JAVA_COM_KAEONX_NYMANDROIDPORT_NYMHANDLERKT_NYMINITIMPL_FALLIBLE;

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_NymHandlerKt_topLevelInitImpl(
    env: JNIEnv,
    class: JClass,
    config_env_file: JString, // Path pointing to an env file that configures the client.
) {
    call_fallible!(
        Java_com_kaeonx_nymandroidport_NymHandlerKt_topLevelInitImpl_fallible,
        env,
        class,
        config_env_file
    );
}

#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_NymHandlerKt_topLevelInitImpl_fallible(
    env: JNIEnv,
    _: JClass,
    config_env_file: JString, // Path pointing to an env file that configures the client.
) -> Result<(), String> {
    // TODO Consider tracing crate, used by nym-client, if the necessity arises.
    // TODO @ Saturday: reminder to Daniel for some template code
    #[cfg(feature = "debug_logs")]
    android_logger::init_once(android_logger::Config::default().with_min_level(log::Level::Trace));

    let config_env_file = get_nullable_string_fallible(env, config_env_file, "config_env_file")?;
    let config_env_file = config_env_file.map(PathBuf::from);

    setup_env(config_env_file); // config_env_file can be provided as an additional argument
    Ok(())
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
    );
}

#[allow(non_snake_case)]
#[allow(clippy::too_many_arguments)]
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

    /*
    DONE-TODO: Instead of environment variables, consider top level static mutable variables?
    Considered, but these (static mut) are only allowed in unsafe blocks. I don't want to litter
    unsafe blocks everywhere in code. Sticking to the environment variable strategy should make
    it clear that we're depending on runtime behaviour which cannot be predicted at compile time.
    */
    /*
    DONE-TODO: If proceeding with environment variables, how do you ensure that there are no key
    collisions? I.e. what's your key naming strategy? How about line number pair?
    File name & line number pair (file1definition-lineX, f2usage-lineY) requires manual maintenance;
    I don't see a way to automatically manage this. Instead, I'll take the method name in which the
    environment variable is defined, and append to it a suitable name.
    - Methods aren't very big (by good programming practice), so the programmer can easily ensure
      that within each method, no environment variable keys are repeated.
    - Method names are unlikely to change in the long run, and if they do, programmers should be
      reminded to change the environment variable names in code. This can be indicated in the
      docstring.
    */
    // TODO: topLevelInit() to setup singletons, then all other methods use singletons
    // Hack to pass this to AndroidConfig's NymConfig(trait)::default_root_directory() method
    // That trait method takes no arguments, and I cannot change the implementation of the NymConfig
    // trait. Environment variables are just another form of arguments to functions, so I'm using
    // that facility to pass this value to the default_root_directory() function at runtime.
    // This line must be executed before creation of any AndroidConfig structs.
    std::env::set_var(
        STORAGE_ABS_PATH_FROM_JAVA_COM_KAEONX_NYMANDROIDPORT_NYMHANDLERKT_NYMINITIMPL_FALLIBLE,
        &storage_abs_path,
    );

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
    // using tokio's block_on() instead of direct .await or futures::executor::block_on()
    // TODO: futures::executor::block_on() does not work on aarch64 (works on x86_64); not sure why
    // let gateway = futures::executor::block_on(gateway)
    //     .map_err(|err| format!("Failed to setup gateway\nError: {err}"))?;
    let gateway = tokio::runtime::Builder::new_current_thread()
        .enable_all()
        .build()
        .map_err(|err| format!("Failed to setup tokio runtime ({})", err))?
        .block_on(gateway)
        .map_err(|err| format!("Failed to setup gateway\nError: {err}"))?;
    config.get_base_mut().with_gateway_endpoint(gateway);

    let config_save_location = config.get_config_file_save_location();
    config
        .save_to_file(None)
        .map_err(|err| format!("Failed to save the config file ({})", err))?;

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
        log::info!(
            "Configuring gateway {:?}",
            config.get_base().get_validator_api_endpoints()
        );
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
