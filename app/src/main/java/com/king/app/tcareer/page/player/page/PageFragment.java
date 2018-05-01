package com.king.app.tcareer.page.player.page;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.BaseMvpFragment;
import com.king.app.tcareer.base.IFragmentHolder;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.page.record.page.RecordPageActivity;
import com.king.app.tcareer.utils.DebugLog;

import java.util.List;

import butterknife.BindView;

/**
 * 描述: fragment to present records of competitor
 * <p/>作者：景阳
 * <p/>创建时间: 2017/11/20 16:05
 */
public class PageFragment extends BaseMvpFragment<SubPagePresenter> implements SubPageView {

    protected static final String KEY_USER_ID = "key_user_id";
    protected static final String KEY_COURT = "key_court";
    protected static final String KEY_LEVEL = "key_level";
    protected static final String KEY_YEAR = "key_year";

    @BindView(R.id.rv_records)
    RecyclerView rvRecords;
    @BindView(R.id.tv_year)
    TextView tvYear;

    private PageRecordAdapter cardAdapter;
    private FullRecordAdapter fullAdapter;

    private IPageHolder holder;

    private int mFirstPosition;

    private boolean initialYearTitle;

    public static PageFragment newInstance(long userId, String court, String level, String year) {
        DebugLog.e("newInstance userId=" + userId + ", court=" + court + ", level=" + level + ", year=" + year);
        PageFragment fragment = new PageFragment();
        Bundle args = new Bundle();
        args.putLong(KEY_USER_ID, userId);
        args.putString(KEY_COURT, court);
        args.putString(KEY_LEVEL, level);
        args.putString(KEY_YEAR, year);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {
        this.holder = (IPageHolder) holder;
    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_player_page;
    }

    @Override
    protected void onCreate(View view) {
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rvRecords.setLayoutManager(manager);
    }

    @Override
    protected SubPagePresenter createPresenter() {
        return new SubPagePresenter();
    }

    @Override
    protected void onCreateData() {
        presenter.setCompetitor(holder.getCompetitor());

        long userId = getArguments().getLong(KEY_USER_ID);
        String court = getArguments().getString(KEY_COURT);
        String level = getArguments().getString(KEY_LEVEL);
        String year = getArguments().getString(KEY_YEAR);
        DebugLog.e("userId=" + userId + ", court=" + court + ", level=" + level + ", year=" + year);
        presenter.createRecords(userId, court, level, year);
    }

    private void refreshYear(int first) {
        mFirstPosition = first;
        Palette.Swatch swatch = fullAdapter.getSwatch(first);
        if (swatch != null) {
            GradientDrawable drawable = (GradientDrawable) tvYear.getBackground();
            drawable.setColor(swatch.getRgb());
            tvYear.setTextColor(swatch.getBodyTextColor());
        }
        tvYear.setText(presenter.getYearTitle(fullAdapter.getYear(first)));
    }

    @Override
    public void onDataLoaded(List<Object> list, int viewType) {
        if (viewType == SubPagePresenter.TYPE_PURE) {
            tvYear.setVisibility(View.VISIBLE);
            rvRecords.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int first = ((LinearLayoutManager) rvRecords.getLayoutManager()).findFirstVisibleItemPosition();
                    if (first != mFirstPosition) {
                        refreshYear(first);
                    }
                }
            });

            fullAdapter = new FullRecordAdapter(presenter.getUser());
            fullAdapter.setList(list);
            fullAdapter.setOnItemListener(new FullRecordAdapter.OnItemListener() {
                @Override
                public void onClickRecord(View v, Record record) {
                    showRecordPage(v, record);
                }

                @Override
                public void onSwatchLoaded(int position, Palette.Swatch swatch) {
                    // 进入界面初始化yearTitle，后续会随着recyclerView的滚动而变化
                    if (!initialYearTitle) {
                        initialYearTitle = true;
                        refreshYear(position);
                    }
                }
            });
            rvRecords.setAdapter(fullAdapter);
        }
        else {
            tvYear.setVisibility(View.GONE);
            cardAdapter = new PageRecordAdapter(presenter.getUser());
            cardAdapter.setList(list);
            cardAdapter.setOnItemClickListener(new PageRecordAdapter.OnItemClickListener() {
                @Override
                public void onClickRecord(View v, final Record record) {
                    showRecordPage(v, record);
                }
            });
            rvRecords.setAdapter(cardAdapter);
        }
    }

    private void showRecordPage(View view, Record record) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), RecordPageActivity.class);
        intent.putExtra(RecordPageActivity.KEY_RECORD_ID, record.getId());
        ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(getActivity()
                , Pair.create(view.findViewById(R.id.iv_match),getString(R.string.anim_match_page_head)));
        startActivity(intent, transitionActivityOptions.toBundle());
    }
}
