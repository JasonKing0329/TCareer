package com.king.app.tcareer.page.home;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.BaseMvpFragment;
import com.king.app.tcareer.base.IFragmentHolder;
import com.king.app.tcareer.model.FlagProvider;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.page.score.IScorePageView;
import com.king.app.tcareer.page.score.ScorePageData;
import com.king.app.tcareer.page.score.ScorePresenter;
import com.king.app.tcareer.utils.FormatUtil;
import com.king.app.tcareer.utils.ScreenUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/11/14 15:41
 */
public class HomeHeadFragment extends BaseMvpFragment<ScorePresenter> implements IScorePageView {

    private static final String BUNDLE_USERID = "userId";

    @BindView(R.id.iv_flag_bg)
    ImageView ivFlagBg;
    @BindView(R.id.tv_country)
    TextView tvCountry;
    @BindView(R.id.tv_birthday)
    TextView tvBirthday;
    @BindView(R.id.tv_height)
    TextView tvHeight;
    @BindView(R.id.tv_match_number)
    TextView tvMatchNumber;
    @BindView(R.id.tv_player)
    TextView tvPlayer;
    @BindView(R.id.tv_total)
    TextView tvTotal;
    @BindView(R.id.tv_rank)
    TextView tvRank;
    @BindView(R.id.group_player_basic)
    RelativeLayout groupPlayerBasic;

    private IHomeHeaderHolder holder;

    public static HomeHeadFragment newInstance(long userId) {

        Bundle args = new Bundle();
        args.putLong(BUNDLE_USERID, userId);
        HomeHeadFragment fragment = new HomeHeadFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {
        if (holder instanceof IHomeHeaderHolder) {
            this.holder = (IHomeHeaderHolder) holder;
        }
    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.layout_player_basic;
    }

    @Override
    protected void onCreate(View view) {
        tvPlayer.setVisibility(View.INVISIBLE);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) tvRank.getLayoutParams();
        params.bottomMargin = params.bottomMargin + ScreenUtils.dp2px(20);
    }

    @Override
    protected ScorePresenter createPresenter() {
        return new ScorePresenter();
    }

    @Override
    protected void onCreateData() {
        load52WeekScore();
    }

    private void load52WeekScore() {

        long userId = getArguments().getLong(BUNDLE_USERID);
        presenter.query52WeekRecords(userId);
    }

    @Override
    public void showUser(final User user) {
        tvPlayer.setText(user.getNameEng());
        tvCountry.setText(user.getCountry());
        tvBirthday.setText(user.getBirthday());
        tvHeight.setText(user.getHeight() + "  " + FormatUtil.formatNumber(user.getWeight()) + "kg");
        ivFlagBg.setImageResource(FlagProvider.getFlagRes(user.getCountry()));
    }

    @Override
    public void onPageDataLoaded(ScorePageData data) {
        onScoreLoaded(data.getCountScore(), data.getRank());
        tvMatchNumber.setText("Match count " + String.valueOf(data.getScoreList().size()));
    }

    private void onScoreLoaded(int score, int rank) {
        tvTotal.setText(String.valueOf(score));
        tvRank.setText(String.valueOf(rank));
    }

    @OnClick(R.id.group_player_basic)
    public void onViewClicked() {
        holder.onClickScoreHead();
    }

    public void onRankChanged() {
        tvRank.setText(String.valueOf(presenter.getRank()));
    }
}
