package com.solana.custom.dto.metaplex;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NftItem {
    private String mintAddress;
    private String tokenId;
    private String name;
    private String uri;
    private String imgUrl;
    private String symbol;
    private String description;
}
