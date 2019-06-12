package com.king.app.tcareer.page.glory.gs;

import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.king.app.tcareer.R;
import com.king.app.tcareer.databinding.FragmentGloryGsBinding;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.page.glory.BaseGloryPageFragment;
import com.king.app.tcareer.page.glory.title.OnRecordItemListener;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/6/1 18:45
 */
public class GsFragment extends BaseGloryPageFragment<FragmentGloryGsBinding> implements OnRecordItemListener {

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_glory_gs;
    }

    @Override
    protected void onCreate(View view) {
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mBinding.rvGsList.setLayoutManager(manager);

        GsYearAdapter adapter = new GsYearAdapter();
        adapter.setList(getMainViewModel().getGloryTitle().getGsItemList());
        adapter.setOnRecordItemListener(this);
        mBinding.rvGsList.setAdapter(adapter);
    }

    @Override
    public void onClickRecord(Record record) {
        showGloryMatchDialog(record);
    }
}
