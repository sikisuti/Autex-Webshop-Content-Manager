package org.autex.util;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.autex.util.Secure.decrypt;
import static org.autex.util.Secure.encrypt;

public class SecureTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(SecureTest.class);

    @Test
    public void givenPassword_whenEncrypt_thenSuccess() throws Exception {
        String OUTPUT_FORMAT = "%-30s:%s";
        String PASSWORD = "this is a password";
        String pText = "AES-GSM Password-Bases encryption!";

        String encryptedTextBase64 = encrypt(pText.getBytes(UTF_8), PASSWORD);

        LOGGER.info("\n------ AES GCM Password-based Encryption ------");
        LOGGER.info(String.format(OUTPUT_FORMAT, "Input (plain text)", pText));
        LOGGER.info(String.format(OUTPUT_FORMAT, "Encrypted (base64) ", encryptedTextBase64));

        LOGGER.info("\n------ AES GCM Password-based Decryption ------");
        LOGGER.info(String.format(OUTPUT_FORMAT, "Input (base64)", encryptedTextBase64));

        String decryptedText = decrypt(encryptedTextBase64, PASSWORD);
        LOGGER.info(String.format(OUTPUT_FORMAT, "Decrypted (plain text)", decryptedText));

        Assert.assertEquals(pText, decryptedText);
    }


}
