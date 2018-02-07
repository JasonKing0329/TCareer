package com.king.app.tcareer.page.player.h2hlist;

import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.IFragmentHolder;
import com.king.app.tcareer.view.dialog.DraggableDialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/2/7 17:27
 */
public class FilterDialog extends DraggableDialogFragment {

    private FilterFragment filterFragment;

    private OnFilterListener onFilterListener;

    @Override
    protected View getToolbarView(ViewGroup groupToolbar) {
        requestOkAction();
        requestCloseAction();
        setTitle("Filter");
        return null;
    }

    @Override
    protected Fragment getContentViewFragment() {
        filterFragment = new FilterFragment();
        filterFragment.setOnFilterListener(onFilterListener);
        return filterFragment;
    }

    public void setOnFilterListener(OnFilterListener onFilterListener) {
        this.onFilterListener = onFilterListener;
    }

    @Override
    protected boolean onClickOk() {
        return filterFragment.onSave();
    }

    public static class FilterFragment extends ContentFragment {

        @BindView(R.id.sp_type)
        Spinner spType;
        @BindView(R.id.et_value1)
        EditText etValue1;
        @BindView(R.id.et_value2)
        EditText etValue2;
        Unbinder unbinder;

        private String[] arrType;

        private OnFilterListener onFilterListener;

        @Override
        protected int getContentLayoutRes() {
            return R.layout.dialog_h2hlist_filter;
        }

        @Override
        protected void onCreate(View view) {
            unbinder = ButterKnife.bind(this, view);

            arrType = getContext().getResources().getStringArray(R.array.h2hlist_filter_type);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, arrType);
            spType.setAdapter(adapter);
            spType.setSelection(0);

            spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                    switch (position) {
                        case 0:// none
                            etValue1.setVisibility(View.INVISIBLE);
                            etValue2.setVisibility(View.INVISIBLE);
                            break;
                        case 1:// country
                            etValue1.setVisibility(View.VISIBLE);
                            etValue2.setVisibility(View.INVISIBLE);
                            etValue1.setText("");
                            etValue1.setInputType(EditorInfo.TYPE_CLASS_TEXT);
                            break;
                        case 2:// count
                        case 3:// win
                        case 4:// lose
                        case 5:// delta
                            etValue1.setVisibility(View.VISIBLE);
                            etValue2.setVisibility(View.VISIBLE);
                            etValue1.setText("");
                            etValue2.setText("");
                            etValue1.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
                            etValue2.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
                            break;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }

        @Override
        protected void bindChildFragmentHolder(IFragmentHolder holder) {

        }

        public void setOnFilterListener(OnFilterListener onFilterListener) {
            this.onFilterListener = onFilterListener;
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            unbinder.unbind();
        }

        public boolean onSave() {
            if (onFilterListener != null) {
                switch (spType.getSelectedItemPosition()) {
                    case 0:// none
                        onFilterListener.onFilterNothing();
                        break;
                    case 1:// country
                        onFilterListener.onFilterCountry(etValue1.getText().toString());
                        break;
                    case 2:// count
                        onFilterListener.onFilterCount(getMin(), getMax());
                        break;
                    case 3:// win
                        onFilterListener.onFilterWin(getMin(), getMax());
                        break;
                    case 4:// lose
                        onFilterListener.onFilterLose(getMin(), getMax());
                        break;
                    case 5:// delta
                        onFilterListener.onFilterDeltaWin(getMin(), getMax());
                        break;
                }
            }
            return true;
        }

        private int getMax() {
            int max;
            try {
                max = Integer.parseInt(etValue2.getText().toString());
            } catch (Exception e) {
                e.printStackTrace();
                max = Integer.MAX_VALUE;
            }
            return max;
        }

        private int getMin() {
            int min;
            try {
                min = Integer.parseInt(etValue1.getText().toString());
            } catch (Exception e) {
                e.printStackTrace();
                min = Integer.MIN_VALUE;
            }
            return min;
        }

    }

    public interface OnFilterListener {
        void onFilterNothing();
        void onFilterCountry(String country);
        void onFilterRank(int min, int max);
        void onFilterCount(int min, int max);
        void onFilterWin(int min, int max);
        void onFilterLose(int min, int max);
        void onFilterDeltaWin(int min, int max);
    }
}
