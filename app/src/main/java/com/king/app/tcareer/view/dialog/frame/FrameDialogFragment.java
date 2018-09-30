package com.king.app.tcareer.view.dialog.frame;

import android.content.DialogInterface;
import android.support.v4.app.FragmentTransaction;
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
import com.king.app.tcareer.view.dialog.BaseDialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/9/30 9:19
 */
public class FrameDialogFragment extends BaseDialogFragment implements FrameHolder {

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

    private Point startPoint, touchPoint;

    private String title;

    private int backgroundColor;

    private boolean hideClose;

    private FrameContentFragment contentFragment;

    private int maxHeight;

    protected DialogInterface.OnDismissListener onDismissListener;

    @Override
    protected int getLayoutResource() {
        return R.layout.dlg_ft_custom;
    }

    @Override
    protected void initView(View view) {
        ButterKnife.bind(this, view);
        if (title != null) {
            tvTitle.setText(title);
        }
        if (backgroundColor != 0) {
            groupDialog.setBackgroundColor(backgroundColor);
        }
        if (hideClose) {
            ivClose.setVisibility(View.GONE);
        }
        else {
            ivClose.setVisibility(View.VISIBLE);
        }

        initDragParams();

        if (contentFragment != null) {
            contentFragment.setDialogHolder(this);
            replaceContentFragment(contentFragment, "ContentView");
        }

        groupFtContent.post(() -> {
            DebugLog.e("groupFtContent height=" + groupFtContent.getHeight());
            limitMaxHeight();
        });

        ivClose.setOnClickListener(v -> dismissAllowingStateLoss());
    }

    protected void replaceContentFragment(FrameContentFragment target, String tag) {
        if (target != null) {
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.replace(R.id.group_ft_content, target, tag);
            ft.commit();
        }
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setHideClose(boolean hideClose) {
        this.hideClose = hideClose;
    }

    public void setContentFragment(FrameContentFragment contentFragment) {
        this.contentFragment = contentFragment;
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialog);
        }
    }

    private void limitMaxHeight() {
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
        if (maxHeight != 0) {
            return maxHeight;
        }
        else {
            return ScreenUtils.getScreenHeight() * 3 / 5;
        }
    }

    private void initDragParams() {
        touchPoint = new FrameDialogFragment.Point();
        startPoint = new FrameDialogFragment.Point();
        groupDialog.setOnTouchListener(new FrameDialogFragment.DialogTouchListener());
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
