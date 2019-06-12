package com.king.app.tcareer.page.glory;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.support.annotation.NonNull;
import android.view.View;

import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.base.mvvm.BaseViewModel;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.bean.KeyValueCountBean;
import com.king.app.tcareer.model.bean.MatchResultBean;
import com.king.app.tcareer.model.dao.GloryDao;
import com.king.app.tcareer.model.db.entity.EarlierAchieve;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.model.db.entity.RecordDao;
import com.king.app.tcareer.page.glory.bean.GloryRecordItem;
import com.king.app.tcareer.page.glory.bean.GloryTitle;
import com.king.app.tcareer.page.glory.gs.GloryGsItem;
import com.king.app.tcareer.page.glory.gs.GloryMasterItem;
import com.king.app.tcareer.page.glory.title.HeaderBean;
import com.king.app.tcareer.page.glory.title.HeaderItem;
import com.king.app.tcareer.page.glory.title.SubItem;
import com.king.app.tcareer.page.setting.SettingProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/6/5 0005.
 */

public class GloryViewModel extends BaseViewModel {

    public ObservableField<String> careerTotalText = new ObservableField<>();
    public ObservableField<String> seasonTotalText = new ObservableField<>();
    public ObservableInt titleVisibility = new ObservableInt(View.GONE);

    public ObservableField<String> careerGsText = new ObservableField<>();
    public ObservableField<String> seasonGsText = new ObservableField<>();
    public ObservableField<String> careerAoText = new ObservableField<>();
    public ObservableField<String> careerWoText = new ObservableField<>();
    public ObservableField<String> careerFoText = new ObservableField<>();
    public ObservableField<String> careerUoText = new ObservableField<>();
    public ObservableInt gsVisibility = new ObservableInt(View.GONE);

    public ObservableField<String> careerMasterText = new ObservableField<>();
    public ObservableField<String> seasonMasterText = new ObservableField<>();
    public ObservableInt masterVisibility = new ObservableInt(View.GONE);

    public MutableLiveData<GloryTitle> gloryTitleObserver = new MutableLiveData<>();
    private GloryDao gloryModel;

    public GloryViewModel(@NonNull Application application) {
        super(application);
        gloryModel = new GloryDao();
    }

    public void bindPageContent(int position) {
        if (position >= PageEnum.values().length) {
            position = 0;
        }
        PageEnum pageEnum = PageEnum.values()[position];
        switch (pageEnum) {
            case CHAMPION:
                bindChampionContent();
                break;
            case RUNNERUP:
                bindRunnerUpContent();
                break;
            case GS:
                bindGsContent();
                break;
            case ATP1000:
                bindMasterContent();
                break;
            case TARGET:
                bindTargetContent();
                break;
        }
    }

    private void bindChampionContent() {
        gsVisibility.set(View.GONE);
        masterVisibility.set(View.GONE);
        titleVisibility.set(View.VISIBLE);
        careerTotalText.set(String.valueOf(gloryTitleObserver.getValue().getChampionTitle().getCareerTotal()));
        seasonTotalText.set(String.valueOf(gloryTitleObserver.getValue().getChampionTitle().getYearTotal()));
    }

    private void bindRunnerUpContent() {
        gsVisibility.set(View.GONE);
        masterVisibility.set(View.GONE);
        titleVisibility.set(View.VISIBLE);
        careerTotalText.set(String.valueOf(gloryTitleObserver.getValue().getRunnerupTitle().getCareerTotal()));
        seasonTotalText.set(String.valueOf(gloryTitleObserver.getValue().getRunnerupTitle().getYearTotal()));
    }

    private void bindGsContent() {
        GloryTitle gloryTitle = gloryTitleObserver.getValue();
        gsVisibility.set(View.VISIBLE);
        masterVisibility.set(View.GONE);
        titleVisibility.set(View.GONE);
        careerGsText.set("Win " + gloryTitle.getGs().getCareerWin() + "  Lose " + gloryTitle.getGs().getCareerLose());
        seasonGsText.set("Win " + gloryTitle.getGs().getSeasonWin() + "  Lose " + gloryTitle.getGs().getSeasonLose());
        careerAoText.set("澳网 Win " + gloryTitle.getGs().getAoWin() + "  Lose " + gloryTitle.getGs().getAoLose());
        careerFoText.set("法网 Win " + gloryTitle.getGs().getFoWin() + "  Lose " + gloryTitle.getGs().getFoLose());
        careerWoText.set("温网 Win " + gloryTitle.getGs().getWoWin() + "  Lose " + gloryTitle.getGs().getWoLose());
        careerUoText.set("美网 Win " + gloryTitle.getGs().getUoWin() + "  Lose " + gloryTitle.getGs().getUoLose());
    }

    private void bindMasterContent() {
        GloryTitle gloryTitle = gloryTitleObserver.getValue();
        masterVisibility.set(View.VISIBLE);
        titleVisibility.set(View.GONE);
        gsVisibility.set(View.GONE);
        careerMasterText.set("Win " + gloryTitle.getMaster1000().getCareerWin() + "  Lose " + gloryTitle.getMaster1000().getCareerLose());
        seasonMasterText.set("Win " + gloryTitle.getMaster1000().getSeasonWin() + "  Lose " + gloryTitle.getMaster1000().getSeasonLose());
    }

