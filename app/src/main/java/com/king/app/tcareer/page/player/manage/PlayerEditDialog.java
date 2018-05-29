package com.king.app.tcareer.page.player.manage;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.BaseView;
import com.king.app.tcareer.base.IFragmentHolder;
import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.model.bean.CompetitorBean;
import com.king.app.tcareer.model.db.entity.PlayerAtpBean;
import com.king.app.tcareer.model.db.entity.PlayerAtpBeanDao;
import com.king.app.tcareer.model.db.entity.PlayerBean;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.page.player.atp.AtpManageActivity;
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
    private CompetitorBean competitorBean;
    private OnPlayerEditListener onPlayerEditListener;
    private String customTitle;

    @Override
    protected View getToolbarView(ViewGroup groupToolbar) {
        requestOkAction();
        requestCloseAction();
        if (customTitle == null) {
            setTitle("New player");
        }
        else {
            setTitle(customTitle);
        }
        return null;
    }

    @Override
    protected Fragment getContentViewFragment() {
        editFragment = new EditFragment();
        editFragment.setCompetitorBean(competitorBean);
        editFragment.setOnPlayerEditListener(onPlayerEditListener);
        return editFragment;
    }

    public void setCompetitorBean(CompetitorBean competitorBean) {
        this.competitorBean = competitorBean;
    }

    @Override
    protected boolean onClickOk() {
        return editFragment.onSave();
    }

    public void setOnPlayerEditListener(OnPlayerEditListener onPlayerEditListener) {
        this.onPlayerEditListener = onPlayerEditListener;
    }

    public void setCustomTitle(String customTitle) {
        this.customTitle = customTitle;
    }

    public static class EditFragment extends ContentFragment implements PlayerEditView {

        private final int REQUEST_SELECT_ATP = 101;

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
        @BindView(R.id.tv_atp_id)
        TextView tvAtpId;
        @BindView(R.id.tv_atp_conclude)
        TextView tvAtpConclude;
        @BindView(R.id.iv_update)
        ImageView ivUpdate;

        private EditPresenter presenter;

        private CompetitorBean competitorBean;
        private OnPlayerEditListener onPlayerEditListener;

        private String atpId;

        @Override
        protected int getContentLayoutRes() {
            return R.layout.dialog_player_manage;
        }

        public void setCompetitorBean(CompetitorBean competitorBean) {
            this.competitorBean = competitorBean;
        }

        @Override
        protected void onCreate(View view) {
            ButterKnife.bind(this, view);

            presenter = new EditPresenter();
            presenter.onAttach(this);

            if (competitorBean != null) {
                atpId = competitorBean.getAtpId();
            }
            ivUpdate.setVisibility(atpId == null ? View.GONE:View.VISIBLE);

            if (competitorBean != null) {
                etName.setText(competitorBean.getNameChn());
                etNameEng.setText(competitorBean.getNameEng());
                etCountry.setText(competitorBean.getCountry());
                etCity.setText(competitorBean.getCity());
                etBirthday.setText(competitorBean.getBirthday());
                if (competitorBean.getAtpBean() != null) {
                    atpId = competitorBean.getAtpId();
                    updateByAtpBean(competitorBean.getAtpBean());
                }
            }
            tvAtpId.setOnClickListener(v -> {
                Intent intent = new Intent().setClass(getContext(), AtpManageActivity.class);
                intent.putExtra(AtpManageActivity.EXTRA_SELECT, true);
                startActivityForResult(intent, REQUEST_SELECT_ATP);
            });

            ivUpdate.setOnClickListener(v -> presenter.updateAtpData(atpId));
        }

        private void updateByAtpBean(PlayerAtpBean atpBean) {
            tvAtpId.setText("Atp id: " + atpBean.getId());
            etNameEng.setText(atpBean.getName());
            StringBuffer buffer = new StringBuffer();
            buffer.append(atpBean.getOverViewUrl());
            // 更新过详细信息
            if (atpBean.getLastUpdateDate() > 0) {
                etCountry.setText(atpBean.getBirthCountry());

                if (!TextUtils.isEmpty(atpBean.getBirthCity())) {
                    etCity.setText(atpBean.getBirthCity());
                }
                if (!TextUtils.isEmpty(atpBean.getBirthday())) {
                    etBirthday.setText(atpBean.getBirthday());
                }

                buffer.append("\n").append("Residence: ");
                if (!TextUtils.isEmpty(atpBean.getResidenceCity())) {
                    buffer.append(atpBean.getResidenceCity()).append(", ");
                }
                buffer.append(atpBean.getResidenceCountry()).append("\n");
                buffer.append("Plays: ").append(atpBean.getPlays()).append("\n");
                buffer.append("Turned Pro: ").append(atpBean.getTurnedPro()).append("\n");
                buffer.append("Coach: ").append(atpBean.getCoach()).append("\n");
                buffer.append("Career highest rank: ").append(atpBean.getCareerHighSingle()).append(", ").append(atpBean.getCareerHighSingleDate()).append("\n");
                buffer.append("Year: ").append(atpBean.getYearWin()).append("-").append(atpBean.getYearLose()).append(", ").append(atpBean.getYearPrize()).append("\n");
                buffer.append("Career: ").append(atpBean.getCareerWin()).append("-").append(atpBean.getCareerLose()).append(", ").append(atpBean.getCareerPrize()).append("\n");
            }
            else {
                etCountry.setText("");
                etCity.setText("");
                etBirthday.setText("");
            }
            tvAtpConclude.setText(buffer.toString());
        }

        @Override
        protected void bindChildFragmentHolder(IFragmentHolder holder) {

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
            if (competitorBean != null) {
                if (competitorBean instanceof User) {
                    User user = (User) competitorBean;
                    user.setBirthday(birthday);
                    user.setCountry(country);
                    user.setCity(city);
                    user.setNameChn(name);
                    user.setNameEng(engName);
                    user.setNamePinyin(PinyinUtil.getPinyin(name));
                    user.setAtpId(atpId);
                    presenter.updateUser(user);
                }
                else {
                    PlayerBean playerBean = (PlayerBean) competitorBean;
                    playerBean.setBirthday(birthday);
                    playerBean.setCountry(country);
                    playerBean.setCity(city);
                    playerBean.setNameChn(name);
                    playerBean.setNameEng(engName);
                    playerBean.setNamePinyin(PinyinUtil.getPinyin(name));
                    playerBean.setAtpId(atpId);
                    presenter.updatePlayer(playerBean);
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
                bean.setAtpId(atpId);
                presenter.insertPlayer(bean);
            }
            if (onPlayerEditListener != null) {
                onPlayerEditListener.onPlayerUpdated(competitorBean);
            }
            return true;
        }

        public void setOnPlayerEditListener(OnPlayerEditListener onPlayerEditListener) {
            this.onPlayerEditListener = onPlayerEditListener;
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == REQUEST_SELECT_ATP) {
                if (resultCode == Activity.RESULT_OK) {
                    atpId = data.getStringExtra(AtpManageActivity.RESP_ATP_ID);
                    ivUpdate.setVisibility(View.VISIBLE);
                    PlayerAtpBean bean = TApplication.getInstance().getDaoSession().getPlayerAtpBeanDao()
                            .queryBuilder()
                            .where(PlayerAtpBeanDao.Properties.Id.eq(atpId))
                            .build().unique();
                    updateByAtpBean(bean);
                }
            }
        }

        /**
         * 更新atp数据完成
         * @param bean
         */
        @Override
        public void onUpdateAtpCompleted(PlayerAtpBean bean) {
            updateByAtpBean(bean);
        }

        @Override
        public void showLoading() {
            showProgress("loading");
        }

        @Override
        public void dismissLoading() {
            dismissProgress();
        }

        @Override
        public void showConfirm(String message) {
        }

        @Override
        public void showMessage(String message) {
            showMessageShort(message);
        }
    }

    public interface OnPlayerEditListener {
        void onPlayerUpdated(CompetitorBean bean);
    }
}
