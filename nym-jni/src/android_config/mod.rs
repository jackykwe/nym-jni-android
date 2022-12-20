// Copyright 2021 - Nym Technologies SA <contact@nymtech.net>
// SPDX-License-Identifier: Apache-2.0

// Note to nym-jni maintainers: must manually synchronise with nym-client

use client_core::config::Config as BaseConfig;
pub use client_core::config::MISSING_VALUE;
use config::defaults::DEFAULT_WEBSOCKET_LISTENING_PORT;
use config::NymConfig;
use serde::{Deserialize, Serialize};
use std::path::PathBuf;

mod template;
use self::template::config_template;
pub const STORAGE_ABS_PATH_FROM_JAVA_COM_KAEONX_NYMANDROIDPORT_JNI_NYMHANDLERKT_NYMINITIMPL_FALLIBLE:
    &str = "ANDROIDCONFIG_STORAGE_ABS_PATH";

#[derive(Debug, Deserialize, PartialEq, Eq, Serialize, Clone, Copy)]
#[serde(deny_unknown_fields)]
pub enum SocketType {
    WebSocket,
    None,
}

impl SocketType {
    #[allow(dead_code)] // TODO: Remove after full implementation
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

    #[allow(clippy::expect_used)] // nym also uses expect
    fn default_root_directory() -> PathBuf {
        PathBuf::from(
            std::env::var(STORAGE_ABS_PATH_FROM_JAVA_COM_KAEONX_NYMANDROIDPORT_JNI_NYMHANDLERKT_NYMINITIMPL_FALLIBLE).unwrap_or_else(|_| {
                panic!(
                    "Failed to get {}. Is the environment variable not set?",
                    STORAGE_ABS_PATH_FROM_JAVA_COM_KAEONX_NYMANDROIDPORT_JNI_NYMHANDLERKT_NYMINITIMPL_FALLIBLE
                )
            }),
        )
        .join(".nym")
        .join("clients")
    }

    fn try_default_root_directory() -> Option<PathBuf> {
        std::env::var(
            STORAGE_ABS_PATH_FROM_JAVA_COM_KAEONX_NYMANDROIDPORT_JNI_NYMHANDLERKT_NYMINITIMPL_FALLIBLE,
        )
        .ok()
        .map(|path| PathBuf::from(path).join(".nym").join("clients"))
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
    #[allow(clippy::default_trait_access)] // I avoid reformatting nym code as far as possible
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

    #[allow(dead_code)] // TODO: Remove after full implementation

    pub fn get_socket_type(&self) -> SocketType {
        self.socket.socket_type
    }

    #[allow(dead_code)] // TODO: Remove after full implementation

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
