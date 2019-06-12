package com.king.app.tcareer.page.match.manage;

import android.arch.lifecycle.ViewModelProviders;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.IFragmentHolder;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.databinding.DialogMatchManageBinding;
import com.king.app.tcareer.model.db.entity.MatchBean;
import com.king.app.tcareer.model.db.entity.MatchNameBean;
import com.king.app.tcareer.view.dialog.frame.FrameContentFragment;

/**
 * 描述: modify match dialog
 * <p/>作者：景阳
 * <p/>创建时间: 2018/1/30 15:05
 */
public class MatchEditDialog extends FrameContentFragment<DialogMatchManageBinding, EditViewModel> implements AdapterView.OnItemSelectedListener {

    private boolean isOnlyAddName;
    private int nLevel, nCourt, nRegion, nMonth;

    private String[] arr_level, arr_court, arr_region, arr_month;
    private OnMatchEditListener onMatchEditListener;
    private MatchNameBean editBean;

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {
        
    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.dialog_match_manage;
    }

    @Override
    protected EditViewModel createViewModel() {
        return ViewModelProviders.of(this).get(EditViewModel.class);
    }

    @Override
    protected void onCreate(View view) {

        if (isOnlyAddName) {
            mBinding.etCountry.setVisibility(View.GONE);
            mBinding.etCity.setVisibility(View.GONE);
            mBinding.spCourt.setVisibility(View.GONE);
            mBinding.spLevel.setVisibility(View.GONE);
            mBinding.spMonth.setVisibility(View.GONE);
            mBinding.spRegion.setVisibility(View.GONE);
            mBinding.etWeek.setVisibility(View.GONE);
        }
        else {
            initSpinner();

            initData();
        }

        mBinding.tvOk.setOnClickListener(v -> {
            if (onSave()) {
                dismissAllowingStateLoss();
            }
        });
    }

    @Override
    protected void onCreateData() {

    }

