package com.king.app.tcareer.page.compare;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.mvvm.BaseViewModel;
import com.king.app.tcareer.base.mvvm.MvvmActivity;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.databinding.ActivityCompareBinding;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/9/12 16:25
 */
public class PlayerCompActivity extends MvvmActivity<ActivityCompareBinding, BaseViewModel> {

    private PlayerCompFragment ftKing;
    private PlayerCompFragment ftFla;
    private PlayerCompFragment ftHen;
    private PlayerCompFragment ftQi;
    private String[] arrLevel1;
    private String[] arrLevel2;
    private int level1Index;

    @Override
    protected BaseViewModel createViewModel() {
        return null;
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_compare;
    }

    @Override
    protected void initView() {
        mBinding.actionBar.setOnBackListener(() -> onBackPressed());
        mBinding.spLevel1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                level1Index = position;
                arrLevel2 = getLevel2(level1Index);
                ArrayAdapter adapter = new ArrayAdapter(PlayerCompActivity.this, android.R.layout.simple_dropdown_item_1line, arrLevel2);
                mBinding.spLevel2.setAdapter(adapter);
                updateParams(arrLevel1[level1Index], arrLevel2[0]);
                notifyParamsChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mBinding.spLevel2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateParams(arrLevel1[level1Index], arrLevel2[position]);
                notifyParamsChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void updateParams(String level1, String level2) {
        ftKing.setParams(level1, level2);
        ftQi.setParams(level1, level2);
        ftHen.setParams(level1, level2);
        ftFla.setParams(level1, level2);
    }

    private void notifyParamsChanged() {
        ftKing.onArgumentsChanged();
        ftQi.onArgumentsChanged();
        ftHen.onArgumentsChanged();
        ftFla.onArgumentsChanged();
    }

    @Override
    protected void initData() {
        arrLevel1 = getResources().getStringArray(R.array.compare_level1);
        arrLevel2 = getResources().getStringArray(R.array.compare_gs_round);

        ftKing = PlayerCompFragment.newInstance(AppConstants.USER_ID_KING);
        ftFla = PlayerCompFragment.newInstance(AppConstants.USER_ID_FLAMENCO);
        ftHen = PlayerCompFragment.newInstance(AppConstants.USER_ID_HENRY);
        ftQi = PlayerCompFragment.newInstance(AppConstants.USER_ID_QI);

        updateParams(arrLevel1[0], arrLevel2[0]);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_king, ftKing, "PlayerCompFragment_king")
                .commit();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_fla, ftFla, "PlayerCompFragment_fla")
                .commit();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_hen, ftHen, "PlayerCompFragment_hen")
                .commit();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_qi, ftQi, "PlayerCompFragment_qi")
                .commit();
    }

    private String[] getLevel2(int index) {
        switch (index) {
            case 0:
                return getResources().getStringArray(R.array.compare_gs_round);
            case 1:
                return getResources().getStringArray(R.array.compare_court);
            case 2:
                return getResources().getStringArray(R.array.compare_champion);
        }
        return null;
    }
}
