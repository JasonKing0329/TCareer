package com.king.app.tcareer.page.player.page;

import com.king.app.tcareer.base.BaseView;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/4/27 13:16
 */
public interface SubPageView extends BaseView {
    void onDataLoaded(List<Object> list, int viewType);
}
