#!/bin/bash

export RUST_BACKTRACE=1
SCRIPT_NAME='collect_android_data_phy_adbusb.sh'

############
# ARGPARSE #
############

echo "Invoked:"
invoked=$(echo "$0" "$@")
echo "$invoked"

TEMP=$(getopt -o 'a:d:v:p:c:b:s:m:h' --long 'abi:,device-name:,variant:,probeeffect:,connectivity:,battery-restriction:,power-save-mode:,average-packet-delay-ms:,average-ack-delay-ms:,loop-cover-traffic-average-delay-ms:,message-sending-average-delay-ms:,max-messages:,help' -n "$SCRIPT_NAME" -s 'bash' -- "$@")

if [ $? -ne 0 ]; then
    echo 'Try '"'--help'"' for more information.' >&2
    exit 1
fi

function help() {
    echo 'Usage:'
    echo 'collect_android_data_phy_adbusb.sh -a arm64-v8a|armeabi-v7a'
    echo '                                   -d DEVICE_NAME'
    echo '                                   -v debug|release'
    echo '                                   -c data|wifi'
    echo '                                   -b unrestricted|optimised|restricted'
    echo '                                   -s true|false'
    echo '                                   -p true|false'
    echo '                                   --average-packet-delay-ms N'
    echo '                                   --average-ack-delay-ms N'
    echo '                                   --loop-cover-traffic-average-delay-ms N'
    echo '                                   --message-sending-average-delay-ms N'
    echo '                                   [-m N]'
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
    echo '--average-packet-delay-ms N'
    echo '        (required) the average packet delay to be inserted into the Nym config file.'
    echo '        (units: ms) Nym'"'"'s default value is 50.'
    echo '--average-ack-delay-ms N'
    echo '        (required) the average ack delay to be inserted into the Nym config file.'
    echo '        (units: ms) Nym'"'"'s default value is 50.'
    echo '--loop-cover-traffic-average-delay-ms N'
    echo '        (required) the loop cover traffic average delay to be inserted into the Nym'
    echo '        config file. (units: ms) Nym'"'"'s default value is 200.'
    echo '--message-sending-average-delay-ms N'
    echo '        (required) the message sending average delay to be inserted into the Nym config'
    echo '        file. (units: ms) Nym'"'"'s default value is 20.'
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
    '--average-packet-delay-ms')
        case "$2" in
        # https://www.gnu.org/software/bash/manual/html_node/Pattern-Matching.html
        *[0-9])
            average_packet_delay=$2
            ;;
        *)
            echo 'Invalid argument passed to --average-packet-delay-ms. Expected a positive integer.'
            exit 2
            ;;
        esac
        shift 2
        continue
        ;;
    '--average-ack-delay-ms')
        case "$2" in
        # https://www.gnu.org/software/bash/manual/html_node/Pattern-Matching.html
        *[0-9])
            average_ack_delay=$2
            ;;
        *)
            echo 'Invalid argument passed to --average-ack-delay-ms. Expected a positive integer.'
            exit 2
            ;;
        esac
        shift 2
        continue
        ;;
    '--loop-cover-traffic-average-delay-ms')
        case "$2" in
        # https://www.gnu.org/software/bash/manual/html_node/Pattern-Matching.html
        *[0-9])
            loop_cover_traffic_average_delay=$2
            ;;
        *)
            echo 'Invalid argument passed to --loop-cover-traffic-average-delay-ms. Expected a positive integer.'
            exit 2
            ;;
        esac
        shift 2
        continue
        ;;
    '--message-sending-average-delay-ms')
        case "$2" in
        # https://www.gnu.org/software/bash/manual/html_node/Pattern-Matching.html
        *[0-9])
            message_sending_average_delay=$2
            ;;
        *)
            echo 'Invalid argument passed to --message-sending-average-delay-ms. Expected a positive integer.'
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
if [ -z ${average_packet_delay+defined} ]; then
    echo '--average-packet-delay-ms is a required argument (expect a positive integer, in ms; default is 50)'
    exit 2
fi
if [ -z ${average_ack_delay+defined} ]; then
    echo '--average-ack-delay-ms is a required argument (expect a positive integer, in ms; default is 50)'
    exit 2
fi
if [ -z ${loop_cover_traffic_average_delay+defined} ]; then
    echo '--loop-cover-traffic-average-delay-ms is a required argument (expect a positive integer, in ms; default is 200)'
    exit 2
fi
if [ -z ${message_sending_average_delay+defined} ]; then
    echo '--message-sending-average-delay-ms is a required argument (expect a positive integer, in ms; default is 20)'
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

    adb -d shell settings put global low_power 0
    adb -d shell dumpsys battery reset  # Reset Power Save Mode (PSM) to default
    exit 0
}

trap onExit INT TERM

########
# MAIN #
########

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
log_output_file_name="${new_client_id}_android_${device_name}_${variant}_${connectivity}_${battery_restriction}"
if [ "$power_save_mode" == 'true' ]; then
    log_output_file_name="${log_output_file_name}_powersavemode"
fi
if [ "$probeeffect" == 'true' ]; then
    log_output_file_name="${log_output_file_name}_probeeffect"
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

