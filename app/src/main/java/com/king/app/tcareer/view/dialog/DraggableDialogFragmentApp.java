package com.king.app.tcareer.view.dialog;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.databinding.ViewDataBinding;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.BaseFragmentApp;
import com.king.app.tcareer.base.IFragmentHolder;
import com.king.app.tcareer.base.mvvm.BaseViewModel;
import com.king.app.tcareer.base.mvvm.BindingDialogFragmentApp;
import com.king.app.tcareer.base.mvvm.MvvmFragmentApp;
import com.king.app.tcareer.databinding.DlgFtCustomBinding;
import com.king.app.tcareer.utils.DebugLog;
import com.king.app.tcareer.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述: 可拖拽移动的base dialog框架
 * <p/>作者：景阳
 * <p/>创建时间: 2017/7/20 11:45
 */
public abstract class DraggableDialogFragmentApp extends BindingDialogFragmentApp<DlgFtCustomBinding> {

    //用于记录当前加的所有的icon，更换颜色的时候需要使用
    private List<ImageView> currentButtonList;

    private Point startPoint, touchPoint;

    @Override
    protected int getLayoutResource() {
        return R.layout.dlg_ft_custom;
    }

    @Override
    protected void initView(View view) {
        currentButtonList = new ArrayList<>();

        initDragParams();
        initOnclick();

        View toolbar = getToolbarView(mBinding.groupToolbar);
        if (toolbar != null) {
            mBinding.groupToolbar.addView(toolbar);
        }

        Fragment content = getContentViewFragment();
        replaceContentFragment( content, "ContentView");

        mBinding.groupFtContent.post(() -> {
            DebugLog.e("mBinding.groupFtContent height=" + mBinding.groupFtContent.getHeight());
            limitMaxHeihgt();
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
        int maxContentHeight = getMaxHeight();
        if (mBinding.groupFtContent.getHeight() > maxContentHeight) {
            ViewGroup.LayoutParams params = mBinding.groupFtContent.getLayoutParams();
            params.height = maxContentHeight;
            mBinding.groupFtContent.setLayoutParams(params);
        }
    }

    /**
     * 子类可选择覆盖
     * @return
     */
    protected int getMaxHeight() {
        return ScreenUtils.getScreenHeight(getActivity()) * 3 / 5;
    }

    protected abstract View getToolbarView(ViewGroup groupToolbar);

    protected abstract Fragment getContentViewFragment();

    private void initDragParams() {
        touchPoint = new Point();
        startPoint = new Point();
        mBinding.groupDialog.setOnTouchListener(new DialogTouchListener());
    }

    public void setTitle(String text) {
        mBinding.tvTitle.setText(text);
    }

    public void setTitle(int resId) {
        mBinding.tvTitle.setText(getResources().getString(resId));
    }

    public void setTitleColor(int color) {
        mBinding.tvTitle.setTextColor(color);
    }

    public void setDividerColor(int color) {
        mBinding.vDivider.setBackgroundColor(color);
    }

    public void setBackgroundColor(int color) {
        GradientDrawable drawable = (GradientDrawable) mBinding.groupDialog.getBackground();
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
        GradientDrawable drawable = (GradientDrawable) mBinding.groupTitle.getBackground();
        drawable.setColor(color);
    }

    public void updateTitleBorderColor(int color) {
        GradientDrawable drawable = (GradientDrawable) mBinding.groupTitle.getBackground();
        drawable.setStroke(getResources()
                        .getDimensionPixelSize(R.dimen.custom_dialog_icon_frame_width)
                , color);
    }

    public void requestOkAction() {
        mBinding.ivOk.setVisibility(View.VISIBLE);
        currentButtonList.add(mBinding.ivOk);
    }

    public void requestOkAction(int srcRes) {
        requestOkAction();
        mBinding.ivOk.setImageResource(srcRes);
    }

    public void requestCloseAction() {
        mBinding.ivClose.setVisibility(View.VISIBLE);
        currentButtonList.add(mBinding.ivClose);
    }

    public void requestPlayAction() {
        mBinding.ivPlay.setVisibility(View.VISIBLE);
        currentButtonList.add(mBinding.ivPlay);
    }

    public void requestSaveAction() {
        mBinding.ivSave.setVisibility(View.VISIBLE);
        currentButtonList.add(mBinding.ivSave);
    }

    public void requestSearchAction() {
        mBinding.ivSearch.setVisibility(View.VISIBLE);
        currentButtonList.add(mBinding.ivSearch);
    }

    public void registTextChangeListener(TextWatcher textWatcher) {
        mBinding.etSearch.addTextChangedListener(textWatcher);
    }

    private void initOnclick() {
        mBinding.ivSearch.setOnClickListener(v -> mBinding.groupSearch.setVisibility(View.VISIBLE));
        mBinding.ivSearchClose.setOnClickListener(v -> mBinding.groupSearch.setVisibility(View.GONE));
        mBinding.ivPlay.setOnClickListener(v -> onClickPlay());
        mBinding.ivOk.setOnClickListener(v -> {
            if (onClickOk()) {
                dismissAllowingStateLoss();
            }
        });
        mBinding.ivSave.setOnClickListener(v -> {
            onClickSave();
            dismissAllowingStateLoss();
        });
        mBinding.ivClose.setOnClickListener(v -> {
            onClickClose();
            dismissAllowingStateLoss();
        });
    }

    /**
     * 由子类选择实现
     */
    protected void onClickClose() {
    }

    /**
     * 由子类选择实现
     */
    protected boolean onClickOk() {
        return true;
    }

    /**
     * 由子类选择实现
     */
    protected void onClickPlay() {
    }

    /**
     * 由子类选择实现
     */
    protected void onClickSave() {
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

    public static abstract class ContentFragment extends BaseFragmentApp {

        @Override
        protected void bindFragmentHolder(IFragmentHolder holder) {
            // ContentFragment是嵌入到Fragment中的，而onAttach是把activity的context通知到bindFragmentHolder了
            // 而这里的ContentFragment的holder是DialogFragment，所以要把正确的holder以getParentFragment()的形式绑定
            if (getParentFragment() instanceof IFragmentHolder) {
                bindChildFragmentHolder((IFragmentHolder) getParentFragment());
            }
        }

        protected abstract void bindChildFragmentHolder(IFragmentHolder holder);

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            // 从LoginActivity启动的DownloadDialogFragment不知为何sub fragment没有第二次onAttach就执行onCreateView了，导致holder为null
            if (getParentFragment() instanceof IFragmentHolder) {
                bindChildFragmentHolder((IFragmentHolder) getParentFragment());
            }
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }

    public static abstract class BindingContentFragment<T extends ViewDataBinding, VM extends BaseViewModel> extends MvvmFragmentApp<T, VM> {

        @Override
        protected void bindFragmentHolder(IFragmentHolder holder) {
            // ContentFragment是嵌入到Fragment中的，而onAttach是把activity的context通知到bindFragmentHolder了
            // 而这里的ContentFragment的holder是DialogFragment，所以要把正确的holder以getParentFragment()的形式绑定
            if (getParentFragment() instanceof IFragmentHolder) {
                bindChildFragmentHolder((IFragmentHolder) getParentFragment());
            }
        }

        protected abstract void bindChildFragmentHolder(IFragmentHolder holder);

        @Override
        protected void onCreateData() {

        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            // 从LoginActivity启动的DownloadDialogFragment不知为何sub fragment没有第二次onAttach就执行onCreateView了，导致holder为null
            if (getParentFragment() instanceof IFragmentHolder) {
                bindChildFragmentHolder((IFragmentHolder) getParentFragment());
            }
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }

}
