package com.king.app.tcareer.page.record.list;

import android.view.View;

/**
 * Created by Administrator on 2017/4/22 0022.
 */

public interface OnItemMenuListener {
    void onUpdateRecord(RecordItem record);
    void onDeleteRecord(RecordItem record);
    void onAllDetail(RecordItem record);
    void onListDetail(RecordItem record);

    void onItemClicked(View view, RecordItem curRecordItem);
}
