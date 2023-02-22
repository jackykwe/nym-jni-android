#!/bin/bash

# Did consider having something like this on Android, but the nohup process gets killed when the app is killed.
# I.e. the nohup process's lifecycle is tied to that of the Android app. There is no known way to do this automatically.

debug=true
probeeffect=true

new_client_id=$(date +'%y%m%d%H%M')

# Bash booleans caveats: https://stackoverflow.com/a/21210966
if [ "$debug" = true ]
then
    log_output_file_name=$new_client_id'_pc_debug'
else
    log_output_file_name=$new_client_id'_pc_release'
fi
if [ "$probeeffect" = true ]
then
    log_output_file_name=$log_output_file_name'_probeeffect.txt'
else
    log_output_file_name=$log_output_file_name'.txt'
fi

cd ../nym

if [ "$debug" = true ]
then
    RUST_LOG=INFO cargo run --bin nym-client init --id $new_client_id >> $log_output_file_name 2>&1
    RUST_LOG=INFO cargo run --bin nym-client run --id $new_client_id >> $log_output_file_name 2>&1 &
    echo "Running nym-client (debug) in PID $!"
else
    RUST_LOG=INFO cargo run --release --bin nym-client init --id $new_client_id >> $log_output_file_name 2>&1
    RUST_LOG=INFO cargo run --release --bin nym-client run --id $new_client_id >> $log_output_file_name 2>&1 &
    echo "Running nym-client (release) in PID $!"
fi

echo Sleeping for 10s to allow nym-client to startup
sleep 10
echo Saving logs to $log_output_file_name

cd ../nym-pc

if [ "$debug" = true ]
then
    RUST_LOG=INFO cargo run >> $log_output_file_name 2>&1 &
    echo "Running nym-pc (debug) in PID $!"
else
    RUST_LOG=INFO cargo run --release >> $log_output_file_name 2>&1 &
    echo "Running nym-pc (release) in PID $!"
fi

