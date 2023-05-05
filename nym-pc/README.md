# nym-pc

A Rust crate that contains the PC client. Adapted from example code in [`nymtech/nym`](https://github.com/nymtech/nym/blob/d92d6877a47aeec233a65658caba0379c75a9788/clients/native/examples/websocket_textsend.rs).

## Setup and run (manual, for demonstration)

To setup a PC Nym client, these three commands are run in sequence in a shell. ("in `<dir>`" means that the current directory of the shell should be set to `<dir>`)

1. (in [`nym`](https://github.com/jackykwe/nym)) `cargo run --release --bin nym-client init --id <ID>`\
   This executes the `init(...)` (as in the dissertation), terminating after setting up a new Nym client.
2. (in [`nym`](https://github.com/jackykwe/nym)) `cargo run --release --bin nym-client run --id <ID>`\
   This executes `run(...)` (as in the dissertation), a long-running command-line program that continuously talks to the gateway chosen during `init(...)`.
3. (in this repository, and in a separate shell) `cargo run --release`
   This executes a WebSocket Client (WSC), a long-running process that serves as a frontend to the `run(...)` program, saving messages to a local database.

## Usage of data collection scripts (automatic)

This is part of the extension task (semi-automated testing pipeline). It setups and runs a PC client, and collects timestamps into a `.txt` file in the current working directory.

Running `./collect_pc_data.sh --help` returns the following help information. Example usages are in the file `collect_pc_data_experiments.sh`.

```
Usage:
collect_pc_data.sh --variant debug|release --probeeffect true|false [--max-messages N]

Utility to create a new Nym Client, run it, and collect timestamps from it.
The timestamps are saved into a new text file in the current working
directory. The name of the file will be printed on execution of this script.

This utility may trigger recompilation of the underlying Rust crates 'nym'
and 'nym-pc'.

Options:
-h, --help
        show this help message and exit
-m, --max-messages N
        stop the evaluation after exactly N messages are sent through the nym network. If
        not specified, the evaluation will not terminate until explictily interrupted.
-p, --probeeffect true|false
        (required) whether to run the builds of the underlying Rust crates 'nym' and
        'nym-pc', while collecting only timestamps tK=1 and tK=8.
-v, --variant debug|release
        (required) whether to run the debug or release builds of the underlying Rust
        crates 'nym' and 'nym-pc'. This collects all timestamps from tK=1 to tK=8
        inclusive.
```