    private void initSpinner() {
        arr_month = new String[12];
        for (int i = 0; i < 12;) {
            if (i < 9)
                arr_month[i] = "" + (++i);
            else
                arr_month[i] = "" + (++i);
        }
        arr_court = AppConstants.RECORD_MATCH_COURTS;
        arr_level = AppConstants.RECORD_MATCH_LEVELS;
        arr_region = getContext().getResources().getStringArray(
                R.array.spinner_region);

        ArrayAdapter spinnerAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, arr_month);
        spinnerAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_item);
        mBinding.spMonth.setAdapter(spinnerAdapter);
        mBinding.spMonth.setOnItemSelectedListener(this);
        spinnerAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, arr_court);
        spinnerAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBinding.spCourt.setAdapter(spinnerAdapter);
        mBinding.spCourt.setOnItemSelectedListener(this);
        spinnerAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, arr_level);
        spinnerAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBinding.spLevel.setAdapter(spinnerAdapter);
        mBinding.spLevel.setOnItemSelectedListener(this);
        spinnerAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, arr_region);
        spinnerAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBinding.spRegion.setAdapter(spinnerAdapter);
        mBinding.spRegion.setOnItemSelectedListener(this);
    }

    private void initData() {
        if (editBean == null) {
            mBinding.etName.setText("");
            mBinding.etWeek.setText("");
            mBinding.etCountry.setText("");
            mBinding.etCity.setText("");
            mBinding.spRegion.setSelection(0);
            mBinding.spCourt.setSelection(0);
            mBinding.spLevel.setSelection(0);
            mBinding.spMonth.setSelection(0);
        }
        else {
            mBinding.etName.setText(editBean.getName());
            mBinding.etWeek.setText(String.valueOf(editBean.getMatchBean().getWeek()));
            mBinding.etCountry.setText(editBean.getMatchBean().getCountry());
            mBinding.etCity.setText(editBean.getMatchBean().getCity());
            setSpinnerSelection(mBinding.spCourt, arr_court, editBean.getMatchBean().getCourt());
            setSpinnerSelection(mBinding.spRegion, arr_region, editBean.getMatchBean().getRegion());
            setSpinnerSelection(mBinding.spLevel, arr_level, editBean.getMatchBean().getLevel());
            setSpinnerSelection(mBinding.spMonth, arr_month, String.valueOf(editBean.getMatchBean().getMonth()));
        }
    }

    public void setSpinnerSelection(Spinner spinner, String[] array, String text) {
        for (int i = 0; i < array.length; i ++) {
            if (array[i].equals(text)) {
                spinner.setSelection(i);
                break;
            }
        }
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent == mBinding.spMonth) {
            nMonth = position;
        } else if (parent == mBinding.spCourt) {
            nCourt = position;
        } else if (parent == mBinding.spLevel) {
            nLevel = position;
        } else if (parent == mBinding.spRegion) {
            nRegion = position;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private boolean onSave() {
        if (editBean != null && isOnlyAddName) {
            return saveNewName();
        }
        else {
            return saveNewMatch();
        }
    }

    private boolean saveNewMatch() {
        String week = mBinding.etWeek.getText().toString();
        if (TextUtils.isEmpty(week)) {
            mBinding.etWeek.setError("Week can't be null");
            return false;
        }
        String name = mBinding.etName.getText().toString();
        if (TextUtils.isEmpty(name)) {
            mBinding.etName.setError("Name can't be null");
            return false;
        }
        String country = mBinding.etCountry.getText().toString();
        if (TextUtils.isEmpty(country)) {
            mBinding.etCountry.setError("Country can't be null");
            return false;
        }
        String city = mBinding.etCity.getText().toString();
        if (TextUtils.isEmpty(city)) {
            mBinding.etCity.setError("City can't be null");
            return false;
        }

        if (editBean == null) {
            MatchNameBean nameBean = new MatchNameBean();
            MatchBean matchBean = new MatchBean();
            nameBean.setName(name);
            matchBean.setCountry(country);
            matchBean.setCity(city);
            matchBean.setCourt(arr_court[nCourt]);
            matchBean.setLevel(arr_level[nLevel]);
            matchBean.setRegion(arr_region[nRegion]);
            matchBean.setMonth(Integer.parseInt(arr_month[nMonth]));
            matchBean.setWeek(Integer.parseInt(week));
            mModel.insertFullMatch(nameBean, matchBean);
            onMatchEditListener.onMatchAdded();
        }
        else {
            MatchBean matchBean = editBean.getMatchBean();
            editBean.setName(name);
            matchBean.setCountry(country);
            matchBean.setCity(city);
            matchBean.setCourt(arr_court[nCourt]);
            matchBean.setLevel(arr_level[nLevel]);
            matchBean.setRegion(arr_region[nRegion]);
            matchBean.setMonth(Integer.parseInt(arr_month[nMonth]));
            matchBean.setWeek(Integer.parseInt(week));
            mModel.updateMatch(editBean, matchBean);
            onMatchEditListener.onMatchUpdated(editBean);
        }
        return true;
    }

    private boolean saveNewName() {
        String name = mBinding.etName.getText().toString();
        if (TextUtils.isEmpty(name)) {
            mBinding.etName.setError("Name can't be null");
            return false;
        }

        MatchNameBean nameBean = new MatchNameBean();
        nameBean.setName(name);
        nameBean.setMatchId(editBean.getMatchId());
        mModel.insertMatchName(nameBean);
        onMatchEditListener.onMatchAdded();
        return true;
    }

    public void setOnMatchEditListener(OnMatchEditListener onMatchEditListener) {
        this.onMatchEditListener = onMatchEditListener;
    }

    public void setEditBean(MatchNameBean editBean) {
        this.editBean = editBean;
    }

    public void setIsOnlyAddName(boolean isOnlyAddName) {
        this.isOnlyAddName = isOnlyAddName;
    }

    public interface OnMatchEditListener {
        void onMatchAdded();

        void onMatchUpdated(MatchNameBean editBean);
    }
}
