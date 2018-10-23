package com.king.app.tcareer.utils;

import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.model.db.entity.Retire;
import com.king.app.tcareer.model.db.entity.RetireDao;

import java.util.Date;
import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/9/30 11:51
 */
public class RetireUtil {

    public static boolean isEffectiveRetiredNow(long userId) {
        return isRetired(userId, new Date(), true);
    }

    public static boolean isRetired(long userId, Date date, boolean isEffective) {
        return getRetired(userId, date, isEffective) == null ? false:true;
    }

    public static Retire getRetired(long userId, Date date, boolean isEffective) {
        Retire retireBean = null;
        List<Retire> retires = TApplication.getInstance().getDaoSession().getRetireDao().queryBuilder()
                .where(RetireDao.Properties.UserId.eq(userId))
                .where(RetireDao.Properties.RelieveId.eq(0))
                .build().list();

        List<Retire> relieves = TApplication.getInstance().getDaoSession().getRetireDao().queryBuilder()
                .where(RetireDao.Properties.UserId.eq(userId))
                .where(RetireDao.Properties.RelieveId.notEq(0))
                .build().list();
        Retire relieve = null;
        // 只考虑一次复出的情况
        if (relieves.size() > 0) {
            relieve = relieves.get(0);
        }
        for (Retire retire:retires) {
            long retireTime = getTime(retire, isEffective);
            long relieveTime = Long.MAX_VALUE;
            if (relieve != null && relieve.getRelieveId() == retire.getId()) {
                relieveTime = getTime(relieve, isEffective);
            }
            if (date.getTime() >= retireTime && date.getTime() < relieveTime) {
                retireBean = retire;
                break;
            }
        }

        return retireBean;
    }

    private static long getTime(Retire retire, boolean isEffective) {
        return isEffective ? retire.getEffectDate().getTime() : retire.getDeclareDate().getTime();
    }
}
