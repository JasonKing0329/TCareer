package com.king.app.tcareer.page.record.list;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.king.app.tcareer.R;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.CompetitorParser;
import com.king.app.tcareer.model.GlideOptions;
import com.king.app.tcareer.model.ImageProvider;
import com.king.app.tcareer.model.ScoreParser;
import com.king.app.tcareer.model.bean.CompetitorBean;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.utils.DebugLog;
import com.king.app.tcareer.view.widget.CircleImageView;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomMenuButton;
import com.zaihuishou.expandablerecycleradapter.viewholder.AbstractExpandableAdapterItem;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/4/21 16:15
 */
public class RecordItemAdapter extends AbstractExpandableAdapterItem implements View.OnClickListener
    , OnBMClickListener {

    private CircleImageView ivPlayer;
    private TextView tvPlayer;
    private TextView tvRankSeed;
    private TextView tvRound;
    private TextView tvScore;
    private ViewGroup groupRecord;
    private BoomMenuButton bmbMenu;

    private OnItemMenuListener onItemMenuListener;
    
    private RecordItem curRecordItem;

    private RequestOptions playerOptions;

    public RecordItemAdapter(OnItemMenuListener onItemMenuListener) {
        this.onItemMenuListener = onItemMenuListener;
        playerOptions = GlideOptions.getDefaultPlayerOptions();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.adapter_record_sub_item;
    }

    @Override
    public void onBindViews(View root) {
        groupRecord = (ViewGroup) root.findViewById(R.id.group_record);
        ivPlayer = (CircleImageView) root.findViewById(R.id.iv_player);
        tvPlayer = (TextView) root.findViewById(R.id.tv_player);
        tvRankSeed = (TextView) root.findViewById(R.id.tv_rank_seed);
        tvRound = (TextView) root.findViewById(R.id.tv_round);
        tvScore = (TextView) root.findViewById(R.id.tv_score);
        bmbMenu = (BoomMenuButton) root.findViewById(R.id.bmb_menu);
    }

    @Override
    public void onSetViews() {
        int padding = bmbMenu.getContext().getResources().getDimensionPixelSize(R.dimen.boom_menu_icon_padding);
        bmbMenu.addBuilder(new HamButton.Builder().normalTextRes(R.string.record_longclick_update)
            .normalImageRes(R.drawable.ic_edit_location_white_24dp)
                .imagePadding(new Rect(padding, padding, padding, padding))
                .listener(this));
        bmbMenu.addBuilder(new HamButton.Builder().normalTextRes(R.string.record_longclick_delete)
                .normalImageRes(R.drawable.ic_delete_white_24dp)
                .imagePadding(new Rect(padding, padding, padding, padding))
                .listener(this));
    }

    @Override
    public void onUpdateViews(Object model, int position) {
        super.onUpdateViews(model, position);
        DebugLog.e("position=" + position);
        curRecordItem = (RecordItem) model;
        curRecordItem.setItemPosition(position);
        Record curRecord = curRecordItem.getRecord();
        CompetitorBean competitor = CompetitorParser.getCompetitorFrom(curRecord);
        tvPlayer.setText(competitor.getNameChn());
        tvRankSeed.setText("(".concat(String.valueOf(curRecord.getRankCpt())).concat("/")
            .concat(String.valueOf(curRecord.getSeedpCpt()).concat(")")));
        tvRound.setText(AppConstants.getRoundShortName(curRecord.getRound()));

        String score = ScoreParser.getScoreText(curRecord.getScoreList(), curRecord.getWinnerFlag(), curRecord.getRetireFlag());
        if (AppConstants.WINNER_USER == curRecord.getWinnerFlag()) {
            String winner = curRecord.getUser().getNameShort();
            tvScore.setText(winner + "  " + score);
        }
        else {
            String winner = competitor instanceof User ? ((User) competitor).getNameShort():competitor.getNameChn();
            tvScore.setText(winner + "  " + score);
        }
        groupRecord.setOnClickListener(this);

        String path = ImageProvider.getPlayerHeadPath(competitor.getNameChn());
        Glide.with(ivPlayer.getContext())
                .load(path)
                .apply(playerOptions)
                .into(ivPlayer);
    }

    @Override
    public void onExpansionToggled(boolean expanded) {

    }

    @Override
    public void onClick(View view) {
        onItemMenuListener.onItemClicked(view, curRecordItem);
    }

    @Override
    public void onBoomButtonClick(int index) {
        switch (index) {
            case 0:
                onItemMenuListener.onUpdateRecord(curRecordItem);
                break;
            case 1:
                onItemMenuListener.onDeleteRecord(curRecordItem);
                break;
        }
    }
}