    private void bindTargetContent() {
        GloryTitle gloryTitle = gloryTitleObserver.getValue();
        masterVisibility.set(View.VISIBLE);
        careerMasterText.set("Win " + gloryTitle.getCareerWin() + "  Lose " + (gloryTitle.getCareerMatch() - gloryTitle.getCareerWin()));
        seasonMasterText.set("Win " + gloryTitle.getYearWin() + "  Lose " + (gloryTitle.getYearMatch() - gloryTitle.getYearWin()));
    }

    public void loadData(long userId) {
        loadingObserver.setValue(true);
        queryUser(userId)
                .flatMap(user -> getGloryData())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GloryTitle>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(GloryTitle gloryTitle) {
                        loadingObserver.setValue(false);
                        gloryTitleObserver.setValue(gloryTitle);
                        bindPageContent(SettingProperty.getGloryPageIndex());
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        loadingObserver.setValue(false);
                        messageObserver.setValue(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private ObservableSource<GloryTitle> getGloryData() {
        return observer -> observer.onNext(loadGloryData());
    }

    private GloryTitle loadGloryData() {
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
        Map<Integer, GloryGsItem> gsMap = new HashMap<>();
        List<GloryGsItem> gsGlories = new ArrayList<>();
        RecordDao recordDao = TApplication.getInstance().getDaoSession().getRecordDao();
        for (MatchResultBean bean:gsList) {
            int year = Integer.parseInt(bean.getDate().split("-")[0]);
            GloryGsItem item = gsMap.get(year);

            Record record = recordDao.queryBuilder()
                    .where(RecordDao.Properties.UserId.eq(mUser.getId()))
                    .where(RecordDao.Properties.MatchNameId.eq(bean.getMatchNameId()))
                    .where(RecordDao.Properties.DateStr.eq(bean.getDate()))
                    .build().list().get(0);

            if (item == null) {
                item = new GloryGsItem();
                gsMap.put(year, item);
                item.setYear(year);
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
        Collections.sort(gsGlories, (left, right) -> left.getYear() - right.getYear());
        // 处理出现一整年未参加大满贯的情况
        checkSequenceGsYear(gsGlories);

        data.setGsItemList(gsGlories);
    }

    /**
     * 处理出现一整年未参加大满贯的情况
     * @param gsGlories
     */
    private void checkSequenceGsYear(List<GloryGsItem> gsGlories) {
        for (int i = 0; i < gsGlories.size(); i ++) {
            if (i > 0) {
                if (gsGlories.get(i).getYear() - gsGlories.get(i - 1).getYear() != 1) {
                    GloryGsItem item = new GloryGsItem();
                    item.setYear(gsGlories.get(i - 1).getYear() + 1);
                    gsGlories.add(i, item);
                    checkSequenceGsYear(gsGlories);
                    break;
                }
            }
        }
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
        Collections.sort(masterGlories, (left, right) -> left.getYear() - right.getYear());
        // 处理出现一整年未参加大师赛的情况
        checkSequenceMasterYear(masterGlories);

        data.setMasterItemList(masterGlories);
    }

    /**
     * 处理出现一整年未参加大师赛的情况
     * @param gloryMasterItems
     */
    private void checkSequenceMasterYear(List<GloryMasterItem> gloryMasterItems) {
        for (int i = 0; i < gloryMasterItems.size(); i ++) {
            if (i > 0) {
                if (gloryMasterItems.get(i).getYear() - gloryMasterItems.get(i - 1).getYear() != 1) {
                    GloryMasterItem item = new GloryMasterItem();
                    item.setYear(gloryMasterItems.get(i - 1).getYear() + 1);
                    gloryMasterItems.add(i, item);
                    checkSequenceMasterYear(gloryMasterItems);
                    break;
                }
            }
        }
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
    public List<HeaderItem> getHeaderList(List<GloryRecordItem> recordList, int groupMode) {
        List<HeaderItem> list = new ArrayList<>();
        Map<String, List<SubItem>> keyMap = new HashMap<>();
        for (GloryRecordItem recordItem:recordList) {
            String key;
            if (groupMode == AppConstants.GROUP_BY_COURT) {
                key = recordItem.getRecord().getMatch().getMatchBean().getCourt();
            }
            else if (groupMode == AppConstants.GROUP_BY_LEVEL) {
                key = recordItem.getRecord().getMatch().getMatchBean().getLevel();
            }
            else {
                key = recordItem.getRecord().getDateStr().split("-")[0];
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
            item.setRecord(gloryModel.convertTitleRecordItem(recordItem.getRecord(), 0));
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
            for (int i = 0; i < item.getHeaderBean().getItemList().size(); i ++) {
                SubItem sub = item.getHeaderBean().getItemList().get(i);
                sub.setGroupCount(item.getHeaderBean().getItemList().size());
                // 重置正确的序号。组内倒序
                sub.getRecord().setIndex(String.valueOf(sub.getGroupCount() - sub.getItemPosition()));
            }
        }
        return list;
    }

    public GloryTitle getGloryTitle() {
        return gloryTitleObserver.getValue();
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
