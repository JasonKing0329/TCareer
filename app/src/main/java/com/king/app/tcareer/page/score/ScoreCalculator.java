package com.king.app.tcareer.page.score;

import android.text.TextUtils;
import android.view.View;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.IFragmentHolder;
import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.base.mvvm.BaseViewModel;
import com.king.app.tcareer.databinding.FragmentScoreCalculatorBinding;
import com.king.app.tcareer.model.DateManager;
import com.king.app.tcareer.model.db.entity.RankWeek;
import com.king.app.tcareer.model.db.entity.RankWeekDao;
import com.king.app.tcareer.view.dialog.frame.FrameContentFragment;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/6 13:30
 */
public class ScoreCalculator extends FrameContentFragment<FragmentScoreCalculatorBinding, BaseViewModel> {

    private ScoreModel scoreModel;

    private long mUserId;

    private ValidScores mValidScore;

    private RankWeek mUpdateData;

    private DateManager dateManager;

    public void setUserId(long userId) {
        mUserId = userId;
    }

    public void setUpdateData(RankWeek updateData) {
        this.mUpdateData = updateData;
    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_score_calculator;
    }

    @Override
    protected BaseViewModel createViewModel() {
        return null;
    }

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {

    }

    @Override
    protected void onCreate(View view) {
        scoreModel = new ScoreModel();
        mBinding.progress.setVisibility(View.INVISIBLE);

        dateManager = new DateManager();

        mBinding.btnStart.setOnClickListener(v -> {
            dateManager.pickDate(getActivity(), () -> {
                mBinding.btnStart.setText(dateManager.getDateStr());

                mBinding.etScore.setText("");
                onDateChanged();
            });
        });
        mBinding.btnOk.setOnClickListener(v -> calculateScore());
        mBinding.btnInsert.setOnClickListener(v -> insertToDb());
    }

    @Override
    protected void onCreateData() {

        if (mUpdateData != null) {
            dateManager.setDate(mUpdateData.getDate());
            mBinding.btnOk.setVisibility(View.GONE);
            mBinding.btnInsert.setText("Update");
            mBinding.btnStart.setText(dateManager.getDateStr());
            mBinding.btnStart.setEnabled(false);
            mBinding.etRank.setText(String.valueOf(mUpdateData.getRank()));
            mBinding.etScore.setText(String.valueOf(mUpdateData.getScore()));
            calculateScore();
        }
    }

