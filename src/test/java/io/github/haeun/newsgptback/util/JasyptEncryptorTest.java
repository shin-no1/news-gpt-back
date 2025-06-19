package io.github.haeun.newsgptback.util;

import io.github.cdimascio.dotenv.Dotenv;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.jupiter.api.Test;

public class JasyptEncryptorTest {
    @Test
    void testEncryptPassword() {
        String plainPassword = "ENCTYPTED";

        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(Dotenv.load().get("DB_JASYPT_KEY"));
        encryptor.setAlgorithm("PBEWithMD5AndDES");

        String encrypted = encryptor.encrypt(plainPassword);
        System.out.println("Encrypted: ENC(" + encrypted + ")");
    }
}