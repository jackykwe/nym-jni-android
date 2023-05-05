# nym-jni-android (Demonstration)

## Phase 1A: Setup Android Client

1. (from this repository) `cd nym-android-port; ./collect_android_data_phy_adbusb.sh -a arm64-v8a -d pixel -v release -c wifi -b optimised -s false -p true --average-packet-delay-ms 50 --average-ack-delay-ms 50 --loop-cover-traffic-average-delay-ms 200 --message-sending-average-delay-ms 20`\
   Run the script until completion. The option `-d` is not inconsequential for a demonstration (it's used for some conditional statements during data analysis) but needs to be specified. All other arguments are important as they affect compilation and installation.

## Phase 1B: Setup PC Client

1. (in [`nym`](https://github.com/jackykwe/nym)) `cargo run --release --bin nym-client init --id <ID>`\
   This executes the `init(...)` (as in the dissertation), terminating after setting up a new Nym client.
2. (in [`nym`](https://github.com/jackykwe/nym)) `cargo run --release --bin nym-client run --id <ID>`\
   This executes `run(...)` (as in the dissertation), a long-running command-line program that continuously talks to the gateway chosen during `init(...)`.
3. (from this repository, and in a separate shell) `cd nym-pc; cargo run --release`
   This executes a WebSocket Client (WSC), a long-running process that serves as a frontend to the `run(...)` program, saving messages to a local database.

## Phase 2: Demonstration

1. On the Android app, copy the Nym address and send it to the PC. Paste it into the `nym-pc/communicate_with_android_client.py` file.
2. In a new shell, run `cd nym-pc; python3 communicate_with_android_client.py`. You may encounter missing dependencies: either install them or use a virtual environment (git-ignored). This will print out the PC client
3. Initiate the conversation from either the PC or Android:
   - Initiating from Android: copy PC's Nym address onto the Android application, add a contact a send a message. The PC shell in step 2 should receive the message.
   - Initiating from PC: Just type a message and press enter. The Android client should receive your message (and automatically have you as a contact too).
