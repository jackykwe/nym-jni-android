#!/bin/bash

# Did consider having something like this on Android, but the nohup process gets killed when the app is killed.
# I.e. the nohup process's lifecycle is tied to that of the Android app. There is no known way to do this automatically.

export RUST_BACKTRACE=1
SCRIPT_NAME='collect_pc_data.sh'

############
# ARGPARSE #
############

TEMP=$(getopt -o 'd:p:m:h' --long 'debug:,probeeffect:,max-messages:,help' -n "$SCRIPT_NAME" -s 'bash' -- "$@")

if [ $? -ne 0 ]; then
    echo 'Try '"'--help'"' for more information.' >&2
    exit 1
fi

function help() {
    echo 'Usage:'
    echo 'collect_pc_data.sh --debug BOOL --probeeffect BOOL'
    echo
    echo 'Utility to create a new Nym Client, run it, and collect timestamps from it.'
    echo 'The timestamps are saved into a new text file in the current working '
    echo 'directory. The name of the file will be printed on execution of this script.'
    echo
    echo 'This utility may trigger recompilation of the underlying Rust crates '"'nym'"
    echo 'and '"'nym-pc'"'. '
    echo
    echo 'Where '"'BOOL'"' is specified, either '"'true'"' or '"'false'"' must be provided.'
    echo
    echo 'Options:'
    echo '-d, --debug BOOL        whether to run the debug or release builds of the'
    echo '                        underlying Rust crates '"'nym'"' and '"'nym-pc'"'. This'
    echo '                        collects all timestamps from tK=1 to tK=8 inclusive.'
    echo '-h, --help              show this help message and exit'
    echo '-m, --max-messages N    stop the evaluation after exactly N messages are sent'
    echo '                        through the nym network.'
    echo '-p, --probeeffect BOOL  whether to run the builds of the underlying Rust'
    echo '                        crates '"'nym'"' and '"'nym-pc'"', while collecting only'
    echo '                        timestamps tK=1 and tK=8.'
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
    '-d' | '--debug')
        case "$2" in
        'true' | 'false')
            debug=$2
            ;;
        *)
            echo 'Invalid argument passed to option -d/--debug. Expected either '"'true'"' or '"'false'"'; got '"'$2'"
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
if [ -z ${debug+defined} ]; then
    echo '-d/--debug is a required argument'
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
    if [ "$experiment_started" == 'true' ]; then
        echo 'EXPERIMENT_ENDED' >>"$log_output_file_name"
    fi
    exit 0
}

trap onExit INT TERM

########
# MAIN #
########

new_client_id=$(date +'%y%m%d%H%M')

# Bash booleans caveats: https://stackoverflow.com/a/21210966
if [ "$debug" == "true" ]; then
    log_output_file_name="$(pwd)/${new_client_id}_pc_debug"
else
    log_output_file_name="$(pwd)/${new_client_id}_pc_release"
fi
if [ "$probeeffect" == "true" ]; then
    log_output_file_name="${log_output_file_name}_probeeffect.txt"
else
    log_output_file_name="$log_output_file_name.txt"
fi

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
echo "*** Saving output to $log_output_file_name ***"
echo
echo 'EXPERIMENT_STARTED' >>"$log_output_file_name"

if [ "$debug" == "true" ]; then
    echo 'Compiling and starting nym-client (debug)...'
    RUST_LOG=INFO cargo run --bin nym-client init --id "$new_client_id" >>"$log_output_file_name" 2>&1
    RUST_LOG=INFO cargo run --bin nym-client run --id "$new_client_id" >>"$log_output_file_name" 2>&1 &
    nym_client_pid=$!
    echo "Running nym-client (debug) in PID $nym_client_pid"
else
    echo 'Compiling and starting nym-client (release)...'
    RUST_LOG=INFO cargo run --release --bin nym-client init --id "$new_client_id" >>"$log_output_file_name" 2>&1
    RUST_LOG=INFO cargo run --release --bin nym-client run --id "$new_client_id" >>"$log_output_file_name" 2>&1 &
    nym_client_pid=$!
    echo "Running nym-client (release) in PID $nym_client_pid"
fi

cd ../nym-pc || exit 3

if [ "$probeeffect" == "true" ]; then
    git checkout probe-effect-evaluation
    current_branch_check=$(git branch --show-current)
    if [ "$current_branch_check" == "probe-effect-evaluation" ]; then
        echo "'nym-pc' is on branch '$current_branch_check'"
    else
        echo "Failed to ensure 'nym-pc' is on branch 'probe-effect-evaluation'; stuck on branch '$current_branch_check'"
    fi
else
    git checkout main
    current_branch_check=$(git branch --show-current)
    if [ "$current_branch_check" == "main" ]; then
        echo "'nym-pc' is on branch '$current_branch_check'"
    else
        echo "Failed to ensure 'nym-pc' is on branch 'main'; stuck on branch '$current_branch_check'"
    fi
fi

backoff_seconds=1
while true; do
    if [ "$debug" = "true" ]; then
        echo 'Compiling and starting nym-pc (debug) ... '
        # [VS Code] Ignore shell-check: $max_messages should not be quoted, as it inserts single quotations for some reason
        RUST_LOG=INFO cargo run -- $max_messages >>"$log_output_file_name" 2>&1
        result=$?
    else
        echo 'Compiling and starting nym-pc (release)...'
        # [VS Code] Ignore shell-check: $max_messages should not be quoted, as it inserts single quotations for some reason
        RUST_LOG=INFO cargo run --release -- $max_messages >>"$log_output_file_name" 2>&1
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
