package com.king.app.tcareer.page.rank;

import com.king.app.tcareer.base.BaseView;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/9 13:39
 */
public interface RankCountView extends BaseView {
    void showBasic(int current, int highest, boolean isTop1);

    void showTop1(int weeks, String sequence);

    void showConditions(int weeks, String sequence);
}
