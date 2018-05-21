package com.king.app.tcareer.page.player.list;

import android.text.TextUtils;

import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.page.setting.SettingProperty;
import com.king.app.tcareer.utils.ConstellationUtil;
import com.king.app.tcareer.utils.FormatUtil;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.ObservableEmitter;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/5/21 10:15
 */
public class IndexEmitter {

    private Map<String, Integer> playerIndexMap;

    public IndexEmitter() {
        playerIndexMap = new HashMap<>();
    }

    public void clear() {
        playerIndexMap.clear();
    }

    public Map<String, Integer> getPlayerIndexMap() {
        return playerIndexMap;
    }

    public void createRecordsIndex(ObservableEmitter<String> e, List<RichPlayerBean> mList) {
        // player list查询出来已经是升序的
        for (int i = 0; i < mList.size(); i ++) {
            if (mList.get(i).getCompetitorBean() instanceof User) {
                continue;
            }
            int number = mList.get(i).getWin() + mList.get(i).getLose();
            //1 2 3 4 5 6 7 8 9 10 12 15 20 25 30 35 40 40+
            String key;
            if (number > 40) {
                key = "40+";
            }
            else if (number > 35 && number <= 40) {
                key = "40";
            }
            else if (number > 30 && number <= 35) {
                key = "35";
            }
            else if (number > 25 && number <= 30) {
                key = "30";
            }
            else if (number > 20 && number <= 25) {
                key = "25";
            }
            else if (number > 15 && number <= 20) {
                key = "20";
            }
            else if (number > 12 && number <= 15) {
                key = "15";
            }
            else if (number > 10 && number <= 12) {
                key = "12";
            }
            else {
                key = String.valueOf(number);
            }
            Integer index = playerIndexMap.get(key);
            if (index == null) {
                playerIndexMap.put(key, i);
                e.onNext(String.valueOf(key));
            }
        }
    }

    public void createSignIndex(ObservableEmitter<String> e, List<RichPlayerBean> mList) {
        // player list查询出来已经是升序的
        for (int i = 0; i < mList.size(); i ++) {
            if (mList.get(i).getCompetitorBean() instanceof User) {
                continue;
            }
            String birthday;
            try {
                birthday = mList.get(i).getCompetitorBean().getAtpBean().getBirthday();
            } catch (Exception exception) {
                birthday = mList.get(i).getCompetitorBean().getBirthday();
            }
            String sign;
            try {
                sign = ConstellationUtil.getConstellationEng(birthday);
            } catch (ConstellationUtil.ConstellationParseException e1) {
                sign = "Unknown";
            }
            Integer index = playerIndexMap.get(sign);
            if (index == null) {
                playerIndexMap.put(sign, i);
                e.onNext(sign);
            }
        }
    }

    public void createAgeIndex(ObservableEmitter<String> e, List<RichPlayerBean> mList) {
        // player list查询出来已经是升序的
        for (int i = 0; i < mList.size(); i ++) {
            if (mList.get(i).getCompetitorBean() instanceof User) {
                continue;
            }
            String birthday;
            try {
                birthday = mList.get(i).getCompetitorBean().getAtpBean().getBirthday();
            } catch (Exception exception) {
                birthday = mList.get(i).getCompetitorBean().getBirthday();
            }
            int age;
            try {
                age = ConstellationUtil.getAge(birthday);
            } catch (Exception e1) {
                age = Integer.MAX_VALUE;
            }
            String first = age > 35 ? ">35":String.valueOf(age);
            Integer index = playerIndexMap.get(first);
            if (index == null) {
                playerIndexMap.put(first, i);
                e.onNext(String.valueOf(first));
            }
        }
    }

    public void createCountryIndex(ObservableEmitter<String> e, List<RichPlayerBean> mList) {
        // player list查询出来已经是升序的
        for (int i = 0; i < mList.size(); i ++) {
            if (mList.get(i).getCompetitorBean() instanceof User) {
                continue;
            }
            String country;
            try {
                country = mList.get(i).getCompetitorBean().getAtpBean().getBirthCountry();
            } catch (Exception exception) {
                country = mList.get(i).getCompetitorBean().getCountry();
            }
            // 没有录入的排在最后
            if (TextUtils.isEmpty(country)) {
                country = "ZZZZZZZZ";
            }
            String first = String.valueOf(country.charAt(0));
            Integer index = playerIndexMap.get(first);
            if (index == null) {
                playerIndexMap.put(first, i);
                e.onNext(String.valueOf(first));
            }
        }
    }

