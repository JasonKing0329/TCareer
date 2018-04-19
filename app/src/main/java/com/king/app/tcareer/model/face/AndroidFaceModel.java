package com.king.app.tcareer.model.face;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.media.FaceDetector;

import com.king.app.tcareer.utils.DebugLog;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * 描述: android原生FaceDetector
 * <p/>作者：景阳
 * <p/>创建时间: 2018/4/19 15:46
 */
public class AndroidFaceModel implements FaceModel {
    @Override
    public Observable<FaceData> createFaceData(final Bitmap source) {
        return Observable.create(new ObservableOnSubscribe<FaceData>() {
            @Override
            public void subscribe(ObservableEmitter<FaceData> e) throws Exception {
                long start = System.currentTimeMillis();
                AndroidFaceData data = new AndroidFaceData();
                Bitmap bitmap = getFaceBitmap(source);
                FaceDetector detector = new FaceDetector(bitmap.getWidth(), bitmap.getHeight(), 1);
                FaceDetector.Face[] faces = new FaceDetector.Face[1];
                int num = detector.findFaces(bitmap, faces);
                DebugLog.e("findFaces " + num);
                if (faces.length > 0 && faces[0] != null) {
                    FaceDetector.Face face = faces[0];
                    data.centerPoint = new PointF();
                    face.getMidPoint(data.centerPoint);
                    DebugLog.e("x=" + data.centerPoint.x + ", y=" + data.centerPoint.y);
                }
                bitmap.recycle();

                long end = System.currentTimeMillis();
                DebugLog.e("cost time " + (end - start));
                e.onNext(data);
            }
        });
    }

    @Override
    public Observable<FaceData> createFaceData(String filePath) {
        return null;
    }

    /**
     * Android官方提供的人脸识别api在android.media.FaceDetector，核心的api是
     * FaceDetector.findFaces()
     * 主要的坑有两个：
     * （1）只能处理RGB_565格式的Bitmap，需要做转换。
     * （2）只能处理width为偶数的Bitmap，这里的解法是如果是奇数，则放弃最右侧的一列像素。
     * @param sourceBitmap
     * @return
     */
    public Bitmap getFaceBitmap(Bitmap sourceBitmap) {
        // default algorithm of face detecting of Android can only handle
        // RGB_565 bitmap, so copy it by RGB_565 here.
        Bitmap cacheBitmap = sourceBitmap.copy(Bitmap.Config.RGB_565, false);

        DebugLog.e(
                "genFaceBitmap() : source bitmap width - "
                        + cacheBitmap.getWidth() + " , height - "
                        + cacheBitmap.getHeight());

        int cacheWidth = cacheBitmap.getWidth();
        int cacheHeight = cacheBitmap.getHeight();

        // default algorithm of face detecting of Android can only handle the
        // bitmap that width is even, so we give up the 1 pixel from right if
        // not even.
        if (cacheWidth % 2 != 0) {
            if (0 == cacheWidth - 1) {
                return null;
            }
            final Bitmap localCacheBitmap = Bitmap.createBitmap(cacheBitmap, 0,
                    0, cacheWidth - 1, cacheHeight);
            cacheBitmap.recycle();
            cacheBitmap = localCacheBitmap;
            --cacheWidth;

            DebugLog.e(
                    "genFaceBitmap() : source bitmap width - "
                            + cacheBitmap.getWidth() + " , height - "
                            + cacheBitmap.getHeight());
        }
        return cacheBitmap;
    }

}
