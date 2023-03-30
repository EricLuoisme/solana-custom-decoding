package com.solana.custom.dto.extra;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Roylic
 * 2023/3/30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompactNftInfo {
    private String mint;
    private String name;
    private String symbol;
    private String uri;
}
