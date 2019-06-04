package com.king.app.tcareer.page.player.manage;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.IFragmentHolder;
import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.databinding.DialogPlayerManageBinding;
import com.king.app.tcareer.model.bean.CompetitorBean;
import com.king.app.tcareer.model.db.entity.PlayerAtpBean;
import com.king.app.tcareer.model.db.entity.PlayerAtpBeanDao;
import com.king.app.tcareer.model.db.entity.PlayerBean;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.page.player.atp.AtpManageActivity;
import com.king.app.tcareer.utils.PinyinUtil;
import com.king.app.tcareer.view.dialog.DraggableDialogFragment;

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

    public static class EditFragment extends BindingContentFragment<DialogPlayerManageBinding, EditViewModel> {

        private final int REQUEST_SELECT_ATP = 101;

        private CompetitorBean competitorBean;
        private OnPlayerEditListener onPlayerEditListener;

        private String atpId;

        @Override
        protected int getContentLayoutRes() {
            return R.layout.dialog_player_manage;
        }

        @Override
        protected EditViewModel createViewModel() {
            return ViewModelProviders.of(this).get(EditViewModel.class);
        }

        public void setCompetitorBean(CompetitorBean competitorBean) {
            this.competitorBean = competitorBean;
        }

        @Override
        protected void onCreate(View view) {
            if (competitorBean != null) {
                atpId = competitorBean.getAtpId();
            }
            mBinding.ivUpdate.setVisibility(atpId == null ? View.GONE:View.VISIBLE);

            if (competitorBean != null) {
                mBinding.etName.setText(competitorBean.getNameChn());
                mBinding.etNameEng.setText(competitorBean.getNameEng());
                mBinding.etCountry.setText(competitorBean.getCountry());
                mBinding.etCity.setText(competitorBean.getCity());
                mBinding.etBirthday.setText(competitorBean.getBirthday());
                if (competitorBean.getAtpBean() != null) {
                    atpId = competitorBean.getAtpId();
                    updateByAtpBean(competitorBean.getAtpBean());
                }
            }
            mBinding.tvAtpId.setOnClickListener(v -> {
                Intent intent = new Intent().setClass(getContext(), AtpManageActivity.class);
                intent.putExtra(AtpManageActivity.EXTRA_SELECT, true);
                startActivityForResult(intent, REQUEST_SELECT_ATP);
            });

            mModel.onUpdateAtpCompleted.observe(this, bean -> onUpdateAtpCompleted(bean));
            mBinding.ivUpdate.setOnClickListener(v -> mModel.updateAtpData(atpId));
        }

        private void updateByAtpBean(PlayerAtpBean atpBean) {
            mBinding.tvAtpId.setText("Atp id: " + atpBean.getId());
            mBinding.etNameEng.setText(atpBean.getName());
            StringBuffer buffer = new StringBuffer();
            buffer.append(atpBean.getOverViewUrl());
            // 更新过详细信息
            if (atpBean.getLastUpdateDate() > 0) {
                mBinding.etCountry.setText(atpBean.getBirthCountry());

                if (!TextUtils.isEmpty(atpBean.getBirthCity())) {
                    mBinding.etCity.setText(atpBean.getBirthCity());
                }
                if (!TextUtils.isEmpty(atpBean.getBirthday())) {
                    mBinding.etBirthday.setText(atpBean.getBirthday());
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
                mBinding.etCountry.setText("");
                mBinding.etCity.setText("");
                mBinding.etBirthday.setText("");
            }
            mBinding.tvAtpConclude.setText(buffer.toString());
        }

        @Override
        protected void bindChildFragmentHolder(IFragmentHolder holder) {

        }

        public boolean onSave() {
            String name = mBinding.etName.getText().toString();
            if (TextUtils.isEmpty(name)) {
                mBinding.etName.setError("Name can't be null");
                return false;
            }
            String country = mBinding.etCountry.getText().toString();
            if (TextUtils.isEmpty(country)) {
                mBinding.etCountry.setError("Country can't be null");
                return false;
            }

            String city = mBinding.etCity.getText().toString();
            String birthday = mBinding.etBirthday.getText().toString();
            String engName = mBinding.etNameEng.getText().toString();
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
                    mModel.updateUser(user);
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
                    mModel.updatePlayer(playerBean);
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
                mModel.insertPlayer(bean);
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
                    mBinding.ivUpdate.setVisibility(View.VISIBLE);
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
        private void onUpdateAtpCompleted(PlayerAtpBean bean) {
            updateByAtpBean(bean);
        }
    }

    public interface OnPlayerEditListener {
        void onPlayerUpdated(CompetitorBean bean);
    }
}
