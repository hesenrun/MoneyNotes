package com.bqmz001.moneynotes.util;

import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.widget.Toast;

import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class FingerPrintUtil {
    private static KeyStore keyStore;
    private static final String DEFAULT_KEY_NAME = "default_key";

    public static boolean supportFingerprint(Context context) {
        if (Build.VERSION.SDK_INT < 23) {
            ToastUtil.show(ToastUtil.VERSION_TOO_LOW);
            return false;
        } else {
            KeyguardManager keyguardManager = context.getApplicationContext().getSystemService(KeyguardManager.class);
            FingerprintManager fingerprintManager = context.getApplicationContext().getSystemService(FingerprintManager.class);
            if (!fingerprintManager.isHardwareDetected()) {
                ToastUtil.show(ToastUtil.DONOT_SUPPORT_FINGERPRINT);
                return false;
            } else if (!keyguardManager.isKeyguardSecure()) {
                ToastUtil.show(ToastUtil.NOT_LOCKSCREEN);
                return false;
            } else if (!fingerprintManager.hasEnrolledFingerprints()) {
                ToastUtil.show(ToastUtil.NOT_FINGERPRINT);
                return false;
            }
        }
        return true;
    }

    @TargetApi(23)
    private static void initKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(DEFAULT_KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);
            keyGenerator.init(builder.build());
            keyGenerator.generateKey();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @TargetApi(23)
    private static Cipher initCipher() {
        try {
            SecretKey key = (SecretKey) keyStore.getKey(DEFAULT_KEY_NAME, null);
            Cipher cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Cipher getCipher() {
        initKey();
        return initCipher();
    }

}
