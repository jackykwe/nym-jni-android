#!/bin/bash

./generateConstantsFiles.sh || exit 1

currentDir=$(pwd)
echo "$currentDir"
mkdir -p app/src/main/jniLibs/aarch64
mkdir -p app/src/main/jniLibs/x86_64

cd ../sphinx-jni || exit 2
cargo build --color always --target aarch64-linux-android
cargo build --color always --target x86_64-linux-android
cargo run
cd "$currentDir" || exit 3
cp ../sphinx-jni/generated/SphinxConstants.kt app/src/main/java/com/kaeonx/nymandroidport
cp ../sphinx-jni/target/aarch64-linux-android/debug/libsphinx_jni.so app/src/main/jniLibs/aarch64
cp ../sphinx-jni/target/x86_64-linux-android/debug/libsphinx_jni.so app/src/main/jniLibs/x86_64

echo "beforeAppBuild: OK"