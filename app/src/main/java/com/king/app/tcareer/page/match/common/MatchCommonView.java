package com.king.app.tcareer.page.match.common;

import com.king.app.tcareer.base.BaseView;
import com.king.app.tcareer.model.db.entity.MatchNameBean;
import com.king.app.tcareer.model.db.entity.User;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/1/29 16:47
 */
public interface MatchCommonView extends BaseView {
    void postShowMatchInfor(MatchNameBean mMatchBean);

    void showUserInfor(User user, String h2h, String years);
}
