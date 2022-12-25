// Requires manual sync with nym-client

use anyhow::Context;
use jni::{
    objects::{JClass, JObject, JString},
    sys::jboolean,
    JNIEnv,
};

use crate::clients_native_src::client::config::STORAGE_ABS_PATH_ENVVARKEY;
use crate::clients_native_src::commands::init::{execute, Init};
use crate::utils::{
    consume_kt_boolean, consume_kt_nullable_string, consume_kt_nullable_ushort, consume_kt_string,
};

#[allow(non_snake_case)]
#[allow(clippy::too_many_arguments)]
pub fn Java_com_kaeonx_nymandroidport_jni_NymHandlerKt_nymInitImpl_fallible(
    env: JNIEnv,
    _: JClass,
    storage_abs_path: JString,
    id: JString,
    gateway: JString,
    force_register_gateway: jboolean,
    validators: JString,
    disable_socket: jboolean,
    port: JObject,
    fastmode: jboolean,
    // #[cfg(feature = "coconut")] enabled_credentials_mode: bool,
) -> Result<(), anyhow::Error> {
    let storage_abs_path = consume_kt_string(env, storage_abs_path)?;

    let args = Init {
        id: consume_kt_string(env, id)?,
        gateway: consume_kt_nullable_string(env, gateway)?,
        force_register_gateway: consume_kt_boolean(force_register_gateway),
        validators: consume_kt_nullable_string(env, validators)?,
        disable_socket: consume_kt_boolean(disable_socket),
        port: consume_kt_nullable_ushort(env, port)?,
        fastmode: consume_kt_boolean(fastmode),
        // #[cfg(feature = "coconut")]
        // enabled_credentials_mode,
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
    std::env::set_var(STORAGE_ABS_PATH_ENVVARKEY, &storage_abs_path);

    // using tokio's block_on() instead of direct .await or futures::executor::block_on()
    // TODO: futures::executor::block_on() does not work on aarch64 (works on x86_64); not sure why
    tokio::runtime::Builder::new_current_thread()
        .enable_all()
        .build()
        .with_context(|| "Failed to setup tokio runtime")?
        .block_on(execute(&args))?;

    Ok(())
}
