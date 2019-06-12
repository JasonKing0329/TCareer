package com.king.app.tcareer.page.record.list;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Pair;
import android.view.View;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.mvvm.MvvmActivity;
import com.king.app.tcareer.databinding.ActivityRecordBinding;
import com.king.app.tcareer.page.match.page.MatchPageActivity;
import com.king.app.tcareer.page.record.editor.RecordEditorActivity;
import com.king.app.tcareer.page.record.page.RecordPageActivity;
import com.king.app.tcareer.page.record.search.SearchDialog;
import com.king.app.tcareer.utils.DebugLog;
import com.king.app.tcareer.utils.ScreenUtils;
import com.king.app.tcareer.view.dialog.AlertDialogFragment;
import com.king.app.tcareer.view.dialog.frame.FrameDialogFragment;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceAlignmentEnum;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.SimpleCircleButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;

import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/4/20 16:17
 */
public class RecordActivity extends MvvmActivity<ActivityRecordBinding, RecordViewModel> implements OnItemMenuListener, OnHeadLongClickListener
    , OnBMClickListener {

    public static final String KEY_USER_ID = "key_user_id";

    private final int REQUEST_UPDATE = 100;
    private final int REQUEST_RECORD_PAGE = 101;

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
    protected RecordViewModel createViewModel() {
        return ViewModelProviders.of(this).get(RecordViewModel.class);
    }

    @Override
    protected void initView() {
        mBinding.setModel(mModel);
        initToolbar();
        initBoomButton();
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mBinding.rvRecord.setLayoutManager(manager);
    }

    private void initToolbar() {
        setSupportActionBar(mBinding.toolbar);
        mBinding.toolbar.setNavigationOnClickListener(view -> {
            // 加入了转场动画，必须用onBackPressed，finish无效果
            onBackPressed();
        });
    }

    private void initBoomButton() {
        // 修改了源码，image自适应为button的一半中间，不需要再设置imagePadding了
//        int padding = mBinding.bmbMenu.getContext().getResources().getDimensionPixelSize(R.dimen.boom_menu_icon_padding);
        int radius = mBinding.bmbMenu.getContext().getResources().getDimensionPixelSize(R.dimen.boom_menu_btn_radius);
        mBinding.bmbMenu.setButtonEnum(ButtonEnum.SimpleCircle);
        mBinding.bmbMenu.setButtonRadius(radius);
        mBinding.bmbMenu.setPiecePlaceEnum(PiecePlaceEnum.DOT_3_1);
        mBinding.bmbMenu.setButtonPlaceEnum(ButtonPlaceEnum.Vertical);
        mBinding.bmbMenu.setButtonPlaceAlignmentEnum(ButtonPlaceAlignmentEnum.BR);
        mBinding.bmbMenu.setButtonRightMargin(mBinding.bmbMenu.getContext().getResources().getDimensionPixelSize(R.dimen.home_pop_menu_right));
        mBinding.bmbMenu.setButtonBottomMargin(mBinding.bmbMenu.getContext().getResources().getDimensionPixelSize(R.dimen.home_pop_menu_bottom));
        mBinding.bmbMenu.setButtonVerticalMargin(mBinding.bmbMenu.getContext().getResources().getDimensionPixelSize(R.dimen.boom_menu_btn_margin_ver));
        mBinding.bmbMenu.addBuilder(new SimpleCircleButton.Builder()
                .normalImageRes(R.drawable.ic_search_white_24dp)
                .buttonRadius(radius)
//                .imagePadding(new Rect(padding, padding, padding, padding))
                .listener(this));
        mBinding.bmbMenu.addBuilder(new SimpleCircleButton.Builder()
                .normalImageRes(R.drawable.ic_refresh_white_24dp)
                .buttonRadius(radius)
                .listener(this));
        mBinding.bmbMenu.addBuilder(new SimpleCircleButton.Builder()
                .normalImageRes(R.drawable.ic_arrow_upward_white_24dp)
                .buttonRadius(radius)
                .listener(this));
    }

    @Override
    protected void initData() {
        mModel.listObserver.observe(this, list -> showList(list));
        mModel.deletePosition.observe(this, position -> deleteSuccess(position));
        mModel.loadRecordData(getIntent().getLongExtra(KEY_USER_ID, -1));
    }

    private void showList(List<YearItem> list) {
        if (recordAdapter == null) {
            recordAdapter = new RecordAdapter(list, this, this);
            mBinding.rvRecord.setAdapter(recordAdapter);
        } else {
            recordAdapter.updateData(list);
        }
        // expand the first item, it's 1, not 0
        recordAdapter.expandParent(1);
    }

    @Override
    public void onUpdateRecord(final RecordItem recordItem) {
        mUpdatePosition = recordItem.getItemPosition();
        Intent intent = new Intent().setClass(this, RecordEditorActivity.class);
        intent.putExtra(RecordEditorActivity.KEY_USER_ID, mModel.getUser().getId());
        intent.putExtra(RecordEditorActivity.KEY_RECORD_ID, recordItem.getRecord().getId());
        startActivityForResult(intent, REQUEST_UPDATE);
    }

    private void showSearchDialog() {
        SearchDialog searchDialog = new SearchDialog();
        searchDialog.setRecordList(mModel.getRecordList());
        searchDialog.setOnRecordFilterListener(list -> {
            mModel.loadRecordData(list);
        });
        FrameDialogFragment dialogSearch = new FrameDialogFragment();
        dialogSearch.setContentFragment(searchDialog);
        dialogSearch.setTitle("Search");
        dialogSearch.setMaxHeight(ScreenUtils.getScreenHeight() * 4 / 5);
        dialogSearch.show(getSupportFragmentManager(), "SearchDialog");
    }

    @Override
    public void onDeleteRecord(final RecordItem record) {
        new AlertDialogFragment()
                .setMessage(getString(R.string.delete_confirm))
                .setPositiveText(getString(R.string.ok))
                .setPositiveListener((dialogInterface, i) -> doDelete(record))
                .setNegativeText(getString(R.string.cancel))
                .show(getSupportFragmentManager(), "AlertDialogFragment");
    }

    private void doDelete(RecordItem record) {
        mModel.delete(record.getRecord(), record.getItemPosition());
    }

    private void deleteSuccess(int viewPosition) {
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
                mBinding.rvRecord.smoothScrollToPosition(0);
                break;
        }
    }

    @Override
    public void onLongClickHead(View view, HeaderItem item) {
        Intent intent = new Intent();
        intent.setClass(this, MatchPageActivity.class);
        intent.putExtra(MatchPageActivity.KEY_USER_ID, mModel.getUser().getId());
        intent.putExtra(MatchPageActivity.KEY_MATCH_NAME_ID, item.getRecord().getMatchNameId());
        ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(this
                , Pair.create(view.findViewById(R.id.iv_match),getString(R.string.anim_match_page_head)));
        startActivity(intent, transitionActivityOptions.toBundle());
    }
}
