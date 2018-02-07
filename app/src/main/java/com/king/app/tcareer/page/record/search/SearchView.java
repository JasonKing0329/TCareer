package com.king.app.tcareer.page.record.search;

import com.king.app.tcareer.base.BaseView;
import com.king.app.tcareer.model.db.entity.Record;

import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/2/7 14:34
 */
public interface SearchView extends BaseView {
    void searchResult(List<Record> records);
}
