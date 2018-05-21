package com.king.app.tcareer.page.player.list;

import com.king.app.tcareer.base.IFragmentHolder;
import com.king.app.tcareer.model.bean.CompetitorBean;

/**
 * @desc
 * @auth 景阳
 * @time 2018/5/19 0019 21:24
 */

public interface RichPlayerHolder extends IFragmentHolder {
    void onSelectPlayer(CompetitorBean bean);

    void onSortFinished();
}
