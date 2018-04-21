package com.king.app.tcareer.page.player.page;

import com.king.app.tcareer.base.IFragmentHolder;
import com.king.app.tcareer.model.db.entity.User;

/**
 * 描述: bind to PlayerPageActivity
 * <p/>作者：景阳
 * <p/>创建时间: 2017/11/20 17:27
 */
public interface IPageHolder extends IFragmentHolder {
    PagePresenter getPresenter();

    User getUser(String tabId);
}
