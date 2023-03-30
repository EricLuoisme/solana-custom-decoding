package com.solana.custom.constant;

/**
 * @author Roylic
 * 2023/3/30
 */
public class JsonRpcConstants {

    public static final String RPC_LATEST_BLOCK = "{\"id\":1,\"jsonrpc\":\"2.0\",\"method\":\"getLatestBlockhash\",\"params\":[{\"commitment\":\"confirmed\"}]}";

    public static final String RPC_LATEST_SLOT = "{\"jsonrpc\":\"2.0\",\"id\":1, \"method\":\"getSlot\"}";

    public static final String RPC_BLOCK_BY_SLOT = "{\"jsonrpc\": \"2.0\",\"id\":1,\"method\":\"getBlock\",\"params\":[%d, {\"encoding\": \"json\",\"maxSupportedTransactionVersion\":0,\"transactionDetails\":\"accounts\",\"rewards\":false}]}";

    public static final String RPC_ASSOCIATED_TOKEN_ACCOUNT = "{\"jsonrpc\":\"2.0\",\"id\":1,\"method\":\"getTokenAccountsByOwner\",\"params\":[\"%s\",{\"programId\":\"TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA\"},{\"encoding\":\"base64\"}]}";

    public static final String RPC_ACC_SIGNATURE_LIMIT = "{\"jsonrpc\":\"2.0\",\"id\":1,\"method\":\"getSignaturesForAddress\",\"params\":[\"%s\",{\"limit\":%d}]}";

    public static final String RPC_FULL_TRANSACTION = "{\"jsonrpc\":\"2.0\",\"id\":1,\"method\":\"getTransaction\",\"params\":[\"%s\",{\"encoding\":\"json\"}]}";

    public static final String RPC_ACCOUNT_INFO_DATA = "{\"jsonrpc\":\"2.0\",\"id\":1,\"method\":\"getAccountInfo\",\"params\":[\"%s\",{\"encoding\":\"base64\"}]}";


}
