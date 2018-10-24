package com.king.app.tcareer.page.match;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.king.app.tcareer.R;
import com.king.app.tcareer.base.IFragmentHolder;
import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.GlideOptions;
import com.king.app.tcareer.model.ImageProvider;
import com.king.app.tcareer.model.db.entity.MatchNameBean;
import com.king.app.tcareer.model.db.entity.MatchNameBeanDao;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.model.db.entity.RecordDao;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.page.match.page.MatchPageActivity;
import com.king.app.tcareer.page.player.slider.PlayerSlideActivity;
import com.king.app.tcareer.utils.ScreenUtils;
import com.king.app.tcareer.view.dialog.DraggableDialogFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 描述: match dialog
 * <p/>作者：景阳
 * <p/>创建时间: 2018/2/1 11:44
 */
public class MatchDialog extends DraggableDialogFragment {

    private long matchNameId;
    private String date;

    private MatchFragment matchFragment;
    private String matchName;
    private User user;

    @Override
    protected View getToolbarView(ViewGroup groupToolbar) {
        requestCloseAction();
        setTitle(matchName);
        return null;
    }

    @Override
    protected Fragment getContentViewFragment() {
        matchFragment = new MatchFragment();
        matchFragment.setMatch(matchNameId, date);
        matchFragment.setUser(user);
        return matchFragment;
    }

    public void setMatch(long matchNameId, String matchName, String date) {
        this.matchNameId = matchNameId;
        this.matchName = matchName;
        this.date = date;
    }

    @Override
    protected int getMaxHeight() {
        return ScreenUtils.getScreenHeight(getActivity()) * 4 / 5;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public static class MatchFragment extends ContentFragment {

        @BindView(R.id.iv_match)
        ImageView ivMatch;
        @BindView(R.id.tv_place)
        TextView tvPlace;
        @BindView(R.id.tv_level)
        TextView tvLevel;
        @BindView(R.id.tv_date)
        TextView tvDate;
        @BindView(R.id.tv_rank)
        TextView tvRank;
        @BindView(R.id.tv_seed)
        TextView tvSeed;
        @BindView(R.id.tv_achieve)
        TextView tvAchieve;
        @BindView(R.id.rv_list)
        RecyclerView rvList;

        private MatchItemAdapter itemAdapter;
        private CompositeDisposable compositeDisposable;

        private long matchNameId;
        private String date;
        private User user;

        @Override
        protected int getContentLayoutRes() {
            return R.layout.dialog_match;
        }

        @Override
        protected void onCreate(View view) {

            LinearLayoutManager manager = new LinearLayoutManager(getActivity());
            manager.setOrientation(LinearLayoutManager.VERTICAL);
            rvList.setLayoutManager(manager);

            ivMatch.setOnClickListener(v -> goToMatchPage());

            compositeDisposable = new CompositeDisposable();
            loadData();
        }

        private void goToMatchPage() {
            Intent intent = new Intent(getContext(), MatchPageActivity.class);
            intent.putExtra(MatchPageActivity.KEY_USER_ID, user.getId());
            intent.putExtra(MatchPageActivity.KEY_MATCH_NAME_ID, matchNameId);
            startActivity(intent);
        }

        @Override
        protected void bindChildFragmentHolder(IFragmentHolder holder) {

        }

        public void setMatch(long matchNameId, String date) {
            this.matchNameId = matchNameId;
            this.date = date;
        }

        @Override
        public void onDestroyView() {
            if (compositeDisposable != null) {
                compositeDisposable.clear();
            }
            super.onDestroyView();
        }

        private void loadData() {
            Observable.create(new ObservableOnSubscribe<Object>() {
                @Override
                public void subscribe(ObservableEmitter<Object> e) throws Exception {
                    // 查询match
                    MatchNameBeanDao dao = TApplication.getInstance().getDaoSession().getMatchNameBeanDao();
                    MatchNameBean bean = dao.queryBuilder()
                            .where(MatchNameBeanDao.Properties.Id.eq(matchNameId))
                            .build().unique();
                    bean.getMatchBean();
                    e.onNext(bean);

                    // 查询records，按自然顺序降序
                    RecordDao recordDao = TApplication.getInstance().getDaoSession().getRecordDao();
                    List<Record> recordList = recordDao.queryBuilder()
                            .where(RecordDao.Properties.MatchNameId.eq(matchNameId)
                                , RecordDao.Properties.DateStr.eq(date)
                                , RecordDao.Properties.UserId.eq(user.getId()))
                            .orderDesc(RecordDao.Properties.Id)
                            .build().list();
                    e.onNext(recordList);
                    e.onComplete();
                }
            }).observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Observer<Object>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            compositeDisposable.add(d);
                        }

                        @Override
                        public void onNext(Object object) {
                            if (object instanceof MatchNameBean) {
                                MatchNameBean bean = (MatchNameBean) object;
                                showMatch(bean);
                            }
                            else {
                                List<Record> recordList = (List<Record>) object;
                                showRecords(recordList);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            showMessageLong(e.getMessage());
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }

        private void showMatch(MatchNameBean bean) {
            tvDate.setText(date);
            tvLevel.setText(bean.getMatchBean().getLevel() + "/" + bean.getMatchBean().getCourt());
            tvPlace.setText(bean.getMatchBean().getCountry() + "/" + bean.getMatchBean().getCity());

            Glide.with(getActivity())
                    .load(ImageProvider.getMatchHeadPath(bean.getName(), bean.getMatchBean().getCourt()))
                    .apply(GlideOptions.getDefaultMatchOptions())
                    .into(ivMatch);
        }

        private void showRecords(List<Record> recordList) {
            if (recordList.size() == 0) {
                return;
            }
            // 已按降序排列
            Record record = recordList.get(0);
            tvRank.setText("Rank(" + record.getRank() + ")");
            if (record.getSeed() > 0) {
                tvSeed.setText("/Seed(" + record.getSeed() + ")");
                tvSeed.setVisibility(View.VISIBLE);
            }
            else {
                tvSeed.setVisibility(View.GONE);
            }
            if (record.getRound().equals(AppConstants.RECORD_MATCH_ROUNDS[0])) {
                if (record.getWinnerFlag() == AppConstants.WINNER_USER) {
                    tvAchieve.setText(AppConstants.CHAMPOION);
                }
                else {
                    tvAchieve.setText(AppConstants.RUNNERUP);
                }
            }
            else {
                tvAchieve.setText(record.getRound());
            }

            itemAdapter = new MatchItemAdapter(recordList);
            rvList.setAdapter(itemAdapter);
        }

        public void setUser(User user) {
            this.user = user;
        }
    }
}
