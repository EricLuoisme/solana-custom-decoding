package com.solana.custom.dto.extra;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author Roylic
 * 2023/3/28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAssetChangingPair {
    private String userAccount;
    private Map<String, AssetChanging> assetChangingMap;
}
