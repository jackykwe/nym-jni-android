/*
 * nym/clients/native/src/websocket/mod.rs
 *
 * Essentially the same as the above file (from the nym crate).
 *
 * This file is copied over and adapted because in the nym crate, it hides Handler and Listener with
 * `pub(crate)`, thus they cannot be accessed from nym_jni. By copying this file over to nym_jni,
 * nym_jni is no longer an outsider, and is now part of the `pub(crate)` scope.
 */

// Copyright 2021 - Nym Technologies SA <contact@nymtech.net>
// SPDX-License-Identifier: Apache-2.0

// ? Copied wholesale
pub(crate) use handler::Handler;
pub(crate) use listener::Listener;

// ? Copied wholesale
pub(crate) mod handler;
pub(crate) mod listener;
