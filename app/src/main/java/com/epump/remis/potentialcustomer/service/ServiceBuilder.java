package com.epump.remis.potentialcustomer.service;

import android.os.Build;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionSpec;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceBuilder {

    private static final String URL = "https://api.epump.com.ng/";

    public static HttpLoggingInterceptor sLoggingInterceptor =
            new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);

    private static OkHttpClient.Builder okHttp =
            new OkHttpClient.Builder()
            .writeTimeout(15, TimeUnit.SECONDS)
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request request =chain.request();
                            Response response = chain.proceed(chain.request());
                            if(response.code() == 307){
                                request = request.newBuilder()
                                        .url(response.header("Location"))
                                        .build();
                                response = chain.proceed(request);
                            }
                            return response;
                        }
                    })
            .addInterceptor(sLoggingInterceptor);
    private static Retrofit.Builder sBuilder = new Retrofit.Builder().baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttp.build());
    private static  Retrofit sRetrofit = sBuilder.build();

    public static <S> S buildeService(Class<S> serviceType){
        return sRetrofit.create(serviceType);
    }

}
