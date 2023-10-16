package com.solana.custom.handler.rx;

import com.solana.custom.dto.extra.AccInfoDeriveNftTokenAccPair;
import com.solana.custom.dto.extra.AccountInfoFlat;
import com.solana.custom.dto.metaplex.MetaplexStandardJsonObj;
import com.solana.custom.dto.metaplex.NftItem;
import com.solana.custom.utils.req.rx.ExternalRequestUtil_Rx;
import com.solana.custom.utils.req.rx.SolanaRequestUtil_Rx;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.encoders.Base64;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.nio.charset.StandardCharsets;

import static com.solana.custom.utils.atom.ByteUtils.trimRight;

/**
 * For decoding nft
 *
 * @author Roylic
 * 2023/10/16
 */
@Slf4j
public class MetaplexDecodeHandler_Rx {

    /**
     * Call and get nft concrete json result
     *
     * @param base64DataStr nft data info encoded in Base64
     * @return standard metaplex json obj
     */
    public static Mono<MetaplexStandardJsonObj> accountInfoData2JsonObj(String base64DataStr) {

        if (StringUtils.isBlank(base64DataStr)) {
            return Mono.empty();
        }

        byte[] decode = Base64.decode(base64DataStr);
        if (decode.length < 320) {
            return Mono.empty();
        }

        byte[] uri = new byte[204];
        System.arraycopy(decode, 115, uri, 0, 204);
        byte[] trimUri = trimRight(uri);
        if (trimUri.length < 4) {
            return Mono.empty();
        }

        return ExternalRequestUtil_Rx.metaplexExternalJsonReq(
                new String(trimUri, 4, trimUri.length - 4, StandardCharsets.UTF_8));
    }


    /**
     * Get account's all metaplex nft info, with concrete nft image
     *
     * @param client
     * @param account
     * @return
     */
    public static Flux<NftItem> accountAllAssociatedNftFiles(HttpClient client, String account) {
        return SolanaRequestUtil_Rx.rpcAssociatedTokenAccountByOwner(client, account)
                // from Mono<List<NftFileItem>> to Flux<AccountInfo>
                .flatMapMany(Flux::fromIterable)
                // convert & flat
                .map(AccInfoDecodeHandler::parseFlat)
                // filter
                .filter(accountInfoFlat -> accountInfoFlat.getAmount() > 0)
                // collect as map, eliminate repeat nft
                .collectMap(
                        accountInfoFlat -> accountInfoFlat.getAtAddress(),
                        accountInfoFlat -> AccInfoDeriveNftTokenAccPair.builder()
                                .accountInfoFlat(accountInfoFlat)
                                .nftTokenMintAccount(AccInfoDecodeHandler.deriveNftTokenAddress(accountInfoFlat.getMintAddress()))
                                .build())
                // flat to Flux again
                .flatMapMany(map ->
                        Flux.fromIterable(map.entrySet())
                                .flatMap(entry -> constructNftItemTask(client, entry.getKey(), entry.getValue())))
                .filter(nftItem -> StringUtils.isNotBlank(nftItem.getName()));
    }


    /**
     * Decode Metaplex Nft & request external json, reactive ver.
     *
     * @param client
     * @param nftAstAccount
     * @param pair
     * @return
     */
    public static Mono<NftItem> constructNftItemTask(HttpClient client, String nftAstAccount, AccInfoDeriveNftTokenAccPair pair) {

        AccountInfoFlat accountInfoFlat = pair.getAccountInfoFlat();
        String nftTokenMintAccount = pair.getNftTokenMintAccount();

        return SolanaRequestUtil_Rx.rpcAccountInfoDataBase64(client, nftTokenMintAccount)
                .doOnError(err -> log.error("<<< [MetaplexDecodeHandler_Rx] got exception:", err))
                .flatMap(dataBase64 -> {
                    if (!StringUtils.isNotBlank(dataBase64)) {
                        return Mono.just(NftItem.builder().build());
                    }
                    byte[] decode = Base64.decode(dataBase64);
                    // pure token
                    if (decode.length < 320) {
                        return Mono.just(NftItem.builder().build());
                    }
                    // nft
                    byte[] uri = new byte[204];
                    System.arraycopy(decode, 115, uri, 0, 204);
                    byte[] trimUri = trimRight(uri);
                    if (trimUri.length < 4) {
                        return Mono.just(NftItem.builder().build());
                    }
                    String uriStr = new String(trimUri, 4, trimUri.length - 4, StandardCharsets.UTF_8);
                    // inside metaplexExternalJsonReq is blocking-io, because we call multiple external api
                    return ExternalRequestUtil_Rx.metaplexExternalJsonReq(uriStr)
                            .map(metaplexStandardJsonObj ->
                                    // construct
                                    NftItem.builder()
                                            .mintAddress(accountInfoFlat.getMintAddress())
                                            .tokenId(nftAstAccount)
                                            .name(metaplexStandardJsonObj.getName())
                                            .uri(uriStr)
                                            .imgUrl(metaplexStandardJsonObj.getImage())
                                            .symbol(StringUtils.isNotBlank(metaplexStandardJsonObj.getSymbol()) ? metaplexStandardJsonObj.getSymbol() : "SPL")
                                            .description(metaplexStandardJsonObj.getDescription())
                                            .build());
                });
    }

}
