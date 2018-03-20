package com.king.app.tcareer.page.record.complex;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.IFragmentHolder;
import com.king.app.tcareer.utils.ScreenUtils;
import com.king.app.tcareer.view.dialog.DraggableDialogFragment;

import java.util.List;

import butterknife.BindView;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/20 11:34
 */
public class CareerCompareDialog extends DraggableDialogFragment {

    private CompareFragment ftCompare;

    @Override
    protected View getToolbarView(ViewGroup groupToolbar) {
        setTitle("Career compare");
        requestCloseAction();
        return null;
    }

    @Override
    protected int getMaxHeight() {
        return ScreenUtils.getScreenHeight(getActivity()) * 4 / 5;
    }

    @Override
    protected Fragment getContentViewFragment() {
        ftCompare = new CompareFragment();
        return ftCompare;
    }

    public static class CompareFragment extends ContentMvpFragment<CareerComparePresenter> implements CareerCompareView {

        @BindView(R.id.rv_items)
        RecyclerView rvItems;

        private CareerCompareAdapter adapter;

        @Override
        protected int getContentLayoutRes() {
            return R.layout.fragment_career_compare;
        }

        @Override
        protected void onCreate(View view) {
            LinearLayoutManager manager = new LinearLayoutManager(getActivity());
            manager.setOrientation(LinearLayoutManager.VERTICAL);
            rvItems.setLayoutManager(manager);
        }

        @Override
        protected void bindChildFragmentHolder(IFragmentHolder holder) {

        }

        @Override
        protected CareerComparePresenter createPresenter() {
            return new CareerComparePresenter();
        }

        @Override
        protected void onCreateData() {
            presenter.loadData();
        }

        @Override
        public void showData(List<CompareItem> list) {
            adapter = new CareerCompareAdapter();
            adapter.setList(list);
            rvItems.setAdapter(adapter);
        }
    }
}
