package com.king.app.tcareer.model.face;

import android.graphics.Bitmap;

import io.reactivex.Observable;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/4/19 15:42
 */
public interface FaceModel {
    Observable<FaceData> createFaceData(Bitmap bitmap);
    Observable<FaceData> createFaceData(String filePath);
}
