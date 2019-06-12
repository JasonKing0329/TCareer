package com.king.app.tcareer.page.glory.target;

import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.king.app.tcareer.R;
import com.king.app.tcareer.databinding.FragmentGloryTargetBinding;
import com.king.app.tcareer.page.glory.BaseGloryPageFragment;
import com.king.app.tcareer.page.glory.GloryRecordAdapter;
import com.king.app.tcareer.page.glory.bean.GloryRecordItem;
import com.king.app.tcareer.page.setting.SettingProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/6/19 10:09
 */
public class TargetFragment extends BaseGloryPageFragment<FragmentGloryTargetBinding> {

    private GloryRecordAdapter adapter;

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_glory_target;
    }

    @Override
    protected void onCreate(View view) {
        mBinding.tvAll.setSelected(true);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mBinding.rvList.setLayoutManager(manager);

        mBinding.tvAll.setText("All(" + getMainViewModel().getGloryTitle().getCareerMatch() + ")");
        mBinding.tvWin.setText("Win(" + getMainViewModel().getGloryTitle().getCareerWin() + ")");
        if (SettingProperty.isGloryTargetWin()) {
            mBinding.tvWin.setSelected(true);
            mBinding.tvAll.setSelected(false);
            initList(getMainViewModel().getGloryTitle().getTargetWinList());
        }
        else {
            mBinding.tvWin.setSelected(false);
            mBinding.tvAll.setSelected(true);
            initList(getMainViewModel().getGloryTitle().getTargetList());
        }

        mBinding.tvAll.setOnClickListener(v -> {
            SettingProperty.setGloryTargetWin(false);
            mBinding.tvAll.setSelected(true);
            mBinding.tvWin.setSelected(false);

            refreshList(getMainViewModel().getGloryTitle().getTargetList());
        });
        mBinding.tvAll.setOnClickListener(v -> {
            SettingProperty.setGloryTargetWin(true);
            mBinding.tvAll.setSelected(false);
            mBinding.tvWin.setSelected(true);

            refreshList(getMainViewModel().getGloryTitle().getTargetWinList());
        });
    }

    /**
     * 倒序显示，不改变原数据顺序
     * @param targetList
     */
    private void initList(List<GloryRecordItem> targetList) {
        List<GloryRecordItem> newList = new ArrayList<>();
        newList.addAll(targetList);
        Collections.reverse(newList);

        adapter = new GloryRecordAdapter();
        adapter.setList(newList);
        adapter.setOnItemClickListener((view, position, data) -> showGloryMatchDialog(data.getRecord()));
        mBinding.rvList.setAdapter(adapter);
    }

    private void refreshList(List<GloryRecordItem> targetList) {
        List<GloryRecordItem> newList = new ArrayList<>();
        newList.addAll(targetList);
        Collections.reverse(newList);

        adapter.setList(newList);
        adapter.notifyDataSetChanged();
    }
}
