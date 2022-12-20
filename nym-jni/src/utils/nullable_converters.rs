use std::ptr::null_mut;

use jni::{
    errors::Error as JNIError,
    objects::{JObject, JString, JValue},
    signature::{Primitive, ReturnType},
    sys::{jobject, jstring, jvalue},
    JNIEnv,
};

// Kotlin Boolean?
/// Receives a Kotlin `Boolean?` through JNI.
///
/// # Background
/// While the Kotlin `Boolean` type is represented as a primitive `boolean` in JVM, the Kotlin
/// `Boolean?` type is represented as the boxed class `java/lang/Boolean` in JVM. Therefore, in
/// Rust, we call the JNI functions that call the `booleanValue` method of the `java/lang/Boolean`
/// class instance.
///
/// NB: The JVM's memory representation of `boolean`s uses 8 bits, and there are only 2 possible
/// values: `1` for `true` and `0` for `false`. This coincides with Rust's memory representation,
/// which also uses 8 bits, and uses bit pattern `0x01` for `true` and `0x00` for `false`.
pub fn consume_kt_nullable_boolean(env: JNIEnv, source: JObject) -> Result<Option<bool>, JNIError> {
    if source.is_null() {
        return Ok(None); // null was passed from Kotlin, so return None
    }

    let method_id = env.get_method_id("java/lang/Boolean", "booleanValue", "()Z")?;

    env.call_method_unchecked(
        source,
        method_id,
        ReturnType::Primitive(Primitive::Boolean),
        &[],
    )?
    .z()
    .map(Some)
}
/// Prepares a Kotlin `Boolean?` to be sent through JNI.
///
/// # Background
/// While the Kotlin `Boolean` type is represented as a primitive `boolean` in JVM, the Kotlin
/// `Boolean?` type is represented as the boxed class `java/lang/Boolean` in JVM. Therefore, in
/// Rust, we call the JNI functions that call the `booleanValue` method of the `java/lang/Boolean`
/// class instance.
///
/// NB: The JVM's memory representation of `boolean`s uses 8 bits, and there are only 2 possible
/// values: `1` for `true` and `0` for `false`. This coincides with Rust's memory representation,
/// which also uses 8 bits, and uses bit pattern `0x01` for `true` and `0x00` for `false`.
pub fn produce_kt_nullable_boolean(env: JNIEnv, source: Option<bool>) -> Result<jobject, JNIError> {
    match source {
        None => Ok(null_mut()),
        Some(source) => {
            // variable shadowing
            let source = jvalue {
                z: super::produce_kt_bool(source),
            };

            let method_id =
                env.get_static_method_id("java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;")?;

            env.call_static_method_unchecked(
                "java/lang/Boolean",
                method_id,
                ReturnType::Object,
                &[source],
            )?
            .l()
            .map(JObject::into_raw)
        }
    }
}

// Kotlin Char?
// TODO: Requires handling of modified UTF-8 strings. It's rare to ever need to pass a single
// TODO: character around; usually we use strings, where all these is handled for us
// pub fn consume_kt_nullable_char(...) -> char {
// }
// pub fn produce_kt_nullable_char(...) -> jobject {
// }

// Kotlin Byte?
/// Receives a Kotlin `Byte?` through JNI.
///
/// # Background
/// While the Kotlin `Byte` type is represented as a primitive `byte` in JVM, the Kotlin `Byte?`
/// type is represented as the boxed class `java/lang/Byte` in JVM. Therefore, in Rust, we call the
/// JNI functions that call the `byteValue` method of the `java/lang/Byte` class instance.
pub fn consume_kt_nullable_byte(env: JNIEnv, source: JObject) -> Result<Option<i8>, JNIError> {
    if source.is_null() {
        return Ok(None); // null was passed from Kotlin, so return None
    }

    let method_id = env.get_method_id("java/lang/Byte", "byteValue", "()B")?;

    env.call_method_unchecked(
        source,
        method_id,
        ReturnType::Primitive(Primitive::Byte),
        &[],
    )?
    .b()
    .map(Some)
}
/// Prepares a Kotlin `Byte?` to be sent through JNI.
///
/// # Background
/// While the Kotlin `Byte` type is represented as a primitive `byte` in JVM, the Kotlin `Byte?`
/// type is represented as the boxed class `java/lang/Byte` in JVM. Therefore, in Rust, we call the
/// JNI functions that create a new `java/lang/Byte` class instance.
pub fn produce_kt_nullable_byte(env: JNIEnv, source: Option<i8>) -> Result<jobject, JNIError> {
    match source {
        None => Ok(null_mut()),
        Some(source) => {
            let source = jvalue {
                b: super::produce_kt_byte(source),
            };

            let method_id =
                env.get_static_method_id("java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;")?;

            env.call_static_method_unchecked(
                "java/lang/Byte",
                method_id,
                ReturnType::Object,
                &[source],
            )?
            .l()
            .map(JObject::into_raw)
        }
    }
}

