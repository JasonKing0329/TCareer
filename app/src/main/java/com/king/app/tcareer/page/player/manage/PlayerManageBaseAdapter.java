package com.king.app.tcareer.page.player.manage;

import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.king.app.tcareer.R;
import com.king.app.tcareer.model.GlideOptions;
import com.king.app.tcareer.model.ImageProvider;
import com.king.app.tcareer.model.db.entity.PlayerBean;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.model.http.Command;
import com.king.app.tcareer.model.http.bean.ImageUrlBean;
import com.king.app.tcareer.page.imagemanager.DataController;
import com.king.app.tcareer.page.imagemanager.ImageManager;
import com.king.app.tcareer.page.setting.SettingProperty;
import com.king.app.tcareer.utils.ConstellationUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/8/17 16:14
 */
@Deprecated
public abstract class PlayerManageBaseAdapter extends RecyclerView.Adapter implements View.OnClickListener {

    protected List<PlayerViewBean> list;
    private List<PlayerViewBean> originList;

    private String mKeyword;

    protected boolean selectMode;
    protected SparseBooleanArray mCheckMap;
    private OnPlayerItemClickListener onPlayerItemClickListener;

    /**
     * 单击头像位置
     */
    private int nGroupPosition;

    /**
     * 保存首次从文件夹加载的图片序号
     */
    protected Map<String, Integer> playerImageIndexMap;

    private FragmentManager fragmentManager;

    public PlayerManageBaseAdapter(List<PlayerViewBean> list) {
        mCheckMap = new SparseBooleanArray();
        playerImageIndexMap = new HashMap<>();
        setList(list);
    }

    public void setSelectMode(boolean selectMode) {
        this.selectMode = selectMode;
        if (!selectMode) {
            mCheckMap.clear();
        }
    }

    public void setList(List<PlayerViewBean> data) {
        originList = data;
        list = new ArrayList<>();
        for (PlayerViewBean t:originList) {
            list.add(t);
        }
    }

    public void setOnPlayerItemClickListener(OnPlayerItemClickListener onPlayerItemClickListener) {
        this.onPlayerItemClickListener = onPlayerItemClickListener;
    }

    @Override
    public int getItemCount() {
        return list == null ? 0:list.size();
    }

    public List<PlayerViewBean> getSelectedList() {
        List<PlayerViewBean> dlist = new ArrayList<>();
        for (int i = 0; i < list.size(); i ++) {
            if (mCheckMap.get(i)) {
                dlist.add(list.get(i));
            }
        }
        return dlist;
    }

    /**
     * Item背景色
     * @param position
     * @param view
     */
    protected void updateItemBackground(int position, View view) {
        if (list.get(position).getData() instanceof User) {// 显示为浅灰背景
            view.setBackgroundColor(view.getContext().getResources().getColor(R.color.lightgrey));
        }
        else {
            view.setBackgroundColor(view.getContext().getResources().getColor(R.color.white));
        }
    }

    protected boolean isSortByConstellation() {
        return SettingProperty.getPlayerSortMode() == SettingProperty.VALUE_SORT_PLAYER_CONSTELLATION;
    }

    protected String getConstellation(int position) {
        String constellation;
        try {
            constellation = ConstellationUtil.getConstellationEng(list.get(position).getBirthday());
            constellation = constellation.concat("(").concat(list.get(position).getBirthday()).concat(")");
        } catch (ConstellationUtil.ConstellationParseException e) {
            e.printStackTrace();
            constellation = list.get(position).getBirthday();
        }
        return constellation;
    }

    /**
     * image path
     * @param position
     * @return
     */
    protected String getPlayerPath(int position) {
        String filePath;
        if (playerImageIndexMap.get(list.get(position).getName()) == null) {
            filePath = ImageProvider.getPlayerHeadPath(list.get(position).getName(), playerImageIndexMap);
        }
        else {
            filePath = ImageProvider.getPlayerHeadPath(list.get(position).getName(), playerImageIndexMap.get(list.get(position).getName()));
        }
        return filePath;
    }

    /**
     * show and set listener
     * @param path
     * @param position
     * @param view
     */
    protected void updateItemImage(String path, int position, ImageView view) {
        if (path == null) {
            Glide.with(view.getContext())
                    .load(R.drawable.pic_def)
                    .into(view);
        }
        else {
            Glide.with(view.getContext())
                    .load("file://" + path)
                    .apply(GlideOptions.getDefaultPlayerOptions())
                    .into(view);
        }
        view.setOnClickListener(this);
        view.setTag(R.id.tag_record_list_player_group_index, position);
    }

