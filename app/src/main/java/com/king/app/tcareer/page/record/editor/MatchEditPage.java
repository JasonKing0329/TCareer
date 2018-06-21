package com.king.app.tcareer.page.record.editor;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.king.app.tcareer.R;
import com.king.app.tcareer.base.BaseActivity;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.GlideOptions;
import com.king.app.tcareer.model.ImageProvider;
import com.king.app.tcareer.model.ScoreParser;
import com.king.app.tcareer.model.bean.AutoFillMatchBean;
import com.king.app.tcareer.model.bean.CompetitorBean;
import com.king.app.tcareer.model.db.entity.MatchNameBean;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.model.db.entity.Score;
import com.king.app.tcareer.page.setting.SettingProperty;
import com.king.app.tcareer.utils.ListUtil;
import com.king.app.tcareer.view.adapter.BaseRecyclerAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/3/13 13:22
 */
public class MatchEditPage implements View.OnClickListener {

    private IEditorHolder holder;

    private ImageView ivChangeMatch;
    private ImageView ivMatch;
    private RecyclerView rvRecentMatches;
    private ViewGroup groupMatch;
    private ViewGroup groupScore;
    private ViewGroup groupWinner;
    private String[] arr_round;
    private String[] arr_year;
    private TextView tvMatch, tvMatchCountry, tvMatchLevel, tvMatchCity, tvMatchCourt;
    private TextView tvWinner, tvScore;
    protected Spinner sp_year, sp_round;
    protected int cur_year = 2, cur_round = 0;// 记录当前spinner选项

    private SpinnerListener spinnerListener;

    private String mStrWinner;
    private String mStrScore;

    private RecentMatchAdapter recentMatchAdapter;

    public MatchEditPage(IEditorHolder holder) {
        this.holder = holder;
        spinnerListener = new SpinnerListener();
        arr_round = AppConstants.RECORD_MATCH_ROUNDS;
        arr_year = new String[20];
        for (int n = 0; n < 20; n++) {
            arr_year[n] = "" + (n + 2010);
        }
    }

    public void initView() {

        ArrayAdapter<String> spinnerAdapter;

        groupMatch = (ViewGroup) holder.getActivity().findViewById(R.id.editor_match_group);
        groupScore = (ViewGroup) holder.getActivity().findViewById(R.id.editor_score_group);
        groupScore.setOnClickListener(this);
        groupWinner = (ViewGroup) holder.getActivity().findViewById(R.id.editor_group_winner);
        tvMatch = (TextView) holder.getActivity().findViewById(R.id.editor_match_name);
        tvMatchCountry = (TextView) holder.getActivity().findViewById(R.id.editor_match_country);
        tvMatchCity = (TextView) holder.getActivity().findViewById(R.id.editor_match_city);
        tvWinner = (TextView) holder.getActivity().findViewById(R.id.editor_match_winner);
        tvScore = (TextView) holder.getActivity().findViewById(R.id.editor_match_score);
        tvWinner.setOnClickListener(this);
        tvScore.setOnClickListener(this);
        ivChangeMatch = (ImageView) holder.getActivity().findViewById(R.id.editor_match_change);
        ivChangeMatch.setOnClickListener(this);
        ivMatch = (ImageView) holder.getActivity().findViewById(R.id.editor_match_image);
        tvMatchCourt = (TextView) holder.getActivity().findViewById(R.id.editor_match_court);
        tvMatchLevel = (TextView) holder.getActivity().findViewById(R.id.editor_match_level);
        sp_year = (Spinner) holder.getActivity().findViewById(R.id.editor_match_year);
        sp_round = (Spinner) holder.getActivity().findViewById(R.id.editor_match_round);
        rvRecentMatches = holder.getActivity().findViewById(R.id.rv_recent_matches);

        spinnerAdapter = new ArrayAdapter<String>(holder.getActivity(),
                android.R.layout.simple_spinner_item, arr_year);
        spinnerAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_item);
        sp_year.setAdapter(spinnerAdapter);
        sp_year.setOnItemSelectedListener(spinnerListener);
        spinnerAdapter = new ArrayAdapter<String>(holder.getActivity(),
                android.R.layout.simple_spinner_item, arr_round);
        spinnerAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_round.setAdapter(spinnerAdapter);
        sp_round.setOnItemSelectedListener(spinnerListener);

