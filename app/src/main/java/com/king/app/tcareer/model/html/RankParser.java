package com.king.app.tcareer.model.html;

import com.king.app.tcareer.base.TApplication;
import com.king.app.tcareer.model.db.entity.PlayerAtpBean;
import com.king.app.tcareer.model.db.entity.PlayerAtpBeanDao;
import com.king.app.tcareer.model.db.entity.PlayerBean;
import com.king.app.tcareer.model.db.entity.PlayerBeanDao;
import com.king.app.tcareer.utils.DebugLog;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.functions.Function;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/5/15 10:34
 */
public class RankParser extends AbsParser {

    public Observable<Boolean> parse(final File file) {
        return Observable.create(new ObservableOnSubscribe<List<PlayerAtpBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<PlayerAtpBean>> e) throws Exception {

                PlayerAtpBeanDao dao = TApplication.getInstance().getDaoSession().getPlayerAtpBeanDao();
                List<PlayerAtpBean> insertList = new ArrayList<>();

                // 文件不存在则从网络里重新，虽然这个可以满足文件不存在是从网络里重新下载并且还存到file里，
                // 但是这种方式不能自定义user agent
//                Document document = Jsoup.parse(file, "UTF-8", AtpWorldTourParams.URL_RANK);

                Document document = Jsoup.parse(file, "UTF-8");
                Elements playerCell = document.select("td.player-cell");
                for (int i = 0; i < playerCell.size(); i ++) {
                    Element cell = playerCell.get(i);
                    String url = cell.select("a").get(0).attr("href");
                    Element span = cell.selectFirst("span.player-name");
                    String name = span.select("font").text();
                    name = name.replace(".", "");

                    String[] arrays = url.split("/");
                    String id = arrays[arrays.length - 2];

                    // 只插入不在数据库中的数据
                    PlayerAtpBean bean = dao.queryBuilder()
                            .where(PlayerAtpBeanDao.Properties.Id.eq(id))
                            .build().unique();
                    if (bean == null) {
                        DebugLog.e("insert i=" + i + ", url=" + url + ", name=" + name);
                        bean = new PlayerAtpBean();
                        bean.setId(id);
                        bean.setName(name);
                        bean.setOverViewUrl(url);
                        insertList.add(bean);
                    }
                }

                e.onNext(insertList);
            }
        }).flatMap(new Function<List<PlayerAtpBean>, ObservableSource<Boolean>>() {
            @Override
            public ObservableSource<Boolean> apply(List<PlayerAtpBean> playerAtpBeans) throws Exception {
                return insertAndRelate(playerAtpBeans);
            }
        });
    }

    private ObservableSource<Boolean> insertAndRelate(final List<PlayerAtpBean> insertList) {
        return new ObservableSource<Boolean>() {
            @Override
            public void subscribe(Observer<? super Boolean> observer) {
                PlayerAtpBeanDao dao = TApplication.getInstance().getDaoSession().getPlayerAtpBeanDao();
                if (insertList.size() > 0) {
                    dao.insertInTx(insertList);

                    PlayerBeanDao playerDao = TApplication.getInstance().getDaoSession().getPlayerBeanDao();
                    for (PlayerAtpBean atpBean:insertList) {
                        // NameEng在PlayerBeanDao中不是主键，有可能重复
                        List<PlayerBean> list = playerDao.queryBuilder()
                                .where(PlayerBeanDao.Properties.NameEng.eq(atpBean.getName()))
                                .build().list();
                        for (PlayerBean player:list) {
                            player.setAtpId(atpBean.getId());
                            playerDao.update(player);
                            playerDao.detach(player);
                        }
                    }
                }
                observer.onNext(true);
            }
        };
    }
}
