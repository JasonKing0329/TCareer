package com.king.app.tcareer.page.record.page;

import com.king.app.tcareer.base.BaseView;
import com.king.app.tcareer.model.db.entity.Record;

import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/22 13:38
 */
public interface RecordPageView extends BaseView {
    void postShowRecord(Record record);

    void postShowMatchRecords(List<Record> records);

    void showDetails(String scoreSet, String levelStr, String courtStr);
}
