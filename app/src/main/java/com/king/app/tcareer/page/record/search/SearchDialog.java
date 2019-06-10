package com.king.app.tcareer.page.record.search;

import android.app.DatePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.RadioGroup;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.IFragmentHolder;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.databinding.DialogRecordFilterBinding;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.view.dialog.frame.FrameContentFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/2/7 14:10
 */
public class SearchDialog extends FrameContentFragment<DialogRecordFilterBinding, SearchViewModel> implements CompoundButton.OnCheckedChangeListener {

    private OnRecordFilterListener onRecordFilterListener;

    private List<Record> mRecordList;

    private String[] arr_round, arr_level, arr_court, arr_region;
    private String[] arr_year, arr_month;

    private int nYear_start, nMonth_start, nDay_start;
    private int nYear_end, nMonth_end, nDay_end;
    private boolean isDateStartChanged = false, isDateEndChanged = false;
    private int cur_round = 0, cur_level = 0, cur_court = 0, cur_region = 0;

    private SearchBean searchBean;

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {
        
    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.dialog_record_filter;
    }

    @Override
    protected SearchViewModel createViewModel() {
        return ViewModelProviders.of(this).get(SearchViewModel.class);
    }

    @Override
    protected void onCreate(View view) {

        searchBean = new SearchBean();

        mBinding.tvSearch.setOnClickListener(v -> onSave());
        loadSpinnerArray();
        initView(view);
    }

    @Override
    protected void onCreateData() {
        mModel.searchResultObserver.observe(this, list -> {
            if (onRecordFilterListener != null) {
                onRecordFilterListener.recordFiltered(list);
            }
        });
    }

    private void loadSpinnerArray() {

        arr_court = AppConstants.RECORD_MATCH_COURTS;
        arr_level = AppConstants.RECORD_MATCH_LEVELS;
        arr_region = getContext().getResources().getStringArray(R.array.spinner_region);
        arr_round = AppConstants.RECORD_MATCH_ROUNDS;
        arr_month = new String[12];
        for (int i = 0; i < 12; ) {
            if (i < 9)
                arr_month[i] = "0" + (++i);
            else
                arr_month[i] = "" + (++i);
        }
        arr_year = new String[20];
        for (int n = 0; n < 20; n++) {
            arr_year[n] = "" + (n + 2010);
        }
    }

