package com.king.app.tcareer.page.record.search;

import android.app.DatePickerDialog;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.IFragmentHolder;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.view.dialog.DraggableDialogFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/2/7 14:10
 */
public class SearchDialog extends DraggableDialogFragment implements SearchHolder {

    private SearchFragment searchFragment;

    private OnRecordFilterListener onRecordFilterListener;

    private List<Record> mRecordList;

    @Override
    protected View getToolbarView(ViewGroup groupToolbar) {
        requestOkAction();
        requestCloseAction();
        setTitle("Search");
        return null;
    }

    @Override
    protected Fragment getContentViewFragment() {
        searchFragment = new SearchFragment();
        searchFragment.setRecordList(mRecordList);
        searchFragment.setOnRecordFilterListener(onRecordFilterListener);
        return searchFragment;
    }

    @Override
    protected boolean onClickOk() {
        return searchFragment.onSave();
    }

    public void setOnRecordFilterListener(OnRecordFilterListener onRecordFilterListener) {
        this.onRecordFilterListener = onRecordFilterListener;
    }

    public void setRecordList(List<Record> mRecordList) {
        this.mRecordList = mRecordList;
    }

    public static class SearchFragment extends ContentFragment implements CompoundButton.OnCheckedChangeListener
        , SearchView{

        private String[] arr_round, arr_level, arr_court, arr_region;
        private String[] arr_year, arr_month;
        private CheckBox cbox_court, cbox_level, cbox_match, cbox_round, cbox_region, cbox_matchcountry, cbox_comp, cbox_compcountry, cbox_comprank, cbox_date, cbox_score, cbox_winlose, cbox_sql;
        private EditText et_match, et_comp, et_compcountry, et_comprank_min, et_comprank_max, et_matchcountry, et_score, et_where, et_wherevalue;
        private TextView hintTableText;
        private RadioGroup rgroup_winlose;
        private RadioButton radio_win, radio_lose;
        private Button button_date_start, button_date_end;
        private Spinner sp_court, sp_level, sp_region, sp_round;
        private ArrayAdapter<String> spinnerAdapter;
        private int nYear_start, nMonth_start, nDay_start;
        private int nYear_end, nMonth_end, nDay_end;
        private boolean isDateStartChanged = false, isDateEndChanged = false;
        private int cur_round = 0, cur_level = 0, cur_court = 0, cur_region = 0;

        private OnRecordFilterListener onRecordFilterListener;

        private SearchBean searchBean;

        private SearchPresenter presenter;

        private List<Record> mRecordList;

        private SearchHolder holder;

        @Override
        public void showLoading() {
            showProgress("loading...");
        }

        @Override
        public void dismissLoading() {
            dismissProgress();
        }

        @Override
        public void showConfirm(String message) {
            showConfirmMessage(message, null);
        }

        @Override
        public void showMessage(String message) {
            showMessageShort(message);
        }

        @Override
        protected int getContentLayoutRes() {
            return R.layout.dialog_record_filter;
        }

        @Override
        protected void onCreate(View view) {

            presenter = new SearchPresenter();
            presenter.onAttach(this);
            presenter.onCreate();
            searchBean = new SearchBean();

            loadSpinnerArray();
            initView(view);
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
            sp_court = (Spinner) view.findViewById(R.id.insert_spinner_court);
            sp_level = (Spinner) view.findViewById(R.id.insert_spinner_level);
            sp_region = (Spinner) view.findViewById(R.id.insert_spinner_region);
            sp_round = (Spinner) view.findViewById(R.id.insert_spinner_round);
            spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item
                    , arr_round);
            //或spinnerAdapter = ArrayAdapter.createFromResource(userActivity, R.array.spinner_round, android.R.layout.simple_spinner_item);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sp_round.setAdapter(spinnerAdapter);
            sp_round.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1,
                                           int pos, long arg3) {
                    cur_round = pos;
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {

                }
            });
            spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item
                    , arr_court);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sp_court.setAdapter(spinnerAdapter);
            sp_court.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1,
                                           int pos, long arg3) {
                    cur_court = pos;
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {

                }
            });
            spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item
                    , arr_level);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sp_level.setAdapter(spinnerAdapter);
            sp_level.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1,
                                           int pos, long arg3) {
                    cur_level = pos;
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {

                }
            });
            spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item
                    , arr_region);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sp_region.setAdapter(spinnerAdapter);
            sp_region.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1,
                                           int pos, long arg3) {
                    cur_region = pos;
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {

                }
            });

            et_match = (EditText) view.findViewById(R.id.search_et_match);
            et_comp = (EditText) view.findViewById(R.id.search_et_comp);
            et_compcountry = (EditText) view.findViewById(R.id.search_et_compcountry);
            et_comprank_min = (EditText) view.findViewById(R.id.search_rank_min);
            et_comprank_max = (EditText) view.findViewById(R.id.search_rank_max);
            et_matchcountry = (EditText) view.findViewById(R.id.search_et_matchcountry);
            et_score = (EditText) view.findViewById(R.id.search_et_score);
            et_where = (EditText) view.findViewById(R.id.search_et_sql1);
            et_wherevalue = (EditText) view.findViewById(R.id.search_et_sql2);
            button_date_start = (Button) view.findViewById(R.id.button_date_start);
            button_date_end = (Button) view.findViewById(R.id.button_date_end);
            button_date_start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Calendar calen = Calendar.getInstance();
                    nYear_start = calen.get(Calendar.YEAR);
                    nMonth_start = calen.get(Calendar.MONTH);

                    DatePickerDialog startDlg = new DatePickerDialog(getContext(),
                            startDateLis, nYear_start, nMonth_start, 1);
                    startDlg.show();
                }
            });
            button_date_end.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Calendar calen = Calendar.getInstance();
                    nYear_end = calen.get(Calendar.YEAR);
                    nMonth_end = calen.get(Calendar.MONTH);

                    DatePickerDialog endDlg = new DatePickerDialog(getContext(),
                            endDateLis, nYear_end, nMonth_end, 28);
                    endDlg.show();
                }
            });

            hintTableText = (TextView) view.findViewById(R.id.search_tv_hint_table);

            rgroup_winlose = (RadioGroup) view.findViewById(R.id.radioGroup_winlose);
            rgroup_winlose.setOnCheckedChangeListener(rgoutListener);
            radio_win = (RadioButton) view.findViewById(R.id.radio_win);
            radio_lose = (RadioButton) view.findViewById(R.id.radio_lose);

            cbox_court = (CheckBox) view.findViewById(R.id.cbox_court);
            cbox_court.setOnCheckedChangeListener(this);
            cbox_level = (CheckBox) view.findViewById(R.id.cbox_level);
            cbox_level.setOnCheckedChangeListener(this);
            cbox_match = (CheckBox) view.findViewById(R.id.cbox_match);
            cbox_match.setOnCheckedChangeListener(this);
            cbox_round = (CheckBox) view.findViewById(R.id.cbox_round);
            cbox_round.setOnCheckedChangeListener(this);
            cbox_region = (CheckBox) view.findViewById(R.id.cbox_region);
            cbox_region.setOnCheckedChangeListener(this);
            cbox_matchcountry = (CheckBox) view.findViewById(R.id.cbox_matchcountry);
            cbox_matchcountry.setOnCheckedChangeListener(this);
            cbox_comp = (CheckBox) view.findViewById(R.id.cbox_compname);
            cbox_comp.setOnCheckedChangeListener(this);
            cbox_compcountry = (CheckBox) view.findViewById(R.id.cbox_compcountry);
            cbox_compcountry.setOnCheckedChangeListener(this);
            cbox_comprank = (CheckBox) view.findViewById(R.id.cbox_comprank);
            cbox_comprank.setOnCheckedChangeListener(this);
            cbox_date = (CheckBox) view.findViewById(R.id.cbox_date);
            cbox_date.setOnCheckedChangeListener(this);
            cbox_score = (CheckBox) view.findViewById(R.id.cbox_score);
            cbox_score.setOnCheckedChangeListener(this);
            cbox_winlose = (CheckBox) view.findViewById(R.id.cbox_winorlose);
            cbox_winlose.setOnCheckedChangeListener(this);
            cbox_sql = (CheckBox) view.findViewById(R.id.cbox_sql);
            cbox_sql.setOnCheckedChangeListener(this);

        }

        RadioGroup.OnCheckedChangeListener rgoutListener = new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

