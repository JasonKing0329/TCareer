package com.king.app.tcareer.page.glory;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.ViewDataBinding;

import com.king.app.tcareer.base.IFragmentHolder;
import com.king.app.tcareer.base.mvvm.BaseViewModel;
import com.king.app.tcareer.base.mvvm.MvvmFragment;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.page.match.MatchDialog;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/6/1 18:37
 */
public abstract class BaseGloryPageFragment<T extends ViewDataBinding> extends MvvmFragment<T, BaseViewModel> {

    protected GloryViewModel mainViewModel;

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {

    }

    /**
     * record could only include match and strdate
     * @param record
     */
    protected void showGloryMatchDialog(final Record record) {
        MatchDialog dialog = new MatchDialog();
        dialog.setUser(getMainViewModel().getUser());
        dialog.setMatch(record.getMatchNameId(), record.getMatch().getName(), record.getDateStr());
        dialog.show(getChildFragmentManager(), "MatchDialog");
    }

    @Override
    protected BaseViewModel createViewModel() {
        mainViewModel = ViewModelProviders.of(getActivity()).get(GloryViewModel.class);
        return null;
    }

    @Override
    protected void onCreateData() {

    }

    protected GloryViewModel getMainViewModel() {
        return mainViewModel;
    }
}
