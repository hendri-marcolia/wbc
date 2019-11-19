package jp.co.soramitsu.iroha.android.sample.EndPoint;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static jp.co.soramitsu.iroha.android.sample.Constants.BANK_URL;

public class EndPoint {
    private static  WbcApi wbcApi;
    public static WbcApi wbcApi(){
        if (wbcApi == null) {
            wbcApi = new Retrofit
                    .Builder()
                    .baseUrl(BANK_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()
                    .create(WbcApi.class);
        }
        return  wbcApi;
    }
}
