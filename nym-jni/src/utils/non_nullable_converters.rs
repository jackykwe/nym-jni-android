use jni::{
    objects::JString,
    sys::{jboolean, jbyte, jchar, jdouble, jfloat, jint, jlong, jshort},
    JNIEnv,
};

// Kotlin Boolean
pub fn consume_kt_bool(source: jboolean) -> bool {
    source == 1
}
pub fn produce_kt_bool(source: bool) -> jboolean {
    if source {
        1
    } else {
        0
    }
}

// Kotlin Char
// TODO: Requires handling of modified UTF-8 strings. It's rare to ever need to pass a single
// TODO: character around; usually we use strings, where all these is handled for us
// pub fn consume_kt_char(source: jchar) -> char {
// }
// pub fn produce_kt_char(jsource: char) -> jchar {
// }

// Kotlin Byte
pub fn consume_kt_byte(source: jbyte) -> i8 {
    source
}
pub fn produce_kt_byte(source: i8) -> jbyte {
    source
}

// Kotlin UByte
// Strategy from https://doc.rust-lang.org/std/mem/fn.transmute.html#alternatives
pub fn consume_kt_ubyte(source: jbyte) -> u8 {
    u8::from_be_bytes(source.to_be_bytes())
}
pub fn produce_kt_ubyte(source: u8) -> jbyte {
    jbyte::from_be_bytes(source.to_be_bytes())
}

// Kotlin Short
pub fn consume_kt_short(source: jshort) -> i16 {
    source
}
pub fn produce_kt_short(source: i16) -> jshort {
    source
}

// Kotlin UShort
pub fn consume_kt_ushort(source: jshort) -> u16 {
    u16::from_be_bytes(source.to_be_bytes())
}
pub fn produce_kt_ushort(source: u16) -> jshort {
    jshort::from_be_bytes(source.to_be_bytes())
}

// Kotlin Int
pub fn consume_kt_int(source: jint) -> i32 {
    source
}
pub fn produce_kt_int(source: i32) -> jint {
    source
}

// Kotlin UInt
pub fn consume_kt_uint(source: jint) -> u32 {
    u32::from_be_bytes(source.to_be_bytes())
}
pub fn produce_kt_uint(source: u32) -> jint {
    jint::from_be_bytes(source.to_be_bytes())
}

// Kotlin Long
pub fn consume_kt_long(source: jlong) -> i64 {
    source
}
pub fn produce_kt_long(source: i64) -> jlong {
    source
}

// Kotlin ULong
pub fn consume_kt_ulong(source: jlong) -> u64 {
    u64::from_be_bytes(source.to_be_bytes())
}
pub fn produce_kt_ulong(source: u64) -> jlong {
    jlong::from_be_bytes(source.to_be_bytes())
}

// Kotlin Float
pub fn consume_kt_float(source: jfloat) -> f32 {
    source
}
pub fn produce_kt_float(source: f32) -> jfloat {
    source
}

// Kotlin Double
pub fn consume_kt_double(source: jdouble) -> f64 {
    source
}
pub fn produce_kt_double(source: f64) -> jdouble {
    source
}

// Kotlin Void: nothing to implement

// Kotlin String
pub fn consume_kt_string_string_fallible(
    env: JNIEnv,
    source: JString,
    err_field_name: &str,
) -> Result<String, String> {
    env.get_string(source).map(Into::into).map_err(|err| {
        format!(
            "Rust: Unable to get {} from Kotlin ({})",
            err_field_name, err
        )
    })
}
