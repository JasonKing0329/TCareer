package com.king.app.tcareer.page.record.complex;

import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.model.db.entity.RecordDao;
import com.king.app.tcareer.utils.ListUtil;
import com.zaihuishou.expandablerecycleradapter.model.ExpandableListItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/4/21 16:11
 */
public class HeaderItem implements ExpandableListItem {

    private Record record;
    private List<RecordComplexItem> list;
    public boolean mExpanded = false;

    private int yearPosition;

    public Record getRecord() {
        return record;
    }

    public void setRecord(Record record) {
        this.record = record;
    }

    @Override
    public List<RecordComplexItem> getChildItemList() {
        return list;
    }

    public void setChildItemList(List<RecordComplexItem> list) {
        this.list = list;
    }

    @Override
    public boolean isExpanded() {
        return mExpanded;
    }

    @Override
    public void setExpanded(boolean isExpanded) {
        mExpanded = isExpanded;

        // 如果要每次展开都重新加载，可以放开下面的代码
//        if (!mExpanded) {
//            list = null;
//        }
    }

    public int getYearPosition() {
        return yearPosition;
    }

    public void setYearPosition(int yearPosition) {
        this.yearPosition = yearPosition;
    }

    @Override
    public void loadChildItems() {
        RecordDao dao = TApplication.getInstance().getDaoSession().getRecordDao();
        List<Record> recordList = dao.queryBuilder()
                .where(RecordDao.Properties.MatchNameId.eq(record.getMatchNameId())
                    , RecordDao.Properties.DateStr.eq(record.getDateStr()))
                // 按user升序
                .orderAsc(RecordDao.Properties.UserId)
                .build().list();
        parseRecords(recordList);
    }

    /**
     * 将list转化为title(round)+list(record items)的形式
     * 按round级别降序排列，同round层级按userId升序排列
     * @param recordList
     */
    private void parseRecords(List<Record> recordList) {
        list = new ArrayList<>();
        Map<String, List<RecordComplexItem>> map = new HashMap<>();
        for (int i = 0; i < recordList.size(); i ++) {
            Record rec = recordList.get(i);
            RecordComplexItem item = new RecordComplexItem();
            item.setRecord(rec);
            if (map.get(rec.getRound()) == null) {
                map.put(rec.getRound(), new ArrayList<RecordComplexItem>());
            }
            map.get(rec.getRound()).add(item);
        }

        String[] array = AppConstants.RECORD_MATCH_ROUNDS;
        for (int i = 0; i < array.length; i ++) {
            List<RecordComplexItem> sub = map.get(array[i]);
            if (!ListUtil.isEmpty(sub)) {
                RecordComplexItem item = new RecordComplexItem();
                item.setTitle(true);
                item.setRecord(sub.get(0).getRecord());
                list.add(item);
                list.addAll(sub);
            }
        }
    }
}
