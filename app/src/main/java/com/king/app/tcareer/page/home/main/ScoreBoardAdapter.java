package com.king.app.tcareer.page.home.main;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.king.app.tcareer.R;
import com.king.app.tcareer.view.adapter.BaseRecyclerAdapter;
import com.king.app.tcareer.view.widget.scoreboard.ScoreBoard;
import com.king.app.tcareer.view.widget.scoreboard.ScoreBoardParam;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ScoreBoardAdapter extends BaseRecyclerAdapter<ScoreBoardAdapter.ScoreHolder, ScoreBoardParam> {

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_score_board;
    }

    @Override
    protected ScoreHolder newViewHolder(View view) {
        return new ScoreHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScoreHolder scoreHolder, int position) {
        scoreHolder.scoreBoard.setParam(list.get(position));
    }

    public static class ScoreHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.score_board)
        ScoreBoard scoreBoard;

        public ScoreHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
