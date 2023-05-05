# nym-jni-android

Main repository containing all Android-centric code for the prototype port of Nym to Android. The siblings to this repository are [`nym`](https://github.com/jackykwe/nym) (a fork of [`nymtech/nym`](https://github.com/nymtech/nym) and [`nym-data-analysis`](https://github.com/jackykwe/nym-data-analysis).

This repository contains 3 sub-repositories:
- `nym-android-port`: The Android Studio project for the Nym Android client (prototype).
- `nym-jni`: A Rust crate that glues `nym` with `nym-android-port`. Additionally, it contains the `jvm_kotlin_typing` library.
- `nym-pc`: A Rust crate that contains the PC client. Adapted from example code in [`nymtech/nym`](https://github.com/nymtech/nym/blob/d92d6877a47aeec233a65658caba0379c75a9788/clients/native/examples/websocket_textsend.rs).

