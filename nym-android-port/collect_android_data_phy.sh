#!/bin/bash

export RUST_BACKTRACE=1
SCRIPT_NAME='collect_android_data_phy.sh'

############
# ARGPARSE #
############

echo "Invoked:"
echo "$0" "$@"

TEMP=$(getopt -o 'a:d:i:v:p:c:b:s:m:h' --long 'abi:,device-name:,ip-and-port:,variant:,probeeffect:,connectivity:,battery-restriction:,power-save-mode:,max-messages:,help' -n "$SCRIPT_NAME" -s 'bash' -- "$@")

if [ $? -ne 0 ]; then
    echo 'Try '"'--help'"' for more information.' >&2
    exit 1
fi

function help() {
    echo 'Usage:'
    echo 'collect_android_data_phy.sh -a arm64-v8a|armeabi-v7a'
    echo '                            -d DEVICE_NAME'
    echo '                            -i IP:PORT'
    echo '                            -v debug|release'
    echo '                            -c data|wifi'
    echo '                            -b unrestricted|optimised|restricted'
    echo '                            -s true|false'
    echo '                            -p true|false'
    echo '                            [-m N]'
    echo
    echo 'Utility to create a new Nym Client, run it, and collect timestamps from it.'
    echo 'The timestamps are saved into a new text file in the current working '
    echo 'directory. The name of the file will be printed on execution of this script.'
    echo
    echo 'This utility may trigger recompilation of the underlying Android application and Rust'
    echo 'crates '"'nym'"' and '"'nym-pc'"'.'
    echo
    echo 'Options:'
    echo '-a, --abi arm64-v8a|armeabi-v7a'
    echo '        (required) target architecture of Rust shared library. Must match the connected device.'
    echo '-b, --battery-restriction unrestricted|optimised|restricted'
    echo '        (required) selects the user-defined battery restriction mode of the Android app.'
    echo '-c, --connectivity data|wifi'
    echo '        (required) automatically switches between mobile data or WiFi in preparation for'
    echo '        data collection.'
    echo '-d, --device-name DEVICE_NAME'
    echo '        (required) Device name used in the name of output log files'
    echo '-h, --help'
    echo '        show this help message and exit'
    echo '-i, --ip-and-port IP:PORT'
    echo '        (required) IP and port used to connect to an ADB device'
    echo '-m, --max-messages N'
    echo '        stop the evaluation after exactly N messages are sent through the nym network. If'
    echo '        not specified, the evaluation will not terminate until explictily interrupted.'
    echo '-p, --probeeffect true|false'
    echo '        (required) whether to run the builds of the underlying Rust crates '"'nym'"' and '
    echo '        '"'nym-pc'"', while collecting only timestamps tK=1 and tK=8.'
    echo '-s, --power-save-mode true|false'
    echo '        (required) whether to enable Power Save Mode (PSM) (battery saver) on the device'
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
    '-a' | '--abi')
        case "$2" in
        'arm64-v8a' | 'armeabi-v7a')
            abi=$2
            ;;
        *)
            echo 'Invalid argument passed to option -a/--abi. Expected either '"'arm64-v8a'"' or '"'armeabi-v7a'"'; got '"'$2'"
            exit 2
            ;;
        esac
        shift 2
        continue
        ;;
    '-d' | '--device-name')
        case "$2" in
        '')
            echo 'Invalid argument passed to option -d/--device. Expected a non-empty device name.'
            exit 2
            ;;
        *)
            device_name=$2
            ;;
        esac
        shift 2
        continue
        ;;
    '-i' | '--ip-and-port')
        case "$2" in
        '')
            echo 'Invalid argument passed to option -i/--ip-and-port. Expected IP:PORT (e.g. 192.168.1.1).'
            exit 2
            ;;
        *)
            adb_ip_port=$2
            ;;
        esac
        shift 2
        continue
        ;;
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
    '-s' | '--power-save-mode')
        case "$2" in
        'true' | 'false')
            power_save_mode=$2
            ;;
        *)
            echo 'Invalid argument passed to option -s/--power-save-mode. Expected either '"'true'"' or '"'false'"'; got '"'$2'"
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
if [ -z ${abi+defined} ]; then
    echo '-a/--abi is a required argument (expect '"'arm64-v8a'"' or '"'armeabi-v7a'"')'
    exit 2
