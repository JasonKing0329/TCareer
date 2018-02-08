package com.king.app.tcareer.page.glory;

import com.king.app.tcareer.base.IFragmentHolder;
import com.king.app.tcareer.page.glory.bean.GloryTitle;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/6/1 18:38
 */
public interface IGloryHolder extends IFragmentHolder {
    GloryTitle getGloryTitle();
    GloryPresenter getPresenter();
}
