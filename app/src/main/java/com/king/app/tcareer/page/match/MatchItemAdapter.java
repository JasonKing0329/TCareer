package com.king.app.tcareer.page.match;

import android.graphics.Color;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.mvvm.BaseBindingAdapter;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.databinding.AdapterMatchDialogListBinding;
import com.king.app.tcareer.model.CompetitorParser;
import com.king.app.tcareer.model.ScoreParser;
import com.king.app.tcareer.model.bean.CompetitorBean;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.model.db.entity.User;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/2/1 14:32
 */
public class MatchItemAdapter extends BaseBindingAdapter<AdapterMatchDialogListBinding, Record> {

    private Record mFocusItem;

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_match_dialog_list;
    }

    @Override
    protected void onBindItem(AdapterMatchDialogListBinding binding, int position, Record record) {

        binding.setBean(record);
        CompetitorBean competitor = CompetitorParser.getCompetitorFrom(record);

        String winner;
        binding.tvPlayer.setText(competitor.getNameChn());
        binding.tvLine1.setText("(" + record.getRankCpt() + "/" + record.getSeedpCpt() + ")  "
                + competitor.getCountry());

        // round
        for (int i = 0; i < AppConstants.RECORD_MATCH_ROUNDS.length; i ++) {
            if (record.getRound().equals(AppConstants.RECORD_MATCH_ROUNDS[i])) {
                binding.tvRound.setText(AppConstants.RECORD_MATCH_ROUNDS_SHORT[i]);
                break;
            }
        }

        // winner and score
        if (record.getWinnerFlag() == AppConstants.WINNER_COMPETITOR) {
            winner = competitor.getNameChn();
            if (competitor instanceof User) {
                winner = ((User) competitor).getNameShort();
            }
        }
        else {
            winner = record.getUser().getNameShort();
        }
        binding.tvScore.setText(winner + " " + ScoreParser.getScoreText(record.getScoreList(), record.getWinnerFlag(), record.getRetireFlag()));

        if (mFocusItem != null && list.get(position).getId() == mFocusItem.getId()) {
            binding.groupItem.setBackgroundColor(Color.parseColor("#9900a5c4"));
        }
        else {
            binding.groupItem.setBackground(null);
        }
    }

    public void setFocusItem(Record focusItem) {
        this.mFocusItem = focusItem;
    }

}
