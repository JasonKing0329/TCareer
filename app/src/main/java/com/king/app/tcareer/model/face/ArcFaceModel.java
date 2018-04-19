package com.king.app.tcareer.model.face;

import android.graphics.Bitmap;
import android.graphics.PointF;

import com.arcsoft.facedetection.AFD_FSDKEngine;
import com.arcsoft.facedetection.AFD_FSDKError;
import com.arcsoft.facedetection.AFD_FSDKFace;
import com.arcsoft.facedetection.AFD_FSDKVersion;
import com.guo.android_extend.image.ImageConverter;
import com.king.app.tcareer.utils.DebugLog;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * 描述: 采用第三方库，虹软ArcFace sdk
 * <p/>作者：景阳
 * <p/>创建时间: 2018/4/19 15:51
 */
public class ArcFaceModel implements FaceModel {

    private AFD_FSDKEngine engine;

    public ArcFaceModel() {
        engine = new AFD_FSDKEngine();
    }

    @Override
    public Observable<FaceData> createFaceData(final Bitmap resource) {
        return Observable.create(new ObservableOnSubscribe<FaceData>() {
            @Override
            public void subscribe(ObservableEmitter<FaceData> e) throws Exception {
                long start = System.currentTimeMillis();
                byte[] bitdata = new byte[resource.getWidth() * resource.getHeight() * 3 / 2];

                ImageConverter convert = new ImageConverter();
                convert.initial(resource.getWidth(), resource.getHeight(), ImageConverter.CP_PAF_NV21);
                boolean convertResult = convert.convert(resource, bitdata);
                DebugLog.e("convert " + convertResult);
                convert.destroy();
                AFD_FSDKVersion version = new AFD_FSDKVersion();
                List<AFD_FSDKFace> result = new ArrayList<>();
                AFD_FSDKError err = engine.AFD_FSDK_InitialFaceEngine(FaceDB.appid, FaceDB.fd_key, AFD_FSDKEngine.AFD_OPF_0_HIGHER_EXT, 16, 1);
                DebugLog.e("AFD_FSDK_InitialFaceEngine = " + err.getCode());
                err = engine.AFD_FSDK_GetVersion(version);
                DebugLog.e("AFD_FSDK_GetVersion =" + version.toString() + ", " + err.getCode());
                err  = engine.AFD_FSDK_StillImageFaceDetection(bitdata, resource.getWidth(), resource.getHeight(), AFD_FSDKEngine.CP_PAF_NV21, result);
                DebugLog.e("AFD_FSDK_StillImageFaceDetection =" + err.getCode() + "<" + result.size());

                ArcFaceData data = new ArcFaceData();
                if (result.size() > 0) {
                    AFD_FSDKFace face = result.get(0);
                    data.rect = face.getRect();
                    data.centerPoint = new PointF(face.getRect().left + (face.getRect().right - face.getRect().left) / 2
                            , face.getRect().top + (face.getRect().bottom - face.getRect().top) / 2);
                    DebugLog.e("x=" + data.centerPoint.x + ", y=" + data.centerPoint.y);
                }


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

    @Override
    public void destroy() {
        if (engine != null) {
            AFD_FSDKError err = engine.AFD_FSDK_UninitialFaceEngine();
            DebugLog.e("AFD_FSDK_UninitialFaceEngine =" + err.getCode());
        }
    }
}
