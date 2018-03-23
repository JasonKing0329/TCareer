package com.king.app.tcareer.page.record.editor;

import com.king.app.tcareer.base.BaseView;
import com.king.app.tcareer.model.bean.AutoFillMatchBean;
import com.king.app.tcareer.model.bean.CompetitorBean;
import com.king.app.tcareer.model.db.entity.MatchNameBean;
import com.king.app.tcareer.model.db.entity.Record;
import com.king.app.tcareer.model.db.entity.Score;
import com.king.app.tcareer.model.db.entity.User;

import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/11/28 9:47
 */
public interface IEditorView extends BaseView {

    void showUser(User user);

    void showRecord(Record record);

    void showH2h(int win, int lose);

    void updateSuccess();

    void insertSuccess();

    void showMatchAutoFill(AutoFillMatchBean autoFill);

    void showMatchInfor(Record record, MatchNameBean mMatchNameBean, CompetitorBean mCompetitor, List<Score> mScoreList);

    void showMatchFill(int year, String round);
}