    /**
     * check status
     * @param position
     * @param checkBox
     */
    protected void updateCheckStatus(int position, CheckBox checkBox) {
        if (selectMode) {
            if (list.get(position).getData() instanceof User) {// 不允许删除
                checkBox.setVisibility(View.INVISIBLE);
            }
            else {
                checkBox.setVisibility(View.VISIBLE);
            }
        }
        else {
            checkBox.setVisibility(View.GONE);
        }
        checkBox.setChecked(mCheckMap.get(position));
    }

    @Override
    public void onClick(View v) {
        if (v instanceof ViewGroup) {
            int position = (int) v.getTag();
            if (selectMode) {
                mCheckMap.put(position, !mCheckMap.get(position));
                notifyDataSetChanged();
            }
            else {
                if (onPlayerItemClickListener != null) {
                    onPlayerItemClickListener.onPlayerItemClick(list.get(position));
                }
            }
        }
        else if (v instanceof ImageView) {
            nGroupPosition = (int) v.getTag(R.id.tag_record_list_player_group_index);

            ImageManager imageManager = new ImageManager(v.getContext());
            imageManager.setOnActionListener(imageActionListener);
            imageManager.setDataProvider(dataProvider);
            imageManager.showOptions(list.get(nGroupPosition).getName(), nGroupPosition, Command.TYPE_IMG_PLAYER, list.get(nGroupPosition).getName());
        }
    }

    public void notifyPlayerChanged(Long playerId) {
        for (int i = 0; i < list.size(); i ++) {
            PlayerViewBean bean = list.get(i);
            if (bean.getData() instanceof PlayerBean) {
                PlayerBean playerBean = (PlayerBean) bean.getData();
                if (playerBean.getId() == playerId) {
                    // 重新读取data里的内容
                    bean.setBirthday(playerBean.getBirthday());
                    bean.setCountry(playerBean.getCountry());
                    bean.setName(playerBean.getNameChn());
                    bean.setNameEng(playerBean.getNameEng());
                    bean.setNamePinyin(playerBean.getNamePinyin());
                    notifyItemChanged(i);
                    break;
                }
            }
        }
    }

    public void notifyUserChanged(Long userId) {
        for (int i = 0; i < list.size(); i ++) {
            PlayerViewBean bean = list.get(i);
            if (bean.getData() instanceof User) {
                if (((User) bean.getData()).getId() == userId) {
                    notifyItemChanged(i);
                    break;
                }
            }
        }
    }

    public void setFragmentManager(FragmentManager manager) {
        this.fragmentManager = manager;
    }

    ImageManager.DataProvider dataProvider = new ImageManager.DataProvider() {

        @Override
        public ImageUrlBean createImageUrlBean(DataController dataController) {
            ImageUrlBean bean = dataController.getPlayerImageUrlBean(list.get(nGroupPosition).getName());
            return bean;
        }
    };

    ImageManager.OnActionListener imageActionListener = new ImageManager.OnActionListener() {
        @Override
        public void onRefresh(int position) {
            String name = list.get(position).getName();
            ImageProvider.getPlayerHeadPath(name, playerImageIndexMap);
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

    public void filter(String text) {
        if (!text.equals(mKeyword)) {
            list.clear();
            mKeyword = text;
            for (int i = 0; i < originList.size(); i ++) {
                if (TextUtils.isEmpty(text)) {
                    list.add(originList.get(i));
                }
                else {
                    if (isMatchForKeyword(originList.get(i), text)) {
                        list.add(originList.get(i));
                    }
                }
            }
            notifyDataSetChanged();
        }
    }

    private boolean isMatchForKeyword(PlayerViewBean playerViewBean, String text) {
        // 中文、英文、拼音模糊匹配
        if (playerViewBean.getName().toLowerCase().contains(text.toLowerCase())) {
            return true;
        }
        if (playerViewBean.getNameEng() != null && playerViewBean.getNameEng().toLowerCase().contains(text.toLowerCase())) {
            return true;
        }
        if (playerViewBean.getNamePinyin() != null && playerViewBean.getNamePinyin().toLowerCase().contains(text.toLowerCase())) {
            return true;
        }
        return false;
    }

    public interface OnPlayerItemClickListener {
        void onPlayerItemClick(PlayerViewBean bean);
    }

}