// Kotlin UByte?
/// Receives a Kotlin `UByte?` through JNI.
///
/// # Background
/// The `UByte` type is non-primitive in Kotlin, so in Rust, we call the JNI functions that get the
/// `data` field value of the `UByte` class instance.
///
/// NB: `UByte` is an inline class in Kotlin, so the nullable variant is represented in memory as a
/// boxed `kotlin/UByte` class instance, and the non-nullable variant is represented in memory as
/// the underlying `Byte` value.
pub fn consume_kt_nullable_ubyte(env: JNIEnv, source: JObject) -> Result<Option<u8>, JNIError> {
    if source.is_null() {
        return Ok(None); // null was passed from Kotlin, so return None
    }

    let field_id = env.get_field_id("kotlin/UByte", "data", "B")?;

    env.get_field_unchecked(source, field_id, ReturnType::Primitive(Primitive::Byte))?
        .b()
        .map(|val| Some(super::consume_kt_ubyte(val)))
}
/// Prepares a Kotlin `UByte?` to be sent through JNI.
///
/// # Background
/// The `UByte` type is non-primitive in Kotlin, so in Rust, we call the JNI functions that create
/// a new `UByte` class instance.
///
/// NB: `UByte` is an inline class in Kotlin, so the nullable variant is represented in memory as a
/// boxed `kotlin/UByte` class instance, and the non-nullable variant is represented in memory as
/// the underlying `Byte` value.
///
/// # Failure
/// If the JVM cannot instantiate an `UByte` object (e.g. the JVM runs out of memory).
pub fn produce_kt_nullable_ubyte(env: JNIEnv, source: Option<u8>) -> Result<jobject, JNIError> {
    match source {
        None => Ok(null_mut()),
        Some(source) => {
            let source = JValue::Byte(super::produce_kt_ubyte(source));

            let method_id = env.get_method_id("kotlin/UByte", "<init>", "(B)V")?;

            env.new_object_unchecked("kotlin/UByte", method_id, &[source])
                .map(JObject::into_raw)
        }
    }
}

// Kotlin Short?
/// Receives a Kotlin `Short?` through JNI.
///
/// # Background
/// While the Kotlin `Short` type is represented as a primitive `short` in JVM, the Kotlin `Short?`
/// type is represented as the boxed class `java/lang/Short` in JVM. Therefore, in Rust, we call the
/// JNI functions that call the `shortValue` method of the `java/lang/Short` class instance.
pub fn consume_kt_nullable_short(env: JNIEnv, source: JObject) -> Result<Option<i16>, JNIError> {
    if source.is_null() {
        return Ok(None); // null was passed from Kotlin, so return None
    }

    let method_id = env.get_method_id("java/lang/Short", "shortValue", "()S")?;

    env.call_method_unchecked(
        source,
        method_id,
        ReturnType::Primitive(Primitive::Short),
        &[],
    )?
    .s()
    .map(Some)
}
/// Prepares a Kotlin `Short?` to be sent through JNI.
///
/// # Background
/// While the Kotlin `Short` type is represented as a primitive `short` in JVM, the Kotlin `Short?`
/// type is represented as the boxed class `java/lang/Short` in JVM. Therefore, in Rust, we call the
/// JNI functions that create a new `java/lang/Short` class instance.
pub fn produce_kt_nullable_short(env: JNIEnv, source: Option<i16>) -> Result<jobject, JNIError> {
    match source {
        None => Ok(null_mut()),
        Some(source) => {
            let source = jvalue {
                s: super::produce_kt_short(source),
            };

            let method_id =
                env.get_static_method_id("java/lang/Short", "valueOf", "(S)Ljava/lang/Short;")?;

            env.call_static_method_unchecked(
                "java/lang/Short",
                method_id,
                ReturnType::Object,
                &[source],
            )?
            .l()
            .map(JObject::into_raw)
        }
    }
}

