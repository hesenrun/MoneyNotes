package com.bqmz001.moneynotes;

import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.bqmz001.moneynotes.private_ui.FingerPrintDialog;
import com.bqmz001.moneynotes.util.FingerPrintUtil;
import com.bqmz001.moneynotes.util.ToastUtil;


import javax.crypto.Cipher;


public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("设置");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        SwitchPreferenceCompat fingerPrint, zuAnMode;
        Cipher cipher;
        FingerPrintDialog dialog;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            fingerPrint = findPreference("FingerPrint");
            fingerPrint.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, final Object newValue) {
                    if (FingerPrintUtil.supportFingerprint(getContext())) {
                        final boolean c = (boolean) newValue;
                        cipher = FingerPrintUtil.getCipher();
                        dialog = new FingerPrintDialog(getContext(), cipher);
                        dialog.setEvent(new FingerPrintDialog.Event() {
                            @Override
                            public void success(FingerprintManager.AuthenticationResult result) {
                                if (c == true) {
                                    fingerPrint.setChecked(true);
                                } else {
                                    fingerPrint.setChecked(false);
                                }

                            }

                            @Override
                            public void failed() {
                                if (c == true)
                                    fingerPrint.setChecked(false);
                                else
                                    fingerPrint.setChecked(true);
                            }

                            @Override
                            public void anomalous(int helpCode, CharSequence helpString) {
                                if (c == true)
                                    fingerPrint.setChecked(false);
                                else
                                    fingerPrint.setChecked(true);
                            }

                            @Override
                            public void lock(int errorCode, CharSequence errString) {
                                if (c == true)
                                    fingerPrint.setChecked(false);
                                else
                                    fingerPrint.setChecked(true);
                            }

                            @Override
                            public void cancelSelf(int errorCode, CharSequence errString) {
                                if (c == true)
                                    fingerPrint.setChecked(false);
                                else
                                    fingerPrint.setChecked(true);
                            }
                        });
                        dialog.show();


                    } else {
                        return false;
                    }
                    return true;
                }
            });
            zuAnMode = findPreference("ZuAnMode");
            zuAnMode.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    ToastUtil.refreshSwitch((boolean) newValue);
                    return true;
                }
            });


        }


        @Override
        public void onPause() {
            super.onPause();
            if (dialog != null)
                dialog.hide();
        }

        @Override
        public void onResume() {
            super.onResume();

        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}