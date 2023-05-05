cd ../nym
cloc --include-ext=rs --out ../nym-jni-android/cloc-nym.txt --diff d92d687 7a58931

cd ../nym-jni-android
cd nym-pc
cloc --include-ext=rs,sh,py --out ../cloc-nym-pc.txt --git communicate-with-pc-demo

cd ../nym-jni
cloc --include-ext=rs,sh,py --out ../cloc-nym-jni.txt --git main --match-d src/

cd ../nym-android-port
cloc --include-ext=kt,sh --out ../cloc-nym-android-port.txt --git main

cd ../../nym-data-analysis
cloc --include-ext=py,ipynb,sh --out ../nym-jni-android/cloc-nym-data-analysis.txt library/ *.ipynb *.sh

cd ../nym-jni-android
cloc --sum-reports cloc-{nym-pc,nym-jni,nym-android-port,nym-data-analysis}.txt
cat cloc-nym.txt
echo "Need to manually sum numbers..."
