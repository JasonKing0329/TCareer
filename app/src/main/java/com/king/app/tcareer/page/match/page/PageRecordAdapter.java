package com.king.app.tcareer.page.match.page;

import android.graphics.drawable.GradientDrawable;
import android.view.View;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.mvvm.HeadChildBindingAdapter;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.databinding.AdapterMatchPageGroupTitleBinding;
import com.king.app.tcareer.databinding.AdapterMatchPageRecordItemBinding;
import com.king.app.tcareer.model.CompetitorParser;
import com.king.app.tcareer.model.ScoreParser;
import com.king.app.tcareer.model.bean.CompetitorBean;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.model.db.entity.User;

/**
 * 描述: title + items
 * <p/>作者：景阳
 * <p/>创建时间: 2017/11/21 9:34
 */
public class PageRecordAdapter extends HeadChildBindingAdapter<AdapterMatchPageGroupTitleBinding, AdapterMatchPageRecordItemBinding, PageTitleBean, Record> {

    @Override
    protected int getHeaderRes() {
        return R.layout.adapter_match_page_group_title;
    }

    @Override
    protected int getItemRes() {
        return R.layout.adapter_match_page_record_item;
    }

    @Override
    protected Class getItemClass() {
        return Record.class;
    }

    @Override
    protected void onBindHead(AdapterMatchPageGroupTitleBinding binding, int position, PageTitleBean head) {
        binding.tvTitle.setText(String.valueOf(head.getYear()));
        binding.ivCup.setVisibility(head.isWinner() ? View.VISIBLE:View.GONE);
    }

    @Override
    protected void onBindItem(AdapterMatchPageRecordItemBinding binding, int position, Record record) {
        binding.setBean(record);
        CompetitorBean competitor = CompetitorParser.getCompetitorFrom(record);
        binding.tvLevel.setText(AppConstants.getMasterGloryForRound(record.getRound()));
        binding.tvLine2.setText(competitor.getNameChn() + " " + record.getRankCpt() + "/" + record.getSeedpCpt());
        GradientDrawable drawable = (GradientDrawable) binding.tvLevel.getBackground();
        String winner = record.getUser().getNameShort();
        if (record.getWinnerFlag() == AppConstants.WINNER_USER) {
            if (record.getRound().equals(AppConstants.RECORD_MATCH_ROUNDS[0])) {
                drawable.setColor(binding.tvLevel.getResources().getColor(R.color.colorAccent));
            }
            else {
                drawable.setColor(binding.tvLevel.getResources().getColor(R.color.match_timeline));
            }
        }
        else {
            winner = competitor.getNameChn();
            if (competitor instanceof User) {
                winner = ((User) competitor).getNameShort();
            }
            drawable.setColor(binding.tvLevel.getResources().getColor(R.color.match_timeline));
        }
        binding.tvLevel.setBackground(drawable);
        binding.tvLine3.setText(winner + " " + ScoreParser.getScoreText(record.getScoreList(), record.getWinnerFlag(), record.getRetireFlag()));
    }
}
