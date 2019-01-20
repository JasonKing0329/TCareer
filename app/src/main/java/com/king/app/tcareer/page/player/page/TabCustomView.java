/*
 * Copyright 2017 Jiaheng Ge
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.king.app.tcareer.page.player.page;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.king.app.tcareer.R;

/**
 * custom view about tabs of TabLayout
 */
public class TabCustomView extends LinearLayout {

    private TextView countText;
    private TextView contentCategoryText;

    private String count;
    private String contentCategory;

    public TabCustomView(Context context) {
        super(context);
        init(context);
    }

    public TabCustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TabCustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.tab_custom_view_content, this, true);
        setOrientation(LinearLayout.VERTICAL);

        countText = (TextView) findViewById(R.id.count);
        contentCategoryText = (TextView) findViewById(R.id.content_category);
    }

    public void setContentCategory(@StringRes int categoryId) {
        setContentCategory(getContext().getString(categoryId));
    }

    public void setCount(String count) {
        this.count = count;
        if (TextUtils.isEmpty(count)) {
            countText.setVisibility(GONE);
        }
        else {
            countText.setVisibility(VISIBLE);
            countText.setText(count);
        }
    }

    public String getContentCategory() {
        return contentCategory;
    }

    public void setContentCategory(String category) {
        contentCategory = category;
        contentCategoryText.setText(contentCategory);
    }

    public void setTextColor(int defaultColor, int selectedColor) {
        ColorStateList list = createColorStateList(defaultColor, selectedColor);
        countText.setTextColor(list);
        contentCategoryText.setTextColor(list);
    }

    private static ColorStateList createColorStateList(int defaultColor, int selectedColor) {
        final int[][] states = new int[2][];
        final int[] colors = new int[2];
        int i = 0;

        states[i] = SELECTED_STATE_SET;
        colors[i] = selectedColor;
        i++;

        // Default enabled state
        states[i] = EMPTY_STATE_SET;
        colors[i] = defaultColor;
        i++;

        return new ColorStateList(states, colors);
    }

}

