package com.king.app.tcareer.page.match.gallery;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.mvvm.MvvmActivity;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.databinding.ActivityUserMatchBinding;
import com.king.app.tcareer.model.http.Command;
import com.king.app.tcareer.model.http.bean.ImageUrlBean;
import com.king.app.tcareer.page.imagemanager.ImageManager;
import com.king.app.tcareer.page.match.page.MatchPageActivity;
import com.king.app.tcareer.view.widget.discrete.DiscreteScrollView;
import com.king.app.tcareer.view.widget.discrete.transform.ScaleTransformer;

import java.util.List;

/**
 * 描述: 按week排列的横向gallery赛事总览，进入时定位到离当前周数最近的赛事
 * <p/>作者：景阳
 * <p/>创建时间: 2017/3/15 9:42
 */
public class UserMatchActivity extends MvvmActivity<ActivityUserMatchBinding, UserMatchViewModel> implements DiscreteScrollView.CurrentItemChangeListener {

    public static final String KEY_START_POSITION = "start_position";

    public static final String KEY_USER_ID = "user_id";

    private UserMatchAdapter userMatchAdapter;

    private int nMatchIndex;
    private MatchScrollManager scrollManager;

    private ImageManager imageManager;

    @Override
    protected int getContentView() {
        return R.layout.activity_user_match;
    }

    @Override
    protected UserMatchViewModel createViewModel() {
        return ViewModelProviders.of(this).get(UserMatchViewModel.class);
    }

    @Override
    protected void initView() {
        mBinding.ivBack.setOnClickListener(v -> onBackPressed());
        mBinding.ivRefresh.setOnClickListener(v -> userMatchAdapter.refreshImage(nMatchIndex));
        mBinding.btnDownload.setOnClickListener(v -> imageManager.download(Command.TYPE_IMG_MATCH, userMatchAdapter.getItem(nMatchIndex).getNameBean().getName()));
        mBinding.ivDelete.setOnClickListener(v -> {
            imageManager.setDataProvider(interactionController -> {
                ImageUrlBean bean = interactionController.getMatchImageUrlBean(userMatchAdapter.getItem(nMatchIndex).getNameBean().getName());
                return bean;
            });
            imageManager.manageLocal();
        });

        imageManager = new ImageManager(this);
        imageManager.setOnActionListener(new ImageManager.OnActionListener() {
            @Override
            public void onRefresh(int position) {

            }

            @Override
            public void onManageFinished() {
                userMatchAdapter.notifyDataSetChanged();
            }

            @Override
            public void onDownloadFinished() {
                userMatchAdapter.notifyDataSetChanged();
            }

            @Override
            public FragmentManager getFragmentManager() {
                return getSupportFragmentManager();
            }
        });
    }

    @Override
    protected void initData() {
        scrollManager = new MatchScrollManager(this);
        // 绑定滑动过程中联动变化的view
        scrollManager.bindBehaviorView(mBinding.matchBk, mBinding.btnDownload);

        initMatchGallery();
    }

    private void initMatchGallery() {
        mBinding.svGallery.setCurrentItemChangeListener(this);
        mBinding.svGallery.setItemTransitionTimeMillis(200);
        mBinding.svGallery.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.8f)
                .build());
        mBinding.svGallery.setScrollStateChangeListener(scrollManager);
        
        mModel.matchesObserver.observe(this, list -> showMatches(list));
        mModel.loadMatches(getIntent().getLongExtra(KEY_USER_ID, -1));
    }

    private void showMatches(List<UserMatchBean> list) {
        // 绑定滑动依据数据
        scrollManager.bindData(list);

        userMatchAdapter = new UserMatchAdapter(getResources());
        userMatchAdapter.setList(list);
        userMatchAdapter.setOnItemClickListener((view, position, data) -> {
            Intent intent = new Intent().setClass(view.getContext(), MatchPageActivity.class);
            intent.putExtra(MatchPageActivity.KEY_MATCH_NAME_ID, list.get(position).getNameBean().getId());
            intent.putExtra(MatchPageActivity.KEY_USER_ID, mModel.getUser().getId());
            startActivity(intent);
        });
        mBinding.svGallery.setAdapter(userMatchAdapter);

        // 定位到最近的赛事或者intent指定的赛事
        focusToLatestWeek();
    }

    private void focusToLatestWeek() {

        String pos = getIntent().getStringExtra(KEY_START_POSITION);
        final int position;
        if (TextUtils.isEmpty(pos)) {
            position = mModel.findLatestWeekItem();
        }
        else {
            position = Integer.parseInt(pos);
        }
        mBinding.svGallery.scrollToPosition(position);

        // 必须post，因为在GradientBkView里的相关计算getWidth()和getHeight()可能还等于0，渐变颜色的相关区域跟其有关
        // 不post的话会造成只有一种颜色
        mBinding.matchBk.post(() -> scrollManager.initPosition(position));
    }

    private void onItemChanged(UserMatchBean bean) {
        mBinding.tvName.setText(bean.getNameBean().getName());
        mBinding.tvPlace.setText(bean.getNameBean().getMatchBean().getCountry()
                + "/" + bean.getNameBean().getMatchBean().getCity());
        mBinding.tvMonth.setText(AppConstants.MONTH_ENG[bean.getNameBean().getMatchBean().getMonth() - 1]);
        mBinding.tvWeek.setText("week " + bean.getNameBean().getMatchBean().getWeek());
    }

    @Override
    public void onCurrentItemChanged(RecyclerView.ViewHolder viewHolder, int adapterPosition) {
        nMatchIndex = adapterPosition;
        onItemChanged(mModel.getUserMatchBean(adapterPosition));
    }
}
