package com.king.app.tcareer.page.home;

import com.king.app.tcareer.R;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceAlignmentEnum;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/4/24 15:26
 */
public class BoomMenuHome {

    public static final int HARD = 0;
    public static final int CLAY = 1;
    public static final int GRASS = 2;
    public static final int INHARD = 3;

    private BoomMenuButton bmbMenu;

    public BoomMenuHome(BoomMenuButton bmbMenu) {
        this.bmbMenu = bmbMenu;
    }

    public void init(int seasonType, OnBMClickListener listener) {
//        int colors[] = getSeasonTypeColor(seasonType);
//        bmbMenu.setNormalColor(colors[0]);
        // init boom menu
        switch (seasonType) {
            case HARD:
                bmbMenu.setNormalColor(bmbMenu.getContext().getResources().getColor(R.color.swipecard_text_hard));
                break;
            case CLAY:
                bmbMenu.setNormalColor(bmbMenu.getContext().getResources().getColor(R.color.swipecard_text_clay));
                break;
            case GRASS:
                bmbMenu.setNormalColor(bmbMenu.getContext().getResources().getColor(R.color.swipecard_text_grass));
                break;
            case INHARD:
                bmbMenu.setNormalColor(bmbMenu.getContext().getResources().getColor(R.color.swipecard_text_innerhard));
                break;
        }
        int radius = bmbMenu.getContext().getResources().getDimensionPixelSize(R.dimen.boom_menu_btn_radius);
        bmbMenu.setButtonEnum(ButtonEnum.TextInsideCircle);
        bmbMenu.setButtonPlaceAlignmentEnum(ButtonPlaceAlignmentEnum.BR);
        bmbMenu.setButtonRightMargin(bmbMenu.getContext().getResources().getDimensionPixelSize(R.dimen.home_pop_menu_right));
        bmbMenu.setButtonBottomMargin(bmbMenu.getContext().getResources().getDimensionPixelSize(R.dimen.home_pop_menu_bottom));
        bmbMenu.setButtonVerticalMargin(bmbMenu.getContext().getResources().getDimensionPixelSize(R.dimen.boom_menu_btn_margin_ver));
        bmbMenu.setPiecePlaceEnum(PiecePlaceEnum.DOT_4_1);
        bmbMenu.setButtonPlaceEnum(ButtonPlaceEnum.Vertical);
        bmbMenu.addBuilder(new TextInsideCircleButton.Builder()
                .listener(listener)
                .buttonRadius(radius)
//                .normalColor(colors[0])
                .normalText(bmbMenu.getContext().getString(R.string.menu_save))
                .normalImageRes(R.drawable.ic_save_white_24dp)
        );
        bmbMenu.addBuilder(new TextInsideCircleButton.Builder()
                .listener(listener)
                .buttonRadius(radius)
//                .normalColor(colors[1])
                .normalText(bmbMenu.getContext().getString(R.string.menu_saveas))
                .normalImageRes(R.drawable.ic_inbox_white_24dp)
        );
        bmbMenu.addBuilder(new TextInsideCircleButton.Builder()
                .listener(listener)
                .buttonRadius(radius)
//                .normalColor(colors[2])
                .normalText(bmbMenu.getContext().getString(R.string.exit))
                .normalImageRes(R.drawable.ic_exit_to_app_white_24dp)
        );
        bmbMenu.addBuilder(new TextInsideCircleButton.Builder()
                .listener(listener)
                .buttonRadius(radius)
//                .normalColor(colors[3])
                .normalText(bmbMenu.getContext().getString(R.string.home_go_top))
                .normalImageRes(R.drawable.ic_arrow_upward_white_24dp)
        );

    }

}
