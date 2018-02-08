package com.king.app.tcareer.view.dialog;

import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.king.app.tcareer.R;
import com.king.app.tcareer.utils.DebugLog;
import com.king.app.tcareer.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 描述: 可拖拽移动的base dialog框架
 * <p/>作者：景阳
 * <p/>创建时间: 2017/7/20 11:45
 */
public class CommonDialog<T extends CommonContentFragment> extends BaseDialogFragment implements CommonHolder {

    @BindView(R.id.group_dialog)
    ViewGroup groupDialog;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.group_title)
    ViewGroup groupTitle;
    @BindView(R.id.iv_search)
    ImageView ivSearch;
    @BindView(R.id.iv_play)
    ImageView ivPlay;
    @BindView(R.id.iv_ok)
    ImageView ivOk;
    @BindView(R.id.iv_save)
    ImageView ivSave;
    @BindView(R.id.iv_close)
    ImageView ivClose;
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.iv_search_close)
    ImageView ivSearchClose;
    @BindView(R.id.group_search)
    FrameLayout groupSearch;
    @BindView(R.id.v_divider)
    View vDivider;
    @BindView(R.id.tv_null_content)
    TextView tvNullContent;
    @BindView(R.id.group_toolbar)
    ViewGroup groupToolbar;
    @BindView(R.id.group_ft_content)
    ViewGroup groupFtContent;


    //用于记录当前加的所有的icon，更换颜色的时候需要使用
    private List<ImageView> currentButtonList;

    private Point startPoint, touchPoint;

    private T ftContent;

    @Override
    protected int getLayoutResource() {
        return R.layout.dlg_ft_custom;
    }

    public void setContentFragment(T fragment) {
        ftContent = fragment;
    }

    @Override
    protected void initView(View view) {
        ButterKnife.bind(this, view);

        currentButtonList = new ArrayList<>();

        initDragParams();

        replaceContentFragment(ftContent, "ContentView");

        groupFtContent.post(new Runnable() {
            @Override
            public void run() {
                DebugLog.e("groupFtContent height=" + groupFtContent.getHeight());
                limitMaxHeihgt();
            }
        });
    }

    protected void replaceContentFragment(Fragment target, String tag) {
        if (target != null) {
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.replace(R.id.group_ft_content, target, tag);
            ft.commit();
        }
    }

    private void limitMaxHeihgt() {
        int maxContentHeight = ftContent.getMaxHeight();
        if (groupFtContent.getHeight() > maxContentHeight) {
            ViewGroup.LayoutParams params = groupFtContent.getLayoutParams();
            params.height = maxContentHeight;
            groupFtContent.setLayoutParams(params);
        }

        int minHeight = ftContent.getMinHeight();
        // 不设置最小高度
        if (minHeight == -1) {
            return;
        }
        else {
            if (groupFtContent.getHeight() < minHeight) {
                ViewGroup.LayoutParams params = groupFtContent.getLayoutParams();
                params.height = minHeight;
                groupFtContent.setLayoutParams(params);
            }
        }
    }

    private void initDragParams() {
        touchPoint = new Point();
        startPoint = new Point();
        groupDialog.setOnTouchListener(new DialogTouchListener());
    }

    @Override
    public void setTitle(String text) {
        tvTitle.setText(text);
    }

    public void setTitle(int resId) {
        tvTitle.setText(getResources().getString(resId));
    }

    public void setTitleColor(int color) {
        tvTitle.setTextColor(color);
    }

    public void setDividerColor(int color) {
        vDivider.setBackgroundColor(color);
    }

    public void setBackgroundColor(int color) {
        GradientDrawable drawable = (GradientDrawable) groupDialog.getBackground();
        drawable.setColor(color);
    }

    public void updateToobarIconBk(int color) {
        for (ImageView view : currentButtonList) {
            StateListDrawable stateDrawable = (StateListDrawable) view.getBackground();
            GradientDrawable drawable = (GradientDrawable) stateDrawable.getCurrent();
            drawable.setColor(color);
        }
    }

    public void updateTitleBk(int color) {
        GradientDrawable drawable = (GradientDrawable) groupTitle.getBackground();
        drawable.setColor(color);
    }

    public void updateTitleBorderColor(int color) {
        GradientDrawable drawable = (GradientDrawable) groupTitle.getBackground();
        drawable.setStroke(getContext().getResources()
                        .getDimensionPixelSize(R.dimen.custom_dialog_icon_frame_width)
                , color);
    }

    @Override
    public void requestOkAction() {
        ivOk.setVisibility(View.VISIBLE);
        currentButtonList.add(ivOk);
    }

    @Override
    public void requestOkAction(int srcRes) {
        requestOkAction();
        ivOk.setImageResource(srcRes);
    }

    @Override
    public void requestCloseAction() {
        ivClose.setVisibility(View.VISIBLE);
        currentButtonList.add(ivClose);
    }

    public void requestPlayAction() {
        ivPlay.setVisibility(View.VISIBLE);
        currentButtonList.add(ivPlay);
    }

    public void requestSaveAction() {
        ivSave.setVisibility(View.VISIBLE);
        currentButtonList.add(ivSave);
    }

    public void requestSearchAction() {
        ivSearch.setVisibility(View.VISIBLE);
        currentButtonList.add(ivSearch);
    }

    public void registTextChangeListener(TextWatcher textWatcher) {
        etSearch.addTextChangedListener(textWatcher);
    }

    @OnClick({R.id.iv_search, R.id.iv_play, R.id.iv_ok, R.id.iv_save, R.id.iv_close, R.id.iv_search_close})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_search:
                groupSearch.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_search_close:
                groupSearch.setVisibility(View.GONE);
                break;
            case R.id.iv_play:
                onClickPlay();
                break;
            case R.id.iv_ok:
                if (onClickOk()) {
                    dismiss();
                }
                break;
            case R.id.iv_save:
                if (onClickSave()) {
                    dismiss();
                }
                break;
            case R.id.iv_close:
                onClickClose();
                dismiss();
                break;
        }
    }

    /**
     * 由子类选择实现
     */
    protected void onClickClose() {
        ftContent.onClose();
    }

    /**
     * 由子类选择实现
     */
    protected boolean onClickOk() {
        return ftContent.onSave();
    }

    /**
     * 由子类选择实现
     */
    protected void onClickPlay() {
    }

    /**
     * 由子类选择实现
     */
    protected boolean onClickSave() {
        return ftContent.onSave();
    }

    private class Point {
        float x;
        float y;
    }

    /**
     * move dialog
     */
    private class DialogTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    float x = event.getRawX();//
                    float y = event.getRawY();
                    startPoint.x = x;
                    startPoint.y = y;
                    DebugLog.d("ACTION_DOWN x=" + x + ", y=" + y);
                    break;
                case MotionEvent.ACTION_MOVE:
                    x = event.getRawX();
                    y = event.getRawY();
                    touchPoint.x = x;
                    touchPoint.y = y;
                    float dx = touchPoint.x - startPoint.x;
                    float dy = touchPoint.y - startPoint.y;

                    move((int) dx, (int) dy);

                    startPoint.x = x;
                    startPoint.y = y;
                    break;
                case MotionEvent.ACTION_UP:
                    break;

                default:
                    break;
            }
            return true;
        }
    }
}
