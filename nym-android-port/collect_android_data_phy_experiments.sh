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

experimentPrologue
./collect_android_data_phy.sh -a armeabi-v7a -d moto -i 192.168.0.200:5555 -v release -c wifi -b unrestricted -s false -p false -m 3600
experimentEpilogue

experimentPrologue
./collect_android_data_phy.sh -a armeabi-v7a -d moto -i 192.168.0.200:5555 -v release -c wifi -b optimised -s false -p false -m 3600
experimentEpilogue

experimentPrologue
./collect_android_data_phy.sh -a armeabi-v7a -d moto -i 192.168.0.200:5555 -v release -c wifi -b unrestricted -s true -p false -m 3600
experimentEpilogue

experimentPrologue
./collect_android_data_phy.sh -a armeabi-v7a -d moto -i 192.168.0.200:5555 -v release -c wifi -b optimised -s true -p false -m 3600
experimentEpilogue

# 2 rounds.

experimentPrologue
./collect_android_data_phy.sh -a armeabi-v7a -d moto -i 192.168.0.200:5555 -v release -c wifi -b unrestricted -s false -p false -m 3600
experimentEpilogue

experimentPrologue
./collect_android_data_phy.sh -a armeabi-v7a -d moto -i 192.168.0.200:5555 -v release -c wifi -b optimised -s false -p false -m 3600
experimentEpilogue

experimentPrologue
./collect_android_data_phy.sh -a armeabi-v7a -d moto -i 192.168.0.200:5555 -v release -c wifi -b unrestricted -s true -p false -m 3600
experimentEpilogue

experimentPrologue
./collect_android_data_phy.sh -a armeabi-v7a -d moto -i 192.168.0.200:5555 -v release -c wifi -b optimised -s true -p false -m 3600
experimentEpilogue