// Kotlin UShort?
/// Receives a Kotlin `UShort?` through JNI.
///
/// # Background
/// The `UShort` type is non-primitive in Kotlin, so in Rust, we call the JNI functions that get the
/// `data` field value of the `UShort` class instance.
///
/// NB: `UShort` is an inline class in Kotlin, so the nullable variant is represented in memory as a
/// boxed `kotlin/UShort` class instance, and the non-nullable variant is represented in memory as
/// the underlying `Short` value.
pub fn consume_kt_nullable_ushort(env: JNIEnv, source: JObject) -> Result<Option<u16>, JNIError> {
    if source.is_null() {
        return Ok(None); // null was passed from Kotlin, so return None
    }

    let field_id = env.get_field_id("kotlin/UShort", "data", "S")?;

    env.get_field_unchecked(source, field_id, ReturnType::Primitive(Primitive::Short))?
        .s()
        .map(|val| Some(super::consume_kt_ushort(val)))
}
/// Prepares a Kotlin `UShort?` to be sent through JNI.
///
/// # Background
/// The `UShort` type is non-primitive in Kotlin, so in Rust, we call the JNI functions that create
/// a new `UShort` class instance.
///
/// NB: `UShort` is an inline class in Kotlin, so the nullable variant is represented in memory as a
/// boxed `kotlin/UShort` class instance, and the non-nullable variant is represented in memory as
/// the underlying `Short` value.
///
/// # Failure
/// If the JVM cannot instantiate an `UShort` object (e.g. the JVM runs out of memory).
pub fn produce_kt_nullable_ushort(env: JNIEnv, source: Option<u16>) -> Result<jobject, JNIError> {
    match source {
        None => Ok(null_mut()),
        Some(source) => {
            let source = JValue::Short(super::produce_kt_ushort(source));

            let method_id = env.get_method_id("kotlin/UShort", "<init>", "(S)V")?;

            env.new_object_unchecked("kotlin/UShort", method_id, &[source])
                .map(JObject::into_raw)
        }
    }
}

// Kotlin Int?
/// Receives a Kotlin `Int?` through JNI.
///
/// # Background
/// While the Kotlin `Int` type is represented as a primitive `int` in JVM, the Kotlin `Int?` type
/// is represented as the boxed class `java/lang/Integer` in JVM. Therefore, in Rust, we call the
/// JNI functions that call the `intValue` method of the `java/lang/Integer` class instance.
pub fn consume_kt_nullable_int(env: JNIEnv, source: JObject) -> Result<Option<i32>, JNIError> {
    if source.is_null() {
        return Ok(None); // null was passed from Kotlin, so return None
    }

    let method_id = env.get_method_id("java/lang/Integer", "intValue", "()I")?;

    env.call_method_unchecked(
        source,
        method_id,
        ReturnType::Primitive(Primitive::Int),
        &[],
    )?
    .i()
    .map(Some)
}
/// Prepares a Kotlin `Int?` to be sent through JNI.
///
/// # Background
/// While the Kotlin `Int` type is represented as a primitive `int` in JVM, the Kotlin `Int?` type
/// is represented as the boxed class `java/lang/Integer` in JVM. Therefore, in Rust, we call the
/// JNI functions that create a new `java/lang/Integer` class instance.
pub fn produce_kt_nullable_int(env: JNIEnv, source: Option<i32>) -> Result<jobject, JNIError> {
    match source {
        None => Ok(null_mut()),
        Some(source) => {
            let source = jvalue {
                i: super::produce_kt_int(source),
            };

            let method_id =
                env.get_static_method_id("java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;")?;

            env.call_static_method_unchecked(
                "java/lang/Integer",
                method_id,
                ReturnType::Object,
                &[source],
            )?
            .l()
            .map(JObject::into_raw)
        }
    }
}

// Kotlin UInt?
/// Receives a Kotlin `UInt?` through JNI.
///
/// # Background
/// The `UInt` type is non-primitive in Kotlin, so in Rust, we call the JNI functions that get the
/// `data` field value of the `UInt` class instance.
///
/// NB: `UInt` is an inline class in Kotlin, so the nullable variant is represented in memory as a
/// boxed `kotlin/UInt` class instance, and the non-nullable variant is represented in memory as the
/// underlying `Int` value.
pub fn consume_kt_nullable_uint(env: JNIEnv, source: JObject) -> Result<Option<u32>, JNIError> {
    if source.is_null() {
        return Ok(None); // null was passed from Kotlin, so return None
    }

    let field_id = env.get_field_id("kotlin/UInt", "data", "I")?;

    env.get_field_unchecked(source, field_id, ReturnType::Primitive(Primitive::Int))?
        .i()
        .map(|val| Some(super::consume_kt_uint(val)))
}
/// Prepares a Kotlin `UInt?` to be sent through JNI.
///
/// # Background
/// The `UInt` type is non-primitive in Kotlin, so in Rust, we call the JNI functions that create
/// a new `UInt` class instance.
///
/// NB: `UInt` is an inline class in Kotlin, so the nullable variant is represented in memory as a
/// boxed `kotlin/UInt` class instance, and the non-nullable variant is represented in memory as the
/// underlying `Int` value.
///
/// # Failure
/// If the JVM cannot instantiate an `UInt` object (e.g. the JVM runs out of memory).
pub fn produce_kt_nullable_uint(env: JNIEnv, source: Option<u32>) -> Result<jobject, JNIError> {
    match source {
        None => Ok(null_mut()),
        Some(source) => {
            let source = JValue::Int(super::produce_kt_uint(source));

            let method_id = env.get_method_id("kotlin/UInt", "<init>", "(I)V")?;

            env.new_object_unchecked("kotlin/UInt", method_id, &[source])
                .map(JObject::into_raw)
        }
    }
}