fi
if [ -z ${device_name+defined} ]; then
    echo '-d/--device-name is a required argument (expect non-empty string)'
    exit 2
fi
if [ -z ${adb_ip_port+defined} ]; then
    echo '-i/--ip-and-port is a required argument (expect IP:PORT (e.g. 192.168.1.1))'
    exit 2
fi
if [ -z ${variant+defined} ]; then
    echo '-v/--variant is a required argument (expect '"'debug'"' or '"'release'"')'
    exit 2
fi
if [ -z ${connectivity+defined} ]; then
    echo '-c/--connectivity is a required argument (expect '"'data'"' or '"'wifi'"')'
    exit 2
fi
if [ -z ${battery_restriction+defined} ]; then
    echo '-b/--battery-restriction is a required argument (expect '"'unrestricted'"', '"'optimised'"' or '"'restricted'"')'
    exit 2
fi
if [ -z ${probeeffect+defined} ]; then
    echo '-p/--probeeffect is a required argument (expect '"'true'"' or '"'false'"')'
    exit 2
fi
if [ -z ${power_save_mode+defined} ]; then
    echo '-s/--power-save-mode is a required argument (expect '"'true'"' or '"'false'"')'
    exit 2
fi


########
# TRAP #
########

function onExit() {
    echo
    if [ -n "${logcat_pid_on_device+defined}" ]; then
        adb -s "$adb_ip_port" shell kill "$logcat_pid_on_device"
        adb -s "$adb_ip_port" pull "/sdcard/Documents/nym_android_port_logs/fragment_$log_output_file_name" "fragment_$log_output_file_name"
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

    adb -s "$adb_ip_port" shell settings put global low_power 0
    adb -s "$adb_ip_port" shell dumpsys battery reset  # Reset Power Save Mode (PSM) to default
    adb disconnect "$adb_ip_port"
    exit 0
}

trap onExit INT TERM

########
# MAIN #
########

# Establish ADB connection via WiFi
adb_connect_output=$(adb connect "$adb_ip_port")
echo "$adb_connect_output"
if [ "$adb_connect_output" == "already connected to $adb_ip_port" ]; then
    :  # Bash no-op
elif [ "$adb_connect_output" == "connected to $adb_ip_port" ]; then
    :  # Bash no-op
else
    echo "Couldn't establish ADB connection to $adb_ip_port. Exiting..."
    exit 10;
fi

mkdir -p app/src/main/jniLibs/arm64-v8a
mkdir -p app/src/main/jniLibs/armeabi-v7a
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
log_output_file_name="${new_client_id}_android_${abi}_${device_name}_${variant}_${connectivity}_${battery_restriction}"
if [ "$probeeffect" == 'true' ]; then
    log_output_file_name="${log_output_file_name}_probeeffect"
fi
if [ "$power_save_mode" == 'true' ]; then
    log_output_file_name="${log_output_file_name}_powersavemode"
fi
log_output_file_name="$log_output_file_name.txt"

mkdir -p data_collection
main_log_output_file_path="$(pwd)/data_collection/$log_output_file_name"

echo
echo "*** Saving output to $main_log_output_file_path ***"
echo

cd ../nym || exit 3

if [ "$probeeffect" == 'true' ]; then
    current_branch_check=$(git describe --tags 2>/dev/null || echo NA) # returns NA if no tags exist in repo; some other tag that's not probe-effect-evaluation (probe-effect-evalation is only returned if on the exact commit)
    if [ "$current_branch_check" != 'probe-effect-evaluation' ]; then
        git checkout -q tags/probe-effect-evaluation || (
            echo 'Failed to ensure "nym" is on tag "probe-effect-evaluation"'
            exit 8
        )
    fi
    echo "'nym' is on tag 'tag/$(git describe --tags 2>/dev/null || echo NA)'"
