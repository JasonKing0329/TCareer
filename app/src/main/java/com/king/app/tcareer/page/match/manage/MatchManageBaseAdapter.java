package com.king.app.tcareer.page.match.manage;

import android.databinding.ViewDataBinding;
import android.support.v4.app.FragmentManager;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.king.app.tcareer.base.mvvm.BaseBindingAdapter;
import com.king.app.tcareer.model.ImageProvider;
import com.king.app.tcareer.model.bean.MatchImageBean;
import com.king.app.tcareer.model.db.entity.MatchNameBean;
import com.king.app.tcareer.model.http.Command;
import com.king.app.tcareer.model.http.bean.ImageUrlBean;
import com.king.app.tcareer.page.imagemanager.DataController;
import com.king.app.tcareer.page.imagemanager.ImageManager;
import com.king.app.tcareer.utils.DebugLog;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/10/18 10:56
 */
public abstract class MatchManageBaseAdapter<V extends ViewDataBinding> extends BaseBindingAdapter<V, MatchImageBean> {

    protected boolean selectMode;
    protected SparseBooleanArray mCheckMap;

    /**
     * 单击头像位置
     */
    protected int nGroupPosition;

    private FragmentManager fragmentManager;

    public MatchManageBaseAdapter() {
        mCheckMap = new SparseBooleanArray();
    }

    public void setSelectMode(boolean selectMode) {
        this.selectMode = selectMode;
        if (!selectMode) {
            mCheckMap.clear();
        }
    }

    protected void onBindImage(ImageView image, int position, MatchNameBean bean) {
        image.setOnClickListener(v -> showImageAction(v, position, bean));
    }

    protected void onBindCheckStatus(CheckBox check, int position) {
        check.setVisibility(selectMode ? View.VISIBLE:View.GONE);
        check.setChecked(mCheckMap.get(position));
    }

    public void notifyItemChanged(MatchNameBean editBean) {
        for (int i = 0; i < getItemCount(); i ++) {
            if (list.get(i).getBean().getId() == editBean.getId()) {
                notifyItemChanged(i);
                break;
            }
        }
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    protected void showImageAction(View v, int position, MatchNameBean bean) {
        nGroupPosition = position;
        ImageManager manager = new ImageManager(v.getContext());
        manager.setOnActionListener(imageActionListener);
        manager.setDataProvider(dataProvider);
        manager.showOptions(bean.getName(), position, Command.TYPE_IMG_MATCH, bean.getName());
    }

    ImageManager.DataProvider dataProvider = new ImageManager.DataProvider() {

        @Override
        public ImageUrlBean createImageUrlBean(DataController dataController) {
            ImageUrlBean bean = dataController.getMatchImageUrlBean(list.get(nGroupPosition).getBean().getName());
            return bean;
        }
    };

    ImageManager.OnActionListener imageActionListener = new ImageManager.OnActionListener() {
        @Override
        public void onRefresh(int position) {
            String path = ImageProvider.getMatchHeadPath(list.get(position).getBean().getName(), list.get(position).getBean().getMatchBean().getCourt());
            DebugLog.e(path);
            list.get(position).setImageUrl(path);
        }

        @Override
        public void onManageFinished() {
            notifyDataSetChanged();
        }

        @Override
        public void onDownloadFinished() {
            notifyDataSetChanged();
        }

        @Override
        public FragmentManager getFragmentManager() {
            return fragmentManager;
        }
    };

    public List<MatchNameBean> getSelectedList() {
        List<MatchNameBean> dlist = new ArrayList<>();
        for (int i = 0; i < list.size(); i ++) {
            if (mCheckMap.get(i)) {
                dlist.add(list.get(i).getBean());
            }
        }
        return dlist;
    }

    @Override
    protected void onClickItem(View v, int position) {
        if (selectMode) {
            mCheckMap.put(position, !mCheckMap.get(position));
            notifyDataSetChanged();
        }
        else {
            super.onClickItem(v, position);
        }
    }

}
