#!/bin/bash

currentDir=$(pwd)
echo "$currentDir"
mkdir -p app/src/main/jniLibs/arm64-v8a
#mkdir -p app/src/main/jniLibs/x86_64  # For emulator

cd ../../nym || exit 3

# Must ensure nym crate is in probe-effect-evaluation branch, as the communicate-with-pc-demo is
# a child of the probe-effect-evaluation branch and isn't compatible with the main branch (implementation gore)
current_branch_check=$(git describe --tags 2>/dev/null || echo NA) # returns 'NA' if no tags exist in repo, or current commit's tag ('probe-effect-evaluation' is only returned if on the commit tagged with probe-effect-evaluation)
if [ "$current_branch_check" != 'probe-effect-evaluation' ]; then
    git checkout -q tags/probe-effect-evaluation || (
        echo 'Failed to ensure "nym" is on tag "probe-effect-evaluation"'
        exit 8
    )
fi
echo "'nym' is on tag 'tag/$(git describe --tags 2>/dev/null || echo NA)'"

cd ../nym-jni-android/nym-jni || exit 2

# IMPORTANT

# 3 checks for DEBUG BUILD:
# - Android Studio build variant is debug?
# - --release flag is not used?
# - Copying from .../debug/... folder instead of .../release/...?

# 3 checks for RELEASE BUILD:
# - Android Studio build variant is release?
# - --release flag is used?
# - Copying from .../release/... folder instead of .../debug/...?

cargo build --color always --target aarch64-linux-android --release || exit 3
#cargo build --color always --target aarch64-linux-android || exit 3
#cargo build --color always --target x86_64-linux-android --release || exit 4
#cargo build --color always --target x86_64-linux-android || exit 4

cd "$currentDir" || exit 5
cp -u ../nym-jni/target/aarch64-linux-android/release/libnym_jni.so app/src/main/jniLibs/arm64-v8a || exit 6
#cp -u ../nym-jni/target/aarch64-linux-android/debug/libnym_jni.so app/src/main/jniLibs/arm64-v8a || exit 6
#cp -u ../nym-jni/target/x86_64-linux-android/release/libnym_jni.so app/src/main/jniLibs/x86_64 || exit 7
#cp -u ../nym-jni/target/x86_64-linux-android/debug/libnym_jni.so app/src/main/jniLibs/x86_64 || exit 7

echo "beforeAppBuild: OK"
