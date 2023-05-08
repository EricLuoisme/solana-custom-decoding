package com.solana.custom.constant;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Solana related constants
 *
 * @author Roylic
 * 2023/3/22
 */
public class ProgramConstants {

    public static final Integer SOL_TRANSFER_INSTRUCTION_IDX = 1;

    public static final Integer SPL_TRANSFER_INSTRUCTION_IDX = 4;

    public static final String SYSTEM_PROGRAM_ID = "11111111111111111111111111111111";

    public static final String CONFIG_PROGRAM_ID = "Config1111111111111111111111111111111111111";

    public static final String STAKE_PROGRAM_ID = "Stake11111111111111111111111111111111111111";

    public static final String VOTE_PROGRAM_ID = "Vote111111111111111111111111111111111111111";

    public static final String BPF_LOADER_PROGRAM_ID = "BPFLoader1111111111111111111111111111111111";

    public static final String SECP_256K1_PROGRAM_ID = "KeccakSecp256k11111111111111111111111111111";

    public static final String TOKEN_PROGRAM_ID = "TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA";

    public static final String MEMO_PROGRAM_ID = "MemoSq4gqABAXKb96qnH8TysNcWxMyWCqXgDLGmfcHr";

    public static final String SPL_ASSOCIATED_TOKEN_PROGRAM_ID = "ATokenGPvbdGVxr1b2hvZbsiqW5xWH25efTNsLJA8knL";

    public static final String SPL_NAME_SERVICE_PROGRAM_ID = "namesLPneVptA9Z5rqUDD9tMTWEJwofgaYwp8cawRkX";

    public static final String METAPLEX_TOKEN_META_PROGRAM_ID = "metaqbxxUerdq28cj1RbAWkYQm3ybzjb6a8bt518x1s";

    public static final String COMPUTE_BUDGET_PROGRAM_ID = "ComputeBudget111111111111111111111111111111";

    public static final String ADDRESS_LOOK_UP_PROGRAM_ID = "AddressLookupTab1e1111111111111111111111111";


    public static final Set<String> PROGRAM_SET = new HashSet<>(
            Arrays.asList(SYSTEM_PROGRAM_ID, CONFIG_PROGRAM_ID, STAKE_PROGRAM_ID, VOTE_PROGRAM_ID, BPF_LOADER_PROGRAM_ID,
                    SECP_256K1_PROGRAM_ID, TOKEN_PROGRAM_ID, MEMO_PROGRAM_ID, SPL_ASSOCIATED_TOKEN_PROGRAM_ID,
                    SPL_NAME_SERVICE_PROGRAM_ID, METAPLEX_TOKEN_META_PROGRAM_ID, COMPUTE_BUDGET_PROGRAM_ID,
                    ADDRESS_LOOK_UP_PROGRAM_ID));

    public static boolean isProgramAddress(String inputAddress) {
        return PROGRAM_SET.contains(inputAddress);
    }

}
