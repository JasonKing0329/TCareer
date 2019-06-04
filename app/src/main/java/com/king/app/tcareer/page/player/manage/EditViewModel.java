package com.king.app.tcareer.page.player.manage;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.model.db.entity.PlayerAtpBean;
import com.king.app.tcareer.model.db.entity.PlayerBean;
import com.king.app.tcareer.model.db.entity.PlayerBeanDao;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.model.db.entity.UserDao;
import com.king.app.tcareer.page.player.atp.PlayerAtpViewModel;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/1/30 17:33
 */
public class EditViewModel extends PlayerAtpViewModel {

    public MutableLiveData<PlayerAtpBean> onUpdateAtpCompleted = new MutableLiveData<>();

    public EditViewModel(@NonNull Application application) {
        super(application);
    }

    public void insertPlayer(PlayerBean bean) {
        PlayerBeanDao dao = TApplication.getInstance().getDaoSession().getPlayerBeanDao();
        dao.insert(bean);
    }

    public void updateUser(User user) {
        UserDao dao = TApplication.getInstance().getDaoSession().getUserDao();
        dao.update(user);
        dao.detach(user);
    }

    public void updatePlayer(PlayerBean bean) {
        PlayerBeanDao dao = TApplication.getInstance().getDaoSession().getPlayerBeanDao();
        dao.update(bean);
        dao.detach(bean);
    }

    @Override
    protected void onUpdateAtpCompleted(PlayerAtpBean bean) {
        super.onUpdateAtpCompleted(bean);
        onUpdateAtpCompleted.setValue(bean);
    }
}
