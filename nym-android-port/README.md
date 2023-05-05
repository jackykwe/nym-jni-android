# nym-android-port

The Android Studio project for the Nym Android client (prototype).

## Setup and run (quick debugging in Android Studio)

This was the setup used in very-early phases of the project, and is not recommended as many manual steps must be taken.

1. Uncomment these lines in `app/build.gradle`:
   ```
   //task beforeAppBuild(type: Exec) {
   //    workingDir '../'
   //    commandLine './beforeAppBuild.sh'
   //}
   //
   //afterEvaluate {
   //    tasks.named('preBuild') {
   //        it.dependsOn 'beforeAppBuild'
   //    }
   //}
   ```
   These are left commented by default because the building of the `nym-jni` crate and copying over the resultant `libnym_jni.so` file is done via another mechanism (shell scripts) as described in the next section. If those shell scripts are not used, then we rely on the simpler `beforeAppBuild.sh`.
2. In `beforeAppBuild.sh`, ensure the 3 checks are done.
3. In `beforeAppBuild.sh`, uncomment only the relevant lines at the bottom.
4. In the `nym` fork, ensure that the branch name is the same as in this repository (either `main` or `probe-effect-evaluation`). A mismatch may still result in successful compilation, but the runtime behaviour will be unexpected.
5. Press the run (Shift+F10) button in Android Studio.

## Usage of data collection scripts (automatic)

This is the recommended way to compile and run the Android application for testing. If testing is not necessary, this is still the recommended way to compile and install the application, as it performs all necessary checks and ensures the right combinations of artefacts are compiled. Just terminate the script once it says `"Printing received messages count every X min"`

This is part of the extension task (semi-automated testing pipeline). It compiles and installs the Android application, and using a persistent ADB connection, tracks the progress of an experiment. The timestamps rae collected into a `.txt` file in the `data_collection` directory (git-ignored).

### Using ADB USB

