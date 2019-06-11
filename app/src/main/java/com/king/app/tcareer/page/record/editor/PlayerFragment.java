package com.king.app.tcareer.page.record.editor;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.view.View;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.IFragmentHolder;
import com.king.app.tcareer.base.mvvm.BaseViewModel;
import com.king.app.tcareer.base.mvvm.MvvmFragment;
import com.king.app.tcareer.databinding.FragmentEditorPlayerBinding;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.page.player.manage.PlayerManageActivity;
import com.king.app.tcareer.page.player.page.PlayerPageActivity;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/3/13 13:22
 */
public class PlayerFragment extends MvvmFragment<FragmentEditorPlayerBinding, BaseViewModel> {

    private boolean supportChooseUser;

    private EditorViewModel parentViewModel;

    private final int REQUEST_CHANGE_PLAYER = 102;
    private final int REQUEST_CHANGE_USER = 103;

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {
        
    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_editor_player;
    }

    @Override
    protected BaseViewModel createViewModel() {
        return null;
    }

    public void setSupportChooseUser(boolean supportChooseUser) {
        this.supportChooseUser = supportChooseUser;
    }

    @Override
    protected void onCreate(View view) {
        parentViewModel = ViewModelProviders.of(getActivity()).get(EditorViewModel.class);
        mBinding.setModel(parentViewModel);

        if (!supportChooseUser) {
            mBinding.ivChooseUser.setVisibility(View.GONE);
        }

        mBinding.ivChooseUser.setOnClickListener(v -> selectUser());
        mBinding.ivChoosePlayer.setOnClickListener(v -> selectPlayer());
        mBinding.tvH2h.setOnClickListener(v -> showH2hDetails());
    }

    @Override
    protected void onCreateData() {
        parentViewModel.initData();
    }

    public void selectPlayer() {
        Intent intent = new Intent().setClass(getContext(), PlayerManageActivity.class);
        intent.putExtra(PlayerManageActivity.KEY_START_MODE, PlayerManageActivity.START_MODE_SELECT);
        startActivityForResult(intent, REQUEST_CHANGE_PLAYER);
    }

    public void selectUser() {
        Intent intent = new Intent().setClass(getContext(), PlayerManageActivity.class);
        intent.putExtra(PlayerManageActivity.KEY_START_MODE, PlayerManageActivity.START_MODE_SELECT);
        intent.putExtra(PlayerManageActivity.KEY_ONLY_USER, true);
        startActivityForResult(intent, REQUEST_CHANGE_USER);
    }

    public void reset() {
        mBinding.etPlayerRank.setText("");
        mBinding.etPlayerSeed.setText("");
        mBinding.etUserRank.setText("");
        mBinding.etUserSeed.setText("");
        mBinding.tvPlayerName.setText("");
        mBinding.tvPlayerBirthday.setText("");
        mBinding.tvUserName.setText("");
        mBinding.tvH2h.setText("H2H");
        mBinding.rlPlayer.setVisibility(View.GONE);
    }

    private void showH2hDetails() {
        if (parentViewModel.getCompetitor() != null) {
            Intent intent = new Intent(getContext(), PlayerPageActivity.class);
            intent.putExtra(PlayerPageActivity.KEY_USER_ID, parentViewModel.getUser().getId());
            if (parentViewModel.getCompetitor() instanceof User) {
                intent.putExtra(PlayerPageActivity.KEY_COMPETITOR_IS_USER, true);
            }
            intent.putExtra(PlayerPageActivity.KEY_COMPETITOR_ID, parentViewModel.getCompetitor().getId());
            startActivity(intent);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CHANGE_PLAYER) {
            if (resultCode == Activity.RESULT_OK) {
                long playerId = data.getLongExtra(PlayerManageActivity.RESPONSE_PLAYER_ID, -1);
                boolean isUser = data.getBooleanExtra(PlayerManageActivity.RESPONSE_PLAYER_IS_USER, false);
                parentViewModel.queryCompetitor(playerId, isUser);
            }
        }
        if (requestCode == REQUEST_CHANGE_USER) {
            if (resultCode == Activity.RESULT_OK) {
                long userId = data.getLongExtra(PlayerManageActivity.RESPONSE_PLAYER_ID, -1);
                parentViewModel.reLoadUser(userId);
            }
        }
    }
}
