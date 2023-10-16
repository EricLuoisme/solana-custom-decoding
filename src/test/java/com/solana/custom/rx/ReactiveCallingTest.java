package com.solana.custom.rx;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solana.custom.constant.NetConstants;
import com.solana.custom.dto.json.JsonRpc;
import com.solana.custom.utils.TrustAllX509CertManager;
import io.netty.channel.ChannelOption;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufMono;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static com.solana.custom.constant.JsonRpcConstants.RPC_ASSOCIATED_TOKEN_ACCOUNT;

public class ReactiveCallingTest {

    private final static ObjectMapper om = new ObjectMapper();


    private HttpClient httpClient = HttpClient.create()
            .secure(sslSpec -> {
                try {
                    sslSpec.sslContext(
                            SslContextBuilder.forClient()
                                    .trustManager(new TrustAllX509CertManager())
                                    .build());
                } catch (SSLException e) {
                    throw new RuntimeException(e);
                }
            })
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
            .responseTimeout(Duration.ofMillis(5000))
            .doOnConnected(connection -> {
                connection.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS));
                connection.addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS));
            });

    @Test
    public void reactiveBalance() throws JsonProcessingException {

        String address = "AnayTW335MabjhtXTJeBit5jdLhNeUVBVPXeRKCid79D";
        Mono<String> respStrMono = httpClient
                .baseUrl(NetConstants.DEVNET_URL)
                .headers(h -> h.set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON))
                .post()
                .send(
                        ByteBufMono.fromString(
                                Mono.just(String.format(RPC_ASSOCIATED_TOKEN_ACCOUNT, address)))
                )
                .responseSingle((httpClientResponse, byteBufMono) -> {
                    if (httpClientResponse.status().equals(HttpResponseStatus.OK)) {
                        return byteBufMono.asString();
                    }
                    return byteBufMono.asString()
                            .flatMap(errorMessage -> Mono.error(
                                    new RuntimeException("HTTP Error: " + httpClientResponse.status().code() + ", Message: " + errorMessage)));
                });

        String respStr = respStrMono.block();
        JsonRpc jsonRpc = JSON.parseObject(respStr, JsonRpc.class);
        System.out.println(om.writerWithDefaultPrettyPrinter().writeValueAsString(jsonRpc));
    }


}
