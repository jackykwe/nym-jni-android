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
./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v release -c wifi -b unrestricted -s true -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 20000 --message-sending-average-delay-ms 2000 -m 3600
experimentEpilogue

experimentPrologue
./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v release -c wifi -b optimised -s true -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 20000 --message-sending-average-delay-ms 2000 -m 3600
experimentEpilogue

experimentPrologue
./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v release -c data -b unrestricted -s true -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 20000 --message-sending-average-delay-ms 2000 -m 3600
experimentEpilogue

experimentPrologue
./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v release -c data -b optimised -s true -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 20000 --message-sending-average-delay-ms 2000 -m 3600
experimentEpilogue
#experimentPrologue
#./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v release -c wifi -b optimised -s false -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 20000 --message-sending-average-delay-ms 2000 -m 3600
#experimentEpilogue
#
#experimentPrologue
#./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v release -c data -b optimised -s false -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 20000 --message-sending-average-delay-ms 2000 -m 3600
#experimentEpilogue
#
#experimentPrologue
#./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v release -c wifi -b unrestricted -s false -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 2000 --message-sending-average-delay-ms 200 -m 3600
#experimentEpilogue
#
#experimentPrologue
#./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v release -c wifi -b unrestricted -s false -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 2000 --message-sending-average-delay-ms 200 -m 3600
#experimentEpilogue
#
#experimentPrologue
#./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v release -c wifi -b unrestricted -s false -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 2000 --message-sending-average-delay-ms 200 -m 3600
#experimentEpilogue
#
#experimentPrologue
#./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v release -c wifi -b unrestricted -s false -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 2000 --message-sending-average-delay-ms 200 -m 3600
#experimentEpilogue


#experimentPrologue
##./collect_android_data_phy_adbtcp.sh -a armeabi-v7a -d moto -i 192.168.0.201:5555 -v release -c wifi -b unrestricted -s false -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 200 --message-sending-average-delay-ms 20 -m 3600
#./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v release -c wifi -b unrestricted -s false -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 200 --message-sending-average-delay-ms 20 -m 3600
#experimentEpilogue
#
#experimentPrologue
##./collect_android_data_phy_adbtcp.sh -a armeabi-v7a -d moto -i 192.168.0.201:5555 -v release -c wifi -b optimised -s false -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 200 --message-sending-average-delay-ms 20 -m 3600
#./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v release -c wifi -b optimised -s false -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 200 --message-sending-average-delay-ms 20 -m 3600
#experimentEpilogue
#
#experimentPrologue
##./collect_android_data_phy_adbtcp.sh -a armeabi-v7a -d moto -i 192.168.0.201:5555 -v release -c wifi -b unrestricted -s true -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 200 --message-sending-average-delay-ms 20 -m 3600
#./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v release -c wifi -b unrestricted -s true -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 200 --message-sending-average-delay-ms 20 -m 3600
#experimentEpilogue
#
#experimentPrologue
##./collect_android_data_phy_adbtcp.sh -a armeabi-v7a -d moto -i 192.168.0.201:5555 -v release -c wifi -b optimised -s true -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 200 --message-sending-average-delay-ms 20 -m 3600
#./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v release -c wifi -b optimised -s true -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 200 --message-sending-average-delay-ms 20 -m 3600
#experimentEpilogue


## 2nd round.
#
#experimentPrologue
#./collect_android_data_phy_adbtcp.sh -a armeabi-v7a -d moto -i 192.168.0.201:5555 -v release -c wifi -b unrestricted -s false -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 2000 --message-sending-average-delay-ms 200 -m 3600
#./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v release -c wifi -b unrestricted -s false -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 2000 --message-sending-average-delay-ms 200 -m 3600
##experimentEpilogue
#
#experimentPrologue
##./collect_android_data_phy_adbtcp.sh -a armeabi-v7a -d moto -i 192.168.0.201:5555 -v release -c wifi -b optimised -s false -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 2000 --message-sending-average-delay-ms 200 -m 3600
#./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v release -c wifi -b optimised -s false -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 2000 --message-sending-average-delay-ms 200 -m 3600
#experimentEpilogue
#
#experimentPrologue
##./collect_android_data_phy_adbtcp.sh -a armeabi-v7a -d moto -i 192.168.0.201:5555 -v release -c wifi -b unrestricted -s true -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 2000 --message-sending-average-delay-ms 200 -m 3600
#./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v release -c wifi -b unrestricted -s true -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 2000 --message-sending-average-delay-ms 200 -m 3600
#experimentEpilogue
#
#experimentPrologue
##./collect_android_data_phy_adbtcp.sh -a armeabi-v7a -d moto -i 192.168.0.201:5555 -v release -c wifi -b optimised -s true -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 2000 --message-sending-average-delay-ms 200 -m 3600
#./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v release -c wifi -b optimised -s true -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 2000 --message-sending-average-delay-ms 200 -m 3600
#experimentEpilogue
##
### 3rd round.
##
#experimentPrologue
##./collect_android_data_phy_adbtcp.sh -a armeabi-v7a -d moto -i 192.168.0.201:5555 -v release -c wifi -b unrestricted -s false -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 20000 --message-sending-average-delay-ms 2000 -m 3600
#./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v release -c wifi -b unrestricted -s false -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 20000 --message-sending-average-delay-ms 2000 -m 3600
#experimentEpilogue
#
#experimentPrologue
##./collect_android_data_phy_adbtcp.sh -a armeabi-v7a -d moto -i 192.168.0.201:5555 -v release -c wifi -b optimised -s false -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 20000 --message-sending-average-delay-ms 2000 -m 3600
#./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v release -c wifi -b optimised -s false -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 20000 --message-sending-average-delay-ms 2000 -m 3600
#experimentEpilogue
#
#experimentPrologue
##./collect_android_data_phy_adbtcp.sh -a armeabi-v7a -d moto -i 192.168.0.201:5555 -v release -c wifi -b unrestricted -s true -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 20000 --message-sending-average-delay-ms 2000 -m 3600
#./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v release -c wifi -b unrestricted -s true -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 20000 --message-sending-average-delay-ms 2000 -m 3600
#experimentEpilogue
#
#experimentPrologue
##./collect_android_data_phy_adbtcp.sh -a armeabi-v7a -d moto -i 192.168.0.201:5555 -v release -c wifi -b optimised -s true -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 20000 --message-sending-average-delay-ms 2000 -m 3600
#./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v release -c wifi -b optimised -s true -p false --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 20000 --message-sending-average-delay-ms 2000 -m 3600
#experimentEpilogue