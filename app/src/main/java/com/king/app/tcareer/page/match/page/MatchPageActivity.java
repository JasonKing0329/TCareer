package com.king.app.tcareer.page.match.page;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.king.app.tcareer.R;
import com.king.app.tcareer.base.BaseMvpActivity;
import com.king.app.tcareer.model.GlideOptions;
import com.king.app.tcareer.model.ImageProvider;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.page.record.page.RecordPageActivity;
import com.king.app.tcareer.view.widget.CircleImageView;

import java.util.List;

import butterknife.BindView;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/11/21 14:06
 */
public class MatchPageActivity extends BaseMvpActivity<PagePresenter> implements IPageView {

    public static final String KEY_MATCH_NAME_ID = "key_match_name_id";
    public static final String KEY_USER_ID = "key_user_id";

    @BindView(R.id.iv_match)
    ImageView ivMatch;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.appbar_layout)
    AppBarLayout appbarLayout;
    @BindView(R.id.tv_match)
    TextView tvMatch;
    @BindView(R.id.tv_winlose)
    TextView tvWinlose;
    @BindView(R.id.iv_match_thumb)
    CircleImageView ivMatchThumb;
    @BindView(R.id.tv_place)
    TextView tvPlace;
    @BindView(R.id.tv_level)
    TextView tvLevel;
    @BindView(R.id.fab_like)
    FloatingActionButton fabLike;
    @BindView(R.id.rv_records)
    RecyclerView rvRecords;

    private PageRecordAdapter adapter;

    @Override
    protected int getContentView() {
        return R.layout.activity_match_page;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected PagePresenter createPresenter() {
        return new PagePresenter();
    }

    @Override
    protected void initData() {
        init();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        init();
    }

    private void init() {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_filterrable);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.dark_grey), PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rvRecords.setLayoutManager(manager);

        long matchNameId = getIntent().getLongExtra(KEY_MATCH_NAME_ID, -1);
        long userId = getIntent().getLongExtra(KEY_USER_ID, -1);
        presenter.loadData(matchNameId, userId);
    }

    @Override
    public void showMatchInfo(String name, String country, String city, String level, String court) {
        tvMatch.setText(name);
        tvPlace.setText(country + "/" + city);
        tvLevel.setText(level + "/" + court);

        Glide.with(this)
                .load(ImageProvider.getMatchHeadPath(name, court))
                .apply(GlideOptions.getDefaultMatchOptions())
                .into(ivMatch);

        Glide.with(this)
                .load(ImageProvider.getMatchHeadPath(name, court))
                .apply(GlideOptions.getDefaultMatchOptions())
                .into(ivMatchThumb);
    }

    @Override
    public void showError(String msg) {
        showConfirmMessage(msg, null);
    }

    @Override
    public void onRecordsLoaded(List<Object> list, int win, int lose) {
        tvWinlose.setText(win + "胜" + lose + "负");
        adapter = new PageRecordAdapter(presenter.getUser(), list);
        adapter.setOnItemClickListener(new PageRecordAdapter.OnItemClickListener() {
            @Override
            public void onClickRecord(View v, Record record) {
                showRecord(v, record);
//                CompetitorBean competitor = CompetitorParser.getCompetitorFrom(record);
//                Intent intent = new Intent().setClass(MatchPageActivity.this, PlayerPageActivity.class);
//                intent.putExtra(PlayerPageActivity.KEY_USER_ID, presenter.getUser().getId());
//                intent.putExtra(PlayerPageActivity.KEY_COMPETITOR_ID, competitor.getId());
//                if (competitor instanceof User) {
//                    intent.putExtra(PlayerPageActivity.KEY_COMPETITOR_IS_USER, true);
//                }
//                startActivity(intent);
            }
        });
        rvRecords.setAdapter(adapter);
    }

    private void showRecord(View view, Record record) {
        Intent intent = new Intent();
        intent.setClass(this, RecordPageActivity.class);
        intent.putExtra(RecordPageActivity.KEY_RECORD_ID, record.getId());
        ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(this
                , Pair.create(view.findViewById(R.id.iv_player),getString(R.string.anim_player_page_head)));
        startActivity(intent, transitionActivityOptions.toBundle());
    }
}
