package com.king.app.tcareer.page.glory.title;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.king.app.tcareer.R;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.page.glory.BaseGloryPageFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2017/6/5 0005.
 */

public abstract class AbsGloryListFragment extends BaseGloryPageFragment implements OnRecordItemListener {

    protected int groupMode;

    @BindView(R.id.rv_record)
    RecyclerView rvRecord;

    Unbinder unbinder;

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_glory_champion;
    }

    @Override
    protected void onCreate(View view) {
        unbinder = ButterKnife.bind(this, view);

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rvRecord.setLayoutManager(manager);

        rvRecord.setAdapter(getListAdapter());
    }

    protected abstract RecyclerView.Adapter getListAdapter();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.fab_up)
    public void onViewClicked() {
        rvRecord.scrollToPosition(0);
    }

    public void setGroupMode(int groupMode) {
        this.groupMode = groupMode;
    }

    @Override
    public void onClickRecord(Record record) {
        showGloryMatchDialog(record);
    }
}
