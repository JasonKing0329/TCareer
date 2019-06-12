package com.king.app.tcareer.page.glory.title;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.king.app.tcareer.R;
import com.king.app.tcareer.databinding.FragmentGloryChampionBinding;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.page.glory.BaseGloryPageFragment;

/**
 * Created by Administrator on 2017/6/5 0005.
 */

public abstract class AbsGloryListFragment extends BaseGloryPageFragment<FragmentGloryChampionBinding> implements OnRecordItemListener {

    protected int groupMode;

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_glory_champion;
    }

    @Override
    protected void onCreate(View view) {
        mBinding.fabUp.setOnClickListener(v -> mBinding.rvRecord.scrollToPosition(0));

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mBinding.rvRecord.setLayoutManager(manager);

        mBinding.rvRecord.setAdapter(getListAdapter());
    }

    protected abstract RecyclerView.Adapter getListAdapter();

    public void setGroupMode(int groupMode) {
        this.groupMode = groupMode;
    }

    @Override
    public void onClickRecord(Record record) {
        showGloryMatchDialog(record);
    }
}
