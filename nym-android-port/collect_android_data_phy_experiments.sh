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

# NB: The device is unlikely to survive all iterations in one go. Perform experiments 6 at a time (if perform >=7, phone may die).
# NB: the Moto device's IP is pinned at 192.168.0.201 via DHCP IP Reservation (configured home WiFi router)
# NB: The experiments listed here are for illustrative purposes. Actual runs may not use these exact experiments.

# Delay factor 1x

experimentPrologue
./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v release -c wifi -b unrestricted -s false -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 200 --message-sending-average-delay-ms 20 -m 3600
experimentEpilogue

experimentPrologue
./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v release -c wifi -b unrestricted -s false -p true --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 200 --message-sending-average-delay-ms 20 -m 3600
experimentEpilogue

experimentPrologue
./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v release -c wifi -b unrestricted -s true -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 200 --message-sending-average-delay-ms 20 -m 3600
experimentEpilogue

experimentPrologue
./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v release -c wifi -b unrestricted -s true -p true --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 200 --message-sending-average-delay-ms 20 -m 3600
experimentEpilogue

experimentPrologue
./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v release -c wifi -b optimised -s false -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 200 --message-sending-average-delay-ms 20 -m 3600
experimentEpilogue

experimentPrologue
./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v release -c wifi -b optimised -s false -p true --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 200 --message-sending-average-delay-ms 20 -m 3600
experimentEpilogue

experimentPrologue
./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v release -c wifi -b optimised -s true -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 200 --message-sending-average-delay-ms 20 -m 3600
experimentEpilogue

experimentPrologue
./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v release -c wifi -b optimised -s true -p true --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 200 --message-sending-average-delay-ms 20 -m 3600
experimentEpilogue

experimentPrologue
./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v debug -c wifi -b unrestricted -s false -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 200 --message-sending-average-delay-ms 20 -m 3600
experimentEpilogue

experimentPrologue
./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v debug -c wifi -b unrestricted -s false -p true --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 200 --message-sending-average-delay-ms 20 -m 3600
experimentEpilogue

experimentPrologue
./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v debug -c wifi -b unrestricted -s true -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 200 --message-sending-average-delay-ms 20 -m 3600
experimentEpilogue

experimentPrologue
./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v debug -c wifi -b unrestricted -s true -p true --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 200 --message-sending-average-delay-ms 20 -m 3600
experimentEpilogue

experimentPrologue
./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v debug -c wifi -b optimised -s false -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 200 --message-sending-average-delay-ms 20 -m 3600
experimentEpilogue

experimentPrologue
./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v debug -c wifi -b optimised -s false -p true --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 200 --message-sending-average-delay-ms 20 -m 3600
experimentEpilogue

experimentPrologue
./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v debug -c wifi -b optimised -s true -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 200 --message-sending-average-delay-ms 20 -m 3600
experimentEpilogue

experimentPrologue
./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v debug -c wifi -b optimised -s true -p true --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 200 --message-sending-average-delay-ms 20 -m 3600
experimentEpilogue

experimentPrologue
./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v release -c data -b unrestricted -s false -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 200 --message-sending-average-delay-ms 20 -m 3600
experimentEpilogue

experimentPrologue
./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v release -c data -b unrestricted -s false -p true --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 200 --message-sending-average-delay-ms 20 -m 3600
experimentEpilogue

experimentPrologue
./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v release -c data -b unrestricted -s true -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 200 --message-sending-average-delay-ms 20 -m 3600
experimentEpilogue

experimentPrologue
./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v release -c data -b unrestricted -s true -p true --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 200 --message-sending-average-delay-ms 20 -m 3600
experimentEpilogue

experimentPrologue
./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v release -c data -b optimised -s false -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 200 --message-sending-average-delay-ms 20 -m 3600
experimentEpilogue

experimentPrologue
./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v release -c data -b optimised -s false -p true --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 200 --message-sending-average-delay-ms 20 -m 3600
experimentEpilogue

experimentPrologue
./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v release -c data -b optimised -s true -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 200 --message-sending-average-delay-ms 20 -m 3600
experimentEpilogue

experimentPrologue
./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v release -c data -b optimised -s true -p true --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 200 --message-sending-average-delay-ms 20 -m 3600
experimentEpilogue

experimentPrologue
./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v debug -c data -b unrestricted -s false -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 200 --message-sending-average-delay-ms 20 -m 3600
experimentEpilogue

experimentPrologue
./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v debug -c data -b unrestricted -s false -p true --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 200 --message-sending-average-delay-ms 20 -m 3600
experimentEpilogue

experimentPrologue
./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v debug -c data -b unrestricted -s true -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 200 --message-sending-average-delay-ms 20 -m 3600
experimentEpilogue

experimentPrologue
./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v debug -c data -b unrestricted -s true -p true --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 200 --message-sending-average-delay-ms 20 -m 3600
experimentEpilogue

experimentPrologue
./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v debug -c data -b optimised -s false -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 200 --message-sending-average-delay-ms 20 -m 3600
experimentEpilogue

experimentPrologue
./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v debug -c data -b optimised -s false -p true --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 200 --message-sending-average-delay-ms 20 -m 3600
experimentEpilogue

