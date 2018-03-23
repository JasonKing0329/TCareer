package com.king.app.tcareer.page.record.page;

import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.king.app.tcareer.R;
import com.king.app.tcareer.base.BaseMvpActivity;
import com.king.app.tcareer.model.CompetitorParser;
import com.king.app.tcareer.model.GlideOptions;
import com.king.app.tcareer.model.ImageProvider;
import com.king.app.tcareer.model.ScoreParser;
import com.king.app.tcareer.model.bean.CompetitorBean;
import com.king.app.tcareer.model.db.entity.MatchBean;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.page.match.MatchItemAdapter;
import com.king.app.tcareer.page.match.page.MatchPageActivity;
import com.king.app.tcareer.page.player.page.PlayerPageActivity;
import com.king.app.tcareer.page.record.editor.RecordEditorActivity;
import com.king.app.tcareer.view.widget.CircleImageView;
import com.youth.banner.Banner;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/22 13:38
 */
public class RecordPageActivity extends BaseMvpActivity<RecordPagePresenter> implements RecordPageView {

    public static final String KEY_RECORD_ID = "record_id";

    private final int REQUEST_EDIT = 121;

    @BindView(R.id.iv_match)
    ImageView ivMatch;
    @BindView(R.id.lmbanner)
    Banner lmbanner;
    @BindView(R.id.tv_place)
    TextView tvPlace;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.ctl_toolbar)
    CollapsingToolbarLayout ctlToolbar;
    @BindView(R.id.iv_user)
    CircleImageView ivUser;
    @BindView(R.id.iv_player)
    CircleImageView ivPlayer;
    @BindView(R.id.tv_score_set)
    TextView tvScoreSet;
    @BindView(R.id.tv_rs_user)
    TextView tvRsUser;
    @BindView(R.id.tv_rs_player)
    TextView tvRsPlayer;
    @BindView(R.id.tv_round)
    TextView tvRound;
    @BindView(R.id.tv_score)
    TextView tvScore;
    @BindView(R.id.tv_level)
    TextView tvLevel;
    @BindView(R.id.tv_level_detail)
    TextView tvLevelDetail;
    @BindView(R.id.tv_court)
    TextView tvCourt;
    @BindView(R.id.tv_court_detail)
    TextView tvCourtDetail;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.rv_records)
    RecyclerView rvRecords;

    private MatchItemAdapter itemAdapter;

    @Override
    protected int getContentView() {
        return R.layout.activity_record_page;
    }

    @Override
    protected void initView() {
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 加入了转场动画，必须用onBackPressed，finish无效果
                onBackPressed();
            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rvRecords.setLayoutManager(manager);
    }

    @Override
    protected RecordPagePresenter createPresenter() {
        return new RecordPagePresenter();
    }

    @Override
    protected void initData() {
        long recordId = getIntent().getLongExtra(KEY_RECORD_ID, -1);
        presenter.loadRecord(recordId);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        initData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.record_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_action_edit:
                editRecord();
                break;
        }
        return true;
    }

    private void editRecord() {
        Intent intent = new Intent();
        intent.setClass(this, RecordEditorActivity.class);
        intent.putExtra(RecordEditorActivity.KEY_USER_ID, presenter.getUser().getId());
        intent.putExtra(RecordEditorActivity.KEY_RECORD_ID, presenter.getRecord().getId());
        startActivityForResult(intent, REQUEST_EDIT);
    }

    @Override
    public void postShowRecord(final Record record) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showRecord(record);
            }
        });
    }

    @Override
    public void postShowMatchRecords(final List<Record> records) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showMatchRecords(records);
            }
        });
    }

    private void showRecord(Record record) {
        // match
        MatchBean match = record.getMatch().getMatchBean();
        String name = record.getMatch().getName();
        toolbar.setTitle(name);
        ctlToolbar.setTitle(name);
        tvPlace.setText(match.getCountry() + "/" + match.getCity());
        tvCourt.setText(match.getCourt());
        tvLevel.setText(match.getLevel());
        tvDate.setText(record.getDateStr());
        tvRound.setText(record.getRound());
        tvScore.setText(ScoreParser.getScoreText(record.getScoreList(), record.getRetireFlag()));
        Glide.with(this)
                .load(ImageProvider.getMatchHeadPath(name, match.getCourt()))
                .apply(GlideOptions.getDefaultMatchOptions())
                .into(ivMatch);

        // user
        StringBuffer buffer = new StringBuffer();
        buffer.append(record.getUser().getNameChn()).append("(").append(record.getRank());
        if (record.getSeed() > 0) {
            buffer.append("/").append(record.getSeed());
        }
        buffer.append(")");
        tvRsUser.setText(buffer.toString());
        Glide.with(this)
                .load(ImageProvider.getPlayerHeadPath(record.getUser().getNameChn()))
                .apply(GlideOptions.getDefaultPlayerOptions())
                .into(ivUser);
        // player
        CompetitorBean competitor = CompetitorParser.getCompetitorFrom(record);
        buffer = new StringBuffer();
        buffer.append(competitor.getNameChn()).append("(").append(record.getRankCpt());
        if (record.getSeedpCpt() > 0) {
            buffer.append("/").append(record.getSeedpCpt());
        }
        buffer.append(")");
        tvRsPlayer.setText(buffer.toString());
        Glide.with(this)
                .load(ImageProvider.getPlayerHeadPath(competitor.getNameChn()))
                .apply(GlideOptions.getDefaultPlayerOptions())
                .into(ivPlayer);
    }

    private void showMatchRecords(List<Record> records) {
        itemAdapter = new MatchItemAdapter(records);
        itemAdapter.setFocusItem(presenter.getRecord());
        rvRecords.setAdapter(itemAdapter);
    }

    @Override
    public void showDetails(String scoreSet, String levelStr, String courtStr) {
        tvScoreSet.setText(scoreSet);
        tvLevelDetail.setText(levelStr);
        tvCourtDetail.setText(courtStr);
    }

    @OnClick({R.id.tv_h2h, R.id.tv_match_page})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_h2h:
                showPlayerPage();
                break;
            case R.id.tv_match_page:
                showMatchPage();
                break;
        }
    }

    private void showMatchPage() {
        Intent intent = new Intent();
        intent.setClass(this, MatchPageActivity.class);
        intent.putExtra(MatchPageActivity.KEY_USER_ID, presenter.getUser().getId());
        intent.putExtra(MatchPageActivity.KEY_MATCH_NAME_ID, presenter.getRecord().getMatchNameId());
        startActivity(intent);
    }

    private void showPlayerPage() {
        Intent intent = new Intent();
        intent.setClass(this, PlayerPageActivity.class);
        intent.putExtra(PlayerPageActivity.KEY_USER_ID, presenter.getUser().getId());
        CompetitorBean competitor = CompetitorParser.getCompetitorFrom(presenter.getRecord());
        if (competitor instanceof User) {
            intent.putExtra(PlayerPageActivity.KEY_COMPETITOR_IS_USER, true);
        }
        intent.putExtra(PlayerPageActivity.KEY_COMPETITOR_ID, competitor.getId());
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_EDIT:
                if (resultCode == RESULT_OK) {
                    setResult(RESULT_OK);
                    initData();
                }
                break;
        }
    }
}
