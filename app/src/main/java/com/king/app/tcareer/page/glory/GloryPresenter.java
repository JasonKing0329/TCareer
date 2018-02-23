package com.king.app.tcareer.page.glory;

import com.king.app.tcareer.base.BasePresenter;
import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.GloryDao;
import com.king.app.tcareer.model.bean.KeyValueCountBean;
import com.king.app.tcareer.model.bean.MatchResultBean;
import com.king.app.tcareer.model.db.entity.EarlierAchieve;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.model.db.entity.RecordDao;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.page.glory.bean.GloryTitle;
import com.king.app.tcareer.page.glory.gs.GloryGsItem;
import com.king.app.tcareer.page.glory.gs.GloryMasterItem;
import com.king.app.tcareer.page.glory.title.HeaderBean;
import com.king.app.tcareer.page.glory.title.HeaderItem;
import com.king.app.tcareer.page.glory.title.SubItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/6/5 0005.
 */

public class GloryPresenter extends BasePresenter<IGloryView> {
    
    private GloryDao gloryModel;

    @Override
    protected void onCreate() {
        gloryModel = new GloryDao();
    }

    public void loadData(long userId) {
        queryUser(userId)
                .flatMap(new Function<User, ObservableSource<GloryTitle>>() {
                    @Override
                    public ObservableSource<GloryTitle> apply(User user) throws Exception {
                        return new ObservableSource<GloryTitle>() {
                            @Override
                            public void subscribe(Observer<? super GloryTitle> observer) {
                                observer.onNext(loadGloryDatas());
                            }
                        };
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<GloryTitle>() {
                    @Override
                    public void accept(GloryTitle gloryTitle) throws Exception {
                        view.onGloryTitleLoaded(gloryTitle);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    private GloryTitle loadGloryDatas() {
        GloryTitle data = new GloryTitle();
        int earlierWin = 0;
        int earlierLose = 0;
        for (EarlierAchieve achieve:mUser.getEarlierAchieves()) {
            earlierWin += achieve.getWin();
            earlierLose += achieve.getLose();
        }
        data.setEarlierWin(earlierWin);
        data.setEarlierLose(earlierLose);
        data.setChampionList(gloryModel.getChampionRecords(mUser.getId()));
        data.setRunnerUpList(gloryModel.getRunnerupRecords(mUser.getId()));
        data.setTargetList(gloryModel.getTargetRecords(mUser.getId(), AppConstants.GLORY_TARGET_FACTOR, false, earlierWin, earlierLose));
        data.setTargetWinList(gloryModel.getTargetRecords(mUser.getId(), AppConstants.GLORY_TARGET_FACTOR, true, earlierWin, earlierLose));
        data.setCareerMatch(gloryModel.getCareerRecordNumber(mUser.getId(), false) + earlierLose + earlierWin);
        data.setCareerWin(gloryModel.getCareerRecordNumber(mUser.getId(), true) + earlierWin);
        data.setYearMatch(gloryModel.getYearRecordNumber(mUser.getId(), false));
        data.setYearWin(gloryModel.getYearRecordNumber(mUser.getId(), true));

        // count champion params
        data.setChampionTitle(data.new Title());
        List<KeyValueCountBean> titleCounts = gloryModel.getTitleCountByLevel(mUser.getId(), false);
        parseCountDataByLevel(data.getChampionTitle(), titleCounts, false);
        titleCounts = gloryModel.getTitleCountByLevel(mUser.getId(), true);
        parseCountDataByLevel(data.getChampionTitle(), titleCounts, true);
        titleCounts = gloryModel.getTitleCountByCourt(mUser.getId(), false);
        parseCountDataByCourt(data.getChampionTitle(), titleCounts, false);
        titleCounts = gloryModel.getTitleCountByCourt(mUser.getId(), true);
        parseCountDataByCourt(data.getChampionTitle(), titleCounts, true);

        // count runner-up params
        data.setRunnerupTitle(data.new Title());
        titleCounts = gloryModel.getRunnerUpCountByLevel(mUser.getId(), false);
        parseCountDataByLevel(data.getRunnerupTitle(), titleCounts, false);
        titleCounts = gloryModel.getRunnerUpCountByLevel(mUser.getId(), true);
        parseCountDataByLevel(data.getRunnerupTitle(), titleCounts, true);
        titleCounts = gloryModel.getRunnerUpCountByCourt(mUser.getId(), false);
        parseCountDataByCourt(data.getRunnerupTitle(), titleCounts, false);
        titleCounts = gloryModel.getRunnerUpCountByCourt(mUser.getId(), true);
        parseCountDataByCourt(data.getRunnerupTitle(), titleCounts, true);
        // count gs params
        data.setGs(data.new Gs());
        Map<String, Integer[]> map = gloryModel.getGsWinLose(mUser.getId());
        parseGsParamData(data, map);
        Integer[] result = gloryModel.getGsCount(mUser.getId(), false);
        data.getGs().setCareerWin(result[0]);
        data.getGs().setCareerLose(result[1]);
        result = gloryModel.getGsCount(mUser.getId(), true);
        data.getGs().setSeasonWin(result[0]);
        data.getGs().setSeasonLose(result[1]);
        // count atp1000 params
        data.setMaster1000(data.new Master1000());
        result = gloryModel.getATP1000Count(mUser.getId(), false);
        data.getMaster1000().setCareerWin(result[0]);
        data.getMaster1000().setCareerLose(result[1]);
        result = gloryModel.getATP1000Count(mUser.getId(), true);
        data.getMaster1000().setSeasonWin(result[0]);
        data.getMaster1000().setSeasonLose(result[1]);

        parseGsData(data);
        parseMasterData(data);
        return data;
    }

    private void parseGsParamData(GloryTitle data, Map<String, Integer[]> map) {
        Integer[] result = map.get(AppConstants.MATCH_GS[0]);
        if (result != null) {
            data.getGs().setAoWin(result[0]);
            data.getGs().setAoLose(result[1]);
        }
        result = map.get(AppConstants.MATCH_GS[1]);
        if (result != null) {
            data.getGs().setFoWin(result[0]);
            data.getGs().setFoLose(result[1]);
        }
        result = map.get(AppConstants.MATCH_GS[2]);
        if (result != null) {
            data.getGs().setWoWin(result[0]);
            data.getGs().setWoLose(result[1]);
        }
        result = map.get(AppConstants.MATCH_GS[3]);
        if (result != null) {
            data.getGs().setUoWin(result[0]);
            data.getGs().setUoLose(result[1]);
        }
        data.getGs().setCareerWin(data.getGs().getAoWin());
    }

    private void parseGsData(GloryTitle data) {
        List<MatchResultBean> gsList = gloryModel.getGsResultList(mUser.getId());
        Map<String, GloryGsItem> gsMap = new HashMap<>();
        List<GloryGsItem> gsGlories = new ArrayList<>();
        RecordDao recordDao = TApplication.getInstance().getDaoSession().getRecordDao();
        for (MatchResultBean bean:gsList) {
            String year = bean.getDate().split("-")[0];
            GloryGsItem item = gsMap.get(year);

            Record record = recordDao.queryBuilder()
                    .where(RecordDao.Properties.UserId.eq(mUser.getId()))
                    .where(RecordDao.Properties.MatchNameId.eq(bean.getMatchNameId()))
                    .where(RecordDao.Properties.DateStr.eq(bean.getDate()))
                    .build().list().get(0);

            if (item == null) {
                item = new GloryGsItem();
                gsMap.put(year, item);
                item.setYear(Integer.parseInt(year));
                gsGlories.add(item);
            }
            if (bean.getMatch().equals("澳大利亚网球公开赛")) {
                if ("Winner".equals(bean.getResult())) {
                    item.setAo("冠军");
                }
                else {
                    item.setAo(AppConstants.getGsGloryForRound(bean.getResult(), false));
               }
               item.setRecordAo(record);
            }
            else if (bean.getMatch().equals("法国网球公开赛")) {
                if ("Winner".equals(bean.getResult())) {
                    item.setFo("冠军");
                }
                else {
                    item.setFo(AppConstants.getGsGloryForRound(bean.getResult(), false));
                }
                item.setRecordFo(record);
            }
            else if (bean.getMatch().equals("温布尔顿网球公开赛")) {
                if ("Winner".equals(bean.getResult())) {
                    item.setWo("冠军");
                }
                else {
                    item.setWo(AppConstants.getGsGloryForRound(bean.getResult(), false));
                }
                item.setRecordWo(record);
            }
            else if (bean.getMatch().equals("美国网球公开赛")) {
                if ("Winner".equals(bean.getResult())) {
                    item.setUo("冠军");
                }
                else {
                    item.setUo(AppConstants.getGsGloryForRound(bean.getResult(), false));
                }
                item.setRecordUo(record);
            }
        }
        Collections.sort(gsGlories, new Comparator<GloryGsItem>() {
            @Override
            public int compare(GloryGsItem left, GloryGsItem right) {
                return left.getYear() - right.getYear();
            }
        });
        data.setGsItemList(gsGlories);
    }

    private void parseMasterData(GloryTitle data) {
        List<MatchResultBean> gsList = gloryModel.getAtp1000ResultList(mUser.getId());
        Map<String, GloryMasterItem> masterMap = new HashMap<>();
        List<GloryMasterItem> masterGlories = new ArrayList<>();
        RecordDao recordDao = TApplication.getInstance().getDaoSession().getRecordDao();
        for (MatchResultBean bean:gsList) {
            String year = bean.getDate().split("-")[0];
            GloryMasterItem item = masterMap.get(year);
            if (item == null) {
                item = new GloryMasterItem();
                item.setYear(Integer.parseInt(year));
                masterMap.put(year, item);
                masterGlories.add(item);
            }

            Record record = recordDao.queryBuilder()
                    .where(RecordDao.Properties.UserId.eq(mUser.getId()))
                    .where(RecordDao.Properties.MatchNameId.eq(bean.getMatchNameId()))
                    .where(RecordDao.Properties.DateStr.eq(bean.getDate()))
                    .build().list().get(0);

            setMasterResult(item, bean.getMatchId(), bean.getResult(), record);
        }
        Collections.sort(masterGlories, new Comparator<GloryMasterItem>() {
            @Override
            public int compare(GloryMasterItem left, GloryMasterItem right) {
                return left.getYear() - right.getYear();
            }
        });
        data.setMasterItemList(masterGlories);
    }

    private void setMasterResult(GloryMasterItem item, long matchId, String result, Record record) {
        if (matchId == AppConstants.ATP_1000_MATCH_ID[0]) {
            item.setIw(AppConstants.getMasterGloryForRound(result));
            item.setRecordIW(record);
        }
        else if (matchId == AppConstants.ATP_1000_MATCH_ID[1]) {
            item.setMiami(AppConstants.getMasterGloryForRound(result));
            item.setRecordMiami(record);
        }
        else if (matchId == AppConstants.ATP_1000_MATCH_ID[2]) {
            item.setMc(AppConstants.getMasterGloryForRound(result));
            item.setRecordMC(record);
        }
        else if (matchId == AppConstants.ATP_1000_MATCH_ID[3]) {
            item.setMadrid(AppConstants.getMasterGloryForRound(result));
            item.setRecordMadrid(record);
        }
        else if (matchId == AppConstants.ATP_1000_MATCH_ID[4]) {
            item.setRoma(AppConstants.getMasterGloryForRound(result));
            item.setRecordRoma(record);
        }
        else if (matchId == AppConstants.ATP_1000_MATCH_ID[5]) {
            item.setRc(AppConstants.getMasterGloryForRound(result));
            item.setRecordRC(record);
        }
        else if (matchId == AppConstants.ATP_1000_MATCH_ID[6]) {
            item.setCicinati(AppConstants.getMasterGloryForRound(result));
            item.setRecordCicinati(record);
        }
        else if (matchId == AppConstants.ATP_1000_MATCH_ID[7]) {
            item.setSh(AppConstants.getMasterGloryForRound(result));
            item.setRecordSH(record);
        }
        else if (matchId == AppConstants.ATP_1000_MATCH_ID[8]) {
            item.setParis(AppConstants.getMasterGloryForRound(result));
            item.setRecordParis(record);
        }
    }

    private void parseCountDataByLevel(GloryTitle.Title title, List<KeyValueCountBean> countList, boolean isCurrentYear) {
        int sum = 0;
        for (KeyValueCountBean countBean:countList) {
            sum += countBean.getValue();
            if (AppConstants.RECORD_MATCH_LEVELS[0].equals(countBean.getKey())) {
                if (isCurrentYear) {
                    title.setYearGs(countBean.getValue());
                }
                else {
                    title.setCareerGs(countBean.getValue());
                }
            }
            else if (AppConstants.RECORD_MATCH_LEVELS[1].equals(countBean.getKey())) {
                if (isCurrentYear) {
                    title.setYearMasterCup(countBean.getValue());
                }
                else {
                    title.setCareerMasterCup(countBean.getValue());
                }
            }
            else if (AppConstants.RECORD_MATCH_LEVELS[2].equals(countBean.getKey())) {
                if (isCurrentYear) {
                    title.setYearAtp1000(countBean.getValue());
                }
                else {
                    title.setCareerAtp1000(countBean.getValue());
                }
            }
            else if (AppConstants.RECORD_MATCH_LEVELS[3].equals(countBean.getKey())) {
                if (isCurrentYear) {
                    title.setYearAtp500(countBean.getValue());
                }
                else {
                    title.setCareerAtp500(countBean.getValue());
                }
            }
            else if (AppConstants.RECORD_MATCH_LEVELS[4].equals(countBean.getKey())) {
                if (isCurrentYear) {
                    title.setYearAtp250(countBean.getValue());
                }
                else {
                    title.setCareerAtp250(countBean.getValue());
                }
            }
            else if (AppConstants.RECORD_MATCH_LEVELS[6].equals(countBean.getKey())) {
                if (isCurrentYear) {
                    title.setYearOlympics(countBean.getValue());
                }
                else {
                    title.setCareerOlympics(countBean.getValue());
                }
            }
        }
        if (isCurrentYear) {
            title.setYearTotal(sum);
        }
        else {
            title.setCareerTotal(sum);
        }
    }

    private void parseCountDataByCourt(GloryTitle.Title title, List<KeyValueCountBean> countList, boolean isCurrentYear) {
        for (KeyValueCountBean countBean:countList) {
            if (AppConstants.RECORD_MATCH_COURTS[0].equals(countBean.getKey())) {
                if (isCurrentYear) {
                    title.setYearHard(countBean.getValue());
                }
                else {
                    title.setCareerHard(countBean.getValue());
                }
            }
            else if (AppConstants.RECORD_MATCH_COURTS[1].equals(countBean.getKey())) {
                if (isCurrentYear) {
                    title.setYearClay(countBean.getValue());
                }
                else {
                    title.setCareerClay(countBean.getValue());
                }
            }
            else if (AppConstants.RECORD_MATCH_COURTS[2].equals(countBean.getKey())) {
                if (isCurrentYear) {
                    title.setYearGrass(countBean.getValue());
                }
                else {
                    title.setCareerGrass(countBean.getValue());
                }
            }
            else if (AppConstants.RECORD_MATCH_COURTS[3].equals(countBean.getKey())) {
                if (isCurrentYear) {
                    title.setYearInhard(countBean.getValue());
                }
                else {
                    title.setCareerInhard(countBean.getValue());
                }
            }
        }
    }

    /**
     *
     * @param recordList
     * @param groupMode AppConstants.GROUP_BY_XX
     * @return
     */
    public List<HeaderItem> getHeaderList(List<Record> recordList, int groupMode) {
        List<HeaderItem> list = new ArrayList<>();
        Map<String, List<SubItem>> keyMap = new HashMap<>();
        for (Record record:recordList) {
            String key;
            if (groupMode == AppConstants.GROUP_BY_COURT) {
                key = record.getMatch().getMatchBean().getCourt();
            }
            else if (groupMode == AppConstants.GROUP_BY_LEVEL) {
                key = record.getMatch().getMatchBean().getLevel();
            }
            else {
                key = record.getDateStr().split("-")[0];
            }
            List<SubItem> subList = keyMap.get(key);
            if (subList == null) {
                subList = new ArrayList<>();
                keyMap.put(key, subList);

                HeaderItem header = new HeaderItem();
                header.setHeaderBean(new HeaderBean());
                header.getHeaderBean().setItemList(subList);
                header.getHeaderBean().setKey(key);
                list.add(header);
            }
            SubItem item = new SubItem();
            item.setItemPosition(subList.size());
            item.setRecord(record);
            subList.add(item);
        }

        if (groupMode == AppConstants.GROUP_BY_COURT) {
            Collections.sort(list, new CourtComparotor());
        }
        else if (groupMode == AppConstants.GROUP_BY_LEVEL) {
            Collections.sort(list, new LevelComparotor());
        }
        else if (groupMode == AppConstants.GROUP_BY_YEAR) {
            Collections.sort(list, new YearComparotor());
        }

        for (HeaderItem item:list) {
            item.getHeaderBean().setContent(String.valueOf(item.getHeaderBean().getItemList().size()));
            for (SubItem sub:item.getHeaderBean().getItemList()) {
                sub.setGroupCount(item.getHeaderBean().getItemList().size());
            }
        }
        return list;
    }

    private class LevelComparotor implements Comparator<HeaderItem> {

        @Override
        public int compare(HeaderItem o1, HeaderItem o2) {
            return getLevelValue(o1.getHeaderBean().getKey()) - getLevelValue(o2.getHeaderBean().getKey());
        }

        private int getLevelValue(String level) {
            for (int i = 0; i < AppConstants.RECORD_MATCH_LEVELS.length; i ++) {
                if (level.equals(AppConstants.RECORD_MATCH_LEVELS[i])) {
                    return i;
                }
            }
            return AppConstants.RECORD_MATCH_LEVELS.length;
        }
    }

    private class CourtComparotor implements Comparator<HeaderItem> {

        @Override
        public int compare(HeaderItem o1, HeaderItem o2) {
            return getCourtValue(o1.getHeaderBean().getKey()) - getCourtValue(o2.getHeaderBean().getKey());
        }

        private int getCourtValue(String court) {
            for (int i = 0; i < AppConstants.RECORD_MATCH_COURTS.length; i ++) {
                if (court.equals(AppConstants.RECORD_MATCH_COURTS[i])) {
                    return i;
                }
            }
            return AppConstants.RECORD_MATCH_COURTS.length;
        }
    }

    private class YearComparotor implements Comparator<HeaderItem> {

        @Override
        public int compare(HeaderItem o1, HeaderItem o2) {
            return Integer.parseInt(o2.getHeaderBean().getKey()) - Integer.parseInt(o1.getHeaderBean().getKey());
        }
    }

}
