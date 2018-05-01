package com.king.app.tcareer.page.player.page;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述: FragmentPagerAdapter of PageFragment
 * 这里要用FragmentStatePagerAdapter，不用FragmentPagerAdapter
 * 因为FragmentPagerAdapter在ViewPager切换中会缓存fragment，被释放的只是Fragment的view，即只执行了onDestroyView而没有执行onDestroy
 * 这就导致了尽管PageFragment动态变化时执行的是newInstance并且设置了新的arguments，但是getArguments()还是拿的是缓存里的参数，（没有绑定除view意外的元素）
 * 所以，根据功能需求，这里需要用FragmentStatePagerAdapter保证Fragment被销毁
 * <p/>作者：景阳
 * <p/>创建时间: 2017/11/20 16:09
 */
public class PageAdapter extends FragmentStatePagerAdapter {

    private List<PageFragment> ftList;

    public PageAdapter(FragmentManager fm) {
        super(fm);
        ftList = new ArrayList<>();
    }

    public void addFragment(PageFragment fragment) {
        ftList.add(fragment);
    }

    @Override
    public PageFragment getItem(int position) {
        return ftList.get(position);
    }

    @Override
    public int getCount() {
        return ftList == null ? 0:ftList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return super.getPageTitle(position);
    }
}
