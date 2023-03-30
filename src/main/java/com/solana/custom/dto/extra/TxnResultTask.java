package com.solana.custom.dto.extra;

import com.solana.custom.dto.TxnResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TxnResultTask {
    private String associatedTokenAccount;
    private String signature;
    private TxnResult txnResult;
}
