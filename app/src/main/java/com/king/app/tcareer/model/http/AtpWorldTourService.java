package com.king.app.tcareer.model.http;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Desc:fetch data from AtpWorldTour website
 *
 * @author：Jing Yang
 * @date: 2018/5/15 9:49
 */
public interface AtpWorldTourService {
    @GET
    Observable<ResponseBody> getRankList(@Url String url);

    @GET
    Observable<ResponseBody> getPlayerOverView(@Url String url);
}
