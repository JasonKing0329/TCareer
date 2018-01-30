package com.king.app.tcareer.page.match.manage;

import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.IFragmentHolder;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.model.db.entity.MatchBean;
import com.king.app.tcareer.model.db.entity.MatchNameBean;
import com.king.app.tcareer.view.dialog.DraggableDialogFragment;

/**
 * 描述: modify match dialog
 * <p/>作者：景阳
 * <p/>创建时间: 2018/1/30 15:05
 */
public class MatchEditDialog extends DraggableDialogFragment {
    
    private EditFragment editFragment;
    private OnMatchEditListener onMatchEditListener;
    private MatchNameBean editBean;
    private boolean isOnlyAddName;

    @Override
    protected View getToolbarView(ViewGroup groupToolbar) {
        requestOkAction();
        requestCloseAction();
        if (editBean == null) {
            setTitle("New match");
        }
        else {
            setTitle(editBean.getName());
        }
        return null;
    }

    @Override
    protected Fragment getContentViewFragment() {
        editFragment = new EditFragment();
        editFragment.setOnMatchEditListener(onMatchEditListener);
        editFragment.setEditBean(editBean);
        editFragment.setIsOnlyAddName(isOnlyAddName);
        return editFragment;
    }

    @Override
    protected boolean onClickOk() {
        return editFragment.onSave();
    }

    public void setOnMatchEditListener(OnMatchEditListener onMatchEditListener) {
        this.onMatchEditListener = onMatchEditListener;
    }

    public void setEditBean(MatchNameBean editBean) {
        this.editBean = editBean;
    }

    public void setOnlyAddName(boolean onlyAddName) {
        this.isOnlyAddName = onlyAddName;
    }

    public static class EditFragment extends ContentFragment implements AdapterView.OnItemSelectedListener {
        
        private EditText etName;
        private EditText etCountry;
        private EditText etCity;
        private Spinner spLevel;
        private Spinner spCourt;
        private Spinner spMonth;
        private Spinner spRegion;
        private EditText etWeek;

        private String[] arr_level, arr_court, arr_region, arr_month;
        private int nLevel, nCourt, nRegion, nMonth;

        private OnMatchEditListener onMatchEditListener;
        private MatchNameBean editBean;
        private boolean isOnlyAddName;
        
        private EditPresenter editPresenter;

        @Override
        protected int getContentLayoutRes() {
            return R.layout.dialog_match_manage;
        }

        @Override
        protected void onCreate(View view) {
            
            editPresenter = new EditPresenter();
            
            etName = view.findViewById(R.id.et_name);

            etCountry = view.findViewById(R.id.et_country);
            etCity = view.findViewById(R.id.et_city);
            spLevel = view.findViewById(R.id.sp_level);
            spCourt = view.findViewById(R.id.sp_court);
            spMonth = view.findViewById(R.id.sp_month);
            spRegion = view.findViewById(R.id.sp_region);
            etWeek = view.findViewById(R.id.et_week);

            if (isOnlyAddName) {
                etCountry.setVisibility(View.GONE);
                etCity.setVisibility(View.GONE);
                spCourt.setVisibility(View.GONE);
                spLevel.setVisibility(View.GONE);
                spMonth.setVisibility(View.GONE);
                spRegion.setVisibility(View.GONE);
                etWeek.setVisibility(View.GONE);
            }
            else {
                initSpinner();

                initData();
            }
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
            spMonth.setAdapter(spinnerAdapter);
            spMonth.setOnItemSelectedListener(this);
            spinnerAdapter = new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_spinner_item, arr_court);
            spinnerAdapter
                    .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spCourt.setAdapter(spinnerAdapter);
            spCourt.setOnItemSelectedListener(this);
            spinnerAdapter = new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_spinner_item, arr_level);
            spinnerAdapter
                    .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spLevel.setAdapter(spinnerAdapter);
            spLevel.setOnItemSelectedListener(this);
            spinnerAdapter = new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_spinner_item, arr_region);
            spinnerAdapter
                    .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spRegion.setAdapter(spinnerAdapter);
            spRegion.setOnItemSelectedListener(this);
        }

        private void initData() {
            if (editBean == null) {
                etName.setText("");
                etWeek.setText("");
                etCountry.setText("");
                etCity.setText("");
                spRegion.setSelection(0);
                spCourt.setSelection(0);
                spLevel.setSelection(0);
                spMonth.setSelection(0);
            }
            else {
                etName.setText(editBean.getName());
                etWeek.setText(String.valueOf(editBean.getMatchBean().getWeek()));
                etCountry.setText(editBean.getMatchBean().getCountry());
                etCity.setText(editBean.getMatchBean().getCity());
                setSpinnerSelection(spCourt, arr_court, editBean.getMatchBean().getCourt());
                setSpinnerSelection(spRegion, arr_region, editBean.getMatchBean().getRegion());
                setSpinnerSelection(spLevel, arr_level, editBean.getMatchBean().getLevel());
                setSpinnerSelection(spMonth, arr_month, String.valueOf(editBean.getMatchBean().getMonth()));
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
            if (parent == spMonth) {
                nMonth = position;
            } else if (parent == spCourt) {
                nCourt = position;
            } else if (parent == spLevel) {
                nLevel = position;
            } else if (parent == spRegion) {
                nRegion = position;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }

        @Override
        protected void bindChildFragmentHolder(IFragmentHolder holder) {

        }

        public boolean onSave() {
            if (editBean != null && isOnlyAddName) {
                return saveNewName();
            }
            else {
                return saveNewMatch();
            }
        }

        private boolean saveNewMatch() {
            String week = etWeek.getText().toString();
            if (TextUtils.isEmpty(week)) {
                etWeek.setError("Week can't be null");
                return false;
            }
            String name = etName.getText().toString();
            if (TextUtils.isEmpty(name)) {
                etName.setError("Name can't be null");
                return false;
            }
            String country = etCountry.getText().toString();
            if (TextUtils.isEmpty(country)) {
                etCountry.setError("Country can't be null");
                return false;
            }
            String city = etCity.getText().toString();
            if (TextUtils.isEmpty(city)) {
                etCity.setError("City can't be null");
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
                editPresenter.insertFullMatch(nameBean, matchBean);
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
                editPresenter.updateMatch(editBean, matchBean);
                onMatchEditListener.onMatchUpdated(editBean);
            }
            return true;
        }

        private boolean saveNewName() {
            String name = etName.getText().toString();
            if (TextUtils.isEmpty(name)) {
                etName.setError("Name can't be null");
                return false;
            }

            MatchNameBean nameBean = new MatchNameBean();
            nameBean.setName(name);
            nameBean.setMatchId(editBean.getMatchId());
            editPresenter.insertMatchName(nameBean);
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
    }

    public interface OnMatchEditListener {
        void onMatchAdded();

        void onMatchUpdated(MatchNameBean editBean);
    }
}
