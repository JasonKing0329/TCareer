package com.king.app.tcareer.page.glory.title;

import android.support.v7.widget.RecyclerView;

import com.king.app.tcareer.page.glory.GloryRecordAdapter;
import com.king.app.tcareer.page.glory.bean.GloryRecordItem;

import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/6/12 16:16
 */
public class SeqRunnerupListFragment extends AbsGloryListFragment {
    @Override
    protected RecyclerView.Adapter getListAdapter() {
        List<GloryRecordItem> recordList = gloryHolder.getGloryTitle().getRunnerUpList();
        GloryRecordAdapter adapter = new GloryRecordAdapter();
        adapter.setList(recordList);
        adapter.setOnItemClickListener((view, position, data) -> showGloryMatchDialog(data.getRecord()));
        return adapter;
    }
}
