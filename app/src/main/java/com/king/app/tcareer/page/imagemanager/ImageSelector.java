package com.king.app.tcareer.page.imagemanager;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.IFragmentHolder;
import com.king.app.tcareer.model.http.bean.ImageUrlBean;
import com.king.app.tcareer.view.dialog.DraggableDialogFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @desc
 * @auth 景阳
 * @time 2018/2/2 0002 23:56
 */

public abstract class ImageSelector extends DraggableDialogFragment implements ISelectorHolder {

    protected ImageUrlBean imageUrlBean;

    protected OnSelectorListener onSelectorListener;

    private String title;

    public void setImageUrlBean(ImageUrlBean imageUrlBean) {
        this.imageUrlBean = imageUrlBean;
    }

    public void setDialogTitle(String title) {
        this.title = title;
    }

    @Override
    protected View getToolbarView(ViewGroup groupToolbar) {
        setTitle(title);
        return customToolbar();
    }

    protected abstract View customToolbar();

    @Override
    protected Fragment getContentViewFragment() {
        return new SelectorFragment();
    }

    public <T> void setOnSelectorListener(OnSelectorListener<T> onSelectorListener) {
        this.onSelectorListener = onSelectorListener;
    }

    public static class SelectorFragment extends ContentFragment {

        @BindView(R.id.rv_selector)
        RecyclerView rvSelector;
        Unbinder unbinder;
        
        private ISelectorHolder holder;

        @Override
        protected int getContentLayoutRes() {
            return R.layout.dialog_image_selector;
        }

        @Override
        protected void onCreate(View view) {
            unbinder = ButterKnife.bind(this, view);

            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            rvSelector.setLayoutManager(layoutManager);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, getContext().getResources().getDimensionPixelSize(R.dimen.dlg_loadfrom_list_height)
            );
            view.setLayoutParams(params);

            rvSelector.setAdapter(holder.initAdapter());
        }

        @Override
        protected void bindChildFragmentHolder(IFragmentHolder holder) {
            this.holder = (ISelectorHolder) holder;
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            unbinder.unbind();
        }
    }

    public interface OnSelectorListener<T> {
        void onSelectDone(List<T> list);
    }
}
