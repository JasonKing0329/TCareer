package com.king.app.tcareer.page.player.page;

import com.king.app.tcareer.model.bean.CompetitorBean;
import com.king.app.tcareer.model.db.entity.User;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/4/27 14:06
 */
public interface SubPageModel {

    List<TabBean> createTabs(User user, CompetitorBean competitor);
}