    private void onDateChanged() {
        // 查询是否已录入rank
        queryRankWeek(dateManager.getDateStr())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<RankWeek>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(RankWeek rankWeek) {
                        if (rankWeek != null) {
                            if (rankWeek.getId() != null) {
                                mBinding.etRank.setText(String.valueOf(rankWeek.getRank()));
                            }
                            mBinding.etScore.setText(String.valueOf(rankWeek.getScore()));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void calculateScore() {
        mBinding.progress.setVisibility(View.VISIBLE);
        mBinding.tvScore.setText("");
        mBinding.tvScoreDetail.setText("");

        scoreModel.queryScoreToDate(dateManager.getDateStr(), mUserId)
                .flatMap(new Function<List<ScoreBean>, ObservableSource<ValidScores>>() {
                    @Override
                    public ObservableSource<ValidScores> apply(List<ScoreBean> list) throws Exception {
                        return scoreModel.countValidScores(list, mUserId, dateManager.getDateStr());
                    }
                })
                .map(new Function<ValidScores, ValidScores>() {
                    @Override
                    public ValidScores apply(ValidScores validScores) throws Exception {
                        return sortValidScores(validScores);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<ValidScores>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ValidScores validScores) {

                        mValidScore = validScores;

                        StringBuffer lineScore = new StringBuffer("有效积分(");
                        lineScore.append(validScores.getValidList().size())
                                .append("站)： ")
                                .append(validScores.getValidScore());
                        if (validScores.getStartScore() > 0) {
                            lineScore.append("，起记分：").append(validScores.getStartScore());
                        }
                        if (validScores.getFrozenScore() > 0) {
                            lineScore.append("，冻结积分：").append(validScores.getFrozenScore());
                        }
                        showScore(lineScore.toString());

                        StringBuffer buffer = new StringBuffer();
                        for (ScoreBean bean:validScores.getValidList()) {
                            buffer.append("\n");
                            if (bean.getMatchBean() != null) {
                                int month = bean.getMatchBean().getMatchBean().getMonth();
                                buffer.append(bean.getYear()).append("-")
                                        .append(month < 10 ? "0" + month : month).append(" ");
                            }
                            buffer.append(bean.getTitle()).append("(").append(bean.getScore()).append(")");
                        }
                        String detail = buffer.toString();
                        if (detail.length() > 1) {
                            detail = detail.substring(1);
                        }
                        showScoreDetail(detail);

                        mBinding.progress.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        showMessageLong("查询异常：" + e.getMessage());
                        mBinding.progress.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void showScore(String score) {
        mBinding.tvScore.setText(score);
        mBinding.etScore.setText(String.valueOf(mValidScore.getValidScore()));
    }

    private void showScoreDetail(String detail) {
        mBinding.tvScoreDetail.setText(detail);
    }

    private ValidScores sortValidScores(ValidScores validScores) {
        Collections.sort(validScores.getValidList(), new ScoreComparator());
        return validScores;
    }

    private Observable<RankWeek> queryRankWeek(final String strDate) {
        return Observable.create(new ObservableOnSubscribe<RankWeek>() {
            @Override
            public void subscribe(ObservableEmitter<RankWeek> e) throws Exception {
                RankWeekDao dao = TApplication.getInstance().getDaoSession().getRankWeekDao();
                Date date = dateManager.getDate();
                RankWeek week = null;
                try {
                    week = dao.queryBuilder()
                            .where(RankWeekDao.Properties.Date.eq(date)
                                    , RankWeekDao.Properties.UserId.eq(mUserId))
                            .build().unique();
                } catch (Exception exception) {}
                if (week == null) {
                    week = new RankWeek();
                }
                e.onNext(week);
            }
        });
    }

    private void insertToDb() {
        final String rank = mBinding.etRank.getText().toString();
        if (TextUtils.isEmpty(rank)) {
            showMessageLong("Please input rank");
            return;
        }

        mBinding.progress.setVisibility(View.VISIBLE);

        queryRankWeek(dateManager.getDateStr())
                .flatMap(new Function<RankWeek, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(final RankWeek rankWeek) throws Exception {
                        return new ObservableSource<Object>() {
                            @Override
                            public void subscribe(Observer<? super Object> observer) {
                                RankWeek week = rankWeek;
                                Date date = dateManager.getDate();
                                week.setUserId(mUserId);
                                week.setRank(Integer.parseInt(rank));
                                int score = mValidScore.getValidScore();
                                if (!TextUtils.isEmpty(mBinding.etScore.getText().toString())) {
                                    score = Integer.parseInt(mBinding.etScore.getText().toString());
                                }
                                week.setScore(score);
                                week.setDate(date);
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(date);
                                week.setYear(calendar.get(Calendar.YEAR));
                                week.setWeek(calendar.get(Calendar.WEEK_OF_YEAR));
                                RankWeekDao dao = TApplication.getInstance().getDaoSession().getRankWeekDao();
                                dao.insertOrReplace(week);
                                observer.onNext(new Object());
                            }
                        };
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object object) {
                        showMessageShort("Insert or replace successfully");
                        mBinding.progress.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        showMessageLong("Insert or replace failed: " + e.getMessage());
                        mBinding.progress.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 按年、周升序排列，罚分放最后
     */
    private class ScoreComparator implements Comparator<ScoreBean> {

        @Override
        public int compare(ScoreBean left, ScoreBean right) {
            // 罚分放最后
            if (left.getMatchBean() == null) {
                return 1;
            }
            else {
                // 先年份，后week
                int value = left.getYear() - right.getYear();
                if (value > 0) {
                    return 1;
                }
                else if (value < 0) {
                    return -1;
                }
                else {
                    value = left.getMatchBean().getMatchBean().getWeek() - right.getMatchBean().getMatchBean().getWeek();
                    if (value > 0) {
                        return 1;
                    }
                    else if (value < 0) {
                        return -1;
                    }
                    else {
                        return 0;
                    }
                }
            }
        }
    }
}
