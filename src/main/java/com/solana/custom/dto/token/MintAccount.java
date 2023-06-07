package com.solana.custom.dto.token;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MintAccount {
    private String mintAuthority;
    private Integer supply;
    private Integer decimals;
    private Boolean isInitialized;
    private String freezeAuthority;
}
