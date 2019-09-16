package com.king.app.tcareer.page.record.complex;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.mvvm.MvvmActivity;
import com.king.app.tcareer.databinding.ActivityRecordComplexBinding;
import com.king.app.tcareer.page.compare.CareerCompareActivity;
import com.king.app.tcareer.page.record.editor.RecordEditorActivity;
import com.king.app.tcareer.page.record.list.OnItemMenuListener;
import com.king.app.tcareer.page.record.list.RecordItem;
import com.king.app.tcareer.page.record.page.RecordPageActivity;
import com.king.app.tcareer.utils.DebugLog;
import com.king.app.tcareer.view.dialog.AlertDialogFragment;
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
 * <p/>创建时间: 2018/3/2 14:23
 */
public class RecordComplexActivity extends MvvmActivity<ActivityRecordComplexBinding, ComplexViewModel> implements 
        OnBMClickListener, OnItemMenuListener {

    private final int REQUEST_UPDATE = 100;
    private final int REQUEST_RECORD_PAGE = 101;

    private RecordAdapter recordAdapter;

    /**
     * the item view position to update
     */
    private int mUpdatePosition;

    @Override
    protected int getContentView() {
        return R.layout.activity_record_complex;
    }

    @Override
    protected ComplexViewModel createViewModel() {
        return ViewModelProviders.of(this).get(ComplexViewModel.class);
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

    @Override
    protected void initData() {
        mModel.listObserver.observe(this, list -> showItems(list));
        mModel.deletePosition.observe(this, position -> {
            setResult(RESULT_OK);
            // delete from list
            // 通过调试发现，itemPosition是在整个recycler view里的position，框架对根据position做的3级显示，整个position都是顺序排列的
            recordAdapter.removedItem(position);
        });
        mModel.loadRecords();
    }

    private void initToolbar() {
        setSupportActionBar(mBinding.toolbar);
        mBinding.toolbar.setNavigationOnClickListener(view -> {
            // 加入了转场动画，必须用onBackPressed，finish无效果
            onBackPressed();
        });
        mBinding.toolbar.setTitle("全部记录");
        mBinding.ctlToolbar.setTitle("全部记录");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.record_complex, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_action_chart:
                showCareerCompare();
                break;
        }
        return true;
    }

    private void showCareerCompare() {
        Intent intent = new Intent();
        intent.setClass(this, CareerCompareActivity.class);
        startActivity(intent);
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

    private void showItems(List<YearItem> yearItems) {
        // year默认展开
        for (YearItem item:yearItems) {
            item.setExpanded(true);
        }

        if (recordAdapter == null) {
            recordAdapter = new RecordAdapter(yearItems, this);
            mBinding.rvRecord.setAdapter(recordAdapter);
        }
        else {
            recordAdapter.updateData(yearItems);
        }
        // expand the first item, it's 1, not 0
        recordAdapter.expandParent(1);
    }

    @Override
    public void onBoomButtonClick(int index) {
        switch (index) {
            case 0:// search
                break;
            case 1:// refresh
                mModel.loadRecords();
                break;
            case 2:// go top
                mBinding.rvRecord.scrollToPosition(0);
                break;
        }
    }

    @Override
    public void onUpdateRecord(RecordItem record) {
        mUpdatePosition = record.getItemPosition();
        Intent intent = new Intent().setClass(this, RecordEditorActivity.class);
        intent.putExtra(RecordEditorActivity.KEY_USER_ID, record.getRecord().getUserId());
        intent.putExtra(RecordEditorActivity.KEY_RECORD_ID, record.getRecord().getId());
        startActivityForResult(intent, REQUEST_UPDATE);
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

    @SuppressLint("RestrictedApi")
    @Override
    public void onItemClicked(View view, RecordItem record) {
        Intent intent = new Intent();
        intent.setClass(this, RecordPageActivity.class);
        intent.putExtra(RecordPageActivity.KEY_RECORD_ID, record.getRecord().getId());
        ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(this
                , Pair.create(view.findViewById(R.id.iv_player),getString(R.string.anim_player_page_head))
                , Pair.create(view.findViewById(R.id.iv_user),getString(R.string.anim_user_page_head)));
        startActivityForResult(intent, REQUEST_RECORD_PAGE, transitionActivityOptions.toBundle());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_UPDATE:
            case REQUEST_RECORD_PAGE:
                DebugLog.e("update " + mUpdatePosition);
                recordAdapter.notifyItemChanged(mUpdatePosition);
                // notify home
                setResult(RESULT_OK);
                break;
        }
    }
}
