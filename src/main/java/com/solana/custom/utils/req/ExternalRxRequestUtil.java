package com.solana.custom.utils.req;

import com.alibaba.fastjson2.JSONObject;
import com.solana.custom.dto.metaplex.MetaplexStandardJsonObj;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Optional;

public class ExternalRxRequestUtil {

    private final static OkHttpClient client = new OkHttpClient();

    public static Optional<MetaplexStandardJsonObj> metaplexExternalJsonReq(String nftUrl) {
        Request request = new Request.Builder()
                .url(nftUrl)
                .build();
        try {
            Response response = client.newCall(request).execute();
            return Optional.of(JSONObject.parseObject(response.body().string(), MetaplexStandardJsonObj.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }


}
