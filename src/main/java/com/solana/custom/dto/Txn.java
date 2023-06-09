package com.solana.custom.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Roylic
 * 2023/2/2
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Txn {
    private Meta meta;
    private InnerTxn transaction;
    private String version;
}
