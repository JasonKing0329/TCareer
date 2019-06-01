package com.king.app.tcareer.page.match.recent;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.design.widget.TabLayout;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.king.app.tcareer.R;
import com.king.app.tcareer.base.mvvm.MvvmActivity;
import com.king.app.tcareer.databinding.ActivityRecentMatchBinding;
import com.king.app.tcareer.model.GlideOptions;
import com.king.app.tcareer.model.ImageProvider;
import com.king.app.tcareer.model.db.entity.MatchNameBean;
import com.king.app.tcareer.model.palette.PaletteCallback;
import com.king.app.tcareer.model.palette.PaletteRequestListener;
import com.king.app.tcareer.model.palette.PaletteResponse;
import com.king.app.tcareer.model.palette.ViewColorBound;
import com.king.app.tcareer.page.player.page.TabCustomView;

import java.util.Calendar;
import java.util.List;

public class RecentMatchActivity extends MvvmActivity<ActivityRecentMatchBinding, RecentViewModel> {

    public static final String KEY_MATCH_ID = "match_id";
    public static final String KEY_YEAR = "year";

    private RecentRecordAdapter recordAdapter;

    @Override
    protected int getContentView() {
        return R.layout.activity_recent_match;
    }

    @Override
    protected RecentViewModel createViewModel() {
        return ViewModelProviders.of(this).get(RecentViewModel.class);
    }

    @Override
    protected void initView() {
        setSupportActionBar(mBinding.toolbar);
        mBinding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_filterrable);
        mBinding.toolbar.setOverflowIcon(getResources().getDrawable(R.drawable.ic_more_vert_white_24dp));
        mBinding.toolbar.setNavigationOnClickListener(v -> finish());
        mBinding.collapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.transparent));
        mBinding.collapsingToolbar.setCollapsedTitleTextColor(getResources().getColor(R.color.white));

        mBinding.rvList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    protected void initData() {
        mModel.matchObserver.observe(this, bean -> showMatch(bean));
        mModel.yearsObserver.observe(this, list -> showYears(list));
        mModel.recordsObserver.observe(this, list -> showRecords(list));
        mModel.loadMatch(getIntent().getLongExtra(KEY_MATCH_ID, -1), getIntent().getIntExtra(KEY_YEAR, Calendar.getInstance().get(Calendar.YEAR)));
    }

    private void showMatch(MatchNameBean bean) {
        mBinding.collapsingToolbar.setTitle(bean.getName());

        mBinding.tvName.setText(bean.getName());
        mBinding.tvPlace.setText(bean.getMatchBean().getCountry() + "/" + bean.getMatchBean().getCity());
        mBinding.tvType.setText(bean.getMatchBean().getLevel() + "/" + bean.getMatchBean().getCourt());
        String imagePath = ImageProvider.getMatchHeadPath(bean.getName(), bean.getMatchBean().getCourt());
        Glide.with(mBinding.ivMatch.getContext())
                .asBitmap()
                .load(imagePath)
                .apply(GlideOptions.getDefaultMatchOptions())
                .listener(new PaletteRequestListener(0, new PaletteCallback() {
                    @Override
                    public List<ViewColorBound> getTargetViews() {
                        return null;
                    }

                    @Override
                    public void noPaletteResponseLoaded(int position) {

                    }

                    @Override
                    public void onPaletteResponse(int position, PaletteResponse response) {
                        Palette.Swatch swatch = mModel.getTitlebarSwatch(response.palette);
                        if (swatch != null) {
                            mBinding.tabLayout.setBackgroundColor(swatch.getRgb());
                            for (int i = 0; i < mBinding.tabLayout.getTabCount(); i ++) {
                                ((TabCustomView) mBinding.tabLayout.getTabAt(i).getCustomView()).setTextColor(swatch.getBodyTextColor(), swatch.getTitleTextColor());
                            }
                            mBinding.collapsingToolbar.setContentScrimColor(swatch.getRgb());
                            mBinding.collapsingToolbar.setCollapsedTitleTextColor(swatch.getTitleTextColor());
                            mBinding.toolbar.getNavigationIcon().setColorFilter(swatch.getTitleTextColor(), PorterDuff.Mode.SRC_IN);
                        }
                    }
                }))
                .into(mBinding.ivMatch);
    }

    private void showYears(List<Integer> yearList) {
        if (yearList.size() > 6) {
            mBinding.tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        } else {
            mBinding.tabLayout.setTabMode(TabLayout.MODE_FIXED);
        }
        for (Integer year : yearList) {
            TabLayout.Tab shotsTab = mBinding.tabLayout.newTab();
            TabCustomView shotsTabCustomView = new TabCustomView(this);
            shotsTab.setCustomView(shotsTabCustomView);
            shotsTabCustomView.setCount(null);
            shotsTabCustomView.setContentCategory(String.valueOf(year));
            shotsTabCustomView.setTextColor(Color.WHITE, getResources().getColor(R.color.tab_actionbar_text_focus));
            mBinding.tabLayout.addTab(shotsTab);
        }
        mBinding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mModel.showYear(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void showRecords(List<Object> records) {
        if (recordAdapter == null) {
            recordAdapter = new RecentRecordAdapter();
            recordAdapter.setList(records);
            mBinding.rvList.setAdapter(recordAdapter);
        }
        else {
            recordAdapter.setList(records);
            recordAdapter.notifyDataSetChanged();
        }
    }
}
