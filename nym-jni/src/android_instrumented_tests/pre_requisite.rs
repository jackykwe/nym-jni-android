// Corresponds to PreRequisiteHelpers.kt

use std::ptr::null_mut;

use jni::{errors::Error as JNIError, objects::JClass, sys::jstring, JNIEnv};

use crate::{call_fallible_or_else, utils::produce_kt_string};

const PRE_DETERMINED_STRING: &str = "the brown fox jumps over the lazy dog";

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_PreRequisiteHelpersKt__1testReceivePreDeterminedStringImpl(
    env: JNIEnv,
    class: JClass,
) -> jstring {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_PreRequisiteHelpersKt__1testReceivePreDeterminedStringImpl_fallible,
        env,
        class
    )
}

#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_jni_instrumentedtesthelpers_PreRequisiteHelpersKt__1testReceivePreDeterminedStringImpl_fallible(
    env: JNIEnv,
    _: JClass,
) -> Result<jstring, JNIError> {
    produce_kt_string(env, String::from(PRE_DETERMINED_STRING))
}
