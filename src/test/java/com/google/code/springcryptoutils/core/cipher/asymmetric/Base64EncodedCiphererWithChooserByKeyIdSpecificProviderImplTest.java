package com.google.code.springcryptoutils.core.cipher.asymmetric;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.UnsupportedEncodingException;
import java.security.Security;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class Base64EncodedCiphererWithChooserByKeyIdSpecificProviderImplTest {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Autowired
    private Base64EncodedCiphererWithChooserByKeyId encrypter;

    @Autowired
    private Base64EncodedCiphererWithChooserByKeyId decrypter;

    @Before
    public void setup() {
        assertNotNull(encrypter);
        assertNotNull(decrypter);
    }

    @Test
    public void testEncryptionBothWays() throws UnsupportedEncodingException {
        final String message = "this is a top-secret message";
        String b64encryptedMessage = encrypter.encrypt("privateKeyId", message);
        assertNotNull(b64encryptedMessage);
        String decryptedMessage = decrypter.encrypt("publicKeyId", b64encryptedMessage);
        assertNotNull(decryptedMessage);
        assertEquals(message, decryptedMessage);
    }

    @Test
    public void testEncryptionBothWaysInALoop() throws UnsupportedEncodingException {
        for (int i = 0; i < 100; i++) {
            final String message = UUID.randomUUID().toString();
            String b64encryptedMessage = encrypter.encrypt("privateKeyId", message);
            assertNotNull(b64encryptedMessage);
            String decryptedMessage = decrypter.encrypt("publicKeyId", b64encryptedMessage);
            assertNotNull(decryptedMessage);
            assertEquals(message, decryptedMessage);
        }
    }

    @Test
    public void testDecryptionWithGarbageFailsNot() throws UnsupportedEncodingException {
        // for some reason, the BC provider differs from the Sun native
        // implementation in that it doesn't fail when you throw garbage at it
        // in decrypt mode, which makes sense and is better behavior than
        // the native provider
        final String message = "this is garbage";
        decrypter.encrypt("publicKeyId", message);
    }

}