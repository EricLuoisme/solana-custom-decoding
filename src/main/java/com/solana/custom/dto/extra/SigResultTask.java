package com.solana.custom.dto.extra;

import com.solana.custom.dto.SigResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Roylic
 * 2023/3/24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SigResultTask {
    private String account;
    private List<SigResult> sigResultList;
}
