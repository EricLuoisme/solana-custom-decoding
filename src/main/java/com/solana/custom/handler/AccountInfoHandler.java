package com.solana.custom.handler;

import com.solana.custom.dto.AccountInfo;
import com.solana.custom.dto.extra.AccountInfoFlat;
import com.solana.custom.utils.atom.PublicKey;
import org.bitcoinj.core.Base58;
import org.bouncycastle.util.encoders.Base64;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static com.solana.custom.constant.ProgramConstants.METAPLEX_TOKEN_META_PROGRAM_ID;

/**
 * Account info decode helper
 *
 * @author Roylic
 * 2023/3/29
 */
public class AccountInfoHandler {

    private static byte[] META_DATA_BYTE = "metadata".getBytes(StandardCharsets.UTF_8);
    private static PublicKey METAPLEX_PROGRAM_ID_PUB_KEY = new PublicKey(METAPLEX_TOKEN_META_PROGRAM_ID);

    /**
     * Derive Nft token address
     *
     * @param nftMintAddress nft mint address
     * @return nft token address
     */
    public static String deriveNftTokenAddress(String nftMintAddress) {
        PublicKey.ProgramDerivedAddress derivedAddress = null;
        try {
            derivedAddress = PublicKey.findProgramAddress(
                    Arrays.asList(META_DATA_BYTE, METAPLEX_PROGRAM_ID_PUB_KEY.toByteArray(), new PublicKey(nftMintAddress).toByteArray()),
                    METAPLEX_PROGRAM_ID_PUB_KEY);
            return derivedAddress.getAddress().toBase58();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Decode account info's data into flat
     *
     * @param accountInfo account info
     * @return account info with decoded fields
     */
    public static AccountInfoFlat parseFlat(AccountInfo accountInfo) {
        AccountInfo.Account account = accountInfo.getAccount();
        AccountInfoFlat flat = AccountInfoFlat.builder()
                .atAddress(accountInfo.getPubkey())
                .ownerAddress(account.getOwner())
                .executable(account.isExecutable())
                .lamports(account.getLamports())
                .rentEpoch(account.getRentEpoch())
                .build();
        fillData(account.getData(), flat);
        return flat;
    }

    /**
     * Trim empty right
     *
     * @param array byte array
     * @return compact array
     */
    public static byte[] trimRight(byte[] array) {
        int i = array.length - 1;
        while (array[i] == 0 && i >= 0) {
            i--;
        }
        byte[] trimArr = new byte[i + 1];
        System.arraycopy(array, 0, trimArr, 0, i + 1);
        return trimArr;
    }


    private static void fillData(String[] dataArr, AccountInfoFlat accountInfoFlat) {

        byte[] decode = null;
        if ("base64".equalsIgnoreCase(dataArr[1])) {
            decode = Base64.decode(dataArr[0]);
        }
        if ("base58".equalsIgnoreCase(dataArr[1])) {
            decode = Base58.decode(dataArr[0]);
        }
        if (decode == null) {
            return;
        }

        byte[] mint = new byte[32];
        byte[] owner = new byte[32];
        byte[] amount = new byte[8];
        System.arraycopy(decode, 0, mint, 0, 32);
        System.arraycopy(decode, 32, owner, 0, 32);
        System.arraycopy(decode, 64, amount, 0, 8);
        int theAmount = 0;
        for (int i = 0; i < amount.length; i++) {
            theAmount |= (amount[i] & 0xFF) << (8 * i);
        }

        accountInfoFlat.setMintAddress(Base58.encode(mint));
        accountInfoFlat.setOwnerAddress(Base58.encode(owner));
        accountInfoFlat.setAmount(theAmount);
    }

}
