package com.solana.custom.dto.extra;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccInfoDeriveNftTokenAccPair {
    private AccountInfoFlat accountInfoFlat;
    private String nftTokenMintAccount;
}
