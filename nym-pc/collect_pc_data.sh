#!/bin/bash

export RUST_BACKTRACE=1
SCRIPT_NAME='collect_pc_data.sh'

############
# ARGPARSE #
############

TEMP=$(getopt -o 'v:p:m:h' --long 'variant:,probeeffect:,max-messages:,help' -n "$SCRIPT_NAME" -s 'bash' -- "$@")

if [ $? -ne 0 ]; then
    echo 'Try '"'--help'"' for more information.' >&2
    exit 1
fi

function help() {
    echo 'Usage:'
    echo 'collect_pc_data.sh --variant debug|release --probeeffect true|false [--max-messages N]'
    echo
    echo 'Utility to create a new Nym Client, run it, and collect timestamps from it.'
    echo 'The timestamps are saved into a new text file in the current working '
    echo 'directory. The name of the file will be printed on execution of this script.'
    echo
    echo 'This utility may trigger recompilation of the underlying Rust crates '"'nym'"
    echo 'and '"'nym-pc'"'.'
    echo
    echo 'Options:'
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
            max_messages="--max-messages $2"
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
if [ -z ${probeeffect+defined} ]; then
    echo '-p/--probeeffect is a required argument'
    exit 2
fi

########
# TRAP #
########

function onExit() {
    echo
    if [ -n "${nym_client_pid+defined}" ]; then
        echo 'Stopping nym_client...'
        kill -INT "$nym_client_pid" # SIGINT the nym_client
        wait "$nym_client_pid"
        echo 'nym_client stopped.'
    fi
    if [ -n "${progress_poller_pid+defined}" ]; then
        kill "$progress_poller_pid"
    fi
    exit 0
}

trap onExit INT TERM

########
# MAIN #
########

new_client_id=$(date +'%y%m%d%H%M')

# Bash booleans caveats: https://stackoverflow.com/a/21210966
log_output_file_name="${new_client_id}_pc_$variant"
if [ "$probeeffect" == "true" ]; then
    log_output_file_name="${log_output_file_name}_probeeffect.txt"
else
    log_output_file_name="$log_output_file_name.txt"
fi
main_log_output_file_path="$(pwd)/$log_output_file_name"

cd ../nym || exit 3

if [ "$probeeffect" == "true" ]; then
    current_branch_check=$(git describe --tags 2>/dev/null || echo NA) # returns NA if no tags exist in repo; some other tag that's not probe-effect-evaluation (probe-effect-evalation is only returned if on the exact commit)
    if [ "$current_branch_check" != "probe-effect-evaluation" ]; then
        git checkout -q tags/probe-effect-evaluation
    fi
    echo "'nym' is on tag 'tag/$(git describe --tags 2>/dev/null || echo NA)'"
else
    current_branch_check=$(git branch --show-current) # if still on tag/probe-effect-evaluation (detached HEAD), this returns empty
    if [ "$current_branch_check" != "nym-binaries-v1.1.4-logging-dev" ]; then
        git checkout -q nym-binaries-v1.1.4-logging-dev
    fi
    echo "'nym' is on branch '$(git branch --show-current)'"
fi

echo
echo "*** Saving output to $main_log_output_file_path ***"
echo

if [ "$variant" == "debug" ]; then
    echo 'Compiling and starting nym-client (debug)...'
    RUST_LOG=INFO cargo run --bin nym-client init --id "$new_client_id" >>"$main_log_output_file_path" 2>&1
    RUST_LOG=INFO cargo run --bin nym-client run --id "$new_client_id" >>"$main_log_output_file_path" 2>&1 &
    nym_client_pid=$!
    echo "Running nym-client (debug) in PID $nym_client_pid"
else
    echo 'Compiling and starting nym-client (release)...'
    RUST_LOG=INFO cargo run --release --bin nym-client init --id "$new_client_id" >>"$main_log_output_file_path" 2>&1
    RUST_LOG=INFO cargo run --release --bin nym-client run --id "$new_client_id" >>"$main_log_output_file_path" 2>&1 &
    nym_client_pid=$!
    echo "Running nym-client (release) in PID $nym_client_pid"
fi

cd ../nym-pc || exit 3

if [ "$probeeffect" == "true" ]; then
    current_branch_check=$(git branch --show-current)
    if [ "$current_branch_check" != "probe-effect-evaluation" ]; then
        git checkout -q probe-effect-evaluation
    fi
    echo "'nym-pc' is on branch '$(git branch --show-current)'"
else
    current_branch_check=$(git branch --show-current)
    if [ "$current_branch_check" != "main" ]; then
        git checkout -q main
    fi
    echo "'nym-pc' is on branch '$(git branch --show-current)'"
fi

backoff_seconds=1

echo 0 >nymRunEvaluationMessagesReceived.txt
echo 'Printing received messages count every 6 min'
messages_received_print_count=0
while true; do
    sleep 360
    echo -n "#$(cat nymRunEvaluationMessagesReceived.txt) · "
    messages_received_print_count=$((messages_received_print_count + 1))
    if [ "$((messages_received_print_count % 10))" == '0' ]; then
        echo
    fi
done &
progress_poller_pid=$!

while true; do
    if [ "$variant" = "debug" ]; then
        echo 'Compiling and starting nym-pc (debug) ... '
        # [VS Code] Ignore shell-check: $max_messages should not be quoted, as it inserts single quotations for some reason
        RUST_LOG=INFO cargo run -- $max_messages >>"$main_log_output_file_path" 2>&1
        result=$?
    else
        echo 'Compiling and starting nym-pc (release)...'
        # [VS Code] Ignore shell-check: $max_messages should not be quoted, as it inserts single quotations for some reason
        RUST_LOG=INFO cargo run --release -- $max_messages >>"$main_log_output_file_path" 2>&1
        result=$?
    fi

    if [ "$result" == "42" ]; then
        echo "Could not start nym-pc as nym-client is not yet ready. Backing off for $backoff_seconds second(s)"
        sleep $backoff_seconds
        backoff_seconds=$((backoff_seconds * 2))
    else
        break
    fi
done

onExit
