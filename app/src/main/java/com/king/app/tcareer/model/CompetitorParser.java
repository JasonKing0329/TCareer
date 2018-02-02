package com.king.app.tcareer.model;

import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.bean.CompetitorBean;
import com.king.app.tcareer.model.db.entity.Record;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/2/2 13:26
 */
public class CompetitorParser {

    public static CompetitorBean getCompetitorFrom(Record record) {
        if (record.getPlayerFlag() == AppConstants.COMPETITOR_VIRTUAL) {
            return record.getCompetitorUser();
        }
        else {
            return record.getCompetitor();
        }
    }
}
