#!/bin/bash

export RUST_BACKTRACE=1
SCRIPT_NAME='collect_android_data_phy.sh'

############
# ARGPARSE #
############

TEMP=$(getopt -o 'v:p:c:b:m:h' --long 'variant:,probeeffect:,connectivity:,battery-restriction:,max-messages:,help' -n "$SCRIPT_NAME" -s 'bash' -- "$@")

if [ $? -ne 0 ]; then
    echo 'Try '"'--help'"' for more information.' >&2
    exit 1
fi

function help() {
    echo 'Usage:'
    echo 'collect_android_data_phy.sh --variant debug|release --connectivity data|wifi'
    echo '                            --battery-restriction unrestricted|optimised|restricted'
    echo '                            --probeeffect true|false [--max-messages N]'
    echo
    echo 'Utility to create a new Nym Client, run it, and collect timestamps from it.'
    echo 'The timestamps are saved into a new text file in the current working '
    echo 'directory. The name of the file will be printed on execution of this script.'
    echo
    echo 'This utility may trigger recompilation of the underlying Android application and Rust'
    echo 'crates '"'nym'"' and '"'nym-pc'"'.'
    echo
    echo 'Options:'
    echo '-b, --battery-restriction unrestricted|optimised|restricted'
    echo '        (required) selects the user-defined battery restriction mode of the Android app.'
    echo '-c, --connectivity data|wifi'
    echo '        (required) automatically switches between mobile data or WiFi in preparation for'
    echo '        data collection.'
    echo '-h, --help'
    echo '        show this help message and exit'
    echo '-m, --max-messages N'
    echo '        stop the evaluation after exactly N messages are sent through the nym network. If'
    echo '        not specified, the evaluation will not terminate until explictily interrupted.'
    echo '-p, --probeeffect true|false'
    echo '        (required) whether to run the builds of the underlying Rust crates '"'nym'"' and '
    echo '        '"'nym-pc'"', while collecting only timestamps tK=1 and tK=8.'
    echo '-v, --variant debug|release'
    echo '        (required) whether to run the debug or release builds of the underlying Rust '
    echo '        crates '"'nym'"' and '"'nym-pc'"'. This collects all timestamps from tK=1 to tK=8'
    echo '        inclusive.'
}

# Note the quotes around "$TEMP": they are essential!
eval set -- "$TEMP"
unset TEMP

while true; do
    case "$1" in
    '-h' | '--help')
        help
        exit 0
        ;;
    '-v' | '--variant')
        case "$2" in
        'debug' | 'release')
            variant=$2
            ;;
        *)
            echo 'Invalid argument passed to option -v/--variant. Expected either '"'debug'"' or '"'release'"'; got '"'$2'"
            exit 2
            ;;
        esac
        shift 2
        continue
        ;;
    '-c' | '--connectivity')
        case "$2" in
        'data' | 'wifi')
            connectivity=$2
            ;;
        *)
            echo 'Invalid argument passed to option -c/--connectivity. Expected either '"'data'"' or '"'wifi'"'; got '"'$2'"
            exit 2
            ;;
        esac
        shift 2
        continue
        ;;
    '-b' | '--battery-restriction')
        case "$2" in
        'unrestricted' | 'optimised' | 'restricted')
            battery_restriction=$2
            ;;
        *)
            echo 'Invalid argument passed to option -b/--battery-restriction. Expected one of '"'unrestricted'"', '"'optimised'"' or '"'restricted'"'; got '"'$2'"
            exit 2
            ;;
        esac
        shift 2
        continue
        ;;
    '-p' | '--probeeffect')
        case "$2" in
        'true' | 'false')
            probeeffect=$2
            ;;
        *)
            echo 'Invalid argument passed to option -p/--probeeffect. Expected either '"'true'"' or '"'false'"'; got '"'$2'"
            exit 2
            ;;
        esac
        shift 2
        continue
        ;;
    '-m' | '--max-messages')
        case "$2" in
        # https://www.gnu.org/software/bash/manual/html_node/Pattern-Matching.html
        *[0-9])
            max_messages=$2
            ;;
        *)
            echo 'Invalid argument passed to -m/--max-messages. Expected a positive integer.'
            exit 2
            ;;
        esac
        shift 2
        continue
        ;;
    '--')
        shift
        break
        ;;
    *)
        echo 'Internal argparse error!' "$1" >&2
        exit 2
        ;;
    esac
done

# https://www.gnu.org/software/bash/manual/bash.html#Bash-Conditional-Expressions
# Courtesy of https://stackoverflow.com/a/13864829
if [ -z ${variant+defined} ]; then
    echo '-v/--variant is a required argument'
    exit 2
fi
if [ -z ${connectivity+defined} ]; then
    echo '-c/--connectivity is a required argument'
    exit 2
fi
if [ -z ${battery_restriction+defined} ]; then
    echo '-b/--battery-restriction is a required argument'
    exit 2
