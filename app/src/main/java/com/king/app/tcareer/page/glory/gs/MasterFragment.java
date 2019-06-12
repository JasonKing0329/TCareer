package com.king.app.tcareer.page.glory.gs;

import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.king.app.tcareer.R;
import com.king.app.tcareer.databinding.FragmentGloryAtp1000Binding;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.page.glory.BaseGloryPageFragment;
import com.king.app.tcareer.page.glory.title.OnRecordItemListener;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/6/1 18:45
 */
public class MasterFragment extends BaseGloryPageFragment<FragmentGloryAtp1000Binding> implements OnRecordItemListener {

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_glory_atp1000;
    }

    @Override
    protected void onCreate(View view) {
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mBinding.rvGsList.setLayoutManager(manager);

        MasterYearAdapter adapter = new MasterYearAdapter();
        adapter.setList(getMainViewModel().getGloryTitle().getMasterItemList());
        adapter.setOnRecordItemListener(this);
        mBinding.rvGsList.setAdapter(adapter);
    }

    @Override
    public void onClickRecord(Record record) {
        showGloryMatchDialog(record);
    }
}