experimentPrologue
./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v debug -c data -b optimised -s true -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 200 --message-sending-average-delay-ms 20 -m 3600
experimentEpilogue

experimentPrologue
./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v debug -c data -b optimised -s true -p true --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 200 --message-sending-average-delay-ms 20 -m 3600
experimentEpilogue

# Delay factor 10x

experimentPrologue
# ./collect_android_data_phy_adbtcp.sh -a armeabi-v7a -d moto -i 192.168.0.201:5555 -v release -c wifi -b unrestricted -s false -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 2000 --message-sending-average-delay-ms 200 -m 3600
./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v release -c wifi -b unrestricted -s false -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 2000 --message-sending-average-delay-ms 200 -m 3600
#experimentEpilogue

experimentPrologue
#./collect_android_data_phy_adbtcp.sh -a armeabi-v7a -d moto -i 192.168.0.201:5555 -v release -c wifi -b optimised -s false -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 2000 --message-sending-average-delay-ms 200 -m 3600
./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v release -c wifi -b optimised -s false -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 2000 --message-sending-average-delay-ms 200 -m 3600
experimentEpilogue

experimentPrologue
#./collect_android_data_phy_adbtcp.sh -a armeabi-v7a -d moto -i 192.168.0.201:5555 -v release -c wifi -b unrestricted -s true -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 2000 --message-sending-average-delay-ms 200 -m 3600
./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v release -c wifi -b unrestricted -s true -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 2000 --message-sending-average-delay-ms 200 -m 3600
experimentEpilogue

experimentPrologue
#./collect_android_data_phy_adbtcp.sh -a armeabi-v7a -d moto -i 192.168.0.201:5555 -v release -c wifi -b optimised -s true -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 2000 --message-sending-average-delay-ms 200 -m 3600
./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v release -c wifi -b optimised -s true -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 2000 --message-sending-average-delay-ms 200 -m 3600
experimentEpilogue

# Delay factor 100x

experimentPrologue
#./collect_android_data_phy_adbtcp.sh -a armeabi-v7a -d moto -i 192.168.0.201:5555 -v release -c wifi -b unrestricted -s false -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 20000 --message-sending-average-delay-ms 2000 -m 3600
./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v release -c wifi -b unrestricted -s false -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 20000 --message-sending-average-delay-ms 2000 -m 3600
experimentEpilogue

experimentPrologue
#./collect_android_data_phy_adbtcp.sh -a armeabi-v7a -d moto -i 192.168.0.201:5555 -v release -c wifi -b optimised -s false -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 20000 --message-sending-average-delay-ms 2000 -m 3600
./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v release -c wifi -b optimised -s false -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 20000 --message-sending-average-delay-ms 2000 -m 3600
experimentEpilogue

experimentPrologue
#./collect_android_data_phy_adbtcp.sh -a armeabi-v7a -d moto -i 192.168.0.201:5555 -v release -c wifi -b unrestricted -s true -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 20000 --message-sending-average-delay-ms 2000 -m 3600
./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v release -c wifi -b unrestricted -s true -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 20000 --message-sending-average-delay-ms 2000 -m 3600
experimentEpilogue

experimentPrologue
#./collect_android_data_phy_adbtcp.sh -a armeabi-v7a -d moto -i 192.168.0.201:5555 -v release -c wifi -b optimised -s true -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 20000 --message-sending-average-delay-ms 2000 -m 3600
./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v release -c wifi -b optimised -s true -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 20000 --message-sending-average-delay-ms 2000 -m 3600
experimentEpilogue

# Delay factor 1000x

experimentPrologue
#./collect_android_data_phy_adbtcp.sh -a armeabi-v7a -d moto -i 192.168.0.201:5555 -v release -c wifi -b unrestricted -s false -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 200000 --message-sending-average-delay-ms 20000 -m 3600
./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v release -c wifi -b unrestricted -s false -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 200000 --message-sending-average-delay-ms 20000 -m 3600
experimentEpilogue

experimentPrologue
#./collect_android_data_phy_adbtcp.sh -a armeabi-v7a -d moto -i 192.168.0.201:5555 -v release -c wifi -b optimised -s false -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 200000 --message-sending-average-delay-ms 20000 -m 3600
./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v release -c wifi -b optimised -s false -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 200000 --message-sending-average-delay-ms 20000 -m 3600
experimentEpilogue

experimentPrologue
#./collect_android_data_phy_adbtcp.sh -a armeabi-v7a -d moto -i 192.168.0.201:5555 -v release -c wifi -b unrestricted -s true -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 200000 --message-sending-average-delay-ms 20000 -m 3600
./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v release -c wifi -b unrestricted -s true -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 200000 --message-sending-average-delay-ms 20000 -m 3600
experimentEpilogue

experimentPrologue
#./collect_android_data_phy_adbtcp.sh -a armeabi-v7a -d moto -i 192.168.0.201:5555 -v release -c wifi -b optimised -s true -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 200000 --message-sending-average-delay-ms 20000 -m 3600
./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v release -c wifi -b optimised -s true -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 200000 --message-sending-average-delay-ms 20000 -m 3600
experimentEpilogue
