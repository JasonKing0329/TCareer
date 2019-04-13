package com.king.app.tcareer.view.dialog;

import android.hardware.fingerprint.FingerprintManager;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.king.app.tcareer.R;
import com.king.app.tcareer.utils.DebugLog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/3/1 14:30
 */
public class FingerprintDialog extends BaseDialogFragment {

    @BindView(R.id.iv_fingerprint)
    ImageView ivFingerprint;
    @BindView(R.id.tv_msg)
    TextView tvMsg;

    Unbinder unbind;

    private OnFingerPrintListener onFingerPrintListener;

    public void setOnFingerPrintListener(OnFingerPrintListener onFingerPrintListener) {
        this.onFingerPrintListener = onFingerPrintListener;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 在xml里或initView里调用不起作用
        setWidth(getResources().getDimensionPixelSize(R.dimen.dlg_fingerprint_width));
        setHeight(getResources().getDimensionPixelSize(R.dimen.dlg_fingerprint_height));
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_fingerprint;
    }

    @Override
    protected void initView(View view) {
        unbind = ButterKnife.bind(this, view);
        FingerprintManagerCompat fingerprint = FingerprintManagerCompat.from(getActivity());
        fingerprint.authenticate(null, 0, new CancellationSignal()
                , new FingerprintManagerCompat.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errMsgId, CharSequence errString) {
                        super.onAuthenticationError(errMsgId, errString);
                        DebugLog.e("errMsgId=" + errMsgId + " msg=" + errString.toString());
                        handleErrorCode(errMsgId, errString.toString());
                    }

                    @Override
                    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
                        super.onAuthenticationHelp(helpMsgId, helpString);
                        DebugLog.e("helpMsgId=" + helpMsgId + " msg=" + helpString.toString());
                        tvMsg.setText(helpString.toString());
                    }

                    @Override
                    public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        dismissAllowingStateLoss();
                        onFingerPrintListener.onPass();
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        tvMsg.setText("识别错误，请重试");
                    }
                }
                , null);
    }

    private void handleErrorCode(int code, String msg) {
        DebugLog.e("code=" + code);
        switch (code) {
            case FingerprintManager.FINGERPRINT_ERROR_CANCELED:
                // 指纹传感器不可用，该操作被取消
                break;
            case FingerprintManager.FINGERPRINT_ERROR_HW_UNAVAILABLE:
                // 当前设备不可用，请稍后再试
                tvMsg.setText(msg);
                break;
            case FingerprintManager.FINGERPRINT_ERROR_LOCKOUT:
                // 由于太多次尝试失败导致被锁，该操作被取消
                tvMsg.setText(msg);
                break;
            case FingerprintManager.FINGERPRINT_ERROR_NO_SPACE:
                // 没有足够的存储空间保存这次操作，该操作不能完成
                tvMsg.setText(msg);
                break;
            case FingerprintManager.FINGERPRINT_ERROR_TIMEOUT:
                // 操作时间太长，一般为30秒
                tvMsg.setText(msg);
                break;
            case FingerprintManager.FINGERPRINT_ERROR_UNABLE_TO_PROCESS:
                // 传感器不能处理当前指纹图片
                tvMsg.setText(msg);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbind.unbind();
    }

    public interface OnFingerPrintListener {
        void onPass();
    }
}
