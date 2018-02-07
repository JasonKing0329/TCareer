package com.king.app.tcareer.page.record.editor;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.BaseMvpActivity;
import com.king.app.tcareer.model.bean.AutoFillMatchBean;
import com.king.app.tcareer.model.bean.CompetitorBean;
import com.king.app.tcareer.model.db.entity.MatchNameBean;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.model.db.entity.Score;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.page.match.manage.MatchManageActivity;
import com.king.app.tcareer.page.player.manage.PlayerManageActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/3/13 13:23
 */
public class RecordEditorActivity extends BaseMvpActivity<EditorPresenter> implements IEditorView, IEditorHolder {

    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_RECORD_ID = "record_id";

    private final int REQUEST_CHANGE_MATCH = 101;
    private final int REQUEST_CHANGE_PLAYER = 102;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_next_page)
    TextView tvNextPage;
    @BindView(R.id.tv_done)
    TextView tvDone;
    @BindView(R.id.tv_continue)
    TextView tvContinue;
    @BindView(R.id.tv_previous_page)
    TextView tvPreviousPage;
    @BindView(R.id.group_player)
    LinearLayout groupPlayer;
    @BindView(R.id.group_match)
    LinearLayout groupMatch;

    private PlayerEditPage playerEditPage;
    private MatchEditPage matchEditPage;

    private MatchNameBean matchNameBean;

    @Override
    protected int getContentView() {
        return R.layout.activity_record_editor;
    }

    @Override
    protected void initView() {
        initParentView();

        showPlayerView();
    }

    private void showPlayerView() {
        if (playerEditPage == null) {
            playerEditPage = new PlayerEditPage(this);
            playerEditPage.initView();
        }
        groupPlayer.setVisibility(View.VISIBLE);
        groupMatch.setVisibility(View.GONE);
    }

    private void initParentView() {
        // 只有title必须在setSupportActionBar之前调用，其他都必须在setSupportActionBar之后调用
        toolbar.setTitle(getResources().getString(R.string.player_infor));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 加入了转场动画，必须用onBackPressed，finish无效果
                onBackPressed();
            }
        });
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
    }

    @Override
    protected EditorPresenter createPresenter() {
        return new EditorPresenter();
    }

    @Override
    protected void initData() {
        long userId = getIntent().getLongExtra(KEY_USER_ID, -1);
        long recordId = getIntent().getLongExtra(KEY_RECORD_ID, -1);
        presenter.init(userId, recordId);
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public EditorPresenter getPresenter() {
        return presenter;
    }

    @Override
    public void showUser(User user) {
        playerEditPage.showUser(user);
    }

    @Override
    public void showRecord(Record record) {
        playerEditPage.showRecord(record);
    }

    @Override
    public void showH2h(int win, int lose) {
        playerEditPage.showH2h(win, lose);
    }

    private void showMatchView() {
        if (matchEditPage == null) {
            matchEditPage = new MatchEditPage(this);
            matchEditPage.initView();
            matchEditPage.initData();
        }
        groupPlayer.setVisibility(View.GONE);
        groupMatch.setVisibility(View.VISIBLE);
    }

    @Override
    public void showMatchAutoFill(AutoFillMatchBean autoFill) {
        matchEditPage.showMatchAutoFill(autoFill);
    }

    @Override
    public void showMatchInfor(Record record, MatchNameBean mMatchNameBean, CompetitorBean mCompetitor, List<Score> mScoreList) {
        matchEditPage.showMatchInfor(record, mMatchNameBean, mCompetitor, mScoreList);
    }

    @OnClick({R.id.tv_next_page, R.id.tv_done, R.id.tv_continue, R.id.tv_previous_page})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_next_page:
                toolbar.setTitle(getResources().getString(R.string.match_infor));
                tvPreviousPage.setVisibility(View.VISIBLE);
                tvNextPage.setVisibility(View.GONE);
                tvDone.setVisibility(View.VISIBLE);
                showMatchView();
                break;
            case R.id.tv_done:
                if (tvContinue.getVisibility() == View.VISIBLE) {
                    finish();
                }
                else {
                    String errorMsg = playerEditPage.fillRecord();
                    if (!TextUtils.isEmpty(errorMsg)) {
                        showMessage(errorMsg);
                        return;
                    }
                    errorMsg = matchEditPage.fillRecord();
                    if (!TextUtils.isEmpty(errorMsg)) {
                        showMessage(errorMsg);
                        return;
                    }
                    matchEditPage.saveAutoFill();
                    presenter.insertOrUpdate();
                }
                break;
            case R.id.tv_continue:
                continueInsert();
                break;
            case R.id.tv_previous_page:
                toolbar.setTitle(getResources().getString(R.string.player_infor));
                tvPreviousPage.setVisibility(View.GONE);
                tvNextPage.setVisibility(View.VISIBLE);
                tvDone.setVisibility(View.GONE);
                showPlayerView();
                break;
        }
    }

    @Override
    public void insertSuccess() {
        showMessage("添加成功");
        setResult(RESULT_OK);
        tvContinue.setVisibility(View.VISIBLE);
    }

    @Override
    public void updateSuccess() {
        finish();
    }

    private void continueInsert() {
        presenter.reset();
        tvContinue.setVisibility(View.GONE);
        toolbar.setTitle(getResources().getString(R.string.player_infor));
        tvPreviousPage.setVisibility(View.GONE);
        tvNextPage.setVisibility(View.VISIBLE);
        tvDone.setVisibility(View.GONE);
        playerEditPage.reset();
        matchEditPage.reset();
        showPlayerView();
    }

    @Override
    public void selectMatch() {
        Intent intent = new Intent().setClass(this, MatchManageActivity.class);
        intent.putExtra(MatchManageActivity.KEY_START_MODE, MatchManageActivity.START_MODE_SELECT);
        startActivityForResult(intent, REQUEST_CHANGE_MATCH);
    }

    @Override
    public void selectPlayer() {
        Intent intent = new Intent().setClass(this, PlayerManageActivity.class);
        intent.putExtra(PlayerManageActivity.KEY_START_MODE, PlayerManageActivity.START_MODE_SELECT);
        startActivityForResult(intent, REQUEST_CHANGE_PLAYER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CHANGE_MATCH) {
            if (resultCode == RESULT_OK) {
                long matchId = data.getLongExtra(MatchManageActivity.RESPONSE_MATCH_NAME_ID, -1);
                presenter.queryMatch(matchId);
                matchEditPage.onMatchSelected(presenter.getMatchNameBean());
            }
        }
        if (requestCode == REQUEST_CHANGE_PLAYER) {
            if (resultCode == RESULT_OK) {
                long playerId = data.getLongExtra(PlayerManageActivity.RESPONSE_PLAYER_ID, -1);
                boolean isUser = data.getBooleanExtra(PlayerManageActivity.RESPONSE_PLAYER_IS_USER, false);
                presenter.queryCompetitor(playerId, isUser);
                playerEditPage.onPlayerSelected(presenter.getCompetitor());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
