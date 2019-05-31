package com.king.app.tcareer.page.home.main;

import android.app.ActivityOptions;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Rect;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.base.mvvm.MvvmActivity;
import com.king.app.tcareer.databinding.ActivityMainHomeBinding;
import com.king.app.tcareer.model.SeasonManager;
import com.king.app.tcareer.model.db.entity.MatchNameBean;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.page.home.HomeActivity;
import com.king.app.tcareer.page.home.NotifyRankAdapter;
import com.king.app.tcareer.page.home.NotifyRankBean;
import com.king.app.tcareer.page.match.manage.MatchManageActivity;
import com.king.app.tcareer.page.match.recent.RecentMatchActivity;
import com.king.app.tcareer.page.player.manage.PlayerManageActivity;
import com.king.app.tcareer.page.rank.RankDetailActivity;
import com.king.app.tcareer.page.record.complex.RecordComplexActivity;
import com.king.app.tcareer.page.record.editor.RecordEditorActivity;
import com.king.app.tcareer.page.setting.SettingActivity;
import com.king.app.tcareer.utils.ScreenUtils;
import com.king.app.tcareer.view.content.LoadFromContent;
import com.king.app.tcareer.view.dialog.CommonDialog;
import com.king.app.tcareer.view.widget.scoreboard.ScoreBoardParam;

import java.util.Calendar;
import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/1/14 17:10
 */
public class MainHomeActivity extends MvvmActivity<ActivityMainHomeBinding, MainHomeViewModel> {

    private final int REQUEST_EDIT_RECORD = 1101;
    private final int REQUEST_RANK_DETAIL = 1102;
    private final int REQUEST_RECORD_COMPLEX = 1103;

    private ScoreBoardAdapter scoreBoardAdapter;

    private NotifyRankAdapter notifyRankAdapter;

    @Override
    protected int getContentView() {
        return R.layout.activity_main_home;
    }

    @Override
    protected MainHomeViewModel createViewModel() {
        return ViewModelProviders.of(this).get(MainHomeViewModel.class);
    }

