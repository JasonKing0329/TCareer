package com.king.app.tcareer.page.score;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.IFragmentHolder;
import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.model.db.entity.RankWeek;
import com.king.app.tcareer.model.db.entity.RankWeekDao;
import com.king.app.tcareer.view.dialog.DraggableDialogFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
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
public class ScoreCalculator extends DraggableDialogFragment {

    private DialogInterface.OnDismissListener onDismissListener;

    private CalculatorFragment ftCalculator;

    private long mUserId;

    @Override
    protected View getToolbarView(ViewGroup groupToolbar) {
        setTitle("Score calculator");
        requestCloseAction();
        return null;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialog);
        }
    }

    @Override
    protected Fragment getContentViewFragment() {
        ftCalculator = new CalculatorFragment();
        ftCalculator.setUserId(mUserId);
        return ftCalculator;
    }

    public void setUserId(long userId) {
        mUserId = userId;
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    public static class CalculatorFragment extends ContentFragment {

        @BindView(R.id.btn_start)
        Button btnStart;
        @BindView(R.id.btn_insert)
        Button btnInsert;
        @BindView(R.id.tv_score)
        TextView tvScore;
        @BindView(R.id.et_rank)
        EditText etRank;
        @BindView(R.id.et_score)
        EditText etScore;
        @BindView(R.id.tv_score_detail)
        TextView tvScoreDetail;
        @BindView(R.id.progress)
        ProgressBar progressBar;

        Unbinder unbinder;

        private ScoreModel scoreModel;

        private int nYearStart;
        private int nMonthStart;
        private int nDayStart;

        private long mUserId;

        private String mDate;

        private ValidScores mValidScore;

        @Override
        protected int getContentLayoutRes() {
            return R.layout.dialog_score_calculator;
        }

        @Override
        protected void onCreate(View view) {
            unbinder = ButterKnife.bind(this, view);
            scoreModel = new ScoreModel();
            progressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void bindChildFragmentHolder(IFragmentHolder holder) {

        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            if (unbinder != null) {
                unbinder.unbind();
            }
        }

        public void setUserId(long userId) {
            this.mUserId = userId;
        }

        @OnClick({R.id.btn_start, R.id.btn_ok, R.id.btn_insert})
        public void onViewClicked(View view) {
            switch (view.getId()) {
                case R.id.btn_start:
                    pickStartDate();
                    break;
                case R.id.btn_ok:
                    calculateScore();
                    break;
                case R.id.btn_insert:
                    insertToDb();
                    break;
            }
        }

        private void pickStartDate() {
            if (nYearStart == 0) {
                Calendar calendar = Calendar.getInstance();
                nYearStart = calendar.get(Calendar.YEAR);
                nMonthStart= calendar.get(Calendar.MONTH);
                nDayStart = 1;
            }

            DatePickerDialog startDlg = new DatePickerDialog(getContext(),
                    startListener, nYearStart, nMonthStart - 1, nDayStart);
            startDlg.show();
        }

        DatePickerDialog.OnDateSetListener startListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                updateDate(year, monthOfYear, dayOfMonth);
            }
        };

        private void updateDate(int year, int monthOfYear, int dayOfMonth) {
            nYearStart = year;
            nMonthStart = monthOfYear + 1;//日期控件的月份是从0开始编号的
            nDayStart = dayOfMonth;
            StringBuffer buffer = new StringBuffer();
            buffer.append(nYearStart).append("-");
            buffer.append(nMonthStart < 10 ? "0" + nMonthStart : nMonthStart).append("-");
            buffer.append(nDayStart < 10 ? "0" + nDayStart : nDayStart);
            mDate = buffer.toString();
            btnStart.setText(mDate);

            etScore.setText("");
            // 查询是否已录入rank
            queryRankWeek(mDate)
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
                                    etRank.setText(String.valueOf(rankWeek.getRank()));
                                }
                                etScore.setText(String.valueOf(rankWeek.getScore()));
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
            progressBar.setVisibility(View.VISIBLE);
            tvScore.setText("");
            tvScoreDetail.setText("");

            scoreModel.queryScoreToDate(mDate, mUserId)
                    .flatMap(new Function<List<ScoreBean>, ObservableSource<ValidScores>>() {
                        @Override
                        public ObservableSource<ValidScores> apply(List<ScoreBean> list) throws Exception {
                            return scoreModel.countValidScores(list, mUserId, mDate);
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

                            StringBuffer lineScore = new StringBuffer("有效积分（");
                            lineScore.append(validScores.getValidList().size())
                                    .append("站）： ")
                                    .append(validScores.getValidScore());
                            if (validScores.getStartScore() > 0) {
                                lineScore.append("  起记分：").append(validScores.getStartScore());
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

                            progressBar.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            showMessageLong("查询异常：" + e.getMessage());
                            progressBar.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }

        private void showScore(String score) {
            tvScore.setText(score);
            etScore.setText(String.valueOf(mValidScore.getValidScore()));
        }

        private void showScoreDetail(String detail) {
            tvScoreDetail.setText(detail);
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
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = sdf.parse(strDate);
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
            final String rank = etRank.getText().toString();
            if (TextUtils.isEmpty(rank)) {
                showMessageLong("Please input rank");
                return;
            }

            progressBar.setVisibility(View.VISIBLE);

            queryRankWeek(mDate)
                    .flatMap(new Function<RankWeek, ObservableSource<?>>() {
                        @Override
                        public ObservableSource<?> apply(final RankWeek rankWeek) throws Exception {
                            return new ObservableSource<Object>() {
                                @Override
                                public void subscribe(Observer<? super Object> observer) {
                                    RankWeek week = rankWeek;
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                    try {
                                        Date date = sdf.parse(mDate);
                                        week.setUserId(mUserId);
                                        week.setRank(Integer.parseInt(rank));
                                        int score = mValidScore.getValidScore();
                                        if (!TextUtils.isEmpty(etScore.getText().toString())) {
                                            score = Integer.parseInt(etScore.getText().toString());
                                        }
                                        week.setScore(score);
                                        week.setDate(date);
                                        Calendar calendar = Calendar.getInstance();
                                        calendar.setTime(date);
                                        week.setYear(calendar.get(Calendar.YEAR));
                                        week.setWeek(calendar.get(Calendar.WEEK_OF_YEAR));
                                        RankWeekDao dao = TApplication.getInstance().getDaoSession().getRankWeekDao();
                                        dao.insertOrReplace(week);
                                    } catch (ParseException e) {
                                        observer.onError(e);
                                    }
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
                            progressBar.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            showMessageLong("Insert or replace failed: " + e.getMessage());
                            progressBar.setVisibility(View.INVISIBLE);
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
}
