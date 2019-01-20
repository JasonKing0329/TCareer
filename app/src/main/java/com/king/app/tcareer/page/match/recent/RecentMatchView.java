package com.king.app.tcareer.page.match.recent;

import com.king.app.tcareer.base.BaseView;
import com.king.app.tcareer.model.db.entity.MatchNameBean;

import java.util.List;

public interface RecentMatchView extends BaseView {
    void postShowMatch(MatchNameBean bean);

    void showYears(List<Integer> yearList);

    void showRecords(List<Object> records);
}
