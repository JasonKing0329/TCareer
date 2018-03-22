package com.king.app.tcareer.model;

import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.db.entity.Score;

import java.util.List;

/**
 * 描述: parse score
 * <p/>作者：景阳
 * <p/>创建时间: 2018/2/1 11:12
 */
public class ScoreParser {

    /**
     * 解析比分，形式为 6-3/6-7(5)/3-2(对手退赛)...，user比分在前
     * @param scoreList
     * @param retireFlag
     * @return
     */
    public static String getScoreText(List<Score> scoreList, int retireFlag) {
        return getScoreText(scoreList, AppConstants.WINNER_USER, retireFlag);
    }

    /**
     * 解析比分，形式为 6-3/6-7(5)/3-2(对手退赛)...，winnerFlag决定谁的比分在前
     * @param scoreList
     * @param winnerFlag
     * @param retireFlag
     * @return
     */
    public static String getScoreText(List<Score> scoreList, int winnerFlag, int retireFlag) {
        StringBuffer buffer = new StringBuffer();
        if (retireFlag == AppConstants.RETIRE_WO) {
            buffer.append(AppConstants.SCORE_RETIRE);
            return buffer.toString();
        }
        else {
            for (int i = 0; i < scoreList.size(); i ++) {
                Score score = scoreList.get(i);
                buffer.append("/");
                if (winnerFlag == AppConstants.WINNER_USER) {
                    buffer.append(score.getUserPoint()).append("-").append(score.getCompetitorPoint());
                }
                else {
                    buffer.append(score.getCompetitorPoint()).append("-").append(score.getUserPoint());
                }
                if (score.getIsTiebreak()) {
                    buffer.append("(").append(score.getUserTiebreak() > 0 ? score.getUserTiebreak() : score.getCompetitorTiebreak()).append(")");
                }
            }
            if (retireFlag == AppConstants.RETIRE_WITH_SCORE) {
                buffer.append(AppConstants.SCORE_RETIRE_NORMAL);
            }

            String text = buffer.toString();
            if (text.length() > 1) {
                text = text.substring(1);
            }
            return text;
        }
    }
}
