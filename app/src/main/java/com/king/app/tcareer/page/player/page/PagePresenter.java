package com.king.app.tcareer.page.player.page;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TabLayout;
import android.support.v7.graphics.Palette;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.BasePresenter;
import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.ImageProvider;
import com.king.app.tcareer.model.bean.CompetitorBean;
import com.king.app.tcareer.model.db.entity.PlayerBeanDao;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.model.db.entity.UserDao;
import com.king.app.tcareer.model.face.FaceData;
import com.king.app.tcareer.model.face.FaceModel;
import com.king.app.tcareer.model.face.FaceModelFactory;
import com.king.app.tcareer.model.palette.PaletteResponse;
import com.king.app.tcareer.model.palette.ViewColorBound;
import com.king.app.tcareer.utils.ConstellationUtil;
import com.king.app.tcareer.utils.ListUtil;
import com.king.app.tcareer.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 描述: handle operations of player page activity and fragment
 * <p/>作者：景阳
 * <p/>创建时间: 2017/11/20 15:48
 */
public abstract class PagePresenter extends BasePresenter<IPageView> {

    protected CompetitorBean mCompetitor;

    private PageData mPageData;

    private FaceModel faceModel;

    @Override
    protected void onCreate() {
        faceModel = FaceModelFactory.create();
    }

