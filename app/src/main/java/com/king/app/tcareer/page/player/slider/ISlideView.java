package com.king.app.tcareer.page.player.slider;

import com.king.app.tcareer.base.BaseView;
import com.king.app.tcareer.model.bean.H2hBean;

import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/11/24 14:57
 */
public interface ISlideView extends BaseView {
    void onPlayerLoaded(List<H2hBean> playerList);

    void onPlayerLoadFailed(String message);

    void onRecordLoaded(List<Object> list);
}
