package com.king.app.tcareer.page.glory.gs;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.king.app.tcareer.R;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.page.glory.BaseGloryPageFragment;
import com.king.app.tcareer.page.glory.title.OnRecordItemListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/6/1 18:45
 */
public class MasterFragment extends BaseGloryPageFragment implements OnRecordItemListener {

    @BindView(R.id.rv_gs_list)
    RecyclerView rvGsList;

    Unbinder unbinder;

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_glory_atp1000;
    }

    @Override
    protected void onCreate(View view) {
        unbinder = ButterKnife.bind(this, view);

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rvGsList.setLayoutManager(manager);

        MasterYearAdapter adapter = new MasterYearAdapter(gloryHolder.getGloryTitle().getMasterItemList());
        adapter.setOnRecordItemListener(this);
        rvGsList.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onClickRecord(Record record) {
        showGloryMatchDialog(record);
    }
}
