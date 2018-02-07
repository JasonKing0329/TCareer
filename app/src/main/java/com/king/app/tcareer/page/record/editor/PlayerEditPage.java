package com.king.app.tcareer.page.record.editor;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.king.app.tcareer.R;
import com.king.app.tcareer.model.CompetitorParser;
import com.king.app.tcareer.model.GlideOptions;
import com.king.app.tcareer.model.ImageProvider;
import com.king.app.tcareer.model.bean.CompetitorBean;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.page.player.page.PlayerPageActivity;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/3/13 13:22
 */
public class PlayerEditPage implements View.OnClickListener {

    private IEditorHolder holder;
    private ImageView ivChangePlayer;
    private ImageView ivPlayer;
    private ViewGroup groupPlayer;

    protected EditText et_rankp1, et_seedp1, et_rank, et_seed;
    private TextView tvUser;
    private TextView tvCompetitor;
    private TextView tvH2h;
    private TextView tvNameEng, tvBirthday;

    public PlayerEditPage(IEditorHolder holder) {
        this.holder = holder;
    }

    public void initView() {
        groupPlayer = (ViewGroup) holder.getActivity().findViewById(R.id.editor_player_group);
        et_rankp1 = (EditText) holder.getActivity().findViewById(R.id.editor_player_rank1);
        et_seedp1 = (EditText) holder.getActivity().findViewById(R.id.editor_player_seed1);
        tvUser = (TextView) holder.getActivity().findViewById(R.id.editor_player_user_name);
        tvH2h = (TextView) holder.getActivity().findViewById(R.id.editor_player_h2h);
        tvH2h.setOnClickListener(this);
        tvCompetitor = (TextView) holder.getActivity().findViewById(R.id.editor_player_name);
        tvNameEng = (TextView) holder.getActivity().findViewById(R.id.editor_player_name_eng);
        tvBirthday = (TextView) holder.getActivity().findViewById(R.id.editor_player_birthday);
        et_rank = (EditText) holder.getActivity().findViewById(R.id.editor_player_rank2);
        et_seed = (EditText) holder.getActivity().findViewById(R.id.editor_player_seed2);
        ivPlayer = (ImageView) holder.getActivity().findViewById(R.id.editor_player_image);
        ivChangePlayer = (ImageView) holder.getActivity().findViewById(R.id.edit_player_change);
        ivChangePlayer.setOnClickListener(this);
    }

    public void showUser(User user) {
        tvUser.setText(user.getNameShort());
    }

    public void reset() {
        et_rankp1.setText("");
        et_seedp1.setText("");
        et_rank.setText("");
        et_seed.setText("");
        tvCompetitor.setText("");
        tvBirthday.setText("");
        tvNameEng.setText("");
        tvH2h.setText("H2H");
        groupPlayer.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        if (v == ivChangePlayer) {
            // 回调在onPlayerSelected
            holder.selectPlayer();
        } else if (v == tvH2h) {
            showH2hDetails();
        }
    }

    private void showH2hDetails() {
        if (holder.getPresenter().getCompetitor() != null) {
            Intent intent = new Intent(holder.getActivity(), PlayerPageActivity.class);
            intent.putExtra(PlayerPageActivity.KEY_USER_ID, holder.getPresenter().getUser().getId());
            if (holder.getPresenter().getCompetitor() instanceof User) {
                intent.putExtra(PlayerPageActivity.KEY_COMPETITOR_IS_USER, true);
            }
            intent.putExtra(PlayerPageActivity.KEY_COMPETITOR_ID, holder.getPresenter().getCompetitor().getId());
            holder.getActivity().startActivity(intent);
        }
    }

    public String getCompetitor() {
        return tvCompetitor.getText().toString();
    }

    /**
     * selectPlayer的回调
     *
     * @param bean
     */
    public void onPlayerSelected(CompetitorBean bean) {
        holder.getPresenter().setCompetitor(bean);
        groupPlayer.setVisibility(View.VISIBLE);
        tvCompetitor.setText(bean.getNameChn().concat("(").concat(bean.getCountry()).concat(")"));
        tvBirthday.setText(bean.getBirthday());
        tvNameEng.setText(bean.getNameEng());
        Glide.with(holder.getActivity())
                .load(ImageProvider.getDetailPlayerPath(bean.getNameChn()))
                .apply(GlideOptions.getEditorPlayerOptions())
                .into(ivPlayer);

        holder.getPresenter().queryH2H();
    }

    public void showH2h(int win, int lose) {
        tvH2h.setText("H2H  " + win + "-" + lose);
    }

    public String fillRecord() {
        if (holder.getPresenter().getCompetitor() == null) {
            return holder.getActivity().getString(R.string.editor_null_player);
        }
        int rank = 0;
        try {
            rank = Integer.parseInt(et_rankp1.getText().toString());
        } catch (Exception e) {}
        int seed = 0;
        try {
            seed = Integer.parseInt(et_seedp1.getText().toString());
        } catch (Exception e) {}
        int rankCpt = 0;
        try {
            rankCpt = Integer.parseInt(et_rank.getText().toString());
        } catch (Exception e) {}
        int seedCpt = 0;
        try {
            seedCpt = Integer.parseInt(et_seed.getText().toString());
        } catch (Exception e) {}

        holder.getPresenter().fillPlayerPage(rank, seed, rankCpt, seedCpt);
        return null;
    }

    public void showRecord(Record record) {
        et_rankp1.setText(String.valueOf(record.getRank()));
        et_seedp1.setText(String.valueOf(record.getSeed()));
        et_rank.setText(String.valueOf(record.getRankCpt()));
        et_seed.setText(String.valueOf(record.getSeedpCpt()));
        onPlayerSelected(CompetitorParser.getCompetitorFrom(record));
    }
}
