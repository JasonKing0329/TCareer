package com.king.app.tcareer.page.home;

import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.IFragmentHolder;
import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.base.mvvm.BaseViewModel;
import com.king.app.tcareer.databinding.DialogRetireBinding;
import com.king.app.tcareer.model.DateManager;
import com.king.app.tcareer.model.db.entity.Retire;
import com.king.app.tcareer.model.db.entity.RetireDao;
import com.king.app.tcareer.view.dialog.frame.FrameContentFragment;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/9/29 18:21
 */
public class RetireDialog extends FrameContentFragment<DialogRetireBinding, BaseViewModel> {
    
    private long userId;

    private long relieveId;

    private Retire mRetire;

    private RetireAdapter adapter;

    private DateManager declareDateManager;

    private DateManager effectDateManager;

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {

    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.dialog_retire;
    }

    @Override
    protected BaseViewModel createViewModel() {
        return null;
    }

    @Override
    protected void onCreate(View view) {

        declareDateManager = new DateManager();
        effectDateManager = new DateManager();

        mBinding.tvNewTitle.setVisibility(View.INVISIBLE);
        mBinding.tvRelieveTitle.setVisibility(View.INVISIBLE);
        mBinding.tvRelieve.setVisibility(View.INVISIBLE);
        mBinding.btnDeclare.setVisibility(View.INVISIBLE);
        mBinding.btnEffect.setVisibility(View.INVISIBLE);
        mBinding.rvItems.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mBinding.tvConfirm.setOnClickListener(v -> onConfirm());
        mBinding.btnEffect.setOnClickListener(v -> effectDateManager.pickDate(getActivity()
                , () -> mBinding.btnEffect.setText(effectDateManager.getDateStr())));
        mBinding.btnDeclare.setOnClickListener(v -> declareDateManager.pickDate(getActivity()
                , () -> mBinding.btnDeclare.setText(declareDateManager.getDateStr())));
        mBinding.ivAdd.setOnClickListener(v -> onAdd());
    }

    @Override
    protected void onCreateData() {
        loadRetirements();
    }

    private void onAdd() {
        mBinding.ivAdd.setVisibility(View.INVISIBLE);
        mBinding.spType.setEnabled(false);
        mRetire = null;
        declareDateManager.reset();
        effectDateManager.reset();

        if (isRelive()) {
            relieveStatus();
        }
        else {
            retireStatus();
        }
    }

    private void relieveStatus() {
        mBinding.tvNewTitle.setText("New relive");
        mBinding.tvNewTitle.setVisibility(View.VISIBLE);
        mBinding.btnDeclare.setText("Declare Date");
        mBinding.btnEffect.setText("Effective Date");
        mBinding.btnDeclare.setVisibility(View.VISIBLE);
        mBinding.btnEffect.setVisibility(View.VISIBLE);

        mBinding.tvRelieveTitle.setVisibility(View.VISIBLE);
        mBinding.tvRelieve.setVisibility(View.VISIBLE);
        mBinding.tvConfirm.setText("Confirm Relieve");
    }

    private void retireStatus() {
        mBinding.tvNewTitle.setText("New retire");
        mBinding.tvNewTitle.setVisibility(View.VISIBLE);
        mBinding.btnDeclare.setText("Declare Date");
        mBinding.btnEffect.setText("Effective Date");
        mBinding.btnDeclare.setVisibility(View.VISIBLE);
        mBinding.btnEffect.setVisibility(View.VISIBLE);

        relieveId = 0;
        mBinding.tvRelieveTitle.setVisibility(View.INVISIBLE);
        mBinding.tvRelieve.setVisibility(View.INVISIBLE);
        mBinding.tvConfirm.setText("Confirm Retire");
    }

    private void onConfirm() {
        if (declareDateManager.getDate() == null) {
            showMessageShort("Declare date can't be null");
            return;
        }
        if (effectDateManager.getDate() == null) {
            showMessageShort("Effective date can't be null");
            return;
        }
        if (isRelive()) {
            if (relieveId == 0) {
                showMessageShort("You didn't select retirement to relive");
                return;
            }
        }

        if (mRetire == null) {
            mRetire = new Retire();
        }
        mRetire.setRelieveId(relieveId);
        mRetire.setUserId(userId);
        mRetire.setDeclareDate(declareDateManager.getDate());
        mRetire.setEffectDate(effectDateManager.getDate());
        if (isRelive()) {
            long count = TApplication.getInstance().getDaoSession().getRetireDao().queryBuilder()
                    .where(RetireDao.Properties.UserId.eq(userId))
                    .where(RetireDao.Properties.RelieveId.notEq(0))
                    .buildCount().count();
            mRetire.setIndex((int) (count + 1));
        }
        else {
            long count = TApplication.getInstance().getDaoSession().getRetireDao().queryBuilder()
                    .where(RetireDao.Properties.UserId.eq(userId))
                    .where(RetireDao.Properties.RelieveId.eq(0))
                    .buildCount().count();
            mRetire.setIndex((int) (count + 1));
        }
        TApplication.getInstance().getDaoSession().getRetireDao().insertOrReplace(mRetire);

        dismissAllowingStateLoss();
    }

    private void loadRetirements() {
        List<Retire> list = TApplication.getInstance().getDaoSession().getRetireDao().queryBuilder()
                .where(RetireDao.Properties.UserId.eq(userId))
                .build().list();
        adapter = new RetireAdapter();
        adapter.setList(list);
        adapter.setOnItemClickListener((view, position, data) -> {
            // select as data to relieve
            if (isRelive()) {
                relieveId = data.getId();
                if (data.getRelieveId() == 0) {
                    setRelieveText(data);
                }
            }
            // update data
            else {
                mRetire = data;
                if (data.getRelieveId() == 0) {
                    retireStatus();
                    mBinding.tvNewTitle.setText("Update retire");
                }
                else {
                    relieveStatus();
                    mBinding.tvNewTitle.setText("Update relieve");
                    setRelieveText(data.getRelieveRetire());
                }
                declareDateManager.setDate(data.getDeclareDate());
                effectDateManager.setDate(data.getEffectDate());
                mBinding.btnDeclare.setText(declareDateManager.getDateStr());
                mBinding.btnEffect.setText(effectDateManager.getDateStr());
                mBinding.tvConfirm.setText("Update");
            }
        });
        adapter.setOnRetireListener(retire -> deleteItem(retire));
        mBinding.rvItems.setAdapter(adapter);
    }

    private void setRelieveText(Retire data) {
        mBinding.tvRelieve.setText("Declare date: " + declareDateManager.getDateFormat().format(data.getDeclareDate())
            + ", Effective date: " + effectDateManager.getDateFormat().format(data.getEffectDate()));
    }

    private void deleteItem(Retire retire) {
        TApplication.getInstance().getDaoSession().getRetireDao().delete(retire);
        TApplication.getInstance().getDaoSession().getRetireDao().detachAll();
        loadRetirements();
    }

    private boolean isRelive() {
        return mBinding.spType.getSelectedItemPosition() == 1;
    }
}
