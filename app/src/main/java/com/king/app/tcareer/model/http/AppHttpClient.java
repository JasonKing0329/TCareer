package com.king.app.tcareer.model.http;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * Created by Administrator on 2016/9/5.
 */
public class AppHttpClient extends BaseHttpClient {

    private AppService appService;

    private AppHttpClient() {
        super();
    }

    @Override
    protected void createService(Retrofit retrofit) {
        appService = retrofit.create(AppService.class);
    }

    @Override
    protected void extendBuilder(OkHttpClient.Builder builder) {

    }

    //在访问HttpMethods时创建单例
    private static class SingletonHolder{
        private static final AppHttpClient INSTANCE = new AppHttpClient();
    }

    //获取单例
    public static AppHttpClient getInstance(){
        return SingletonHolder.INSTANCE;
    }

    public AppService getAppService() {
        return appService;
    }
}
