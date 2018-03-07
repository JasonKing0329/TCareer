package com.king.app.tcareer.page.record.list;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.king.app.tcareer.R;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.GlideOptions;
import com.king.app.tcareer.model.ImageProvider;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.utils.DebugLog;
import com.zaihuishou.expandablerecycleradapter.viewholder.AbstractExpandableAdapterItem;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/4/21 16:15
 */
public class HeaderAdapter extends AbstractExpandableAdapterItem implements View.OnClickListener {

//    private TextView tvMatchFirst;
    private ImageView ivMatch;
    private TextView tvMatchName;
    private TextView tvMatchLevel;
    private TextView tvMatchDate;
    private TextView tvMatchRound;
    private ImageView ivWinnerCup;
    private ViewGroup groupCard;

    private OnHeadLongClickListener onHeadLongClickListener;

    public HeaderAdapter(OnHeadLongClickListener onHeadLongClickListener) {
        this.onHeadLongClickListener = onHeadLongClickListener;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.adapter_record_group_card;
    }

    @Override
    public void onBindViews(View root) {
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doExpandOrUnexpand();
            }
        });
//        tvMatchFirst = (TextView) root.findViewById(R.id.tv_match_first);
        tvMatchName = (TextView) root.findViewById(R.id.tv_match_name);
        tvMatchLevel = (TextView) root.findViewById(R.id.tv_match_level);
        tvMatchDate = (TextView) root.findViewById(R.id.tv_match_date);
        tvMatchRound = (TextView) root.findViewById(R.id.tv_match_round);
        ivWinnerCup = (ImageView) root.findViewById(R.id.iv_winner_cup);
        ivMatch = (ImageView) root.findViewById(R.id.iv_match);
        groupCard = (ViewGroup) root.findViewById(R.id.group_card);
    }

    @Override
    public void onUpdateViews(Object model, int position) {
        super.onUpdateViews(model, position);
        DebugLog.e("position=" + position);
        HeaderItem item = (HeaderItem) model;
        if (item.getChildItemList() != null) {
            for (int i = 0; i < item.getChildItemList().size(); i ++) {
                item.getChildItemList().get(i).setYearPosition(item.getYearPosition());
                item.getChildItemList().get(i).setHeaderPosition(position);
            }
        }

        Record record = item.getRecord();
        tvMatchName.setText(record.getMatch().getName());
        // champion
        if (AppConstants.RECORD_MATCH_ROUNDS[0].equals(record.getRound())
                && record.getWinnerFlag() == AppConstants.WINNER_USER) {
            ivWinnerCup.setVisibility(View.VISIBLE);
            tvMatchRound.setVisibility(View.GONE);
        }
        else {
            ivWinnerCup.setVisibility(View.GONE);
            tvMatchRound.setVisibility(View.VISIBLE);
            tvMatchRound.setText(record.getRound());
        }
        tvMatchDate.setText(record.getDateStr());
        StringBuffer level = new StringBuffer(record.getMatch().getMatchBean().getLevel());
        level.append("  rank(").append(record.getRank()).append(")");
        if (record.getSeed() > 0) {
            level.append(" seed(").append(record.getSeed()).append(")");
        }
        tvMatchLevel.setText(level);

        String court = record.getMatch().getMatchBean().getCourt();
        Glide.with(ivMatch.getContext())
                .load(ImageProvider.getMatchHeadPath(record.getMatch().getName(), court))
                .apply(GlideOptions.getDefaultMatchOptions())
                .into(ivMatch);

        // 设置onLongClickListener会导致无法展开
//        groupCard.setTag(item);
//        groupCard.setOnLongClickListener(this);
        ivMatch.setTag(R.id.tag_record_group_match, item);
        ivMatch.setOnClickListener(this);
    }

    @Override
    public void onSetViews() {

    }

    @Override
    public void onExpansionToggled(boolean expanded) {

    }

    @Override
    public void onClick(View v) {
        onHeadLongClickListener.onLongClickHead(v, (HeaderItem) v.getTag(R.id.tag_record_group_match));
    }
}
