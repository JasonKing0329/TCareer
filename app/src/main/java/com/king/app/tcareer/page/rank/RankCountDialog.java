package com.king.app.tcareer.page.rank;

import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.IFragmentHolder;
import com.king.app.tcareer.view.dialog.DraggableDialogFragment;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/3/9 14:40
 */
public class RankCountDialog extends DraggableDialogFragment {

    private long mUserId;

    @Override
    protected View getToolbarView(ViewGroup groupToolbar) {
        setTitle("Rank count");
        requestCloseAction();
        return null;
    }

    @Override
    protected Fragment getContentViewFragment() {
        CountFragment fragment = new CountFragment();
        fragment.setUserId(mUserId);
        return fragment;
    }

    public void setUserId(Long mUserId) {
        this.mUserId = mUserId;
    }

    public static class CountFragment extends ContentFragment {

        private long mUserId;

        @Override
        protected int getContentLayoutRes() {
            return R.layout.dialog_rank_count;
        }

        @Override
        protected void onCreate(View view) {
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.group_ft_container, RankCountFragment.newInstance(mUserId), "RankCountFragment")
                    .commit();
        }

        @Override
        protected void bindChildFragmentHolder(IFragmentHolder holder) {

        }

        public void setUserId(long userId) {
            this.mUserId = userId;
        }

    }
}
