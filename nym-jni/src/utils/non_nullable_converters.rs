use jni::{
    objects::JString,
    sys::{jboolean, jbyte, jdouble, jfloat, jint, jlong, jshort, jstring},
    JNIEnv,
};

// Kotlin Boolean
/// Receives a Kotlin `Boolean` through JNI.
///
/// # Background
/// NB: The JVM's memory representation of `boolean`s uses 8 bits, and there are only 2 possible
/// values: `1` for `true` and `0` for `false`. This coincides with Rust's memory representation,
/// which also uses 8 bits, and uses bit pattern `0x01` for `true` and `0x00` for `false`.
pub fn consume_kt_bool(source: jboolean) -> bool {
    source == 1
}
/// Prepares a Kotlin `Boolean` to be sent through JNI.
///
/// # Background
/// NB: The JVM's memory representation of `boolean`s uses 8 bits, and there are only 2 possible
/// values: `1` for `true` and `0` for `false`. This coincides with Rust's memory representation,
/// which also uses 8 bits, and uses bit pattern `0x01` for `true` and `0x00` for `false`.
pub fn produce_kt_bool(source: bool) -> jboolean {
    source.into()
}

// Kotlin Char
// TODO: Requires handling of modified UTF-8 strings. It's rare to ever need to pass a single
// TODO: character around; usually we use strings, where all these is handled for us
// pub fn consume_kt_char(source: jchar) -> char {
// }
// pub fn produce_kt_char(jsource: char) -> jchar {
// }

// Kotlin Byte
/// Receives a Kotlin `Byte` through JNI. Functionally, the identity function.
///
/// # Background
/// This function exists only to make user code consistent when used with other interfacing
/// functions like `produce_kt_ubyte()`.
pub fn consume_kt_byte(source: jbyte) -> i8 {
    source
}
/// Prepares a Kotlin `Byte` to be sent through JNI. Functionally, the identity function.
///
/// # Background
/// This function exists only to make user code consistent when used with other interfacing
/// functions like `produce_kt_ubyte()`.
pub fn produce_kt_byte(source: i8) -> jbyte {
    source
}

// Kotlin UByte
/// Receives a Kotlin `UByte` through JNI. Functionally, this interprets the bits of a signed byte
/// (`i8`) as unsigned (`u8`).
///
/// # Background
/// The JNI interface only supports signed numeric types (`Byte` in Kotlin and `i8` in Rust). When
/// sending a `UByte` from Kotlin to Rust (`u8`) through JNI (`byte`), the byte's bits remain
/// unchanged throughout. Work to correctly interface the type interpretation of the bits between
/// Kotlin and the JVM is done by the Kotlin standard library, but such work isn't provided in Rust.
/// This function does this interfacing work between Rust and the JVM.
///
/// NB: `UByte` is an inline class in Kotlin, so the nullable variant is represented in memory as a
/// boxed `kotlin/UByte` class instance, and the non-nullable variant is represented in memory as
/// the underlying `Byte` value.
///
/// # Reference
/// Strategy from <https://doc.rust-lang.org/std/mem/fn.transmute.html#alternatives>
pub fn consume_kt_ubyte(source: jbyte) -> u8 {
    u8::from_be_bytes(source.to_be_bytes())
}
/// Prepares a Kotlin `UByte` to be sent through JNI. Functionally, this interprets the bits of an
/// unsigned byte (`u8`) as signed (`i8`).
///
/// # Background
/// The JNI interface only supports signed numeric types (`Byte` in Kotlin and `i8` in Rust). When
/// sending a `u8` from Rust to Kotlin (`UByte`) through JNI (`byte`), the byte's bits remain
/// unchanged throughout. Work to correctly interface the type interpretation of the bits between
/// Kotlin and the JVM is done by the Kotlin standard library, but such work isn't provided in Rust.
/// This function does this interfacing work between Rust and the JVM.
///
/// NB: `UByte` is an inline class in Kotlin, so the nullable variant is represented in memory as a
/// boxed `kotlin/UByte` class instance, and the non-nullable variant is represented in memory as
/// the underlying `Byte` value.
///
/// # Reference
/// Strategy from <https://doc.rust-lang.org/std/mem/fn.transmute.html#alternatives>
pub fn produce_kt_ubyte(source: u8) -> jbyte {
    jbyte::from_be_bytes(source.to_be_bytes())
}

// Kotlin Short
/// Receives a Kotlin `Short` through JNI. Functionally, the identity function.
///
/// # Background
/// This function exists only to make user code consistent when used with other interfacing
/// functions like `consume_kt_ushort()`.
pub fn consume_kt_short(source: jshort) -> i16 {
    source
}
/// Prepares a Kotlin `Short` to be sent through JNI. Functionally, the identity function.
///
/// # Background
/// This function exists only to make user code consistent when used with other interfacing
/// functions like `produce_kt_ushort()`.
pub fn produce_kt_short(source: i16) -> jshort {
    source
}