    public void createNameIndex(ObservableEmitter<String> e, List<RichPlayerBean> mList, int sortType) {
        // player list查询出来已经是升序的
        for (int i = 0; i < mList.size(); i ++) {
            if (mList.get(i).getCompetitorBean() instanceof User) {
                continue;
            }
            String targetText;
            if (sortType == SettingProperty.VALUE_SORT_PLAYER_NAME) {
                targetText = mList.get(i).getCompetitorBean().getNamePinyin();
            }
            else {
                targetText = mList.get(i).getCompetitorBean().getNameEng();
                // 没有录入英文名的排在最后
                if (TextUtils.isEmpty(targetText)) {
                    targetText = "ZZZZZZZZ";
                }
            }
            String first = String.valueOf(targetText.charAt(0));
            Integer index = playerIndexMap.get(first);
            if (index == null) {
                playerIndexMap.put(first, i);
                e.onNext(String.valueOf(first));
            }
        }
    }

    public void createHeightIndex(ObservableEmitter<String> e, List<RichPlayerBean> mList) {
        // player list查询出来已经是升序的
        for (int i = 0; i < mList.size(); i ++) {
            if (mList.get(i).getCompetitorBean() instanceof User) {
                continue;
            }
            double height;
            try {
                height = mList.get(i).getCompetitorBean().getAtpBean().getCm();
            } catch (Exception e1) {
                height = 0;
            }
            //200+ 199 195 193 190 189 188 187 186 185 184 183 182 181 180 178 175 170 170-
            String key;
            if (height >= 200) {
                key = "200+";
            }
            else if (height > 195 && height <= 199) {
                key = "199";
            }
            else if (height > 193 && height <= 195) {
                key = "195";
            }
            else if (height > 190 && height <= 193) {
                key = "193";
            }
            else if (height < 170) {
                key = "170-";
            }
            else {
                key = FormatUtil.formatNumber(height);
            }
            Integer index = playerIndexMap.get(key);
            if (index == null) {
                playerIndexMap.put(key, i);
                e.onNext(String.valueOf(key));
            }
        }
    }

    public void createWeightIndex(ObservableEmitter<String> e, List<RichPlayerBean> mList) {
        // player list查询出来已经是升序的
        for (int i = 0; i < mList.size(); i ++) {
            if (mList.get(i).getCompetitorBean() instanceof User) {
                continue;
            }
            double weight;
            try {
                weight = mList.get(i).getCompetitorBean().getAtpBean().getKg();
            } catch (Exception e1) {
                weight = 0;
            }
            //100+ 99 95 90 85 80 75 70-
            String key;
            if (weight >= 100) {
                key = "100+";
            }
            else if (weight > 95 && weight <= 99) {
                key = "99";
            }
            else if (weight > 90 && weight <= 95) {
                key = "95";
            }
            else if (weight > 85 && weight <= 90) {
                key = "90";
            }
            else if (weight > 80 && weight <= 85) {
                key = "85";
            }
            else if (weight > 75 && weight <= 80) {
                key = "80";
            }
            else if (weight > 70 && weight <= 75) {
                key = "75";
            }
            else {
                key = "70-";
            }
            Integer index = playerIndexMap.get(key);
            if (index == null) {
                playerIndexMap.put(key, i);
                e.onNext(String.valueOf(key));
            }
        }
    }

    public void createCareerHighIndex(ObservableEmitter<String> e, List<RichPlayerBean> mList) {
        // player list查询出来已经是升序的
        for (int i = 0; i < mList.size(); i ++) {
            if (mList.get(i).getCompetitorBean() instanceof User) {
                continue;
            }
            int high;
            try {
                high = mList.get(i).getCompetitorBean().getAtpBean().getCareerHighSingle();
            } catch (Exception e1) {
                high = 0;
            }
            String key;
            if (high >= 500) {
                key = "500+";
            }
            else if (high >= 300) {
                key = "300+";
            }
            else if (high >= 200) {
                key = "200+";
            }
            else if (high >= 100) {
                key = "100+";
            }
            else if (high >= 80) {
                key = "80+";
            }
            else if (high >= 70) {
                key = "70+";
            }
            else if (high >= 60) {
                key = "60+";
            }
            else if (high >= 50) {
                key = "50+";
            }
            else if (high >= 40) {
                key = "40+";
            }
            else if (high >= 30) {
                key = "30+";
            }
            else if (high >= 20) {
                key = "20+";
            }
            else if (high >= 15) {
                key = "15+";
            }
            else if (high >= 11) {
                key = "11+";
            }
            else if (high == 0) {
                key = "Unknown";
            }
            else {
                key = String.valueOf(high);
            }
            Integer index = playerIndexMap.get(key);
            if (index == null) {
                playerIndexMap.put(key, i);
                e.onNext(String.valueOf(key));
            }
        }
    }

