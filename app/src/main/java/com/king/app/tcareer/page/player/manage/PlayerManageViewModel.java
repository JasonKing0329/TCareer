package com.king.app.tcareer.page.player.manage;

import android.app.Application;
import android.support.annotation.NonNull;

import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.base.mvvm.BaseViewModel;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.model.db.entity.UserDao;

import java.util.List;

/**
 * @desc
 * @auth 景阳
 * @time 2018/1/31 0031 11:48
 */

public class PlayerManageViewModel extends BaseViewModel {

    private List<User> userList;

    public PlayerManageViewModel(@NonNull Application application) {
        super(application);
    }

    public String[] getUserSelector() {
        if (userList == null) {
            UserDao dao = TApplication.getInstance().getDaoSession().getUserDao();
            userList = dao.loadAll();
        }
        String[] users = new String[userList.size() + 1];
        users[0] = "All users";
        for (int i = 0; i < userList.size(); i ++) {
            users[i + 1] = userList.get(i).getNameEng();
        }
        return users;
    }

    public User getUser(int position) {
        if (position == 0) {
            return null;
        }
        else {
            return userList.get(position - 1);
        }
    }
}
