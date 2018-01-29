package com.king.app.tcareer.page.match.common;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.king.app.tcareer.R;
import com.king.app.tcareer.base.BaseMvpActivity;
import com.king.app.tcareer.model.GlideOptions;
import com.king.app.tcareer.model.ImageProvider;
import com.king.app.tcareer.model.db.entity.MatchNameBean;
import com.king.app.tcareer.model.db.entity.User;
import com.king.lib.tool.ui.RippleFactory;

import butterknife.BindView;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/1/29 16:49
 */
public class MatchCommonActivity extends BaseMvpActivity<MatchCommonPresenter> implements MatchCommonView {

    public static final String KEY_MATCH = "common_match";

    @BindView(R.id.iv_match)
    ImageView ivMatch;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_country)
    TextView tvCountry;
    @BindView(R.id.tv_level)
    TextView tvLevel;
    @BindView(R.id.tv_court)
    TextView tvCourt;
    @BindView(R.id.tv_king_name)
    TextView tvKingName;
    @BindView(R.id.tv_king_h2h)
    TextView tvKingH2h;
    @BindView(R.id.group_king)
    LinearLayout groupKing;
    @BindView(R.id.tv_flamenco_name)
    TextView tvFlamencoName;
    @BindView(R.id.tv_flamenco_h2h)
    TextView tvFlamencoH2h;
    @BindView(R.id.group_flamenco)
    LinearLayout groupFlamenco;
    @BindView(R.id.tv_henry_name)
    TextView tvHenryName;
    @BindView(R.id.tv_henry_h2h)
    TextView tvHenryH2h;
    @BindView(R.id.group_henry)
    LinearLayout groupHenry;
    @BindView(R.id.tv_qi_name)
    TextView tvQiName;
    @BindView(R.id.tv_qi_h2h)
    TextView tvQiH2h;
    @BindView(R.id.group_qi)
    LinearLayout groupQi;
    @BindView(R.id.tv_king_year)
    TextView tvKingYear;
    @BindView(R.id.tv_flamenco_year)
    TextView tvFlamencoYear;
    @BindView(R.id.tv_henry_year)
    TextView tvHenryYear;
    @BindView(R.id.tv_qi_year)
    TextView tvQiYear;

    @Override
    protected int getContentView() {
        return R.layout.activity_match_common;
    }

    @Override
    protected void initView() {
        groupKing.setBackground(RippleFactory.getRippleBackground(getResources().getColor(R.color.mview_layout_insert_bk)
                , getResources().getColor(R.color.ripple_material_dark)));
        groupFlamenco.setBackground(RippleFactory.getRippleBackground(getResources().getColor(R.color.mview_layout_search_bk)
                , getResources().getColor(R.color.ripple_material_dark)));
        groupHenry.setBackground(RippleFactory.getRippleBackground(getResources().getColor(R.color.mview_layout_h2h_bk)
                , getResources().getColor(R.color.ripple_material_dark)));
        groupQi.setBackground(RippleFactory.getRippleBackground(getResources().getColor(R.color.mview_layout_rank_bk)
                , getResources().getColor(R.color.ripple_material_dark)));

    }

    @Override
    protected MatchCommonPresenter createPresenter() {
        return new MatchCommonPresenter();
    }

    @Override
    protected void initData() {
        long matchNameId = getIntent().getLongExtra(KEY_MATCH, -1);
        if (matchNameId == -1) {
            showMessage("赛事不存在");
        }
        else {
            presenter.loadMatch(matchNameId);
        }
    }

    @Override
    public void postShowMatchInfor(final MatchNameBean matchNameBean) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvName.setText(matchNameBean.getName());
                tvCountry.setText(matchNameBean.getMatchBean().getCountry() + "/" + matchNameBean.getMatchBean().getCity());
                tvLevel.setText(matchNameBean.getMatchBean().getLevel());
                tvCourt.setText(matchNameBean.getMatchBean().getCourt());

                Glide.with(MatchCommonActivity.this)
                        .load("file://" + ImageProvider.getMatchHeadPath(matchNameBean.getName(), matchNameBean.getMatchBean().getCourt()))
                        .apply(GlideOptions.getDefaultMatchOptions())
                        .into(ivMatch);
            }
        });
    }

    @Override
    public void showUserInfor(User user, String h2h, String years) {

        if (user.getId() == 1) {
            tvKingH2h.setText(h2h);
            tvKingYear.setText(years);
        }
        else if (user.getId() == 2) {
            tvFlamencoH2h.setText(h2h);
            tvFlamencoYear.setText(years);
        }
        else if (user.getId() == 3) {
            tvHenryH2h.setText(h2h);
            tvHenryYear.setText(years);
        }
        else if (user.getId() == 4) {
            tvQiH2h.setText(h2h);
            tvQiYear.setText(years);
        }
    }
}
