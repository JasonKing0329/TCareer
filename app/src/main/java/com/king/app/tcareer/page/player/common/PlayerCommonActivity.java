package com.king.app.tcareer.page.player.common;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.king.app.tcareer.R;
import com.king.app.tcareer.base.BaseMvpActivity;
import com.king.app.tcareer.model.GlideOptions;
import com.king.app.tcareer.model.ImageProvider;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.page.player.manage.PlayerViewBean;
import com.king.app.tcareer.utils.ConstellationUtil;
import com.king.lib.tool.ui.RippleFactory;

import butterknife.BindView;

/**
 * @desc
 * @auth 景阳
 * @time 2018/1/31 0031 11:03
 */

public class PlayerCommonActivity extends BaseMvpActivity<PlayerCommonPresenter> implements PlayerCommonView {

    public static final String KEY_PLAYER = "player_id";

    public static final String KEY_IS_USER = "is_user";

    @BindView(R.id.iv_player)
    ImageView ivPlayer;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_name_eng)
    TextView tvNameEng;
    @BindView(R.id.tv_birthday)
    TextView tvBirthday;
    @BindView(R.id.tv_place)
    TextView tvPlace;
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

    @Override
    protected int getContentView() {
        return R.layout.activity_player_common;
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
    protected PlayerCommonPresenter createPresenter() {
        return new PlayerCommonPresenter();
    }

    @Override
    protected void initData() {
        long playerId = getIntent().getLongExtra(KEY_PLAYER, -1);
        if (playerId == -1) {
            showMessage("player不存在");
        }
        else {
            presenter.loadPlayer(playerId, getIntent().getBooleanExtra(KEY_IS_USER, false));
        }
    }

    @Override
    public void showPlayer(PlayerViewBean playerBean) {
        tvName.setText(playerBean.getName());

        if (TextUtils.isEmpty(playerBean.getNameEng()) || playerBean.getName().equals(playerBean.getNameEng())) {
            tvNameEng.setVisibility(View.GONE);
        }
        else {
            tvNameEng.setText(playerBean.getNameEng());
        }
        tvPlace.setText(playerBean.getCountry());

        if (TextUtils.isEmpty(playerBean.getBirthday())) {
            tvBirthday.setVisibility(View.GONE);
        }
        else {
            String constel = "";
            try {
                constel = ConstellationUtil.getConstellationChn(playerBean.getBirthday());
            } catch (ConstellationUtil.ConstellationParseException e) {
                e.printStackTrace();
            }
            String birthday = playerBean.getBirthday();
            if (!TextUtils.isEmpty(constel)) {
                birthday = birthday.concat("(").concat(constel).concat(")");
            }
            tvBirthday.setText(birthday);
        }

        Glide.with(this)
                .load(ImageProvider.getDetailPlayerPath(playerBean.getName()))
                .apply(GlideOptions.getEditorPlayerOptions())
                .into(ivPlayer);

    }

    @Override
    public void showH2H(User user, int win, int lose) {

        if (user.getId() == 1) {
            tvKingH2h.setText(win + " - " + lose);
            tvKingName.setText(user.getNameChn());
        }
        else if (user.getId() == 2) {
            tvFlamencoH2h.setText(win + " - " + lose);
            tvFlamencoName.setText(user.getNameChn());
        }
        else if (user.getId() == 3) {
            tvHenryH2h.setText(win + " - " + lose);
            tvHenryName.setText(user.getNameChn());
        }
        else if (user.getId() == 4) {
            tvQiH2h.setText(win + " - " + lose);
            tvQiName.setText(user.getNameChn());
        }
    }
}
