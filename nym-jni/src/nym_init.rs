// Requires manual sync with nym-client

use anyhow::Context;
use jni::{
    objects::{JClass, JObject, JString},
    sys::jboolean,
    JNIEnv,
};

use crate::clients_native_src::commands::init::{execute, Init};
use crate::utils::{
    consume_kt_boolean, consume_kt_nullable_string, consume_kt_nullable_ushort, consume_kt_string,
};

#[allow(non_snake_case)]
#[allow(clippy::too_many_arguments)]
pub fn Java_com_kaeonx_nymandroidport_jni_NymHandlerKt_nymInitImpl_fallible(
    env: JNIEnv,
    _: JClass,
    id: JString,
    gateway: JString,
    force_register_gateway: jboolean,
    validators: JString,
    disable_socket: jboolean,
    port: JObject,
    fastmode: jboolean,
    // #[cfg(feature = "coconut")] enabled_credentials_mode: bool,
) -> Result<(), anyhow::Error> {
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

    // using tokio's block_on() instead of direct .await or futures::executor::block_on()
    // TODO: futures::executor::block_on() does not work on aarch64 (works on x86_64); not sure why
    tokio::runtime::Builder::new_current_thread()
        .enable_all()
        .build()
        .with_context(|| "Failed to setup tokio runtime")?
        .block_on(execute(&args))?;

    Ok(())
}
