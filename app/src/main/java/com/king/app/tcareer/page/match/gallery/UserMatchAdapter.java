package com.king.app.tcareer.page.match.gallery;

import android.content.res.Resources;

import com.king.app.tcareer.R;
import com.king.app.tcareer.base.mvvm.BaseBindingAdapter;
import com.king.app.tcareer.conf.AppConstants;
import com.king.app.tcareer.databinding.AdapterUserMatchItemBinding;
import com.king.app.tcareer.model.ImageProvider;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/315 10:1
 */
public class UserMatchAdapter extends BaseBindingAdapter<AdapterUserMatchItemBinding, UserMatchBean> {

    private int colorHard;
    private int colorClay;
    private int colorGrass;
    private int colorInnerHard;
    private String[] courtValues;

    public UserMatchAdapter(Resources resources) {
        courtValues = AppConstants.RECORD_MATCH_COURTS;
        colorHard = resources.getColor(R.color.swipecard_text_hard);
        colorClay = resources.getColor(R.color.swipecard_text_clay);
        colorGrass = resources.getColor(R.color.swipecard_text_grass);
        colorInnerHard = resources.getColor(R.color.swipecard_text_innerhard);

    }

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_user_match_item;
    }

    @Override
    protected void onBindItem(AdapterUserMatchItemBinding binding, int position, UserMatchBean bean) {
        binding.setBean(bean);
        binding.tvTotal.setText("总胜负  " + bean.getWin() + "胜" + bean.getLose() + "负");
        String best = "最佳战绩  " + bean.getBest();
        if (bean.getBestYears().length() > 0) {
            best = best + "(" + bean.getBestYears() + ")";
        }
        binding.tvBest.setText(best);

        int color = getCardIndexColor(position);
        binding.tvCourt.setTextColor(color);
    }

    public int getCardIndexColor(int position) {
        int color = colorHard;
        if (position < list.size()) {
            if (list.get(position).getNameBean().getMatchBean().getCourt().equals(courtValues[1])) {
                color = colorClay;
            }
            else if (list.get(position).getNameBean().getMatchBean().getCourt().equals(courtValues[2])) {
                color = colorGrass;
            }
            else if (list.get(position).getNameBean().getMatchBean().getCourt().equals(courtValues[3])) {
                color = colorInnerHard;
            }
        }
        return color;
    }

    public void refreshImage(int index) {
        String url = ImageProvider.getMatchHeadPath(list.get(index).getNameBean().getName()
                , list.get(index).getNameBean().getMatchBean().getCourt());
        list.get(index).setImageUrl(url);
    }
}
