#!/bin/bash

# Did consider having something like this on Android, but the nohup process gets killed when the app is killed.
# I.e. the nohup process's lifecycle is tied to that of the Android app. There is no known way to do this automatically.

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
    echo '                            --probeeffect BOOL [--max-messages N]'
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
    echo 'TODO: SEND SOME SIGNAL TO ANDROID TO STOP THE PROCESS.'
    #    if [ -n "${nym_client_pid+defined}" ]; then
    #        echo 'Stopping nym_client...'
    #        kill -INT "$nym_client_pid" # SIGINT the nym_client
    #        wait "$nym_client_pid"
    #        echo 'nym_client stopped.'
    #    fi
    if [ "$experiment_started" == 'true' ]; then
        echo 'EXPERIMENT_ENDED' >>"$log_output_file_name"
    fi
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
cat <<EOF > app/src/main/java/com/kaeonx/nymandroidport/autogenerated/ExperimentParameters.kt
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
main_log_output_file_path="$(pwd)/$log_output_file_name"

cd ../nym || exit 3

if [ "$probeeffect" == "true" ]; then
    git checkout tags/probe-effect-evaluation
    current_branch_check=$(git describe --tags 2>/dev/null || echo NA) # returns NA if no tags exist in repo; some other tag that's not probe-effect-evaluation (probe-effect-evalation is only returned if on the exact commit)
    if [ "$current_branch_check" == "probe-effect-evaluation" ]; then
        echo "'nym' is on tag 'tag/$current_branch_check'"
    else
        echo "Failed to ensure 'nym-pc' is on branch 'probe-effect-evaluation'; stuck on branch '$current_branch_check'"
        exit 4
    fi
else
    git checkout nym-binaries-v1.1.4-logging-dev
    current_branch_check=$(git branch --show-current) # if still on tag/probe-effect-evaluation (detached HEAD), this returns empty
    if [ "$current_branch_check" == "nym-binaries-v1.1.4-logging-dev" ]; then
        echo "'nym' is on branch '$current_branch_check'"
    else
        echo "Failed to ensure 'nym' is on branch 'nym-binaries-v1.1.4-logging-dev'; stuck on branch '$current_branch_check'"
        exit 4
    fi
fi

experiment_started='true'
echo
echo "*** Saving output to $main_log_output_file_path ***"
echo
echo 'EXPERIMENT_STARTED' >>"$main_log_output_file_path"

cd ../nym-jni || exit 3

if [ "$probeeffect" == "true" ]; then
    git checkout probe-effect-evaluation
    current_branch_check=$(git branch --show-current)
    if [ "$current_branch_check" == "probe-effect-evaluation" ]; then
        echo "'nym-jni' is on branch '$current_branch_check'"
    else
        echo "Failed to ensure 'nym-jni' is on branch 'probe-effect-evaluation'; stuck on branch '$current_branch_check'"
    fi
else
    git checkout main
    current_branch_check=$(git branch --show-current)
    if [ "$current_branch_check" == "main" ]; then
        echo "'nym-jni' is on branch '$current_branch_check'"
    else
        echo "Failed to ensure 'nym-jni' is on branch 'main'; stuck on branch '$current_branch_check'"
    fi
fi

case "$variant" in
'debug')
    echo 'Compiling nym-jni (debug)...'
    cargo build --color always --target aarch64-linux-android >>"$main_log_output_file_path" 2>&1 || exit 5
    cp -u target/aarch64-linux-android/debug/libnym_jni.so ../nym-android-port/app/src/main/jniLibs/arm64-v8a || exit 6
    ;;
'release')
    echo 'Compiling nym-jni (release)...'
    cargo build --color always --target aarch64-linux-android --release >>"$main_log_output_file_path" 2>&1 || exit 5
    cp -u target/aarch64-linux-android/release/libnym_jni.so ../nym-android-port/app/src/main/jniLibs/arm64-v8a || exit 6
    ;;
esac

cd ../nym-android-port || exit 3

