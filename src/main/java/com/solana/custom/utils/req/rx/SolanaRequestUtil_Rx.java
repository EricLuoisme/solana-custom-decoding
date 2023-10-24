package com.solana.custom.utils.req.rx;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.solana.custom.dto.*;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufMono;
import reactor.netty.http.client.HttpClient;

import java.util.List;

import static com.solana.custom.constant.JsonRpcConstants.*;

/**
 * Solana request util, reactive ver.
 * Caller should handle the possible Mono error
 *
 * @author Roylic
 * 2023/10/16
 */
@Slf4j
public class SolanaRequestUtil_Rx {

    private static final int RETRY_COUNT = 3;

    /**
     * Get LatestBlock
     */
    public static Mono<LatestBlock> rpcLatestBlock(HttpClient client) {
        return executeJsonRpcReq(client, RPC_LATEST_BLOCK)
                .flatMap(respJsonStr -> {
                    JSONObject respObject = JSONObject.parseObject(respJsonStr);
                    return null != respObject
                            ? Mono.just(JSONObject.parseObject(respObject.getJSONObject("result").getJSONObject("value").toJSONString(), LatestBlock.class))
                            : Mono.error(new RuntimeException("Json format Error"));
                })
                .onErrorResume(err -> {
                    log.error("<<< [SolanaRxRequestUtil] got exception on rpcLatestBlock:{}", err.getMessage());
                    return Mono.empty();
                });
    }

    /**
     * Get Latest slot
     */
    public static Mono<Long> rpcLatestSlot(HttpClient client) {
        return executeJsonRpcReq(client, RPC_LATEST_SLOT)
                .flatMap(respJsonStr -> {
                    JSONObject respObject = JSONObject.parseObject(respJsonStr);
                    return null != respObject
                            ? Mono.just(respObject.getLong("result"))
                            : Mono.error(new RuntimeException("Json format Error"));
                })
                .onErrorResume(err -> {
                    log.error("<<< [SolanaRxRequestUtil] got exception on rpcLatestSlot:{}", err.getMessage());
                    return Mono.empty();
                });
    }

    /**
     * Get Block Full Detail
     */
    public static Mono<BlockResult> rpcFullBlockBySlot(HttpClient client, Long slot) {
        return executeJsonRpcReq(client, String.format(RPC_BLOCK_BY_SLOT, slot))
                .flatMap(respJsonStr -> {
                    JSONObject jsonObject = JSONObject.parseObject(respJsonStr);
                    JSONObject error = jsonObject.getJSONObject("error");
                    if (null != error) {
                        return error.getString("message").contains("was skipped, or missing")
                                ? Mono.just(BlockResult.builder().blockSkip(true).build())
                                : Mono.error(new RuntimeException("Json format Error"));
                    }
                    return Mono.just(JSONObject.parseObject(jsonObject.getJSONObject("result").toJSONString(), BlockResult.class));
                })
                .onErrorResume(err -> {
                    log.error("<<< [SolanaRxRequestUtil] got exception on rpcFullBlockBySlot:{}", err.getMessage());
                    return Mono.empty();
                });
    }

    /**
     * Get associated token accounts
     */
    public static Mono<List<AccountInfo>> rpcAssociatedTokenAccountByOwner(HttpClient client, String address) {
        return executeJsonRpcReq(client, String.format(RPC_ASSOCIATED_TOKEN_ACCOUNT, address))
                .flatMap(respJsonStr -> {
                    JSONObject respObject = JSONObject.parseObject(respJsonStr);
                    return null != respObject
                            ? Mono.just(JSON.parseArray(respObject.getJSONObject("result").getJSONArray("value").toJSONString(), AccountInfo.class))
                            : Mono.error(new RuntimeException("Json format Error"));
                })
                .onErrorResume(err -> {
                    log.error("<<< [SolanaRxRequestUtil] got exception on rpcAssociatedTokenAccountByOwner:{}", err.getMessage());
                    return Mono.empty();
                });
    }

    /**
     * Get account the latest signatures with limits
     */
    public static Mono<List<SigResult>> rpcAccountSignaturesWithLimit(HttpClient client, String account, int limit) {
        return executeJsonRpcReq(client, String.format(RPC_ACC_SIGNATURE_LIMIT, account, limit))
                .flatMap(respJsonStr -> {
                    JSONObject respObject = JSONObject.parseObject(respJsonStr);
                    return null != respObject
                            ? Mono.just(JSON.parseArray(JSON.toJSONString(respObject.getJSONArray("result")), SigResult.class))
                            : Mono.error(new RuntimeException("Json format Error"));
                })
                .onErrorResume(err -> {
                    log.error("<<< [SolanaRxRequestUtil] got exception on rpcAccountSignaturesWithLimit:{}", err.getMessage());
                    return Mono.empty();
                });

    }

    /**
     * Get transaction full detail by signature
     */
    public static Mono<TxnResult> rpcTransactionBySignature(HttpClient client, String signature) {
        return executeJsonRpcReq(client, String.format(RPC_FULL_TRANSACTION, signature))
                .flatMap(respJsonStr -> {
                    JSONObject respObject = JSONObject.parseObject(respJsonStr);
                    return null != respObject
                            ? Mono.just(JSON.parseObject(respObject.getJSONObject("result").toJSONString(), TxnResult.class))
                            : Mono.error(new RuntimeException("Json format Error"));
                })
                .onErrorResume(err -> {
                    log.error("<<< [SolanaRxRequestUtil] got exception on rpcTransactionBySignature:{}", err.getMessage());
                    return Mono.empty();
                });
    }

    /**
     * Get AccountInfoData
     */
    public static Mono<String> rpcAccountInfoDataBase64(HttpClient client, String account) {
        return executeJsonRpcReq(client, String.format(RPC_ACCOUNT_INFO_DATA, account))
                .flatMap(respJsonStr -> {
                    JSONObject respObject = JSONObject.parseObject(respJsonStr);
                    JSONObject valueObject = respObject.getJSONObject("result").getJSONObject("value");
                    return null != valueObject
                            ? Mono.just(valueObject.getJSONArray("data").get(0).toString())
                            : Mono.error(new RuntimeException("Json format Error"));
                })
                .onErrorResume(err -> {
                    log.error("<<< [SolanaRxRequestUtil] got exception on rpcAccountInfoDataBase64:{}", err.getMessage());
                    return Mono.empty();
                });
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
