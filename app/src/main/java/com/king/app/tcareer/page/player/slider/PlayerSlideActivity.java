package com.king.app.tcareer.page.player.slider;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.StyleRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.mvvm.BaseBindingAdapter;
import com.king.app.tcareer.base.mvvm.MvvmActivity;
import com.king.app.tcareer.databinding.ActivityPlayerSlideBinding;
import com.king.app.tcareer.model.bean.H2hBean;
import com.king.app.tcareer.model.db.entity.User;
import com.king.app.tcareer.page.match.page.MatchPageActivity;
import com.king.app.tcareer.page.player.page.PageRecordAdapter;
import com.king.app.tcareer.page.player.page.PlayerPageActivity;
import com.king.app.tcareer.utils.ConstellationUtil;
import com.king.app.tcareer.view.widget.cardslider.CardSliderLayoutManager;
import com.king.app.tcareer.view.widget.cardslider.CardSnapHelper;

import java.util.List;

/**
 * 描述: slider page, show players and h2h records order by insert sequence of latest record
 * <p/>作者：景阳
 * <p/>创建时间: 2017/11/24 14:53
 */
public class PlayerSlideActivity extends MvvmActivity<ActivityPlayerSlideBinding, SlideViewModel> {

    public static final String KEY_USER_ID = "key_user_id";

    private PlayerSlideAdapter playerSlideAdapter;

    private int currentPosition;
    private int countryOffset1;
    private int countryOffset2;
    private long countryAnimDuration;

    private PageRecordAdapter recordAdapter;

    @Override
    protected int getContentView() {
        return R.layout.activity_player_slide;
    }

    @Override
    protected SlideViewModel createViewModel() {
        return ViewModelProviders.of(this).get(SlideViewModel.class);
    }

    @Override
    protected void initView() {
        mBinding.ivBack.setOnClickListener(v -> onBackPressed());
        initRecyclerView();
        initCountryText();
        initSwitchers();
    }

    @Override
    protected void initData() {
        long userId = getIntent().getLongExtra(KEY_USER_ID, -1);
        
        mModel.playersObserver.observe(this, list -> onPlayerLoaded(list));
        mModel.recordsObserver.observe(this, list -> onRecordLoaded(list));
        mModel.loadPlayers(userId);
    }

    private void initRecyclerView() {
        mBinding.rvPlayers.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    onActiveCardChange();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mBinding.rvRecords.setLayoutManager(manager);
    }

    private void onPlayerLoaded(List<SlideItem<H2hBean>> playerList) {
        if (playerSlideAdapter == null) {
            playerSlideAdapter = new PlayerSlideAdapter();
            playerSlideAdapter.setList(playerList);
            playerSlideAdapter.setOnItemClickListener((BaseBindingAdapter.OnItemClickListener<SlideItem<H2hBean>>) (view, position, bean) -> {
                clickPlayer(bean.getBean(), position);
            });
            mBinding.rvPlayers.setAdapter(playerSlideAdapter);
            new CardSnapHelper().attachToRecyclerView(mBinding.rvPlayers);
            onActiveCardChange(0);
        }
        else {
            playerSlideAdapter.setList(playerList);
            playerSlideAdapter.notifyDataSetChanged();
        }
    }

    private void clickPlayer(H2hBean bean, int position) {
        CardSliderLayoutManager lm =  (CardSliderLayoutManager) mBinding.rvPlayers.getLayoutManager();

        if (lm.isSmoothScrolling()) {
            return;
        }

        final int activeCardPosition = lm.getActiveCardPosition();
        if (activeCardPosition == RecyclerView.NO_POSITION) {
            return;
        }

        if (position == activeCardPosition) {
            Intent intent = new Intent(PlayerSlideActivity.this, PlayerPageActivity.class);
            intent.putExtra(PlayerPageActivity.KEY_USER_ID, mModel.getUser().getId());
            intent.putExtra(PlayerPageActivity.KEY_COMPETITOR_ID, bean.getCompetitor().getId());
            if (bean.getCompetitor() instanceof User) {
                intent.putExtra(PlayerPageActivity.KEY_COMPETITOR_IS_USER, true);
            }
            startActivity(intent);
        } else if (position > activeCardPosition) {
            mBinding.rvPlayers.smoothScrollToPosition(position);
            onActiveCardChange(position);
        }
    }

    private void onRecordLoaded(List<Object> list) {
        if (recordAdapter == null) {
            recordAdapter = new PageRecordAdapter();
            recordAdapter.setUser(mModel.getUser());
            recordAdapter.setList(list);
            recordAdapter.setOnItemClickListener((view, position, data) -> {
                Intent intent = new Intent(PlayerSlideActivity.this, MatchPageActivity.class);
                intent.putExtra(MatchPageActivity.KEY_USER_ID, mModel.getUser().getId());
                intent.putExtra(MatchPageActivity.KEY_MATCH_NAME_ID, data.getMatchNameId());
                startActivity(intent);
            });
            mBinding.rvRecords.setAdapter(recordAdapter);
        }
        else {
            recordAdapter.setList(list);
            recordAdapter.notifyDataSetChanged();
        }
        mBinding.rvRecords.scrollToPosition(0);
    }

