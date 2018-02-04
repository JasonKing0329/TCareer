package com.king.app.tcareer.model;

import com.king.app.tcareer.R;

/**
 * @desc
 * @auth 景阳
 * @time 2018/2/4 0004 23:08
 */

public class FlagProvider {

    public static int getFlagRes(String country) {
        if (country.equals("法国")) {
            return R.drawable.flag_france;
        }
        else if (country.equals("美国")) {
            return R.drawable.flag_usa;
        }
        else {
            return R.drawable.flag_china;
        }
    }
}
