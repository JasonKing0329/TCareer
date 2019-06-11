package com.king.app.tcareer.page.record.editor;

import android.arch.lifecycle.ViewModelProviders;
import android.view.View;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.mvvm.MvvmActivity;
import com.king.app.tcareer.databinding.ActivityRecordEditorBinding;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/3/13 13:23
 */
public class RecordEditorActivity extends MvvmActivity<ActivityRecordEditorBinding, EditorViewModel> {

    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_RECORD_ID = "record_id";
    public static final String KEY_CHOOSE_USER = "choose_user";

    private PlayerFragment ftPlayer;
    private MatchFragment ftMatch;

    @Override
    protected int getContentView() {
        return R.layout.activity_record_editor;
    }

    @Override
    protected EditorViewModel createViewModel() {
        return ViewModelProviders.of(this).get(EditorViewModel.class);
    }

    @Override
    protected void initView() {
        initParentView();

        mBinding.tvNextPage.setOnClickListener(v -> {
            mBinding.toolbar.setTitle(getResources().getString(R.string.match_infor));
            mBinding.tvPreviousPage.setVisibility(View.VISIBLE);
            mBinding.tvNextPage.setVisibility(View.INVISIBLE);
            mBinding.tvDone.setVisibility(View.VISIBLE);
            showMatchView();
        });
        mBinding.tvDone.setOnClickListener(v -> {
            if (mBinding.tvContinue.getVisibility() == View.VISIBLE) {
                finish();
            }
            else {
                mModel.executeUpdate();
            }
        });
        mBinding.tvContinue.setOnClickListener(v -> continueInsert());
        mBinding.tvPreviousPage.setOnClickListener(v -> {
            mBinding.toolbar.setTitle(getResources().getString(R.string.player_infor));
            mBinding.tvPreviousPage.setVisibility(View.INVISIBLE);
            mBinding.tvNextPage.setVisibility(View.VISIBLE);
            mBinding.tvDone.setVisibility(View.INVISIBLE);
            showPlayerView();
        });
    }

    private void initParentView() {
        // 只有title必须在setSupportActionBar之前调用，其他都必须在setSupportActionBar之后调用
        mBinding.toolbar.setTitle(getResources().getString(R.string.player_infor));
        setSupportActionBar(mBinding.toolbar);
        mBinding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mBinding.toolbar.setNavigationOnClickListener(v -> {
            // 加入了转场动画，必须用onBackPressed，finish无效果
            onBackPressed();
        });
        mBinding.toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mBinding.toolbar.setTitleTextColor(getResources().getColor(R.color.white));
    }

    private void showPlayerView() {
        if (ftPlayer == null) {
            ftPlayer = new PlayerFragment();
            ftPlayer.setSupportChooseUser(isSupportChooseUser());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fl_ft, ftPlayer, "PlayerFragment")
                    .commit();
        }
        else {
            getSupportFragmentManager().beginTransaction()
                    .show(ftPlayer)
                    .hide(ftMatch)
                    .commit();
        }
    }

    private void showMatchView() {
        if (ftMatch == null) {
            ftMatch = new MatchFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fl_ft, ftMatch, "MatchFragment")
                    .hide(ftPlayer)
                    .commit();
        }
        else {
            getSupportFragmentManager().beginTransaction()
                    .show(ftMatch)
                    .hide(ftPlayer)
                    .commit();
        }
    }

    @Override
    protected void initData() {

        mModel.insertSuccess.observe(this, success -> {
            showMessageShort("添加成功");
            setResult(RESULT_OK);
            mBinding.tvContinue.setVisibility(View.VISIBLE);
        });
        mModel.updateSuccess.observe(this, success -> {
            setResult(RESULT_OK);
            finish();
        });

        long userId = getIntent().getLongExtra(KEY_USER_ID, -1);
        long recordId = getIntent().getLongExtra(KEY_RECORD_ID, -1);
        mModel.saveInit(userId, recordId);

        showPlayerView();
    }

    private boolean isSupportChooseUser() {
        return getIntent().getBooleanExtra(KEY_CHOOSE_USER, false);
    }

    private void continueInsert() {
        mModel.reset();
        mBinding.tvContinue.setVisibility(View.INVISIBLE);
        mBinding.toolbar.setTitle(getResources().getString(R.string.player_infor));
        mBinding.tvPreviousPage.setVisibility(View.INVISIBLE);
        mBinding.tvNextPage.setVisibility(View.VISIBLE);
        mBinding.tvDone.setVisibility(View.INVISIBLE);
        ftPlayer.reset();
        ftMatch.reset();
        showPlayerView();
    }
}
