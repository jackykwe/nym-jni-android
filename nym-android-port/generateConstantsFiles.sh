#!/bin/bash

currentDir=$(pwd)
echo "$currentDir"

cd ../sphinx-jni || exit 1
cargo run
cd "$currentDir" || exit 1
cp ../sphinx-jni/generated/SphinxConstants.kt app/src/main/java/com/kaeonx/nymandroidport

echo "generateConstantsFiles: OK"