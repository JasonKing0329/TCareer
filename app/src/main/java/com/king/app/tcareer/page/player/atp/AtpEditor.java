package com.king.app.tcareer.page.player.atp;

import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.IFragmentHolder;
import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.model.db.entity.PlayerAtpBean;
import com.king.app.tcareer.model.db.entity.PlayerAtpBeanDao;
import com.king.app.tcareer.view.dialog.DraggableDialogFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/5/18 11:12
 */
public class AtpEditor extends DraggableDialogFragment {

    private EditFragment ftEdit;

    private String atpId;

    private OnEditListener onEditListener;

    public void setOnEditListener(OnEditListener onEditListener) {
        this.onEditListener = onEditListener;
    }

    @Override
    protected View getToolbarView(ViewGroup groupToolbar) {
        requestOkAction();
        requestCloseAction();
        if (atpId == null) {
            setTitle("New player");
        }
        else {
            setTitle(atpId);
        }
        return null;
    }

    @Override
    protected Fragment getContentViewFragment() {
        ftEdit = new EditFragment();
        ftEdit.setAtpId(atpId);
        ftEdit.setOnEditListener(onEditListener);
        return ftEdit;
    }

    public void setAtpId(String atpId) {
        this.atpId = atpId;
    }

    @Override
    protected boolean onClickOk() {
        return ftEdit.onClickOk();
    }

    public interface OnEditListener {
        void onUpdated(PlayerAtpBean bean);
        void onInserted(PlayerAtpBean bean);
    }

    public static class EditFragment extends ContentFragment {

        @BindView(R.id.et_id)
        EditText etId;
        @BindView(R.id.et_name)
        EditText etName;
        @BindView(R.id.tv_url)
        TextView tvUrl;
        @BindView(R.id.tv_details)
        TextView tvDetails;

        private String atpId;
        private PlayerAtpBean atpBean;

        private OnEditListener onEditListener;

        public void setOnEditListener(OnEditListener onEditListener) {
            this.onEditListener = onEditListener;
        }

        @Override
        protected int getContentLayoutRes() {
            return R.layout.dialog_add_atp;
        }

        @Override
        protected void onCreate(View view) {
            if (atpId == null) {
                atpBean = new PlayerAtpBean();
            }
            else {
                etId.setText(atpId);
                try {
                    atpBean = TApplication.getInstance().getDaoSession().getPlayerAtpBeanDao()
                            .queryBuilder()
                            .where(PlayerAtpBeanDao.Properties.Id.eq(atpId))
                            .build().unique();
                    etName.setText(atpBean.getName());
                    tvUrl.setText(atpBean.getOverViewUrl());
                    updateDetail();
                } catch (Exception e) {
                    e.printStackTrace();
                    atpBean = new PlayerAtpBean();
                }
            }

            etName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    onUrlChanged();
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            etId.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    onUrlChanged();
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }

        private void onUrlChanged() {
            String id = etId.getText().toString();
            String name = etName.getText().toString();
            String[] nameArray = name.split(" ");
            StringBuffer url = new StringBuffer();
            url.append("/en/players/");
            for (int i = 0; i < nameArray.length; i++) {
                if (i > 0) {
                    url.append("-");
                }
                url.append(nameArray[i].toLowerCase());
            }
            url.append("/").append(id).append("/overview");
            tvUrl.setText(url);
        }

        private void updateDetail() {
            StringBuffer buffer = new StringBuffer();
            if (!TextUtils.isEmpty(atpBean.getBirthCountry())) {
                buffer.append("Birth place: ");
                if (!TextUtils.isEmpty(atpBean.getBirthCity())) {
                    buffer.append(atpBean.getBirthCity()).append(", ");
                }
                buffer.append(atpBean.getBirthCountry()).append("\n");
                buffer.append("Residence: ");
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
            tvDetails.setText(buffer.toString());
        }
        @Override
        protected void bindChildFragmentHolder(IFragmentHolder holder) {

        }

        public void setAtpId(String atpId) {
            this.atpId = atpId;
        }

        public boolean onClickOk() {
            if (TextUtils.isEmpty(etId.getText().toString())) {
                showMessageShort("ID不能为空");
                return false;
            }
            if (TextUtils.isEmpty(etName.getText().toString())) {
                showMessageShort("Name不能为空");
                return false;
            }
            atpId = etId.getText().toString();
            atpBean.setId(atpId);
            atpBean.setName(etName.getText().toString());
            atpBean.setOverViewUrl(tvUrl.getText().toString());
            PlayerAtpBeanDao dao = TApplication.getInstance().getDaoSession().getPlayerAtpBeanDao();
            List<PlayerAtpBean> list = dao.queryBuilder()
                    .where(PlayerAtpBeanDao.Properties.Id.eq(atpId))
                    .build().list();
            if (list.size() > 0) {
                showConfirmCancelMessage("目标ID已存在，是否更新现有数据？", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            PlayerAtpBeanDao dao = TApplication.getInstance().getDaoSession().getPlayerAtpBeanDao();
                            dao.update(atpBean);
                            if (onEditListener != null) {
                                onEditListener.onUpdated(atpBean);
                            }
                        }
                    }
                });
                return false;
            }
            else {
                dao.insert(atpBean);
                if (onEditListener != null) {
                    onEditListener.onInserted(atpBean);
                }
            }
            return true;
        }
    }
}
