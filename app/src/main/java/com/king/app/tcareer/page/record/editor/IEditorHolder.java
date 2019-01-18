package com.king.app.tcareer.page.record.editor;

import android.app.Activity;
import android.support.v4.app.FragmentManager;

import com.king.app.tcareer.model.bean.CompetitorBean;
import com.king.app.tcareer.model.db.entity.MatchNameBean;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.model.db.entity.User;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/3/13 14:38
 */
public interface IEditorHolder {

    Activity getActivity();

    FragmentManager getSupportFragmentManager();

    EditorPresenter getPresenter();

    void selectPlayer();

    void selectMatch();

    void selectUser();
}
