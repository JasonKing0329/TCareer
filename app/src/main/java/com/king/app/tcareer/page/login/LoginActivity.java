package com.king.app.tcareer.page.login;

import android.Manifest;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.BaseMvpActivity;
import com.king.app.tcareer.page.player.manage.PlayerManageActivity;
import com.king.app.tcareer.utils.DBExportor;
import com.tbruyelle.rxpermissions2.RxPermissions;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * login page
 */
public class LoginActivity extends BaseMvpActivity<LoginPresenter> implements LoginView {

    @BindView(R.id.et_pwd)
    EditText etPwd;
    @BindView(R.id.group_login)
    LinearLayout groupLogin;

    private FingerPrintController fingerPrint;

    @Override
    protected int getContentView() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
        groupLogin.setVisibility(View.INVISIBLE);
    }

    @Override
    protected LoginPresenter createPresenter() {
        return new LoginPresenter();
    }

    @Override
    protected void initData() {
        new RxPermissions(this)
                .request(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean isGranted) throws Exception {
                        if (isGranted) {
                            initCreate();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        finish();
                    }
                });

    }

    private void initCreate() {
        // 每次进入导出一次数据库
        DBExportor.execute();
        presenter.prepare();
    }

    @Override
    public void showLoginFrame() {
        groupLogin.setVisibility(View.VISIBLE);
    }

    @Override
    public void showFingerPrint() {
        fingerPrint = new FingerPrintController(this);
        if (fingerPrint.isSupported()) {
            if (fingerPrint.hasRegistered()) {
                startFingerPrintDialog();
            } else {
                showMessage("设备未注册指纹");
            }
            return;
        } else {
            showMessage("设备不支持指纹识别");
        }
    }

    private void startFingerPrintDialog() {
        if (fingerPrint.hasRegistered()) {
            boolean withPW = false;
            fingerPrint.showIdentifyDialog(withPW, new FingerPrintController.SimpleIdentifyListener() {

                @Override
                public void onSuccess() {
                    permitLogin();
                }

                @Override
                public void onFail() {

                }

                @Override
                public void onCancel() {
                    finish();
                }
            });
        } else {
            showMessage(getString(R.string.login_finger_not_register));
        }
    }

    @OnClick(R.id.btn_login)
    public void onViewClicked() {
        presenter.checkPassword(etPwd.getText().toString());
    }

    @Override
    public void permitLogin() {
        Intent intent = new Intent(this, PlayerManageActivity.class);
        startActivity(intent);
    }
}
