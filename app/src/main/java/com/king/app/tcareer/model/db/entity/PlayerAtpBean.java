package com.king.app.tcareer.model.db.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Desc: data from AtpWorldTour
 *
 * @author：Jing Yang
 * @date: 2018/5/15 16:37
 */
@Entity(nameInDb = "player_atp")
public class PlayerAtpBean {

    @Id
    private String id;

    private String name;

    private String overViewUrl;

    private int age;

    private String birthday;

    private int turnedPro;

    private double lbs;

    private double kg;

    private String ft;

    private double cm;

    private String birthCity;

    private String birthCountry;

    private String residenceCity;

    private String residenceCountry;

    private String coach;

    private String plays;

    private int userFlag;

    private int careerHighSingle;

    private int careerHighDouble;

    private String careerHighSingleDate;

    private String careerHighDoubleDate;

    private int yearWin;

    private int yearLose;

    private int yearSingles;
    
    private int yearDoubles;

    private int careerWin;

    private int careerLose;

    private int careerSingles;

    private int careerDoubles;

    private String yearPrize;

    private String careerPrize;

    private long lastUpdateDate;

    @Generated(hash = 1918522178)
    public PlayerAtpBean(String id, String name, String overViewUrl, int age,
            String birthday, int turnedPro, double lbs, double kg, String ft,
            double cm, String birthCity, String birthCountry, String residenceCity,
            String residenceCountry, String coach, String plays, int userFlag,
            int careerHighSingle, int careerHighDouble, String careerHighSingleDate,
            String careerHighDoubleDate, int yearWin, int yearLose, int yearSingles,
            int yearDoubles, int careerWin, int careerLose, int careerSingles,
            int careerDoubles, String yearPrize, String careerPrize,
            long lastUpdateDate) {
        this.id = id;
        this.name = name;
        this.overViewUrl = overViewUrl;
        this.age = age;
        this.birthday = birthday;
        this.turnedPro = turnedPro;
        this.lbs = lbs;
        this.kg = kg;
        this.ft = ft;
        this.cm = cm;
        this.birthCity = birthCity;
        this.birthCountry = birthCountry;
        this.residenceCity = residenceCity;
        this.residenceCountry = residenceCountry;
        this.coach = coach;
        this.plays = plays;
        this.userFlag = userFlag;
        this.careerHighSingle = careerHighSingle;
        this.careerHighDouble = careerHighDouble;
        this.careerHighSingleDate = careerHighSingleDate;
        this.careerHighDoubleDate = careerHighDoubleDate;
        this.yearWin = yearWin;
        this.yearLose = yearLose;
        this.yearSingles = yearSingles;
        this.yearDoubles = yearDoubles;
        this.careerWin = careerWin;
        this.careerLose = careerLose;
        this.careerSingles = careerSingles;
        this.careerDoubles = careerDoubles;
        this.yearPrize = yearPrize;
        this.careerPrize = careerPrize;
        this.lastUpdateDate = lastUpdateDate;
    }

    @Generated(hash = 970576887)
    public PlayerAtpBean() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOverViewUrl() {
        return this.overViewUrl;
    }

    public void setOverViewUrl(String overViewUrl) {
        this.overViewUrl = overViewUrl;
    }

    public int getAge() {
        return this.age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getBirthday() {
        return this.birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public int getTurnedPro() {
        return this.turnedPro;
    }

    public void setTurnedPro(int turnedPro) {
        this.turnedPro = turnedPro;
    }

    public double getLbs() {
        return this.lbs;
    }

    public void setLbs(double lbs) {
        this.lbs = lbs;
    }

    public double getKg() {
        return this.kg;
    }

    public void setKg(double kg) {
        this.kg = kg;
    }

    public String getFt() {
        return this.ft;
    }

    public void setFt(String ft) {
        this.ft = ft;
    }

    public double getCm() {
        return this.cm;
    }

    public void setCm(double cm) {
        this.cm = cm;
    }

    public String getBirthCity() {
        return this.birthCity;
    }

    public void setBirthCity(String birthCity) {
        this.birthCity = birthCity;
    }

    public String getBirthCountry() {
        return this.birthCountry;
    }

    public void setBirthCountry(String birthCountry) {
        this.birthCountry = birthCountry;
    }

    public String getResidenceCity() {
        return this.residenceCity;
    }

    public void setResidenceCity(String residenceCity) {
        this.residenceCity = residenceCity;
    }

    public String getResidenceCountry() {
        return this.residenceCountry;
    }

    public void setResidenceCountry(String residenceCountry) {
        this.residenceCountry = residenceCountry;
    }

    public String getCoach() {
        return this.coach;
    }

    public void setCoach(String coach) {
        this.coach = coach;
    }

    public String getPlays() {
        return this.plays;
    }

    public void setPlays(String plays) {
        this.plays = plays;
    }

    public int getUserFlag() {
        return this.userFlag;
    }

    public void setUserFlag(int userFlag) {
        this.userFlag = userFlag;
    }

    public int getCareerHighSingle() {
        return this.careerHighSingle;
    }

    public void setCareerHighSingle(int careerHighSingle) {
        this.careerHighSingle = careerHighSingle;
    }

    public int getCareerHighDouble() {
        return this.careerHighDouble;
    }

    public void setCareerHighDouble(int careerHighDouble) {
        this.careerHighDouble = careerHighDouble;
    }

    public String getCareerHighSingleDate() {
        return this.careerHighSingleDate;
    }

    public void setCareerHighSingleDate(String careerHighSingleDate) {
        this.careerHighSingleDate = careerHighSingleDate;
    }

    public String getCareerHighDoubleDate() {
        return this.careerHighDoubleDate;
    }

    public void setCareerHighDoubleDate(String careerHighDoubleDate) {
        this.careerHighDoubleDate = careerHighDoubleDate;
    }

    public int getYearWin() {
        return this.yearWin;
    }

    public void setYearWin(int yearWin) {
        this.yearWin = yearWin;
    }

    public int getYearLose() {
        return this.yearLose;
    }

    public void setYearLose(int yearLose) {
        this.yearLose = yearLose;
    }

    public int getYearSingles() {
        return this.yearSingles;
    }

    public void setYearSingles(int yearSingles) {
        this.yearSingles = yearSingles;
    }

    public int getYearDoubles() {
        return this.yearDoubles;
    }

    public void setYearDoubles(int yearDoubles) {
        this.yearDoubles = yearDoubles;
    }

    public int getCareerWin() {
        return this.careerWin;
    }

    public void setCareerWin(int careerWin) {
        this.careerWin = careerWin;
    }

    public int getCareerLose() {
        return this.careerLose;
    }

    public void setCareerLose(int careerLose) {
        this.careerLose = careerLose;
    }

    public int getCareerSingles() {
        return this.careerSingles;
    }

    public void setCareerSingles(int careerSingles) {
        this.careerSingles = careerSingles;
    }

    public int getCareerDoubles() {
        return this.careerDoubles;
    }

    public void setCareerDoubles(int careerDoubles) {
        this.careerDoubles = careerDoubles;
    }

    public String getYearPrize() {
        return this.yearPrize;
    }

    public void setYearPrize(String yearPrize) {
        this.yearPrize = yearPrize;
    }

    public String getCareerPrize() {
        return this.careerPrize;
    }

    public void setCareerPrize(String careerPrize) {
        this.careerPrize = careerPrize;
    }

    public long getLastUpdateDate() {
        return this.lastUpdateDate;
    }

    public void setLastUpdateDate(long lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

}
