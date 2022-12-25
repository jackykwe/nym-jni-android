// Requires manual sync with nym-client

use std::path::PathBuf;

use jni::{
    objects::{JClass, JObject, JString},
    JNIEnv,
};
use network_defaults::setup_env;
use tracing_subscriber::layer::SubscriberExt;
mod android_instrumented_tests;
mod clients_native_src;
mod nym_init;
mod utils;

use crate::nym_init::Java_com_kaeonx_nymandroidport_jni_NymHandlerKt_nymInitImpl_0002dlxgbCg4_fallible;
use crate::utils::consume_kt_nullable_string;

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_NymHandlerKt_topLevelInitImpl(
    env: JNIEnv,
    class: JClass,
    config_env_file: JString, // Path pointing to an env file that configures the client.
) {
    call_fallible!(
        Java_com_kaeonx_nymandroidport_jni_NymHandlerKt_topLevelInitImpl_fallible,
        env,
        class,
        config_env_file
    );
}

#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_NymHandlerKt_topLevelInitImpl_fallible(
    env: JNIEnv,
    _: JClass,
    config_env_file: JString, // Path pointing to an env file that configures the client.
) -> Result<(), anyhow::Error> {
    // Set up log crate to be detectable on Android Studio. Note exact dependency on android_logger 0.11.1.
    android_logger::init_once(
        android_logger::Config::default()
            .with_min_level(log::Level::Trace)
            .with_tag("nym_jni_log"),
    );

    // Set up tracing crate to be detectable on Android Studio
    let android_studio_layer = tracing_android::layer("nym_jni_tracing")?;
    let subscriber = tracing_subscriber::registry().with(android_studio_layer);
    tracing::subscriber::set_global_default(subscriber)?;

    // Actual Nym initialisation, done once per process
    let config_env_file = consume_kt_nullable_string(env, config_env_file)?;
    let config_env_file = config_env_file.map(PathBuf::from);
    setup_env(config_env_file); // config_env_file can be provided as an additional argument

    Ok(())
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_NymHandlerKt_nymInitImpl_0002dlxgbCg4(
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
        Java_com_kaeonx_nymandroidport_jni_NymHandlerKt_nymInitImpl_0002dlxgbCg4_fallible,
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
