use std::fs::File;
use std::io::{BufWriter, Write};
use std::ptr::null_mut;

use jni::objects::{JClass, JString};
use jni::sys::jstring;
use jni::JNIEnv;

mod utils;

#[no_mangle]
pub extern "C" fn Java_com_kaeonx_nymandroidport_NymHandlerKt_initImpl(
    env: JNIEnv,
    class: JClass,
    test_path: JString,
) -> jstring {
    call_fallible_or_else!(
        null_mut,
        Java_com_kaeonx_nymandroidport_NymHandlerKt_initImpl_fallible,
        env,
        class,
        test_path
    )
}

#[allow(non_snake_case)]
fn Java_com_kaeonx_nymandroidport_NymHandlerKt_initImpl_fallible(
    env: JNIEnv,
    _: JClass,
    test_path: JString,
) -> Result<jstring, String> {
    let test_path: String = env
        .get_string(test_path)
        .map(Into::into)
        .map_err(|_| String::from("Rust: Unable to get testPath from Kotlin"))?;

    let file = File::create(test_path.clone()).map_err(|err| {
        format!(
            "Rust: Unable to open file {} in write mode ({})",
            test_path, err
        )
    })?;
    // BufWriter to buffer repeated writes, reduces number of syscalls
    let mut writer = BufWriter::new(file);
    writeln!(writer, "package com.kaeonx.nymandroidport")
        .map_err(|_| format!("Rust: Unable to write to file {}", test_path))?;
    writeln!(writer).map_err(|_| format!("Rust: Unable to write to file {}", test_path))?;
    writeln!(writer, "// Writing to Android storage from Rust!")
        .map_err(|_| format!("Rust: Unable to write to file {}", test_path))?;
    writeln!(writer).map_err(|_| format!("Rust: Unable to write to file {}", test_path))?;

    env.new_string(format!("Rust: Successfully wrote to {}!", test_path))
        .map(JString::into_raw)
        .map_err(|err| format!("Rust: Unable to create jstring ({})", err))
}
