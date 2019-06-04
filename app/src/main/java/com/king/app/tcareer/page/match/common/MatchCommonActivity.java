package com.king.app.tcareer.page.match.common;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.mvvm.MvvmActivity;
import com.king.app.tcareer.databinding.ActivityMatchCommonBinding;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.page.match.page.MatchPageActivity;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/1/29 16:49
 */
public class MatchCommonActivity extends MvvmActivity<ActivityMatchCommonBinding, MatchCommonViewModel> {

    public static final String KEY_MATCH = "common_match";

    @Override
    protected int getContentView() {
        return R.layout.activity_match_common;
    }

    @Override
    protected MatchCommonViewModel createViewModel() {
        return ViewModelProviders.of(this).get(MatchCommonViewModel.class);
    }

    @Override
    protected void initView() {
        mBinding.setModel(mModel);
        mBinding.rvUsers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    protected void initData() {
        long matchNameId = getIntent().getLongExtra(KEY_MATCH, -1);
        if (matchNameId == -1) {
            showMessageShort("赛事不存在");
        }
        else {
            mModel.usersObserver.observe(this, list -> {
                UserItemAdapter adapter = new UserItemAdapter();
                adapter.setList(list);
                adapter.setOnItemClickListener((view, position, data) -> startPlayerPage(data.getUser()));
                mBinding.rvUsers.setAdapter(adapter);
            });
            mModel.loadMatch(matchNameId);
        }
    }

    private void startPlayerPage(User user) {
        Intent intent = new Intent(this, MatchPageActivity.class);
        intent.putExtra(MatchPageActivity.KEY_MATCH_NAME_ID, mModel.getmMatchNameBean().getId());
        intent.putExtra(MatchPageActivity.KEY_USER_ID, user.getId());
        startActivity(intent);
    }
}
