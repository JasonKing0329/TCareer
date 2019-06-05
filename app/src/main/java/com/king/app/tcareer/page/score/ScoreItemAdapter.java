package com.king.app.tcareer.page.score;

import android.view.View;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.mvvm.HeadChildBindingAdapter;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.databinding.AdapterScoreItemBinding;
import com.king.app.tcareer.databinding.AdapterScoreTitleBinding;
import com.king.app.tcareer.view.widget.scoreboard.ScoreBoard;

/**
 *
 * <p/>作者：景阳
 * <p/>创建时间: 2017/2/23 15:22
 */
public class ScoreItemAdapter extends HeadChildBindingAdapter<AdapterScoreTitleBinding, AdapterScoreItemBinding, ScoreBean, ScoreBean> {

    @Override
    public int getItemViewType(int position) {
        return ((ScoreBean) list.get(position)).isTitle() ? TYPE_HEAD:TYPE_ITEM;
    }

    @Override
    protected int getHeaderRes() {
        return R.layout.adapter_score_title;
    }

    @Override
    protected int getItemRes() {
        return R.layout.adapter_score_item;
    }

    @Override
    protected Class getItemClass() {
        return ScoreBoard.class;
    }

    @Override
    protected void onBindHead(AdapterScoreTitleBinding binding, int position, ScoreBean bean) {
        binding.tvTitle.setText(bean.getTitle());
    }

    @Override
    protected void onBindItem(AdapterScoreItemBinding binding, int position, ScoreBean bean) {
        if (bean.getMatchBean() == null) {// 500 赛罚分
            binding.ivWinner.setVisibility(View.INVISIBLE);
            binding.tvName.setText("500赛罚分");
            binding.tvName.setTextColor(binding.tvName.getResources().getColor(R.color.text_normal));
            binding.tvScore.setText("0");
            binding.tvComplete.setVisibility(View.INVISIBLE);
        }
        else {
            String court = bean.getMatchBean().getMatchBean().getCourt();
            if (AppConstants.RECORD_MATCH_COURTS[1].equals(court)) {
                binding.tvName.setTextColor(binding.tvName.getResources().getColor(R.color.normal_court_clay));
            }
            else if (AppConstants.RECORD_MATCH_COURTS[2].equals(court)) {
                binding.tvName.setTextColor(binding.tvName.getResources().getColor(R.color.normal_court_grass));
            }
            else if (AppConstants.RECORD_MATCH_COURTS[3].equals(court)) {
                binding.tvName.setTextColor(binding.tvName.getResources().getColor(R.color.normal_court_inhard));
            }
            else {
                binding.tvName.setTextColor(binding.tvName.getResources().getColor(R.color.text_normal));
            }
            binding.ivWinner.setVisibility(bean.isChampion() ? View.VISIBLE:View.INVISIBLE);
            binding.tvName.setText(bean.getMatchBean().getName());
            binding.tvScore.setText(String.valueOf(bean.getScore()));
            binding.tvComplete.setVisibility(bean.isCompleted() ? View.VISIBLE:View.INVISIBLE);
        }
    }

    @Override
    protected void onClickItem(View view, int position, ScoreBean data) {
        if (data.getMatchBean() == null) {// 500赛罚分
            return;
        }
        else {
            super.onClickItem(view, position, data);
        }
    }
}
