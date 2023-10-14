package com.solana.custom.handler.rx;

import com.solana.custom.dto.Instructions;
import com.solana.custom.dto.Meta;
import com.solana.custom.dto.TxnResult;
import com.solana.custom.utils.atom.ByteUtils;
import com.solana.custom.utils.req.SolanaRequestUtil;
import okhttp3.OkHttpClient;
import org.bitcoinj.core.Base58;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

import static com.solana.custom.constant.ProgramConstants.SOL_TRANSFER_INSTRUCTION_IDX;
import static com.solana.custom.constant.ProgramConstants.SPL_TRANSFER_INSTRUCTION_IDX;

public class TxnDecodeHandler {

    /**
     * For decoding the real input amount of a txn by using the signature¬
     */
    public static Optional<BigDecimal> getInputTxnAmt(OkHttpClient okHttpClient, String nodeUrl, String signature) {

        // request
        Optional<TxnResult> opTxnResult = SolanaRequestUtil.rpcTransactionBySignature(okHttpClient, nodeUrl, signature);
        if (!opTxnResult.isPresent()) {
            return Optional.empty();
        }

        BigInteger minimumUnit = null;
        Integer instructionIdx = null;
        TxnResult txnResult = opTxnResult.get();
        List<Instructions> instructionList = txnResult.getTransaction().getMessage().getInstructions();
        for (Instructions instructions : instructionList) {
            // sol
            if (SOL_TRANSFER_INSTRUCTION_IDX.equals(instructions.getProgramIdIndex())) {
                byte[] decode = Base58.decode(instructions.getData());
                minimumUnit = ByteUtils.readUint64(decode, 4);
                instructionIdx = SOL_TRANSFER_INSTRUCTION_IDX;
                break;
            }
            // spl
            if (SPL_TRANSFER_INSTRUCTION_IDX.equals(instructions.getProgramIdIndex())) {
                byte[] decode = Base58.decode(instructions.getData());
                minimumUnit = ByteUtils.readUint64(decode, 1);
                instructionIdx = SPL_TRANSFER_INSTRUCTION_IDX;
                break;
            }
        }

        if (minimumUnit != null) {
            Integer decimal = 9;
            if (instructionIdx.equals(SPL_TRANSFER_INSTRUCTION_IDX)) {
                boolean found = false;
                List<Meta.TokenBalance> preTokenBalances = txnResult.getMeta().getPreTokenBalances();
                for (Meta.TokenBalance preTokenBalance : preTokenBalances) {
                    decimal = preTokenBalance.getUiTokenAmount().getDecimals();
                    found = true;
                    break;
                }
                if (!found) {
                    List<Meta.TokenBalance> postTokenBalances = txnResult.getMeta().getPostTokenBalances();
                    for (Meta.TokenBalance postTokenBalance : postTokenBalances) {
                        decimal = postTokenBalance.getUiTokenAmount().getDecimals();
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    return Optional.empty();
                }
            }
            return Optional.of(new BigDecimal(minimumUnit)
                    .divide(BigDecimal.TEN.pow(decimal), 18, RoundingMode.FLOOR));
        }

        return Optional.empty();
    }
}
