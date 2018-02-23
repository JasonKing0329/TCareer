package com.king.app.tcareer.model.db.entity;

import com.king.app.tcareer.model.bean.CompetitorBean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.JoinProperty;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;
import org.greenrobot.greendao.DaoException;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/1/26 15:34
 */
@Entity(nameInDb = "users")
public class User implements CompetitorBean {

    @Id(autoincrement = true)
    private Long id;

    private String nameEng;
    private String nameChn;
    private String namePinyin;
    private String country;
    private String city;
    private String birthday;
    private String nameShort;
    private int height;
    private double weight;

    @ToMany(referencedJoinProperty = "userId")
    private List<EarlierAchieve> earlierAchieves;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1507654846)
    private transient UserDao myDao;

    @Generated(hash = 598643048)
    public User(Long id, String nameEng, String nameChn, String namePinyin,
            String country, String city, String birthday, String nameShort,
            int height, double weight) {
        this.id = id;
        this.nameEng = nameEng;
        this.nameChn = nameChn;
        this.namePinyin = namePinyin;
        this.country = country;
        this.city = city;
        this.birthday = birthday;
        this.nameShort = nameShort;
        this.height = height;
        this.weight = weight;
    }
    @Generated(hash = 586692638)
    public User() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getNameEng() {
        return this.nameEng;
    }
    public void setNameEng(String nameEng) {
        this.nameEng = nameEng;
    }
    public String getNameChn() {
        return this.nameChn;
    }
    public void setNameChn(String nameChn) {
        this.nameChn = nameChn;
    }
    public String getNamePinyin() {
        return this.namePinyin;
    }
    public void setNamePinyin(String namePinyin) {
        this.namePinyin = namePinyin;
    }
    public String getCountry() {
        return this.country;
    }
    public void setCountry(String country) {
        this.country = country;
    }
    public String getCity() {
        return this.city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public String getBirthday() {
        return this.birthday;
    }
    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
    public String getNameShort() {
        return this.nameShort;
    }
    public void setNameShort(String nameShort) {
        this.nameShort = nameShort;
    }
    public int getHeight() {
        return this.height;
    }
    public void setHeight(int height) {
        this.height = height;
    }
    public double getWeight() {
        return this.weight;
    }
    public void setWeight(double weight) {
        this.weight = weight;
    }
    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1856259866)
    public List<EarlierAchieve> getEarlierAchieves() {
        if (earlierAchieves == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            EarlierAchieveDao targetDao = daoSession.getEarlierAchieveDao();
            List<EarlierAchieve> earlierAchievesNew = targetDao
                    ._queryUser_EarlierAchieves(id);
            synchronized (this) {
                if (earlierAchieves == null) {
                    earlierAchieves = earlierAchievesNew;
                }
            }
        }
        return earlierAchieves;
    }
    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 612826865)
    public synchronized void resetEarlierAchieves() {
        earlierAchieves = null;
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 2059241980)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getUserDao() : null;
    }

}
