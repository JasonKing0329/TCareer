package com.king.app.tcareer.page.compare;

import android.app.Application;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.base.mvvm.BaseViewModel;
import com.king.app.tcareer.model.ImageProvider;
import com.king.app.tcareer.model.db.entity.User;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/9/12 14:45
 */
public class PlayerCompViewModel extends BaseViewModel {

    public ObservableField<String> playerUrl = new ObservableField<>();

    public ObservableField<String> playerName = new ObservableField<>();

    public PlayerCompViewModel(@NonNull Application application) {
        super(application);
    }

    public void loadPlayer(long playerId) {
        User user = getDaoSession().getUserDao().load(playerId);
        playerUrl.set(ImageProvider.getPlayerHeadPath(user.getNameChn()));
        playerName.set(user.getNameEng());
    }

    public void loadContents(String level1, String level2) {
        String[] arrLevel1 = TApplication.getInstance().getResources().getStringArray(R.array.compare_level1);
        if (level1.equals(arrLevel1[0])) {

        }
    }
}
