package com.king.app.tcareer.page.record.list;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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
import com.king.app.tcareer.page.match.page.MatchPageActivity;
import com.king.app.tcareer.page.record.editor.RecordEditorActivity;
import com.king.app.tcareer.page.record.page.RecordPageActivity;
import com.king.app.tcareer.page.record.search.SearchDialog;
import com.king.app.tcareer.utils.DebugLog;
import com.king.app.tcareer.utils.ListUtil;
import com.king.app.tcareer.view.dialog.AlertDialogFragment;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceAlignmentEnum;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.SimpleCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/4/20 16:17
 */
public class RecordActivity extends BaseMvpActivity<RecordPresenter> implements IRecordView, OnItemMenuListener, OnHeadLongClickListener
    , OnBMClickListener {

    public static final String KEY_USER_ID = "key_user_id";

    private final int REQUEST_UPDATE = 100;
    private final int REQUEST_RECORD_PAGE = 101;

    @BindView(R.id.rv_record)
    RecyclerView rvRecord;
    @BindView(R.id.iv_record_head)
    ImageView ivRecordHead;
    @BindView(R.id.tv_career_winlose)
    TextView tvCareerWinlose;
    @BindView(R.id.tv_career_rate)
    TextView tvCareerRate;
    @BindView(R.id.tv_year_title)
    TextView tvYearTitle;
    @BindView(R.id.tv_year_winlose)
    TextView tvYearWinlose;
    @BindView(R.id.tv_year_rate)
    TextView tvYearRate;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.ctl_toolbar)
    CollapsingToolbarLayout ctlToolbar;
    @BindView(R.id.bmb_menu)
    BoomMenuButton bmbMenu;

    private RecordAdapter recordAdapter;

    /**
     * the item view position to update
     */
    private int mUpdatePosition;

    @Override
    protected int getContentView() {
        return R.layout.activity_record;
    }

    @Override
    protected void initView() {
        initToolbar();
        initBoomButton();
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rvRecord.setLayoutManager(manager);
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 加入了转场动画，必须用onBackPressed，finish无效果
                onBackPressed();
            }
        });
    }

    private void initBoomButton() {
        // 修改了源码，image自适应为button的一半中间，不需要再设置imagePadding了
//        int padding = bmbMenu.getContext().getResources().getDimensionPixelSize(R.dimen.boom_menu_icon_padding);
        int radius = bmbMenu.getContext().getResources().getDimensionPixelSize(R.dimen.boom_menu_btn_radius);
        bmbMenu.setButtonEnum(ButtonEnum.SimpleCircle);
        bmbMenu.setButtonRadius(radius);
        bmbMenu.setPiecePlaceEnum(PiecePlaceEnum.DOT_3_1);
        bmbMenu.setButtonPlaceEnum(ButtonPlaceEnum.Vertical);
        bmbMenu.setButtonPlaceAlignmentEnum(ButtonPlaceAlignmentEnum.BR);
        bmbMenu.setButtonRightMargin(bmbMenu.getContext().getResources().getDimensionPixelSize(R.dimen.home_pop_menu_right));
        bmbMenu.setButtonBottomMargin(bmbMenu.getContext().getResources().getDimensionPixelSize(R.dimen.home_pop_menu_bottom));
        bmbMenu.setButtonVerticalMargin(bmbMenu.getContext().getResources().getDimensionPixelSize(R.dimen.boom_menu_btn_margin_ver));
        bmbMenu.addBuilder(new SimpleCircleButton.Builder()
                .normalImageRes(R.drawable.ic_search_white_24dp)
                .buttonRadius(radius)
//                .imagePadding(new Rect(padding, padding, padding, padding))
                .listener(this));
        bmbMenu.addBuilder(new SimpleCircleButton.Builder()
                .normalImageRes(R.drawable.ic_refresh_white_24dp)
                .buttonRadius(radius)
                .listener(this));
        bmbMenu.addBuilder(new SimpleCircleButton.Builder()
                .normalImageRes(R.drawable.ic_arrow_upward_white_24dp)
                .buttonRadius(radius)
                .listener(this));
    }

    @Override
    protected RecordPresenter createPresenter() {
        return new RecordPresenter();
    }

    @Override
    protected void initData() {
        presenter.loadRecordDatas(getIntent().getLongExtra(KEY_USER_ID, -1));
    }

    @Override
    public void postShowUser() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ctlToolbar.setTitle(presenter.getUser().getNameEng());
            }
        });
    }

    @Override
    public void onRecordDataLoaded(RecordPageData data) {

        if (TextUtils.isEmpty(data.getCareerRate())) {
            tvCareerRate.setVisibility(View.GONE);
        }
        else {
            tvCareerRate.setVisibility(View.VISIBLE);
            tvCareerRate.setText(data.getCareerRate());
        }
        tvCareerWinlose.setText("Win " + data.getCareerWin() + "   Lose " + data.getCareerLose());
        tvYearTitle.setText(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));

        if (TextUtils.isEmpty(data.getYearRate())) {
            tvYearRate.setVisibility(View.GONE);
        }
        else {
            tvYearRate.setVisibility(View.VISIBLE);
            tvYearRate.setText(data.getYearRate());
        }
        tvYearWinlose.setText("Win " + data.getYearWin() + "   Lose " + data.getYearLose());

        // year默认展开
        List<YearItem> yearList = data.getYearList();
        for (YearItem item:yearList) {
            item.setExpanded(true);
        }

        if (recordAdapter == null) {
            recordAdapter = new RecordAdapter(data.getYearList(), this, this);
            rvRecord.setAdapter(recordAdapter);
        } else {
            recordAdapter.updateData(data.getYearList());
        }
        if (!ListUtil.isEmpty(data.getYearList())) {
            Record record = data.getYearList().get(0).getChildItemList().get(0).getRecord();
            String path = ImageProvider.getMatchHeadPath(record.getMatch().getName(), record.getMatch().getMatchBean().getCourt());

            Glide.with(this)
                    .load(path)
                    .apply(GlideOptions.getEditorMatchOptions())
                    .into(ivRecordHead);
        }

        // expand the first item, it's 1, not 0
        recordAdapter.expandParent(1);
    }

    @Override
    public void onUpdateRecord(final RecordItem recordItem) {
        mUpdatePosition = recordItem.getItemPosition();
        Intent intent = new Intent().setClass(this, RecordEditorActivity.class);
        intent.putExtra(RecordEditorActivity.KEY_USER_ID, presenter.getUser().getId());
        intent.putExtra(RecordEditorActivity.KEY_RECORD_ID, recordItem.getRecord().getId());
        startActivityForResult(intent, REQUEST_UPDATE);
    }

    private void showSearchDialog() {
        SearchDialog searchDialog = new SearchDialog();
        searchDialog.setRecordList(presenter.getRecordList());
        searchDialog.setOnRecordFilterListener(new SearchDialog.OnRecordFilterListener() {
            @Override
            public void recordFiltered(List<Record> list) {
                presenter.loadRecordDatas(list);
            }
        });
        searchDialog.show(getSupportFragmentManager(), "SearchDialog");
    }

    @Override
    public void onDeleteRecord(final RecordItem record) {
        new AlertDialogFragment()
                .setMessage(getString(R.string.delete_confirm))
                .setPositiveText(getString(R.string.ok))
                .setPositiveListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        doDelete(record);
                    }
                })
                .setNegativeText(getString(R.string.cancel))
                .show(getSupportFragmentManager(), "AlertDialogFragment");
    }

    private void doDelete(RecordItem record) {
        presenter.delete(record.getRecord(), record.getItemPosition());
    }

    @Override
    public void deleteSuccess(int viewPosition) {
        setResult(RESULT_OK);
        // delete from list
        // 通过调试发现，itemPosition是在整个recycler view里的position，框架对根据position做的3级显示，整个position都是顺序排列的
        recordAdapter.removedItem(viewPosition);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onItemClicked(View view, RecordItem recordItem) {
        Intent intent = new Intent();
        intent.setClass(this, RecordPageActivity.class);
        intent.putExtra(RecordPageActivity.KEY_RECORD_ID, recordItem.getRecord().getId());
        ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(this
                , Pair.create(view.findViewById(R.id.iv_player),getString(R.string.anim_player_page_head)));
        startActivityForResult(intent, REQUEST_RECORD_PAGE, transitionActivityOptions.toBundle());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_UPDATE:
            case REQUEST_RECORD_PAGE:
                if (resultCode == RESULT_OK) {
                    DebugLog.e("update " + mUpdatePosition);
                    recordAdapter.notifyItemChanged(mUpdatePosition);
                    // notify home
                    setResult(RESULT_OK);
                }
                break;
        }
    }

    @Override
    public void onBoomButtonClick(int index) {
        switch (index) {
            case 0:
                showSearchDialog();
                break;
            case 1:
                initData();
                break;
            case 2:
                rvRecord.smoothScrollToPosition(0);
                break;
        }
    }

    @Override
    public void onLongClickHead(View view, HeaderItem item) {
        Intent intent = new Intent();
        intent.setClass(this, MatchPageActivity.class);
        intent.putExtra(MatchPageActivity.KEY_USER_ID, presenter.getUser().getId());
        intent.putExtra(MatchPageActivity.KEY_MATCH_NAME_ID, item.getRecord().getMatchNameId());
        ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(this
                , Pair.create(view.findViewById(R.id.iv_match),getString(R.string.anim_match_page_head)));
        startActivity(intent, transitionActivityOptions.toBundle());
    }
}