// Kotlin Long?
/// Receives a Kotlin `Long?` through JNI.
///
/// # Background
/// While the Kotlin `Long` type is represented as a primitive `long` in JVM, the Kotlin `Long?`
/// type is represented as the boxed class `java/lang/Long` in JVM. Therefore, in Rust, we call the
/// JNI functions that call the `longValue` method of the `java/lang/Long` class instance.
pub fn consume_kt_nullable_long(env: JNIEnv, source: JObject) -> Result<Option<i64>, JNIError> {
    if source.is_null() {
        return Ok(None); // null was passed from Kotlin, so return None
    }

    let method_id = env.get_method_id("java/lang/Long", "longValue", "()J")?;

    env.call_method_unchecked(
        source,
        method_id,
        ReturnType::Primitive(Primitive::Long),
        &[],
    )?
    .j()
    .map(Some)
}
/// Prepares a Kotlin `Long?` to be sent through JNI.
///
/// # Background
/// While the Kotlin `Long` type is represented as a primitive `long` in JVM, the Kotlin `Long?`
/// type is represented as the boxed class `java/lang/Long` in JVM. Therefore, in Rust, we call the
/// JNI functions that create a new `java/lang/Long` class instance.
pub fn produce_kt_nullable_long(env: JNIEnv, source: Option<i64>) -> Result<jobject, JNIError> {
    match source {
        None => Ok(null_mut()),
        Some(source) => {
            let source = jvalue {
                j: super::produce_kt_long(source),
            };

            let method_id =
                env.get_static_method_id("java/lang/Long", "valueOf", "(J)Ljava/lang/Long;")?;

            env.call_static_method_unchecked(
                "java/lang/Long",
                method_id,
                ReturnType::Object,
                &[source],
            )?
            .l()
            .map(JObject::into_raw)
        }
    }
}

// Kotlin ULong?
/// Receives a Kotlin `ULong?` through JNI.
///
/// # Background
/// The `ULong` type is non-primitive in Kotlin, so in Rust, we call the JNI functions that get the
/// `data` field value of the `ULong` class instance.
///
/// NB: `ULong` is an inline class in Kotlin, so the nullable variant is represented in memory as a
/// boxed `kotlin/ULong` class instance, and the non-nullable variant is represented in memory as
/// the underlying `Long` value.
pub fn consume_kt_nullable_ulong(env: JNIEnv, source: JObject) -> Result<Option<u64>, JNIError> {
    if source.is_null() {
        return Ok(None); // null was passed from Kotlin, so return None
    }

    let field_id = env.get_field_id("kotlin/ULong", "data", "J")?;

    env.get_field_unchecked(source, field_id, ReturnType::Primitive(Primitive::Long))?
        .j()
        .map(|val| Some(super::consume_kt_ulong(val)))
}
/// Prepares a Kotlin `ULong?` to be sent through JNI.
///
/// # Background
/// The `ULong` type is non-primitive in Kotlin, so in Rust, we call the JNI functions that create
/// a new `ULong` class instance.
///
/// NB: `ULong` is an inline class in Kotlin, so the nullable variant is represented in memory as a
/// boxed `kotlin/ULong` class instance, and the non-nullable variant is represented in memory as
/// the underlying `Long` value.
///
/// # Failure
/// If the JVM cannot instantiate an `ULong` object (e.g. the JVM runs out of memory).
pub fn produce_kt_nullable_ulong(env: JNIEnv, source: Option<u64>) -> Result<jobject, JNIError> {
    match source {
        None => Ok(null_mut()),
        Some(source) => {
            let source = JValue::Long(super::produce_kt_ulong(source));

            let method_id = env.get_method_id("kotlin/ULong", "<init>", "(J)V")?;

            env.new_object_unchecked("kotlin/ULong", method_id, &[source])
                .map(JObject::into_raw)
        }
    }
}

