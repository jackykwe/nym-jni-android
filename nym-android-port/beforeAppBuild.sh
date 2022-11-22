#!/bin/bash

#./generateConstantsFiles.sh || exit 1

currentDir=$(pwd)
echo "$currentDir"
mkdir -p app/src/main/jniLibs/aarch64
mkdir -p app/src/main/jniLibs/x86_64

cd ../nym-jni || exit 2
#cargo build --color always --target aarch64-linux-android || exit 3
cargo build --color always --target x86_64-linux-android || exit 4
cd "$currentDir" || exit 5
#cp ../nym-jni/target/aarch64-linux-android/debug/libnym_jni.so app/src/main/jniLibs/aarch64 || exit 6
cp ../nym-jni/target/x86_64-linux-android/debug/libnym_jni.so app/src/main/jniLibs/x86_64 || exit 7

echo "beforeAppBuild: OK"