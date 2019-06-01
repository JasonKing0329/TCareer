package com.king.app.tcareer.page.home.main;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.mvvm.BaseBindingAdapter;
import com.king.app.tcareer.databinding.AdapterScoreBoardBinding;
import com.king.app.tcareer.view.widget.scoreboard.ScoreBoardParam;

public class ScoreBoardAdapter extends BaseBindingAdapter<AdapterScoreBoardBinding, ScoreBoardParam> {

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_score_board;
    }

    @Override
    protected void onBindItem(AdapterScoreBoardBinding binding, int position, ScoreBoardParam bean) {
        binding.scoreBoard.setParam(list.get(position));
    }

}
