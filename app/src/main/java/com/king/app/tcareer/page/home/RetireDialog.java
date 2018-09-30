package com.king.app.tcareer.page.home;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.BasePresenter;
import com.king.app.tcareer.base.IFragmentHolder;
import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.model.DateManager;
import com.king.app.tcareer.model.db.entity.Retire;
import com.king.app.tcareer.model.db.entity.RetireDao;
import com.king.app.tcareer.view.dialog.frame.FrameContentFragment;

import java.util.List;

import butterknife.BindView;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/9/29 18:21
 */
public class RetireDialog extends FrameContentFragment {

    @BindView(R.id.rv_items)
    RecyclerView rvItems;
    @BindView(R.id.sp_type)
    Spinner spType;
    @BindView(R.id.iv_add)
    ImageView ivAdd;
    @BindView(R.id.tv_new_title)
    TextView tvNewTitle;
    @BindView(R.id.btn_declare)
    Button btnDeclare;
    @BindView(R.id.btn_effect)
    Button btnEffect;
    @BindView(R.id.tv_relieve_title)
    TextView tvRelieveTitle;
    @BindView(R.id.tv_relieve)
    TextView tvRelieve;
    @BindView(R.id.tv_confirm)
    TextView tvConfirm;

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
    protected void onCreate(View view) {

        declareDateManager = new DateManager();
        effectDateManager = new DateManager();

        tvNewTitle.setVisibility(View.INVISIBLE);
        tvRelieveTitle.setVisibility(View.INVISIBLE);
        tvRelieve.setVisibility(View.INVISIBLE);
        btnDeclare.setVisibility(View.INVISIBLE);
        btnEffect.setVisibility(View.INVISIBLE);
        rvItems.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        tvConfirm.setOnClickListener(v -> onConfirm());
        btnEffect.setOnClickListener(v -> effectDateManager.pickDate(getActivity()
                , () -> btnEffect.setText(effectDateManager.getDateStr())));
        btnDeclare.setOnClickListener(v -> declareDateManager.pickDate(getActivity()
                , () -> btnDeclare.setText(declareDateManager.getDateStr())));
        ivAdd.setOnClickListener(v -> onAdd());
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected void onCreateData() {
        loadRetirements();
    }

    private void onAdd() {
        ivAdd.setVisibility(View.INVISIBLE);
        spType.setEnabled(false);
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
        tvNewTitle.setText("New relive");
        tvNewTitle.setVisibility(View.VISIBLE);
        btnDeclare.setText("Declare Date");
        btnEffect.setText("Effective Date");
        btnDeclare.setVisibility(View.VISIBLE);
        btnEffect.setVisibility(View.VISIBLE);

        tvRelieveTitle.setVisibility(View.VISIBLE);
        tvRelieve.setVisibility(View.VISIBLE);
        tvConfirm.setText("Confirm Relieve");
    }

    private void retireStatus() {
        tvNewTitle.setText("New retire");
        tvNewTitle.setVisibility(View.VISIBLE);
        btnDeclare.setText("Declare Date");
        btnEffect.setText("Effective Date");
        btnDeclare.setVisibility(View.VISIBLE);
        btnEffect.setVisibility(View.VISIBLE);

        relieveId = 0;
        tvRelieveTitle.setVisibility(View.INVISIBLE);
        tvRelieve.setVisibility(View.INVISIBLE);
        tvConfirm.setText("Confirm Retire");
    }

    private void onConfirm() {
        if (declareDateManager.getDate() == null) {
            showMessage("Declare date can't be null");
            return;
        }
        if (effectDateManager.getDate() == null) {
            showMessage("Effective date can't be null");
            return;
        }
        if (isRelive()) {
            if (relieveId == 0) {
                showMessage("You didn't select retirement to relive");
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
        adapter.setOnItemClickListener((position, data) -> {
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
                    tvNewTitle.setText("Update retire");
                }
                else {
                    relieveStatus();
                    tvNewTitle.setText("Update relieve");
                    setRelieveText(data.getRelieveRetire());
                }
                declareDateManager.setDate(data.getDeclareDate());
                effectDateManager.setDate(data.getEffectDate());
                btnDeclare.setText(declareDateManager.getDateStr());
                btnEffect.setText(effectDateManager.getDateStr());
                tvConfirm.setText("Update");
            }
        });
        adapter.setOnRetireListener(retire -> deleteItem(retire));
        rvItems.setAdapter(adapter);
    }

    private void setRelieveText(Retire data) {
        tvRelieve.setText("Declare date: " + declareDateManager.getDateFormat().format(data.getDeclareDate())
            + ", Effective date: " + effectDateManager.getDateFormat().format(data.getEffectDate()));
    }

    private void deleteItem(Retire retire) {
        TApplication.getInstance().getDaoSession().getRetireDao().delete(retire);
        TApplication.getInstance().getDaoSession().getRetireDao().detachAll();
        loadRetirements();
    }

    private boolean isRelive() {
        return spType.getSelectedItemPosition() == 1;
    }
}
