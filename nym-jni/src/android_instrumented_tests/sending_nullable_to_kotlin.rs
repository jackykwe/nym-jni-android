// Corresponds to ReceivingNullableFromRustHelpers.kt

use std::ptr::null_mut;

use jni::{
    errors::Error as JNIError,
    objects::JClass,
    sys::{jobject, jstring},
    JNIEnv,
};

use crate::{
    call_fallible_or_else,
    jvm_kotlin_typing::{
        produce_kt_nullable_boolean, produce_kt_nullable_byte, produce_kt_nullable_double,
        produce_kt_nullable_float, produce_kt_nullable_int, produce_kt_nullable_long,
        produce_kt_nullable_short, produce_kt_nullable_string, produce_kt_nullable_ubyte,
        produce_kt_nullable_uint, produce_kt_nullable_ulong, produce_kt_nullable_ushort,
    },
};

const PRE_DETERMINED_STRING: &str = "the brown fox jumps over the lazy dog";

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableBooleanTrueImpl(
    env: JNIEnv,
    class: JClass,
) -> jobject {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableBooleanTrueImpl_fallible,
        env,
        class
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableBooleanTrueImpl_fallible(
    env: JNIEnv,
    _: JClass,
) -> Result<jobject, JNIError> {
    produce_kt_nullable_boolean(env, Some(true))
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableBooleanFalseImpl(
    env: JNIEnv,
    class: JClass,
) -> jobject {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableBooleanFalseImpl_fallible,
        env,
        class
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableBooleanFalseImpl_fallible(
    env: JNIEnv,
    _: JClass,
) -> Result<jobject, JNIError> {
    produce_kt_nullable_boolean(env, Some(false))
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableBooleanNullImpl(
    env: JNIEnv,
    class: JClass,
) -> jobject {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableBooleanNullImpl_fallible,
        env,
        class
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableBooleanNullImpl_fallible(
    env: JNIEnv,
    _: JClass,
) -> Result<jobject, JNIError> {
    produce_kt_nullable_boolean(env, None)
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableByteMinImpl(
    env: JNIEnv,
    class: JClass,
) -> jobject {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableByteMinImpl_fallible,
        env,
        class
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableByteMinImpl_fallible(
    env: JNIEnv,
    _: JClass,
) -> Result<jobject, JNIError> {
    produce_kt_nullable_byte(env, Some(i8::MIN))
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableByteMaxImpl(
    env: JNIEnv,
    class: JClass,
) -> jobject {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableByteMaxImpl_fallible,
        env,
        class
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableByteMaxImpl_fallible(
    env: JNIEnv,
    _: JClass,
) -> Result<jobject, JNIError> {
    produce_kt_nullable_byte(env, Some(i8::MAX))
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableByteNullImpl(
    env: JNIEnv,
    class: JClass,
) -> jobject {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableByteNullImpl_fallible,
        env,
        class
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableByteNullImpl_fallible(
    env: JNIEnv,
    _: JClass,
) -> Result<jobject, JNIError> {
    produce_kt_nullable_byte(env, None)
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableUByteMinImpl(
    env: JNIEnv,
    class: JClass,
) -> jobject {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableUByteMinImpl_fallible,
        env,
        class
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableUByteMinImpl_fallible(
    env: JNIEnv,
    _: JClass,
) -> Result<jobject, JNIError> {
    produce_kt_nullable_ubyte(env, Some(u8::MIN))
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableUByteMaxImpl(
    env: JNIEnv,
    class: JClass,
) -> jobject {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableUByteMaxImpl_fallible,
        env,
        class
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableUByteMaxImpl_fallible(
    env: JNIEnv,
    _: JClass,
) -> Result<jobject, JNIError> {
    produce_kt_nullable_ubyte(env, Some(u8::MAX))
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableUByteNullImpl(
    env: JNIEnv,
    class: JClass,
) -> jobject {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableUByteNullImpl_fallible,
        env,
        class
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableUByteNullImpl_fallible(
    env: JNIEnv,
    _: JClass,
) -> Result<jobject, JNIError> {
    produce_kt_nullable_ubyte(env, None)
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableShortMinImpl(
    env: JNIEnv,
    class: JClass,
) -> jobject {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableShortMinImpl_fallible,
        env,
        class
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableShortMinImpl_fallible(
    env: JNIEnv,
    _: JClass,
) -> Result<jobject, JNIError> {
    produce_kt_nullable_short(env, Some(i16::MIN))
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableShortMaxImpl(
    env: JNIEnv,
    class: JClass,
) -> jobject {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableShortMaxImpl_fallible,
        env,
        class
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableShortMaxImpl_fallible(
    env: JNIEnv,
    _: JClass,
) -> Result<jobject, JNIError> {
    produce_kt_nullable_short(env, Some(i16::MAX))
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableShortNullImpl(
    env: JNIEnv,
    class: JClass,
) -> jobject {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableShortNullImpl_fallible,
        env,
        class
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableShortNullImpl_fallible(
    env: JNIEnv,
    _: JClass,
) -> Result<jobject, JNIError> {
    produce_kt_nullable_short(env, None)
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableUShortMinImpl(
    env: JNIEnv,
    class: JClass,
) -> jobject {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableUShortMinImpl_fallible,
        env,
        class
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableUShortMinImpl_fallible(
    env: JNIEnv,
    _: JClass,
) -> Result<jobject, JNIError> {
    produce_kt_nullable_ushort(env, Some(u16::MIN))
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableUShortMaxImpl(
    env: JNIEnv,
    class: JClass,
) -> jobject {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableUShortMaxImpl_fallible,
        env,
        class
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableUShortMaxImpl_fallible(
    env: JNIEnv,
    _: JClass,
) -> Result<jobject, JNIError> {
    produce_kt_nullable_ushort(env, Some(u16::MAX))
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableUShortNullImpl(
    env: JNIEnv,
    class: JClass,
) -> jobject {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableUShortNullImpl_fallible,
        env,
        class
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableUShortNullImpl_fallible(
    env: JNIEnv,
    _: JClass,
) -> Result<jobject, JNIError> {
    produce_kt_nullable_ushort(env, None)
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableIntMinImpl(
    env: JNIEnv,
    class: JClass,
) -> jobject {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableIntMinImpl_fallible,
        env,
        class
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableIntMinImpl_fallible(
    env: JNIEnv,
    _: JClass,
) -> Result<jobject, JNIError> {
    produce_kt_nullable_int(env, Some(i32::MIN))
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableIntMaxImpl(
    env: JNIEnv,
    class: JClass,
) -> jobject {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableIntMaxImpl_fallible,
        env,
        class
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableIntMaxImpl_fallible(
    env: JNIEnv,
    _: JClass,
) -> Result<jobject, JNIError> {
    produce_kt_nullable_int(env, Some(i32::MAX))
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableIntNullImpl(
    env: JNIEnv,
    class: JClass,
) -> jobject {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableIntNullImpl_fallible,
        env,
        class
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableIntNullImpl_fallible(
    env: JNIEnv,
    _: JClass,
) -> Result<jobject, JNIError> {
    produce_kt_nullable_int(env, None)
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableUIntMinImpl(
    env: JNIEnv,
    class: JClass,
) -> jobject {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableUIntMinImpl_fallible,
        env,
        class
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableUIntMinImpl_fallible(
    env: JNIEnv,
    _: JClass,
) -> Result<jobject, JNIError> {
    produce_kt_nullable_uint(env, Some(u32::MIN))
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableUIntMaxImpl(
    env: JNIEnv,
    class: JClass,
) -> jobject {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableUIntMaxImpl_fallible,
        env,
        class
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableUIntMaxImpl_fallible(
    env: JNIEnv,
    _: JClass,
) -> Result<jobject, JNIError> {
    produce_kt_nullable_uint(env, Some(u32::MAX))
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableUIntNullImpl(
    env: JNIEnv,
    class: JClass,
) -> jobject {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableUIntNullImpl_fallible,
        env,
        class
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableUIntNullImpl_fallible(
    env: JNIEnv,
    _: JClass,
) -> Result<jobject, JNIError> {
    produce_kt_nullable_uint(env, None)
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableLongMinImpl(
    env: JNIEnv,
    class: JClass,
) -> jobject {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableLongMinImpl_fallible,
        env,
        class
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableLongMinImpl_fallible(
    env: JNIEnv,
    _: JClass,
) -> Result<jobject, JNIError> {
    produce_kt_nullable_long(env, Some(i64::MIN))
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableLongMaxImpl(
    env: JNIEnv,
    class: JClass,
) -> jobject {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableLongMaxImpl_fallible,
        env,
        class
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableLongMaxImpl_fallible(
    env: JNIEnv,
    _: JClass,
) -> Result<jobject, JNIError> {
    produce_kt_nullable_long(env, Some(i64::MAX))
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableLongNullImpl(
    env: JNIEnv,
    class: JClass,
) -> jobject {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableLongNullImpl_fallible,
        env,
        class
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableLongNullImpl_fallible(
    env: JNIEnv,
    _: JClass,
) -> Result<jobject, JNIError> {
    produce_kt_nullable_long(env, None)
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableULongMinImpl(
    env: JNIEnv,
    class: JClass,
) -> jobject {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableULongMinImpl_fallible,
        env,
        class
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableULongMinImpl_fallible(
    env: JNIEnv,
    _: JClass,
) -> Result<jobject, JNIError> {
    produce_kt_nullable_ulong(env, Some(u64::MIN))
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableULongMaxImpl(
    env: JNIEnv,
    class: JClass,
) -> jobject {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableULongMaxImpl_fallible,
        env,
        class
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableULongMaxImpl_fallible(
    env: JNIEnv,
    _: JClass,
) -> Result<jobject, JNIError> {
    produce_kt_nullable_ulong(env, Some(u64::MAX))
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableULongNullImpl(
    env: JNIEnv,
    class: JClass,
) -> jobject {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableULongNullImpl_fallible,
        env,
        class
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableULongNullImpl_fallible(
    env: JNIEnv,
    _: JClass,
) -> Result<jobject, JNIError> {
    produce_kt_nullable_ulong(env, None)
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableFloatMinImpl(
    env: JNIEnv,
    class: JClass,
) -> jobject {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableFloatMinImpl_fallible,
        env,
        class
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableFloatMinImpl_fallible(
    env: JNIEnv,
    _: JClass,
) -> Result<jobject, JNIError> {
    // Kotlin's Float.MIN_VALUE is the smallest subnormal number representable with 32 bits
    // (0x0...01). However, Rust's f32::MIN_POSITIVE only gives the smallest normal number.
    // There is no constant in Rust's f32 for the smallest subnormal number, so this has to be done
    // instead  (f32::from_bits())
    produce_kt_nullable_float(env, Some(f32::from_bits(1)))
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableFloatMaxImpl(
    env: JNIEnv,
    class: JClass,
) -> jobject {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableFloatMaxImpl_fallible,
        env,
        class
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableFloatMaxImpl_fallible(
    env: JNIEnv,
    _: JClass,
) -> Result<jobject, JNIError> {
    produce_kt_nullable_float(env, Some(f32::MAX))
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableFloatNegInfImpl(
    env: JNIEnv,
    class: JClass,
) -> jobject {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableFloatNegInfImpl_fallible,
        env,
        class
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableFloatNegInfImpl_fallible(
    env: JNIEnv,
    _: JClass,
) -> Result<jobject, JNIError> {
    produce_kt_nullable_float(env, Some(f32::NEG_INFINITY))
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableFloatPosInfImpl(
    env: JNIEnv,
    class: JClass,
) -> jobject {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableFloatPosInfImpl_fallible,
        env,
        class
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableFloatPosInfImpl_fallible(
    env: JNIEnv,
    _: JClass,
) -> Result<jobject, JNIError> {
    produce_kt_nullable_float(env, Some(f32::INFINITY))
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableFloatNanImpl(
    env: JNIEnv,
    class: JClass,
) -> jobject {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableFloatNanImpl_fallible,
        env,
        class
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableFloatNanImpl_fallible(
    env: JNIEnv,
    _: JClass,
) -> Result<jobject, JNIError> {
    let nan_value = f32::NAN;
    produce_kt_nullable_float(env, Some(nan_value))
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableFloatNullImpl(
    env: JNIEnv,
    class: JClass,
) -> jobject {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableFloatNullImpl_fallible,
        env,
        class
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableFloatNullImpl_fallible(
    env: JNIEnv,
    _: JClass,
) -> Result<jobject, JNIError> {
    produce_kt_nullable_float(env, None)
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableDoubleMinImpl(
    env: JNIEnv,
    class: JClass,
) -> jobject {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableDoubleMinImpl_fallible,
        env,
        class
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableDoubleMinImpl_fallible(
    env: JNIEnv,
    _: JClass,
) -> Result<jobject, JNIError> {
    // Kotlin's Double.MIN_VALUE is the smallest subnormal number representable with 64 bits
    // (0x0...01). However, Rust's f64::MIN_POSITIVE only gives the smallest normal number.
    // There is no constant in Rust's f64 for the smallest subnormal number, so this has to be done
    // instead (f64::from_bits())
    produce_kt_nullable_double(env, Some(f64::from_bits(1)))
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableDoubleMaxImpl(
    env: JNIEnv,
    class: JClass,
) -> jobject {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableDoubleMaxImpl_fallible,
        env,
        class
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableDoubleMaxImpl_fallible(
    env: JNIEnv,
    _: JClass,
) -> Result<jobject, JNIError> {
    produce_kt_nullable_double(env, Some(f64::MAX))
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableDoubleNegInfImpl(
    env: JNIEnv,
    class: JClass,
) -> jobject {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableDoubleNegInfImpl_fallible,
        env,
        class
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableDoubleNegInfImpl_fallible(
    env: JNIEnv,
    _: JClass,
) -> Result<jobject, JNIError> {
    produce_kt_nullable_double(env, Some(f64::NEG_INFINITY))
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableDoublePosInfImpl(
    env: JNIEnv,
    class: JClass,
) -> jobject {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableDoublePosInfImpl_fallible,
        env,
        class
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableDoublePosInfImpl_fallible(
    env: JNIEnv,
    _: JClass,
) -> Result<jobject, JNIError> {
    produce_kt_nullable_double(env, Some(f64::INFINITY))
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableDoubleNanImpl(
    env: JNIEnv,
    class: JClass,
) -> jobject {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableDoubleNanImpl_fallible,
        env,
        class
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableDoubleNanImpl_fallible(
    env: JNIEnv,
    _: JClass,
) -> Result<jobject, JNIError> {
    let nan_value = f64::NAN;
    produce_kt_nullable_double(env, Some(nan_value))
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableDoubleNullImpl(
    env: JNIEnv,
    class: JClass,
) -> jobject {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableDoubleNullImpl_fallible,
        env,
        class
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullableDoubleNullImpl_fallible(
    env: JNIEnv,
    _: JClass,
) -> Result<jobject, JNIError> {
    produce_kt_nullable_double(env, None)
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullablePreDeterminedStringImpl(
    env: JNIEnv,
    class: JClass,
) -> jobject {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullablePreDeterminedStringImpl_fallible,
        env,
        class
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullablePreDeterminedStringImpl_fallible(
    env: JNIEnv,
    _: JClass,
) -> Result<jstring, JNIError> {
    produce_kt_nullable_string(env, Some(String::from(PRE_DETERMINED_STRING)))
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullablePreDeterminedStringNullImpl(
    env: JNIEnv,
    class: JClass,
) -> jstring {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullablePreDeterminedStringNullImpl_fallible,
        env,
        class
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNullableFromRustHelpersKt__1testReceiveNullablePreDeterminedStringNullImpl_fallible(
    env: JNIEnv,
    _: JClass,
) -> Result<jstring, JNIError> {
    produce_kt_nullable_string(env, None)
}
