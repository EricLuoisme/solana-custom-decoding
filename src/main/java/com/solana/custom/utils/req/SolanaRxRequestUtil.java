package com.solana.custom.utils.req;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.solana.custom.dto.*;
import com.solana.custom.dto.metaplex.MetaplexStandardJsonObj;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufMono;
import reactor.netty.http.client.HttpClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.solana.custom.constant.JsonRpcConstants.*;

/**
 * Solana request util, reactive ver.
 *
 * @author Roylic
 * 2023/3/24
 */
public class SolanaRxRequestUtil {

    private static final int RETRY_COUNT = 3;
    private static final MediaType MEDIA_TYPE = MediaType.parse("application/json");

    /**
     * Get LatestBlock
     */
    public static Optional<LatestBlock> rpcLatestBlock(OkHttpClient okHttpClient, String nodeUrl) {
        String resp = executeJsonRpcReq(okHttpClient, nodeUrl, RPC_LATEST_BLOCK);
        if (StringUtils.isNotBlank(resp)) {
            try {
                JSONObject respObject = JSONObject.parseObject(resp);
                return Optional.of(
                        JSONObject.parseObject(respObject.getJSONObject("result").getJSONObject("value").toJSONString(),
                                LatestBlock.class));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Optional.empty();
    }

    /**
     * Get Latest slot
     */
    public static Optional<Long> rpcLatestSlot(OkHttpClient okHttpClient, String nodeUrl) {
        String resp = executeJsonRpcReq(okHttpClient, nodeUrl, RPC_LATEST_SLOT);
        if (StringUtils.isNotBlank(resp)) {
            try {
                JSONObject respObject = JSONObject.parseObject(resp);
                return Optional.of(respObject.getLong("result"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Optional.empty();
    }

    /**
     * Get Block Full Detail
     */
    public static Optional<BlockResult> rpcFullBlockBySlot(OkHttpClient okHttpClient, String nodeUrl, Long slot) {
        String resp = executeJsonRpcReq(okHttpClient, nodeUrl, String.format(RPC_BLOCK_BY_SLOT, slot));
        if (StringUtils.isNotBlank(resp)) {
            try {
                JSONObject respObject = JSONObject.parseObject(resp);
                JSONObject error = respObject.getJSONObject("error");
                if (null != error) {
                    BlockResult result = null;
                    if (error.getString("message").contains("was skipped, or missing")) {
                        result = BlockResult.builder().blockSkip(true).build();
                    }
                    return Optional.ofNullable(result);
                }
                return Optional.of(
                        JSONObject.parseObject(respObject.getJSONObject("result").toJSONString(),
                                BlockResult.class));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Optional.empty();
    }

    /**
     * Get associated token accounts
     */
    public static List<AccountInfo> rpcAssociatedTokenAccountByOwner(OkHttpClient okHttpClient, String nodeUrl, String address) {
        String resp = executeJsonRpcReq(okHttpClient, nodeUrl, String.format(RPC_ASSOCIATED_TOKEN_ACCOUNT, address));
        if (StringUtils.isNotBlank(resp)) {
            try {
                JSONObject respObject = JSONObject.parseObject(resp);
                return JSON.parseArray(respObject.getJSONObject("result").getJSONArray("value").toJSONString(), AccountInfo.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();

    }

    /**
     * Get account the latest signatures with limits
     */
    public static List<SigResult> rpcAccountSignaturesWithLimit(OkHttpClient okHttpClient, String nodeUrl, String account, int limit) {
        String resp = executeJsonRpcReq(okHttpClient, nodeUrl, String.format(RPC_ACC_SIGNATURE_LIMIT, account, limit));
        if (StringUtils.isNotBlank(resp)) {
            try {
                JSONObject respObject = JSONObject.parseObject(resp);
                return JSON.parseArray(JSON.toJSONString(respObject.getJSONArray("result")), SigResult.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }

    /**
     * Get transaction full detail by signature
     */
    public static Optional<TxnResult> rpcTransactionBySignature(OkHttpClient okHttpClient, String nodeUrl, String signature) {
        String resp = executeJsonRpcReq(okHttpClient, nodeUrl, String.format(RPC_FULL_TRANSACTION, signature));
        if (StringUtils.isNotBlank(resp)) {
            try {
                JSONObject respObject = JSONObject.parseObject(resp);
                return Optional.of(
                        JSON.parseObject(respObject.getJSONObject("result").toJSONString(),
                                TxnResult.class));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Optional.empty();
    }

    /**
     * Get AccountInfoData
     *
     * @param okHttpClient
     * @param account
     * @return
     */
    public static Optional<String> rpcAccountInfoDataBase64(OkHttpClient okHttpClient, String nodeUrl, String account) {
        String resp = executeJsonRpcReq(okHttpClient, nodeUrl, String.format(RPC_ACCOUNT_INFO_DATA, account));
        Optional<String> opResult = Optional.empty();
        if (StringUtils.isNotBlank(resp)) {
            try {
                JSONObject respObject = JSONObject.parseObject(resp);

                JSONObject valueObject = respObject.getJSONObject("result").getJSONObject("value");
                if (valueObject != null) {
                    opResult = Optional.of(valueObject.getJSONArray("data").get(0).toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return opResult;
    }

    /**
     * Request external metaplex json result
     */
    public static Optional<MetaplexStandardJsonObj> metaplexExternalJsonReq(OkHttpClient okHttpClient, String nftUrl) {
        Request request = new Request.Builder()
                .url(nftUrl)
                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            return Optional.of(JSONObject.parseObject(response.body().string(), MetaplexStandardJsonObj.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }


    private static Mono<String> executeJsonRpcReq(HttpClient client, String jsonMsg) {
        return client
                .headers(head -> head.set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON))
                .post()
                .send(ByteBufMono.fromString(Mono.just(jsonMsg)))
                .responseSingle((httpClientResponse, byteBufMono) ->
                        HttpResponseStatus.OK.equals(httpClientResponse.status())
                                ? byteBufMono.asString()
                                : byteBufMono.asString().flatMap(errorMessage -> Mono.error(new RuntimeException("HTTP Error: " + httpClientResponse.status().code() + ", Message: " + errorMessage))))
                .retry(RETRY_COUNT);
    }

}
