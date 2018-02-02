package com.king.app.tcareer.page.player.page;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.BaseFragment;
import com.king.app.tcareer.base.IFragmentHolder;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.page.match.MatchDialog;

import java.util.List;

import butterknife.BindView;

/**
 * 描述: fragment to present records of competitor
 * <p/>作者：景阳
 * <p/>创建时间: 2017/11/20 16:05
 */
public class PageFragment extends BaseFragment implements IPageCallback {

    private static final String KEY_TAB_NAME = "tab_name";

    @BindView(R.id.rv_records)
    RecyclerView rvRecords;

    private PageRecordAdapter adapter;

    private IPageHolder holder;

    public static PageFragment newInstance(String tabName) {
        Bundle args = new Bundle();
        args.putString(KEY_TAB_NAME, tabName);
        PageFragment fragment = new PageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {
        this.holder = (IPageHolder) holder;
    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_player_page;
    }

    @Override
    protected void onCreate(View view) {
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rvRecords.setLayoutManager(manager);

        String tabName = getArguments().getString(KEY_TAB_NAME);
        holder.getPresenter().createRecords(tabName, this);
    }

    @Override
    public void onDataLoaded(List<Object> list) {
        User user = holder.getUser();
        adapter = new PageRecordAdapter(user, list);
        adapter.setOnItemClickListener(new PageRecordAdapter.OnItemClickListener() {
            @Override
            public void onClickRecord(final Record record) {

                showMatchDialog(record);
            }

            @Override
            public void onLongClickRecord(View view, Record record) {
//                Intent intent = new Intent();
//                intent.setClass(getActivity(), MatchPageActivity.class);
//                intent.putExtra(MatchPageActivity.KEY_MATCH_NAME, record.getMatch());
//                intent.putExtra(MatchPageActivity.KEY_USER_ID, getArguments().getString(KEY_USER_ID));
//                ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(getActivity()
//                        , Pair.create(view.findViewById(R.id.iv_match),getString(R.string.anim_match_page_head)));
//                startActivity(intent, transitionActivityOptions.toBundle());
            }
        });
        rvRecords.setAdapter(adapter);
    }

    private void showMatchDialog(Record record) {
        MatchDialog matchDialog = new MatchDialog();
        matchDialog.setMatch(record.getMatchNameId(), record.getMatch().getName(), record.getDateStr());
        matchDialog.setUser(holder.getUser());
        matchDialog.show(getChildFragmentManager(), "MatchDialog");
    }
}