    public void createCareerTitlesIndex(ObservableEmitter<String> e, List<RichPlayerBean> mList) {
        // player list查询出来已经是升序的
        for (int i = 0; i < mList.size(); i ++) {
            if (mList.get(i).getCompetitorBean() instanceof User) {
                continue;
            }
            int titles;
            try {
                titles = mList.get(i).getCompetitorBean().getAtpBean().getCareerSingles();
            } catch (Exception e1) {
                titles = 0;
            }
            String key;
            if (titles >= 80) {
                key = "80+";
            }
            else if (titles >= 70) {
                key = "70+";
            }
            else if (titles >= 60) {
                key = "60+";
            }
            else if (titles >= 50) {
                key = "50+";
            }
            else if (titles >= 40) {
                key = "40+";
            }
            else if (titles >= 35) {
                key = "35+";
            }
            else if (titles >= 30) {
                key = "30+";
            }
            else if (titles >= 25) {
                key = "25+";
            }
            else if (titles >= 20) {
                key = "20+";
            }
            else if (titles >= 15) {
                key = "15+";
            }
            else if (titles >= 10) {
                key = "10+";
            }
            else if (titles >= 5) {
                key = "5+";
            }
            else {
                key = String.valueOf(titles);
            }
            Integer index = playerIndexMap.get(key);
            if (index == null) {
                playerIndexMap.put(key, i);
                e.onNext(String.valueOf(key));
            }
        }
    }

    public void createCareerWinIndex(ObservableEmitter<String> e, List<RichPlayerBean> mList) {
        // player list查询出来已经是升序的
        for (int i = 0; i < mList.size(); i ++) {
            if (mList.get(i).getCompetitorBean() instanceof User) {
                continue;
            }
            int high;
            try {
                high = mList.get(i).getCompetitorBean().getAtpBean().getCareerWin();
            } catch (Exception e1) {
                high = 0;
            }
            String key;
            if (high >= 800) {
                key = "800+";
            }
            else if (high >= 700) {
                key = "700+";
            }
            else if (high >= 600) {
                key = "600+";
            }
            else if (high >= 500) {
                key = "500+";
            }
            else if (high >= 400) {
                key = "400+";
            }
            else if (high >= 300) {
                key = "300+";
            }
            else if (high >= 200) {
                key = "200+";
            }
            else if (high >= 100) {
                key = "100+";
            }
            else if (high >= 50) {
                key = "50+";
            }
            else {
                key = "50-";
            }
            Integer index = playerIndexMap.get(key);
            if (index == null) {
                playerIndexMap.put(key, i);
                e.onNext(String.valueOf(key));
            }
        }
    }

    public void createTurnedProIndex(ObservableEmitter<String> e, List<RichPlayerBean> mList) {
        // player list查询出来已经是升序的
        for (int i = 0; i < mList.size(); i ++) {
            if (mList.get(i).getCompetitorBean() instanceof User) {
                continue;
            }
            int time;
            try {
                time = mList.get(i).getCompetitorBean().getAtpBean().getTurnedPro();
            } catch (Exception e1) {
                time = 0;
            }

            String key;
            // 近18年的逐年显示，超过15年的统一
            int thisyear = Calendar.getInstance().get(Calendar.YEAR);
            if (time < thisyear - 17) {
                key = (thisyear - 17) + "-";
            }
            else {
                key = String.valueOf(time);
            }
            Integer index = playerIndexMap.get(key);
            if (index == null) {
                playerIndexMap.put(key, i);
                e.onNext(String.valueOf(key));
            }
        }
    }

}
