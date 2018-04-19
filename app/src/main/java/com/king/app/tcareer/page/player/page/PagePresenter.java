package com.king.app.tcareer.page.player.page;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.media.FaceDetector;
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
import com.king.app.tcareer.model.db.entity.MatchBean;
import com.king.app.tcareer.model.db.entity.PlayerBeanDao;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.model.db.entity.RecordDao;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.model.db.entity.UserDao;
import com.king.app.tcareer.model.palette.PaletteResponse;
import com.king.app.tcareer.utils.ConstellationUtil;
import com.king.app.tcareer.utils.DebugLog;
import com.king.app.tcareer.utils.ListUtil;
import com.king.app.tcareer.utils.ScreenUtils;

import org.greenrobot.greendao.query.WhereCondition;

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
public class PagePresenter extends BasePresenter<IPageView> {

    private final String TAB_ALL = "全部";

    private CompetitorBean mCompetitor;

    private List<Record> recordList;

    @Override
    protected void onCreate() {

    }

    public void loadPlayerAndUser(final long playerId, final long userId, final boolean playerIsUser) {
        queryUser(userId)
                .flatMap(new Function<User, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(User user) throws Exception {
                        return new ObservableSource<Object>() {
                            @Override
                            public void subscribe(Observer<? super Object> observer) {
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
                                observer.onNext(new Object());
                            }
                        };
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

    private void loadPlayerInfor() {

        // 先全部隐藏，等到调色板相关参数都加载完再动画渐入
        view.getEngNameTextView().setVisibility(View.GONE);
        view.getChnNameTextView().setVisibility(View.GONE);
        view.getBirthdayTextView().setVisibility(View.GONE);
        view.getCountryTextView().setVisibility(View.GONE);

        view.showCompetitor(mCompetitor.getNameEng(), ImageProvider.getDetailPlayerPath(mCompetitor.getNameChn()));
    }

    public void loadRecords() {
        Observable.create(new ObservableOnSubscribe<List<TabBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<TabBean>> e) throws Exception {

                RecordDao dao = TApplication.getInstance().getDaoSession().getRecordDao();
                WhereCondition competitorCond[] = new WhereCondition[2];
                if (mCompetitor instanceof User) {
                    competitorCond[0] = RecordDao.Properties.PlayerId.eq(mCompetitor.getId());
                    competitorCond[1] = RecordDao.Properties.PlayerFlag.eq(AppConstants.COMPETITOR_VIRTUAL);
                } else {
                    competitorCond[0] = RecordDao.Properties.PlayerId.eq(mCompetitor.getId());
                    competitorCond[1] = RecordDao.Properties.PlayerFlag.eq(AppConstants.COMPETITOR_NORMAL);
                }
                recordList = dao.queryBuilder()
                        .where(RecordDao.Properties.UserId.eq(mUser.getId())
                                , competitorCond)
                        .build().list();

                // 查出来的是时间升序，按时间降序排列
                Collections.reverse(recordList);

                List<TabBean> tabList = new ArrayList<>();

                for (int i = 0; i < AppConstants.RECORD_MATCH_COURTS.length; i++) {
                    TabBean tab = new TabBean();
                    tab.name = AppConstants.RECORD_MATCH_COURTS[i];
                    tabList.add(tab);
                }

                for (int i = 0; i < recordList.size(); i++) {
                    Record record = recordList.get(i);
                    MatchBean matchBean = record.getMatch().getMatchBean();
                    // count h2h by court
                    if (matchBean.getCourt().equals(AppConstants.RECORD_MATCH_COURTS[0])) {
                        tabList.get(0).total++;
                        //如果是赛前退赛不算作h2h
                        if (record.getRetireFlag() == AppConstants.RETIRE_WO) {
                            continue;
                        } else {
                            if (record.getWinnerFlag() == AppConstants.WINNER_COMPETITOR) {
                                tabList.get(0).lose++;
                            } else {
                                tabList.get(0).win++;
                            }
                        }
                    } else if (matchBean.getCourt().equals(AppConstants.RECORD_MATCH_COURTS[1])) {
                        tabList.get(1).total++;
                        //如果是赛前退赛不算作h2h
                        if (record.getRetireFlag() == AppConstants.RETIRE_WO) {
                            continue;
                        } else {
                            if (record.getWinnerFlag() == AppConstants.WINNER_COMPETITOR) {
                                tabList.get(1).lose++;
                            } else {
                                tabList.get(1).win++;
                            }
                        }
                    } else if (matchBean.getCourt().equals(AppConstants.RECORD_MATCH_COURTS[3])) {
                        tabList.get(3).total++;
                        //如果是赛前退赛不算作h2h
                        if (record.getRetireFlag() == AppConstants.RETIRE_WO) {
                            continue;
                        } else {
                            if (record.getWinnerFlag() == AppConstants.WINNER_COMPETITOR) {
                                tabList.get(3).lose++;
                            } else {
                                tabList.get(3).win++;
                            }
                        }
                    } else if (matchBean.getCourt().equals(AppConstants.RECORD_MATCH_COURTS[2])) {
                        tabList.get(2).total++;
                        //如果是赛前退赛不算作h2h
                        if (record.getRetireFlag() == AppConstants.RETIRE_WO) {
                            continue;
                        } else {
                            if (record.getWinnerFlag() == AppConstants.WINNER_COMPETITOR) {
                                tabList.get(2).lose++;
                            } else {
                                tabList.get(2).win++;
                            }
                        }
                    }
                }

                TabBean tabAll = new TabBean();
                tabAll.name = TAB_ALL;
                // 如果没有记录就不显示这个tab
                for (int i = tabList.size() - 1; i >= 0; i--) {
                    tabAll.win += tabList.get(i).win;
                    tabAll.lose += tabList.get(i).lose;
                    tabAll.total += tabList.get(i).total;
                    if (tabList.get(i).total == 0) {
                        tabList.remove(i);
                    }
                }
                tabList.add(0, tabAll);

                e.onNext(tabList);
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

    public void createRecords(final String tabName, final IPageCallback callback) {
        Observable.create(new ObservableOnSubscribe<List<Object>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Object>> e) throws Exception {
                List<Object> list = new ArrayList<>();
                Map<Integer, List<Record>> map = new HashMap<>();
                for (int i = 0; i < recordList.size(); i++) {
                    Record record = recordList.get(i);
                    if (tabName.equals(TAB_ALL) || tabName.equals(record.getMatch().getMatchBean().getCourt())) {
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

    private PageTitleBean countTitle(int year, List<Record> records) {
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
        // PorterDuff.Mode作用参考https://www.jianshu.com/p/d11892bbe055
        // 这里用SRC_IN，第一个参数是source，表示source与原图像叠加后相交的部分运用source的颜色，如果是SRC_TOP则是叠加的情况，SRC则是source color充满整个view区域
        view.getToolbar().getNavigationIcon().setColorFilter(data.mainSwatch.getTitleTextColor(), PorterDuff.Mode.SRC_IN);

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
        }

        // 修改信息标签的位置（根据人脸位置，统一显示在左侧或右侧）
        boolean isRight = true;
        int rule = RelativeLayout.ALIGN_PARENT_RIGHT;
        if (data.faceData != null && data.faceData.point != null) {
            // 人脸在右侧，则text在左侧
            if (data.faceData.point.x > ScreenUtils.getScreenWidth() / 2) {
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

    private class PageData {
        Palette.Swatch mainSwatch;
        FaceData faceData;
        ColorPack pack;
    }

    private class FaceData {
        PointF point;
    }

    private class ColorPack {
        List<Integer> rgbs = new ArrayList<>();
        List<Integer> bodyColors = new ArrayList<>();
    }

    /**
     * 检测人脸位置（中心点）
     * 原生FaceDetector，不是特别理想
     * @param response
     * @return
     */
    private Observable<FaceData> getFaceData(final PaletteResponse response) {
        return Observable.create(new ObservableOnSubscribe<FaceData>() {
            @Override
            public void subscribe(ObservableEmitter<FaceData> e) throws Exception {
                FaceData data = new FaceData();
                Bitmap bitmap = getFaceBitmap(response.resource);
                FaceDetector detector = new FaceDetector(bitmap.getWidth(), bitmap.getHeight(), 1);
                FaceDetector.Face[] faces = new FaceDetector.Face[1];
                int num = detector.findFaces(bitmap, faces);
                DebugLog.e("findFaces " + num);
                if (faces.length > 0 && faces[0] != null) {
                    FaceDetector.Face face = faces[0];
                    data.point = new PointF();
                    face.getMidPoint(data.point);
                    DebugLog.e("x=" + data.point.x + ", y=" + data.point.y);
                }
                bitmap.recycle();
                e.onNext(data);
            }
        });
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
}