# Set average_packet_delay, average_ack_delay, loop_cover_traffic_average_delay, message_sending_average_delay
# for the client used during evaluation
cat <<EOF >clients/client-core/src/config/autogenerated.rs
// Auto-generated from a script, direct updates may be overwritten during build process

use std::time::Duration;

pub(crate) const THOUSAND_OVER_LAMBDA_L: Duration = Duration::from_millis($loop_cover_traffic_average_delay); // LAMBDA_L is in seconds
pub(crate) const THOUSAND_OVER_LAMBDA_P: Duration = Duration::from_millis($message_sending_average_delay); // LAMBDA_P is in seconds
pub(crate) const THOUSAND_OVER_MU: Duration = Duration::from_millis($average_packet_delay); // MU is in seconds
pub(crate) const THOUSAND_OVER_MU_ACK: Duration = Duration::from_millis($average_ack_delay); // MU_ACK is in seconds
EOF

{
    echo "[EXPERIMENT] invoked='$invoked'"
    echo "[EXPERIMENT] abi=$abi";
    echo '[EXPERIMENT] device=android';
    echo "[EXPERIMENT] device_name=$device_name";
    echo "[EXPERIMENT] adb_ip_port=$adb_ip_port";
    echo "[EXPERIMENT] variant=$variant";
    echo "[EXPERIMENT] connectivity=$connectivity";
    echo "[EXPERIMENT] battery_restriction=$battery_restriction";
    echo "[EXPERIMENT] power_save_mode=$power_save_mode";
    echo "[EXPERIMENT] probeeffect=$probeeffect";
    echo "[EXPERIMENT] average_packet_delay=$average_packet_delay";
    echo "[EXPERIMENT] average_ack_delay=$average_ack_delay";
    echo "[EXPERIMENT] loop_cover_traffic_average_delay=$loop_cover_traffic_average_delay";
    echo "[EXPERIMENT] message_sending_average_delay=$message_sending_average_delay";
} >>"$main_log_output_file_path"

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
adb -d shell pm grant com.kaeonx.nymandroidport android.permission.ACCESS_COARSE_LOCATION
adb -d shell pm grant com.kaeonx.nymandroidport android.permission.ACCESS_FINE_LOCATION

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
# WARN: DEVICE SPECIFIC. Need to experiment and figure out what works for your device.
# For Moto, changes effected via ADB are not shown in the Battery Optimisation settings UI.
# Need to close and re-open the App Info. (took so long to figure this out; undocumented...)
# The following works for both device_name in {pixel, moto}.
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

# Commands for setting Power Save Mode (PSM) (battery saver) from:
# https://developer.android.com/topic/performance/power/test-power#adb-battery
if [ "$power_save_mode" == 'true' ]; then
    adb -d shell settings put global low_power 1
else
    adb -d shell settings put global low_power 0
fi

echo "Waiting for 10s for connectivity ($connectivity) to establish itself"
sleep 10

# Courtesy of https://stackoverflow.com/a/59556001
adb -d shell 'echo 1 > /data/local/tmp/nymRunEvaluationRunning.txt'
adb -d shell 'echo 0 > /data/local/tmp/nymRunEvaluationMessagesReceived.txt'
adb -d shell run-as com.kaeonx.nymandroidport mkdir -p files/adbSync
adb -d shell run-as com.kaeonx.nymandroidport cp /data/local/tmp/nymRunEvaluationRunning.txt files/adbSync/nymRunEvaluationRunning.txt
adb -d shell run-as com.kaeonx.nymandroidport cp /data/local/tmp/nymRunEvaluationMessagesReceived.txt files/adbSync/nymRunEvaluationMessagesReceived.txt

adb -d shell logcat -c
adb -d shell mkdir -p /sdcard/Documents/nym_android_port_logs
logcat_pid_on_device=$(adb -d shell "nohup logcat -f /sdcard/Documents/nym_android_port_logs/fragment_$log_output_file_name -r 131072 -n 1 nym_jni_log:I nym_jni_tracing:I *:I >/dev/null 2>&1 & echo \$!")
adb -d shell am start-foreground-service -n com.kaeonx.nymandroidport/.services.ADBForegroundService

messages_check_once_every_minutes=6
echo "Printing received messages count every $messages_check_once_every_minutes min"
minutes_slept=0
messages_received_print_count=0
while true; do
    sleep 60
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
    minutes_slept=$((minutes_slept + 1))
    if [ "$((minutes_slept % messages_check_once_every_minutes))" == '0' ]; then # every 6 minutes, check number of messages received. Expect to receive 360 messages every 5min. Every 100 messages received, Android writes to disk.
        # Potentially expensive operation, trying to minimise (once every 6 min only)
        adb -d shell run-as com.kaeonx.nymandroidport cp files/adbSync/nymRunEvaluationMessagesReceived.txt /data/local/tmp/nymRunEvaluationMessagesReceived.txt
        nymRunEvaluationMessagesReceived=$(adb -d shell cat /data/local/tmp/nymRunEvaluationMessagesReceived.txt)
        echo -n "#$nymRunEvaluationMessagesReceived · " # (*)
        messages_received_print_count=$((messages_received_print_count+1))
        if [ "$((messages_received_print_count % 10))" == '0' ]; then
            echo
        fi
    fi
done
echo # see (*)
