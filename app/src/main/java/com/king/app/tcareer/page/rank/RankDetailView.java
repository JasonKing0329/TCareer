package com.king.app.tcareer.page.rank;

import com.king.app.tcareer.base.BaseView;
import com.king.app.tcareer.model.db.entity.RankWeek;

import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/8 14:03
 */
public interface RankDetailView extends BaseView {
    void showRanks(List<RankWeek> list);

    void postShowUser(String nameEng);
}
