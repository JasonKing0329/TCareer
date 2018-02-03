package com.king.app.tcareer.page.match.manage;

import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.king.app.tcareer.R;
import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.model.GlideOptions;
import com.king.app.tcareer.model.ImageProvider;
import com.king.app.tcareer.model.db.entity.MatchNameBean;
import com.king.app.tcareer.model.http.Command;
import com.king.app.tcareer.model.http.bean.ImageUrlBean;
import com.king.app.tcareer.page.imagemanager.DataController;
import com.king.app.tcareer.page.imagemanager.ImageManager;
import com.king.app.tcareer.utils.DebugLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/10/18 10:56
 */
public abstract class MatchManageBaseAdapter extends RecyclerView.Adapter implements View.OnClickListener {

    protected List<MatchNameBean> list;
    protected boolean selectMode;
    protected SparseBooleanArray mCheckMap;
    protected OnMatchItemClickListener onMatchItemClickListener;

    /**
     * 保存首次从文件夹加载的图片序号
     */
    protected Map<String, Integer> imageIndexMap;

    /**
     * 单击头像位置
     */
    protected int nGroupPosition;

    private FragmentManager fragmentManager;

    public MatchManageBaseAdapter(List<MatchNameBean> list) {
        this.list = list;
        mCheckMap = new SparseBooleanArray();
        imageIndexMap = new HashMap<>();
    }

    public void setSelectMode(boolean selectMode) {
        this.selectMode = selectMode;
        if (!selectMode) {
            mCheckMap.clear();
        }
    }

    public void setOnMatchItemClickListener(OnMatchItemClickListener onMatchItemClickListener) {
        this.onMatchItemClickListener = onMatchItemClickListener;
    }

    public void setList(List<MatchNameBean> list) {
        this.list = list;
    }

    @Override
    public int getItemCount() {
        return list == null ? 0:list.size();
    }

    protected void onBindImage(ImageView image, int position) {
        MatchNameBean bean = list.get(position);
        String filePath;
        if (imageIndexMap.get(bean.getName()) == null) {
            filePath = ImageProvider.getMatchHeadPath(bean.getName(), bean.getMatchBean().getCourt(), imageIndexMap);
        }
        else {
            filePath = ImageProvider.getMatchHeadPath(bean.getName(), bean.getMatchBean().getCourt(), imageIndexMap.get(bean.getName()));
        }
        Glide.with(TApplication.getInstance())
                .load("file://" + filePath)
                .apply(GlideOptions.getDefaultMatchOptions())
                .into(image);
        image.setOnClickListener(this);
        image.setTag(R.id.tag_record_list_player_group_index, position);
    }

    protected void onBindCheckStatus(CheckBox check, int position) {
        check.setVisibility(selectMode ? View.VISIBLE:View.GONE);
        check.setChecked(mCheckMap.get(position));
    }

    protected void onBindGroupStatus(ViewGroup group, int position) {
        group.setTag(position);
        group.setOnClickListener(this);
    }

    public void notifyItemChanged(MatchNameBean editBean) {
        for (int i = 0; i < getItemCount(); i ++) {
            if (list.get(i).getId() == editBean.getId()) {
                notifyItemChanged(i);
                break;
            }
        }
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public interface OnMatchItemClickListener {
        void onMatchItemClick(MatchNameBean bean);
    }

    protected void showImageAction(View v) {
        nGroupPosition = (int) v.getTag(R.id.tag_record_list_player_group_index);
        ImageManager manager = new ImageManager(v.getContext());
        manager.setOnActionListener(imageActionListener);
        manager.setDataProvider(dataProvider);
        manager.showOptions(list.get(nGroupPosition).getName(), nGroupPosition, Command.TYPE_IMG_MATCH, list.get(nGroupPosition).getName());
    }

    ImageManager.DataProvider dataProvider = new ImageManager.DataProvider() {

        @Override
        public ImageUrlBean createImageUrlBean(DataController dataController) {
            ImageUrlBean bean = dataController.getMatchImageUrlBean(list.get(nGroupPosition).getName());
            return bean;
        }
    };

    ImageManager.OnActionListener imageActionListener = new ImageManager.OnActionListener() {
        @Override
        public void onRefresh(int position) {
            String path = ImageProvider.getMatchHeadPath(list.get(position).getName(), list.get(position).getMatchBean().getCourt(), imageIndexMap);
            DebugLog.e(path);
            notifyDataSetChanged();
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
                dlist.add(list.get(i));
            }
        }
        return dlist;
    }

    @Override
    public void onClick(View v) {
        if (v instanceof ViewGroup) {
            onClickItem(v);
        }
        else if (v instanceof ImageView) {
            showImageAction(v);
        }
    }

    protected void onClickItem(View v) {
        int position = (int) v.getTag();
        if (selectMode) {
            mCheckMap.put(position, !mCheckMap.get(position));
            notifyDataSetChanged();
        }
        else {
            if (onMatchItemClickListener != null) {
                onMatchItemClickListener.onMatchItemClick(list.get(position));
            }
        }
    }
}
