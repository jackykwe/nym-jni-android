// TODO interface improvement: Error.throw_as_java (thiserror & anyhow, instead of using String as the error type)

/// # Motivation
/// When Rust functions take an error branch, we'd like to raise a JVM exception. However, the act
/// of raising a JVM exception can itself fail, and the code required to handle that case is
/// repetitive. This macro abstracts this common code away. A macro is used because of the need to
/// handle `func_name` functions with varying number of arguments. The structure of this macro is
/// inspired from Rust's `Result::or` method.
///
/// # Signature and behaviour
/// ```
/// call_fallible_or!(or, func_name, env, class_or_object, arg1, arg2, ..., argN);
/// ```
/// This macro calls `func_name` with the arguments `arg1` to `argN`.
/// - On success, this macro simply returns the return value of `func_name`.
/// - On fail, attempts to throw a JVM e xception through `env`.
///   - On success, returns `or`. This is typically some constant, like `0` if the return type of
/// `func_name` is `jint`.
///   - On failure, panics.
///
/// # Arguments
/// - `or`: the value this macro returns when the underlying call to `func_name` fails for any
/// reason. The `or` value is eagerly evaluated; if you are passing the result of a function call,
/// it is recommended to use `call_fallible_or_else!`, whose first argument lazily evaluated.
/// - `func_name`: a function whose first argument is `env` (of type `JNIEnv`), second argument is
/// `class_or_object` (of type `JClass` or `JObject`), and may take any number of additional
/// arguments. This function is expected to return a type `Result<_, String>`.
/// - `env`: a `JNIEnv`, a standard for JNI functions
/// - `class_or_object`, a `JClass` or `JObject`, a standard for JNI functions
/// - `arg1` to `argN`: any number of arguments of any type additionally expected by `func_name`.
///
/// # Returns
/// This macro's return type is the same as `func_name`.
///
/// # Panics
/// Iff the `func_name` fails AND this macro fails to raise a JVM exception through `env`.
///
/// # Example
/// ```
/// call_fallible_or!(or, func_name, env, class_or_object, arg1, arg2, ..., argN);
/// ```
/// expands into
/// ```
/// match func_name(env, class_or_object, arg1, arg2, ..., argN) {
///     Ok(res) => res,
///     Err(str) => {
///         env.throw(str)
///              .expect("Rust: Unable to throw Kotlin Exception");
///         or
///     }
/// }
/// ```
#[macro_export]
macro_rules! call_fallible_or {
    // No argument case, added so that the macro can be called without a trailing comma inside the
    // parentheses
    ($or:expr, $func_name:ident, $env:expr, $class_or_object:expr) => {
        match $func_name($env, $class_or_object) {
            Ok(res) => res,
            Err(str) => {
                $env.throw(str)
                    .expect("Rust: Unable to throw Kotlin Exception");
                $or
            }
        }
    };
    ($or:expr, $func_name:ident, $env:expr, $class_or_object:expr, $( $arg:expr ),*) => {
        match $func_name($env, $class_or_object, $( $arg ),*) {
            Ok(res) => res,
            Err(str) => {
                $env.throw(str)
                    .expect("Rust: Unable to throw Kotlin Exception");
                $or
            }
        }
    };
}

