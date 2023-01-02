/*
 * nym/clients/native/src/websocket/mod.rs
 *
 * Adapted from the above file (from the nym crate) to fit Android ecosystem.
 *
 * This file is copied over and adapted because in the nym crate, it hides Handler and Listener with
 * `pub(crate)`, thus they cannot be accessed from nym_jni.
 */

// Copyright 2021 - Nym Technologies SA <contact@nymtech.net>
// SPDX-License-Identifier: Apache-2.0

// ? Copied wholesale, except `pub(crate)` -> `pub`
pub use handler::Handler;
pub use listener::Listener;

// ? Copied wholesale
pub(crate) mod handler;
pub(crate) mod listener;
