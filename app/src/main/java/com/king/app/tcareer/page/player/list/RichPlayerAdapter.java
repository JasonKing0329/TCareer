package com.king.app.tcareer.page.player.list;

import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.king.app.tcareer.R;
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
import com.king.app.tcareer.view.adapter.BaseRecyclerAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @desc
 * @auth 景阳
 * @time 2018/5/19 0019 14:54
 */

public class RichPlayerAdapter extends BaseRecyclerAdapter<RichPlayerAdapter.PlayerHolder, RichPlayerBean> {

    private Map<Long, Boolean> mExpandMap;

    private SparseBooleanArray checkMap;
    private RequestOptions playerOptions;
    private SimpleDateFormat dateFormat;

    private OnRichPlayerListener onRichPlayerListener;

    private boolean isSelectMode;

    /**
     * 单击头像位置
     */
    private int nGroupPosition;

    /**
     * 保存首次从文件夹加载的图片序号
     */
    protected Map<String, Integer> playerImageIndexMap;

    public RichPlayerAdapter() {
        super();
        checkMap = new SparseBooleanArray();
        playerOptions = GlideOptions.getDefaultPlayerOptions();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        playerImageIndexMap = new HashMap<>();
    }

    public void setExpandMap(Map<Long, Boolean> mExpandMap) {
        this.mExpandMap = mExpandMap;
    }

