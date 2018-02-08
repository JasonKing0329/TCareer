package com.king.app.tcareer.page.glory.title;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.king.app.tcareer.R;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.CompetitorParser;
import com.king.app.tcareer.model.GlideOptions;
import com.king.app.tcareer.model.ImageProvider;
import com.king.app.tcareer.model.ScoreParser;
import com.king.app.tcareer.model.bean.CompetitorBean;
import com.king.app.tcareer.model.db.entity.Record;
import com.zaihuishou.expandablerecycleradapter.viewholder.AbstractExpandableAdapterItem;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/6/21 10:05
 */
public class SubItemAdapter extends AbstractExpandableAdapterItem implements View.OnClickListener {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.group_item)
    ViewGroup groupItem;
    @BindView(R.id.group_competitor)
    ViewGroup groupCompetitor;
    @BindView(R.id.iv_match)
    RoundedImageView ivMatch;
    @BindView(R.id.iv_competitor)
    CircularImageView ivCompetitor;
    @BindView(R.id.tv_seq)
    TextView tvSeq;
    @BindView(R.id.tv_lose)
    TextView tvLose;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_level)
    TextView tvLevel;
    @BindView(R.id.tv_city)
    TextView tvCity;
    @BindView(R.id.tv_year)
    TextView tvYear;
    @BindView(R.id.tv_competitor)
    TextView tvCompetitor;
    @BindView(R.id.tv_score)
    TextView tvScore;
    
    private SubItem subItem;

    private boolean showCompetitor;
    private boolean hideSequence;
    private boolean showLose;
    private OnRecordItemListener onRecordItemListener;

    private RequestOptions matchOptions;
    private RequestOptions playerOptions;

    public SubItemAdapter(boolean showCompetitor, boolean hideSequence, boolean showLose
            , OnRecordItemListener onRecordItemListener) {
        this.showCompetitor = showCompetitor;
        this.hideSequence = hideSequence;
        this.showLose = showLose;
        this.onRecordItemListener = onRecordItemListener;
        matchOptions = GlideOptions.getDefaultMatchOptions();
        playerOptions = GlideOptions.getDefaultPlayerOptions();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.adapter_glory_list_item;
    }

    @Override
    public void onBindViews(View root) {
        ButterKnife.bind(this, root);
    }

    @Override
    public void onSetViews() {

    }

    @Override
    public void onUpdateViews(Object model, int position) {
        super.onUpdateViews(model, position);
        subItem = (SubItem) model;
        Record record = subItem.getRecord();
        tvCity.setText(record.getMatch().getMatchBean().getCountry() + "/" + record.getMatch().getMatchBean().getCity());
        tvLevel.setText(record.getMatch().getMatchBean().getLevel());
        tvName.setText(record.getMatch().getName());
        tvYear.setText(record.getDateStr());

        // list是倒序排列的
        if (hideSequence) {
            tvSeq.setVisibility(View.GONE);
        }
        else {
            tvSeq.setVisibility(View.VISIBLE);
            tvSeq.setText(String.valueOf(subItem.getGroupCount() - subItem.getItemPosition()));
        }

        Glide.with(ivMatch.getContext())
                .load(ImageProvider.getMatchHeadPath(record.getMatch().getName(), record.getMatch().getMatchBean().getCourt()))
                .apply(matchOptions)
                .into(ivMatch);

        groupItem.setTag(position);
        groupItem.setOnClickListener(this);

        if (showCompetitor) {
            CompetitorBean competitor = CompetitorParser.getCompetitorFrom(record);
            groupCompetitor.setVisibility(View.VISIBLE);
            tvCompetitor.setText(competitor.getNameChn() + "(" + competitor.getCountry() + ")");
            tvScore.setText(ScoreParser.getScoreText(record.getScoreList(), record.getWinnerFlag(), record.getRetireFlag()));

            Glide.with(ivCompetitor.getContext())
                    .load(ImageProvider.getPlayerHeadPath(competitor.getNameChn()))
                    .apply(playerOptions)
                    .into(ivCompetitor);

        }
        else {
            groupCompetitor.setVisibility(View.GONE);
        }

//        if (showTitle) {
//            tvTitle.setVisibility(View.VISIBLE);
//            tvTitle.setText(titleList.get(position));
//        }
//        else {
//            tvTitle.setVisibility(View.GONE);
//        }
        tvTitle.setVisibility(View.GONE);

        if (showLose && record.getWinnerFlag() == AppConstants.WINNER_COMPETITOR) {
            tvLose.setVisibility(View.VISIBLE);
        }
        else {
            tvLose.setVisibility(View.GONE);
        }
    }

    @Override
    public void onExpansionToggled(boolean expanded) {

    }

    @Override
    public void onClick(View v) {
        if (onRecordItemListener != null) {
            onRecordItemListener.onClickRecord(subItem.getRecord());
        }
    }
}
