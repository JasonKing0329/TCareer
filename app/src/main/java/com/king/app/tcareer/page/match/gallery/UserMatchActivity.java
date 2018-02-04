package com.king.app.tcareer.page.match.gallery;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.TextView;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.BaseMvpActivity;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.view.widget.GradientBkView;
import com.king.app.tcareer.view.widget.discrete.DiscreteScrollView;
import com.king.app.tcareer.view.widget.discrete.transform.ScaleTransformer;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 描述: 按week排列的横向gallery赛事总览，进入时定位到离当前周数最近的赛事
 * <p/>作者：景阳
 * <p/>创建时间: 2017/3/15 9:42
 */
public class UserMatchActivity extends BaseMvpActivity<UserMatchPresenter> implements UserMatchView, DiscreteScrollView.CurrentItemChangeListener {

    public static final String KEY_START_POSITION = "start_position";

    public static final String KEY_USER_ID = "user_id";

    @BindView(R.id.match_bk)
    GradientBkView vMatchBk;
    @BindView(R.id.match_name)
    TextView tvMatch;
    @BindView(R.id.match_place)
    TextView tvPlace;
    @BindView(R.id.match_gallery)
    DiscreteScrollView dsvMatch;
    @BindView(R.id.match_month)
    TextView tvMonth;
    @BindView(R.id.match_week)
    TextView tvWeek;
    @BindView(R.id.match_download)
    FloatingActionButton fabDownload;

    private UserMatchAdapter userMatchAdapter;

    private int nMatchIndex;
    private MatchScrollManager scrollManager;

    @Override
    protected int getContentView() {
        return R.layout.activity_user_match;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected UserMatchPresenter createPresenter() {
        return new UserMatchPresenter();
    }

    @Override
    protected void initData() {
        scrollManager = new MatchScrollManager(this);
        // 绑定滑动过程中联动变化的view
        scrollManager.bindBehaviorView(vMatchBk, fabDownload);

        initMatchGallery();
    }

    private void initMatchGallery() {
        dsvMatch.setCurrentItemChangeListener(this);
        dsvMatch.setItemTransitionTimeMillis(200);
        dsvMatch.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.8f)
                .build());
        dsvMatch.setScrollStateChangeListener(scrollManager);
        presenter.loadMatches(getIntent().getLongExtra(KEY_USER_ID, -1));
    }

    @Override
    public void showMatches(List<UserMatchBean> list) {
        // 绑定滑动依据数据
        scrollManager.bindData(list);

        userMatchAdapter = new UserMatchAdapter(this, list);
        userMatchAdapter.setFragmentManager(getSupportFragmentManager());
        userMatchAdapter.setUserId(presenter.getUser().getId());
        userMatchAdapter.setMatchTextView(tvMatch, tvPlace);
        dsvMatch.setAdapter(userMatchAdapter);

        // 定位到最近的赛事或者intent指定的赛事
        focusToLatestWeek();
    }

    private void focusToLatestWeek() {

        String pos = getIntent().getStringExtra(KEY_START_POSITION);
        final int position;
        if (TextUtils.isEmpty(pos)) {
            position = presenter.findLatestWeekItem();
        }
        else {
            position = Integer.parseInt(pos);
        }
        dsvMatch.scrollToPosition(position);

        // 必须post，因为在GradientBkView里的相关计算getWidth()和getHeight()可能还等于0，渐变颜色的相关区域跟其有关
        // 不post的话会造成只有一种颜色
        vMatchBk.post(new Runnable() {
            @Override
            public void run() {
                scrollManager.initPosition(position);
            }
        });
    }

    private void onItemChanged(UserMatchBean bean) {
        tvMatch.setText(bean.getNameBean().getName());
        tvPlace.setText(bean.getNameBean().getMatchBean().getCountry()
                + "/" + bean.getNameBean().getMatchBean().getCity());
        tvMonth.setText(AppConstants.MONTH_ENG[bean.getNameBean().getMatchBean().getMonth() - 1]);
        tvWeek.setText("week " + bean.getNameBean().getMatchBean().getWeek());
    }

    @OnClick({R.id.match_back})
    public void onBack() {
        // 加入了转场动画，必须用onBackPressed，finish无效果
        onBackPressed();
    }

    @OnClick({R.id.match_refresh})
    public void onRefresh() {
        userMatchAdapter.refreshImage(nMatchIndex);
    }

    @OnClick({R.id.match_download})
    public void onDownload() {
        userMatchAdapter.startDownload(nMatchIndex);
    }

    @OnClick({R.id.match_delete})
    public void onDelete() {
        userMatchAdapter.deleteImage(nMatchIndex);
    }

    @Override
    public void onCurrentItemChanged(RecyclerView.ViewHolder viewHolder, int adapterPosition) {
        nMatchIndex = adapterPosition;
        onItemChanged(presenter.getUserMatchBean(adapterPosition));
    }
}
