package com.king.app.tcareer.view.widget.scoreboard;

import android.graphics.Color;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/5/31 10:11
 */
public class BoardStyleProvider {

    public BoardStyle getDefault() {
        return new DefaultStyle();
    }

    public BoardStyle getFrenchOpen() {
        return new FrenchOpen();
    }

    public BoardStyle getWimbledonOpen() {
        return new WimbledonOpen();
    }

    public BoardStyle getAustriliaOpen() {
        return new AustraliaOpen();
    }

    /**
     * 澳网
     */
    private class AustraliaOpen implements BoardStyle {

        @Override
        public int getNormalBgColor() {
            return Color.WHITE;
        }

        @Override
        public int getFocusBgColor() {
            return Color.parseColor("#007cb5");
        }

        @Override
        public int getNormalTextColor() {
            return Color.parseColor("#9b9b9b");
        }

        @Override
        public int getFocusTextColor() {
            return Color.WHITE;
        }

        @Override
        public int getMatchNameColor() {
            return Color.parseColor("#007cb5");
        }

        @Override
        public int getMatchRoundColor() {
            return Color.parseColor("#007cb5");
        }
    }

    /**
     * 法网
     */
    private class FrenchOpen implements BoardStyle {

        @Override
        public int getNormalBgColor() {
            return Color.WHITE;
        }

        @Override
        public int getFocusBgColor() {
            return Color.parseColor("#e6946c");
        }

        @Override
        public int getNormalTextColor() {
            return Color.parseColor("#9b9b9b");
        }

        @Override
        public int getFocusTextColor() {
            return Color.WHITE;
        }

        @Override
        public int getMatchNameColor() {
            return Color.parseColor("#e6946c");
        }

        @Override
        public int getMatchRoundColor() {
            return Color.parseColor("#e6946c");
        }
    }

    /**
     * 温网
     */
    private class WimbledonOpen implements BoardStyle {

        @Override
        public int getNormalBgColor() {
            return Color.WHITE;
        }

        @Override
        public int getFocusBgColor() {
            return Color.parseColor("#00703c");
        }

        @Override
        public int getNormalTextColor() {
            return Color.parseColor("#5400a1");
        }

        @Override
        public int getFocusTextColor() {
            return Color.WHITE;
        }

        @Override
        public int getMatchNameColor() {
            return Color.parseColor("#00703c");
        }

        @Override
        public int getMatchRoundColor() {
            return Color.parseColor("#00703c");
        }
    }

    /**
     * default
     */
    private class DefaultStyle implements BoardStyle {

        @Override
        public int getNormalBgColor() {
            return Color.WHITE;
        }

        @Override
        public int getFocusBgColor() {
            return Color.parseColor("#64907f");
        }

        @Override
        public int getNormalTextColor() {
            return Color.parseColor("#333333");
        }

        @Override
        public int getFocusTextColor() {
            return Color.WHITE;
        }

        @Override
        public int getMatchNameColor() {
            return Color.parseColor("#333333");
        }

        @Override
        public int getMatchRoundColor() {
            return Color.parseColor("#333333");
        }
    }
}
