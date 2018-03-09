package com.king.app.tcareer.page.rank;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.BaseMvpFragment;
import com.king.app.tcareer.base.IFragmentHolder;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/9 13:39
 */
public class RankCountFragment extends BaseMvpFragment<RankCountPresenter> implements RankCountView {

    private static final String KEY_USER_ID = "user_id";

    @BindView(R.id.tv_current)
    TextView tvCurrent;
    @BindView(R.id.tv_high)
    TextView tvHigh;
    @BindView(R.id.tv_top1)
    TextView tvTop1;
    @BindView(R.id.tv_longest)
    TextView tvLongest;
    @BindView(R.id.et_min)
    EditText etMin;
    @BindView(R.id.et_max)
    EditText etMax;
    @BindView(R.id.tv_cond_total)
    TextView tvCondTotal;
    @BindView(R.id.tv_cond_longest)
    TextView tvCondLongest;
    @BindView(R.id.group_condition)
    LinearLayout groupCondition;
    @BindView(R.id.group_top1)
    LinearLayout groupTop1;

    public static RankCountFragment newInstance(long userId) {
        Bundle bundle = new Bundle();
        bundle.putLong(KEY_USER_ID, userId);
        RankCountFragment fragment = new RankCountFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {

    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_rank_count;
    }

    @Override
    protected void onCreate(View view) {

    }

    @Override
    protected RankCountPresenter createPresenter() {
        return new RankCountPresenter();
    }

    @Override
    protected void onCreateData() {
        long userId = getArguments().getLong(KEY_USER_ID);
        presenter.loadDatas(userId);
    }

    @OnClick({R.id.btn_condition, R.id.btn_condition_ok})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_condition:
                groupCondition.setVisibility(groupCondition.getVisibility() == View.VISIBLE ? View.GONE:View.VISIBLE);
                break;
            case R.id.btn_condition_ok:
                String min = etMin.getText().toString();
                String max = etMax.getText().toString();
                if (TextUtils.isEmpty(min) && TextUtils.isEmpty(max)) {
                    return;
                }

                int nMin = 0;
                int nMax = 0;
                if (!TextUtils.isEmpty(min)) {
                    nMin = Integer.parseInt(min);
                }
                if (!TextUtils.isEmpty(max)) {
                    nMax = Integer.parseInt(max);
                }

                if (nMax == 0) {
                    nMax = nMin;
                }
                if (nMin == 0) {
                    nMin = nMax;
                }
                presenter.queryCondition(nMin, nMax);
                break;
        }
    }

    @Override
    public void showBasic(int current, int highest, boolean isTop1) {
        tvCurrent.setText(String.valueOf(current));
        tvHigh.setText(String.valueOf(highest));
        groupTop1.setVisibility(isTop1 ? View.VISIBLE:View.GONE);
    }

    @Override
    public void showTop1(int weeks, String sequence) {
        tvTop1.setText(String.valueOf(weeks));
        tvLongest.setText(sequence);
    }

    @Override
    public void showConditions(int weeks, String sequence) {
        tvCondTotal.setText(String.valueOf(weeks));
        tvCondLongest.setText(sequence);
    }
}
