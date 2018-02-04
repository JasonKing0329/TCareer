package com.king.app.tcareer.page.match.gallery;

import com.king.app.tcareer.base.BaseView;

import java.util.List;

/**
 * @desc
 * @auth 景阳
 * @time 2018/2/4 0004 10:05
 */

public interface UserMatchView extends BaseView {
    void showMatches(List<UserMatchBean> list);
}
