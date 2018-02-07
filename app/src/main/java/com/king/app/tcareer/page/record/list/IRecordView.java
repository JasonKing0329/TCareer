package com.king.app.tcareer.page.record.list;

import com.king.app.tcareer.base.BaseView;

/**
 * Created by Administrator on 2017/4/21 0021.
 */

public interface IRecordView extends BaseView {
    void onRecordDataLoaded(RecordPageData list);

    void postShowUser();

    void deleteSuccess(int viewPosition);
}
