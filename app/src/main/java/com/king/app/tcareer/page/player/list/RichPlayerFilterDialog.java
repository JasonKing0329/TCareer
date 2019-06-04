package com.king.app.tcareer.page.player.list;

import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.IFragmentHolder;
import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.base.mvvm.BaseViewModel;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.databinding.DialogRichPlayerFilterBinding;
import com.king.app.tcareer.model.db.entity.PlayerAtpBeanDao;
import com.king.app.tcareer.utils.ConstellationUtil;
import com.king.app.tcareer.view.dialog.DraggableDialogFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/5/23 9:50
 */
public class RichPlayerFilterDialog extends DraggableDialogFragment {

    private FilterFragment ftFilter;

    private OnFilterListener onFilterListener;

    public void setOnFilterListener(OnFilterListener onFilterListener) {
        this.onFilterListener = onFilterListener;
    }

    @Override
    protected View getToolbarView(ViewGroup groupToolbar) {
        requestOkAction();
        requestCloseAction();
        setTitle("Filter");
        return null;
    }

    @Override
    protected Fragment getContentViewFragment() {
        ftFilter = new FilterFragment();
        ftFilter.setOnFilterListener(onFilterListener);
        return ftFilter;
    }

    @Override
    protected boolean onClickOk() {
        return ftFilter.onClickOk();
    }

    public interface OnFilterListener {
        void onFilterRichPlayer(RichFilterBean bean);
    }

    public static class FilterFragment extends BindingContentFragment<DialogRichPlayerFilterBinding, BaseViewModel> {

        private List<String> signList;

        private List<String> countryList;

        private OnFilterListener onFilterListener;

        public void setOnFilterListener(OnFilterListener onFilterListener) {
            this.onFilterListener = onFilterListener;
        }

        @Override
        protected BaseViewModel createViewModel() {
            return null;
        }

        @Override
        protected int getContentLayoutRes() {
            return R.layout.dialog_rich_player_filter;
        }

        @Override
        protected void onCreate(View view) {
            initSigns();
            loadCountries();
        }

        private void initSigns() {
            signList = new ArrayList<>();
            signList.add(AppConstants.FILTER_ALL);
            signList.addAll(Arrays.asList(ConstellationUtil.starArrEng));
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.adapter_filter_spinner_item, signList);
            mBinding.spSigns.setAdapter(adapter);
            mBinding.spSigns.setSelection(0);
        }

        private void initCountries() {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.adapter_filter_spinner_item, countryList);
            mBinding.spCountry.setAdapter(adapter);
            mBinding.spCountry.setSelection(0);
        }

        private void loadCountries() {
            Observable.create(e -> {
                countryList = new ArrayList<>();
                countryList.add(AppConstants.FILTER_ALL);
                String column = PlayerAtpBeanDao.Properties.BirthCountry.columnName;
                Cursor cursor = TApplication.getInstance().getDaoSession().getDatabase()
                        .rawQuery("select " + column + " from " + PlayerAtpBeanDao.TABLENAME
                                + " group by " + column + " order by " + column + " ASC", new String[]{});
                while (cursor.moveToNext()) {
                    String country = cursor.getString(0);
                    if (!TextUtils.isEmpty(country)) {
                        countryList.add(country);
                    }
                }
                e.onNext(new Object());
            }).observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Observer<Object>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(Object object) {
                            initCountries();
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }

        @Override
        protected void bindChildFragmentHolder(IFragmentHolder holder) {

        }

        public boolean onClickOk() {
            RichFilterBean bean = new RichFilterBean();
            if (mBinding.rbForehandLeft.isChecked()) {
                bean.setForehand(1);
            }
            else if (mBinding.rbForehandRight.isChecked()) {
                bean.setForehand(2);
            }
            else {
                bean.setForehand(0);
            }
            if (mBinding.rbBackhandSingle.isChecked()) {
                bean.setBackhand(1);
            }
            else if (mBinding.rbBackhandDouble.isChecked()) {
                bean.setBackhand(2);
            }
            else {
                bean.setBackhand(0);
            }
            bean.setSign(signList.get(mBinding.spSigns.getSelectedItemPosition()));
            bean.setCountry(countryList.get(mBinding.spCountry.getSelectedItemPosition()));
            try {
                bean.setRankLow(Integer.parseInt(mBinding.etRankLow.getText().toString()));
            } catch (Exception e) {}
            try {
                bean.setRankHigh(Integer.parseInt(mBinding.etRankHigh.getText().toString()));
            } catch (Exception e) {}

            if (onFilterListener != null) {
                onFilterListener.onFilterRichPlayer(bean);
            }
            return true;
        }

    }
}
