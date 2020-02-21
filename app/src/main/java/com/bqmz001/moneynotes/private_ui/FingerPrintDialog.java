package com.bqmz001.moneynotes.private_ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.bqmz001.moneynotes.R;
import com.bqmz001.moneynotes.util.ToastUtil;

import javax.crypto.Cipher;

public class FingerPrintDialog extends Dialog {

    Context context;
    FingerprintManager fingerprintManager;
    CancellationSignal mCancellationSignal;
    Cipher cipher;
    boolean isSelfCancelled;
    TextView textView;
    Event event;


    public FingerPrintDialog(@NonNull Context context, @NonNull Cipher cipher) {
        super(context);
        this.context = context;
        this.cipher = cipher;
    }

    public interface Event {
        void success(FingerprintManager.AuthenticationResult result);

        void failed();

        void anomalous(int helpCode, CharSequence helpString);

        void lock(int errorCode, CharSequence errString);

        void cancelSelf(int errorCode, CharSequence errString);
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    @Override
    public void hide() {
        super.hide();
        stopListening();
    }

    @Override
    public void show() {
        super.show();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            startListening(cipher);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_fingerprint);

        WindowManager windowManager = ((Activity) context).getWindowManager();
        final Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = display.getWidth() * 2 / 3;// 设置dialog宽度为屏幕的4/5
        getWindow().setAttributes(lp);
        setCanceledOnTouchOutside(false);//点击外部Dialog消失
        fingerprintManager = getContext().getSystemService(FingerprintManager.class);
        textView = findViewById(R.id.cancel);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopListening();
                dismiss();
            }
        });
    }



    @Override
    protected void onStart() {
        super.onStart();

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void startListening(Cipher cipher) {
        isSelfCancelled = false;
        mCancellationSignal = new CancellationSignal();
        fingerprintManager.authenticate(new FingerprintManager.CryptoObject(cipher), mCancellationSignal, 0, new FingerprintManager.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                if (!isSelfCancelled) {
                    //锁定
                    if (errorCode == FingerprintManager.FINGERPRINT_ERROR_LOCKOUT) {
                        event.lock(errorCode, errString);
                        ToastUtil.showContent("请"+errString.toString()+"后重试");
                        stopListening();
                        dismiss();
                    }

                }
                //自行退出
                else {
                    event.cancelSelf(errorCode, errString);
                    stopListening();
                    dismiss();
                }
            }

            //异常
            @Override
            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                event.anomalous(helpCode, helpString);


            }

            //成功
            @Override
            public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                event.success(result);
                stopListening();
                dismiss();
            }

            //失败
            @Override
            public void onAuthenticationFailed() {
                event.failed();
            }
        }, null);
    }

    private void stopListening() {
        if (mCancellationSignal != null) {
            mCancellationSignal.cancel();
            mCancellationSignal = null;
            isSelfCancelled = true;
        }
    }

}