else
    current_branch_check=$(git branch --show-current) # if still on tag/probe-effect-evaluation (detached HEAD), this returns empty
    if [ "$current_branch_check" != 'nym-binaries-v1.1.4-logging-dev' ]; then
        git checkout -q nym-binaries-v1.1.4-logging-dev || (
            echo 'Failed to ensure "nym" is on branch "nym-binaries-v1.1.4-logging-dev"'
            exit 8
        )
    fi
    echo "'nym' is on branch '$(git branch --show-current)'"
fi

cd ../nym-jni || exit 3

if [ "$probeeffect" == 'true' ]; then
    current_branch_check=$(git branch --show-current)
    if [ "$current_branch_check" != 'probe-effect-evaluation' ]; then
        git checkout -q probe-effect-evaluation || (
            echo 'Failed to ensure "nym-jni" is on branch "probe-effect-evaluation"'
            exit 8
        )
    fi
    echo "'nym-jni' is on branch $(git branch --show-current)"
else
    current_branch_check=$(git branch --show-current)
    if [ "$current_branch_check" != 'main' ]; then
        git checkout -q main || (
            echo 'Failed to ensure "nym-jni" is on branch "main"'
            exit 8
        )
    fi
    echo "'nym-jni' is on branch $(git branch --show-current)"
fi

case "$variant" in
'debug')
    echo 'Compiling nym-jni (debug)...'
    case "$abi" in
    'armeabi-v7a')
        cargo build --target armv7-linux-androideabi >>"$main_log_output_file_path" 2>&1 || exit 4
        cp -u target/armv7-linux-androideabi/debug/libnym_jni.so ../nym-android-port/app/src/main/jniLibs/armeabi-v7a || exit 5
    ;;
    'arm64-v8a')
        cargo build --target aarch64-linux-android >>"$main_log_output_file_path" 2>&1 || exit 4
        cp -u target/aarch64-linux-android/debug/libnym_jni.so ../nym-android-port/app/src/main/jniLibs/arm64-v8a || exit 5
    ;;
    esac
    ;;
'release')
    echo 'Compiling nym-jni (release)...'
    case "$abi" in
    'armeabi-v7a')
        cargo build --target armv7-linux-androideabi --release >>"$main_log_output_file_path" 2>&1 || exit 4
        cp -u target/armv7-linux-androideabi/release/libnym_jni.so ../nym-android-port/app/src/main/jniLibs/armeabi-v7a || exit 5
    ;;
    'arm64-v8a')
        cargo build --target aarch64-linux-android --release >>"$main_log_output_file_path" 2>&1 || exit 4
        cp -u target/aarch64-linux-android/release/libnym_jni.so ../nym-android-port/app/src/main/jniLibs/arm64-v8a || exit 5
    ;;
    esac
    ;;
esac

cd ../nym-android-port || exit 3

if [ "$probeeffect" == 'true' ]; then
    current_branch_check=$(git branch --show-current)
    if [ "$current_branch_check" != 'probe-effect-evaluation' ]; then
        git checkout -q probe-effect-evaluation || (
            echo 'Failed to ensure "nym-android-port" is on branch "probe-effect-evaluation"'
            exit 8
        )
    fi
    echo "'nym-android-port' is on branch '$(git branch --show-current)'"
else
    current_branch_check=$(git branch --show-current)
    if [ "$current_branch_check" != 'main' ]; then
        git checkout -q main || (
            echo 'Failed to ensure "nym-android-port" is on branch "main"'
            exit 8
        )
    fi
    echo "'nym-android-port' is on branch '$(git branch --show-current)'"
fi

case "$variant" in
'debug')
    echo 'Building nym-android-port (debug)...'
    ./gradlew -q assembleDebug || exit 6
    # to circumvent incompatible signature when doing multiple re-installs during a series of
    # experiments, we uninstall everytime
    adb -s "$adb_ip_port" uninstall com.kaeonx.nymandroidport || echo 'Failed to delete com.kaeonx.nymandroidport (app not installed?)'
    adb -s "$adb_ip_port" install app/build/outputs/apk/debug/app-debug.apk || exit 7
    ;;
