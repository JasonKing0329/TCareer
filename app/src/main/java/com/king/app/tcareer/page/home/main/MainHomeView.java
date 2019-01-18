package com.king.app.tcareer.page.home.main;

import com.king.app.tcareer.base.BaseView;
import com.king.app.tcareer.model.db.entity.MatchNameBean;
import com.king.app.tcareer.model.db.entity.User;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/1/14 17:09
 */
public interface MainHomeView extends BaseView {
    void showUsers(List<User> users);

    void showMatches(List<MatchNameBean> list);

    void showRecords(List<ComplexRecord> records);
}
