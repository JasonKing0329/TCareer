package com.king.app.tcareer.page.record.complex;

import com.king.app.tcareer.base.BaseView;
import com.king.app.tcareer.model.db.entity.Record;

import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/2 14:23
 */
public interface ComplexView extends BaseView {
    void postShowHeadRecord(Record record);

    void showItems(List<YearItem> yearItems);
}