// Kotlin UShort
/// Receives a Kotlin `UShort` through JNI. Functionally, this interprets the bits of a signed short
/// (`i16`) as unsigned (`u16`).
///
/// # Background
/// The JNI interface only supports signed numeric types (`Short` in Kotlin and `i16` in Rust). When
/// sending a `UShort` from Kotlin to Rust (`u16`) through JNI (`short`), the short's bits remain
/// unchanged throughout. Work to correctly interface the type interpretation of the bits between
/// Kotlin and the JVM is done by the Kotlin standard library, but such work isn't provided in Rust.
/// This function does this interfacing work between Rust and the JVM.
///
/// NB: `UShort` is an inline class in Kotlin, so the nullable variant is represented in memory as a
/// boxed `kotlin/UShort` class instance, and the non-nullable variant is represented in memory as
/// the underlying `Short` value.
///
/// # Reference
/// Strategy from <https://doc.rust-lang.org/std/mem/fn.transmute.html#alternatives>
pub fn consume_kt_ushort(source: jshort) -> u16 {
    u16::from_be_bytes(source.to_be_bytes())
}
/// Prepares a Kotlin `UShort` to be sent through JNI. Functionally, this interprets the bits of an
/// unsigned short (`u16`) as signed (`i16`).
///
/// # Background
/// The JNI interface only supports signed numeric types (`Short` in Kotlin and `i16` in Rust). When
/// sending a `u16` from Rust to Kotlin (`UShort`) through JNI (`short`), the short's bits remain
/// unchanged throughout. Work to correctly interface the type interpretation of the bits between
/// Kotlin and the JVM is done by the Kotlin standard library, but such work isn't provided in Rust.
/// This function does this interfacing work between Rust and the JVM.
///
/// NB: `UShort` is an inline class in Kotlin, so the nullable variant is represented in memory as a
/// boxed `kotlin/UShort` class instance, and the non-nullable variant is represented in memory as
/// the underlying `Short` value.
///
/// # Reference
/// Strategy from <https://doc.rust-lang.org/std/mem/fn.transmute.html#alternatives>
pub fn produce_kt_ushort(source: u16) -> jshort {
    jshort::from_be_bytes(source.to_be_bytes())
}

// Kotlin Int
/// Receives a Kotlin `Int` through JNI. Functionally, the identity function.
///
/// # Background
/// This function exists only to make user code consistent when used with other interfacing
/// functions like `consume_kt_uint()`.
pub fn consume_kt_int(source: jint) -> i32 {
    source
}
/// Prepares a Kotlin `Int` to be sent through JNI. Functionally, the identity function.
///
/// # Background
/// This function exists only to make user code consistent when used with other interfacing
/// functions like `produce_kt_uint()`.
pub fn produce_kt_int(source: i32) -> jint {
    source
}

// Kotlin UInt
/// Receives a Kotlin `UInt` through JNI. Functionally, this interprets the bits of a signed integer
/// (`i32`) as unsigned (`u32`).
///
/// # Background
/// The JNI interface only supports signed numeric types (`Int` in Kotlin and `i32` in Rust). When
/// sending a `UInt` from Kotlin to Rust (`u32`) through JNI (`int`), the integer's bits remain
/// unchanged throughout. Work to correctly interface the type interpretation of the bits between
/// Kotlin and the JVM is done by the Kotlin standard library, but such work isn't provided in Rust.
/// This function does this interfacing work between Rust and the JVM.
///
/// NB: `UInt` is an inline class in Kotlin, so the nullable variant is represented in memory as a
/// boxed `kotlin/UInt` class instance, and the non-nullable variant is represented in memory as the
/// underlying `Int` value.
///
/// # Reference
/// Strategy from <https://doc.rust-lang.org/std/mem/fn.transmute.html#alternatives>
pub fn consume_kt_uint(source: jint) -> u32 {
    u32::from_be_bytes(source.to_be_bytes())
}
/// Prepares a Kotlin `UInt` to be sent through JNI. Functionally, this interprets the bits of an
/// unsigned byte (`u32`) as signed (`i32`).
///
/// # Background
/// The JNI interface only supports signed numeric types (`Int` in Kotlin and `i32` in Rust). When
/// sending a `u32` from Rust to Kotlin (`UInt`) through JNI (`int`), the integer's bits remain
/// unchanged throughout. Work to correctly interface the type interpretation of the bits between
/// Kotlin and the JVM is done by the Kotlin standard library, but such work isn't provided in Rust.
/// This function does this interfacing work between Rust and the JVM.
///
/// NB: `UInt` is an inline class in Kotlin, so the nullable variant is represented in memory as a
/// boxed `kotlin/UInt` class instance, and the non-nullable variant is represented in memory as the
/// underlying `Int` value.
///
/// # Reference
/// Strategy from <https://doc.rust-lang.org/std/mem/fn.transmute.html#alternatives>
pub fn produce_kt_uint(source: u32) -> jint {
    jint::from_be_bytes(source.to_be_bytes())
}

