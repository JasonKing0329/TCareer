package com.king.app.tcareer.page.player.manage;

import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.IFragmentHolder;
import com.king.app.tcareer.model.db.entity.PlayerBean;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.utils.PinyinUtil;
import com.king.app.tcareer.view.dialog.DraggableDialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @desc
 * @auth 景阳
 * @time 2018/1/31 0031 11:53
 */

public class PlayerEditDialog extends DraggableDialogFragment {

    private EditFragment editFragment;
    private PlayerBean playerBean;
    private User user;
    private OnPlayerEditListener onPlayerEditListener;

    @Override
    protected View getToolbarView(ViewGroup groupToolbar) {
        requestOkAction();
        requestCloseAction();
        setTitle("New player");
        return null;
    }

    @Override
    protected Fragment getContentViewFragment() {
        editFragment = new EditFragment();
        editFragment.setPlayerBean(playerBean);
        editFragment.setUser(user);
        editFragment.setOnPlayerEditListener(onPlayerEditListener);
        return editFragment;
    }
    
    public void setPlayerBean(PlayerBean bean) {
        this.playerBean = bean;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    protected boolean onClickOk() {
        return editFragment.onSave();
    }

    public void setOnPlayerEditListener(OnPlayerEditListener onPlayerEditListener) {
        this.onPlayerEditListener = onPlayerEditListener;
    }

    public static class EditFragment extends ContentFragment {

        @BindView(R.id.et_name)
        EditText etName;
        @BindView(R.id.et_name_eng)
        EditText etNameEng;
        @BindView(R.id.et_country)
        EditText etCountry;
        @BindView(R.id.et_city)
        EditText etCity;
        @BindView(R.id.et_birthday)
        EditText etBirthday;

        private EditPresenter presenter;
        
        private PlayerBean playerBean;
        private User user;
        private OnPlayerEditListener onPlayerEditListener;

        @Override
        protected int getContentLayoutRes() {
            return R.layout.dialog_player_manage;
        }

        @Override
        protected void onCreate(View view) {
            ButterKnife.bind(this, view);

            presenter = new EditPresenter();
            if (user != null) {
                etName.setText(user.getNameChn());
                etNameEng.setText(user.getNameEng());
                etCountry.setText(user.getCountry());
                etCity.setText(user.getCity());
                etBirthday.setText(user.getBirthday());
            }
            else if (playerBean != null) {
                etName.setText(playerBean.getNameChn());
                etNameEng.setText(playerBean.getNameEng());
                etCountry.setText(playerBean.getCountry());
                etCity.setText(playerBean.getCity());
                etBirthday.setText(playerBean.getBirthday());
            }
        }

        @Override
        protected void bindChildFragmentHolder(IFragmentHolder holder) {

        }

        public void setPlayerBean(PlayerBean playerBean) {
            this.playerBean = playerBean;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public boolean onSave() {
            String name = etName.getText().toString();
            if (TextUtils.isEmpty(name)) {
                etName.setError("Name can't be null");
                return false;
            }
            String country = etCountry.getText().toString();
            if (TextUtils.isEmpty(country)) {
                etCountry.setError("Country can't be null");
                return false;
            }

            String city = etCity.getText().toString();
            String birthday = etBirthday.getText().toString();
            String engName = etNameEng.getText().toString();
            // 修改
            if (user != null || playerBean != null) {
                if (user != null) {
                    user.setBirthday(birthday);
                    user.setCountry(country);
                    user.setCity(city);
                    user.setNameChn(name);
                    user.setNameEng(engName);
                    user.setNamePinyin(PinyinUtil.getPinyin(name));
                    presenter.updateUser(user);
                    if (onPlayerEditListener != null) {
                        onPlayerEditListener.onUserUpdated(user);
                    }
                }
                else {
                    playerBean.setBirthday(birthday);
                    playerBean.setCountry(country);
                    playerBean.setCity(city);
                    playerBean.setNameChn(name);
                    playerBean.setNameEng(engName);
                    playerBean.setNamePinyin(PinyinUtil.getPinyin(name));
                    presenter.updatePlayer(playerBean);
                    if (onPlayerEditListener != null) {
                        onPlayerEditListener.onPlayerUpdated(playerBean);
                    }
                }
            }
            // 添加
            else {
                PlayerBean bean = new PlayerBean();
                bean.setBirthday(birthday);
                bean.setCountry(country);
                bean.setCity(city);
                bean.setNameChn(name);
                bean.setNameEng(engName);
                bean.setNamePinyin(PinyinUtil.getPinyin(name));
                presenter.insertPlayer(bean);
                if (onPlayerEditListener != null) {
                    onPlayerEditListener.onPlayerAdded();
                }
            }
            return true;
        }

        public void setOnPlayerEditListener(OnPlayerEditListener onPlayerEditListener) {
            this.onPlayerEditListener = onPlayerEditListener;
        }
    }

    public interface OnPlayerEditListener {
        void onPlayerAdded();
        void onPlayerUpdated(PlayerBean bean);
        void onUserUpdated(User user);
    }
}
