package com.king.app.tcareer.page.home;

import com.king.app.tcareer.base.BaseView;
import com.king.app.tcareer.model.bean.CompetitorBean;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.page.match.gallery.UserMatchBean;

import java.util.List;

/**
 * Created by Administrator on 2017/4/3 0003.
 */

public interface IHomeView extends BaseView {

    void postShowCurrentUser();

    void postShowAllUsers();

    void postShowLastRecord(Record record);

    void postShowCompetitors(List<CompetitorBean> list);

    void showMatches(List<UserMatchBean> list);
}
