package com.king.app.tcareer.page.glory.title;

import android.support.v7.widget.RecyclerView;

import com.king.app.tcareer.page.glory.bean.GloryRecordItem;

import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/6/12 16:17
 */
public class ExpandRunnerupListFragment extends AbsGloryListFragment {
    @Override
    protected RecyclerView.Adapter getListAdapter() {
        List<GloryRecordItem> recordList = gloryHolder.getGloryTitle().getRunnerUpList();
        List<HeaderItem> headerList = gloryHolder.getPresenter().getHeaderList(recordList, groupMode);
        KeyExpandAdapter adapter = new KeyExpandAdapter(headerList, this, true, false, false);
        return adapter;
    }
}
