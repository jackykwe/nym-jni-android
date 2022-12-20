// Corresponds to ReceivingNonNullableFromRustHelpers.kt

use jni::{
    objects::JClass,
    sys::{jboolean, jbyte, jdouble, jfloat, jint, jlong, jshort},
    JNIEnv,
};

use crate::utils::{
    produce_kt_bool, produce_kt_byte, produce_kt_double, produce_kt_float, produce_kt_int,
    produce_kt_long, produce_kt_short, produce_kt_ubyte, produce_kt_uint, produce_kt_ulong,
    produce_kt_ushort,
};

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNonNullableFromRustHelpersKt__1testReceiveBooleanTrueImpl(
    _: JNIEnv,
    _: JClass,
) -> jboolean {
    produce_kt_bool(true)
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNonNullableFromRustHelpersKt__1testReceiveBooleanFalseImpl(
    _: JNIEnv,
    _: JClass,
) -> jboolean {
    produce_kt_bool(false)
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNonNullableFromRustHelpersKt__1testReceiveByteMinImpl(
    _: JNIEnv,
    _: JClass,
) -> jbyte {
    produce_kt_byte(i8::MIN)
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNonNullableFromRustHelpersKt__1testReceiveByteMaxImpl(
    _: JNIEnv,
    _: JClass,
) -> jbyte {
    produce_kt_byte(i8::MAX)
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNonNullableFromRustHelpersKt__1testReceiveUByteMinImpl(
    _: JNIEnv,
    _: JClass,
) -> jbyte {
    produce_kt_ubyte(u8::MIN)
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNonNullableFromRustHelpersKt__1testReceiveUByteMaxImpl(
    _: JNIEnv,
    _: JClass,
) -> jbyte {
    produce_kt_ubyte(u8::MAX)
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNonNullableFromRustHelpersKt__1testReceiveShortMinImpl(
    _: JNIEnv,
    _: JClass,
) -> jshort {
    produce_kt_short(i16::MIN)
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNonNullableFromRustHelpersKt__1testReceiveShortMaxImpl(
    _: JNIEnv,
    _: JClass,
) -> jshort {
    produce_kt_short(i16::MAX)
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNonNullableFromRustHelpersKt__1testReceiveUShortMinImpl(
    _: JNIEnv,
    _: JClass,
) -> jshort {
    produce_kt_ushort(u16::MIN)
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNonNullableFromRustHelpersKt__1testReceiveUShortMaxImpl(
    _: JNIEnv,
    _: JClass,
) -> jshort {
    produce_kt_ushort(u16::MAX)
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNonNullableFromRustHelpersKt__1testReceiveIntMinImpl(
    _: JNIEnv,
    _: JClass,
) -> jint {
    produce_kt_int(i32::MIN)
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNonNullableFromRustHelpersKt__1testReceiveIntMaxImpl(
    _: JNIEnv,
    _: JClass,
) -> jint {
    produce_kt_int(i32::MAX)
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNonNullableFromRustHelpersKt__1testReceiveUIntMinImpl(
    _: JNIEnv,
    _: JClass,
) -> jint {
    produce_kt_uint(u32::MIN)
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNonNullableFromRustHelpersKt__1testReceiveUIntMaxImpl(
    _: JNIEnv,
    _: JClass,
) -> jint {
    produce_kt_uint(u32::MAX)
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNonNullableFromRustHelpersKt__1testReceiveLongMinImpl(
    _: JNIEnv,
    _: JClass,
) -> jlong {
    produce_kt_long(i64::MIN)
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNonNullableFromRustHelpersKt__1testReceiveLongMaxImpl(
    _: JNIEnv,
    _: JClass,
) -> jlong {
    produce_kt_long(i64::MAX)
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNonNullableFromRustHelpersKt__1testReceiveULongMinImpl(
    _: JNIEnv,
    _: JClass,
) -> jlong {
    produce_kt_ulong(u64::MIN)
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNonNullableFromRustHelpersKt__1testReceiveULongMaxImpl(
    _: JNIEnv,
    _: JClass,
) -> jlong {
    produce_kt_ulong(u64::MAX)
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNonNullableFromRustHelpersKt__1testReceiveFloatMinImpl(
    _: JNIEnv,
    _: JClass,
) -> jfloat {
    // Kotlin's Float.MIN_VALUE is the smallest subnormal number representable with 32 bits
    // (0x0...01). However, Rust's f32::MIN_POSITIVE only gives the smallest normal number.
    // There is no constant in Rust's f32 for the smallest subnormal number, so this has to be done
    // instead (f32::from_bits())
    produce_kt_float(f32::from_bits(1))
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNonNullableFromRustHelpersKt__1testReceiveFloatMaxImpl(
    _: JNIEnv,
    _: JClass,
) -> jfloat {
    produce_kt_float(f32::MAX)
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNonNullableFromRustHelpersKt__1testReceiveFloatNegInfImpl(
    _: JNIEnv,
    _: JClass,
) -> jfloat {
    produce_kt_float(f32::NEG_INFINITY)
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNonNullableFromRustHelpersKt__1testReceiveFloatPosInfImpl(
    _: JNIEnv,
    _: JClass,
) -> jfloat {
    produce_kt_float(f32::INFINITY)
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNonNullableFromRustHelpersKt__1testReceiveFloatNanImpl(
    _: JNIEnv,
    _: JClass,
) -> jfloat {
    let nan_value = f32::NAN;
    produce_kt_float(nan_value)
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNonNullableFromRustHelpersKt__1testReceiveDoubleMinImpl(
    _: JNIEnv,
    _: JClass,
) -> jdouble {
    // Kotlin's Double.MIN_VALUE is the smallest subnormal number representable with 64 bits
    // (0x0...01). However, Rust's f64::MIN_POSITIVE only gives the smallest normal number.
    // There is no constant in Rust's f64 for the smallest subnormal number, so this has to be done
    // instead (f64::from_bits())
    produce_kt_double(f64::from_bits(1))
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNonNullableFromRustHelpersKt__1testReceiveDoubleMaxImpl(
    _: JNIEnv,
    _: JClass,
) -> jdouble {
    produce_kt_double(f64::MAX)
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNonNullableFromRustHelpersKt__1testReceiveDoubleNegInfImpl(
    _: JNIEnv,
    _: JClass,
) -> jdouble {
    produce_kt_double(f64::NEG_INFINITY)
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNonNullableFromRustHelpersKt__1testReceiveDoublePosInfImpl(
    _: JNIEnv,
    _: JClass,
) -> jdouble {
    produce_kt_double(f64::INFINITY)
}

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_ReceivingNonNullableFromRustHelpersKt__1testReceiveDoubleNanImpl(
    _: JNIEnv,
    _: JClass,
) -> jdouble {
    let nan_value = f64::NAN;
    produce_kt_double(nan_value)
}
