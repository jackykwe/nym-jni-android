#!/bin/bash

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
total_experiments=$(cat collect_android_data_phy_experiments.sh | grep -Ec '^experimentPrologue')
total_experiments=$((total_experiments - 1))

######################
# DEFINE EXPERIMENTS #
######################
# Avoid using loops here: the number of experiment-prologues in code is counted to determine total number of experiments
# (for pretty printing purposes)

# 16 experiments here, I do not think my phone can last 16++h on a single charge.
# Split it into 3 sections, 6 + 5 + 5

#===========#
# SECTION 1 #
#===========#

experimentPrologue
printOrange 'Release, WiFi, Unrestricted, Full Timestamps (3600 messages, ~1h)'
./collect_android_data_phy.sh -v release -c wifi -b unrestricted -p false -m 15
experimentEpilogue

experimentPrologue
printOrange 'Release, WiFi, Optimised, Full Timestamps (3600 messages, ~1h)'
./collect_android_data_phy.sh -v release -c wifi -b optimised -p false -m 15
experimentEpilogue

experimentPrologue
printOrange 'Release, Data, Unrestricted, Full Timestamps (3600 messages, ~1h)'
./collect_android_data_phy.sh -v release -c data -b unrestricted -p false -m 15
experimentEpilogue

experimentPrologue
printOrange 'Release, Data, Optimised, Full Timestamps (3600 messages, ~1h)'
./collect_android_data_phy.sh -v release -c data -b optimised -p false -m 15
experimentEpilogue

experimentPrologue
printOrange 'Debug, WiFi, Unrestricted, Full Timestamps (3600 messages, ~1h)'
./collect_android_data_phy.sh -v debug -c wifi -b unrestricted -p false -m 15
experimentEpilogue

experimentPrologue
printOrange 'Debug, WiFi, Optimised, Full Timestamps (3600 messages, ~1h)'
./collect_android_data_phy.sh -v debug -c wifi -b optimised -p false -m 15
experimentEpilogue

#===========#
# SECTION 2 #
#===========#

experimentPrologue
printOrange 'Debug, Data, Unrestricted, Full Timestamps (3600 messages, ~1h)'
./collect_android_data_phy.sh -v debug -c data -b unrestricted -p false -m 15
experimentEpilogue

experimentPrologue
printOrange 'Debug, Data, Optimised, Full Timestamps (3600 messages, ~1h)'
./collect_android_data_phy.sh -v debug -c data -b optimised -p false -m 15
experimentEpilogue

experimentPrologue
printOrange 'Release, WiFi, Unrestricted, Probe Effect (3600 messages, ~1h)'
./collect_android_data_phy.sh -v release -c wifi -b unrestricted -p true -m 15
experimentEpilogue

experimentPrologue
printOrange 'Release, WiFi, Optimised, Probe Effect (3600 messages, ~1h)'
./collect_android_data_phy.sh -v release -c wifi -b optimised -p true -m 15
experimentEpilogue

experimentPrologue
printOrange 'Release, Data, Unrestricted, Probe Effect (3600 messages, ~1h)'
./collect_android_data_phy.sh -v release -c data -b unrestricted -p true -m 15
experimentEpilogue

#===========#
# SECTION 3 #
#===========#

experimentPrologue
printOrange 'Release, Data, Optimised, Probe Effect (3600 messages, ~1h)'
./collect_android_data_phy.sh -v release -c data -b optimised -p true -m 15
experimentEpilogue

experimentPrologue
printOrange 'Debug, WiFi, Unrestricted, Probe Effect (3600 messages, ~1h)'
./collect_android_data_phy.sh -v debug -c wifi -b unrestricted -p true -m 15
experimentEpilogue

experimentPrologue
printOrange 'Debug, WiFi, Optimised, Probe Effect (3600 messages, ~1h)'
./collect_android_data_phy.sh -v debug -c wifi -b optimised -p true -m 15
experimentEpilogue

experimentPrologue
printOrange 'Debug, Data, Unrestricted, Probe Effect (3600 messages, ~1h)'
./collect_android_data_phy.sh -v debug -c data -b unrestricted -p true -m 15
experimentEpilogue

experimentPrologue
printOrange 'Debug, Data, Optimised, Probe Effect (3600 messages, ~1h)'
./collect_android_data_phy.sh -v debug -c data -b optimised -p true -m 15
experimentEpilogue