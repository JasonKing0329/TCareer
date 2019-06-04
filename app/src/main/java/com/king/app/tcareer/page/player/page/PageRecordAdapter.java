package com.king.app.tcareer.page.player.page;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.mvvm.HeadChildBindingAdapter;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.databinding.AdapterPlayerPageRecordItemBinding;
import com.king.app.tcareer.databinding.AdapterPlayerPageRecordTitleBinding;
import com.king.app.tcareer.model.CompetitorParser;
import com.king.app.tcareer.model.ScoreParser;
import com.king.app.tcareer.model.bean.CompetitorBean;
import com.king.app.tcareer.model.db.entity.MatchBean;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.model.db.entity.User;

/**
 * 描述: title + items
 * <p/>作者：景阳
 * <p/>创建时间: 2017/11/21 9:34
 */
public class PageRecordAdapter extends HeadChildBindingAdapter<AdapterPlayerPageRecordTitleBinding, AdapterPlayerPageRecordItemBinding
        , PageTitleBean, Record> {

    private User user;

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    protected int getHeaderRes() {
        return R.layout.adapter_player_page_record_title;
    }

    @Override
    protected int getItemRes() {
        return R.layout.adapter_player_page_record_item;
    }

    @Override
    protected Class getItemClass() {
        return Record.class;
    }

    @Override
    protected void onBindHead(AdapterPlayerPageRecordTitleBinding binding, int position, PageTitleBean pageTitleBean) {
        binding.tvTitle.setText(pageTitleBean.getYear() + " （" + pageTitleBean.getWin() + "胜" + pageTitleBean.getLose() + "负）");
    }

    @Override
    protected void onBindItem(AdapterPlayerPageRecordItemBinding binding, int position, Record record) {
        binding.setBean(record);
        MatchBean matchBean = record.getMatch().getMatchBean();
        CompetitorBean competitor = CompetitorParser.getCompetitorFrom(record);
        binding.tvLine1.setText(matchBean.getLevel() + "  " + record.getDateStr().split("-")[1] + "月  " + record.getRound());
        binding.tvLine2.setText(record.getMatch().getName() + "  " + matchBean.getCourt());
        if (record.getWinnerFlag() == AppConstants.WINNER_USER) {
            binding.tvLine3.setText(user.getNameShort() + "  def.  " + record.getRankCpt() + "/" + record.getSeedpCpt());
            binding.tvLine3.setTextColor(binding.tvLine3.getResources().getColor(R.color.record_item_text_gray));
        }
        else {
            String winner = competitor.getNameChn();
            if (competitor instanceof User) {
                winner = ((User) competitor).getNameShort();
            }
            binding.tvLine3.setText(winner + " " + record.getRankCpt() + "/" + record.getSeedpCpt() + "  def.");
            binding.tvLine3.setTextColor(binding.tvLine3.getResources().getColor(R.color.red));
        }
        binding.tvLine4.setText(ScoreParser.getScoreText(record.getScoreList(), record.getWinnerFlag(), record.getRetireFlag()));
    }

}
