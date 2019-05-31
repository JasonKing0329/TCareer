package com.king.app.tcareer.page.glory;

import com.king.app.tcareer.base.BaseMvpFragment;
import com.king.app.tcareer.base.BasePresenter;
import com.king.app.tcareer.base.IFragmentHolder;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.page.match.MatchDialog;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/6/1 18:37
 */
public abstract class BaseGloryPageFragment extends BaseMvpFragment<BasePresenter> {

    protected IGloryHolder gloryHolder;

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {
        gloryHolder = (IGloryHolder) holder;
    }

    /**
     * record could only include match and strdate
     * @param record
     */
    protected void showGloryMatchDialog(final Record record) {
        MatchDialog dialog = new MatchDialog();
        dialog.setUser(gloryHolder.getPresenter().getUser());
        dialog.setMatch(record.getMatchNameId(), record.getMatch().getName(), record.getDateStr());
        dialog.show(getChildFragmentManager(), "MatchDialog");
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected void onCreateData() {

    }
}
