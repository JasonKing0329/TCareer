package com.king.app.tcareer.page.compare;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.base.mvvm.BaseViewModel;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.CompareModel;
import com.king.app.tcareer.model.ImageProvider;

import java.util.List;

import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/9/12 14:45
 */
public class PlayerCompViewModel extends BaseViewModel {

    public ObservableField<String> playerUrl = new ObservableField<>();

    public ObservableField<String> playerName = new ObservableField<>();

    public MutableLiveData<List<TwoLineBean>> twoLineList = new MutableLiveData<>();

    public MutableLiveData<List<SubTotalBean>> subTotalList = new MutableLiveData<>();

    private CompareModel compareModel;

    public PlayerCompViewModel(@NonNull Application application) {
        super(application);
        compareModel = new CompareModel();
    }

    public void loadPlayer(long playerId) {
        queryUserInstant(playerId);
        playerUrl.set(ImageProvider.getPlayerHeadPath(mUser.getNameChn()));
        playerName.set(mUser.getNameEng());
    }

    public void loadContents(String level1, String level2) {
        String[] arrLevel1 = TApplication.getInstance().getResources().getStringArray(R.array.compare_level1);
        if (level1.equals(arrLevel1[0])) {
            String[] arrLevel2 = TApplication.getInstance().getResources().getStringArray(R.array.compare_gs_round);
            if (level2.equals(arrLevel2[0])) {
                loadGsWinLose();
            }
            else if (level2.equals(arrLevel2[1])) {
                loadGsChampion();
            }
            else if (level2.equals(arrLevel2[2])) {
                loadGsRound(AppConstants.RECORD_MATCH_ROUNDS[0]);
            }
            else if (level2.equals(arrLevel2[3])) {
                loadGsRound(AppConstants.RECORD_MATCH_ROUNDS[1]);
            }
            else if (level2.equals(arrLevel2[4])) {
                loadGsRound(AppConstants.RECORD_MATCH_ROUNDS[2]);
            }
            else if (level2.equals(arrLevel2[5])) {
                loadGsRound(AppConstants.RECORD_MATCH_ROUNDS[3]);
            }
        }
        else if (level1.equals(arrLevel1[1])) {
            String[] arrLevel2 = TApplication.getInstance().getResources().getStringArray(R.array.compare_master_round);
            if (level2.equals(arrLevel2[0])) {
                loadMasterWinLose();
            }
            else if (level2.equals(arrLevel2[1])) {

            }
            else if (level2.equals(arrLevel2[2])) {

            }
            else if (level2.equals(arrLevel2[3])) {

            }
            else if (level2.equals(arrLevel2[4])) {

            }
        }
    }

    private void loadMasterWinLose() {
    }

    private void loadGsChampion() {
        loadGsRound(AppConstants.CHAMPOION);
    }

    private void loadGsRound(String round) {
        compareModel.getGsRound(mUser.getId(), round)
                .compose(subTotalTransformer)
                .subscribe(subTotalObserver);
    }

    private void loadGsWinLose() {
        compareModel.getGsWinLose(mUser.getId())
                .compose(twoLineTransformer)
                .subscribe(twoLineObserver);
    }

    private Observer<List<TwoLineBean>> twoLineObserver = new Observer<List<TwoLineBean>>() {
        @Override
        public void onSubscribe(Disposable d) {
            addDisposable(d);
        }

        @Override
        public void onNext(List<TwoLineBean> twoLineBeans) {
            twoLineList.setValue(twoLineBeans);
        }

        @Override
        public void onError(Throwable e) {
            e.printStackTrace();
            messageObserver.setValue(e.getMessage());
        }

        @Override
        public void onComplete() {

        }
    };

    private Observer<List<SubTotalBean>> subTotalObserver = new Observer<List<SubTotalBean>>() {
        @Override
        public void onSubscribe(Disposable d) {
            addDisposable(d);
        }

        @Override
        public void onNext(List<SubTotalBean> list) {
            subTotalList.setValue(list);
        }

        @Override
        public void onError(Throwable e) {
            e.printStackTrace();
            messageObserver.setValue(e.getMessage());
        }

        @Override
        public void onComplete() {

        }
    };

    private static ObservableTransformer<List<TwoLineBean>, List<TwoLineBean>> twoLineTransformer = upstream -> upstream
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());

    private static ObservableTransformer<List<SubTotalBean>, List<SubTotalBean>> subTotalTransformer = upstream -> upstream
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());

}
