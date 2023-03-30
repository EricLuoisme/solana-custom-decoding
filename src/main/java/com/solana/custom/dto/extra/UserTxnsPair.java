package com.solana.custom.dto.extra;

import com.solana.custom.dto.Txn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Roylic
 * 2023/3/28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserTxnsPair {
    private String userAccount;
    private List<Txn> txnList;
}
