package com.king.app.tcareer.model.db.entity;

import com.king.app.tcareer.model.bean.CompetitorBean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.DaoException;

@Entity(nameInDb = "players")
public class PlayerBean implements CompetitorBean {

    @Id(autoincrement = true)
    private Long id;
    private String nameEng;
    private String nameChn;
    private String namePinyin;
    private String country;
    private String city;
    private String birthday;

    private String atpId;

    @ToOne(joinProperty = "atpId")
    private PlayerAtpBean atpBean;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 624992577)
    private transient PlayerBeanDao myDao;
    @Generated(hash = 2088910886)
    private transient String atpBean__resolvedKey;

    @Generated(hash = 1373979275)
    public PlayerBean(Long id, String nameEng, String nameChn, String namePinyin,
            String country, String city, String birthday, String atpId) {
        this.id = id;
        this.nameEng = nameEng;
        this.nameChn = nameChn;
        this.namePinyin = namePinyin;
        this.country = country;
        this.city = city;
        this.birthday = birthday;
        this.atpId = atpId;
    }

    @Generated(hash = 288301582)
    public PlayerBean() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNameEng() {
        return nameEng;
    }

    public void setNameEng(String nameEng) {
        this.nameEng = nameEng;
    }

    public String getNameChn() {
        return nameChn;
    }

    public void setNameChn(String nameChn) {
        this.nameChn = nameChn;
    }

    public String getNamePinyin() {
        return namePinyin;
    }

    public void setNamePinyin(String namePinyin) {
        this.namePinyin = namePinyin;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getAtpId() {
        return this.atpId;
    }

    public void setAtpId(String atpId) {
        this.atpId = atpId;
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1790333486)
    public PlayerAtpBean getAtpBean() {
        String __key = this.atpId;
        if (atpBean__resolvedKey == null || atpBean__resolvedKey != __key) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            PlayerAtpBeanDao targetDao = daoSession.getPlayerAtpBeanDao();
            PlayerAtpBean atpBeanNew = targetDao.load(__key);
            synchronized (this) {
                atpBean = atpBeanNew;
                atpBean__resolvedKey = __key;
            }
        }
        return atpBean;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 869304510)
    public void setAtpBean(PlayerAtpBean atpBean) {
        synchronized (this) {
            this.atpBean = atpBean;
            atpId = atpBean == null ? null : atpBean.getId();
            atpBean__resolvedKey = atpId;
        }
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
    @Generated(hash = 1939137187)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getPlayerBeanDao() : null;
    }

}