// Kotlin Long
/// Receives a Kotlin `Long` through JNI. Functionally, the identity function.
///
/// # Background
/// This function exists only to make user code consistent when used with other interfacing
/// functions like `consume_kt_ulong()`.
pub fn consume_kt_long(source: jlong) -> i64 {
    source
}
/// Prepares a Kotlin `Long` to be sent through JNI. Functionally, the identity function.
///
/// # Background
/// This function exists only to make user code consistent when used with other interfacing
/// functions like `produce_kt_ulong()`.
pub fn produce_kt_long(source: i64) -> jlong {
    source
}

// Kotlin ULong
/// Receives a Kotlin `ULong` through JNI. Functionally, this interprets the bits of a signed long
/// (`i64`) as unsigned (`u64`).
///
/// # Background
/// The JNI interface only supports signed numeric types (`Long` in Kotlin and `i64` in Rust). When
/// sending a `ULong` from Kotlin to Rust (`u64`) through JNI (`long`), the long's bits remain
/// unchanged throughout. Work to correctly interface the type interpretation of the bits between
/// Kotlin and the JVM is done by the Kotlin standard library, but such work isn't provided in Rust.
/// This function does this interfacing work between Rust and the JVM.
///
/// NB: `ULong` is an inline class in Kotlin, so the nullable variant is represented in memory as a
/// boxed `kotlin/ULong` class instance, and the non-nullable variant is represented in memory as
/// the underlying `Long` value.
///
/// # Reference
/// Strategy from <https://doc.rust-lang.org/std/mem/fn.transmute.html#alternatives>
pub fn consume_kt_ulong(source: jlong) -> u64 {
    u64::from_be_bytes(source.to_be_bytes())
}
/// Prepares a Kotlin `ULong` to be sent through JNI. Functionally, this interprets the bits of an
/// unsigned long (`u64`) as signed (`i64`).
///
/// # Background
/// The JNI interface only supports signed numeric types (`Long` in Kotlin and `i64` in Rust). When
/// sending a `u64` from Rust to Kotlin (`ULong`) through JNI (`long`), the long's bits remain
/// unchanged throughout. Work to correctly interface the type interpretation of the bits between
/// Kotlin and the JVM is done by the Kotlin standard library, but such work isn't provided in Rust.
/// This function does this interfacing work between Rust and the JVM.
///
/// NB: `ULong` is an inline class in Kotlin, so the nullable variant is represented in memory as a
/// boxed `kotlin/ULong` class instance, and the non-nullable variant is represented in memory as
/// the underlying `Long` value.
///
/// # Reference
/// Strategy from <https://doc.rust-lang.org/std/mem/fn.transmute.html#alternatives>
pub fn produce_kt_ulong(source: u64) -> jlong {
    jlong::from_be_bytes(source.to_be_bytes())
}

// Kotlin Float
/// Receives a Kotlin `Float` through JNI. Functionally, the identity function.
///
/// # Background
/// This function exists only to make user code consistent when used with other interfacing
/// functions like `consume_kt_uint()`.
pub fn consume_kt_float(source: jfloat) -> f32 {
    source
}
/// Prepares a Kotlin `Float` to be sent through JNI. Functionally, the identity function.
///
/// # Background
/// This function exists only to make user code consistent when used with other interfacing
/// functions like `produce_kt_uint()`.
pub fn produce_kt_float(source: f32) -> jfloat {
    source
}

// Kotlin Double
/// Receives a Kotlin `Double` through JNI. Functionally, the identity function.
///
/// # Background
/// This function exists only to make user code consistent when used with other interfacing
/// functions like `consume_kt_uint()`.
pub fn consume_kt_double(source: jdouble) -> f64 {
    source
}
/// Prepares a Kotlin `Double` to be sent through JNI. Functionally, the identity function.
///
/// # Background
/// This function exists only to make user code consistent when used with other interfacing
/// functions like `produce_kt_uint()`.
pub fn produce_kt_double(source: f64) -> jdouble {
    source
}

// Kotlin Void: nothing to implement

// Kotlin String
/// Receives a Kotlin `String` through JNI.
///
/// # Failure
/// If the object passed from Kotlin is not a `String` (programmer error).
pub fn consume_kt_string_fallible(
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
/// Prepares a Kotlin `String` to be sent through JNI.
///
/// # Failure
/// If the JVM runs out of memory (as indicated in the JNI specification).
pub fn produce_kt_string_fallible(
    env: JNIEnv,
    source: String,
    err_field_name: &str,
) -> Result<jstring, String> {
    env.new_string(source)
        .map(JString::into_raw)
        .map_err(|err| {
            format!(
                "Rust: Unable to create {} from Rust ({})",
                err_field_name, err
            )
        })
}
