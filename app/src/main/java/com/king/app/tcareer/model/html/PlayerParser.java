package com.king.app.tcareer.model.html;

import android.text.TextUtils;

import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.model.db.entity.PlayerAtpBean;
import com.king.app.tcareer.model.db.entity.PlayerAtpBeanDao;
import com.king.app.tcareer.utils.DebugLog;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import io.reactivex.Observable;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/5/15 10:34
 */
public class PlayerParser extends AbsParser {

    public Observable<Boolean> parse(final String atpId, final String url) {
        return Observable.create(e -> {

            PlayerAtpBeanDao dao = TApplication.getInstance().getDaoSession().getPlayerAtpBeanDao();
            PlayerAtpBean bean = dao.queryBuilder()
                    .where(PlayerAtpBeanDao.Properties.Id.eq(atpId))
                    .build().unique();

            Document document = Jsoup.connect(url)
                    .userAgent(getLocalUserAgent())
                    .get();
            // select里面的语法，如果要加“.”，其后面必须是class而不能是id
            Elements innerWrap = document.select("div.inner-wrap");
            Elements trs = innerWrap.select("tr");

            Element tr0 = trs.get(0);
            Elements tr0Tds = tr0.select("td");

            // 实测有的球员没有这一项
            String age="";
            try {
                age = tr0Tds.get(0).selectFirst("div.table-big-value").text();
                bean.setAge(Integer.parseInt(age));
            } catch (Exception exception) {}

            String birthday = tr0Tds.get(0).selectFirst("div.table-big-value-birthday").text();
            String turnedPro = tr0Tds.get(1).selectFirst("div.table-big-value").text();
            DebugLog.e("age:" + age + ", birthday:" + birthday + ", turnedPro:" + turnedPro);
            birthday = birthday.substring(1, birthday.length() - 1).replace(".", "-");
            bean.setBirthday(birthday);
            // 实测有的球员这一项为空
            if (!TextUtils.isEmpty(turnedPro)) {
                bean.setTurnedPro(Integer.parseInt(turnedPro));
            }

            Element tr1 = trs.get(1);
            Elements tr1Tds = tr1.select("td");
            String lbs = tr1Tds.get(0).selectFirst("div.table-big-value-lbs").text();
            String kg = tr1Tds.get(0).selectFirst("div.table-big-value-kg").text();
            DebugLog.e("lbs:" + lbs + ", kg:" + kg);
            bean.setLbs(Double.parseDouble(lbs));
            kg = kg.split(" ")[0].substring(1);
            bean.setKg(Double.parseDouble(kg));

            String ft = tr1Tds.get(1).selectFirst("div.table-big-value-ft").text();
            String cm = tr1Tds.get(1).selectFirst("div.table-big-value-cm").text();
            DebugLog.e("ft:" + ft + ", cm:" + cm);
            bean.setFt(ft);
            cm = cm.split(" ")[0].substring(1);
            bean.setCm(Double.parseDouble(cm));

            Element tr2 = trs.get(2);
            String birthPlace = tr2.selectFirst("div.table-value").text();
            DebugLog.e("birthPlace:" + birthPlace);
            if (birthPlace.contains(",")) {
                int index = birthPlace.lastIndexOf(",");
                bean.setBirthCity(birthPlace.substring(0, index).trim());
                bean.setBirthCountry(birthPlace.substring(index + 1).trim());
            }
            else {
                bean.setBirthCountry(birthPlace);
            }

            Element tr3 = trs.get(3);
            String residence = tr3.selectFirst("div.table-value").text();
            DebugLog.e("residence:" + residence);
            if (residence.contains(",")) {
                int index = residence.lastIndexOf(",");
                bean.setResidenceCity(residence.substring(0, index).trim());
                bean.setResidenceCountry(residence.substring(index + 1).trim());
            }
            else {
                bean.setResidenceCountry(residence);
            }

            Element tr4 = trs.get(4);
            String plays = tr4.selectFirst("div.table-value").text();
            DebugLog.e("plays:" + plays);
            bean.setPlays(plays);

            Element tr5 = trs.get(5);
            String coach = tr5.selectFirst("div.table-value").text();
            DebugLog.e("coach:" + coach);
            bean.setCoach(coach);

            // select里面的语法，如果要加“.”，其后面必须是class而不能是id
            Elements playersStatsTable = document.select("table.players-stats-table");
            trs = playersStatsTable.select("tr");

            Element statsTr2 = trs.get(2);
            Element rankTd = statsTr2.select("td").get(2);
            Elements rankDivs = rankTd.select("div");
            String rankSingle = rankDivs.get(0).attr("data-singles");
            String rankDouble = rankDivs.get(0).attr("data-doubles");
            String rankSingleDate = rankDivs.get(1).attr("data-singles-label");
            String rankDoubleDate = rankDivs.get(1).attr("data-doubles-label");
            DebugLog.e("rankSingle:" + rankSingle + ", rankDouble" + rankDouble);
            DebugLog.e("rankSingleDate:" + rankSingleDate + ", rankDoubleDate" + rankDoubleDate);
            bean.setCareerHighSingle(Integer.parseInt(rankSingle));
            bean.setCareerHighDouble(Integer.parseInt(rankDouble));
            rankSingleDate = rankSingleDate.replace("Career High ", "")
                    .replace(".", "-");
            rankDoubleDate = rankDoubleDate.replace("Career High ", "")
                    .replace(".", "-");
            bean.setCareerHighSingleDate(rankSingleDate);
            bean.setCareerHighDoubleDate(rankDoubleDate);

            Element statsTr3 = trs.get(3);
            Elements tdsInTr3 = statsTr3.select("td");
            String yearWinLose = tdsInTr3.get(0).select("div.stat-value").attr("data-singles");
            Element yearChampinsDiv = tdsInTr3.get(1).selectFirst("div.stat-value");
            String yearSingles = yearChampinsDiv.attr("data-singles");
            String yearDoubles = yearChampinsDiv.attr("data-doubles");
            DebugLog.e("yearWinLose:" + yearWinLose + ", yearSingles" + yearSingles + ", yearDoubles" + yearDoubles);
            String careerWinLose = tdsInTr3.get(2).select("div.stat-value").attr("data-singles");
            Element careerChampionsDiv = tdsInTr3.get(3).selectFirst("div.stat-value");
            String careerSingles = careerChampionsDiv.attr("data-singles");
            String careerDoubles = careerChampionsDiv.attr("data-doubles");
            DebugLog.e("careerWinLose:" + careerWinLose + ", careerSingles" + careerSingles + ", careerDoubles" + careerDoubles);
            String[] arrays = yearWinLose.split("-");
            bean.setYearWin(Integer.parseInt(arrays[0]));
            bean.setYearLose(Integer.parseInt(arrays[1]));
            bean.setYearSingles(Integer.parseInt(yearSingles));
            bean.setYearDoubles(Integer.parseInt(yearDoubles));
            arrays = careerWinLose.split("-");
            bean.setCareerWin(Integer.parseInt(arrays[0]));
            bean.setCareerLose(Integer.parseInt(arrays[1]));
            bean.setCareerSingles(Integer.parseInt(careerSingles));
            bean.setCareerDoubles(Integer.parseInt(careerDoubles));

            Element statsTr4 = trs.get(4);
            Elements tdsInTr4 = statsTr4.select("td");
            String yearPrize = tdsInTr4.get(0).select("div.stat-value").attr("data-singles");
            String careerPrize = tdsInTr4.get(1).select("div.stat-value").attr("data-singles");
            DebugLog.e("yearPrize:" + yearPrize + ", careerPrize" + careerPrize);
            bean.setYearPrize(yearPrize);
            bean.setCareerPrize(careerPrize);

            bean.setLastUpdateDate(System.currentTimeMillis());
            dao.update(bean);
            dao.detach(bean);

            e.onNext(true);
        });
    }

}
