package com.king.app.tcareer.model.bean;

import com.king.app.tcareer.model.db.entity.PlayerAtpBean;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/2/2 10:31
 */
public interface CompetitorBean {

    Long getId();

    String getNameEng();

    String getNameChn();

    String getNamePinyin();

    String getCountry();

    String getCity();

    String getBirthday();

    String getAtpId();

    PlayerAtpBean getAtpBean();
}