    private void initView(View view) {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item
                , arr_round);
        //或spinnerAdapter = ArrayAdapter.createFromResource(userActivity, R.array.spinner_round, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBinding.spRound.setAdapter(spinnerAdapter);
        mBinding.spRound.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3) {
                cur_round = pos;
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
        spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item
                , arr_court);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBinding.spCourt.setAdapter(spinnerAdapter);
        mBinding.spCourt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3) {
                cur_court = pos;
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
        spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item
                , arr_level);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBinding.spLevel.setAdapter(spinnerAdapter);
        mBinding.spLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3) {
                cur_level = pos;
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
        spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item
                , arr_region);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBinding.spRegion.setAdapter(spinnerAdapter);
        mBinding.spRegion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3) {
                cur_region = pos;
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        mBinding.btnDateStart.setOnClickListener(view1 -> {
            Calendar calen = Calendar.getInstance();
            nYear_start = calen.get(Calendar.YEAR);
            nMonth_start = calen.get(Calendar.MONTH);

            DatePickerDialog startDlg = new DatePickerDialog(getContext(),
                    startDateLis, nYear_start, nMonth_start, 1);
            startDlg.show();
        });
        mBinding.btnDateEnd.setOnClickListener(view12 -> {
            Calendar calen = Calendar.getInstance();
            nYear_end = calen.get(Calendar.YEAR);
            nMonth_end = calen.get(Calendar.MONTH);

            DatePickerDialog endDlg = new DatePickerDialog(getContext(),
                    endDateLis, nYear_end, nMonth_end, 28);
            endDlg.show();
        });

        mBinding.rgWinLose.setOnCheckedChangeListener(rgoutListener);
        mBinding.cbCourt.setOnCheckedChangeListener(this);
        mBinding.cbLevel.setOnCheckedChangeListener(this);
        mBinding.cbMatch.setOnCheckedChangeListener(this);
        mBinding.cbRound.setOnCheckedChangeListener(this);
        mBinding.cbRegion.setOnCheckedChangeListener(this);
        mBinding.cbMatchCountry.setOnCheckedChangeListener(this);
        mBinding.cbPlayer.setOnCheckedChangeListener(this);
        mBinding.cbPlayerCountry.setOnCheckedChangeListener(this);
        mBinding.cbPlayerRank.setOnCheckedChangeListener(this);
        mBinding.cbDate.setOnCheckedChangeListener(this);
        mBinding.cbScore.setOnCheckedChangeListener(this);
        mBinding.cbWinLose.setOnCheckedChangeListener(this);
        mBinding.cbSql.setOnCheckedChangeListener(this);

    }

    RadioGroup.OnCheckedChangeListener rgoutListener = new RadioGroup.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {

//			int radioID = group.getCheckedRadioButtonId();
//			RadioButton radio = (RadioButton) userActivity.findViewById(radioID);
            if (checkedId == mBinding.radioWin.getId()) {
                searchBean.setWinner(true);
            } else {
                searchBean.setWinner(false);
            }
        }

    };

    DatePickerDialog.OnDateSetListener startDateLis = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            nYear_start = year;
            nMonth_start = monthOfYear + 1;//日期控件的月份是从0开始编号的
            nDay_start = dayOfMonth;
            mBinding.btnDateStart.setText(new StringBuilder().append(nYear_start)
                    .append("/").append(nMonth_start).append("/").append(nDay_start));
            isDateStartChanged = true;

        }

    };
    DatePickerDialog.OnDateSetListener endDateLis = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            nYear_end = year;
            nMonth_end = monthOfYear + 1;//日期控件的月份是从0开始编号的
            nDay_end = dayOfMonth;
            mBinding.btnDateEnd.setText(new StringBuilder().append(nYear_end)
                    .append("/").append(nMonth_end).append("/").append(nDay_end));
            isDateEndChanged = true;
        }

    };

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == mBinding.cbCourt) {
            if (isChecked) {
                mBinding.spCourt.setVisibility(View.VISIBLE);
                searchBean.setCourtOn(true);
            } else {
                mBinding.spCourt.setVisibility(View.INVISIBLE);
                searchBean.setCourtOn(false);
            }
        } else if (buttonView == mBinding.cbLevel) {
            if (isChecked) {
                mBinding.spLevel.setVisibility(View.VISIBLE);
                searchBean.setLevelOn(true);
            } else {
                mBinding.spLevel.setVisibility(View.INVISIBLE);
                searchBean.setLevelOn(false);
            }
        } else if (buttonView == mBinding.cbMatch) {
            if (isChecked) {
                mBinding.etMatch.setVisibility(View.VISIBLE);
                searchBean.setMatchOn(true);
            } else {
                mBinding.etMatch.setVisibility(View.INVISIBLE);
                searchBean.setMatchOn(false);
            }
        } else if (buttonView == mBinding.cbRound) {
            if (isChecked) {
                mBinding.spRound.setVisibility(View.VISIBLE);
                searchBean.setRoundOn(true);
            } else {
                mBinding.spRound.setVisibility(View.INVISIBLE);
                searchBean.setRoundOn(false);
            }
        } else if (buttonView == mBinding.cbRegion) {
            if (isChecked) {
                mBinding.spRegion.setVisibility(View.VISIBLE);
                searchBean.setRegionOn(true);
            } else {
                mBinding.spRegion.setVisibility(View.INVISIBLE);
                searchBean.setRegionOn(false);
            }
        } else if (buttonView == mBinding.cbMatchCountry) {
            if (isChecked) {
                mBinding.etMatchCountry.setVisibility(View.VISIBLE);
                searchBean.setMatchCountryOn(true);
            } else {
                mBinding.etMatchCountry.setVisibility(View.INVISIBLE);
                searchBean.setMatchCountryOn(false);
            }
        } else if (buttonView == mBinding.cbPlayer) {
            if (isChecked) {
                mBinding.etPlayer.setVisibility(View.VISIBLE);
                searchBean.setCompetitorOn(true);
            } else {
                mBinding.etPlayer.setVisibility(View.INVISIBLE);
                searchBean.setCompetitorOn(false);
            }
        } else if (buttonView == mBinding.cbPlayerCountry) {
            if (isChecked) {
                mBinding.etPlayerCountry.setVisibility(View.VISIBLE);
                searchBean.setCptCountryOn(true);
            } else {
                mBinding.etPlayerCountry.setVisibility(View.INVISIBLE);
                searchBean.setCptCountryOn(false);
            }
        } else if (buttonView == mBinding.cbPlayerRank) {
            if (isChecked) {
                mBinding.etRankMin.setVisibility(View.VISIBLE);
                mBinding.etRankMax.setVisibility(View.VISIBLE);
                searchBean.setRankOn(true);
            } else {
                mBinding.etRankMin.setVisibility(View.INVISIBLE);
                mBinding.etRankMax.setVisibility(View.INVISIBLE);
                searchBean.setRankOn(false);
            }
        } else if (buttonView == mBinding.cbDate) {
            if (isChecked) {
                mBinding.btnDateStart.setVisibility(View.VISIBLE);
                mBinding.btnDateEnd.setVisibility(View.VISIBLE);
                searchBean.setDateOn(true);
            } else {
                mBinding.btnDateStart.setVisibility(View.INVISIBLE);
                mBinding.btnDateEnd.setVisibility(View.INVISIBLE);
                searchBean.setDateOn(false);
            }
        } else if (buttonView == mBinding.cbScore) {
            if (isChecked) {
                mBinding.etScoreCpt.setVisibility(View.VISIBLE);
                mBinding.etScoreUser.setVisibility(View.VISIBLE);
                mBinding.cbScoreEach.setVisibility(View.VISIBLE);
                searchBean.setScoreOn(true);
            } else {
                mBinding.etScoreCpt.setVisibility(View.INVISIBLE);
                mBinding.etScoreUser.setVisibility(View.INVISIBLE);
                mBinding.cbScoreEach.setVisibility(View.INVISIBLE);
                searchBean.setScoreOn(false);
            }
        } else if (buttonView == mBinding.cbWinLose) {
            if (isChecked) {
                searchBean.setWinnerOn(true);
                mBinding.radioLose.setVisibility(View.VISIBLE);
                mBinding.radioWin.setVisibility(View.VISIBLE);
                mBinding.radioWin.setChecked(true);
                //由于监听器里调用setWinner，如果不调用该句以及下一句的话，而且用户只是在
                //checkbox打上勾但是没有点win的话，就一直没有执行setWinner(true).
                searchBean.setWinner(true);
            } else {
                searchBean.setWinnerOn(false);
                mBinding.radioLose.setVisibility(View.INVISIBLE);
                mBinding.radioWin.setVisibility(View.INVISIBLE);
            }
        } else if (buttonView == mBinding.cbSql) {
            if (isChecked) {
                mBinding.etWhere.setVisibility(View.VISIBLE);
                mBinding.etWhereValue.setVisibility(View.VISIBLE);
                mBinding.tvHintTable.setVisibility(View.VISIBLE);
//                    if (mBinding.tvHintTable.getText() == null || mBinding.tvHintTable.getText().length() == 0) {
//                        StringBuffer buffer = new StringBuffer();
//                        for (String col : DatabaseStruct.TABLE_RECORD_COL) {
//                            buffer.append(",").append(col);
//                        }
//                        mBinding.tvHintTable.setText("Table record:\n" + buffer.toString().substring(1));
//                    }
                setStateEnable(false);
            } else {
                mBinding.etWhere.setVisibility(View.GONE);
                mBinding.etWhereValue.setVisibility(View.GONE);
                mBinding.tvHintTable.setVisibility(View.GONE);
                setStateEnable(true);
            }
        }

    }

    private void setStateEnable(boolean b) {
        if (!b) {
            mBinding.cbPlayer.setChecked(false);
            mBinding.cbPlayerCountry.setChecked(false);
            mBinding.cbPlayerRank.setChecked(false);
            mBinding.cbCourt.setChecked(false);
            mBinding.cbDate.setChecked(false);
            mBinding.cbLevel.setChecked(false);
            mBinding.cbMatch.setChecked(false);
            mBinding.cbMatchCountry.setChecked(false);
            mBinding.cbRegion.setChecked(false);
            mBinding.cbRound.setChecked(false);
            mBinding.cbScore.setChecked(false);
            mBinding.cbWinLose.setChecked(false);
        }
        mBinding.cbPlayer.setEnabled(b);
        mBinding.cbPlayerCountry.setEnabled(b);
        mBinding.cbPlayerRank.setEnabled(b);
        mBinding.cbCourt.setEnabled(b);
        mBinding.cbDate.setEnabled(b);
        mBinding.cbLevel.setEnabled(b);
        mBinding.cbMatch.setEnabled(b);
        mBinding.cbMatchCountry.setEnabled(b);
        mBinding.cbRegion.setEnabled(b);
        mBinding.cbRound.setEnabled(b);
        mBinding.cbScore.setEnabled(b);
        mBinding.cbWinLose.setEnabled(b);
    }

    //除了radioGroup决定的win/lose在其监听接口里组装，其他都在这里统一组装
    private void orgnizeData() {
        if (mBinding.cbPlayer.isChecked()) {
            String competitor = mBinding.etPlayer.getText().toString();
            searchBean.setCompetitor(competitor);
        }
        if (mBinding.cbPlayerCountry.isChecked()) {
            String country = mBinding.etPlayerCountry.getText().toString();
            searchBean.setCptCountry(country);
        }
        if (mBinding.cbMatchCountry.isChecked()) {
            String country = mBinding.etMatchCountry.getText().toString();
            searchBean.setMatchCountry(country);
        }
        if (mBinding.cbCourt.isChecked()) {
            String court = arr_court[cur_court];
            searchBean.setCourt(court);
        }
        if (mBinding.cbLevel.isChecked()) {
            String level = arr_level[cur_level];
            searchBean.setLevel(level);
        }
        if (mBinding.cbRound.isChecked()) {
            String round = arr_round[cur_round];
            searchBean.setRound(round);
        }
        if (mBinding.cbRegion.isChecked()) {
            String region = arr_region[cur_region];
            searchBean.setRegion(region);
        }
        if (mBinding.cbMatch.isChecked()) {
            String match = mBinding.etMatch.getText().toString();
            searchBean.setMatch(match);
        }
        if (mBinding.cbPlayerRank.isChecked()) {

            String str = mBinding.etRankMin.getText().toString();
            int min = 0, max = 100000;
            try {
                min = Integer.parseInt(str);
            } catch (Exception e) {
                min = 0;
            }

            str = mBinding.etRankMax.getText().toString();
            try {
                max = Integer.parseInt(str);
            } catch (Exception e) {
                max = 100000;
            }

            searchBean.setRankMin(min);
            searchBean.setRankMax(max);
        }
        if (mBinding.cbDate.isChecked()) {

            long date_start = 0, date_end = System.currentTimeMillis();
            if (isDateStartChanged) {
                date_start = formatDate(nYear_start, nMonth_start, nDay_start);
            }
            if (isDateEndChanged) {
                date_end = formatDate(nYear_end, nMonth_end, nDay_end);
            }
            searchBean.setDate_start(date_start);
            searchBean.setDate_end(date_end);
        }

        if (mBinding.cbScore.isChecked()) {
            try {
                searchBean.setScoreEachOther(mBinding.cbScoreEach.isChecked());
                searchBean.setScoreUser(Integer.parseInt(mBinding.etScoreUser.getText().toString()));
                searchBean.setScoreCpt(Integer.parseInt(mBinding.etScoreCpt.getText().toString()));
            } catch (Exception e) {
            }
        }
    }

    /**
     * 按比分搜索可以对输入比分按照and、or、&、|四个字符及字符串进行拆分，可以实现一个条件
     * 多个关键字
     */
    private long formatDate(int year, int month, int day) {
        long date;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");//天啊！MM一定要大写！！！
        String date_str;
        if (month < 10)
            date_str = "" + year + "0" + month;
        else
            date_str = "" + year + month;
        if (day < 10)
            date_str += ("0" + day);
        else
            date_str += day;
        try {
            date = sdf.parse(date_str).getTime();
        } catch (ParseException e) {
            date = System.currentTimeMillis();
        }
        return date;
    }

    public boolean onSave() {
        if (mBinding.cbSql.isChecked()) {
            String where = mBinding.etWhere.getText().toString();
            String valueString = mBinding.etWhereValue.getText().toString();
            String[] values = null;
            if (valueString != null) {
                values = valueString.split(",");
            }
            if (onRecordFilterListener != null) {
//                    onRecordFilterListener.recordFiltered(new RecordService().queryByWhere(where, values));
            }
        } else {
            orgnizeData();
            mModel.searchFrom(mRecordList, searchBean);
        }
        return false;
    }

    public void setOnRecordFilterListener(OnRecordFilterListener onRecordFilterListener) {
        this.onRecordFilterListener = onRecordFilterListener;
    }

    public void setRecordList(List<Record> mRecordList) {
        this.mRecordList = mRecordList;
    }

    public interface OnRecordFilterListener {
        void recordFiltered(List<Record> list);
    }
}
