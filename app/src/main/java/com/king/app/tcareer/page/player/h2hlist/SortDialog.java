package com.king.app.tcareer.page.player.h2hlist;

import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.IFragmentHolder;
import com.king.app.tcareer.base.mvvm.BaseViewModel;
import com.king.app.tcareer.databinding.DialogH2hlistSortBinding;
import com.king.app.tcareer.page.setting.SettingProperty;
import com.king.app.tcareer.view.dialog.DraggableDialogFragment;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/2/7 17:27
 */
public class SortDialog extends DraggableDialogFragment {

    /**
     * refer sort_order in array.xml
     */
    public static final int SORT_ORDER_ASC = 0;
    public static final int SORT_ORDER_DESC = 1;

    private SortFragment sortFragment;

    private OnSortListener onSortListener;

    @Override
    protected View getToolbarView(ViewGroup groupToolbar) {
        requestOkAction();
        requestCloseAction();
        setTitle("Filter");
        return null;
    }

    @Override
    protected Fragment getContentViewFragment() {
        sortFragment = new SortFragment();
        sortFragment.setOnSortListener(onSortListener);
        return sortFragment;
    }

    public void setOnSortListener(OnSortListener onSortListener) {
        this.onSortListener = onSortListener;
    }

    @Override
    protected boolean onClickOk() {
        return sortFragment.onSave();
    }

    public static class SortFragment extends BindingContentFragment<DialogH2hlistSortBinding, BaseViewModel> {

        private String[] arrType, arrOrder;

        private OnSortListener onSortListener;

        @Override
        protected int getContentLayoutRes() {
            return R.layout.dialog_h2hlist_sort;
        }

        @Override
        protected BaseViewModel createViewModel() {
            return null;
        }

        @Override
        protected void onCreate(View view) {
            arrOrder = getContext().getResources().getStringArray(R.array.sort_order);
            arrType = getContext().getResources().getStringArray(R.array.h2hlist_sort_type);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, arrType);
            mBinding.spType.setAdapter(adapter);
            mBinding.spType.setSelection(0);

            adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, arrOrder);
            mBinding.spOrder.setAdapter(adapter);
            mBinding.spOrder.setSelection(0);
        }

        @Override
        protected void bindChildFragmentHolder(IFragmentHolder holder) {

        }

        public void setOnSortListener(OnSortListener onSortListener) {
            this.onSortListener = onSortListener;
        }

        public boolean onSave() {
            if (onSortListener != null) {
                onSortListener.onSort(getSortType(mBinding.spType.getSelectedItemPosition()), mBinding. spOrder.getSelectedItemPosition());
            }
            return true;
        }

        private int getSortType(int position) {
            switch (position) {
                case 1:
                    return SettingProperty.VALUE_SORT_PLAYER_NAME;
                case 2:
                    return SettingProperty.VALUE_SORT_PLAYER_RECORD;
                case 3:
                    return SettingProperty.VALUE_SORT_PLAYER_RECORD_WIN;
                case 4:
                    return SettingProperty.VALUE_SORT_PLAYER_RECORD_LOSE;
                case 5:
                    return SettingProperty.VALUE_SORT_PLAYER_RECORD_ODDS_WIN;
                case 6:
                    return SettingProperty.VALUE_SORT_PLAYER_RECORD_ODDS_LOSE;
                default:
                    return SettingProperty.VALUE_SORT_PLAYER_NAME_ENG;
            }
        }

    }

    public interface OnSortListener {
        void onSort(int type, int order);
    }
}