        rvRecentMatches.setLayoutManager(new LinearLayoutManager(holder.getActivity(), LinearLayoutManager.HORIZONTAL, false));
    }

    public void initData() {
        holder.getPresenter().initMatchPage();

        // 新增record显示最近操作的赛事
        if (holder.getPresenter().isEditMode()) {
            rvRecentMatches.setVisibility(View.GONE);
        }
        else {
            holder.getPresenter().loadRecentMatches();
        }
    }

    public void showMatchFill(int year, String round) {
        cur_year = getYearIndex(year);
        sp_year.setSelection(cur_year);
        cur_round = getRoundIndex(round);
        sp_round.setSelection(cur_round);
    }

    public void showMatchAutoFill(AutoFillMatchBean autoFill) {
        cur_year = autoFill.getIndexYear();
        sp_year.setSelection(cur_year);
        cur_round = getRoundIndex(autoFill.getRound());
        sp_round.setSelection(cur_round);
    }

    public void showMatchInfor(Record record, MatchNameBean mMatchNameBean, CompetitorBean mCompetitor, List<Score> mScoreList) {
        onMatchSelected(mMatchNameBean);

        if (record != null) {
            mStrScore = ScoreParser.getScoreText(record.getScoreList()
                    , record.getWinnerFlag(), record.getRetireFlag());
            mStrWinner = record.getWinnerFlag() == AppConstants.WINNER_USER ?
                    record.getUser().getNameShort()
                    : mCompetitor.getNameChn();
            tvScore.setText(mStrScore);
            tvWinner.setText(mStrWinner);
            groupWinner.setVisibility(View.VISIBLE);

            for (int i = 0; i < arr_year.length; i++) {
                String year = arr_year[i];
                if (year.equals(record.getDateStr().split("-")[0])) {
                    sp_year.setSelection(i);
                    break;
                }
            }
            sp_round.setSelection(getRoundIndex(record.getRound()));
        }
    }

    /**
     * 已添加完一个记录后继续添加保留上次添加的赛事信息，清空winner和score
     */
    public void reset() {
        tvWinner.setText("");
        tvScore.setText("");
        mStrScore = "";
        mStrWinner = "";
        groupWinner.setVisibility(View.INVISIBLE);
    }

    private int getYearIndex(int year) {
        for (int i = 0; i < arr_year.length; i++) {
            if (arr_year[i].equals(String.valueOf(year))) {
                return i;
            }
        }
        return 0;
    }

    private int getRoundIndex(String round) {
        for (int i = 0; i < arr_round.length; i++) {
            if (arr_round[i].equals(round)) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public void onClick(View v) {
        if (v == ivChangeMatch) {
            // 回调在onMatchSelected
            holder.selectMatch();
        } else if (v == groupScore) {
            if (holder.getPresenter().getCompetitor() == null) {
                ((BaseActivity) holder.getActivity()).showMessageShort("还没有选择对手");
                return;
            }
            RecordScoreDialog recordScoreDialog = new RecordScoreDialog();
            recordScoreDialog.setCompetitor(holder.getPresenter().getCompetitor());
            recordScoreDialog.setUser(holder.getPresenter().getUser());
            recordScoreDialog.setRecord(holder.getPresenter().getRecord());
            recordScoreDialog.setScoreList(holder.getPresenter().getScoreList());
            recordScoreDialog.setOnScoreListener(new RecordScoreDialog.OnScoreListener() {
                @Override
                public void onCompleteScore(List<Score> scoreList, int retireFlag, int winnerFlag) {

                    // 暂存score信息
                    holder.getPresenter().holdScore(scoreList, retireFlag, winnerFlag);

                    mStrScore = ScoreParser.getScoreText(scoreList, winnerFlag, retireFlag);
                    tvScore.setText(mStrScore);
                    mStrWinner = winnerFlag == AppConstants.WINNER_USER ?
                            holder.getPresenter().getUser().getNameShort()
                            : holder.getPresenter().getCompetitor().getNameChn();
                    tvWinner.setText(mStrWinner);
                    groupWinner.setVisibility(View.VISIBLE);
                }
            });
            recordScoreDialog.show(holder.getSupportFragmentManager(), "RecordScoreDialog");
        }
    }

    /**
     * selectMatch 回调
     *
     * @param bean
     */
    public void onMatchSelected(MatchNameBean bean) {
        if (bean == null) {
            return;
        }
        groupMatch.setVisibility(View.VISIBLE);
        tvMatch.setText(bean.getName());
        tvMatchCountry.setText(bean.getMatchBean().getCountry());
        tvMatchLevel.setText(bean.getMatchBean().getLevel());
        tvMatchCourt.setText(bean.getMatchBean().getCourt());
        tvMatchCity.setText(bean.getMatchBean().getCity());

        Glide.with(holder.getActivity())
                .load(ImageProvider.getMatchHeadPath(bean.getName(), bean.getMatchBean().getCourt()))
                .apply(GlideOptions.getEditorMatchOptions())
                .into(ivMatch);
    }

    public String fillRecord() {
        if (holder.getPresenter().getMatchNameBean() == null) {
            return holder.getActivity().getString(R.string.editor_null_match);
        }
        if (ListUtil.isEmpty(holder.getPresenter().getScoreList())
                && holder.getPresenter().getRecord().getRetireFlag() != AppConstants.RETIRE_WO) {
            return holder.getActivity().getString(R.string.editor_null_match);
        }

        int year = 2010 + cur_year;
        holder.getPresenter().fillMatchPage(arr_round[cur_round], year);
        return null;
    }

    /**
     * 保存为默认填写
     */
    public void saveAutoFill() {
        holder.getPresenter().saveAutoFill(cur_year, arr_round[cur_round]);
        // 新增模式下保存为最近操作赛事，编辑模式下不保存
        if (!holder.getPresenter().isEditMode()) {
            holder.getPresenter().saveAsRecentMatch();
        }
    }

    public void showRecentMatches(List<MatchNameBean> matches) {
        if (recentMatchAdapter == null) {
            recentMatchAdapter = new RecentMatchAdapter();
            recentMatchAdapter.setList(matches);
            recentMatchAdapter.setOnItemClickListener((position, data) -> {
                holder.getPresenter().updateMatchNameBean(data);
                onMatchSelected(data);
            });
            rvRecentMatches.setAdapter(recentMatchAdapter);
        }
        else {
            recentMatchAdapter.setList(matches);
            recentMatchAdapter.notifyDataSetChanged();
        }
    }

    private class SpinnerListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {
            if (parent == sp_year) {
                cur_year = position;
            } else if (parent == sp_round) {
                cur_round = position;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }

    }
}
