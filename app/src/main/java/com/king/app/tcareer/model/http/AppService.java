package com.king.app.tcareer.model.http;

import com.king.app.tcareer.model.http.bean.AppCheckBean;
import com.king.app.tcareer.model.http.bean.GdbRespBean;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2016/9/5.
 */
public interface AppService {
    @GET("online")
    Observable<GdbRespBean> isServerOnline();

    @GET("checkNew")
    Observable<AppCheckBean> checkAppUpdate(@Query("type") String type, @Query("version") String version);

    @GET("checkNew")
    Observable<AppCheckBean> checkGdbDatabaseUpdate(@Query("type") String type, @Query("version") String version);
}
