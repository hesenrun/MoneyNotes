package com.bqmz001.moneynotes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.widget.Toast;

import com.bqmz001.moneynotes.private_ui.FingerPrintDialog;
import com.bqmz001.moneynotes.util.FingerPrintUtil;
import com.bqmz001.moneynotes.util.ToastUtil;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.crypto.Cipher;

public class SplashActivity extends BaseActivity {
    SharedPreferences pref;
    FingerPrintDialog dialog;
    Cipher cipher;
    boolean isAppRuning;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        pref = PreferenceManager.getDefaultSharedPreferences(SplashActivity.this);
        final boolean b = pref.getBoolean("FingerPrint", false);

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (b == true) {
                            cipher = FingerPrintUtil.getCipher();
                            dialog = new FingerPrintDialog(SplashActivity.this, cipher);
                            dialog.setEvent(new FingerPrintDialog.Event() {
                                @Override
                                public void success(FingerprintManager.AuthenticationResult result) {
                                    ToastUtil.show(ToastUtil.SUCCESS_AUTH);
                                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                    finish();
                                }

                                @Override
                                public void failed() {

                                }

                                @Override
                                public void anomalous(int helpCode, CharSequence helpString) {
                                    ToastUtil.showContent(helpString.toString());
                                }

                                @Override
                                public void lock(int errorCode, CharSequence errString) {
                                    ToastUtil.showContent(errString.toString());
                                    finish();
                                }

                                @Override
                                public void cancelSelf(int errorCode, CharSequence errString) {
                                    ToastUtil.showContent(errString.toString());
                                    finish();
                                }
                            });
                            dialog.show();
                        } else {
                            startActivity(new Intent(SplashActivity.this, MainActivity.class));
                            finish();
                        }
                    }
                });
            }
        };
        timer.schedule(timerTask, 1500);
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (dialog != null)
            dialog.hide();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (dialog != null)
            dialog.show();
    }
}
