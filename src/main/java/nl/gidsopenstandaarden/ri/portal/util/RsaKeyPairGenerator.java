/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright Headease B.V. (c) 2020.
 */

package nl.gidsopenstandaarden.ri.portal.util;

import java.security.KeyPair;

public class RsaKeyPairGenerator {

    public static void main(String[] args) throws Exception {
        KeyPair keyPair = KeyUtils.generateKeyPair();
        // Output the public key as base64
        String publicK = KeyUtils.encodeKey(keyPair.getPublic());
        // Output the private key as base64
        String privateK = KeyUtils.encodeKey(keyPair.getPrivate());

        System.out.println(publicK);
        System.out.println(privateK);
    }


}
