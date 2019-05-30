package com.king.app.tcareer.page.home.main;

import android.content.Intent;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.BaseMvpActivity;
import com.king.app.tcareer.model.db.entity.MatchNameBean;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.page.home.HomeActivity;
import com.king.app.tcareer.page.match.recent.RecentMatchActivity;
import com.king.app.tcareer.page.record.editor.RecordEditorActivity;
import com.king.app.tcareer.utils.ScreenUtils;
import com.king.app.tcareer.view.adapter.BaseRecyclerAdapter;
import com.king.app.tcareer.view.widget.scoreboard.ScoreBoardParam;
import com.king.app.tcareer.view.widget.scoreboard.ScoreView;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/1/14 17:10
 */
public class MainHomeActivity extends BaseMvpActivity<MainHomePresenter> implements MainHomeView {

    private final int REQUEST_EDIT_RECORD = 1101;

    @BindView(R.id.tv_top)
    TextView tvTop;
    @BindView(R.id.rv_week)
    RecyclerView rvWeek;
    @BindView(R.id.rv_users)
    RecyclerView rvUsers;
    @BindView(R.id.tv_user)
    TextView tvUser;
    @BindView(R.id.tv_latest)
    TextView tvLatest;
    @BindView(R.id.rv_records)
    RecyclerView rvRecords;

    private RecordsAdapter recordsAdapter;
    private ScoreBoardAdapter scoreBoardAdapter;

    @Override
    protected int getContentView() {
        return R.layout.activity_main_home;
    }

    @Override
    protected void initView() {
        rvRecords.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvUsers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvUsers.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.left = ScreenUtils.dp2px(10);
            }
        });
        rvWeek.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvWeek.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.left = ScreenUtils.dp2px(8);
            }
        });

        tvLatest.setOnClickListener(v -> {
            Intent intent = new Intent().setClass(MainHomeActivity.this, RecordEditorActivity.class);
            intent.putExtra(RecordEditorActivity.KEY_CHOOSE_USER, true);
            startActivityForResult(intent, REQUEST_EDIT_RECORD);
        });
    }

    @Override
    protected MainHomePresenter createPresenter() {
        return new MainHomePresenter();
    }

    @Override
    protected void initData() {

    }

    @Override
    public void showUsers(List<User> users) {
        UserAdapter adapter = new UserAdapter();
        adapter.setList(users);
        adapter.setOnItemClickListener((position, data) -> {
            Intent intent = new Intent().setClass(MainHomeActivity.this, HomeActivity.class);
            intent.putExtra(HomeActivity.KEY_USER_ID, data.getId());
            startActivity(intent);
            finish();
        });
        rvUsers.setAdapter(adapter);
    }

    @Override
    public void showMatches(List<MatchNameBean> list) {
        MatchAdapter adapter = new MatchAdapter();
        adapter.setList(list);
        adapter.setOnItemClickListener((position, data) -> {
            Intent intent = new Intent().setClass(MainHomeActivity.this, RecentMatchActivity.class);
            intent.putExtra(RecentMatchActivity.KEY_MATCH_ID, data.getMatchId());
            intent.putExtra(RecentMatchActivity.KEY_YEAR, Calendar.getInstance().get(Calendar.YEAR));
            startActivity(intent);
        });
        rvWeek.setAdapter(adapter);
    }

    @Override
    public void showRecords(List<ComplexRecord> records) {
        if (recordsAdapter == null) {
            recordsAdapter = new RecordsAdapter();
            recordsAdapter.setList(records);
            recordsAdapter.setOnItemClickListener((position, data) -> {
                Intent intent = new Intent().setClass(MainHomeActivity.this, RecordEditorActivity.class);
                intent.putExtra(RecordEditorActivity.KEY_RECORD_ID, data.getRecord().getId());
                intent.putExtra(RecordEditorActivity.KEY_USER_ID, data.getRecord().getUserId());
                startActivityForResult(intent, REQUEST_EDIT_RECORD);
            });
            rvRecords.setAdapter(recordsAdapter);
        }
        else {
            recordsAdapter.setList(records);
            recordsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void showScoreBoards(List<ScoreBoardParam> records) {
        if (scoreBoardAdapter == null) {
            scoreBoardAdapter = new ScoreBoardAdapter();
            scoreBoardAdapter.setList(records);
            scoreBoardAdapter.setOnItemClickListener((position, data) -> {
                Intent intent = new Intent().setClass(MainHomeActivity.this, RecordEditorActivity.class);
                intent.putExtra(RecordEditorActivity.KEY_RECORD_ID, data.getRecord().getId());
                intent.putExtra(RecordEditorActivity.KEY_USER_ID, data.getRecord().getUserId());
                startActivityForResult(intent, REQUEST_EDIT_RECORD);
            });
            rvRecords.setAdapter(scoreBoardAdapter);
        }
        else {
            scoreBoardAdapter.setList(records);
            scoreBoardAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EDIT_RECORD) {
            if (resultCode == RESULT_OK) {
                presenter.loadRecords();
            }
        }
    }

    @Override
    public void postWeekInfo(String weekText) {
        runOnUiThread(() -> tvTop.setText(weekText));
    }
}
