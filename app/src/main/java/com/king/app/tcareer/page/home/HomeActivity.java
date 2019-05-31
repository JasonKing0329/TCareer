package com.king.app.tcareer.page.home;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.king.app.tcareer.R;
import com.king.app.tcareer.base.BaseMvpActivity;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.GlideOptions;
import com.king.app.tcareer.model.ImageProvider;
import com.king.app.tcareer.model.SeasonManager;
import com.king.app.tcareer.model.bean.CompetitorBean;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.page.glory.GloryActivity;
import com.king.app.tcareer.page.match.gallery.UserMatchActivity;
import com.king.app.tcareer.page.match.gallery.UserMatchBean;
import com.king.app.tcareer.page.player.h2hlist.H2hListActivity;
import com.king.app.tcareer.page.player.page.PlayerPageActivity;
import com.king.app.tcareer.page.player.slider.PlayerSlideActivity;
import com.king.app.tcareer.page.player.slider.PlayerSlideAdapter;
import com.king.app.tcareer.page.rank.RankDetailActivity;
import com.king.app.tcareer.page.rank.RankManageActivity;
import com.king.app.tcareer.page.rank.RankWeekFragment;
import com.king.app.tcareer.page.rank.RankYearEndFragment;
import com.king.app.tcareer.page.record.editor.RecordEditorActivity;
import com.king.app.tcareer.page.record.list.RecordActivity;
import com.king.app.tcareer.page.score.ScoreActivity;
import com.king.app.tcareer.utils.DebugLog;
import com.king.app.tcareer.view.dialog.frame.FrameDialogFragment;
import com.king.app.tcareer.view.widget.discrete.DiscreteScrollView;
import com.king.app.tcareer.view.widget.discrete.transform.ScaleTransformer;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomMenuButton;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class HomeActivity extends BaseMvpActivity<HomePresenter> implements IHomeView, OnBMClickListener, IHomeHeaderHolder {

    public static final String KEY_USER_ID = "user_id";

    private final int REQUEST_RANK = 101;
    private final int REQUEST_ADD = 102;
    private final int REQUEST_RECORD_LIST = 103;
    private final int REQUEST_SCORE = 104;
    private final int REQUEST_RANK_DETAIL = 105;

    private HomeHeadAdapter headAdapter;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.ctl_toolbar)
    CollapsingToolbarLayout ctlToolbar;
    @BindView(R.id.viewpager_head)
    ViewPager viewpagerHead;
    @BindView(R.id.iv_record_bk)
    RoundedImageView ivRecordBk;
    @BindView(R.id.tv_match_round)
    TextView tvMatchRound;
    @BindView(R.id.tv_match_name)
    TextView tvMatchName;
    @BindView(R.id.group_record)
    RelativeLayout groupRecord;
    @BindView(R.id.rv_players)
    RecyclerView rvPlayers;
    @BindView(R.id.group_add)
    ViewGroup groupAdd;
    @BindView(R.id.dsv_match)
    DiscreteScrollView dsvMatch;
    @BindView(R.id.group_glory)
    LinearLayout groupGlory;
    @BindView(R.id.group_h2h)
    LinearLayout groupH2h;

    @BindView(R.id.scroll_home)
    NestedScrollView scrollHome;
    @BindView(R.id.bmb_menu)
    BoomMenuButton bmbMenu;

    private HomeMatchAdapter matchAdapter;

    private List<UserMatchBean> matchList;

    private RankYearEndFragment ftChart;

    private RankWeekFragment ftRankWeek;

    private BoomMenuHome boomMenuHome;

    private PlayerSlideAdapter playerSlideAdapter;

    @Override
    protected int getContentView() {
        return R.layout.activity_home;
    }

    @Override
    protected void initView() {
        boomMenuHome = new BoomMenuHome(bmbMenu);
        initBoomMenu();
        initAppBar();

        // init match gallery
        dsvMatch.setItemTransitionTimeMillis(200);
        dsvMatch.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.9f)
                .build());

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
        setSupportActionBar(toolbar);
    }

    @Override
    protected HomePresenter createPresenter() {
        return new HomePresenter();
    }

    /**
     * 与数据库有关的数据初始化
     */
    @Override
    public void initData() {
        presenter.loadHomeDatas(getIntent().getLongExtra(KEY_USER_ID, AppConstants.USER_ID_KING));
    }

    @Override
    public void postShowCurrentUser() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ctlToolbar.setTitle(presenter.getUser().getNameEng());
                initRankChart();
                initRankWeekChart();
            }
        });
    }

    private void initRankChart() {
        ftChart = RankYearEndFragment.newInstance(presenter.getUser().getId());
        ftChart.setOnChartGroupClickListener(v -> startRankManageActivity());
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.group_chart, ftChart, "RankYearEndFragment");
        ft.commit();
    }

    private void initRankWeekChart() {
        ftRankWeek = RankWeekFragment.newInstance(presenter.getUser().getId());
        ftRankWeek.setOnChartClickListener(v -> startWeekRankActivity(presenter.getUser().getId()));
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.group_chart_week, ftRankWeek, "RankWeekFragment");
        ft.commit();
    }

    @Override
    public void postShowAllUsers() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initPlayerBasic();
            }
        });
    }

    private void showUserSelector() {
        String[] names = new String[presenter.getAllUsers().size()];
        for (int i = 0; i < names.length; i++) {
            names[i] = presenter.getAllUsers().get(i).getNameEng();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(null).setItems(names, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int index) {
                viewpagerHead.setCurrentItem(index);
            }
        }).show();
    }

    private void initPlayerBasic() {
        headAdapter = new HomeHeadAdapter(getSupportFragmentManager());
        int index = 0;
        for (int i = 0; i < presenter.getAllUsers().size(); i++) {
            User user = presenter.getAllUsers().get(i);
            HomeHeadFragment fragment = HomeHeadFragment.newInstance(user.getId());
            headAdapter.addFragment(fragment);

            if (presenter.getUser().getId() == user.getId()) {
                index = i;
            }
        }
        viewpagerHead.setAdapter(headAdapter);
        viewpagerHead.setCurrentItem(index);

        viewpagerHead.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                onUserChanged(presenter.getAllUsers().get(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void postShowLastRecord(final Record record) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // reveal animation
                scrollHome.post(new Runnable() {
                    @Override
                    public void run() {
                        startRevealView(500);
                    }
                });
                refreshLatestMatch(record);
            }
        });
    }

    private void refreshLatestMatch(Record record) {
        Glide.with(this)
                .load(ImageProvider.getMatchHeadPath(record.getMatch().getName()
                        , record.getMatch().getMatchBean().getCourt()))
                .apply(GlideOptions.getDefaultMatchOptions())
                .into(ivRecordBk);
        tvMatchName.setText(record.getMatch().getName() + "(" + record.getMatch().getMatchBean().getCountry() + ")");
        tvMatchRound.setText(record.getRound());
    }

    @Override
    public void postShowCompetitors(final List<CompetitorBean> list) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                refreshPlayers(list);
            }
        });
    }

    private void refreshPlayers(List<CompetitorBean> list) {
        if (playerSlideAdapter == null) {
            playerSlideAdapter = new PlayerSlideAdapter<CompetitorBean>() {
                @Override
                protected String getImageKey(CompetitorBean item) {
                    return item.getNameChn();
                }
            };
            playerSlideAdapter.setList(list);
            playerSlideAdapter.setOnPlayerItemListener(new PlayerSlideAdapter.OnPlayerItemListener<CompetitorBean>() {
                @Override
                public void onClickPlayer(CompetitorBean bean, int position) {
                    Intent intent = new Intent().setClass(HomeActivity.this, PlayerPageActivity.class);
                    intent.putExtra(PlayerPageActivity.KEY_USER_ID, presenter.getUser().getId());
                    intent.putExtra(PlayerPageActivity.KEY_COMPETITOR_ID, bean.getId());
                    if (bean instanceof User) {
                        intent.putExtra(PlayerPageActivity.KEY_COMPETITOR_IS_USER, true);
                    }
                    startActivity(intent);
                }
            });
            rvPlayers.setAdapter(playerSlideAdapter);
        } else {
            playerSlideAdapter.setList(list);
            playerSlideAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void showMatches(List<UserMatchBean> list) {
        if (matchAdapter == null) {
            matchAdapter = new HomeMatchAdapter(list);
            dsvMatch.setAdapter(matchAdapter);
            matchAdapter.setItemOnclickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startMatchActivity((Integer) view.getTag());
                }
            });
        } else {
            matchAdapter.setDatas(list);
            matchAdapter.notifyDataSetChanged();
        }

        // 定位到与当前周最近赛事
        focusToLatestWeek();

    }

    private void focusToLatestWeek() {
        final int position = presenter.findLatestWeekItem();
        dsvMatch.post(new Runnable() {
            @Override
            public void run() {
                dsvMatch.scrollToPosition(position);
            }
        });
    }

    private void onUserChanged(User user) {
        presenter.changeUser(user);
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
        content.setUserId(presenter.getUser().getId());
        FrameDialogFragment dialogFragment = new FrameDialogFragment();
        dialogFragment.setContentFragment(content);
        dialogFragment.setTitle(presenter.getUser().getNameEng());
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
                scrollHome.scrollTo(0, 0);
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
        presenter.saveDatabase();
    }

    @OnClick({R.id.group_record, R.id.group_player, R.id.group_add, R.id.group_glory, R.id.group_h2h})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.group_record:
                startRecordLineActivity();
                break;
            case R.id.group_player:
                startPlayerActivity();
                break;
            case R.id.group_add:
                startRecordEditorActivity();
                break;
            case R.id.group_glory:
                startGloryActivity();
                break;
            case R.id.group_h2h:
                startPlayerH2hActivity();
                break;
        }
    }

    @SuppressLint("RestrictedApi")
    private void startRankManageActivity() {
        Intent intent = new Intent().setClass(this, RankManageActivity.class);
        intent.putExtra(RankManageActivity.KEY_USER_ID, presenter.getUser().getId());
        ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(this
                , Pair.create(findViewById(R.id.group_chart), getString(R.string.anim_home_rank)));
        startActivityForResult(intent, REQUEST_RANK, transitionActivityOptions.toBundle());

    }

    @SuppressLint("RestrictedApi")
    private void startRecordEditorActivity() {
        Intent intent = new Intent().setClass(this, RecordEditorActivity.class);
        intent.putExtra(RecordEditorActivity.KEY_USER_ID, presenter.getUser().getId());
        ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(this
                , Pair.create(findViewById(R.id.group_add), getString(R.string.anim_home_add)));
        startActivityForResult(intent, REQUEST_ADD, transitionActivityOptions.toBundle());
    }

    private void startScoreActivity() {
        Intent intent = new Intent().setClass(this, ScoreActivity.class);
        intent.putExtra(ScoreActivity.KEY_USER_ID, presenter.getUser().getId());
        startActivityForResult(intent, REQUEST_SCORE);
    }

    private void startGloryActivity() {
        Intent intent = new Intent().setClass(this, GloryActivity.class);
        intent.putExtra(GloryActivity.KEY_USER_ID, presenter.getUser().getId());
        startActivity(intent);
    }

    private void startPlayerActivity() {
        Intent intent = new Intent().setClass(this, PlayerSlideActivity.class);
        intent.putExtra(PlayerSlideActivity.KEY_USER_ID, presenter.getUser().getId());
        startActivity(intent);
    }

    private void startMatchActivity(int position) {
        Intent intent = new Intent().setClass(this, UserMatchActivity.class);
        intent.putExtra(UserMatchActivity.KEY_START_POSITION, String.valueOf(position));
        intent.putExtra(UserMatchActivity.KEY_USER_ID, presenter.getUser().getId());
        startActivity(intent);
    }

    @SuppressLint("RestrictedApi")
    private void startRecordLineActivity() {
        Intent intent = new Intent().setClass(this, RecordActivity.class);
        intent.putExtra(RecordActivity.KEY_USER_ID, presenter.getUser().getId());
        ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(this
                , Pair.create(findViewById(R.id.iv_record_bk), getString(R.string.anim_home_date)));
        startActivityForResult(intent, REQUEST_RECORD_LIST, transitionActivityOptions.toBundle());
    }

    private void startPlayerH2hActivity() {
        Intent intent = new Intent().setClass(this, H2hListActivity.class);
        intent.putExtra(H2hListActivity.KEY_USER_ID, presenter.getUser().getId());
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
                headAdapter.getItem(viewpagerHead.getCurrentItem()).onRankChanged();
            }
        } else if (requestCode == REQUEST_ADD) {
            if (resultCode == RESULT_OK) {
                // 添加过新数据，刷新record相关
                presenter.setRecordChanged();
            }
        } else if (requestCode == REQUEST_RECORD_LIST) {
            if (resultCode == RESULT_OK) {
                // 删除或修改过新数据，刷新record相关
                presenter.setRecordChanged();
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
        Animator anim = ViewAnimationUtils.createCircularReveal(scrollHome, scrollHome.getWidth() / 2
                , 0, 0, (float) scrollHome.getHeight());
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