'release')
    echo 'Building nym-android-port (release)...'
    ./gradlew -q assembleRelease || exit 6
    # to circumvent incompatible signature when doing multiple re-installs during a series of
    # experiments, we uninstall everytime
    adb -s "$adb_ip_port" uninstall com.kaeonx.nymandroidport || echo 'Failed to delete com.kaeonx.nymandroidport (app not installed?)'
    adb -s "$adb_ip_port" install app/build/outputs/apk/release/app-release.apk || exit 7
    ;;
esac
adb -s "$adb_ip_port" shell pm grant com.kaeonx.nymandroidport android.permission.POST_NOTIFICATIONS
adb -s "$adb_ip_port" shell pm grant com.kaeonx.nymandroidport android.permission.ACCESS_COARSE_LOCATION
adb -s "$adb_ip_port" shell pm grant com.kaeonx.nymandroidport android.permission.ACCESS_FINE_LOCATION

# Commands for controlling mobile data / WiFi from:
# - https://stackoverflow.com/a/23556400
# - https://stackoverflow.com/a/13652723
case "$connectivity" in
'data')
    adb -s "$adb_ip_port" shell svc wifi disable
    adb -s "$adb_ip_port" shell svc data enable
    ;;
'wifi')
    adb -s "$adb_ip_port" shell svc data disable
    adb -s "$adb_ip_port" shell svc wifi enable
    ;;
esac

# Commands for setting Unrestricted/Optimised/Restricted from:
# - https://developer.android.com/topic/performance/background-optimization#further-optimization
# - https://source.android.com/docs/core/power/app_mgmt#testing-app-restrictions
# WARN: DEVICE SPECIFIC. Need to experiment and figure out what works for your device.
case "$device_name" in
'pixel')
    case "$battery_restriction" in
    'unrestricted')
        adb -s "$adb_ip_port" shell cmd appops set com.kaeonx.nymandroidport RUN_ANY_IN_BACKGROUND allow
        adb -s "$adb_ip_port" shell cmd deviceidle whitelist +com.kaeonx.nymandroidport
        ;;
    'optimised')
        adb -s "$adb_ip_port" shell cmd appops set com.kaeonx.nymandroidport RUN_ANY_IN_BACKGROUND deny
        adb -s "$adb_ip_port" shell cmd deviceidle whitelist -com.kaeonx.nymandroidport
        ;;
    'restricted')
        adb -s "$adb_ip_port" shell cmd appops set com.kaeonx.nymandroidport RUN_ANY_IN_BACKGROUND ignore
        adb -s "$adb_ip_port" shell cmd deviceidle whitelist -com.kaeonx.nymandroidport
        ;;
    esac
    ;;
'moto')
    # For Moto, changes effected via ADB are not shown in the Battery Optimisation settings UI.
    # Need to close and re-open the App Info. (took so long to figure this out; undocumented...)
    case "$battery_restriction" in
    'unrestricted')
        adb -s "$adb_ip_port" shell cmd appops set com.kaeonx.nymandroidport RUN_ANY_IN_BACKGROUND allow
        adb -s "$adb_ip_port" shell cmd deviceidle whitelist +com.kaeonx.nymandroidport
        ;;
    'optimised')
        adb -s "$adb_ip_port" shell cmd appops set com.kaeonx.nymandroidport RUN_ANY_IN_BACKGROUND allow  # NB different from Pixel
        adb -s "$adb_ip_port" shell cmd deviceidle whitelist -com.kaeonx.nymandroidport
        ;;
    'restricted')
        adb -s "$adb_ip_port" shell cmd appops set com.kaeonx.nymandroidport RUN_ANY_IN_BACKGROUND ignore
        adb -s "$adb_ip_port" shell cmd deviceidle whitelist -com.kaeonx.nymandroidport
        ;;
    esac
    ;;
