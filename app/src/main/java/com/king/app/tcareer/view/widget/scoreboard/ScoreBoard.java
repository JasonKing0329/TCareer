package com.king.app.tcareer.view.widget.scoreboard;

import android.content.Context;
import android.graphics.Color;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.king.app.tcareer.R;
import com.king.app.tcareer.model.GlideOptions;
import com.king.app.tcareer.model.db.entity.Score;
import com.king.app.tcareer.utils.ListUtil;
import com.king.app.tcareer.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

public class ScoreBoard extends FrameLayout {

    private TextView tvName;
    private TextView tvRound;
    private TextView tvPlayer1;
    private TextView tvPlayer2;
    private ImageView ivPlayer1;
    private ImageView ivPlayer2;
    private LinearLayout llScore1;
    private LinearLayout llScore2;
    private TextView tvWO1;
    private TextView tvWO2;

    private ScoreBoardParam param;
    private BoardStyle boardStyle;

    private List<ScoreView> scoreViews1;
    private List<ScoreView> scoreViews2;

    public ScoreBoard(Context context) {
        super(context);
        init(null);
    }

    public ScoreBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ScoreBoard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_scoreboard, null);
        tvName = view.findViewById(R.id.tv_name);
        tvRound = view.findViewById(R.id.tv_round);
        ivPlayer1 = view.findViewById(R.id.iv_player1);
        ivPlayer2 = view.findViewById(R.id.iv_player2);
        tvPlayer1 = view.findViewById(R.id.tv_player1);
        tvPlayer2 = view.findViewById(R.id.tv_player2);
        llScore1 = view.findViewById(R.id.ll_score1);
        llScore2 = view.findViewById(R.id.ll_score2);
        tvWO1 = view.findViewById(R.id.tv_wo1);
        tvWO2 = view.findViewById(R.id.tv_wo2);
        addView(view);
    }

    public void setParam(ScoreBoardParam param) {
        this.param = param;
        setBoardStyle(param.getBoardStyle());
        initData();
    }

    /**
     * call this before after setParam
     * @param boardStyle
     */
    private void setBoardStyle(BoardStyle boardStyle) {
        this.boardStyle = boardStyle;
        if (boardStyle == null) {
            boardStyle = new BoardStyleProvider().getDefault();
        }
        tvName.setTextColor(boardStyle.getMatchNameColor());
        tvRound.setTextColor(boardStyle.getMatchRoundColor());
    }

    private void addScores() {
        scoreViews1 = new ArrayList<>();
        scoreViews2 = new ArrayList<>();
        for (int i = 0; i < 5; i ++) {
            scoreViews1.add(addScoreView(llScore1));
            scoreViews2.add(addScoreView(llScore2));
        }
    }

    private ScoreView addScoreView(LinearLayout group) {
        ScoreView scoreView = new ScoreView(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
        params.weight = 1;
        params.leftMargin = ScreenUtils.dp2px(1);
        scoreView.setLayoutParams(params);
        scoreView.setBgColor(boardStyle.getNormalBgColor());
        scoreView.setBgColorFocus(boardStyle.getFocusBgColor());
        scoreView.setTextColor(boardStyle.getNormalTextColor());
        scoreView.setTextColorFocus(boardStyle.getFocusTextColor());
        group.addView(scoreView);
        return scoreView;
    }

    private void initData() {
        llScore1.removeAllViews();
        llScore2.removeAllViews();
        addScores();
        tvName.setText(param.getMatchName());
        tvRound.setText(param.getRound());
        tvPlayer1.setText(param.getPlayer1());
        tvPlayer2.setText(param.getPlayer2());
        if (param.getWinnerIndex() == 0) {
            TextPaint paint1 = tvPlayer1.getPaint();
            paint1.setFakeBoldText(true);
            TextPaint paint2 = tvPlayer2.getPaint();
            paint2.setFakeBoldText(false);
            tvPlayer1.setBackgroundColor(boardStyle.getFocusBgColor());
            tvPlayer1.setTextColor(boardStyle.getFocusTextColor());
            tvPlayer2.setBackgroundColor(boardStyle.getNormalBgColor());
            tvPlayer2.setTextColor(boardStyle.getNormalTextColor());
            tvWO1.setBackgroundColor(boardStyle.getFocusBgColor());
            tvWO2.setBackgroundColor(boardStyle.getNormalBgColor());
        }
        else {
            TextPaint paint1 = tvPlayer2.getPaint();
            paint1.setFakeBoldText(true);
            TextPaint paint2 = tvPlayer1.getPaint();
            paint2.setFakeBoldText(false);
            tvPlayer2.setBackgroundColor(boardStyle.getFocusBgColor());
            tvPlayer2.setTextColor(boardStyle.getFocusTextColor());
            tvPlayer1.setBackgroundColor(boardStyle.getNormalBgColor());
            tvPlayer1.setTextColor(boardStyle.getNormalTextColor());
            tvWO2.setBackgroundColor(boardStyle.getFocusBgColor());
            tvWO1.setBackgroundColor(boardStyle.getNormalBgColor());
        }
        Glide.with(getContext())
                .load(param.getPlayerUrl1())
                .apply(GlideOptions.getDefaultPlayerOptions())
                .into(ivPlayer1);
        Glide.with(getContext())
                .load(param.getPlayerUrl2())
                .apply(GlideOptions.getDefaultPlayerOptions())
                .into(ivPlayer2);
        for (int i = 0; i < 5; i ++) {
            setSetScore(i);
        }
        // 没有比分代表有W/0
        if (ListUtil.isEmpty(param.getScoreList())) {
            if (param.getWinnerIndex() == 0) {
                tvWO1.setVisibility(GONE);
                tvWO2.setVisibility(VISIBLE);
            }
            else {
                tvWO1.setVisibility(VISIBLE);
                tvWO2.setVisibility(GONE);
            }
        }
        else {
            tvWO1.setVisibility(GONE);
            tvWO2.setVisibility(GONE);
        }
    }

    private void setSetScore(int index) {
        if (param.getWinnerIndex() == 0) {
            scoreViews1.get(index).setFocus(true);
            scoreViews2.get(index).setFocus(false);
        }
        else {
            scoreViews1.get(index).setFocus(false);
            scoreViews2.get(index).setFocus(true);
        }
        try {
            Score score = param.getScoreList().get(index);
            if (score.getUserPoint() > score.getCompetitorPoint()) {
                scoreViews1.get(index).setTextBold(true);
                scoreViews2.get(index).setTextBold(false);
            }
            else if (score.getUserPoint() < score.getCompetitorPoint()) {
                scoreViews1.get(index).setTextBold(false);
                scoreViews2.get(index).setTextBold(true);
            }
            else {
                scoreViews1.get(index).setTextBold(false);
                scoreViews2.get(index).setTextBold(false);
            }
            scoreViews1.get(index).setScore(score.getUserPoint());
            scoreViews2.get(index).setScore(score.getCompetitorPoint());
            if (score.getIsTiebreak()) {
                scoreViews1.get(index).setScoreSub(score.getUserTiebreak());
                scoreViews2.get(index).setScoreSub(score.getCompetitorTiebreak());
            }
        } catch (Exception e) {
            scoreViews1.get(index).setScore(null);
            scoreViews2.get(index).setScore(null);
            scoreViews1.get(index).setScoreSub(null);
            scoreViews2.get(index).setScoreSub(null);
        }
    }

}