fi
if [ -z ${probeeffect+defined} ]; then
    echo '-p/--probeeffect is a required argument'
    exit 2
fi

########
# TRAP #
########

function onExit() {
    echo
    if [ -n "${logcat_pid_on_device+defined}" ]; then
        adb -d shell kill "$logcat_pid_on_device"
        adb -d pull "/sdcard/Documents/nym_android_port_logs/fragment_$log_output_file_name" "fragment_$log_output_file_name"
        cat "fragment_$log_output_file_name" >>"$main_log_output_file_path"
        rm "fragment_$log_output_file_name"
    fi

    # Rare use case, not handled.
    echo 'WARN: Script was terminated manually; need to manually go to the Android device to stop the Nym client through the UI.'
    #    if [ -n "${nym_client_pid+defined}" ]; then
    #        echo 'Stopping nym_client...'
    #        kill -INT "$nym_client_pid" # SIGINT the nym_client
    #        wait "$nym_client_pid"
    #        echo 'nym_client stopped.'
    #    fi
    exit 0
}

trap onExit INT TERM

########
# MAIN #
########

mkdir -p app/src/main/jniLibs/arm64-v8a
#mkdir -p app/src/main/jniLibs/x86_64

new_client_id=$(date +'%y%m%d%H%M')

if [ -z ${max_messages+defined} ]; then
    max_messages="ULong.MAX_VALUE"
else
    max_messages="${max_messages}UL"
fi
cat <<EOF >app/src/main/java/com/kaeonx/nymandroidport/autogenerated/ExperimentParameters.kt
package com.kaeonx.nymandroidport.autogenerated

// Auto-generated from a script, direct updates may be overwritten during build process

internal const val MAX_MESSAGES = $max_messages
internal const val NEW_CLIENT_ID = "$new_client_id"
EOF

# Bash booleans caveats: https://stackoverflow.com/a/21210966
log_output_file_name="${new_client_id}_android_${variant}_${connectivity}_$battery_restriction"
if [ "$probeeffect" == "true" ]; then
    log_output_file_name="${log_output_file_name}_probeeffect.txt"
else
    log_output_file_name="$log_output_file_name.txt"
fi
mkdir -p data_collection
main_log_output_file_path="$(pwd)/data_collection/$log_output_file_name"

echo
echo "*** Saving output to $main_log_output_file_path ***"
echo

cd ../nym || exit 3

if [ "$probeeffect" == "true" ]; then
    current_branch_check=$(git describe --tags 2>/dev/null || echo NA) # returns NA if no tags exist in repo; some other tag that's not probe-effect-evaluation (probe-effect-evalation is only returned if on the exact commit)
    if [ "$current_branch_check" != "probe-effect-evaluation" ]; then
        git checkout -q tags/probe-effect-evaluation || (echo 'Failed to ensure "nym" is on tag "probe-effect-evaluation"'; exit 8)
    fi
    echo "'nym' is on tag 'tag/$(git describe --tags 2>/dev/null || echo NA)'"
else
    current_branch_check=$(git branch --show-current) # if still on tag/probe-effect-evaluation (detached HEAD), this returns empty
    if [ "$current_branch_check" != "nym-binaries-v1.1.4-logging-dev" ]; then
        git checkout -q nym-binaries-v1.1.4-logging-dev || (echo 'Failed to ensure "nym" is on branch "nym-binaries-v1.1.4-logging-dev"'; exit 8)
    fi
    echo "'nym' is on branch '$(git branch --show-current)'"
fi

cd ../nym-jni || exit 3

if [ "$probeeffect" == "true" ]; then
    current_branch_check=$(git branch --show-current)
    if [ "$current_branch_check" != "probe-effect-evaluation" ]; then
        git checkout -q probe-effect-evaluation || (echo 'Failed to ensure "nym-jni" is on branch "probe-effect-evaluation"'; exit 8)
    fi
    echo "'nym-jni' is on branch $(git branch --show-current)"
else
    current_branch_check=$(git branch --show-current)
    if [ "$current_branch_check" != "main" ]; then
        git checkout -q main || (echo 'Failed to ensure "nym-jni" is on branch "main"'; exit 8)
    fi
    echo "'nym-jni' is on branch $(git branch --show-current)"
fi

case "$variant" in
'debug')
    echo 'Compiling nym-jni (debug)...'
    cargo build --target aarch64-linux-android >>"$main_log_output_file_path" 2>&1 || exit 4
    cp -u target/aarch64-linux-android/debug/libnym_jni.so ../nym-android-port/app/src/main/jniLibs/arm64-v8a || exit 5
    ;;
'release')
    echo 'Compiling nym-jni (release)...'
    cargo build --target aarch64-linux-android --release >>"$main_log_output_file_path" 2>&1 || exit 4
    cp -u target/aarch64-linux-android/release/libnym_jni.so ../nym-android-port/app/src/main/jniLibs/arm64-v8a || exit 5
    ;;
esac

