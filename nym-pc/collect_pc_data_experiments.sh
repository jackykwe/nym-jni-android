#!/bin/bash

# Did consider having something like this on Android, but the nohup process gets killed when the app is killed.
# I.e. the nohup process's lifecycle is tied to that of the Android app. There is no known way to do this automatically.

####################
# BASH BOILERPLATE #
####################

# https://stackoverflow.com/a/5947802/7254995
printOrange() {
    echo -e "\033[1;33m$1\033[0m"
}
experimentPrologue() {
    experiment_counter=$((experiment_counter + 1))
    printOrange "[Experiment $experiment_counter/$total_experiments]"
    printOrange "Time now: $(date -Iseconds)"
}
experimentEpilogue() {
    echo
    echo
}

experiment_counter=0
total_experiments=$(cat collect_pc_data_experiments.sh | grep -Ec '^experimentPrologue')
total_experiments=$((total_experiments - 1))

######################
# DEFINE EXPERIMENTS #
######################
# Avoid using loops here: the number of experiment-prologues in code is counted to determine total number of experiments
# (for pretty printing purposes)

experimentPrologue
printOrange 'Release, Full Timestamps (3600 messages, ~1h)'
./collect_pc_data.sh -v release -p false -m 3600
experimentEpilogue

# experimentPrologue
# printOrange 'Release, Probe Effect (3600 messages, ~1h)'
# ./collect_pc_data.sh -v release -p true -m 3600
# experimentEpilogue

experimentPrologue
printOrange 'Debug, Full Timestamps (3600 messages, ~1h)'
./collect_pc_data.sh -v debug -p false -m 3600
experimentEpilogue

# experimentPrologue
# printOrange 'Debug, Probe Effect (3600 messages, ~1h)'
# ./collect_pc_data.sh -v debug -p true -m 3600
# experimentEpilogue

experimentPrologue
printOrange 'Release, Full Timestamps (3600 messages, ~1h)'
./collect_pc_data.sh -v release -p false -m 3600
experimentEpilogue

# experimentPrologue
# printOrange 'Release, Probe Effect (3600 messages, ~1h)'
# ./collect_pc_data.sh -v release -p true -m 3600
# experimentEpilogue

experimentPrologue
printOrange 'Debug, Full Timestamps (3600 messages, ~1h)'
./collect_pc_data.sh -v debug -p false -m 3600
experimentEpilogue

# experimentPrologue
# printOrange 'Debug, Probe Effect (3600 messages, ~1h)'
# ./collect_pc_data.sh -v debug -p true -m 3600
# experimentEpilogue
