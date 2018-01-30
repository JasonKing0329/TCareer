package com.king.app.tcareer.page.match.manage;

import com.king.app.tcareer.base.BaseView;
import com.king.app.tcareer.model.db.entity.MatchNameBean;

import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/1/30 14:13
 */
public interface MatchManageView extends BaseView {
    void showMatches(List<MatchNameBean> list);

    void sortFinished(List<MatchNameBean> matchList);
}
