package com.king.app.tcareer.page.match.page;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v7.widget.LinearLayoutManager;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.mvvm.MvvmActivity;
import com.king.app.tcareer.databinding.ActivityMatchPageBinding;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.page.record.page.RecordPageActivity;

import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/11/21 14:06
 */
public class MatchPageActivity extends MvvmActivity<ActivityMatchPageBinding, PageViewModel> {

    public static final String KEY_MATCH_NAME_ID = "key_match_name_id";
    public static final String KEY_USER_ID = "key_user_id";

    private PageRecordAdapter adapter;

    @Override
    protected int getContentView() {
        return R.layout.activity_match_page;
    }

    @Override
    protected void initView() {
        mBinding.setModel(mModel);
    }

    @Override
    protected PageViewModel createViewModel() {
        return ViewModelProviders.of(this).get(PageViewModel.class);
    }

    @Override
    protected void initData() {
        init();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        init();
    }

    private void init() {
        mBinding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_filterrable);
        mBinding.toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.dark_grey), PorterDuff.Mode.SRC_ATOP);
        mBinding.toolbar.setNavigationOnClickListener(v -> finish());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mBinding.rvRecords.setLayoutManager(manager);

        long matchNameId = getIntent().getLongExtra(KEY_MATCH_NAME_ID, -1);
        long userId = getIntent().getLongExtra(KEY_USER_ID, -1);

        mModel.recordsObserver.observe(this, list -> onRecordsLoaded(list));
        mModel.loadData(matchNameId, userId);
    }

    private void onRecordsLoaded(List<Object> list) {
        adapter = new PageRecordAdapter();
        adapter.setList(list);
        adapter.setOnItemClickListener((view, position, data) -> showRecord(data));
        mBinding.rvRecords.setAdapter(adapter);
    }

    private void showRecord(Record record) {
        Intent intent = new Intent();
        intent.setClass(this, RecordPageActivity.class);
        intent.putExtra(RecordPageActivity.KEY_RECORD_ID, record.getId());
        startActivity(intent);
    }
}
