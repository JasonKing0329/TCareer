package com.king.app.tcareer.model.db.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.NotNull;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2020/4/14 17:17
 */
@Entity(nameInDb = "frozen_score")
public class FrozenScore {

    @Id(autoincrement = true)
    private Long id;

    private long userId;

    private long matchNameId;

    private int score;

    private String matchDate;

    @ToOne(joinProperty = "matchNameId")
    private MatchNameBean matchNameBean;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 228131487)
    private transient FrozenScoreDao myDao;

    @Generated(hash = 1806176128)
    private transient Long matchNameBean__resolvedKey;

    @Generated(hash = 1761957056)
    public FrozenScore(Long id, long userId, long matchNameId, int score,
            String matchDate) {
        this.id = id;
        this.userId = userId;
        this.matchNameId = matchNameId;
        this.score = score;
        this.matchDate = matchDate;
    }

    @Generated(hash = 2087300248)
    public FrozenScore() {
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

    public long getMatchNameId() {
        return this.matchNameId;
    }

    public void setMatchNameId(long matchNameId) {
        this.matchNameId = matchNameId;
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getMatchDate() {
        return this.matchDate;
    }

    public void setMatchDate(String matchDate) {
        this.matchDate = matchDate;
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 2111818697)
    public MatchNameBean getMatchNameBean() {
        long __key = this.matchNameId;
        if (matchNameBean__resolvedKey == null
                || !matchNameBean__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            MatchNameBeanDao targetDao = daoSession.getMatchNameBeanDao();
            MatchNameBean matchNameBeanNew = targetDao.load(__key);
            synchronized (this) {
                matchNameBean = matchNameBeanNew;
                matchNameBean__resolvedKey = __key;
            }
        }
        return matchNameBean;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 370853552)
    public void setMatchNameBean(@NotNull MatchNameBean matchNameBean) {
        if (matchNameBean == null) {
            throw new DaoException(
                    "To-one property 'matchNameId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.matchNameBean = matchNameBean;
            matchNameId = matchNameBean.getId();
            matchNameBean__resolvedKey = matchNameId;
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
    @Generated(hash = 1322641710)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getFrozenScoreDao() : null;
    }

}
