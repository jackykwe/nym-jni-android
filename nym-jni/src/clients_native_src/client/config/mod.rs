/*
 * nym/clients/native/src/client/config/mod.rs
 *
 * Adapted from the above file (from the nym crate) to fit Android architecture.
 *
 * This file is copied over because it is hidden in the actual nym crate via `pub(crate)` and cannot
 * be accessed from nym_jni otherwise.
 */

// I avoid reformatting nym code as far as possible
#![allow(clippy::default_trait_access)]
#![allow(clippy::expect_used)]
#![allow(clippy::module_name_repetitions)]

// Copyright 2021 - Nym Technologies SA <contact@nymtech.net>
// SPDX-License-Identifier: Apache-2.0

use crate::clients_native_src::client::config::template::config_template;
use client_core::config::Config as BaseConfig;
use config::defaults::DEFAULT_WEBSOCKET_LISTENING_PORT;
use config::NymConfig;
use serde::{Deserialize, Serialize};
use std::path::PathBuf;

mod template;

// Value (environment variable key) follows the impl block below
pub const STORAGE_ABS_PATH_ENVVARKEY: &str = "IMPL_NYMCONFIG_FOR_CONFIGANDROID_STORAGE_ABS_PATH";

// ? Copied wholesale
#[derive(Debug, Deserialize, PartialEq, Eq, Serialize, Clone, Copy)]
#[serde(deny_unknown_fields)]
pub enum SocketType {
    WebSocket,
    None,
}

// ? Copied wholesale
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

// ? Adapted to fit Android architecture
#[derive(Debug, Default, Deserialize, PartialEq, Serialize)]
#[serde(deny_unknown_fields)]
pub struct ConfigAndroid {
    #[serde(flatten)]
    base: BaseConfig<ConfigAndroid>,

    socket: Socket,
}

// ? Adapted to fit Android architecture
impl NymConfig for ConfigAndroid {
    fn template() -> &'static str {
        config_template()
    }

    fn default_root_directory() -> PathBuf {
        PathBuf::from(
            std::env::var(STORAGE_ABS_PATH_ENVVARKEY).unwrap_or_else(|_| {
                panic!(
                    "Failed to get {}. Is the environment variable not set?",
                    STORAGE_ABS_PATH_ENVVARKEY
                )
            }),
        )
        .join(".nym")
        .join("clients")
    }

    fn try_default_root_directory() -> Option<PathBuf> {
        std::env::var(STORAGE_ABS_PATH_ENVVARKEY)
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

// ? Copied wholesale, except renamed `Config` -> `ConfigAndroid`
impl ConfigAndroid {
    pub fn new<S: Into<String>>(id: S) -> Self {
        ConfigAndroid {
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

// ? Copied wholesale
#[derive(Debug, Deserialize, PartialEq, Eq, Serialize)]
#[serde(deny_unknown_fields)]
pub struct Socket {
    socket_type: SocketType,
    listening_port: u16,
}

// ? Copied wholesale
impl Default for Socket {
    fn default() -> Self {
        Socket {
            socket_type: SocketType::WebSocket,
            listening_port: DEFAULT_WEBSOCKET_LISTENING_PORT,
        }
    }
}
