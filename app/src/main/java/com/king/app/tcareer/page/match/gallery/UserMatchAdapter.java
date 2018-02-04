package com.king.app.tcareer.page.match.gallery;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.king.app.tcareer.R;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.GlideOptions;
import com.king.app.tcareer.model.ImageProvider;
import com.king.app.tcareer.model.http.Command;
import com.king.app.tcareer.model.http.bean.ImageUrlBean;
import com.king.app.tcareer.page.imagemanager.DataController;
import com.king.app.tcareer.page.imagemanager.ImageManager;
import com.king.app.tcareer.page.match.page.MatchPageActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/315 10:1
 */
public class UserMatchAdapter extends RecyclerView.Adapter<UserMatchAdapter.ItemHolder> implements View.OnClickListener {

    private Context mContext;
    private List<UserMatchBean> list;

    private int colorHard;
    private int colorClay;
    private int colorGrass;
    private int colorInnerHard;
    private String[] courtValues;

    /**
     * 保存首次从文件夹加载的图片序号
     */
    private Map<String, Integer> imageIndexMap;

    private View tvMatch;
    private View tvPlace;

    private ImageManager imageManager;

    private RequestOptions matchOptions;

    private FragmentManager fragmentManager;
    private long userId;

    public UserMatchAdapter(Context mContext, List<UserMatchBean> list) {
        this.list = list;
        this.mContext = mContext;
        courtValues = AppConstants.RECORD_MATCH_COURTS;
        colorHard = mContext.getResources().getColor(R.color.swipecard_text_hard);
        colorClay = mContext.getResources().getColor(R.color.swipecard_text_clay);
        colorGrass = mContext.getResources().getColor(R.color.swipecard_text_grass);
        colorInnerHard = mContext.getResources().getColor(R.color.swipecard_text_innerhard);

        imageIndexMap = new HashMap<>();
        imageManager = new ImageManager(mContext);
        imageManager.setOnActionListener(new ImageManager.OnActionListener() {
            @Override
            public void onRefresh(int position) {

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
        });

        matchOptions = GlideOptions.getDefaultMatchOptions();
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_user_match_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        UserMatchBean bean = list.get(position);

        String filePath;
        if (imageIndexMap.get(bean.getNameBean().getName()) == null) {
            filePath = ImageProvider.getMatchHeadPath(bean.getNameBean().getName()
                    , bean.getNameBean().getMatchBean().getCourt(), imageIndexMap);
        }
        else {
            filePath = ImageProvider.getMatchHeadPath(bean.getNameBean().getName()
                    , bean.getNameBean().getMatchBean().getCourt(), imageIndexMap.get(bean.getNameBean().getName()));
        }

        Glide.with(holder.image.getContext())
                .load(filePath)
                .apply(matchOptions)
                .into(holder.image);

        holder.level.setText(bean.getNameBean().getMatchBean().getLevel());
        holder.court.setText(bean.getNameBean().getMatchBean().getCourt());
        holder.total.setText("总胜负  " + bean.getWin() + "胜" + bean.getLose() + "负");
        String best = "最佳战绩  " + bean.getBest();
        if (bean.getBestYears().length() > 0) {
            best = best + "(" + bean.getBestYears() + ")";
        }
        holder.best.setText(best);

        int color = getCardIndexColor(position);
        holder.court.setTextColor(color);

        holder.group.setTag(position);
        holder.group.setOnClickListener(this);
    }

    public int getCardIndexColor(int position) {
        int color = colorHard;
        if (position < list.size()) {
            if (list.get(position).getNameBean().getMatchBean().getCourt().equals(courtValues[1])) {
                color = colorClay;
            }
            else if (list.get(position).getNameBean().getMatchBean().getCourt().equals(courtValues[2])) {
                color = colorGrass;
            }
            else if (list.get(position).getNameBean().getMatchBean().getCourt().equals(courtValues[3])) {
                color = colorInnerHard;
            }
        }
        return color;
    }
    @Override
    public int getItemCount() {
        return list == null ? 0:list.size();
    }

    public void startDownload(int index) {
        imageManager.download(Command.TYPE_IMG_MATCH, list.get(index).getNameBean().getName());
    }

    public void refreshImage(int index) {
        ImageProvider.getMatchHeadPath(list.get(index).getNameBean().getName()
                , list.get(index).getNameBean().getMatchBean().getCourt(), imageIndexMap);
        notifyDataSetChanged();
    }

    public void deleteImage(int index) {
        final String match = list.get(index).getNameBean().getName();
        imageManager.setDataProvider(new ImageManager.DataProvider() {
            @Override
            public ImageUrlBean createImageUrlBean(DataController interactionController) {
                ImageUrlBean bean = interactionController.getMatchImageUrlBean(match);
                return bean;
            }
        });
        imageManager.manageLocal();
    }

    @Override
    public void onClick(View view) {
        int position = (int) view.getTag();
//        ObjectCache.putUserMatchBean(list.get(position));
//        Intent intent = new Intent().setClass(view.getContext(), MatchActivity.class);
//        Pair<View, String>[] pairs = new Pair[3];
//        pairs[0] = Pair.create(view.findViewById(R.id.swipecard_match_img), view.getContext().getString(R.string.anim_player_page_head));
//        pairs[1] = Pair.create(tvMatch, view.getContext().getString(R.string.anim_pullzoom_match_name));
//        pairs[2] = Pair.create(tvPlace, view.getContext().getString(R.string.anim_pullzoom_match_country));
//        ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(
//                (Activity) view.getContext(), pairs);
//        view.getContext().startActivity(intent, transitionActivityOptions.toBundle());

        Intent intent = new Intent().setClass(view.getContext(), MatchPageActivity.class);
        intent.putExtra(MatchPageActivity.KEY_MATCH_NAME_ID, list.get(position).getNameBean().getId());
        intent.putExtra(MatchPageActivity.KEY_USER_ID, userId);
        view.getContext().startActivity(intent);
    }

    /**
     * 用于转场动画
     * @param tvMatch
     * @param tvPlace
     */
    public void setMatchTextView(TextView tvMatch, TextView tvPlace) {
        this.tvMatch = tvMatch;
        this.tvPlace = tvPlace;
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {

        ImageView image;
        ViewGroup group;
        TextView level, court, total, best;
        public ItemHolder(View convertView) {
            super(convertView);
            group = (ViewGroup) convertView.findViewById(R.id.match_group);
            level = (TextView) convertView.findViewById(R.id.swipecard_match_level);
            court = (TextView) convertView.findViewById(R.id.swipecard_match_court);
            total = (TextView) convertView.findViewById(R.id.swipecard_match_total);
            best = (TextView) convertView.findViewById(R.id.swipecard_match_best);
            image = (ImageView) convertView.findViewById(R.id.swipecard_match_img);
        }
    }
}
