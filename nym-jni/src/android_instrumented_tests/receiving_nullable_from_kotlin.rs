// Corresponds to SendingNullableToRustHelpers.kt

use std::ptr::null_mut;

use jni::{
    errors::Error as JNIError,
    objects::{JClass, JObject, JString},
    sys::jstring,
    JNIEnv,
};

use crate::{
    call_fallible_or_else,
    utils::{
        consume_kt_nullable_boolean_fallible, consume_kt_nullable_byte_fallible,
        consume_kt_nullable_double_fallible, consume_kt_nullable_float_fallible,
        consume_kt_nullable_int_fallible, consume_kt_nullable_long_fallible,
        consume_kt_nullable_short_fallible, consume_kt_nullable_string_fallible,
        consume_kt_nullable_ubyte_fallible, consume_kt_nullable_uint_fallible,
        consume_kt_nullable_ulong_fallible, consume_kt_nullable_ushort_fallible,
        produce_kt_string_fallible,
    },
};

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNullableToRustHelpersKt__1testSendNullableBooleanThenReceiveStringImpl(
    env: JNIEnv,
    class: JClass,
    arg: JObject,
) -> jstring {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNullableToRustHelpersKt__1testSendNullableBooleanThenReceiveStringImpl_fallible,
        env,
        class,
        arg
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNullableToRustHelpersKt__1testSendNullableBooleanThenReceiveStringImpl_fallible(
    env: JNIEnv,
    _: JClass,
    arg: JObject,
) -> Result<jstring, JNIError> {
    let arg = consume_kt_nullable_boolean_fallible(env, arg)?;
    produce_kt_string_fallible(
        env,
        format!("Rust received {:?} (Boolean?/Option<bool>)", arg),
    )
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNullableToRustHelpersKt__1testSendNullableByteThenReceiveStringImpl(
    env: JNIEnv,
    class: JClass,
    arg: JObject,
) -> jstring {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNullableToRustHelpersKt__1testSendNullableByteThenReceiveStringImpl_fallible,
        env,
        class,
        arg
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNullableToRustHelpersKt__1testSendNullableByteThenReceiveStringImpl_fallible(
    env: JNIEnv,
    _: JClass,
    arg: JObject,
) -> Result<jstring, JNIError> {
    let arg = consume_kt_nullable_byte_fallible(env, arg)?;
    produce_kt_string_fallible(env, format!("Rust received {:?} (Byte?/Option<i8>)", arg))
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNullableToRustHelpersKt__1testSendNullableUByteThenReceiveStringImpl_0002d3swpYEE(
    env: JNIEnv,
    class: JClass,
    arg: JObject,
) -> jstring {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNullableToRustHelpersKt__1testSendNullableUByteThenReceiveStringImpl_0002d3swpYEE_fallible,
        env,
        class,
        arg
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNullableToRustHelpersKt__1testSendNullableUByteThenReceiveStringImpl_0002d3swpYEE_fallible(
    env: JNIEnv,
    _: JClass,
    arg: JObject,
) -> Result<jstring, JNIError> {
    let arg = consume_kt_nullable_ubyte_fallible(env, arg)?;
    produce_kt_string_fallible(env, format!("Rust received {:?} (UByte?/Option<u8>)", arg))
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNullableToRustHelpersKt__1testSendNullableShortThenReceiveStringImpl(
    env: JNIEnv,
    class: JClass,
    arg: JObject,
) -> jstring {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNullableToRustHelpersKt__1testSendNullableShortThenReceiveStringImpl_fallible,
        env,
        class,
        arg
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNullableToRustHelpersKt__1testSendNullableShortThenReceiveStringImpl_fallible(
    env: JNIEnv,
    _: JClass,
    arg: JObject,
) -> Result<jstring, JNIError> {
    let arg = consume_kt_nullable_short_fallible(env, arg)?;
    produce_kt_string_fallible(env, format!("Rust received {:?} (Short?/Option<i16>)", arg))
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNullableToRustHelpersKt__1testSendNullableUShortThenReceiveStringImpl_0002dffyZV3s(
    env: JNIEnv,
    class: JClass,
    arg: JObject,
) -> jstring {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNullableToRustHelpersKt__1testSendNullableUShortThenReceiveStringImpl_0002dffyZV3s_fallible,
        env,
        class,
        arg
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNullableToRustHelpersKt__1testSendNullableUShortThenReceiveStringImpl_0002dffyZV3s_fallible(
    env: JNIEnv,
    _: JClass,
    arg: JObject,
) -> Result<jstring, JNIError> {
    let arg = consume_kt_nullable_ushort_fallible(env, arg)?;
    produce_kt_string_fallible(
        env,
        format!("Rust received {:?} (UShort?/Option<u16>)", arg),
    )
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNullableToRustHelpersKt__1testSendNullableIntThenReceiveStringImpl(
    env: JNIEnv,
    class: JClass,
    arg: JObject,
) -> jstring {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNullableToRustHelpersKt__1testSendNullableIntThenReceiveStringImpl_fallible,
        env,
        class,
        arg
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNullableToRustHelpersKt__1testSendNullableIntThenReceiveStringImpl_fallible(
    env: JNIEnv,
    _: JClass,
    arg: JObject,
) -> Result<jstring, JNIError> {
    let arg = consume_kt_nullable_int_fallible(env, arg)?;
    produce_kt_string_fallible(env, format!("Rust received {:?} (Int?/Option<i32>)", arg))
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNullableToRustHelpersKt__1testSendNullableUIntThenReceiveStringImpl_0002dExVfyTY(
    env: JNIEnv,
    class: JClass,
    arg: JObject,
) -> jstring {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNullableToRustHelpersKt__1testSendNullableUIntThenReceiveStringImpl_0002dExVfyTY_fallible,
        env,
        class,
        arg
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNullableToRustHelpersKt__1testSendNullableUIntThenReceiveStringImpl_0002dExVfyTY_fallible(
    env: JNIEnv,
    _: JClass,
    arg: JObject,
) -> Result<jstring, JNIError> {
    let arg = consume_kt_nullable_uint_fallible(env, arg)?;
    produce_kt_string_fallible(env, format!("Rust received {:?} (UInt?/Option<u32>)", arg))
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNullableToRustHelpersKt__1testSendNullableLongThenReceiveStringImpl(
    env: JNIEnv,
    class: JClass,
    arg: JObject,
) -> jstring {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNullableToRustHelpersKt__1testSendNullableLongThenReceiveStringImpl_fallible,
        env,
        class,
        arg
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNullableToRustHelpersKt__1testSendNullableLongThenReceiveStringImpl_fallible(
    env: JNIEnv,
    _: JClass,
    arg: JObject,
) -> Result<jstring, JNIError> {
    let arg = consume_kt_nullable_long_fallible(env, arg)?;
    produce_kt_string_fallible(env, format!("Rust received {:?} (Long?/Option<i64>)", arg))
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNullableToRustHelpersKt__1testSendNullableULongThenReceiveStringImpl_0002dADd3fzo(
    env: JNIEnv,
    class: JClass,
    arg: JObject,
) -> jstring {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNullableToRustHelpersKt__1testSendNullableULongThenReceiveStringImpl_0002dADd3fzo_fallible,
        env,
        class,
        arg
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNullableToRustHelpersKt__1testSendNullableULongThenReceiveStringImpl_0002dADd3fzo_fallible(
    env: JNIEnv,
    _: JClass,
    arg: JObject,
) -> Result<jstring, JNIError> {
    let arg = consume_kt_nullable_ulong_fallible(env, arg)?;
    produce_kt_string_fallible(env, format!("Rust received {:?} (ULong?/Option<u64>)", arg))
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNullableToRustHelpersKt__1testSendNullableFloatThenReceiveStringImpl(
    env: JNIEnv,
    class: JClass,
    arg: JObject,
) -> jstring {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNullableToRustHelpersKt__1testSendNullableFloatThenReceiveStringImpl_fallible,
        env,
        class,
        arg
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNullableToRustHelpersKt__1testSendNullableFloatThenReceiveStringImpl_fallible(
    env: JNIEnv,
    _: JClass,
    arg: JObject,
) -> Result<jstring, JNIError> {
    let arg = consume_kt_nullable_float_fallible(env, arg)?;
    let arg = arg.map(f32::to_bits);
    produce_kt_string_fallible(
        env,
        format!("Rust received 0b{:?} (Float?/Option<f32>)", arg),
    )
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNullableToRustHelpersKt__1testSendNullableDoubleThenReceiveStringImpl(
    env: JNIEnv,
    class: JClass,
    arg: JObject,
) -> jstring {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNullableToRustHelpersKt__1testSendNullableDoubleThenReceiveStringImpl_fallible,
        env,
        class,
        arg
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNullableToRustHelpersKt__1testSendNullableDoubleThenReceiveStringImpl_fallible(
    env: JNIEnv,
    _: JClass,
    arg: JObject,
) -> Result<jstring, JNIError> {
    let arg = consume_kt_nullable_double_fallible(env, arg)?;
    let arg = arg.map(f64::to_bits);
    produce_kt_string_fallible(
        env,
        format!("Rust received 0b{:?} (Double?/Option<f64>)", arg),
    )
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNullableToRustHelpersKt__1testSendNullableStringThenUppercaseThenReceiveStringImpl(
    env: JNIEnv,
    class: JClass,
    arg: JString,
) -> jstring {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNullableToRustHelpersKt__1testSendNullableStringThenUppercaseThenReceiveStringImpl_fallible,
        env,
        class,
        arg
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNullableToRustHelpersKt__1testSendNullableStringThenUppercaseThenReceiveStringImpl_fallible(
    env: JNIEnv,
    _: JClass,
    arg: JString,
) -> Result<jstring, JNIError> {
    let arg = consume_kt_nullable_string_fallible(env, arg)?;
    let result = arg.as_ref().map(|v| v.to_uppercase());
    produce_kt_string_fallible(
        env,
        format!(
            "Rust received {:?}, returning {:?} (String?/Option<String>)",
            arg, result
        ),
    )
}