/// # Motivation
/// When Rust functions take an error branch, we'd like to raise a JVM exception. However, the act
/// of raising a JVM exception can itself fail, and the code required to handle that case is
/// repetitive. This macro abstracts this common code away. A macro is used because of the need to
/// handle `func_name` functions with varying number of arguments. The structure of this macro is
/// inspired from Rust's `Result::or_else` method.
///
/// # Signature and behaviour
/// ```
/// call_fallible_or!(or_else_fn, func_name, env, class_or_object, arg1, arg2, ..., argN);
/// ```
/// This macro calls `func_name` with the arguments `arg1` to `argN`.
/// - On success, this macro simply returns the return value of `func_name`.
/// - On fail, attempts to throw a JVM exception through `env`.
///   - On success, returns the result evaluating `or_else_fn`. This is typically a null pointer
/// generating function, `null_mut`.
///   - On failure, panics.
///
/// # Arguments
/// - `or_else_fn`: the function this macro calls to generate a value to return when the underlying
/// call to `func_name` fails for any reason. The `or_else_fn` value is lazily evaluated, only on
/// failure of `func_name`.
/// - `func_name`: a function whose first argument is `env` (of type `JNIEnv`), second argument is
/// `class_or_object` (of type `JClass` or `JObject`), and may take any number of additional
/// arguments. This function is expected to return a type `Result<_, String>`.
/// - `env`: a `JNIEnv`, a standard for JNI functions
/// - `class_or_object`, a `JClass` or `JObject`, a standard for JNI functions
/// - `arg1` to `argN`: any number of arguments of any type additionally expected by `func_name`.
///
/// # Returns
/// This macro's return type is the same as `func_name`.
///
/// # Panics
/// Iff the `func_name` fails AND this macro fails to raise a JVM exception through `env`.
///
/// # Example
/// ```
/// call_fallible_or_else!(or_else_fn, func_name, env, class_or_object, arg1, arg2, ..., argN);
/// ```
/// expands into
/// ```
/// match func_name(env, class_or_object, arg1, arg2, ..., argN) {
///     Ok(res) => res,
///     Err(str) => {
///         env.throw(str)
///              .expect("Rust: Unable to throw Kotlin Exception");
///         or_else_fn()
///     }
/// }
/// ```
#[macro_export]
macro_rules! call_fallible_or_else {
    // No argument case, added so that the macro can be called without a trailing comma inside the
    // parentheses
    ($or_else_fn:expr, $func_name:ident, $env:expr, $class_or_object:expr) => {
        match $func_name($env, $class_or_object) {
            Ok(res) => res,
            Err(str) => {
                $env.throw(str)
                    .expect("Rust: Unable to throw Kotlin Exception");
                $or_else_fn()
            }
        }
    };
    ($or_else_fn:expr, $func_name:ident, $env:expr, $class_or_object:expr, $( $arg:expr ),*) => {
        match $func_name($env, $class_or_object, $( $arg ),*) {
            Ok(res) => res,
            Err(str) => {
                $env.throw(str)
                    .expect("Rust: Unable to throw Kotlin Exception");
                $or_else_fn()
            }
        }
    };
}

/// # Motivation
/// When Rust functions take an error branch, we'd like to raise a JVM exception. However, the act
/// of raising a JVM exception can itself fail, and the code required to handle that case is
/// repetitive. This macro abstracts this common code away. A macro is used because of the need to
/// handle `func_name` functions with varying number of arguments. The structure of this macro is
/// inspired from Rust's `Result::or` method.
///
/// # Signature and behaviour
/// ```
/// call_fallible!(func_name, env, class_or_object, arg1, arg2, ..., argN);
/// ```
/// This macro calls `func_name` with the arguments `arg1` to `argN`.
/// - On success, this macro simply returns.
/// - On fail, attempts to throw a JVM exception through `env`.
///   - On success, returns.
///   - On failure, panics.
///
/// # Arguments
/// - `func_name`: a function whose first argument is `env` (of type `JNIEnv`), second argument is
/// `class_or_object` (of type `JClass` or `JObject`), and may take any number of additional
/// arguments. This function is expected to return a type `Result<_, String>`.
/// - `env`: a `JNIEnv`, a standard for JNI functions
/// - `class_or_object`, a `JClass` or `JObject`, a standard for JNI functions
/// - `arg1` to `argN`: any number of arguments of any type additionally expected by `func_name`.
///
/// # Returns
/// Nothing. Used for `func_name` functions with only side effects.
///
/// # Panics
/// Iff the `func_name` fails AND this macro fails to raise a JVM exception through `env`.
///
/// # Example
/// ```
/// call_fallible!(func_name, env, class_or_object, arg1, arg2, ..., argN);
/// ```
/// expands into
/// ```
/// if let Err(str) = func_name(env, class_or_object, arg1, arg2, ..., argN) {
///     env.throw(str).expect("Rust: Unable to throw Kotlin Exception");
/// };
/// ```
#[macro_export]
macro_rules! call_fallible {
    // No argument case, added so that the macro can be called without a trailing comma inside the
    // parentheses
    ($func_name:ident, $env:expr, $class_or_object:expr) => {
        if let Err(str) = $func_name($env, $class_or_object) {
            $env.throw(str).expect("Rust: Unable to throw Kotlin Exception");
        }
    };
    ($func_name:ident, $env:expr, $class_or_object:expr, $( $arg:expr ),*) => {
        if let Err(str) = $func_name($env, $class_or_object, $( $arg ),*) {
            $env.throw(str).expect("Rust: Unable to throw Kotlin Exception");
        }
    };
}