    public void loadPlayerAndUser(final long playerId, final long userId, final boolean playerIsUser) {
        queryUser(userId)
                .flatMap(new Function<User, ObservableSource<CompetitorBean>>() {
                    @Override
                    public ObservableSource<CompetitorBean> apply(User user) throws Exception {
                        return queryCompetitor(playerId, playerIsUser);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(Object playerBean) {
                        loadPlayerInfor();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        view.showError("Load player failed: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    protected Observable<CompetitorBean> queryCompetitor(final long playerId, final boolean playerIsUser) {
        return Observable.create(new ObservableOnSubscribe<CompetitorBean>() {
            @Override
            public void subscribe(ObservableEmitter<CompetitorBean> e) throws Exception {
                if (playerIsUser) {
                    UserDao userDao = TApplication.getInstance().getDaoSession().getUserDao();
                    mCompetitor = userDao.queryBuilder()
                            .where(UserDao.Properties.Id.eq(playerId))
                            .build().unique();
                } else {
                    PlayerBeanDao playerBeanDao = TApplication.getInstance().getDaoSession().getPlayerBeanDao();
                    mCompetitor = playerBeanDao.queryBuilder()
                            .where(PlayerBeanDao.Properties.Id.eq(playerId))
                            .build().unique();
                }
                e.onNext(mCompetitor);
            }
        });
    }

    protected void loadPlayerInfor() {

        // 先全部隐藏，等到调色板相关参数都加载完再动画渐入
        view.getEngNameTextView().setVisibility(View.GONE);
        view.getChnNameTextView().setVisibility(View.GONE);
        view.getBirthdayTextView().setVisibility(View.GONE);
        view.getCountryTextView().setVisibility(View.GONE);

        view.showCompetitor(mCompetitor.getNameEng(), ImageProvider.getDetailPlayerPath(mCompetitor.getNameChn()));
    }

    public void loadTabs() {
        Observable.create(new ObservableOnSubscribe<List<TabBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<TabBean>> e) throws Exception {

                List<TabBean> list = createTabs();

                e.onNext(list);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<TabBean>>() {
                    @Override
                    public void accept(List<TabBean> list) throws Exception {
                        view.onTabLoaded(list);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        view.showError("loadRecords error: " + throwable.getMessage());
                    }
                });
    }

    protected abstract List<TabBean> createTabs();

    public void createRecords(final String tabId, final IPageCallback callback) {
        Observable.create(new ObservableOnSubscribe<List<Object>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Object>> e) throws Exception {
                List<Object> list = new ArrayList<>();
                Map<Integer, List<Record>> map = new HashMap<>();
                List<Record> recordList = createTabRecords(tabId);
                for (int i = 0; i < recordList.size(); i++) {
                    Record record = recordList.get(i);
                    if (filterRecord(record)) {
                        String strYear = record.getDateStr().split("-")[0];
                        int year = Integer.parseInt(strYear);
                        List<Record> child = map.get(year);
                        if (child == null) {
                            child = new ArrayList<>();
                            map.put(year, child);
                        }
                        child.add(record);
                    }
                }

                // 按year降序
                Iterator<Integer> it = map.keySet().iterator();
                List<Integer> yearList = new ArrayList<>();
                while (it.hasNext()) {
                    yearList.add(it.next());
                }
                Collections.sort(yearList);
                Collections.reverse(yearList);

                for (Integer year : yearList) {
                    List<Record> records = map.get(year);
                    PageTitleBean title = countTitle(year, records);
                    list.add(title);
                    list.addAll(records);
                }

                e.onNext(list);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<Object>>() {
                    @Override
                    public void accept(List<Object> list) throws Exception {
                        callback.onDataLoaded(list);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    protected abstract List<Record> createTabRecords(String tabId);

    protected abstract boolean filterRecord(Record record);

    protected PageTitleBean countTitle(int year, List<Record> records) {
        PageTitleBean bean = new PageTitleBean();
        int win = 0, lose = 0;
        for (int i = 0; i < records.size(); i++) {
            Record record = records.get(i);
            //如果是赛前退赛不算作h2h
            if (record.getRetireFlag() == AppConstants.RETIRE_WO) {
                continue;
            } else {
                if (record.getWinnerFlag() == AppConstants.WINNER_COMPETITOR) {
                    lose++;
                } else {
                    win++;
                }
            }
        }
        bean.setYear(year);
        bean.setWin(win);
        bean.setLose(lose);
        return bean;
    }

    /**
     * 根据背景图片为各个UI空间设置合适的颜色
     * @param response
     */
    public void handlePalette(final PaletteResponse response) {
        Observable.combineLatest(getColorPack(response, 4), getFaceData(response), new BiFunction<ColorPack, FaceData, PageData>() {
            @Override
            public PageData apply(ColorPack colorPack, FaceData faceData) throws Exception {
                PageData data = new PageData();
                data.faceData = faceData;
                data.pack = colorPack;
                // title bar适应的颜色
                data.mainSwatch = getTitlebarSwatch(response.palette);
                return data;
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<PageData>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(PageData data) {
                        mPageData = data;
                        dispatchData(data);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void dispatchData(PageData data) {

        // 修改titlebar的主色调（背景，文字颜色，图标颜色）
        view.getCollapsingToolbar().setContentScrimColor(data.mainSwatch.getRgb());
        view.getCollapsingToolbar().setCollapsedTitleTextColor(data.mainSwatch.getTitleTextColor());
        // toolbar上的图标需要根据展开/折叠状态确定颜色，初始是展开状态
        handleCollapseScrimChanged(false);

        // 修改tab layout的相关颜色
        view.getTabLayout().setBackgroundColor(data.mainSwatch.getRgb());
        view.getTabLayout().setSelectedTabIndicatorColor(data.mainSwatch.getTitleTextColor());
        // 使用了自定义布局，要单独设置
//        view.getTabLayout().setTabTextColors(data.mainSwatch.getBodyTextColor(), data.mainSwatch.getTitleTextColor());
        for (int i = 0; i < view.getTabLayout().getTabCount(); i ++) {
            TabLayout.Tab tab = view.getTabLayout().getTabAt(i);
            TabCustomView view = (TabCustomView) tab.getCustomView();
            view.setTextColor(data.mainSwatch.getBodyTextColor(), data.mainSwatch.getTitleTextColor());
        }
        view.getTabLayout().invalidate();

        // 修改信息标签的颜色（eng name, chn name, place, birthday）
        ColorPack colorPack = data.pack;
        TextView[] textViews;
        // 中文名和英文名相同就不显示中文名
        if (!TextUtils.isEmpty(mCompetitor.getNameChn()) && !mCompetitor.getNameChn().equals(mCompetitor.getNameEng())) {
            textViews = new TextView[] {
                    view.getEngNameTextView(), view.getChnNameTextView(), view.getCountryTextView(), view.getBirthdayTextView()
            };
            view.getChnNameTextView().setText(mCompetitor.getNameChn());
        }
        else {
            textViews = new TextView[] {
                    view.getEngNameTextView(), view.getCountryTextView(), view.getBirthdayTextView()
            };
        }
        for (int i = 0; i < textViews.length; i ++) {
            textViews[i].setBackgroundColor(colorPack.rgbs.get(i));
            textViews[i].setTextColor(colorPack.bodyColors.get(i));
            // country标签，更改drawable颜色
            if (textViews[i] == view.getCountryTextView()) {
                Drawable drawable = view.getContext().getResources().getDrawable(R.drawable.ic_edit_location_white_24dp);
                drawable.setBounds(0, 0, ScreenUtils.dp2px(20), ScreenUtils.dp2px(20));
                // 替换原图颜色
                drawable.setColorFilter(colorPack.bodyColors.get(i), PorterDuff.Mode.SRC_IN);
                textViews[i].setCompoundDrawables(drawable, null, null, null);
            }
        }

        // 修改信息标签的位置（根据人脸位置，统一显示在左侧或右侧）
        boolean isRight = true;
        int rule = RelativeLayout.ALIGN_PARENT_RIGHT;
        if (data.faceData != null && data.faceData.centerPoint != null) {
            // 人脸在右侧，则text在左侧
            if (data.faceData.centerPoint.x > ScreenUtils.getScreenWidth() / 2) {
                rule = RelativeLayout.ALIGN_PARENT_LEFT;
                isRight = false;
            }
        }
        LinearLayout layout = (LinearLayout) textViews[0].getParent();
        layout.setGravity(isRight ? Gravity.RIGHT:Gravity.LEFT);
        RelativeLayout.LayoutParams rParams = (RelativeLayout.LayoutParams) layout.getLayoutParams();
        rParams.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
        rParams.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        rParams.addRule(rule);
        layout.setLayoutParams(rParams);

        // 信息赋值并动画渐入
        String constel = null;
        try {
            constel = ConstellationUtil.getConstellationChn(mCompetitor.getBirthday());
        } catch (ConstellationUtil.ConstellationParseException e) {
            e.printStackTrace();
        }
        int age = 0;
        try {
            age = ConstellationUtil.getAge(mCompetitor.getBirthday());
        } catch (Exception e) {
            e.printStackTrace();
        }
        StringBuffer buffer = new StringBuffer();
        if (age != 0) {
            buffer.append(age).append(", ");
        }
        buffer.append(mCompetitor.getBirthday());
        if (!TextUtils.isEmpty(constel)) {
            buffer.append(", ").append(constel);
        }
        view.getEngNameTextView().setText(mCompetitor.getNameEng());
        view.getCountryTextView().setText(mCompetitor.getCountry());
        view.getBirthdayTextView().setText(buffer.toString());

        view.animTags(isRight);
    }

    /**
     * title bar运用的颜色
     * vibrant优先，其次muted，再其次任意
     * @param palette
     * @return
     */
    public Palette.Swatch getTitlebarSwatch(Palette palette) {
        if (palette == null) {
            return null;
        }
        Palette.Swatch swatch = palette.getVibrantSwatch();
        if (swatch == null) {
            swatch = palette.getMutedSwatch();
            if (swatch == null) {
                List<Palette.Swatch> swatches = palette.getSwatches();
                if (!ListUtil.isEmpty(swatches)) {
                    swatch = swatches.get(0);
                }
            }
        }
        return swatch;
    }

    /**
     * 处理appBar滑动过程scrim的出现和消失引起的图标颜色变化
     * @param isCollapsing
     */
    public void handleCollapseScrimChanged(boolean isCollapsing) {
        // 折叠状态运用main swatch的getTitleTextColor
        if (isCollapsing) {
            // PorterDuff.Mode作用参考https://www.jianshu.com/p/d11892bbe055
            // 这里用SRC_IN，第一个参数是source，表示source与原图像叠加后相交的部分运用source的颜色，如果是SRC_TOP则是叠加的情况，SRC则是source color充满整个view区域
            view.getToolbar().getNavigationIcon().setColorFilter(mPageData.mainSwatch.getTitleTextColor(), PorterDuff.Mode.SRC_IN);
        }
        // 展开状态运用图片区域颜色分析法得到的颜色
        else {
            ViewColorBound bound = findViewColorBound(mPageData.pack.colorBounds, view.getToolbar());
            if (bound != null) {
                view.getToolbar().getNavigationIcon().setColorFilter(bound.color, PorterDuff.Mode.SRC_IN);
            }
        }
    }

    private ViewColorBound findViewColorBound(List<ViewColorBound> colorBounds, View view) {
        if (!ListUtil.isEmpty(colorBounds)) {
            for (ViewColorBound bound:colorBounds) {
                if (bound.view == view) {
                    return bound;
                }
            }
        }
        return null;
    }

    public User getUser(String tabId) {
        return mUser;
    }

    private class PageData {
        Palette.Swatch mainSwatch;
        FaceData faceData;
        ColorPack pack;
    }

    private class ColorPack {
        List<Integer> rgbs = new ArrayList<>();
        List<Integer> bodyColors = new ArrayList<>();
        List<ViewColorBound> colorBounds;
    }

    /**
     * 检测人脸位置（中心点）
     * 原生FaceDetector，不是特别理想，侧面几乎不能识别出来
     * @param response
     * @return
     */
    private Observable<FaceData> getFaceData(final PaletteResponse response) {
        return faceModel.createFaceData(response.resource);
    }

    /**
     * 处理所需要使用的颜色
     * @param response
     * @param colorNumber
     * @return
     */
    private Observable<ColorPack> getColorPack(final PaletteResponse response, final int colorNumber) {
        return Observable.create(new ObservableOnSubscribe<ColorPack>() {
            @Override
            public void subscribe(ObservableEmitter<ColorPack> e) throws Exception {
                ColorPack pack = new ColorPack();
                pack.colorBounds = response.viewColorBounds;
                int nTags = colorNumber;
                if (response != null && response.palette != null && !ListUtil.isEmpty(response.palette.getSwatches())) {
                    List<Palette.Swatch> swatches = response.palette.getSwatches();
                    for (int i = 0; i < nTags; i++) {
                        if (i < swatches.size()) {
                            pack.rgbs.add(swatches.get(i).getRgb());
                            pack.bodyColors.add(swatches.get(i).getBodyTextColor());
                        } else {
                            pack.rgbs.add(swatches.get(i % swatches.size()).getRgb());
                            pack.bodyColors.add(swatches.get(i % swatches.size()).getBodyTextColor());
                        }
                    }
                } else {
                    for (int i = 0; i < nTags; i++) {
                        pack.rgbs.add(view.getContext().getResources().getColor(R.color.colorPrimary));
                        pack.bodyColors.add(view.getContext().getResources().getColor(R.color.white));
                    }
                }
                e.onNext(pack);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (faceModel != null) {
            faceModel.destroy();
        }
    }
}
