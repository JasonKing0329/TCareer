package com.king.app.tcareer.page.player.list;

import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.View;

import com.bumptech.glide.request.RequestOptions;
import com.king.app.tcareer.R;
import com.king.app.tcareer.base.mvvm.BaseBindingAdapter;
import com.king.app.tcareer.databinding.AdapterPlayerRichBinding;
import com.king.app.tcareer.model.GlideOptions;
import com.king.app.tcareer.model.ImageProvider;
import com.king.app.tcareer.model.bean.CompetitorBean;
import com.king.app.tcareer.model.db.entity.PlayerAtpBean;
import com.king.app.tcareer.model.db.entity.PlayerBean;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.model.http.Command;
import com.king.app.tcareer.model.http.bean.ImageUrlBean;
import com.king.app.tcareer.page.imagemanager.DataController;
import com.king.app.tcareer.page.imagemanager.ImageManager;
import com.king.app.tcareer.utils.ConstellationUtil;
import com.king.app.tcareer.utils.FormatUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @desc
 * @auth 景阳
 * @time 2018/5/19 0019 14:54
 */

public class RichPlayerAdapter extends BaseBindingAdapter<AdapterPlayerRichBinding, RichPlayerBean> {

    private Map<Long, Boolean> mExpandMap;

    private SparseBooleanArray checkMap;
    private RequestOptions playerOptions;
    private SimpleDateFormat dateFormat;

    private OnRichPlayerListener onRichPlayerListener;

    private boolean isSelectMode;

    private int nIndexOffset;

    /**
     * 单击头像位置
     */
    private int nGroupPosition;

    public RichPlayerAdapter() {
        super();
        checkMap = new SparseBooleanArray();
        playerOptions = GlideOptions.getDefaultPlayerOptions();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    public void setExpandMap(Map<Long, Boolean> mExpandMap) {
        this.mExpandMap = mExpandMap;
    }

    public void setOnRichPlayerListener(OnRichPlayerListener onRichPlayerListener) {
        this.onRichPlayerListener = onRichPlayerListener;
    }

    @Override
    public void setList(List<RichPlayerBean> list) {
        super.setList(list);
        nIndexOffset = 0;
        for (int i = 0; i < getItemCount(); i ++) {
            if (list.get(i).getCompetitorBean() instanceof User) {
                nIndexOffset ++;
            }
        }
    }

    public void setSelectMode(boolean selectMode) {
        isSelectMode = selectMode;
        checkMap.clear();
    }

    public boolean isSelectMode() {
        return isSelectMode;
    }

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_player_rich;
    }

    @Override
    protected void onBindItem(AdapterPlayerRichBinding binding, int position, RichPlayerBean data) {
        binding.setBean(data);
        CompetitorBean bean = data.getCompetitorBean();
        PlayerAtpBean atpBean = bean.getAtpBean();

        binding.tvIndex.setVisibility(position < nIndexOffset ? View.GONE:View.VISIBLE);
        binding.tvIndex.setText(String.valueOf(position - nIndexOffset + 1));

        binding.getRoot().setBackgroundColor(bean instanceof User ?
                Color.parseColor("#efefef"):Color.WHITE);

        binding.tvName.setText(bean.getNameEng());
        binding.tvWin.setText(String.valueOf(list.get(position).getWin()));
        binding.tvLose.setText(String.valueOf(list.get(position).getLose()));
        if (list.get(position).getWin() > list.get(position).getLose()) {
            binding.tvWin.setTextColor(binding.tvWin.getResources().getColor(R.color.h2hlist_color_win));
            binding.tvLose.setTextColor(binding.tvWin.getResources().getColor(R.color.h2hlist_color_lose));
        }
        else if (list.get(position).getWin() < list.get(position).getLose()) {
            binding.tvLose.setTextColor(binding.tvWin.getResources().getColor(R.color.h2hlist_color_win));
            binding.tvWin.setTextColor(binding.tvWin.getResources().getColor(R.color.h2hlist_color_lose));
        }
        else {
            binding.tvWin.setTextColor(binding.tvWin.getResources().getColor(R.color.h2hlist_color_tie));
            binding.tvLose.setTextColor(binding.tvWin.getResources().getColor(R.color.h2hlist_color_tie));
        }
        boolean hasAtpDetail = (atpBean != null && atpBean.getLastUpdateDate() > 0);
        if (hasAtpDetail) {
            // basic
            binding.tvPlace.setText(getPlaceFromAtp(atpBean.getBirthCity(), atpBean.getBirthCountry()));
            binding.tvBirthday.setText(getBirthdayDetail(atpBean.getBirthday()));
            binding.tvBody.setText(bean.getNameChn()
                    + ", " + FormatUtil.formatNumber(atpBean.getCm()) + "cm, "
                    + FormatUtil.formatNumber(atpBean.getKg()) + "kg");

            // more
            binding.tvTurnedPro.setText("Turned Pro  " + atpBean.getTurnedPro());
            binding.tvTime.setText(dateFormat.format(new Date(atpBean.getLastUpdateDate())));
            binding.tvResidence.setText("Residence:  " + getPlaceFromAtp(atpBean.getResidenceCity(), atpBean.getResidenceCountry()));
            binding.tvPlays.setText("Plays:  " + atpBean.getPlays());
            binding.tvHigh.setText("生涯最高排名 【" + atpBean.getCareerHighSingle() + "】 " + atpBean.getCareerHighSingleDate());
            binding.tvCareer.setText("职业生涯  " + atpBean.getCareerSingles() + "冠  "
                    + atpBean.getCareerWin() + "胜" + atpBean.getCareerLose() + "负  总奖金" + atpBean.getCareerPrize());
            binding.tvCoach.setText("Coach:  " + atpBean.getCoach());
            binding.tvCoach.setVisibility(TextUtils.isEmpty(atpBean.getCoach()) ? View.GONE:View.VISIBLE);
        }
        else {
            // basic
            binding.tvPlace.setText(bean.getCountry());
            binding.tvBirthday.setText(getBirthdayDetail(bean.getBirthday()));
            binding.tvBody.setText(bean.getNameChn());
        }

        binding.ivRefresh.setVisibility(bean.getAtpId() == null ? View.INVISIBLE:View.VISIBLE);
        binding.ivRefresh.setTag(position);
        binding.ivRefresh.setOnClickListener(refreshListener);

        binding.ivPlayer.setOnClickListener(view -> {
            nGroupPosition = position;

            ImageManager imageManager = new ImageManager(view.getContext());
            imageManager.setOnActionListener(imageActionListener);
            imageManager.setDataProvider(dataProvider);
            imageManager.showOptions(list.get(nGroupPosition).getCompetitorBean().getNameChn()
                    , nGroupPosition, Command.TYPE_IMG_PLAYER, list.get(nGroupPosition).getCompetitorBean().getNameChn());
        });

        binding.cbCheck.setVisibility(isSelectMode && (bean instanceof PlayerBean) ? View.VISIBLE:View.GONE);
        binding.cbCheck.setChecked(checkMap.get(position));

        // 除了expand map里的状态，还要看是否有atpDetail，以及user没有扩展信息
        boolean expanded = mExpandMap.get(bean.getId()) && hasAtpDetail;
        binding.groupExpand.setVisibility(expanded ? View.VISIBLE:View.GONE);
        binding.ivMore.setVisibility(hasAtpDetail ? View.VISIBLE:View.GONE);
        binding.ivMore.setImageResource(expanded ? R.drawable.ic_keyboard_arrow_up_666_24dp:R.drawable.ic_keyboard_arrow_down_666_24dp);
        binding.ivMore.setTag(position);
        binding.ivMore.setOnClickListener(moreListener);
    }

