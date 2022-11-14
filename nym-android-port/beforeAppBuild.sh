#!/bin/bash

currentDir=$(pwd)
echo "$currentDir"
mkdir -p app/src/main/jniLibs/x86_64

cd ../NymAndroidPortRust1 || exit 1
cargo build --color always --target aarch64-linux-android
cargo build --color always --target x86_64-linux-android
cd "$currentDir" || exit 1
cp ../NymAndroidPortRust1/target/aarch64-linux-android/debug/librustcode.so app/src/main/jniLibs/aarch64
cp ../NymAndroidPortRust1/target/x86_64-linux-android/debug/librustcode.so app/src/main/jniLibs/x86_64

cd ../NymAndroidPortRust2 || exit 1
cargo build --color always --target aarch64-linux-android
cargo build --color always --target x86_64-linux-android
cd "$currentDir" || exit 1
cp ../NymAndroidPortRust2/target/aarch64-linux-android/debug/librustcode2.so app/src/main/jniLibs/aarch64
cp ../NymAndroidPortRust2/target/x86_64-linux-android/debug/librustcode2.so app/src/main/jniLibs/x86_64

echo "OK"