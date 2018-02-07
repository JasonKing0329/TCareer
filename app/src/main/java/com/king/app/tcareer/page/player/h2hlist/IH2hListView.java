package com.king.app.tcareer.page.player.h2hlist;

import com.king.app.tcareer.base.BaseView;
import com.king.app.tcareer.model.bean.H2hBean;
import com.king.app.tcareer.model.db.entity.User;

import java.util.List;

/**
 * Created by Administrator on 2017/4/30 0030.
 */

public interface IH2hListView extends BaseView {
    void onDataLoaded(H2hListPageData data);

    void onSortFinished(List<H2hBean> list);

    void onFilterFinished(List<H2hBean> list);

    void postShowUser(User user);
}
