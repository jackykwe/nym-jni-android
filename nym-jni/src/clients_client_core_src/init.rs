/*
 * nym/clients/native/src/client/config/mod.rs
 *
 * Adapted from the above file (from the nym crate) to fit Android ecosystem.
 *
 * This file is copied over because of the need to change println!() statement to log::info!().
 * In the Android ecosystem, stdout (where println!() statements get sent to) are ignored.
 */

use client_core::{
    config::{persistence::key_pathfinder::ClientKeyPathfinder, Config},
    error::ClientCoreError,
};
use crypto::asymmetric::{encryption, identity};
use nymsphinx::addressing::{clients::Recipient, nodes::NodeIdentity};
use tap::TapFallible;

// ? Adapted to fit Android ecosystem: returns Result<String, _> instead of Result<(), _>
pub fn show_address<T>(config: &Config<T>) -> Result<String, ClientCoreError>
where
    T: config::NymConfig,
{
    fn load_identity_keys(
        pathfinder: &ClientKeyPathfinder,
    ) -> Result<identity::KeyPair, ClientCoreError> {
        let identity_keypair: identity::KeyPair =
            pemstore::load_keypair(&pemstore::KeyPairPath::new(
                pathfinder.private_identity_key().to_owned(),
                pathfinder.public_identity_key().to_owned(),
            ))
            .tap_err(|_| log::error!("Failed to read stored identity key files"))?;
        Ok(identity_keypair)
    }

    fn load_sphinx_keys(
        pathfinder: &ClientKeyPathfinder,
    ) -> Result<encryption::KeyPair, ClientCoreError> {
        let sphinx_keypair: encryption::KeyPair =
            pemstore::load_keypair(&pemstore::KeyPairPath::new(
                pathfinder.private_encryption_key().to_owned(),
                pathfinder.public_encryption_key().to_owned(),
            ))
            .tap_err(|_| log::error!("Failed to read stored sphinx key files"))?;
        Ok(sphinx_keypair)
    }

    let pathfinder = ClientKeyPathfinder::new_from_config(config);
    let identity_keypair = load_identity_keys(&pathfinder)?;
    let sphinx_keypair = load_sphinx_keys(&pathfinder)?;

    let client_recipient = Recipient::new(
        *identity_keypair.public_key(),
        *sphinx_keypair.public_key(),
        // TODO: below only works under assumption that gateway address == gateway id
        // (which currently is true)
        NodeIdentity::from_base58_string(config.get_gateway_id())?,
    );

    log::info!("\nThe address of this client is: {}", client_recipient);
    Ok(client_recipient.to_string())
}
