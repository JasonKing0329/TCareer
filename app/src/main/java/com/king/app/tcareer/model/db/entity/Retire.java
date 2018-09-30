package com.king.app.tcareer.model.db.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.NotNull;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/9/29 17:37
 */
@Entity(nameInDb = "retire")
public class Retire {

    @Id(autoincrement = true)
    private Long id;

    private long userId;

    private int index;

    /**
     * relive retire(return to court)
     */
    private long relieveId;

    private Date declareDate;

    private Date effectDate;

    @ToOne(joinProperty = "relieveId")
    private Retire relieveRetire;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1854325894)
    private transient RetireDao myDao;

    @Generated(hash = 1505543790)
    private transient Long relieveRetire__resolvedKey;

    @Generated(hash = 1703264981)
    public Retire(Long id, long userId, int index, long relieveId, Date declareDate,
            Date effectDate) {
        this.id = id;
        this.userId = userId;
        this.index = index;
        this.relieveId = relieveId;
        this.declareDate = declareDate;
        this.effectDate = effectDate;
    }

    @Generated(hash = 114962049)
    public Retire() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getUserId() {
        return this.userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public long getRelieveId() {
        return this.relieveId;
    }

    public void setRelieveId(long relieveId) {
        this.relieveId = relieveId;
    }

    public Date getDeclareDate() {
        return this.declareDate;
    }

    public void setDeclareDate(Date declareDate) {
        this.declareDate = declareDate;
    }

    public Date getEffectDate() {
        return this.effectDate;
    }

    public void setEffectDate(Date effectDate) {
        this.effectDate = effectDate;
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1006126904)
    public Retire getRelieveRetire() {
        long __key = this.relieveId;
        if (relieveRetire__resolvedKey == null
                || !relieveRetire__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            RetireDao targetDao = daoSession.getRetireDao();
            Retire relieveRetireNew = targetDao.load(__key);
            synchronized (this) {
                relieveRetire = relieveRetireNew;
                relieveRetire__resolvedKey = __key;
            }
        }
        return relieveRetire;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1772265004)
    public void setRelieveRetire(@NotNull Retire relieveRetire) {
        if (relieveRetire == null) {
            throw new DaoException(
                    "To-one property 'relieveId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.relieveRetire = relieveRetire;
            relieveId = relieveRetire.getId();
            relieveRetire__resolvedKey = relieveId;
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
    @Generated(hash = 294895438)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getRetireDao() : null;
    }
}