if [ "$probeeffect" == "true" ]; then
    git checkout probe-effect-evaluation
    current_branch_check=$(git branch --show-current)
    if [ "$current_branch_check" == "probe-effect-evaluation" ]; then
        echo "'nym-android-port' is on branch '$current_branch_check'"
    else
        echo "Failed to ensure 'nym-android-port' is on branch 'probe-effect-evaluation'; stuck on branch '$current_branch_check'"
    fi
else
    git checkout main
    current_branch_check=$(git branch --show-current)
    if [ "$current_branch_check" == "main" ]; then
        echo "'nym-android-port' is on branch '$current_branch_check'"
    else
        echo "Failed to ensure 'nym-android-port' is on branch 'main'; stuck on branch '$current_branch_check'"
    fi
fi

case "$variant" in
'debug')
    ./gradlew assembleDebug || exit 7
    adb -d install app/build/outputs/apk/debug/app-debug.apk || exit 8
    ;;
'release')
    ./gradlew assembleRelease || exit 7
    adb -d install app/build/outputs/apk/release/app-release.apk || exit 8
    ;;
esac
adb -d shell pm grant com.kaeonx.nymandroidport android.permission.POST_NOTIFICATIONS

# Commands for controlling mobile data / WiFi from:
# - https://stackoverflow.com/a/23556400
# - https://stackoverflow.com/a/13652723
case "$connectivity" in
'data')
    adb shell svc wifi disable
    adb shell svc data enable
    ;;
'wifi')
    adb shell svc data disable
    adb shell svc wifi enable
    ;;
esac

echo "Waiting for 10s for connectivity ($connectivity) to establish itself"
sleep 10

# Commands for setting Unrestricted/Optimised/Restricted from:
# - https://developer.android.com/topic/performance/background-optimization#further-optimization
# - https://source.android.com/docs/core/power/app_mgmt#testing-app-restrictions
case "$battery_restriction" in
'unrestricted')
    adb shell cmd appops set com.kaeonx.nymandroidport RUN_ANY_IN_BACKGROUND allow
    adb shell cmd deviceidle whitelist +com.kaeonx.nymandroidport
    ;;
'optimised')
    adb shell cmd appops set com.kaeonx.nymandroidport RUN_ANY_IN_BACKGROUND allow
    adb shell cmd deviceidle whitelist -com.kaeonx.nymandroidport
    ;;
'restricted')
    adb shell cmd appops set com.kaeonx.nymandroidport RUN_ANY_IN_BACKGROUND ignore
    adb shell cmd deviceidle whitelist -com.kaeonx.nymandroidport
    ;;
esac

# Courtesy of https://stackoverflow.com/a/59556001
adb -d shell 'echo 1 > /data/local/tmp/nymRunEvaluationRunning.txt'
adb -d shell run-as com.kaeonx.nymandroidport mkdir -p files/adbSync
adb -d shell run-as com.kaeonx.nymandroidport cp /data/local/tmp/nymRunEvaluationRunning.txt files/adbSync/nymRunEvaluationRunning.txt

logcat_pid_on_device=$(adb -d shell "logcat -f /sdcard/Documents/nym_android_port_logs/fragment_$log_output_file_name -r 262144 -n 2048 nym_jni_log:D nym_jni_tracing:D *:V >/dev/null 2>&1 & echo \$!")
adb -d shell am start-foreground-service -n com.kaeonx.nymandroidport/.services.ADBForegroundService

while true; do
    sleep 10
    echo 'DEBUG: checking'
    adb -d shell run-as com.kaeonx.nymandroidport cp files/adbSync/nymRunEvaluationRunning.txt /data/local/tmp/nymRunEvaluationRunning.txt
    nymRunEvaluationRunning=$(adb -d shell cat /data/local/tmp/nymRunEvaluationRunning.txt)
    if [ "$nymRunEvaluationRunning" == '0' ]; then
        # The NymRunForegroundService already terminated by this point
        adb -d shell kill "$logcat_pid_on_device"
        adb -d pull "/sdcard/Documents/nym_android_port_logs/fragment_$log_output_file_name" "fragment_$log_output_file_name"
        cat "fragment_$log_output_file_name" >> "$main_log_output_file_path"
        rm "fragment_$log_output_file_name"
        break
    fi
done

onExit
