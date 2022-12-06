// Copyright 2021 - Nym Technologies SA <contact@nymtech.net>
// SPDX-License-Identifier: Apache-2.0

// use crate::client::config::template::config_template;
use client_core::config::Config as BaseConfig;
pub use client_core::config::MISSING_VALUE;
use config::defaults::DEFAULT_WEBSOCKET_LISTENING_PORT;
use config::NymConfig;
use serde::{Deserialize, Serialize};
use std::path::PathBuf;

mod template;
use self::template::config_template;

pub const STORAGE_ABS_PATH_ENV_VAR_NAME: &str = "ANDROIDCONFIG_STORAGE_ABS_PATH";

#[derive(Debug, Deserialize, PartialEq, Eq, Serialize, Clone, Copy)]
#[serde(deny_unknown_fields)]
pub enum SocketType {
    WebSocket,
    None,
}

impl SocketType {
    pub fn from_string<S: Into<String>>(val: S) -> Self {
        let mut upper = val.into();
        upper.make_ascii_uppercase();
        match upper.as_ref() {
            "WEBSOCKET" | "WS" => SocketType::WebSocket,
            _ => SocketType::None,
        }
    }
}

#[derive(Debug, Default, Deserialize, PartialEq, Serialize)]
#[serde(deny_unknown_fields)]
pub struct AndroidConfig {
    #[serde(flatten)]
    base: BaseConfig<AndroidConfig>,

    socket: Socket,
}

impl NymConfig for AndroidConfig {
    fn template() -> &'static str {
        config_template()
    }

    fn default_root_directory() -> PathBuf {
        PathBuf::from(
            std::env::var("ANDROIDCONFIG_STORAGE_ABS_PATH").expect(&format!(
                "Failed to get {}. Is the environment variable not set?",
                STORAGE_ABS_PATH_ENV_VAR_NAME
            )),
        )
        .join(".nym")
        .join("clients")
        // dirs::home_dir()
        //     .expect("Failed to evaluate $HOME value")
        //     .join(".nym")
        //     .join("clients")
    }

    fn try_default_root_directory() -> Option<PathBuf> {
        std::env::var("ANDROIDCONFIG_STORAGE_ABS_PATH")
            .ok()
            .map(|path| PathBuf::from(path).join(".nym").join("clients"))
        // dirs::home_dir().map(|path| path.join(".nym").join("clients"))
    }

    fn root_directory(&self) -> PathBuf {
        self.base.get_nym_root_directory()
    }

    fn config_directory(&self) -> PathBuf {
        self.root_directory()
            .join(self.base.get_id())
            .join("config")
    }

    fn data_directory(&self) -> PathBuf {
        self.root_directory().join(self.base.get_id()).join("data")
    }
}

impl AndroidConfig {
    pub fn new<S: Into<String>>(id: S) -> Self {
        AndroidConfig {
            base: BaseConfig::new(id),
            socket: Default::default(),
        }
    }

    pub fn with_socket(mut self, socket_type: SocketType) -> Self {
        self.socket.socket_type = socket_type;
        self
    }

    pub fn with_port(mut self, port: u16) -> Self {
        self.socket.listening_port = port;
        self
    }

    // getters
    pub fn get_config_file_save_location(&self) -> PathBuf {
        self.config_directory().join(Self::config_file_name())
    }

    pub fn get_base(&self) -> &BaseConfig<Self> {
        &self.base
    }

    pub fn get_base_mut(&mut self) -> &mut BaseConfig<Self> {
        &mut self.base
    }

    pub fn get_socket_type(&self) -> SocketType {
        self.socket.socket_type
    }

    pub fn get_listening_port(&self) -> u16 {
        self.socket.listening_port
    }
}

#[derive(Debug, Deserialize, PartialEq, Eq, Serialize)]
#[serde(deny_unknown_fields)]
pub struct Socket {
    socket_type: SocketType,
    listening_port: u16,
}

impl Default for Socket {
    fn default() -> Self {
        Socket {
            socket_type: SocketType::WebSocket,
            listening_port: DEFAULT_WEBSOCKET_LISTENING_PORT,
        }
    }
}
