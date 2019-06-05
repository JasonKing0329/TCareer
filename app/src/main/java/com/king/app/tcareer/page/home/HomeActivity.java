package com.king.app.tcareer.page.home;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.mvvm.BaseBindingAdapter;
import com.king.app.tcareer.base.mvvm.MvvmActivity;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.databinding.ActivityHomeBinding;
import com.king.app.tcareer.model.ImageProvider;
import com.king.app.tcareer.model.SeasonManager;
import com.king.app.tcareer.model.bean.CompetitorBean;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.model.image.ImageBindingAdapter;
import com.king.app.tcareer.page.glory.GloryActivity;
import com.king.app.tcareer.page.home.main.MainHomeActivity;
import com.king.app.tcareer.page.match.gallery.UserMatchActivity;
import com.king.app.tcareer.page.match.gallery.UserMatchBean;
import com.king.app.tcareer.page.player.h2hlist.H2hListActivity;
import com.king.app.tcareer.page.player.page.PlayerPageActivity;
import com.king.app.tcareer.page.player.slider.PlayerSlideActivity;
import com.king.app.tcareer.page.player.slider.PlayerSlideAdapter;
import com.king.app.tcareer.page.player.slider.SlideItem;
import com.king.app.tcareer.page.rank.RankDetailActivity;
import com.king.app.tcareer.page.rank.RankManageActivity;
import com.king.app.tcareer.page.rank.RankWeekFragment;
import com.king.app.tcareer.page.rank.RankYearEndFragment;
import com.king.app.tcareer.page.record.editor.RecordEditorActivity;
import com.king.app.tcareer.page.record.list.RecordActivity;
import com.king.app.tcareer.page.score.ScoreActivity;
import com.king.app.tcareer.utils.DebugLog;
import com.king.app.tcareer.view.dialog.frame.FrameDialogFragment;
import com.king.app.tcareer.view.widget.discrete.transform.ScaleTransformer;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;

import java.util.List;

public class HomeActivity extends MvvmActivity<ActivityHomeBinding, HomeViewModel> implements OnBMClickListener, IHomeHeaderHolder {

    public static final String KEY_USER_ID = "user_id";

    private final int REQUEST_RANK = 101;
    private final int REQUEST_ADD = 102;
    private final int REQUEST_RECORD_LIST = 103;
    private final int REQUEST_SCORE = 104;
    private final int REQUEST_RANK_DETAIL = 105;

    private HomeHeadAdapter headAdapter;

    private HomeMatchAdapter matchAdapter;

    private RankYearEndFragment ftChart;

    private RankWeekFragment ftRankWeek;

    private BoomMenuHome boomMenuHome;

    private PlayerSlideAdapter playerSlideAdapter;

    @Override
    protected int getContentView() {
        return R.layout.activity_home;
    }

    @Override
    protected HomeViewModel createViewModel() {
        return ViewModelProviders.of(this).get(HomeViewModel.class);
    }