    @Override
    protected void initView() {
        mBinding.setModel(mModel);

        mBinding.actionbar.setOnClickListener(v -> mBinding.drawerLayout.openDrawer(GravityCompat.START));
        mBinding.actionbar.setOnMenuItemListener(menuId -> {
            switch (menuId) {
                case R.id.menu_home_save:
                    mModel.saveDatabase();
                    break;
                case R.id.menu_home_exit:
                    finish();
                    break;
            }
        });

        mBinding.groupNav.setOnClickListener(v -> {});// 防止事件透传
        mBinding.groupNavLoad.setOnClickListener(navGroupListener);
        mBinding.groupNavMatch.setOnClickListener(navGroupListener);
        mBinding.groupNavPlayer.setOnClickListener(navGroupListener);
        mBinding.groupNavSetting.setOnClickListener(navGroupListener);
        mBinding.tvNavComplex.setOnClickListener(navGroupListener);

        mBinding.tvSkip.setOnClickListener(v -> dismissNotify());
        mBinding.groupNotify.setOnClickListener(v -> {});// 防止事件透传

        mBinding.rvRecords.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mBinding.rvUsers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mBinding.rvUsers.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.left = ScreenUtils.dp2px(10);
            }
        });
        mBinding.rvWeek.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mBinding.rvWeek.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.left = ScreenUtils.dp2px(8);
            }
        });

        mBinding.tvLatest.setOnClickListener(v -> {
            Intent intent = new Intent().setClass(MainHomeActivity.this, RecordEditorActivity.class);
            intent.putExtra(RecordEditorActivity.KEY_CHOOSE_USER, true);
            startActivityForResult(intent, REQUEST_EDIT_RECORD);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 重新检测是否还有未更新的排名
        if (mBinding.groupNotify.getVisibility() == View.VISIBLE) {
            mModel.checkWeekRank();
        }
    }

    @Override
    public void onBackPressed() {

        if (mBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            mBinding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void initData() {
        // 按照当前月份的赛季属性更改相应的视图
        updateSeasonStyle();

        mModel.usersObserver.observe(this, users -> showUsers(users));
        mModel.matchObserver.observe(this, list -> showMatches(list));
        mModel.scoreboardsObserver.observe(this, list -> showScoreBoards(list));
        mModel.notifyRankFound.observe(this, list -> notifyRankFound(list));

        mModel.loadData();
    }

    private void updateSeasonStyle() {
        SeasonManager.SeasonEnum type = SeasonManager.getSeasonType();
        if (type == SeasonManager.SeasonEnum.CLAY) {
            mBinding.ivNavImage.setImageResource(R.drawable.nav_header_mon);
        } else if (type == SeasonManager.SeasonEnum.GRASS) {
            mBinding.ivNavImage.setImageResource(R.drawable.nav_header_win);
        } else if (type == SeasonManager.SeasonEnum.INHARD) {
            mBinding.ivNavImage.setImageResource(R.drawable.nav_header_sydney);
        } else {
            mBinding.ivNavImage.setImageResource(R.drawable.nav_header_iw);
        }
    }

    private void notifyRankFound(List<NotifyRankBean> list) {
        if (list.size() == 0) {
            mBinding.groupNotify.setVisibility(View.GONE);
        }
        else {
            mBinding.groupNotify.setVisibility(View.VISIBLE);
            if (notifyRankAdapter == null) {
                LinearLayoutManager manager = new LinearLayoutManager(this);
                manager.setOrientation(LinearLayoutManager.VERTICAL);
                mBinding.rvNotify.setLayoutManager(manager);

                notifyRankAdapter = new NotifyRankAdapter();
                notifyRankAdapter.setList(list);
                notifyRankAdapter.setOnItemListener((bean, position) -> startWeekRankActivity(bean.getUser().getId()));
                mBinding.rvNotify.setAdapter(notifyRankAdapter);
            }
            else {
                notifyRankAdapter.setList(list);
                notifyRankAdapter.notifyDataSetChanged();
            }

        }
    }

    private void startWeekRankActivity(long userId) {
        Intent intent = new Intent().setClass(this, RankDetailActivity.class);
        intent.putExtra(RankDetailActivity.KEY_USER_ID, userId);
        startActivity(intent);
    }

    private void showUsers(List<User> users) {
        UserAdapter adapter = new UserAdapter();
        adapter.setList(users);
        adapter.setOnItemClickListener((position, data) -> {
            Intent intent = new Intent().setClass(MainHomeActivity.this, HomeActivity.class);
            intent.putExtra(HomeActivity.KEY_USER_ID, data.getId());
            startActivity(intent);
            finish();
        });
        mBinding.rvUsers.setAdapter(adapter);
    }

    private void showMatches(List<MatchNameBean> list) {
        MatchAdapter adapter = new MatchAdapter();
        adapter.setList(list);
        adapter.setOnItemClickListener((position, data) -> {
            Intent intent = new Intent().setClass(MainHomeActivity.this, RecentMatchActivity.class);
            intent.putExtra(RecentMatchActivity.KEY_MATCH_ID, data.getMatchId());
            intent.putExtra(RecentMatchActivity.KEY_YEAR, Calendar.getInstance().get(Calendar.YEAR));
            startActivity(intent);
        });
        mBinding.rvWeek.setAdapter(adapter);
    }

    private void showScoreBoards(List<ScoreBoardParam> records) {
        if (scoreBoardAdapter == null) {
            scoreBoardAdapter = new ScoreBoardAdapter();
            scoreBoardAdapter.setList(records);
            scoreBoardAdapter.setOnItemClickListener((position, data) -> {
                Intent intent = new Intent().setClass(MainHomeActivity.this, RecordEditorActivity.class);
                intent.putExtra(RecordEditorActivity.KEY_RECORD_ID, data.getRecord().getId());
                intent.putExtra(RecordEditorActivity.KEY_USER_ID, data.getRecord().getUserId());
                startActivityForResult(intent, REQUEST_EDIT_RECORD);
            });
            mBinding.rvRecords.setAdapter(scoreBoardAdapter);
        }
        else {
            scoreBoardAdapter.setList(records);
            scoreBoardAdapter.notifyDataSetChanged();
        }
    }

    private View.OnClickListener navGroupListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.group_nav_player:
                    startPlayerManageActivity();
                    break;
                case R.id.group_nav_match:
                    startMatchManageActivity();
                    break;
                case R.id.group_nav_load:
                    showLoadFromDialog();
                    break;
                case R.id.group_nav_setting:
                    startSettingActivity();
                    break;
                case R.id.tv_nav_complex:
                    Intent intent = new Intent().setClass(MainHomeActivity.this, RecordComplexActivity.class);
                    startActivityForResult(intent, REQUEST_RECORD_COMPLEX);
                    break;
            }
        }
    };

    private void showLoadFromDialog() {
        LoadFromContent content = new LoadFromContent();
        content.setOnDatabaseChangedListener(() -> {
            TApplication.getInstance().reCreateGreenDao();
            mModel.loadData();
        });
        CommonDialog<LoadFromContent> dialog = new CommonDialog<>();
        dialog.setContentFragment(content);
        dialog.show(getSupportFragmentManager(), "LoadFromContent");
    }

    private void startPlayerManageActivity() {
        Intent intent = new Intent().setClass(this, PlayerManageActivity.class);
        ActivityOptions transitionActivityOptions = ActivityOptions.makeScaleUpAnimation(mBinding.groupNavPlayer, 0, 0, 100, 100);
        startActivity(intent, transitionActivityOptions.toBundle());
    }

    private void startMatchManageActivity() {
        Intent intent = new Intent().setClass(this, MatchManageActivity.class);
        ActivityOptions transitionActivityOptions = ActivityOptions.makeScaleUpAnimation(mBinding.groupNavMatch, 0, 0, 100, 100);
        startActivity(intent, transitionActivityOptions.toBundle());
    }

    private void startSettingActivity() {
        Intent intent = new Intent().setClass(this, SettingActivity.class);
        ActivityOptions transitionActivityOptions = ActivityOptions.makeScaleUpAnimation(mBinding.groupNavSetting, 0, 0, 100, 100);
        startActivity(intent, transitionActivityOptions.toBundle());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EDIT_RECORD) {
            if (resultCode == RESULT_OK) {
                mModel.loadRecords();
            }
        }
        else if (requestCode == REQUEST_RECORD_COMPLEX) {
            if (resultCode == RESULT_OK) {
                // 删除或修改过新数据，刷新record相关
                mModel.loadRecords();
            }
        }
    }

    private void dismissNotify() {
        Animation animation = new TranslateAnimation(0, mBinding.groupNotify.getWidth(), 0, 0);
        animation.setDuration(500);
        animation.setInterpolator(new AccelerateInterpolator());
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mBinding.groupNotify.setVisibility(View.GONE);
                mBinding.groupNotify.setTranslationX(0);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mBinding.groupNotify.startAnimation(animation);
    }
}