//			int radioID = group.getCheckedRadioButtonId();
//			RadioButton radio = (RadioButton) userActivity.findViewById(radioID);
                if (checkedId == radio_win.getId()) {
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
                button_date_start.setText(new StringBuilder().append(nYear_start)
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
                button_date_end.setText(new StringBuilder().append(nYear_end)
                        .append("/").append(nMonth_end).append("/").append(nDay_end));
                isDateEndChanged = true;
            }

        };

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (buttonView == cbox_court) {
                if (isChecked) {
                    sp_court.setVisibility(View.VISIBLE);
                    searchBean.setCourtOn(true);
                } else {
                    sp_court.setVisibility(View.INVISIBLE);
                    searchBean.setCourtOn(false);
                }
            } else if (buttonView == cbox_level) {
                if (isChecked) {
                    sp_level.setVisibility(View.VISIBLE);
                    searchBean.setLevelOn(true);
                } else {
                    sp_level.setVisibility(View.INVISIBLE);
                    searchBean.setLevelOn(false);
                }
            } else if (buttonView == cbox_match) {
                if (isChecked) {
                    et_match.setVisibility(View.VISIBLE);
                    searchBean.setMatchOn(true);
                } else {
                    et_match.setVisibility(View.INVISIBLE);
                    searchBean.setMatchOn(false);
                }
            } else if (buttonView == cbox_round) {
                if (isChecked) {
                    sp_round.setVisibility(View.VISIBLE);
                    searchBean.setRoundOn(true);
                } else {
                    sp_round.setVisibility(View.INVISIBLE);
                    searchBean.setRoundOn(false);
                }
            } else if (buttonView == cbox_region) {
                if (isChecked) {
                    sp_region.setVisibility(View.VISIBLE);
                    searchBean.setRegionOn(true);
                } else {
                    sp_region.setVisibility(View.INVISIBLE);
                    searchBean.setRegionOn(false);
                }
            } else if (buttonView == cbox_matchcountry) {
                if (isChecked) {
                    et_matchcountry.setVisibility(View.VISIBLE);
                    searchBean.setMatchCountryOn(true);
                } else {
                    et_matchcountry.setVisibility(View.INVISIBLE);
                    searchBean.setMatchCountryOn(false);
                }
            } else if (buttonView == cbox_comp) {
                if (isChecked) {
                    et_comp.setVisibility(View.VISIBLE);
                    searchBean.setCompetitorOn(true);
                } else {
                    et_comp.setVisibility(View.INVISIBLE);
                    searchBean.setCompetitorOn(false);
                }
            } else if (buttonView == cbox_compcountry) {
                if (isChecked) {
                    et_compcountry.setVisibility(View.VISIBLE);
                    searchBean.setCptCountryOn(true);
                } else {
                    et_compcountry.setVisibility(View.INVISIBLE);
                    searchBean.setCptCountryOn(false);
                }
            } else if (buttonView == cbox_comprank) {
                if (isChecked) {
                    et_comprank_min.setVisibility(View.VISIBLE);
                    et_comprank_max.setVisibility(View.VISIBLE);
                    searchBean.setRankOn(true);
                } else {
                    et_comprank_min.setVisibility(View.INVISIBLE);
                    et_comprank_max.setVisibility(View.INVISIBLE);
                    searchBean.setRankOn(false);
                }
            } else if (buttonView == cbox_date) {
                if (isChecked) {
                    button_date_start.setVisibility(View.VISIBLE);
                    button_date_end.setVisibility(View.VISIBLE);
                    searchBean.setDateOn(true);
                } else {
                    button_date_start.setVisibility(View.INVISIBLE);
                    button_date_end.setVisibility(View.INVISIBLE);
                    searchBean.setDateOn(false);
                }
            } else if (buttonView == cbox_score) {
                if (isChecked) {
                    et_score.setVisibility(View.VISIBLE);
                    searchBean.setScoreOn(true);
                } else {
                    et_score.setVisibility(View.INVISIBLE);
                    searchBean.setScoreOn(false);
                }
            } else if (buttonView == cbox_winlose) {
                if (isChecked) {
                    searchBean.setWinnerOn(true);
                    radio_lose.setVisibility(View.VISIBLE);
                    radio_win.setVisibility(View.VISIBLE);
                    radio_win.setChecked(true);
                    //由于监听器里调用setWinner，如果不调用该句以及下一句的话，而且用户只是在
                    //checkbox打上勾但是没有点win的话，就一直没有执行setWinner(true).
                    searchBean.setWinner(true);
                } else {
                    searchBean.setWinnerOn(false);
                    radio_lose.setVisibility(View.INVISIBLE);
                    radio_win.setVisibility(View.INVISIBLE);
                }
            } else if (buttonView == cbox_sql) {
                if (isChecked) {
                    et_where.setVisibility(View.VISIBLE);
                    et_wherevalue.setVisibility(View.VISIBLE);
                    hintTableText.setVisibility(View.VISIBLE);
//                    if (hintTableText.getText() == null || hintTableText.getText().length() == 0) {
//                        StringBuffer buffer = new StringBuffer();
//                        for (String col : DatabaseStruct.TABLE_RECORD_COL) {
//                            buffer.append(",").append(col);
//                        }
//                        hintTableText.setText("Table record:\n" + buffer.toString().substring(1));
//                    }
                    setStateEnable(false);
                } else {
                    et_where.setVisibility(View.GONE);
                    et_wherevalue.setVisibility(View.GONE);
                    hintTableText.setVisibility(View.GONE);
                    setStateEnable(true);
                }
            }

        }

        private void setStateEnable(boolean b) {
            if (!b) {
                cbox_comp.setChecked(false);
                cbox_compcountry.setChecked(false);
                cbox_comprank.setChecked(false);
                cbox_court.setChecked(false);
                cbox_date.setChecked(false);
                cbox_level.setChecked(false);
                cbox_match.setChecked(false);
                cbox_matchcountry.setChecked(false);
                cbox_region.setChecked(false);
                cbox_round.setChecked(false);
                cbox_score.setChecked(false);
                cbox_winlose.setChecked(false);
            }
            cbox_comp.setEnabled(b);
            cbox_compcountry.setEnabled(b);
            cbox_comprank.setEnabled(b);
            cbox_court.setEnabled(b);
            cbox_date.setEnabled(b);
            cbox_level.setEnabled(b);
            cbox_match.setEnabled(b);
            cbox_matchcountry.setEnabled(b);
            cbox_region.setEnabled(b);
            cbox_round.setEnabled(b);
            cbox_score.setEnabled(b);
            cbox_winlose.setEnabled(b);
        }

        //除了radioGroup决定的win/lose在其监听接口里组装，其他都在这里统一组装
        private void orgnizeData() {
            if (cbox_comp.isChecked()) {
                String competitor = et_comp.getText().toString();
                searchBean.setCompetitor(competitor);
            }
            if (cbox_compcountry.isChecked()) {
                String country = et_compcountry.getText().toString();
                searchBean.setCptCountry(country);
            }
            if (cbox_matchcountry.isChecked()) {
                String country = et_matchcountry.getText().toString();
                searchBean.setMatchCountry(country);
            }
            if (cbox_court.isChecked()) {
                String court = arr_court[cur_court];
                searchBean.setCourt(court);
            }
            if (cbox_level.isChecked()) {
                String level = arr_level[cur_level];
                searchBean.setLevel(level);
            }
            if (cbox_round.isChecked()) {
                String round = arr_round[cur_round];
                searchBean.setRound(round);
            }
            if (cbox_region.isChecked()) {
                String region = arr_region[cur_region];
                searchBean.setRegion(region);
            }
            if (cbox_match.isChecked()) {
                String match = et_match.getText().toString();
                searchBean.setMatch(match);
            }
            if (cbox_comprank.isChecked()) {

                String str = et_comprank_min.getText().toString();
                int min = 0, max = 100000;
                try {
                    min = Integer.parseInt(str);
                } catch (Exception e) {
                    min = 0;
                }

                str = et_comprank_max.getText().toString();
                try {
                    max = Integer.parseInt(str);
                } catch (Exception e) {
                    max = 100000;
                }

                searchBean.setRankMin(min);
                searchBean.setRankMax(max);
            }
            if (cbox_date.isChecked()) {

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

            if (cbox_score.isChecked()) {

                searchBean.setScore(et_score.getText().toString());
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
        @Override
        protected void bindChildFragmentHolder(IFragmentHolder holder) {
            this.holder = (SearchHolder) holder;
        }

        public boolean onSave() {
            if (cbox_sql.isChecked()) {
                String where = et_where.getText().toString();
                String valueString = et_wherevalue.getText().toString();
                String[] values = null;
                if (valueString != null) {
                    values = valueString.split(",");
                }
                if (onRecordFilterListener != null) {
//                    onRecordFilterListener.recordFiltered(new RecordService().queryByWhere(where, values));
                }
            } else {
                orgnizeData();
                presenter.searchFrom(mRecordList, searchBean);
            }
            return false;
        }

        public void setOnRecordFilterListener(OnRecordFilterListener onRecordFilterListener) {
            this.onRecordFilterListener = onRecordFilterListener;
        }

        public void setRecordList(List<Record> mRecordList) {
            this.mRecordList = mRecordList;
        }

        @Override
        public void searchResult(List<Record> records) {
            if (onRecordFilterListener != null) {
                onRecordFilterListener.recordFiltered(records);
                holder.dismiss();
            }
        }
    }

    public interface OnRecordFilterListener {
        void recordFiltered(List<Record> list);
    }
}
