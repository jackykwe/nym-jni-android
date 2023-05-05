// Corresponds to SendingNonNullableToRustHelpers.kt

use std::ptr::null_mut;

use jni::{
    errors::Error as JNIError,
    objects::{JClass, JString},
    sys::{jboolean, jbyte, jdouble, jfloat, jint, jlong, jshort, jstring},
    JNIEnv,
};

use crate::{
    call_fallible_or_else,
    jvm_kotlin_typing::{
        consume_kt_boolean, consume_kt_byte, consume_kt_double, consume_kt_float, consume_kt_int,
        consume_kt_long, consume_kt_short, consume_kt_string, consume_kt_ubyte, consume_kt_uint,
        consume_kt_ulong, consume_kt_ushort, produce_kt_string,
    },
};

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNonNullableToRustHelpersKt__1testSendBooleanThenReceiveStringImpl(
    env: JNIEnv,
    class: JClass,
    arg: jboolean,
) -> jstring {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNonNullableToRustHelpersKt__1testSendBooleanThenReceiveStringImpl_fallible,
        env,
        class,
        arg
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNonNullableToRustHelpersKt__1testSendBooleanThenReceiveStringImpl_fallible(
    env: JNIEnv,
    _: JClass,
    arg: jboolean,
) -> Result<jstring, JNIError> {
    let arg = consume_kt_boolean(arg);
    produce_kt_string(env, format!("Rust received {} (Boolean/bool)", arg))
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNonNullableToRustHelpersKt__1testSendByteThenReceiveStringImpl(
    env: JNIEnv,
    class: JClass,
    arg: jbyte,
) -> jstring {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNonNullableToRustHelpersKt__1testSendByteThenReceiveStringImpl_fallible,
        env,
        class,
        arg
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNonNullableToRustHelpersKt__1testSendByteThenReceiveStringImpl_fallible(
    env: JNIEnv,
    _: JClass,
    arg: jbyte,
) -> Result<jstring, JNIError> {
    let arg = consume_kt_byte(arg);
    produce_kt_string(env, format!("Rust received {} (Byte/i8)", arg))
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNonNullableToRustHelpersKt__1testSendUByteThenReceiveStringImpl_0002d7apg3OU(
    env: JNIEnv,
    class: JClass,
    arg: jbyte,
) -> jstring {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNonNullableToRustHelpersKt__1testSendUByteThenReceiveStringImpl_0002d7apg3OU_fallible,
        env,
        class,
        arg
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNonNullableToRustHelpersKt__1testSendUByteThenReceiveStringImpl_0002d7apg3OU_fallible(
    env: JNIEnv,
    _: JClass,
    arg: jbyte,
) -> Result<jstring, JNIError> {
    let arg = consume_kt_ubyte(arg);
    produce_kt_string(env, format!("Rust received {} (UByte/u8)", arg))
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNonNullableToRustHelpersKt__1testSendShortThenReceiveStringImpl(
    env: JNIEnv,
    class: JClass,
    arg: jshort,
) -> jstring {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNonNullableToRustHelpersKt__1testSendShortThenReceiveStringImpl_fallible,
        env,
        class,
        arg
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNonNullableToRustHelpersKt__1testSendShortThenReceiveStringImpl_fallible(
    env: JNIEnv,
    _: JClass,
    arg: jshort,
) -> Result<jstring, JNIError> {
    let arg = consume_kt_short(arg);
    produce_kt_string(env, format!("Rust received {} (Short/i16)", arg))
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNonNullableToRustHelpersKt__1testSendUShortThenReceiveStringImpl_0002dxj2QHRw(
    env: JNIEnv,
    class: JClass,
    arg: jshort,
) -> jstring {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNonNullableToRustHelpersKt__1testSendUShortThenReceiveStringImpl_0002dxj2QHRw_fallible,
        env,
        class,
        arg
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNonNullableToRustHelpersKt__1testSendUShortThenReceiveStringImpl_0002dxj2QHRw_fallible(
    env: JNIEnv,
    _: JClass,
    arg: jshort,
) -> Result<jstring, JNIError> {
    let arg = consume_kt_ushort(arg);
    produce_kt_string(env, format!("Rust received {} (UShort/u16)", arg))
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNonNullableToRustHelpersKt__1testSendIntThenReceiveStringImpl(
    env: JNIEnv,
    class: JClass,
    arg: jint,
) -> jstring {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNonNullableToRustHelpersKt__1testSendIntThenReceiveStringImpl_fallible,
        env,
        class,
        arg
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNonNullableToRustHelpersKt__1testSendIntThenReceiveStringImpl_fallible(
    env: JNIEnv,
    _: JClass,
    arg: jint,
) -> Result<jstring, JNIError> {
    let arg = consume_kt_int(arg);
    produce_kt_string(env, format!("Rust received {} (Int/i32)", arg))
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNonNullableToRustHelpersKt__1testSendUIntThenReceiveStringImpl_0002dWZ4Q5Ns(
    env: JNIEnv,
    class: JClass,
    arg: jint,
) -> jstring {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNonNullableToRustHelpersKt__1testSendUIntThenReceiveStringImpl_0002dWZ4Q5Ns_fallible,
        env,
        class,
        arg
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNonNullableToRustHelpersKt__1testSendUIntThenReceiveStringImpl_0002dWZ4Q5Ns_fallible(
    env: JNIEnv,
    _: JClass,
    arg: jint,
) -> Result<jstring, JNIError> {
    let arg = consume_kt_uint(arg);
    produce_kt_string(env, format!("Rust received {} (UInt/u32)", arg))
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNonNullableToRustHelpersKt__1testSendLongThenReceiveStringImpl(
    env: JNIEnv,
    class: JClass,
    arg: jlong,
) -> jstring {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNonNullableToRustHelpersKt__1testSendLongThenReceiveStringImpl_fallible,
        env,
        class,
        arg
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNonNullableToRustHelpersKt__1testSendLongThenReceiveStringImpl_fallible(
    env: JNIEnv,
    _: JClass,
    arg: jlong,
) -> Result<jstring, JNIError> {
    let arg = consume_kt_long(arg);
    produce_kt_string(env, format!("Rust received {} (Long/i64)", arg))
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNonNullableToRustHelpersKt__1testSendULongThenReceiveStringImpl_0002dVKZWuLQ(
    env: JNIEnv,
    class: JClass,
    arg: jlong,
) -> jstring {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNonNullableToRustHelpersKt__1testSendULongThenReceiveStringImpl_0002dVKZWuLQ_fallible,
        env,
        class,
        arg
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNonNullableToRustHelpersKt__1testSendULongThenReceiveStringImpl_0002dVKZWuLQ_fallible(
    env: JNIEnv,
    _: JClass,
    arg: jlong,
) -> Result<jstring, JNIError> {
    let arg = consume_kt_ulong(arg);
    produce_kt_string(env, format!("Rust received {} (ULong/u64)", arg))
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNonNullableToRustHelpersKt__1testSendFloatThenReceiveStringImpl(
    env: JNIEnv,
    class: JClass,
    arg: jfloat,
) -> jstring {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNonNullableToRustHelpersKt__1testSendFloatThenReceiveStringImpl_fallible,
        env,
        class,
        arg
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNonNullableToRustHelpersKt__1testSendFloatThenReceiveStringImpl_fallible(
    env: JNIEnv,
    _: JClass,
    arg: jfloat,
) -> Result<jstring, JNIError> {
    let arg = consume_kt_float(arg);
    produce_kt_string(
        env,
        format!("Rust received 0b{} (Float/f32)", arg.to_bits()),
    )
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNonNullableToRustHelpersKt__1testSendDoubleThenReceiveStringImpl(
    env: JNIEnv,
    class: JClass,
    arg: jdouble,
) -> jstring {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNonNullableToRustHelpersKt__1testSendDoubleThenReceiveStringImpl_fallible,
        env,
        class,
        arg
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNonNullableToRustHelpersKt__1testSendDoubleThenReceiveStringImpl_fallible(
    env: JNIEnv,
    _: JClass,
    arg: jdouble,
) -> Result<jstring, JNIError> {
    let arg = consume_kt_double(arg);
    produce_kt_string(
        env,
        format!("Rust received 0b{} (Double/f64)", arg.to_bits()),
    )
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNonNullableToRustHelpersKt__1testSendStringThenUppercaseThenReceiveStringImpl(
    env: JNIEnv,
    class: JClass,
    arg: JString,
) -> jstring {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNonNullableToRustHelpersKt__1testSendStringThenUppercaseThenReceiveStringImpl_fallible,
        env,
        class,
        arg
    )
}
#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_SendingNonNullableToRustHelpersKt__1testSendStringThenUppercaseThenReceiveStringImpl_fallible(
    env: JNIEnv,
    _: JClass,
    arg: JString,
) -> Result<jstring, JNIError> {
    let arg = consume_kt_string(env, arg)?;
    let result = arg.to_uppercase();
    produce_kt_string(
        env,
        format!(
            "Rust received {}, returning {} (String/String)",
            arg, result
        ),
    )
}
