package com.revolut.rates.http;



import com.revolut.rates.BuildConfig;
import com.revolut.rates.http.models.ResData;

import java.io.IOException;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.GET;

public abstract class RateHttp {

    private static IRequest req;

    private static Retrofit retroCreate() {

        OkHttpClient client = RateHttpClient.getInsytOkHttpClient();

        return new Retrofit.Builder()
                 .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }

    public static IRequest getInstance() {
        if (req == null) {
            synchronized (RateHttp.class) {
                if (req == null)
                    req = retroCreate().create(IRequest.class);
            }
        }
        return req;
    }


    public interface IRequest {
        @GET("latest?base=EUR")
        Call<ResData> getRates();

    }

    public static class IResponse<T>{
        public T data;
    }

    public static class NoInternetConnection extends IOException {
        @Override
        public String getMessage() {
            return "No Internet Connection";
        }
    }


}
