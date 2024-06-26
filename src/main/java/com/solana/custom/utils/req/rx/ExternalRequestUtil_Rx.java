package com.solana.custom.utils.req.rx;

import com.alibaba.fastjson2.JSONObject;
import com.solana.custom.dto.metaplex.MetaplexStandardJsonObj;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import reactor.core.publisher.Mono;

import java.io.IOException;

public class ExternalRequestUtil_Rx {

    private final static OkHttpClient client = new OkHttpClient();

    public static Mono<MetaplexStandardJsonObj> metaplexExternalJsonReq(String nftUrl) {
        Request request = new Request.Builder()
                .url(nftUrl)
                .build();
        try {
            Response response = client.newCall(request).execute();
            return Mono.just(JSONObject.parseObject(response.body().string(), MetaplexStandardJsonObj.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Mono.empty();
    }


}