    public void setOnRichPlayerListener(OnRichPlayerListener onRichPlayerListener) {
        this.onRichPlayerListener = onRichPlayerListener;
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
    protected PlayerHolder newViewHolder(View view) {
        return new PlayerHolder(view);
    }

    /**
     * image path
     * @param position
     * @return
     */
    protected String getPlayerPath(int position) {
        String filePath;
        if (playerImageIndexMap.get(list.get(position).getCompetitorBean().getNameChn()) == null) {
            filePath = ImageProvider.getPlayerHeadPath(list.get(position).getCompetitorBean().getNameChn(), playerImageIndexMap);
        }
        else {
            filePath = ImageProvider.getPlayerHeadPath(list.get(position).getCompetitorBean().getNameChn()
                    , playerImageIndexMap.get(list.get(position).getCompetitorBean().getNameChn()));
        }
        return filePath;
    }

    @Override
    public void onBindViewHolder(PlayerHolder holder, int position) {
        CompetitorBean bean = list.get(position).getCompetitorBean();
        PlayerAtpBean atpBean = bean.getAtpBean();

        holder.tvIndex.setVisibility(bean instanceof User ? View.GONE:View.VISIBLE);
        holder.tvIndex.setText(String.valueOf(position - 3));// 一共4个user

        holder.itemView.setBackgroundColor(bean instanceof User ?
                Color.parseColor("#efefef"):Color.WHITE);

        holder.tvName.setText(bean.getNameEng());
        holder.tvH2h.setText(list.get(position).getWin() + "-" + list.get(position).getLose());
        boolean hasAtpDetail = (atpBean != null && atpBean.getLastUpdateDate() > 0);
        if (hasAtpDetail) {
            // basic
            holder.tvPlace.setText(getPlaceFromAtp(atpBean.getBirthCity(), atpBean.getBirthCountry()));
            holder.tvBirthday.setText(getBirthdayDetail(atpBean.getBirthday()));
            holder.tvBody.setText(bean.getNameChn()
                    + ", " + FormatUtil.formatNumber(atpBean.getCm()) + "cm, "
                    + FormatUtil.formatNumber(atpBean.getKg()) + "kg");

            // more
            holder.tvTurnedPro.setText("Turned Pro  " + atpBean.getTurnedPro());
            holder.tvTime.setText(dateFormat.format(new Date(atpBean.getLastUpdateDate())));
            holder.tvResidence.setText("Residence:  " + getPlaceFromAtp(atpBean.getResidenceCity(), atpBean.getResidenceCountry()));
            holder.tvPlays.setText("Plays:  " + atpBean.getPlays());
            holder.tvHigh.setText("生涯最高排名 【" + atpBean.getCareerHighSingle() + "】 " + atpBean.getCareerHighSingleDate());
            holder.tvCareer.setText("职业生涯  " + atpBean.getCareerSingles() + "冠  "
                    + atpBean.getCareerWin() + "胜" + atpBean.getCareerLose() + "负  总奖金" + atpBean.getCareerPrize());
            holder.tvCoach.setText("Coach:  " + atpBean.getCoach());
            holder.tvCoach.setVisibility(TextUtils.isEmpty(atpBean.getCoach()) ? View.GONE:View.VISIBLE);
        }
        else {
            // basic
            holder.tvPlace.setText(bean.getCountry());
            holder.tvBirthday.setText(getBirthdayDetail(bean.getBirthday()));
            holder.tvBody.setText(bean.getNameChn());
        }
        Glide.with(holder.ivPlayer.getContext())
                .load(getPlayerPath(position))
                .apply(playerOptions)
                .into(holder.ivPlayer);

        holder.ivRefresh.setVisibility(bean.getAtpId() == null ? View.INVISIBLE:View.VISIBLE);
        holder.ivRefresh.setTag(position);
        holder.ivRefresh.setOnClickListener(refreshListener);

        holder.ivPlayer.setOnClickListener(view -> {
            nGroupPosition = position;

            ImageManager imageManager = new ImageManager(view.getContext());
            imageManager.setOnActionListener(imageActionListener);
            imageManager.setDataProvider(dataProvider);
            imageManager.showOptions(list.get(nGroupPosition).getCompetitorBean().getNameChn()
                    , nGroupPosition, Command.TYPE_IMG_PLAYER, list.get(nGroupPosition).getCompetitorBean().getNameChn());
        });

        holder.cbCheck.setVisibility(isSelectMode && (bean instanceof PlayerBean) ? View.VISIBLE:View.GONE);
        holder.cbCheck.setChecked(checkMap.get(position));

        // 除了expand map里的状态，还要看是否有atpDetail，以及user没有扩展信息
        boolean expanded = mExpandMap.get(bean.getId()) && hasAtpDetail;
        holder.groupExpand.setVisibility(expanded ? View.VISIBLE:View.GONE);
        holder.ivMore.setVisibility(hasAtpDetail ? View.VISIBLE:View.GONE);
        holder.ivMore.setImageResource(expanded ? R.drawable.ic_keyboard_arrow_up_666_24dp:R.drawable.ic_keyboard_arrow_down_666_24dp);
        holder.ivMore.setTag(position);
        holder.ivMore.setOnClickListener(moreListener);
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
    protected void onClickItem(View v, PlayerHolder holder) {
        if (isSelectMode()) {
            boolean targetCheck = !checkMap.get(holder.getLayoutPosition());
            holder.cbCheck.setChecked(targetCheck);
            checkMap.put(holder.getLayoutPosition(), targetCheck);
        }
        else {
            if (onRichPlayerListener != null) {
                onRichPlayerListener.onClickItem(holder.getLayoutPosition(), list.get(holder.getLayoutPosition()).getCompetitorBean());
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

    ImageManager.DataProvider dataProvider = new ImageManager.DataProvider() {

        @Override
        public ImageUrlBean createImageUrlBean(DataController dataController) {
            ImageUrlBean bean = dataController.getPlayerImageUrlBean(list.get(nGroupPosition).getCompetitorBean().getNameChn());
            return bean;
        }
    };

    public static class PlayerHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_player)
        ImageView ivPlayer;
        @BindView(R.id.iv_more)
        ImageView ivMore;
        @BindView(R.id.tv_index)
        TextView tvIndex;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_h2h)
        TextView tvH2h;
        @BindView(R.id.tv_place)
        TextView tvPlace;
        @BindView(R.id.tv_birthday)
        TextView tvBirthday;
        @BindView(R.id.tv_body)
        TextView tvBody;
        @BindView(R.id.tv_turned_pro)
        TextView tvTurnedPro;
        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.tv_residence)
        TextView tvResidence;
        @BindView(R.id.tv_plays)
        TextView tvPlays;
        @BindView(R.id.tv_high)
        TextView tvHigh;
        @BindView(R.id.tv_career)
        TextView tvCareer;
        @BindView(R.id.tv_coach)
        TextView tvCoach;
        @BindView(R.id.group_expand)
        LinearLayout groupExpand;
        @BindView(R.id.iv_refresh)
        ImageView ivRefresh;
        @BindView(R.id.cb_check)
        CheckBox cbCheck;

        public PlayerHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnRichPlayerListener extends OnItemClickListener<CompetitorBean> {
        void onRefreshItem(int position, CompetitorBean bean);
    }
}
