package com.solana.custom.handler;

import com.solana.custom.dto.extra.CompactNftInfo;
import com.solana.custom.dto.metaplex.MetaplexAccountInfoData;
import com.solana.custom.dto.metaplex.MetaplexStandardJsonObj;
import com.solana.custom.utils.req.SolanaRequestUtil;
import okhttp3.OkHttpClient;
import org.apache.commons.lang3.StringUtils;
import org.bitcoinj.core.Base58;
import org.bouncycastle.util.encoders.Base64;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.solana.custom.utils.atom.ByteUtils.trimRight;

/**
 * For decoding nft
 *
 * @author Roylic
 * 2023/3/30
 */
public class MetaplexDecodeHandler {

    /**
     * Call and get nft concrete json result
     *
     * @param base64DataStr nft data info encoded in Base64
     * @return standard metaplex json obj
     */
    public static Optional<MetaplexStandardJsonObj> accountInfoData2JsonObj(OkHttpClient okHttpClient, String base64DataStr) {

        if (StringUtils.isBlank(base64DataStr)) {
            return Optional.empty();
        }

        byte[] decode = Base64.decode(base64DataStr);
        if (decode.length < 320) {
            return Optional.empty();
        }

        byte[] uri = new byte[204];
        System.arraycopy(decode, 115, uri, 0, 204);
        byte[] trimUri = trimRight(uri);
        if (trimUri.length < 4) {
            return Optional.empty();
        }

        return SolanaRequestUtil.metaplexExternalJsonReq(okHttpClient,
                new String(trimUri, 4, trimUri.length - 4, StandardCharsets.UTF_8));
    }


    /**
     * Compact version of parsing nft info
     *
     * @param base64DataStr nft data info encoded in Base64
     * @return decoded compact nft info
     */
    public static Optional<CompactNftInfo> parseCompactNftInfo(String base64DataStr) {

        if (StringUtils.isBlank(base64DataStr)) {
            return Optional.empty();
        }

        byte[] decode = Base64.decode(base64DataStr);
        if (decode.length < 320) {
            return Optional.empty();
        }

        byte[] mint = new byte[32];
        byte[] name = new byte[36];
        byte[] symbol = new byte[14];
        byte[] uri = new byte[204];
        System.arraycopy(decode, 33, mint, 0, 32);
        System.arraycopy(decode, 65, name, 0, 36);
        System.arraycopy(decode, 101, symbol, 0, 14);
        System.arraycopy(decode, 115, uri, 0, 204);
        byte[] trimName = trimRight(name);
        byte[] trimSymbol = trimRight(symbol);
        byte[] trimUri = trimRight(uri);
        if (trimName.length < 4 || trimSymbol.length < 4 || trimUri.length < 4) {
            return Optional.empty();
        }
        return Optional.of(
                CompactNftInfo.builder()
                        .mint(Base58.encode(mint))
                        .name(new String(trimName, 4, trimName.length - 4, StandardCharsets.UTF_8))
                        .symbol(new String(trimSymbol, 4, trimSymbol.length - 4, StandardCharsets.UTF_8))
                        .uri(new String(trimUri, 4, trimUri.length - 4, StandardCharsets.UTF_8))
                        .build());
    }


