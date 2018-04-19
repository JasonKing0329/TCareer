package com.king.app.tcareer.model.palette;

import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/4/12 16:14
 */
public interface PaletteCallback {

    List<ViewColorBound> getTargetViews();

    void noPaletteResponseLoaded(int position);

    void onPaletteResponse(int position, PaletteResponse response);
}
