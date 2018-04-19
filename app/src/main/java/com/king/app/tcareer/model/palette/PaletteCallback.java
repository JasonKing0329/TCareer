package com.king.app.tcareer.model.palette;

import android.view.View;

import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/4/12 16:14
 */
public interface PaletteCallback {

    List<View> getTargetViews();

    void noPaletteResponseLoaded(int position);

    void onPaletteResponse(int position, PaletteResponse response);
}
