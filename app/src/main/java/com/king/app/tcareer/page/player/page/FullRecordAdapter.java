package com.king.app.tcareer.page.player.page;

import android.view.View;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.mvvm.BaseBindingAdapter;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.databinding.AdapterPlayerPageRecordItemFullBinding;
import com.king.app.tcareer.model.CompetitorParser;
import com.king.app.tcareer.model.ScoreParser;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.model.db.entity.User;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/4/27 9:12
 */
public class FullRecordAdapter extends BaseBindingAdapter<AdapterPlayerPageRecordItemFullBinding, Object> {

    private User user;

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_player_page_record_item_full;
    }

    @Override
    protected void onBindItem(AdapterPlayerPageRecordItemFullBinding binding, int position, Object data) {
        FullRecordBean bean = (FullRecordBean) data;
        binding.setBean(bean);
        Record record = bean.record;
        if (bean.isYearFirst) {
            binding.tvYear.setVisibility(View.VISIBLE);
            binding.tvYear.setText(bean.year + "\n" + bean.yearWin + "-" + bean.yearLose);
        }
        else {
            binding.tvYear.setVisibility(View.GONE);
        }

        binding.tvName.setText(record.getMatch().getName());
        binding.tvRound.setText(record.getRound());
        int month = Integer.parseInt(record.getDateStr().split("-")[1]);
        binding.tvMonth.setText(month + "月");
        String winner;
        if (record.getWinnerFlag() == AppConstants.WINNER_USER) {
            winner = user.getNameShort();
        }
        else {
            winner = "● " + CompetitorParser.getCompetitorFrom(record).getNameChn();
        }
        binding.tvScore.setText(winner + "  " + ScoreParser.getScoreText(record.getScoreList()
                , record.getWinnerFlag(), record.getRetireFlag()));
    }

    public int getYear(int position) {
        FullRecordBean bean = (FullRecordBean) getItem(position);
        return Integer.parseInt(bean.record.getDateStr().split("-")[0]);
    }

}
