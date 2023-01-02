// TODO: For all `pub` visibility in this project, restrict to `pub(crate)`?

use std::{path::PathBuf, ptr::null_mut};

use client_core::client::replies::reply_storage::fs_backend::Backend;
use clients_native_src::client::config::{ConfigAndroid, STORAGE_ABS_PATH_ENVVARKEY};
use config::NymConfig;
use jni::{
    objects::{JClass, JObject, JString},
    sys::{jboolean, jstring},
    JNIEnv,
};
use network_defaults::setup_env;
use nym_client::error::ClientError;
use tracing_subscriber::layer::SubscriberExt;
use utils::{consume_kt_string, produce_kt_string};
mod android_instrumented_tests;
mod clients_native_src;
mod nym_init;
mod nym_run;
mod utils;

use crate::nym_init::Java_com_kaeonx_nymandroidport_jni_NymHandlerKt_nymInitImpl_fallible;
use crate::nym_run::Java_com_kaeonx_nymandroidport_jni_NymHandlerKt_nymRunImpl_fallible;
use crate::utils::consume_kt_nullable_string;

pub const SET_GLOBAL_DEFAULT_DONE_ENVVARKEY: &str = "TOPLEVELINITIMPL_SET_GLOBAL_DEFAULT_DONE";

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_NymHandlerKt_topLevelInitImpl(
    env: JNIEnv,
    class: JClass,
    storage_abs_path: JString,
    config_env_file: JString, // Path pointing to an env file that configures the client.
) {
    call_fallible!(
        Java_com_kaeonx_nymandroidport_jni_NymHandlerKt_topLevelInitImpl_fallible,
        env,
        class,
        storage_abs_path,
        config_env_file
    );
}

/// "In most cases, every Android application runs in its own Linux process. This process is created
/// for the application when some of its code needs to be run, AND will remain running until it is
/// no longer needed and the system needs to reclaim its memory for use by other applications."
/// Source: <https://developer.android.com/guide/components/activities/process-lifecycle>
///
/// Note the word "AND" (emphasis mine). This means that when the application is closed, the Android
/// OS may not necessarily terminate the process. This can be observed by launching the Nym Android
/// Port app, starting the nym run method, closing and deleting the app from the recents menu, then
/// stopping the background task via the notification drawer. All computation from the app has
/// ceased, but execution continues.
///
/// When the app next launches following what's described in the previous paragraph, a new activity
/// and new view model instance is generated, which will cause this method to be called again.
/// But since this method has already been run before (given that the process didn't die), it'll
/// return an Err, and crash the JVM with a `java.lang.RuntimeException: a global default trace
/// dispatcher has already been set` error.
///
/// The environment variable with key `SET_GLOBAL_DEFAULT_DONE_ENVVARKEY` is used to guard against
/// this case.
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_NymHandlerKt_topLevelInitImpl_fallible(
    env: JNIEnv,
    _: JClass,
    storage_abs_path: JString,
    config_env_file: JString, // Path pointing to an env file that configures the client.
) -> Result<(), anyhow::Error> {
    if std::env::var(SET_GLOBAL_DEFAULT_DONE_ENVVARKEY).is_err() {
        let storage_abs_path = consume_kt_string(env, storage_abs_path)?;
        let config_env_file = consume_kt_nullable_string(env, config_env_file)?;

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
        let config_env_file = config_env_file.map(PathBuf::from);
        setup_env(config_env_file); // config_env_file can be provided as an additional argument

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
        // used in the nym init and nym run methods. That trait method takes no arguments, and I
        // cannot change the implementation of the NymConfig trait. Environment variables are just
        // another form of arguments to functions, so I'm using that facility to pass this value to
        // the default_root_directory() function at runtime. This line must be executed before
        // creation of any AndroidConfig structs.
        std::env::set_var(STORAGE_ABS_PATH_ENVVARKEY, &storage_abs_path);

        std::env::set_var(SET_GLOBAL_DEFAULT_DONE_ENVVARKEY, "y");
    }
    Ok(())
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_NymHandlerKt_nymInitImpl_0002dpB8v_1Vc(
    env: JNIEnv,
    class: JClass,
    id: JString,
    gateway: JString,
    force_register_gateway: jboolean,
    nymd_validators: JString,
    api_validators: JString,
    disable_socket: jboolean,
    port: JObject,
    fastmode: jboolean,
    no_cover: jboolean,
    // #[cfg(feature = "coconut")] enabled_credentials_mode: jboolean,
    output_json: jboolean,
) {
    call_fallible!(
        Java_com_kaeonx_nymandroidport_jni_NymHandlerKt_nymInitImpl_fallible,
        env,
        class,
        id,
        gateway,
        force_register_gateway,
        nymd_validators,
        api_validators,
        disable_socket,
        port,
        fastmode,
        no_cover,
        output_json
    );
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_NymHandlerKt_nymRunImpl_0002ds2fY7I4(
    env: JNIEnv,
    class: JClass,
    id: JString,
    nymd_validators: JString,
    api_validators: JString,
    gateway: JString,
    disable_socket: jboolean,
    port: JObject,
    fastmode: jboolean,
    no_cover: jboolean,
    // #[cfg(feature = "coconut")] enabled_credentials_mode: jboolean,
) {
    call_fallible!(
        Java_com_kaeonx_nymandroidport_jni_NymHandlerKt_nymRunImpl_fallible,
        env,
        class,
        id,
        nymd_validators,
        api_validators,
        gateway,
        disable_socket,
        port,
        fastmode,
        no_cover
    );
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_NymHandlerKt_getAddressImpl(
    env: JNIEnv,
    class: JClass,
    id: JString,
) -> jstring {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_NymHandlerKt_getAddressImpl_fallible,
        env,
        class,
        id
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_NymHandlerKt_getAddressImpl_fallible(
    env: JNIEnv,
    _: JClass,
    id: JString,
) -> Result<jstring, anyhow::Error> {
    let id = consume_kt_string(env, id)?;
    let config = match ConfigAndroid::load_from_file(Some(&id)) {
        Ok(cfg) => cfg,
        Err(err) => {
            log::error!("Failed to load config for {}. Are you sure you have run `init` before? (Error was: {})", id, err);
            anyhow::bail!(ClientError::FailedToLoadConfig(id.to_string()));
        }
    };
    let address_string =
        client_core::init::get_client_address_from_stored_keys::<Backend, _>(config.get_base())?
            .to_string();
    Ok(produce_kt_string(env, address_string)?)
}
