use std::fs::File;
use std::io::{BufWriter, Write};
use std::path::PathBuf;
use std::ptr::null_mut;

use jni::objects::{JClass, JString};
use jni::sys::jstring;
use jni::JNIEnv;
use log::{info, warn};

mod utils;

#[no_mangle]
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
) -> jstring {
    info!("Info logging, this works.");
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_NymHandlerKt_nymInitImpl_fallible,
        env,
        class,
        storage_abs_path,
        id
    )
}

#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_NymHandlerKt_nymInitImpl_fallible(
    env: JNIEnv,
    _: JClass,
    storage_abs_path: JString,
    id: JString,
) -> Result<jstring, String> {
    warn!("warn logging, this works.");

    let storage_abs_path: String = env
        .get_string(storage_abs_path)
        .map(Into::into)
        .map_err(|_| String::from("Rust: Unable to get storage_abs_path from Kotlin"))?;

    let id: String = env
        .get_string(id)
        .map(Into::into)
        .map_err(|_| String::from("Rust: Unable to get id from Kotlin"))?;

    let file_path = PathBuf::from(storage_abs_path);
    let file_path = file_path.join(format!("{}.txt", id));
    let file = File::create(file_path.clone()).map_err(|err| {
        format!(
            "Rust: Unable to open file {} in write mode ({})",
            file_path.to_string_lossy(),
            err
        )
    })?;

    // let already_init = file_path.exists();
    // if already_init {
    //     println!(
    //         "Client \"{}\" was already initialised before! \
    //         Config information will be overwritten (but keys will be kept)!",
    //         id
    //     );
    // }

    // BufWriter to buffer repeated writes, reduces number of syscalls
    let mut writer = BufWriter::new(file);

    // // Usually you only register with the gateway on the first init, however you can force
    // // re-registering if wanted.
    // let user_wants_force_register = args.force_register_gateway;

    // // If the client was already initialized, don't generate new keys and don't re-register with
    // // the gateway (because this would create a new shared key).
    // // Unless the user really wants to.
    // let register_gateway = !already_init || user_wants_force_register;

    // // Attempt to use a user-provided gateway, if possible
    // let user_chosen_gateway_id = args.gateway.as_deref();

    // let mut config = Config::new(id);
    // let override_config_fields = OverrideConfig::from(args.clone());
    // config = override_config(config, override_config_fields);

    // let gateway = setup_gateway(id, register_gateway, user_chosen_gateway_id, &config)
    //     .await
    //     .unwrap_or_else(|err| {
    //         eprintln!("Failed to setup gateway\nError: {err}");
    //         std::process::exit(1)
    //     });
    // config.get_base_mut().with_gateway_endpoint(gateway);

    // let config_save_location = config.get_config_file_save_location();
    // config
    //     .save_to_file(None)
    //     .expect("Failed to save the config file");

    // println!("Saved configuration file to {:?}", config_save_location);
    // println!("Using gateway: {}", config.get_base().get_gateway_id());
    // log::debug!("Gateway id: {}", config.get_base().get_gateway_id());
    // log::debug!("Gateway owner: {}", config.get_base().get_gateway_owner());
    // log::debug!(
    //     "Gateway listener: {}",
    //     config.get_base().get_gateway_listener()
    // );
    // println!("Client configuration completed.");

    // client_core::init::show_address(config.get_base()).unwrap_or_else(|err| {
    //     eprintln!("Failed to show address\nError: {err}");
    //     std::process::exit(1)
    // });

    writeln!(writer, "package com.kaeonx.nymandroidport").map_err(|_| {
        format!(
            "Rust: Unable to write to file {}",
            file_path.to_string_lossy()
        )
    })?;
    writeln!(writer).map_err(|_| {
        format!(
            "Rust: Unable to write to file {}",
            file_path.to_string_lossy()
        )
    })?;
    writeln!(writer, "// Writing to Android storage from Rust!").map_err(|_| {
        format!(
            "Rust: Unable to write to file {}",
            file_path.to_string_lossy()
        )
    })?;
    writeln!(writer).map_err(|_| {
        format!(
            "Rust: Unable to write to file {}",
            file_path.to_string_lossy()
        )
    })?;

    env.new_string(format!(
        "Rust: Successfully wrote to {}!",
        file_path.to_string_lossy()
    ))
    .map(JString::into_raw)
    .map_err(|err| format!("Rust: Unable to create jstring ({})", err))
}
