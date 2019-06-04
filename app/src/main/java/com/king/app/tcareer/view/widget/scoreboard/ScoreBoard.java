package com.king.app.tcareer.view.widget.scoreboard;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.king.app.tcareer.R;
import com.king.app.tcareer.databinding.LayoutScoreboardBinding;
import com.king.app.tcareer.model.db.entity.Score;
import com.king.app.tcareer.utils.ListUtil;
import com.king.app.tcareer.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

public class ScoreBoard extends FrameLayout {

    private LayoutScoreboardBinding binding;

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
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.layout_scoreboard, this, false);
        addView(binding.getRoot());
    }

    public void setParam(ScoreBoardParam param) {
        this.param = param;
        binding.setBean(param);
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
        binding.tvName.setTextColor(boardStyle.getMatchNameColor());
        binding.tvRound.setTextColor(boardStyle.getMatchRoundColor());
    }

    private void addScores() {
        scoreViews1 = new ArrayList<>();
        scoreViews2 = new ArrayList<>();
        for (int i = 0; i < 5; i ++) {
            scoreViews1.add(addScoreView(binding.llScore1));
            scoreViews2.add(addScoreView(binding.llScore2));
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
        binding.llScore1.removeAllViews();
        binding.llScore2.removeAllViews();
        addScores();
        if (param.getWinnerIndex() == 0) {
            TextPaint paint1 = binding.tvPlayer1.getPaint();
            paint1.setFakeBoldText(true);
            TextPaint paint2 = binding.tvPlayer2.getPaint();
            paint2.setFakeBoldText(false);
            binding.tvPlayer1.setBackgroundColor(boardStyle.getFocusBgColor());
            binding.tvPlayer1.setTextColor(boardStyle.getFocusTextColor());
            binding.tvPlayer2.setBackgroundColor(boardStyle.getNormalBgColor());
            binding.tvPlayer2.setTextColor(boardStyle.getNormalTextColor());
            binding.tvWo1.setBackgroundColor(boardStyle.getFocusBgColor());
            binding.tvWo2.setBackgroundColor(boardStyle.getNormalBgColor());
        }
        else {
            TextPaint paint1 = binding.tvPlayer2.getPaint();
            paint1.setFakeBoldText(true);
            TextPaint paint2 = binding.tvPlayer1.getPaint();
            paint2.setFakeBoldText(false);
            binding.tvPlayer2.setBackgroundColor(boardStyle.getFocusBgColor());
            binding.tvPlayer2.setTextColor(boardStyle.getFocusTextColor());
            binding.tvPlayer1.setBackgroundColor(boardStyle.getNormalBgColor());
            binding.tvPlayer1.setTextColor(boardStyle.getNormalTextColor());
            binding.tvWo2.setBackgroundColor(boardStyle.getFocusBgColor());
            binding.tvWo1.setBackgroundColor(boardStyle.getNormalBgColor());
        }
        for (int i = 0; i < 5; i ++) {
            setSetScore(i);
        }
        // 没有比分代表有W/0
        if (ListUtil.isEmpty(param.getScoreList())) {
            if (param.getWinnerIndex() == 0) {
                binding.tvWo1.setVisibility(GONE);
                binding.tvWo2.setVisibility(VISIBLE);
            }
            else {
                binding.tvWo1.setVisibility(VISIBLE);
                binding.tvWo2.setVisibility(GONE);
            }
        }
        else {
            binding.tvWo1.setVisibility(GONE);
            binding.tvWo2.setVisibility(GONE);
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
