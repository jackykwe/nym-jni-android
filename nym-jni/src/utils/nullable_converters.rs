use std::ptr::null_mut;

use jni::{
    descriptors::Desc,
    objects::{JObject, JString, JValue},
    signature::{Primitive, ReturnType},
    sys::{jint, jobject},
    JNIEnv,
};

// Kotlin Boolean?

// Kotlin Char?
// TODO: Requires handling of modified UTF-8 strings. It's rare to ever need to pass a single
// TODO: character around; usually we use strings, where all these is handled for us
// pub fn consume_kt_nullable_char(...) -> char {
// }
// pub fn produce_kt_nullable_char(...) -> jobject {
// }

// Kotlin Byte?

// Kotlin UByte?

// Kotlin Short?

// Kotlin UShort?

// Kotlin Int?
pub fn consume_kt_nullable_int_fallible(
    env: JNIEnv,
    source: JObject,
    err_field_name: &str,
) -> Result<Option<i32>, String> {
    if source.is_null() {
        return Ok(None); // null was passed from Kotlin, so return None
    }

    let method_id = env
        .get_method_id("java/lang/Integer", "intValue", "()I")
        .map_err(|err| {
            format!(
                "Unable to get java/lang/Integer's intValue method ID from Kotlin ({})",
                err
            )
        })?;

    env.call_method_unchecked(
        source,
        method_id,
        ReturnType::Primitive(Primitive::Int),
        &[],
    )
    .map_err(|err| {
        format!(
            "Unable to get {}'s value from Kotlin ({})",
            err_field_name, err
        )
    })?
    .i()
    .map(Some)
    .map_err(|err| {
        format!(
            "Unable to convert {}'s value to i32 ({})",
            err_field_name, err
        )
    })
}

// Kotlin UInt?
pub fn consume_kt_nullable_uint_fallible(
    env: JNIEnv,
    source: JObject,
    err_field_name: &str,
) -> Result<Option<u32>, String> {
    if source.is_null() {
        return Ok(None); // null was passed from Kotlin, so return None
    }

    let field_id = env
        .get_field_id("kotlin/UInt", "data", "I")
        .map_err(|err| {
            format!(
                "Unable to get kotlin/UInt's data field ID from Kotlin ({})",
                err
            )
        })?;

    Ok(env
        .get_field_unchecked(source, field_id, ReturnType::Primitive(Primitive::Int))
        .map_err(|err| {
            format!(
                "Unable to get {}'s value from Kotlin ({})",
                err_field_name, err
            )
        })?
        .i()
        .map(|val| Some(super::consume_kt_uint(val)))
        .map_err(|err| {
            format!(
                "Unable to convert {}'s value to jint ({})",
                err_field_name, err
            )
        })?)
}
pub fn produce_kt_nullable_uint_fallible(
    env: JNIEnv,
    source: Option<u32>,
    err_field_name: &str,
) -> Result<jobject, String> {
    if source.is_none() {
        return Ok(null_mut());
    }
    let source = source.unwrap(); // safe, never panics
    let source = JValue::Int(super::produce_kt_uint(source));
    let x = env
        .new_object("kotlin/UInt", "(I)V", &[source])
        .map_err(|err| format!("Unable to create a new kotlin/UInt from Rust ({})", err))?;
    Ok(x.into_raw())
}

// Kotlin Long?

// Kotlin ULong?

// Kotlin Float?

// Kotlin Double?

// Kotlin Void?: nothing to implement

// Kotlin String?
pub fn consume_kt_nullable_string_fallible(
    env: JNIEnv,
    source: JString,
    err_field_name: &str,
) -> Result<Option<String>, String> {
    if source.is_null() {
        return Ok(None); // null was passed from Kotlin, so return None
    }
    super::consume_kt_string_string_fallible(env, source, err_field_name).map(Some)
}
