package com.king.app.tcareer.model.face;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/4/19 15:43
 */
public class FaceModelFactory {

    public static FaceModel create() {
        /**
         * ArcFaceModel采用虹软三方库
         * AndroidFaceModel采用原生FaceDetector
         * ArcFaceModel效率比AndroidFaceModel高，速度快5-10倍
         * AndroidFaceModel识别1张face，速度几乎都在500ms左右，ArcFaceModel则几乎在100ms之内
         * 鉴别率不好判断，因为贝鲁奇和费雷尔的各一张图两个model都无法识别出来（贝鲁奇的是侧脸，费雷尔的特征不明显）
         */
        return new ArcFaceModel();
    }
}
