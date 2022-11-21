use jni::objects::{JClass, JString};
// use jni::sys::{jbyteArray, jsize};
use jni::sys::jstring;
use jni::JNIEnv;
// use std::ptr::null_mut;
// use sphinx::crypto::STREAM_CIPHER_KEY_SIZE;
// use std::ptr::null_mut;

mod utils;

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_NymHandlerKt_initImpl(
    env: JNIEnv,
    _: JClass,
    testPath: JString,
) -> jstring {
    let testPath: String = env
        .get_string(testPath)
        .expect("Couldn't get testPath from Kotlin")
        .into();
    let output = env
        .new_string(format!("Wow, I got {}", testPath))
        .expect("Rust: Unable to create new string");
    output.into_raw()
}

// #[no_mangle]
// #[allow(clippy::expect_used)]
// pub extern "C" fn Java_com_kaeonx_nymandroidport_SphinxHandlerKt_generatePseudorandomBytesImpl(
//     env: JNIEnv,
//     _: JClass,
//     key: jbyteArray, // this method will check if len == STREAM_CIPHER_KEY_SIZE during try_into()
//     iv: jbyteArray,  // this method will check if len == STREAM_CIPHER_KEY_SIZE during try_into()
//     length: jsize,
// ) -> jbyteArray {
//     let key = if let Ok(vec) = env.convert_byte_array(key) {
//         vec
//     } else {
//         env.throw("Rust: Unable to get key array from Kotlin")
//             .expect("Rust: Unable to throw Kotlin Exception");
//         return null_mut();
//     };
//     // Vec<u8>::try_into() delegates to [T; N]::try_from(), which performs size check on N
//     // https://doc.rust-lang.org/rust-by-example/conversion/try_from_try_into.html
//     // https://doc.rust-lang.org/std/primitive.array.html#impl-TryFrom%3CVec%3CT%2C%20A%3E%3E-for-%5BT%3B%20N%5D
//     let key: [u8; STREAM_CIPHER_KEY_SIZE] = match key.try_into() {
//         Ok(arr) => arr,
//         Err(vec) => {
//             env.throw(format!(
//                 "key has invalid length: expected {}, got {}",
//                 STREAM_CIPHER_KEY_SIZE,
//                 vec.len()
//             ))
//             .expect("Rust: Unable to throw Kotlin Exception");
//             return null_mut();
//         }
//     };

//     let iv = if let Ok(vec) = env.convert_byte_array(iv) {
//         vec
//     } else {
//         env.throw("Rust: Unable to get iv array from Kotlin")
//             .expect("Rust: Unable to throw Kotlin Exception");
//         return null_mut();
//     };
//     let iv: [u8; STREAM_CIPHER_KEY_SIZE] = match iv.try_into() {
//         Ok(arr) => arr,
//         Err(vec) => {
//             env.throw(format!(
//                 "iv has invalid length: expected {}, got {}",
//                 STREAM_CIPHER_KEY_SIZE,
//                 vec.len()
//             ))
//             .expect("Rust: Unable to throw Kotlin Exception");
//             return null_mut();
//         }
//     };

//     let length = if let Ok(res) = length.try_into() {
//         res
//     } else {
//         env.throw("Rust: Numerical out of bounds from jsize (Kotlin) to usize (Rust)")
//             .expect("Rust: Unable to throw Kotlin Exception");
//         return null_mut();
//     };

//     let result = sphinx::crypto::generate_pseudorandom_bytes(&key, &iv, length);
//     match env.byte_array_from_slice(&result) {
//         Ok(res) => res,
//         Err(e) => {
//             // uses Display trait from e's usage of #[derive(thiserror::Error)]
//             env.throw(format!("Rust: Unable to create jbyteArray ({})", e))
//                 .expect("Rust: Unable to throw Kotlin Exception");
//             null_mut()
//         }
//     }
// }
