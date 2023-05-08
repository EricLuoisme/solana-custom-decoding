package com.solana.custom.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * @author Roylic
 * 2023/2/2
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlockResult {
    private Long blockHeight;
    private Long blockTime;
    private String blockhash;
    private Long parentSlot;
    private String previousBlockhash;
    private List<Txn> transactions;

    private boolean blockSkip;
}
