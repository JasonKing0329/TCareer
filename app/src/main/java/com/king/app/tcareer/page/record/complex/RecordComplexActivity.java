package com.king.app.tcareer.page.record.complex;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.king.app.tcareer.R;
import com.king.app.tcareer.base.BaseMvpActivity;
import com.king.app.tcareer.model.GlideOptions;
import com.king.app.tcareer.model.ImageProvider;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.page.record.list.OnItemMenuListener;
import com.king.app.tcareer.page.record.list.RecordItem;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceAlignmentEnum;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.SimpleCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;

import java.util.List;

import butterknife.BindView;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/2 14:23
 */
public class RecordComplexActivity extends BaseMvpActivity<ComplexPresenter> implements ComplexView
        , OnBMClickListener, OnItemMenuListener {

    private final int REQUEST_UPDATE = 100;

    @BindView(R.id.iv_record_head)
    ImageView ivRecordHead;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.ctl_toolbar)
    CollapsingToolbarLayout ctlToolbar;
    @BindView(R.id.rv_record)
    RecyclerView rvRecord;
    @BindView(R.id.bmb_menu)
    BoomMenuButton bmbMenu;

    private RecordAdapter recordAdapter;

    /**
     * the item view position to update
     */
    private int mUpdatePosition;

    @Override
    protected int getContentView() {
        return R.layout.activity_record_complex;
    }

    @Override
    protected void initView() {
        initToolbar();
        initBoomButton();
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rvRecord.setLayoutManager(manager);
    }

    @Override
    protected ComplexPresenter createPresenter() {
        return new ComplexPresenter();
    }

    @Override
    protected void initData() {
        presenter.loadRecords();
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 加入了转场动画，必须用onBackPressed，finish无效果
                onBackPressed();
            }
        });
        toolbar.setTitle("全部记录");
        ctlToolbar.setTitle("全部记录");
    }

    private void initBoomButton() {
        // 修改了源码，image自适应为button的一半中间，不需要再设置imagePadding了
//        int padding = bmbMenu.getContext().getResources().getDimensionPixelSize(R.dimen.boom_menu_icon_padding);
        int radius = bmbMenu.getContext().getResources().getDimensionPixelSize(R.dimen.boom_menu_btn_radius);
        bmbMenu.setButtonEnum(ButtonEnum.SimpleCircle);
        bmbMenu.setButtonRadius(radius);
        bmbMenu.setPiecePlaceEnum(PiecePlaceEnum.DOT_3_1);
        bmbMenu.setButtonPlaceEnum(ButtonPlaceEnum.Vertical);
        bmbMenu.setButtonPlaceAlignmentEnum(ButtonPlaceAlignmentEnum.BR);
        bmbMenu.setButtonRightMargin(bmbMenu.getContext().getResources().getDimensionPixelSize(R.dimen.home_pop_menu_right));
        bmbMenu.setButtonBottomMargin(bmbMenu.getContext().getResources().getDimensionPixelSize(R.dimen.home_pop_menu_bottom));
        bmbMenu.setButtonVerticalMargin(bmbMenu.getContext().getResources().getDimensionPixelSize(R.dimen.boom_menu_btn_margin_ver));
        bmbMenu.addBuilder(new SimpleCircleButton.Builder()
                .normalImageRes(R.drawable.ic_search_white_24dp)
                .buttonRadius(radius)
//                .imagePadding(new Rect(padding, padding, padding, padding))
                .listener(this));
        bmbMenu.addBuilder(new SimpleCircleButton.Builder()
                .normalImageRes(R.drawable.ic_refresh_white_24dp)
                .buttonRadius(radius)
                .listener(this));
        bmbMenu.addBuilder(new SimpleCircleButton.Builder()
                .normalImageRes(R.drawable.ic_arrow_upward_white_24dp)
                .buttonRadius(radius)
                .listener(this));
    }

    @Override
    public void postShowHeadRecord(final Record record) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                String path = ImageProvider.getMatchHeadPath(record.getMatch().getName(), record.getMatch().getMatchBean().getCourt());

                Glide.with(RecordComplexActivity.this)
                        .load(path)
                        .apply(GlideOptions.getEditorMatchOptions())
                        .into(ivRecordHead);
            }
        });
    }

    @Override
    public void showItems(List<YearItem> yearItems) {
        // year默认展开
        for (YearItem item:yearItems) {
            item.setExpanded(true);
        }

        if (recordAdapter == null) {
            recordAdapter = new RecordAdapter(yearItems, this);
            rvRecord.setAdapter(recordAdapter);
        }
        else {
            recordAdapter.updateData(yearItems);
        }
        // expand the first item, it's 1, not 0
        recordAdapter.expandParent(1);
    }

    @Override
    public void onBoomButtonClick(int index) {
        switch (index) {
            case 0:// search
                break;
            case 1:// refresh
                presenter.loadRecords();
                break;
            case 2:// go top
                rvRecord.scrollToPosition(0);
                break;
        }
    }

    @Override
    public void onUpdateRecord(RecordItem record) {

    }

    @Override
    public void onDeleteRecord(RecordItem record) {

    }

    @Override
    public void onAllDetail(RecordItem record) {

    }

    @Override
    public void onListDetail(RecordItem record) {

    }

    @Override
    public void onItemClicked(View view, RecordItem record) {

    }
}
