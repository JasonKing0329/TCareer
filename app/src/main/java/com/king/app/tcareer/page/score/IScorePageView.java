package com.king.app.tcareer.page.score;

import com.king.app.tcareer.base.BaseView;
import com.king.app.tcareer.model.db.entity.User;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/2/21 13:59
 */
public interface IScorePageView extends BaseView {

    void onPageDataLoaded(ScorePageData data);

    void showUser(User mUser);
}
