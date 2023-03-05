use clap::Parser;
use futures::{
    stream::{SplitSink, SplitStream},
    SinkExt, StreamExt,
};
use nix::sys::time::TimeValLike;
use serde_json::json;
use tokio::net::TcpStream;
use tokio_tungstenite::{
    connect_async, tungstenite::protocol::Message, MaybeTlsStream, WebSocketStream,
};

#[derive(Parser, Debug)]
struct Args {
    /// Maximum number of messages to send through the mix network before terminating evaluation.
    #[arg(short, long)]
    max_messages: Option<u64>,
}

// Adapted from example code in the Nym codebase

// fn get_current_battery_level() -> String {
//     std::fs::read_to_string("/sys/class/power_supply/BAT0/capacity")
//         .expect("unable to read system battery level")
//         .trim()
//         .to_string()
// }

// fn get_network_statistics() -> String {
//     let output = std::process::Command::new("/usr/bin/iw")
//         .arg("dev")
//         .arg("wlp0s20f3")
//         .arg("link")
//         .output()
//         .expect("failed to execute process")
//         .stdout;
//     let output = String::from_utf8_lossy(&output);
//     let lines = output.split('\n').collect::<Vec<_>>();
//     if lines[0].trim() == "Not connected." {
//         return String::from("Not connected to WiFi.");
//     }
//     let ssid = lines[1].trim().strip_prefix("SSID: ").unwrap();
//     let dbm_rssi = lines[5]
//         .trim()
//         .strip_prefix("signal: ")
//         .unwrap()
//         .strip_suffix(" dBm")
//         .unwrap();
//     let rx_link_speed_mbps = lines[6]
//         .trim()
//         .strip_prefix("rx bitrate: ")
//         .unwrap()
//         .chars()
//         .take_while(|c| c.ne(&' '))
//         .collect::<String>();
//     let tx_link_speed_mbps = lines[7]
//         .trim()
//         .strip_prefix("tx bitrate: ")
//         .unwrap()
//         .chars()
//         .take_while(|c| c.ne(&' '))
//         .collect::<String>();

//     format!(
//         "Network Statistic (WiFi) | ssid={} rxLsMbps={} txLsMbps={} dBmRssi={}",
//         ssid, rx_link_speed_mbps, tx_link_speed_mbps, dbm_rssi
//     )
// }

fn prepare_message(log_message_id: u64, from_address: &String) -> String {
    json!({
        "type": "send",
        "message": format!("{}|{}", from_address, log_message_id),
        "recipient": from_address
    })
    .to_string()
}

fn prepare_self_address_message() -> String {
    json!({ "type": "selfAddress" }).to_string()
}

async fn get_self_address(ws_stream: &mut WebSocketStream<MaybeTlsStream<TcpStream>>) -> String {
    ws_stream
        .send(Message::Text(prepare_self_address_message()))
        .await
        .unwrap();

    let raw_message = ws_stream.next().await.unwrap().unwrap();
    let response: serde_json::Value = match raw_message {
        Message::Text(txt_msg) => serde_json::from_str(&txt_msg).unwrap(),
        _ => panic!("received an unexpected response type!"),
    };

    response["address"].as_str().unwrap().to_string()
}

async fn producer(
    mut ws_stream: SplitSink<WebSocketStream<MaybeTlsStream<TcpStream>>, Message>,
    from_address: String,
) {
    let mut log_message_id: u64 = 0;

    let mut interval = tokio::time::interval(std::time::Duration::from_secs(1));
    loop {
        interval.tick().await;

        let message = Message::Text(prepare_message(log_message_id, &from_address));

        let log_send_nanos = nix::time::clock_gettime(nix::time::ClockId::CLOCK_BOOTTIME)
            .unwrap()
            .num_nanoseconds();

        ws_stream.send(message).await.unwrap();

        log::info!(
            "tK=1 l=KotlinLeaving tM={} mId={}",
            log_send_nanos,
            log_message_id
        );

        // // Frequency of reading battery matches nym-android-port implementation
        // if log_message_id % 60 == 0 {
        //     log::info!(
        //         "tK=1EB l=Extra tM={} mId={} b={}%",
        //         log_send_nanos,
        //         log_message_id,
        //         get_current_battery_level()
        //     );
        // }
        // // Frequency of logging network statistics matches nym-android-port implementation
        // if log_message_id % 10 == 0 {
        //     log::info!(
        //         "tK=1EN l=Extra tM={} mId{} n='{}'",
        //         log_send_nanos,
        //         log_message_id,
        //         get_network_statistics()
        //     )
        // }

        log_message_id += 1;
    }
}

async fn consumer(
    mut ws_stream: SplitStream<WebSocketStream<MaybeTlsStream<TcpStream>>>,
    max_messages: Option<u64>,
) {
    let mut received_count: u64 = 0;

    loop {
        if let Some(max_messages) = max_messages {
            if received_count == max_messages {
                break;
            }
        }

        let raw_message = ws_stream.next().await.unwrap().unwrap();

        let log_recv_nanos = nix::time::clock_gettime(nix::time::ClockId::CLOCK_BOOTTIME)
            .unwrap()
            .num_nanoseconds();

        let response: serde_json::Value = match raw_message {
            Message::Text(txt_msg) => serde_json::from_str(&txt_msg).unwrap(),
            _ => panic!("received an unexpected response type!"),
        };

        let message = response["message"].as_str().unwrap().to_string();
        let split_index = message.find('|').unwrap();
        let log_message_id = message.chars().skip(split_index + 1).collect::<String>();

        log::info!(
            "tK=8 l=KotlinArrived tM={} mId={}",
            log_recv_nanos,
            log_message_id
        );

        received_count += 1;
        if received_count % 100 == 0 {
            std::fs::write(
                "nymRunEvaluationMessagesReceived.txt",
                received_count.to_string(),
            )
            .expect("Unable to write to messages received sync file");
        }
    }
}

#[tokio::main]
async fn main() {
    let args = Args::parse();

    pretty_env_logger::init();

    let uri = "ws://localhost:1977";
    let mut ws_stream = match connect_async(uri).await {
        Err(e) => {
            log::error!("{}", e);
            std::process::exit(42);
        }
        Ok((stream, _)) => stream,
    };

    let from_address = get_self_address(&mut ws_stream).await;
    log::info!("our full address is: {}", from_address);

    let (ws_sender, ws_receiver) = ws_stream.split();

    let p = tokio::spawn(producer(ws_sender, from_address));
    let c = tokio::spawn(consumer(ws_receiver, args.max_messages));
    c.await.unwrap();
    p.abort();

    std::process::exit(0);
}