    private void initSwitchers() {
        mBinding.tsH2h.setFactory(new TextViewFactory(R.style.TemperatureTextView, true));

        mBinding.tsNameEng.setFactory(new TextViewFactory(R.style.PlaceTextView, false));

        mBinding.tsBirthday.setFactory(new TextViewFactory(R.style.ClockTextView, false));

        mBinding.tsPlace.setInAnimation(this, android.R.anim.fade_in);
        mBinding.tsPlace.setOutAnimation(this, android.R.anim.fade_out);
        mBinding.tsPlace.setFactory(new TextViewFactory(R.style.DescriptionTextView, false));

    }

    private void initCountryText() {
        countryAnimDuration = getResources().getInteger(R.integer.labels_animation_duration);
        countryOffset1 = getResources().getDimensionPixelSize(R.dimen.player_slide_left_offset);
        countryOffset2 = getResources().getDimensionPixelSize(R.dimen.player_slide_left_offset2);

        mBinding.tvNameChn1.setX(countryOffset1);
        mBinding.tvNameChn2.setX(countryOffset2);
        mBinding.tvNameChn2.setAlpha(0f);

//        tvNameChn1.setTypeface(Typeface.createFromAsset(getAssets(), "open-sans-extrabold.ttf"));
//        tvNameChn2.setTypeface(Typeface.createFromAsset(getAssets(), "open-sans-extrabold.ttf"));
    }

    private void setChnName(String text, boolean left2right) {
        final TextView invisibleText;
        final TextView visibleText;
        if (mBinding.tvNameChn1.getAlpha() > mBinding.tvNameChn2.getAlpha()) {
            visibleText = mBinding.tvNameChn1;
            invisibleText = mBinding.tvNameChn2;
        } else {
            visibleText = mBinding.tvNameChn2;
            invisibleText = mBinding.tvNameChn1;
        }

        final int vOffset;
        if (left2right) {
            invisibleText.setX(0);
            vOffset = countryOffset2;
        } else {
            invisibleText.setX(countryOffset2);
            vOffset = 0;
        }

        invisibleText.setText(text);

        final ObjectAnimator iAlpha = ObjectAnimator.ofFloat(invisibleText, "alpha", 1f);
        final ObjectAnimator vAlpha = ObjectAnimator.ofFloat(visibleText, "alpha", 0f);
        final ObjectAnimator iX = ObjectAnimator.ofFloat(invisibleText, "x", countryOffset1);
        final ObjectAnimator vX = ObjectAnimator.ofFloat(visibleText, "x", vOffset);

        final AnimatorSet animSet = new AnimatorSet();
        animSet.playTogether(iAlpha, vAlpha, iX, vX);
        animSet.setDuration(countryAnimDuration);
        animSet.start();
    }

    private void onActiveCardChange() {
        final int pos = ((CardSliderLayoutManager) mBinding.rvPlayers.getLayoutManager()).getActiveCardPosition();
        if (pos == RecyclerView.NO_POSITION || pos == currentPosition) {
            return;
        }

        onActiveCardChange(pos);
    }

    private void onActiveCardChange(int pos) {
        int animH[] = new int[] {R.anim.slide_in_right, R.anim.slide_out_left};
        int animV[] = new int[] {R.anim.slide_in_top, R.anim.slide_out_bottom};

        final boolean left2right = pos < currentPosition;
        if (left2right) {
            animH[0] = R.anim.slide_in_left;
            animH[1] = R.anim.slide_out_right;

            animV[0] = R.anim.slide_in_bottom;
            animV[1] = R.anim.slide_out_top;
        }

        H2hBean bean = mModel.getCompetitorList().get(pos).getBean();
        setChnName(bean.getCompetitor().getNameChn(), left2right);

        mBinding.tsH2h.setInAnimation(this, animH[0]);
        mBinding.tsH2h.setOutAnimation(this, animH[1]);
        mBinding.tsH2h.setText(bean.getWin() + " - " + bean.getLose());

        mBinding.tsNameEng.setInAnimation(this, animV[0]);
        mBinding.tsNameEng.setOutAnimation(this, animV[1]);
        mBinding.tsNameEng.setText(bean.getCompetitor().getNameEng());

        mBinding.tsBirthday.setInAnimation(this, animV[0]);
        mBinding.tsBirthday.setOutAnimation(this, animV[1]);
        try {
            mBinding.tsBirthday.setText(bean.getCompetitor().getBirthday() + "   " + ConstellationUtil.getConstellationChn(bean.getCompetitor().getBirthday()));
        } catch (ConstellationUtil.ConstellationParseException e) {
            e.printStackTrace();
            mBinding.tsBirthday.setText(bean.getCompetitor().getBirthday());
        }

        mBinding.tsPlace.setText(bean.getCompetitor().getCountry());

        currentPosition = pos;

        mModel.loadRecords(bean);
    }

    private class TextViewFactory implements  ViewSwitcher.ViewFactory {

        @StyleRes
        final int styleId;
        final boolean center;

        TextViewFactory(@StyleRes int styleId, boolean center) {
            this.styleId = styleId;
            this.center = center;
        }

        @SuppressWarnings("deprecation")
        @Override
        public View makeView() {
            final TextView textView = new TextView(PlayerSlideActivity.this);

            if (center) {
                textView.setGravity(Gravity.CENTER);
            }

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                textView.setTextAppearance(PlayerSlideActivity.this, styleId);
            } else {
                textView.setTextAppearance(styleId);
            }

            return textView;
        }

    }

}