Some devices (e.g. Pixel) allow for a USB connection to the PC without the PC supplying power to the device, as described [here](https://android.stackexchange.com/a/242046). In such cases, we can still observe the battery drain while maintaining a USB connection, necessary for the shell script `collect_android_data_phy_adbusb.sh` to monitor and control the progress of the experiment.

Running `./collect_android_data_phy_adbusb.sh --help` returns the following help information. Example usages are in the file `collect_android_data_phy_experiments.sh`.

```
Usage:
collect_android_data_phy_adbusb.sh -a arm64-v8a|armeabi-v7a
                                   -d DEVICE_NAME
                                   -v debug|release
                                   -c data|wifi
                                   -b unrestricted|optimised|restricted
                                   -s true|false
                                   -p true|false
                                   --average-packet-delay-ms N
                                   --average-ack-delay-ms N
                                   --loop-cover-traffic-average-delay-ms N
                                   --message-sending-average-delay-ms N
                                   [-m N]

Utility to create a new Nym Client, run it, and collect timestamps from it.
The timestamps are saved into a new text file in the current working
directory. The name of the file will be printed on execution of this script.

This utility may trigger recompilation of the underlying Android application and Rust
crates 'nym' and 'nym-pc'.

Options:
-a, --abi arm64-v8a|armeabi-v7a
        (required) target architecture of Rust shared library. Must match the connected device.
-b, --battery-restriction unrestricted|optimised|restricted
        (required) selects the user-defined battery restriction mode of the Android app.
-c, --connectivity data|wifi
        (required) automatically switches between mobile data or WiFi in preparation for
        data collection.
-d, --device-name DEVICE_NAME
        (required) Device name used in the name of output log files
-h, --help
        show this help message and exit
-m, --max-messages N
        stop the evaluation after exactly N messages are sent through the nym network. If
        not specified, the evaluation will not terminate until explictily interrupted.
-p, --probeeffect true|false
        (required) whether to run the builds of the underlying Rust crates 'nym' and
        'nym-pc' while collecting only timestamps tK=1 and tK=8.
-s, --power-save-mode true|false
        (required) whether to enable Power Save Mode (PSM) (battery saver) on the device
-v, --variant debug|release
        (required) whether to run the debug or release builds of the underlying Rust
        crates 'nym' and 'nym-pc'. This collects all timestamps from tK=1 to tK=8
        inclusive.
--average-packet-delay-ms N
        (required) the average packet delay to be inserted into the Nym config file.
        (units: ms) Nym's default value is 50.
--average-ack-delay-ms N
        (required) the average ack delay to be inserted into the Nym config file.
        (units: ms) Nym's default value is 50.
--loop-cover-traffic-average-delay-ms N
        (required) the loop cover traffic average delay to be inserted into the Nym
        config file. (units: ms) Nym's default value is 200.
--message-sending-average-delay-ms N
        (required) the message sending average delay to be inserted into the Nym config
        file. (units: ms) Nym's default value is 20.
```

### Using ADB TCP

Some devices (e.g. Moto) do _not_ allow for a USB connection to the PC without the PC supplying power to the device (typically older devices). In such cases, we cannot observe the battery drain while maintaining a USB connection. The ADB connection is maintained over WiFi using TCP instead.

This step requires specification of the IP of the Android device to the script, using the `-i` option. It is recommended that the IP address of the device be fixed via DHCP IP Reservation, where possible.

Running `./collect_android_data_phy_adbtcp.sh --help` returns the following help information. Example usages are in the file `collect_android_data_phy_experiments.sh`.

```
Usage:
collect_android_data_phy_adbtcp.sh -a arm64-v8a|armeabi-v7a
                                   -d DEVICE_NAME
                                   -i IP:PORT
                                   -v debug|release
                                   -c data|wifi
                                   -b unrestricted|optimised|restricted
                                   -s true|false
                                   -p true|false
                                   --average-packet-delay-ms N
                                   --average-ack-delay-ms N
                                   --loop-cover-traffic-average-delay-ms N
                                   --message-sending-average-delay-ms N
                                   [-m N]

Utility to create a new Nym Client, run it, and collect timestamps from it.
The timestamps are saved into a new text file in the current working
directory. The name of the file will be printed on execution of this script.

This utility may trigger recompilation of the underlying Android application and Rust
crates 'nym' and 'nym-pc'.

Options:
-a, --abi arm64-v8a|armeabi-v7a
        (required) target architecture of Rust shared library. Must match the connected device.
-b, --battery-restriction unrestricted|optimised|restricted
        (required) selects the user-defined battery restriction mode of the Android app.
-c, --connectivity data|wifi
        (required) automatically switches between mobile data or WiFi in preparation for
        data collection.
-d, --device-name DEVICE_NAME
        (required) Device name used in the name of output log files
-h, --help
        show this help message and exit
-i, --ip-and-port IP:PORT
        (required) IP and port used to connect to an ADB device
-m, --max-messages N
        stop the evaluation after exactly N messages are sent through the nym network. If
        not specified, the evaluation will not terminate until explictily interrupted.
-p, --probeeffect true|false
        (required) whether to run the builds of the underlying Rust crates 'nym' and
        'nym-pc', while collecting only timestamps tK=1 and tK=8.
-s, --power-save-mode true|false
        (required) whether to enable Power Save Mode (PSM) (battery saver) on the device
-v, --variant debug|release
        (required) whether to run the debug or release builds of the underlying Rust
        crates 'nym' and 'nym-pc'. This collects all timestamps from tK=1 to tK=8
        inclusive.
--average-packet-delay-ms N
        (required) the average packet delay to be inserted into the Nym config file.
        (units: ms) Nym's default value is 50.
--average-ack-delay-ms N
        (required) the average ack delay to be inserted into the Nym config file.
        (units: ms) Nym's default value is 50.
--loop-cover-traffic-average-delay-ms N
        (required) the loop cover traffic average delay to be inserted into the Nym
        config file. (units: ms) Nym's default value is 200.
--message-sending-average-delay-ms N
        (required) the message sending average delay to be inserted into the Nym config
        file. (units: ms) Nym's default value is 20.
```
