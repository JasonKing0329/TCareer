package com.king.app.tcareer.page.match.recent;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.king.app.tcareer.R;
import com.king.app.tcareer.base.BaseMvpActivity;
import com.king.app.tcareer.model.GlideOptions;
import com.king.app.tcareer.model.ImageProvider;
import com.king.app.tcareer.model.db.entity.MatchNameBean;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.model.palette.PaletteCallback;
import com.king.app.tcareer.model.palette.PaletteRequestListener;
import com.king.app.tcareer.model.palette.PaletteResponse;
import com.king.app.tcareer.model.palette.ViewColorBound;
import com.king.app.tcareer.page.home.main.RecentRecordAdapter;
import com.king.app.tcareer.page.player.page.TabCustomView;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;

public class RecentMatchActivity extends BaseMvpActivity<RecentPresenter> implements RecentMatchView {

    public static final String KEY_MATCH_ID = "match_id";
    public static final String KEY_YEAR = "year";

    @BindView(R.id.iv_match)
    ImageView ivMatch;
    @BindView(R.id.v_cover)
    TextView vCover;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_type)
    TextView tvType;
    @BindView(R.id.tv_place)
    TextView tvPlace;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.rv_list)
    RecyclerView rvList;

    private RecentRecordAdapter recordAdapter;

    @Override
    protected int getContentView() {
        return R.layout.activity_recent_match;
    }

    @Override
    protected void initView() {
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_filterrable);
        toolbar.setOverflowIcon(getResources().getDrawable(R.drawable.ic_more_vert_white_24dp));
        toolbar.setNavigationOnClickListener(v -> finish());
        collapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.transparent));
        collapsingToolbar.setCollapsedTitleTextColor(getResources().getColor(R.color.white));

        rvList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    protected RecentPresenter createPresenter() {
        return new RecentPresenter();
    }

    @Override
    protected void initData() {
        presenter.loadMatch(getIntent().getLongExtra(KEY_MATCH_ID, -1), getIntent().getIntExtra(KEY_YEAR, Calendar.getInstance().get(Calendar.YEAR)));
    }

    @Override
    public void postShowMatch(MatchNameBean bean) {
        runOnUiThread(() -> {
            collapsingToolbar.setTitle(bean.getName());

            tvName.setText(bean.getName());
            tvPlace.setText(bean.getMatchBean().getCountry() + "/" + bean.getMatchBean().getCity());
            tvType.setText(bean.getMatchBean().getLevel() + "/" + bean.getMatchBean().getCourt());
            String imagePath = ImageProvider.getMatchHeadPath(bean.getName(), bean.getMatchBean().getCourt());
            Glide.with(ivMatch.getContext())
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
                            Palette.Swatch swatch = presenter.getTitlebarSwatch(response.palette);
                            if (swatch != null) {
                                tabLayout.setBackgroundColor(swatch.getRgb());
                                for (int i = 0; i < tabLayout.getTabCount(); i ++) {
                                    ((TabCustomView) tabLayout.getTabAt(i).getCustomView()).setTextColor(swatch.getBodyTextColor(), swatch.getTitleTextColor());
                                }
                                collapsingToolbar.setContentScrimColor(swatch.getRgb());
                                collapsingToolbar.setCollapsedTitleTextColor(swatch.getTitleTextColor());
                                toolbar.getNavigationIcon().setColorFilter(swatch.getTitleTextColor(), PorterDuff.Mode.SRC_IN);
                            }
                        }
                    }))
                    .into(ivMatch);
        });
    }

    @Override
    public void showYears(List<Integer> yearList) {
        if (yearList.size() > 6) {
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        } else {
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
        }
        for (Integer year : yearList) {
            TabLayout.Tab shotsTab = tabLayout.newTab();
            TabCustomView shotsTabCustomView = new TabCustomView(this);
            shotsTab.setCustomView(shotsTabCustomView);
            shotsTabCustomView.setCount(null);
            shotsTabCustomView.setContentCategory(String.valueOf(year));
            shotsTabCustomView.setTextColor(Color.WHITE, getResources().getColor(R.color.tab_actionbar_text_focus));
            tabLayout.addTab(shotsTab);
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                presenter.showYear(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void showRecords(List<Object> records) {
        if (recordAdapter == null) {
            recordAdapter = new RecentRecordAdapter();
            recordAdapter.setList(records);
            rvList.setAdapter(recordAdapter);
        }
        else {
            recordAdapter.setList(records);
            recordAdapter.notifyDataSetChanged();
        }
    }
}
