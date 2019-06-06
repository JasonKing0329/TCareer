package com.king.app.tcareer.page.record.page;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.bumptech.glide.Glide;
import com.king.app.tcareer.R;
import com.king.app.tcareer.base.mvvm.MvvmActivity;
import com.king.app.tcareer.databinding.ActivityRecordPageBinding;
import com.king.app.tcareer.model.CompetitorParser;
import com.king.app.tcareer.model.GlideOptions;
import com.king.app.tcareer.model.bean.CompetitorBean;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.model.palette.PaletteCallback;
import com.king.app.tcareer.model.palette.PaletteRequestListener;
import com.king.app.tcareer.model.palette.PaletteResponse;
import com.king.app.tcareer.model.palette.ViewColorBound;
import com.king.app.tcareer.page.match.MatchItemAdapter;
import com.king.app.tcareer.page.match.page.MatchPageActivity;
import com.king.app.tcareer.page.player.page.PlayerPageActivity;
import com.king.app.tcareer.page.record.editor.RecordEditorActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/22 13:38
 */
public class RecordPageActivity extends MvvmActivity<ActivityRecordPageBinding, RecordViewModel> {

    public static final String KEY_RECORD_ID = "record_id";

    private final int REQUEST_EDIT = 121;

    private MatchItemAdapter itemAdapter;

    private MenuItem mMenuEdit;

    @Override
    protected int getContentView() {
        return R.layout.activity_record_page;
    }

    @Override
    protected RecordViewModel createViewModel() {
        return ViewModelProviders.of(this).get(RecordViewModel.class);
    }

    @Override
    protected void initView() {
        mModel.setViewProvider(viewProvider);
        mBinding.setModel(mModel);

        setSupportActionBar(mBinding.toolbar);
        mBinding.toolbar.setNavigationOnClickListener(view -> onBackPressed());

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mBinding.groupInclude.rvRecords.setLayoutManager(manager);

        mBinding.ctlToolbar.post(() -> {
            // getScrimVisibleHeightTrigger里面用到了getHeight，要在控件布局完成后才有数值
            int trigger = mBinding.ctlToolbar.getScrimVisibleHeightTrigger();
            int total = getResources().getDimensionPixelSize(R.dimen.record_page_head_height);
            mBinding.appbarLayout.addOnOffsetChangedListener(new AppBarListener(total, trigger) {
                @Override
                protected void onCollapseStateChanged(boolean isCollapsing) {
                    mModel.handleCollapseScrimChanged(isCollapsing);
                }
            });
        });

        mBinding.groupInclude.tvH2h.setOnClickListener(v -> showPlayerPage());
        mBinding.groupInclude.tvMatchPage.setOnClickListener(v -> showMatchPage());
    }

    public static abstract class AppBarListener implements AppBarLayout.OnOffsetChangedListener {

        private int collapseHeight;
        private int scrimTrigger;

        private boolean isCollapsing;

        public AppBarListener(int collapseHeight, int scrimTrigger) {
            this.collapseHeight = collapseHeight;
            this.scrimTrigger = scrimTrigger;
        }

        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
            int offset = collapseHeight + verticalOffset;
            boolean collapsing = (offset <= scrimTrigger);
            if (collapsing != isCollapsing) {
                isCollapsing = collapsing;
                onCollapseStateChanged(isCollapsing);
            }
        }

        protected abstract void onCollapseStateChanged(boolean isCollapsing);
    }

    @Override
    protected void initData() {
        long recordId = getIntent().getLongExtra(KEY_RECORD_ID, -1);

        mModel.matchImageUrl.observe(this, url -> showMatchImage(url));
        mModel.recordsObserver.observe(this, list -> showMatchRecords(list));
        mModel.loadRecord(recordId);
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
        mMenuEdit = menu.findItem(R.id.menu_action_edit);
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
        intent.putExtra(RecordEditorActivity.KEY_USER_ID, mModel.getUser().getId());
        intent.putExtra(RecordEditorActivity.KEY_RECORD_ID, mModel.getRecord().getId());
        startActivityForResult(intent, REQUEST_EDIT);
    }

    private ViewProvider viewProvider = new ViewProvider() {
        @Override
        public Toolbar getToolbar() {
            return mBinding.toolbar;
        }

        @Override
        public CollapsingToolbarLayout getCollapsingToolbar() {
            return mBinding.ctlToolbar;
        }

        @Override
        public MenuItem getEditMenuItem() {
            return mMenuEdit;
        }

    };

    private void showMatchImage(String url) {
        Glide.with(this)
                .asBitmap()
                .load(url)
                .listener(new PaletteRequestListener(0, new PaletteCallback() {
                    @Override
                    public List<ViewColorBound> getTargetViews() {
                        List<ViewColorBound> list = new ArrayList<>();
                        ViewColorBound bound = new ViewColorBound();
                        bound.view = mBinding.toolbar;
                        bound.rect = mBinding.toolbar.getNavigationIcon().getBounds();
                        list.add(bound);

                        // FIXME 执行速度快于执行到 onCreateOptionsMenu的速度会出现空指针
                        if (mMenuEdit != null) {
                            bound = new ViewColorBound();
                            bound.view = mMenuEdit.getActionView();
                            bound.object = mMenuEdit;
                            bound.rect = mMenuEdit.getIcon().getBounds();
                            list.add(bound);
                        }
                        return list;
                    }

                    @Override
                    public void noPaletteResponseLoaded(int position) {

                    }

                    @Override
                    public void onPaletteResponse(int position, PaletteResponse response) {
                        mModel.handlePalette(response);
                    }
                }))
                .apply(GlideOptions.getDefaultMatchOptions())
                .into(mBinding.ivMatch);
    }

    private void showMatchRecords(List<Record> records) {
        itemAdapter = new MatchItemAdapter();
        itemAdapter.setList(records);
        itemAdapter.setFocusItem(mModel.getRecord());
        mBinding.groupInclude.rvRecords.setAdapter(itemAdapter);
    }

    private void showMatchPage() {
        Intent intent = new Intent();
        intent.setClass(this, MatchPageActivity.class);
        intent.putExtra(MatchPageActivity.KEY_USER_ID, mModel.getUser().getId());
        intent.putExtra(MatchPageActivity.KEY_MATCH_NAME_ID, mModel.getRecord().getMatchNameId());
        startActivity(intent);
    }

    private void showPlayerPage() {
        Intent intent = new Intent();
        intent.setClass(this, PlayerPageActivity.class);
        intent.putExtra(PlayerPageActivity.KEY_USER_ID, mModel.getUser().getId());
        CompetitorBean competitor = CompetitorParser.getCompetitorFrom(mModel.getRecord());
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