// Kotlin Float?
/// Receives a Kotlin `Float?` through JNI.
///
/// # Background
/// While the Kotlin `Float` type is represented as a primitive `float` in JVM, the Kotlin `Float?`
/// type is represented as the boxed class `java/lang/Float` in JVM. Therefore, in Rust, we call the
/// JNI functions that call the `floatValue` method of the `java/lang/Float` class instance.
pub fn consume_kt_nullable_float(env: JNIEnv, source: JObject) -> Result<Option<f32>, JNIError> {
    if source.is_null() {
        return Ok(None); // null was passed from Kotlin, so return None
    }

    let method_id = env.get_method_id("java/lang/Float", "floatValue", "()F")?;

    env.call_method_unchecked(
        source,
        method_id,
        ReturnType::Primitive(Primitive::Float),
        &[],
    )?
    .f()
    .map(Some)
}
/// Prepares a Kotlin `Float?` to be sent through JNI.
///
/// # Background
/// While the Kotlin `Float` type is represented as a primitive `float` in JVM, the Kotlin `Float?`
/// type is represented as the boxed class `java/lang/Float` in JVM. Therefore, in Rust, we call the
/// JNI functions that create a new `java/lang/Float` class instance.
pub fn produce_kt_nullable_float(env: JNIEnv, source: Option<f32>) -> Result<jobject, JNIError> {
    match source {
        None => Ok(null_mut()),
        Some(source) => {
            let source = jvalue {
                f: super::produce_kt_float(source),
            };

            let method_id =
                env.get_static_method_id("java/lang/Float", "valueOf", "(F)Ljava/lang/Float;")?;

            env.call_static_method_unchecked(
                "java/lang/Float",
                method_id,
                ReturnType::Object,
                &[source],
            )?
            .l()
            .map(JObject::into_raw)
        }
    }
}

// Kotlin Double?
/// Receives a Kotlin `Double?` through JNI.
///
/// # Background
/// While the Kotlin `Double` type is represented as a primitive `double` in JVM, the Kotlin
/// `Double?` type is represented as the boxed class `java/lang/Double` in JVM. Therefore, in Rust,
/// we call the JNI functions that call the `intValue` method of the `java/lang/Double` class
/// instance.
pub fn consume_kt_nullable_double(env: JNIEnv, source: JObject) -> Result<Option<f64>, JNIError> {
    if source.is_null() {
        return Ok(None); // null was passed from Kotlin, so return None
    }

    let method_id = env.get_method_id("java/lang/Double", "doubleValue", "()D")?;

    env.call_method_unchecked(
        source,
        method_id,
        ReturnType::Primitive(Primitive::Double),
        &[],
    )?
    .d()
    .map(Some)
}
/// Prepares a Kotlin `Double?` to be sent through JNI.
///
/// # Background
/// While the Kotlin `Double` type is represented as a primitive `double` in JVM, the Kotlin
/// `Double?` type is represented as the boxed class `java/lang/Double` in JVM. Therefore, in Rust,
/// we call the JNI functions that create a new `java/lang/Double` class instance.
pub fn produce_kt_nullable_double(env: JNIEnv, source: Option<f64>) -> Result<jobject, JNIError> {
    match source {
        None => Ok(null_mut()),
        Some(source) => {
            let source = jvalue {
                d: super::produce_kt_double(source),
            };

            let method_id =
                env.get_static_method_id("java/lang/Double", "valueOf", "(D)Ljava/lang/Double;")?;

            env.call_static_method_unchecked(
                "java/lang/Double",
                method_id,
                ReturnType::Object,
                &[source],
            )?
            .l()
            .map(JObject::into_raw)
        }
    }
}
// Kotlin Void?: nothing to implement

// Kotlin String?
/// Receives a Kotlin `String?` through JNI.
///
/// # Failure
/// If the object passed from Kotlin is not a `String` (programmer error).
pub fn consume_kt_nullable_string(
    env: JNIEnv,
    source: JString,
) -> Result<Option<String>, JNIError> {
    if source.is_null() {
        return Ok(None); // null was passed from Kotlin, so return None
    }
    super::consume_kt_string(env, source).map(Some)
}
/// Prepares a Kotlin `String?` to be sent through JNI.
///
/// # Failure
/// If the JVM runs out of memory (as indicated in the JNI specification).
pub fn produce_kt_nullable_string(
    env: JNIEnv,
    source: Option<String>,
) -> Result<jstring, JNIError> {
    match source {
        None => Ok(null_mut()),
        Some(source) => super::produce_kt_string(env, source),
    }
}