    @Override
    protected void initView() {

        boomMenuHome = new BoomMenuHome(mBinding.bmbMenu);
        initBoomMenu();
        initAppBar();

        // init match gallery
        mBinding.dsvMatch.setItemTransitionTimeMillis(200);
        mBinding.dsvMatch.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.9f)
                .build());

        mBinding.groupRecord.setOnClickListener(v -> startRecordLineActivity());
        mBinding.groupPlayer.setOnClickListener(v -> startPlayerActivity());
        mBinding.groupAdd.setOnClickListener(v -> startRecordEditorActivity());
        mBinding.groupGlory.setOnClickListener(v -> startGloryActivity());
        mBinding.groupH2h.setOnClickListener(v -> startPlayerH2hActivity());
    }

    private void initBoomMenu() {
        SeasonManager.SeasonEnum type = SeasonManager.getSeasonType();
        if (type == SeasonManager.SeasonEnum.CLAY) {
            boomMenuHome.init(BoomMenuHome.CLAY, this);
        } else if (type == SeasonManager.SeasonEnum.GRASS) {
            boomMenuHome.init(BoomMenuHome.GRASS, this);
        } else if (type == SeasonManager.SeasonEnum.INHARD) {
            boomMenuHome.init(BoomMenuHome.INHARD, this);
        } else {
            boomMenuHome.init(BoomMenuHome.HARD, this);
        }
    }

    private void initAppBar() {
        setSupportActionBar(mBinding.toolbar);
        mBinding.toolbar.setNavigationOnClickListener(view -> {
            startActivity(new Intent(HomeActivity.this, MainHomeActivity.class));
            finish();
        });
    }

    /**
     * 与数据库有关的数据初始化
     */
    @Override
    public void initData() {
        mModel.userObserver.observe(this, user -> {
            mBinding.ctlToolbar.setTitle(mModel.getUser().getNameEng());
            initRankChart();
            initRankWeekChart();
        });
        mModel.allUsersObserver.observe(this, list -> {
            initPlayerBasic();
        });
        mModel.lastRecordObserver.observe(this, record -> {
            mBinding.scrollHome.post(() -> startRevealView(500));
            refreshLatestMatch(record);
        });
        mModel.competitorsObserver.observe(this, list -> refreshPlayers(list));
        mModel.matchesObserver.observe(this, list -> showMatches(list));

        mModel.loadHomeDatas(getIntent().getLongExtra(KEY_USER_ID, AppConstants.USER_ID_KING));
    }

    private void initRankChart() {
        ftChart = RankYearEndFragment.newInstance(mModel.getUser().getId());
        ftChart.setOnChartGroupClickListener(v -> startRankManageActivity());
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.group_chart, ftChart, "RankYearEndFragment");
        ft.commit();
    }

    private void initRankWeekChart() {
        ftRankWeek = RankWeekFragment.newInstance(mModel.getUser().getId());
        ftRankWeek.setOnChartClickListener(v -> startWeekRankActivity(mModel.getUser().getId()));
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.group_chart_week, ftRankWeek, "RankWeekFragment");
        ft.commit();
    }

    private void initPlayerBasic() {
        headAdapter = new HomeHeadAdapter(getSupportFragmentManager());
        int index = 0;
        for (int i = 0; i < mModel.getAllUsers().size(); i++) {
            User user = mModel.getAllUsers().get(i);
            HomeHeadFragment fragment = HomeHeadFragment.newInstance(user.getId());
            headAdapter.addFragment(fragment);

            if (mModel.getUser().getId() == user.getId()) {
                index = i;
            }
        }
        mBinding.viewpagerHead.setAdapter(headAdapter);
        mBinding.viewpagerHead.setCurrentItem(index);

        mBinding.viewpagerHead.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                onUserChanged(mModel.getAllUsers().get(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void refreshLatestMatch(Record record) {
        String url = ImageProvider.getMatchHeadPath(record.getMatch().getName()
                , record.getMatch().getMatchBean().getCourt());
        ImageBindingAdapter.setMatchUrl(mBinding.ivRecordBk, url);
        mBinding.tvMatchName.setText(record.getMatch().getName() + "(" + record.getMatch().getMatchBean().getCountry() + ")");
        mBinding.tvMatchRound.setText(record.getRound());
    }

    private void refreshPlayers(List<SlideItem<CompetitorBean>> list) {
        if (playerSlideAdapter == null) {
            playerSlideAdapter = new PlayerSlideAdapter();
            playerSlideAdapter.setList(list);
            playerSlideAdapter.setOnItemClickListener((BaseBindingAdapter.OnItemClickListener<SlideItem<CompetitorBean>>) (view, position, bean) -> {
                Intent intent = new Intent().setClass(HomeActivity.this, PlayerPageActivity.class);
                intent.putExtra(PlayerPageActivity.KEY_USER_ID, mModel.getUser().getId());
                intent.putExtra(PlayerPageActivity.KEY_COMPETITOR_ID, bean.getBean().getId());
                if (bean.getBean() instanceof User) {
                    intent.putExtra(PlayerPageActivity.KEY_COMPETITOR_IS_USER, true);
                }
                startActivity(intent);
            });
            mBinding.rvPlayers.setAdapter(playerSlideAdapter);
        } else {
            playerSlideAdapter.setList(list);
            playerSlideAdapter.notifyDataSetChanged();
        }
    }

    private void showMatches(List<UserMatchBean> list) {
        if (matchAdapter == null) {
            matchAdapter = new HomeMatchAdapter();
            matchAdapter.setList(list);
            matchAdapter.setOnItemClickListener((view, position, data) -> startMatchActivity(position));
            mBinding.dsvMatch.setAdapter(matchAdapter);
        } else {
            matchAdapter.setList(list);
            matchAdapter.notifyDataSetChanged();
        }

        // 定位到与当前周最近赛事
        focusToLatestWeek();

    }

    private void focusToLatestWeek() {
        final int position = mModel.findLatestWeekItem();
        mBinding.dsvMatch.post(new Runnable() {
            @Override
            public void run() {
                mBinding.dsvMatch.scrollToPosition(position);
            }
        });
    }

    private void onUserChanged(User user) {
        mModel.changeUser(user);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_home_retire:
                markRetire();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void markRetire() {
        RetireDialog content = new RetireDialog();
        content.setUserId(mModel.getUser().getId());
        FrameDialogFragment dialogFragment = new FrameDialogFragment();
        dialogFragment.setContentFragment(content);
        dialogFragment.setTitle(mModel.getUser().getNameEng());
        dialogFragment.show(getSupportFragmentManager(), "FrameDialogFragment");
    }

    @Override
    public void onBoomButtonClick(int index) {
        switch (index) {
            case 0:
                executeSave();
                break;
            case 1:
                executeSaveAs();
                break;
            case 2:
                executeExit();
                break;
            case 3:
                DebugLog.e("3");
                mBinding.scrollHome.scrollTo(0, 0);
                break;
        }
    }

    private void executeExit() {
        finish();
    }

    private void executeSaveAs() {
//        BasicOperation.showSaveAsDialog(this, null);
    }

    private void executeSave() {
        mModel.saveDatabase();
    }

    @SuppressLint("RestrictedApi")
    private void startRankManageActivity() {
        Intent intent = new Intent().setClass(this, RankManageActivity.class);
        intent.putExtra(RankManageActivity.KEY_USER_ID, mModel.getUser().getId());
        ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(this
                , Pair.create(findViewById(R.id.group_chart), getString(R.string.anim_home_rank)));
        startActivityForResult(intent, REQUEST_RANK, transitionActivityOptions.toBundle());

    }

    @SuppressLint("RestrictedApi")
    private void startRecordEditorActivity() {
        Intent intent = new Intent().setClass(this, RecordEditorActivity.class);
        intent.putExtra(RecordEditorActivity.KEY_USER_ID, mModel.getUser().getId());
        ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(this
                , Pair.create(findViewById(R.id.group_add), getString(R.string.anim_home_add)));
        startActivityForResult(intent, REQUEST_ADD, transitionActivityOptions.toBundle());
    }

    private void startScoreActivity() {
        Intent intent = new Intent().setClass(this, ScoreActivity.class);
        intent.putExtra(ScoreActivity.KEY_USER_ID, mModel.getUser().getId());
        startActivityForResult(intent, REQUEST_SCORE);
    }

    private void startGloryActivity() {
        Intent intent = new Intent().setClass(this, GloryActivity.class);
        intent.putExtra(GloryActivity.KEY_USER_ID, mModel.getUser().getId());
        startActivity(intent);
    }

    private void startPlayerActivity() {
        Intent intent = new Intent().setClass(this, PlayerSlideActivity.class);
        intent.putExtra(PlayerSlideActivity.KEY_USER_ID, mModel.getUser().getId());
        startActivity(intent);
    }

    private void startMatchActivity(int position) {
        Intent intent = new Intent().setClass(this, UserMatchActivity.class);
        intent.putExtra(UserMatchActivity.KEY_START_POSITION, String.valueOf(position));
        intent.putExtra(UserMatchActivity.KEY_USER_ID, mModel.getUser().getId());
        startActivity(intent);
    }

    @SuppressLint("RestrictedApi")
    private void startRecordLineActivity() {
        Intent intent = new Intent().setClass(this, RecordActivity.class);
        intent.putExtra(RecordActivity.KEY_USER_ID, mModel.getUser().getId());
        ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(this
                , Pair.create(findViewById(R.id.iv_record_bk), getString(R.string.anim_home_date)));
        startActivityForResult(intent, REQUEST_RECORD_LIST, transitionActivityOptions.toBundle());
    }

    private void startPlayerH2hActivity() {
        Intent intent = new Intent().setClass(this, H2hListActivity.class);
        intent.putExtra(H2hListActivity.KEY_USER_ID, mModel.getUser().getId());
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_RANK) {
            // 更新rank chart
            if (resultCode == RESULT_OK) {
                ftChart.refreshRanks();
            }
        } else if (requestCode == REQUEST_SCORE) {
            if (resultCode == RESULT_OK) {
                headAdapter.getItem(mBinding.viewpagerHead.getCurrentItem()).onRankChanged();
            }
        } else if (requestCode == REQUEST_ADD) {
            if (resultCode == RESULT_OK) {
                // 添加过新数据，刷新record相关
                mModel.setRecordChanged();
            }
        } else if (requestCode == REQUEST_RECORD_LIST) {
            if (resultCode == RESULT_OK) {
                // 删除或修改过新数据，刷新record相关
                mModel.setRecordChanged();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClickScoreHead() {
        startScoreActivity();
    }

    private void startRevealView(int animTime) {
        // centerX和centerY实是相对于view的
        Animator anim = ViewAnimationUtils.createCircularReveal(mBinding.scrollHome, mBinding.scrollHome.getWidth() / 2
                , 0, 0, (float) mBinding.scrollHome.getHeight());
        anim.setDuration(animTime);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim.start();
    }

    @SuppressLint("RestrictedApi")
    private void startWeekRankActivity(long userId) {
        Intent intent = new Intent().setClass(this, RankDetailActivity.class);
        intent.putExtra(RankDetailActivity.KEY_USER_ID, userId);
        ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(this
                , Pair.create(findViewById(R.id.group_chart_week), getString(R.string.anim_home_rank_week)));
        startActivityForResult(intent, REQUEST_RANK_DETAIL, transitionActivityOptions.toBundle());
    }

}
