package com.king.app.tcareer.page.player.list;

import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.IFragmentHolder;
import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.model.db.entity.PlayerAtpBeanDao;
import com.king.app.tcareer.utils.ConstellationUtil;
import com.king.app.tcareer.view.dialog.DraggableDialogFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
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

    public static class FilterFragment extends ContentFragment {

        @BindView(R.id.sp_signs)
        Spinner spSigns;
        @BindView(R.id.sp_country)
        Spinner spCountry;
        @BindView(R.id.rb_forehand_all)
        RadioButton rbForehandAll;
        @BindView(R.id.rb_forehand_left)
        RadioButton rbForehandLeft;
        @BindView(R.id.rb_forehand_right)
        RadioButton rbForehandRight;
        @BindView(R.id.rb_backhand_all)
        RadioButton rbBackhandAll;
        @BindView(R.id.rb_backhand_single)
        RadioButton rbBackhandSingle;
        @BindView(R.id.rb_backhand_double)
        RadioButton rbBackhandDouble;

        private List<String> signList;

        private List<String> countryList;

        private OnFilterListener onFilterListener;

        public void setOnFilterListener(OnFilterListener onFilterListener) {
            this.onFilterListener = onFilterListener;
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
            signList.add("All");
            signList.addAll(Arrays.asList(ConstellationUtil.starArrEng));
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.adapter_filter_spinner_item, signList);
            spSigns.setAdapter(adapter);
            spSigns.setSelection(0);
        }

        private void initCountries() {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.adapter_filter_spinner_item, countryList);
            spCountry.setAdapter(adapter);
            spCountry.setSelection(0);
        }

        private void loadCountries() {
            Observable.create(e -> {
                countryList = new ArrayList<>();
                countryList.add( "All");
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
            if (rbForehandLeft.isChecked()) {
                bean.setForehand(1);
            }
            else if (rbForehandRight.isChecked()) {
                bean.setForehand(2);
            }
            else {
                bean.setForehand(0);
            }
            if (rbBackhandSingle.isChecked()) {
                bean.setBackhand(1);
            }
            else if (rbBackhandDouble.isChecked()) {
                bean.setBackhand(2);
            }
            else {
                bean.setBackhand(0);
            }
            bean.setSign(signList.get(spSigns.getSelectedItemPosition()));
            bean.setCountry(countryList.get(spCountry.getSelectedItemPosition()));
            if (onFilterListener != null) {
                onFilterListener.onFilterRichPlayer(bean);
            }
            return true;
        }

    }
}
