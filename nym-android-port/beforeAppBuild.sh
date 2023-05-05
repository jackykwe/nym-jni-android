#!/bin/bash

#./generateConstantsFiles.sh || exit 1

currentDir=$(pwd)
echo "$currentDir"
mkdir -p app/src/main/jniLibs/arm64-v8a
mkdir -p app/src/main/jniLibs/x86_64

cd ../nym-jni || exit 2

# IMPORTANT

# 3 checks for DEBUG BUILD:
# - Android Studio build variant is debug?
# - --release flag is not used?
# - Copied libnym_jni.so from ../nym-jni/target/<target>/debug folder instead of ../nym-jni/target/<target>/release?

# 3 checks for RELEASE BUILD:
# - Android Studio build variant is release?
# - --release flag is used?
# - Copied libnym_jni.so from ../nym-jni/target/<target>/release folder instead of ../nym-jni/target/<target>/debug?

#cargo build --color always --target aarch64-linux-android --release || exit 3
cargo build --color always --target aarch64-linux-android || exit 3
#cargo build --color always --target x86_64-linux-android --release || exit 4
#cargo build --color always --target x86_64-linux-android || exit 4
cd "$currentDir" || exit 5
#cp -u ../nym-jni/target/aarch64-linux-android/release/libnym_jni.so app/src/main/jniLibs/arm64-v8a || exit 6
cp -u ../nym-jni/target/aarch64-linux-android/debug/libnym_jni.so app/src/main/jniLibs/arm64-v8a || exit 6
#cp -u ../nym-jni/target/x86_64-linux-android/release/libnym_jni.so app/src/main/jniLibs/x86_64 || exit 7
#cp -u ../nym-jni/target/x86_64-linux-android/debug/libnym_jni.so app/src/main/jniLibs/x86_64 || exit 7

echo "beforeAppBuild: OK"
