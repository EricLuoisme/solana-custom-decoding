package com.solana.custom;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solana.custom.constant.NetConstants;
import com.solana.custom.dto.Instructions;
import com.solana.custom.dto.TxnResult;
import com.solana.custom.utils.atom.ByteUtils;
import com.solana.custom.utils.req.SolanaRequestUtil;
import okhttp3.OkHttpClient;
import org.bitcoinj.core.Base58;
import org.junit.Test;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.Optional;

public class InstructionTest {

    private static final ObjectMapper om = new ObjectMapper();

    private static final OkHttpClient client = new OkHttpClient();

    @Test
    public void solTransferTest() {
        String sig = "41ZCAJiXCHmXjtGTm5VtB5q1F1eWoGa84xBBiErdDsqf7K3m2yn4GACjuXzimMg6zjtGA2MGuu9gPRmRqA6bV3ka";
        Optional<TxnResult> opTxnResult = SolanaRequestUtil.rpcTransactionBySignature(client, NetConstants.DEVNET_URL, sig);
        if (opTxnResult.isPresent()) {
            TxnResult txnResult = opTxnResult.get();
            List<Instructions> instructions = txnResult.getTransaction().getMessage().getInstructions();
            Instructions inst = instructions.get(0);
            byte[] decode = Base58.decode(inst.getData());
            byte[] amount = new byte[8];
            System.arraycopy(decode, 4, amount, 0, 8);
            ByteBuffer buffer = ByteBuffer.wrap(amount).order(ByteOrder.LITTLE_ENDIAN);
            System.out.println(buffer.getLong());
        }
    }

    @Test
    public void splTransferTest() {
        String sig = "4JQVvVJ3QQaBosMDUB7S9D3xRujgcb47jpdyXiUt9us5j5YjWTg9x6sHhpaPagsETZ5hPMVJR3LQ1SBnp4YCyHti";
        Optional<TxnResult> opTxnResult = SolanaRequestUtil.rpcTransactionBySignature(client, NetConstants.DEVNET_URL, sig);
        if (opTxnResult.isPresent()) {
            TxnResult txnResult = opTxnResult.get();
            List<Instructions> instructions = txnResult.getTransaction().getMessage().getInstructions();
            Instructions inst = instructions.get(0);
            byte[] decode = Base58.decode(inst.getData());
            BigInteger amount = ByteUtils.readUint64(decode, 1);
            System.out.println(amount);
        }
    }
}
