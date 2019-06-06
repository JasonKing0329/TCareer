package com.king.app.tcareer.page.imagemanager;

import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.IFragmentHolder;
import com.king.app.tcareer.base.mvvm.BaseViewModel;
import com.king.app.tcareer.databinding.DialogImageSelectorBinding;
import com.king.app.tcareer.model.http.Command;
import com.king.app.tcareer.model.http.bean.ImageItemBean;
import com.king.app.tcareer.model.http.bean.ImageUrlBean;
import com.king.app.tcareer.utils.ScreenUtils;
import com.king.app.tcareer.view.dialog.DraggableDialogFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @desc
 * @auth 景阳
 * @time 2018/2/2 0002 23:56
 */

public abstract class ImageSelector extends DraggableDialogFragment implements ISelectorHolder {

    protected ImageAdapter mAdapter;

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
    protected int getMaxHeight() {
        return ScreenUtils.getScreenHeight(getActivity()) * 4 / 5;
    }

    @Override
    protected Fragment getContentViewFragment() {
        return new SelectorFragment();
    }

    public <T> void setOnSelectorListener(OnSelectorListener<T> onSelectorListener) {
        this.onSelectorListener = onSelectorListener;
    }

    @Override
    public ImageAdapter getAdapter() {
        if (mAdapter == null) {
            mAdapter = new ImageAdapter();
            mAdapter.setList(createList());
        }
        return mAdapter;
    }

    private List<Object> createList() {
        List<Object> list = new ArrayList<>();
        if (imageUrlBean.getItemList() != null) {
            Map<String, GroupBean> groupMap = new HashMap<>();
            for (int i = 0; i < imageUrlBean.getItemList().size(); i ++) {
                ImageItemBean bean = imageUrlBean.getItemList().get(i);
                String key = bean.getKey();
                GroupBean group = groupMap.get(key);
                if (group == null) {
                    // head第一次出现，添加head
                    GroupBean pack = new GroupBean();
                    pack.setTitle(getTypeName(key));
                    list.add(pack);
                    groupMap.put(key, pack);
                }
                ItemBean item = new ItemBean();
                initItemBean(item, bean);
                list.add(item);
            }
        }
        return list;
    }

    protected abstract void initItemBean(ItemBean item, ImageItemBean bean);

    protected String getTypeName(String key) {
        if (key.equals(Command.TYPE_IMG_PLAYER)) {
            return "Player Normal";
        }
        else if (key.equals(Command.TYPE_IMG_MATCH)) {
            return "Match";
        }
        else {
            return "Player Head";
        }
    }

    public static class SelectorFragment extends BindingContentFragment<DialogImageSelectorBinding, BaseViewModel> {

        private ISelectorHolder holder;

        private final int COLUMN = 3;

        @Override
        protected int getContentLayoutRes() {
            return R.layout.dialog_image_selector;
        }

        @Override
        protected BaseViewModel createViewModel() {
            return null;
        }

        @Override
        protected void onCreate(View view) {
            GridLayoutManager layoutManager = new GridLayoutManager(getContext(), COLUMN);
            layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return holder.getAdapter().getSpanSize(position, COLUMN);
                }
            });
            mBinding.rvSelector.setLayoutManager(layoutManager);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
            );
            view.setLayoutParams(params);

            mBinding.rvSelector.setAdapter(holder.getAdapter());
        }

        @Override
        protected void bindChildFragmentHolder(IFragmentHolder holder) {
            this.holder = (ISelectorHolder) holder;
        }

    }

    public interface OnSelectorListener<T> {
        void onSelectDone(List<T> list);
    }
}
