use anyhow::Context;
use jni::{
    objects::{JClass, JObject, JString},
    sys::jboolean,
    JNIEnv,
};

use crate::clients_native_src::client::config::STORAGE_ABS_PATH_ENVVARKEY;
use crate::clients_native_src::commands::run::{execute, Run};
use crate::utils::{
    consume_kt_boolean, consume_kt_nullable_string, consume_kt_nullable_ushort, consume_kt_string,
};

#[allow(non_snake_case)]
#[allow(clippy::too_many_arguments)]
pub fn Java_com_kaeonx_nymandroidport_jni_NymHandlerKt_nymRunImpl_fallible(
    env: JNIEnv,
    _: JClass,
    storage_abs_path: JString,
    id: JString,
    validators: JString,
    gateway: JString,
    disable_socket: jboolean,
    port: JObject,
) -> Result<(), anyhow::Error> {
    let storage_abs_path = consume_kt_string(env, storage_abs_path)?;

    std::env::set_var(STORAGE_ABS_PATH_ENVVARKEY, &storage_abs_path);

    let args = Run {
        id: consume_kt_string(env, id)?,
        validators: consume_kt_nullable_string(env, validators)?,
        gateway: consume_kt_nullable_string(env, gateway)?,
        disable_socket: consume_kt_boolean(disable_socket),
        port: consume_kt_nullable_ushort(env, port)?,
    };

    tokio::runtime::Builder::new_current_thread()
        .enable_all()
        .build()
        .with_context(|| "Failed to setup tokio runtime")?
        .block_on(execute(&args))?;

    Ok(())
}