cd ../nym-android-port || exit 3

if [ "$probeeffect" == "true" ]; then
    current_branch_check=$(git branch --show-current)
    if [ "$current_branch_check" != "probe-effect-evaluation" ]; then
        git checkout -q probe-effect-evaluation || (echo 'Failed to ensure "nym-android-port" is on branch "probe-effect-evaluation"'; exit 8)
    fi
    echo "'nym-android-port' is on branch '$(git branch --show-current)'"
else
    current_branch_check=$(git branch --show-current)
    if [ "$current_branch_check" != "main" ]; then
        git checkout -q main || (echo 'Failed to ensure "nym-android-port" is on branch "main"'; exit 8)
    fi
    echo "'nym-android-port' is on branch '$(git branch --show-current)'"
fi

case "$variant" in
'debug')
    echo 'Building nym-android-port (debug)...'
    ./gradlew -q assembleDebug || exit 6
    # to circumvent incompatible signature when doing multiple re-installs during a series of
    # experiments, we uninstall everytime
    adb -d uninstall com.kaeonx.nymandroidport || echo 'Failed to delete com.kaeonx.nymandroidport (app not installed?)'
    adb -d install app/build/outputs/apk/debug/app-debug.apk || exit 7
    ;;
'release')
    echo 'Building nym-android-port (release)...'
    ./gradlew -q assembleRelease || exit 6
    # to circumvent incompatible signature when doing multiple re-installs during a series of
    # experiments, we uninstall everytime
    adb -d uninstall com.kaeonx.nymandroidport || echo 'Failed to delete com.kaeonx.nymandroidport (app not installed?)'
    adb -d install app/build/outputs/apk/release/app-release.apk || exit 7
    ;;
esac
adb -d shell pm grant com.kaeonx.nymandroidport android.permission.POST_NOTIFICATIONS

# Commands for controlling mobile data / WiFi from:
# - https://stackoverflow.com/a/23556400
# - https://stackoverflow.com/a/13652723
case "$connectivity" in
'data')
    adb -d shell svc wifi disable
    adb -d shell svc data enable
    ;;
'wifi')
    adb -d shell svc data disable
    adb -d shell svc wifi enable
    ;;
esac

# Commands for setting Unrestricted/Optimised/Restricted from:
# - https://developer.android.com/topic/performance/background-optimization#further-optimization
# - https://source.android.com/docs/core/power/app_mgmt#testing-app-restrictions
case "$battery_restriction" in
'unrestricted')
    adb -d shell cmd appops set com.kaeonx.nymandroidport RUN_ANY_IN_BACKGROUND allow
    adb -d shell cmd deviceidle whitelist +com.kaeonx.nymandroidport
    ;;
'optimised')
    adb -d shell cmd appops set com.kaeonx.nymandroidport RUN_ANY_IN_BACKGROUND allow
    adb -d shell cmd deviceidle whitelist -com.kaeonx.nymandroidport
    ;;
'restricted')
    adb -d shell cmd appops set com.kaeonx.nymandroidport RUN_ANY_IN_BACKGROUND ignore
    adb -d shell cmd deviceidle whitelist -com.kaeonx.nymandroidport
    ;;
esac

echo "Waiting for 10s for connectivity ($connectivity) to establish itself"
sleep 10

# Courtesy of https://stackoverflow.com/a/59556001
adb -d shell 'echo 1 > /data/local/tmp/nymRunEvaluationRunning.txt'
adb -d shell run-as com.kaeonx.nymandroidport mkdir -p files/adbSync
adb -d shell run-as com.kaeonx.nymandroidport cp /data/local/tmp/nymRunEvaluationRunning.txt files/adbSync/nymRunEvaluationRunning.txt

adb -d shell logcat -c
logcat_pid_on_device=$(adb -d shell "nohup logcat -f /sdcard/Documents/nym_android_port_logs/fragment_$log_output_file_name -r 131072 -n 1 nym_jni_log:I nym_jni_tracing:I *:I >/dev/null 2>&1 & echo \$!")
adb -d shell am start-foreground-service -n com.kaeonx.nymandroidport/.services.ADBForegroundService

while true; do
    sleep 30
    adb -d shell run-as com.kaeonx.nymandroidport cp files/adbSync/nymRunEvaluationRunning.txt /data/local/tmp/nymRunEvaluationRunning.txt
    nymRunEvaluationRunning=$(adb -d shell cat /data/local/tmp/nymRunEvaluationRunning.txt)
    if [ "$nymRunEvaluationRunning" == '0' ]; then
        # The NymRunForegroundService already terminated by this point
        adb -d shell kill "$logcat_pid_on_device"
        adb -d pull "/sdcard/Documents/nym_android_port_logs/fragment_$log_output_file_name" "fragment_$log_output_file_name"
        cat "fragment_$log_output_file_name" >>"$main_log_output_file_path"
        rm "fragment_$log_output_file_name"
        break
    fi
done
