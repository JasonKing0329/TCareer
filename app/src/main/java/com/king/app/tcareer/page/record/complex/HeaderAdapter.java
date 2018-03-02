package com.king.app.tcareer.page.record.complex;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.king.app.tcareer.R;
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
public class HeaderAdapter extends AbstractExpandableAdapterItem {

//    private TextView tvMatchFirst;
    private ImageView ivMatch;
    private TextView tvMatchName;
    private TextView tvMatchLevel;
    private TextView tvMatchDate;
    private TextView tvMatchRound;
    private ImageView ivWinnerCup;
    private ViewGroup groupCard;

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

        ivWinnerCup.setVisibility(View.GONE);
        tvMatchRound.setVisibility(View.GONE);

        tvMatchDate.setText(record.getDateStr());
        tvMatchLevel.setText(record.getMatch().getMatchBean().getLevel());

        String court = record.getMatch().getMatchBean().getCourt();
        Glide.with(ivMatch.getContext())
                .load(ImageProvider.getMatchHeadPath(record.getMatch().getName(), court))
                .apply(GlideOptions.getDefaultMatchOptions())
                .into(ivMatch);
    }

    @Override
    public void onSetViews() {

    }

    @Override
    public void onExpansionToggled(boolean expanded) {

    }

}
