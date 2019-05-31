package com.king.app.tcareer.view.dialog;

import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.BaseMvpFragment;
import com.king.app.tcareer.base.BasePresenter;
import com.king.app.tcareer.base.IFragmentHolder;
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
public abstract class DraggableDialogFragment extends BaseDialogFragment {

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

    @Override
    protected int getLayoutResource() {
        return R.layout.dlg_ft_custom;
    }

    @Override
    protected void initView(View view) {
        ButterKnife.bind(this, view);

        currentButtonList = new ArrayList<>();

        initDragParams();

        View toolbar = getToolbarView(groupToolbar);
        if (toolbar != null) {
            groupToolbar.addView(toolbar);
        }

        Fragment content = getContentViewFragment();
        replaceContentFragment( content, "ContentView");

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
        int maxContentHeight = getMaxHeight();
        if (groupFtContent.getHeight() > maxContentHeight) {
            ViewGroup.LayoutParams params = groupFtContent.getLayoutParams();
            params.height = maxContentHeight;
            groupFtContent.setLayoutParams(params);
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
        groupDialog.setOnTouchListener(new DialogTouchListener());
    }

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

    public void requestOkAction() {
        ivOk.setVisibility(View.VISIBLE);
        currentButtonList.add(ivOk);
    }

    public void requestOkAction(int srcRes) {
        requestOkAction();
        ivOk.setImageResource(srcRes);
    }

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
                onClickSave();
                dismiss();
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

    public static abstract class ContentFragment extends BaseMvpFragment<BasePresenter> {

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
        protected BasePresenter createPresenter() {
            return null;
        }

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

    public static abstract class ContentMvpFragment<T extends BasePresenter> extends BaseMvpFragment<T> {

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
}
