package com.bqmz001.moneynotes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.bqmz001.moneynotes.private_ui.FingerPrintDialog;
import com.bqmz001.moneynotes.util.EventUtil;
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
        SwitchPreferenceCompat fingerPrint, zuAnMode,noti;
        Preference about,bty;
        Cipher cipher;
        FingerPrintDialog dialog;
        PowerManager pm;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            pm=(PowerManager)getContext().getSystemService(Context.POWER_SERVICE);
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

            about=findPreference("ab");
            about.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    return false;
                }
            });


            noti=findPreference("Notification");
            noti.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    EventUtil.postEvent(1,"notification",((boolean)newValue)+"");
                    noti.setChecked((boolean)newValue);
                    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M&&noti.isChecked()&&pm.isIgnoringBatteryOptimizations(getContext().getPackageName())==false){
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                                .setTitle("电池优化")
                                .setMessage("在Android6.0及以上，需要将应用加入电池优化白名单，才能保证通知栏数据的实时性和准确性。\n\n要将应用加入电池优化白名单吗？\n\n如果没能添加，可以随时在设置中设置。")
                                .setNegativeButton("朕已阅", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setPositiveButton("朕去看看", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (pm.isIgnoringBatteryOptimizations(getContext().getPackageName())==false){
                                            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                                            intent.setData(Uri.parse("package:" + getContext().getPackageName()));
                                            startActivity(intent);

                                        }
                                        dialog.dismiss();

                                    }
                                });
                        builder.create().show();
                    }
                    return false;
                }
            });

            bty=findPreference("IngoreBatteryOptimizations");
            bty.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                      if (pm.isIgnoringBatteryOptimizations(getContext().getPackageName())==false){
                          Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                          intent.setData(Uri.parse("package:" + getContext().getPackageName()));
                          startActivity(intent);
                      }else {
                          ToastUtil.showContent("已经在白名单了......");
                      }
                    }else {
                        ToastUtil.showContent("这个系统版本应该用不着......");
                    }
                    return false;
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