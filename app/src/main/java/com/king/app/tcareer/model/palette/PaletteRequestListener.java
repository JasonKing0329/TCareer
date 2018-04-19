package com.king.app.tcareer.model.palette;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.view.View;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.king.app.tcareer.utils.ColorUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 描述: 由于banner使用了ViewPager的机制，ViewPager会缓存3个页面，即3张图会被提前加载
 * 所以页面相关颜色的变化需要在onPageSelected监听
 * 这种情况下只需要缓存所需要的颜色值就可以了
 * 所以用这个类来返回页面对应的各个color集合
 * <p/>作者：景阳
 * <p/>创建时间: 2018/4/12 16:08
 */
public class PaletteRequestListener implements RequestListener<Bitmap> {

    private PaletteCallback callback;
    private int position;

    public PaletteRequestListener(int position, PaletteCallback callback) {
        this.callback = callback;
        this.position = position;
    }

    @Override
    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
        callback.noPaletteResponseLoaded(position);
        return false;
    }

    @Override
    public boolean onResourceReady(final Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
        Observable.combineLatest(createPalette(resource), createViewColorBound(resource), new BiFunction<Palette, List<ViewColorBound>, PaletteResponse>() {
            @Override
            public PaletteResponse apply(Palette palette, List<ViewColorBound> viewColorBounds) throws Exception {
                PaletteResponse response = new PaletteResponse();
                response.resource = resource;
                response.palette = palette;
                response.viewColorBounds = viewColorBounds;
                return response;
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<PaletteResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(PaletteResponse response) {
                        callback.onPaletteResponse(position, response);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        callback.noPaletteResponseLoaded(position);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        return false;
    }

    private Observable<Palette> createPalette(final Bitmap resource) {
        return Observable.create(new ObservableOnSubscribe<Palette>() {
            @Override
            public void subscribe(final ObservableEmitter<Palette> e) throws Exception {
                Palette.from(resource)
                        .generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(@NonNull Palette palette) {
                                e.onNext(palette);
                            }
                        });
            }
        });
    }

    private Observable<List<ViewColorBound>> createViewColorBound(final Bitmap resource) {
        List<View> list = callback.getTargetViews();
        if (list == null) {
            list = new ArrayList<>();
        }
        return Observable.fromIterable(list)
                        .map(new Function<View, ViewColorBound>() {
                            @Override
                            public ViewColorBound apply(View view) throws Exception {
                                int color = ColorUtils.averageImageColor(resource, view);
                                ViewColorBound bound = new ViewColorBound();
                                bound.view = view;
                                bound.color = ColorUtils.generateForgroundColorForBg(color);
                                return bound;
                            }
                        })
                        .toList()
                        .toObservable();
    }
}
