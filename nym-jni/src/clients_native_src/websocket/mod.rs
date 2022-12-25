/*
 * nym/clients/native/src/websocket/mod.rs
 *
 * Essentially the same as the above file (from the nym crate), minus some minor adjustments to make
 * it fit into nym_jni.
 *
 * This file is copied over because it hides its components with `pub(crate)`, which cannot be
 * accessed from nym_jni otherwise.
 */

// Copyright 2021 - Nym Technologies SA <contact@nymtech.net>
// SPDX-License-Identifier: Apache-2.0

// ? Copied wholesale, except `pub(crate) -> pub`
pub use handler::Handler;
pub use listener::Listener;

// ? Copied wholesale
pub(crate) mod handler;
pub(crate) mod listener;
