#!/bin/bash
# Add executing this file to Android Studio build configuration under "external tools"

mkdir -p app/src/main/jniLibs/x86_64

cd ../NymAndroidPortRust1 || exit
cargo build --color always
cp target/x86_64-linux-android/debug/librustcode.so ../NymAndroidPort/app/src/main/jniLibs/x86_64

cd ../NymAndroidPortRust2 || exit
cargo build --color always
cp target/x86_64-linux-android/debug/librustcode2.so ../NymAndroidPort/app/src/main/jniLibs/x86_64

echo "OK"