    /**
     * Decode full Metaplex accountInfo's data
     *
     * @param base64DataStr nft data info encoded in Base64
     * @return decoded nft info
     */
    public static Optional<MetaplexAccountInfoData> parseFullNftInfo(String base64DataStr) {

        if (StringUtils.isBlank(base64DataStr)) {
            return Optional.empty();
        }

        byte[] decode = Base64.decode(base64DataStr);
        if (decode.length < 320) {
            // < 320 must be pure token
            return Optional.empty();
        }
        // key, updateAuthority, mint, name, symbol, uri, sellerFeeBasicPoints
        MetaplexAccountInfoData.MetaplexAccountInfoDataBuilder builder = MetaplexAccountInfoData.builder();
        byte[] key = new byte[1];
        byte[] updateAuthority = new byte[32];
        byte[] mint = new byte[32];
        byte[] name = new byte[36];
        byte[] symbol = new byte[14];
        byte[] uri = new byte[204];
        byte[] sellerFeeBasicPoints = new byte[2];
        System.arraycopy(decode, 0, key, 0, 1);
        System.arraycopy(decode, 1, updateAuthority, 0, 32);
        System.arraycopy(decode, 33, mint, 0, 32);
        System.arraycopy(decode, 65, name, 0, 36);
        System.arraycopy(decode, 101, symbol, 0, 14);
        System.arraycopy(decode, 115, uri, 0, 204);
        System.arraycopy(decode, 319, sellerFeeBasicPoints, 0, 2);
        byte[] trimName = trimRight(name);
        byte[] trimSymbol = trimRight(symbol);
        byte[] trimUri = trimRight(uri);
        if (trimName.length < 4 || trimSymbol.length < 4 || trimUri.length < 4) {
            return Optional.empty();
        }
        int sellerFeeBasicPointInt = 0;
        for (int i = 0; i < sellerFeeBasicPoints.length; i++) {
            sellerFeeBasicPointInt |= (sellerFeeBasicPoints[i] & 0xFF) << (8 * i);
        }
        builder.key(Numeric.toHexString(key))
                .updateAuthority(Base58.encode(updateAuthority))
                .mint(Base58.encode(mint))
                .name(new String(trimName, 4, trimName.length - 4, StandardCharsets.UTF_8))
                .symbol(new String(trimSymbol, 4, trimSymbol.length - 4, StandardCharsets.UTF_8))
                .uri(new String(trimUri, 4, trimUri.length - 4, StandardCharsets.UTF_8))
                .sellerBasicPoints(sellerFeeBasicPointInt);

        // creators
        byte[] creatorIndicator = new byte[1];
        System.arraycopy(decode, 321, creatorIndicator, 0, 1);
        int decodeIdx = 322;
        if (creatorIndicator[0] == 1) {
            // have creator list
            byte[] creatorList = new byte[4];
            System.arraycopy(decode, decodeIdx, creatorList, 0, 4);
            int creatorNums = 0;
            for (int i = 0; i < creatorList.length; i++) {
                creatorNums |= (creatorList[i] & 0xFF) << (8 * i);
            }
            // traverse creators
            List<MetaplexAccountInfoData.Creator> creators = new ArrayList<>(creatorNums);
            decodeIdx += 4;
            int i = 0;
            while (i++ < creatorNums) {
                byte[] creator = new byte[32];
                byte[] verified = new byte[1];
                byte[] shared = new byte[1];
                System.arraycopy(decode, decodeIdx, creator, 0, 32);
                System.arraycopy(decode, decodeIdx + 32, verified, 0, 1);
                System.arraycopy(decode, decodeIdx + 32 + 1, shared, 0, 1);
                decodeIdx += 32 + 1 + 1;
                creators.add(
                        MetaplexAccountInfoData.Creator.builder()
                                .address(Base58.encode(creator))
                                .verified(verified[0] == 1)
                                .share((int) shared[0])
                                .build());
            }
            builder.creatorList(creators);
        }

        // primarySale, isMutable
        byte[] primarySale = new byte[1];
        byte[] isMutable = new byte[1];
        System.arraycopy(decode, decodeIdx++, primarySale, 0, 1);
        System.arraycopy(decode, decodeIdx++, isMutable, 0, 1);
        builder.primarySaleHappened(primarySale[0] == 1)
                .isMutable(isMutable[0] == 1);

        // edition nonce 396
        byte[] editionNonceIndicator = new byte[1];
        System.arraycopy(decode, decodeIdx++, editionNonceIndicator, 0, 1);
        if (editionNonceIndicator[0] == 1) {
            byte[] editionNonce = new byte[1];
            System.arraycopy(decode, decodeIdx++, editionNonce, 0, 1);
            builder.editionNonce((editionNonce[0] & 0xff));
        }

        // token standard
        byte[] tokenStandardIndicator = new byte[1];
        System.arraycopy(decode, decodeIdx++, tokenStandardIndicator, 0, 1);
        if (tokenStandardIndicator[0] == 1) {
            byte[] tokenStandard = new byte[1];
            System.arraycopy(decode, decodeIdx++, tokenStandard, 0, 1);
            builder.tokenStandard(new BigInteger(tokenStandard));
        }

        // collection
        byte[] collectionIndicator = new byte[1];
        System.arraycopy(decode, decodeIdx++, collectionIndicator, 0, 1);
        if (collectionIndicator[0] == 1) {
            byte[] verified = new byte[1];
            byte[] collectionMintKey = new byte[32];
            System.arraycopy(decode, decodeIdx++, verified, 0, 1);
            System.arraycopy(decode, decodeIdx, collectionMintKey, 0, 32);
            decodeIdx += 32;
            builder.collection(
                    MetaplexAccountInfoData.Collection.builder()
                            .verified(verified[0] == 1)
                            .collectionMintAccount(Base58.encode(collectionMintKey))
                            .build());
        }

//        // uses
//        byte[] usesIndicator = new byte[1];
//        System.arraycopy(decode, decodeIdx++, usesIndicator, 0, 1);
//        if (usesIndicator[0] == 1) {
//            byte[] useMethod = new byte[1];
//            byte[] remaining = new byte[8];
//            byte[] total = new byte[8];
//            System.arraycopy(decode, decodeIdx++, useMethod, 0, 1);
//            System.arraycopy(decode, decodeIdx, remaining, 0, 8);
//            System.arraycopy(decode, decodeIdx + 8, total, 0, 8);
//            decodeIdx += 8 + 8;
//            int remainingNums = 0;
//            for (int i = 0; i < remaining.length; i++) {
//                remainingNums |= (remaining[i] & 0xFF) << (8 * i);
//            }
//            int totalNums = 0;
//            for (int i = 0; i < total.length; i++) {
//                totalNums |= (total[i] & 0xFF) << (8 * i);
//            }
//
//            System.out.println("Uses: ");
//            System.out.println("  Use Method: " + new BigInteger(useMethod));
//            System.out.println("  Remaining: " + remainingNums);
//            System.out.println("  Total: " + totalNums);
//        }
//
//        // programmable config
//        byte[] programIndicator = new byte[1];
//        System.arraycopy(decode, decodeIdx++, programIndicator, 0, 1);
//        if (programIndicator[0] == 1) {
//            byte[] programConfig = new byte[33];
//            System.arraycopy(decode, decodeIdx, programConfig, 0, 33);
//            System.out.println();
//        }

        return Optional.of(builder.build());
    }

}
