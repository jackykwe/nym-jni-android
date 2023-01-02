use anyhow::Context;
use jni::{
    objects::{JClass, JObject, JString},
    sys::jboolean,
    JNIEnv,
};

use crate::clients_native_src::commands::run::{execute, Run};
use crate::utils::{
    consume_kt_boolean, consume_kt_nullable_string, consume_kt_nullable_ushort, consume_kt_string,
};

#[allow(non_snake_case)]
#[allow(clippy::too_many_arguments)]
pub fn Java_com_kaeonx_nymandroidport_jni_NymHandlerKt_nymRunImpl_fallible(
    env: JNIEnv,
    _: JClass,
    id: JString,
    nymd_validators: JString,
    api_validators: JString,
    gateway: JString,
    disable_socket: jboolean,
    port: JObject,
    fastmode: jboolean,
    no_cover: jboolean,
    // #[cfg(feature = "coconut")] enabled_credentials_mode: jboolean,
) -> Result<(), anyhow::Error> {
    let args = Run {
        id: consume_kt_string(env, id)?,
        nymd_validators: consume_kt_nullable_string(env, nymd_validators)?,
        api_validators: consume_kt_nullable_string(env, api_validators)?,
        gateway: consume_kt_nullable_string(env, gateway)?,
        disable_socket: consume_kt_boolean(disable_socket),
        port: consume_kt_nullable_ushort(env, port)?,
        fastmode: consume_kt_boolean(fastmode),
        no_cover: consume_kt_boolean(no_cover),
    };

    tokio::runtime::Builder::new_current_thread()
        .enable_all()
        .build()
        .with_context(|| "Failed to setup tokio runtime")?
        .block_on(execute(&args))?;

    Ok(())
}
