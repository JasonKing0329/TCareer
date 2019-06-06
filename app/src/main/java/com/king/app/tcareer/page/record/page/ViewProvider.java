package com.king.app.tcareer.page.record.page;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/6/6 11:11
 */
public interface ViewProvider {
    CollapsingToolbarLayout getCollapsingToolbar();
    Toolbar getToolbar();
    MenuItem getEditMenuItem();
}