    private View.OnClickListener moreListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int position = (int) view.getTag();
            boolean targetExpand = !mExpandMap.get(list.get(position).getCompetitorBean().getId());
            mExpandMap.put(list.get(position).getCompetitorBean().getId(), targetExpand);
            notifyItemChanged(position);
        }
    };

    @Override
    protected void onClickItem(View v, int position) {
        super.onClickItem(v, position);
        if (isSelectMode()) {
            boolean targetCheck = !checkMap.get(position);
            checkMap.put(position, targetCheck);
            notifyItemChanged(position);
        }
        else {
            if (onRichPlayerListener != null) {
                onRichPlayerListener.onClickItem(v, position, list.get(position).getCompetitorBean());
            }
        }
    }

    /**
     *
     * @param birthday yyyy-MM-dd
     * @return formatted text: age, birthday, sign
     */
    private String getBirthdayDetail(String birthday) {
        String constel = null;
        try {
            constel = ConstellationUtil.getConstellationEng(birthday);
        } catch (ConstellationUtil.ConstellationParseException e) {
            e.printStackTrace();
        }
        int age = 0;
        try {
            age = ConstellationUtil.getAge(birthday);
        } catch (Exception e) {
            e.printStackTrace();
        }
        StringBuffer buffer = new StringBuffer();
        if (age != 0) {
            buffer.append(age).append(", ");
        }
        buffer.append(birthday);
        if (!TextUtils.isEmpty(constel)) {
            buffer.append(", ").append(constel);
        }
        return buffer.toString();
    }

    public String getPlaceFromAtp(String city, String country) {
        if (TextUtils.isEmpty(city)) {
            return country;
        }
        else {
            return city + ", " + country;
        }
    }

    private View.OnClickListener refreshListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int position = (int) view.getTag();
            if (onRichPlayerListener != null) {
                onRichPlayerListener.onRefreshItem(position, list.get(position).getCompetitorBean());
            }
        }
    };

    public void notifyPlayerChanged(long id) {
        for (int i = 0; i < list.size(); i ++) {
            if (list.get(i).getCompetitorBean().getId() == id) {
                notifyItemChanged(i);
                break;
            }
        }
    }

    public List<RichPlayerBean> getSelectedList() {
        List<RichPlayerBean> results = new ArrayList<>();
        for (int i = 0; i < list.size(); i ++) {
            if (checkMap.get(i)) {
                results.add(list.get(i));
                break;
            }
        }
        return results;
    }

    private FragmentManager fragmentManager;

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    ImageManager.OnActionListener imageActionListener = new ImageManager.OnActionListener() {
        @Override
        public void onRefresh(int position) {
            String name = list.get(position).getCompetitorBean().getNameChn();
            String path = ImageProvider.getPlayerHeadPath(name);
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

    ImageManager.DataProvider dataProvider = new ImageManager.DataProvider() {

        @Override
        public ImageUrlBean createImageUrlBean(DataController dataController) {
            ImageUrlBean bean = dataController.getPlayerImageUrlBean(list.get(nGroupPosition).getCompetitorBean().getNameChn());
            return bean;
        }
    };

    public interface OnRichPlayerListener extends OnItemClickListener<CompetitorBean> {
        void onRefreshItem(int position, CompetitorBean bean);
    }
}