esac


# Commands for setting Power Save Mode (PSM) (battery saver) from:
# https://developer.android.com/topic/performance/power/test-power#adb-battery
if [ "$power_save_mode" == 'true' ]; then
    adb -s "$adb_ip_port" shell settings put global low_power 1
else
    adb -s "$adb_ip_port" shell settings put global low_power 0
fi

echo "Waiting for 10s for connectivity ($connectivity) to establish itself"
sleep 10

# Courtesy of https://stackoverflow.com/a/59556001
adb -s "$adb_ip_port" shell 'echo 1 > /data/local/tmp/nymRunEvaluationRunning.txt'
adb -s "$adb_ip_port" shell 'echo 0 > /data/local/tmp/nymRunEvaluationMessagesReceived.txt'
adb -s "$adb_ip_port" shell run-as com.kaeonx.nymandroidport mkdir -p files/adbSync
adb -s "$adb_ip_port" shell run-as com.kaeonx.nymandroidport cp /data/local/tmp/nymRunEvaluationRunning.txt files/adbSync/nymRunEvaluationRunning.txt
adb -s "$adb_ip_port" shell run-as com.kaeonx.nymandroidport cp /data/local/tmp/nymRunEvaluationMessagesReceived.txt files/adbSync/nymRunEvaluationMessagesReceived.txt

adb -s "$adb_ip_port" shell logcat -c
adb -s "$adb_ip_port" shell mkdir -p /sdcard/Documents/nym_android_port_logs
logcat_pid_on_device=$(adb -s "$adb_ip_port" shell "nohup logcat -f /sdcard/Documents/nym_android_port_logs/fragment_$log_output_file_name -r 131072 -n 1 nym_jni_log:I nym_jni_tracing:I *:I >/dev/null 2>&1 & echo \$!")
adb -s "$adb_ip_port" shell am start-foreground-service -n com.kaeonx.nymandroidport/.services.ADBForegroundService

messages_check_once_every_minutes=6
echo "Printing received messages count every $messages_check_once_every_minutes min"
minutes_slept=0
messages_received_print_count=0
while true; do
    sleep 60
    adb -s "$adb_ip_port" shell run-as com.kaeonx.nymandroidport cp files/adbSync/nymRunEvaluationRunning.txt /data/local/tmp/nymRunEvaluationRunning.txt
    nymRunEvaluationRunning=$(adb -s "$adb_ip_port" shell cat /data/local/tmp/nymRunEvaluationRunning.txt)
    if [ "$nymRunEvaluationRunning" == '0' ]; then
        # The NymRunForegroundService already terminated by this point
        adb -s "$adb_ip_port" shell kill "$logcat_pid_on_device"
        adb -s "$adb_ip_port" pull "/sdcard/Documents/nym_android_port_logs/fragment_$log_output_file_name" "fragment_$log_output_file_name"
        cat "fragment_$log_output_file_name" >>"$main_log_output_file_path"
        rm "fragment_$log_output_file_name"
        break
    fi
    minutes_slept=$((minutes_slept + 1))
    if [ "$((minutes_slept % messages_check_once_every_minutes))" == '0' ]; then # every 6 minutes, check number of messages received. Expect to receive 360 messages every 5min. Every 100 messages received, Android writes to disk.
        # Potentially expensive operation, trying to minimise (once every 6 min only)
        adb -s "$adb_ip_port" shell run-as com.kaeonx.nymandroidport cp files/adbSync/nymRunEvaluationMessagesReceived.txt /data/local/tmp/nymRunEvaluationMessagesReceived.txt
        nymRunEvaluationMessagesReceived=$(adb -s "$adb_ip_port" shell cat /data/local/tmp/nymRunEvaluationMessagesReceived.txt)
        echo -n "#$nymRunEvaluationMessagesReceived · " # (*)
        messages_received_print_count=$((messages_received_print_count+1))
        if [ "$((messages_received_print_count % 10))" == '0' ]; then
            echo
        fi
    fi
done
echo # see (*)
