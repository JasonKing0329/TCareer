package com.king.app.tcareer.page.rank;

import com.king.app.tcareer.base.BaseView;
import com.king.app.tcareer.model.db.entity.Rank;

import java.util.List;

/**
 * @desc
 * @auth 景阳
 * @time 2018/2/4 0004 11:29
 */

public interface RankView extends BaseView {
    void showRanks(List<Rank> list);
}
