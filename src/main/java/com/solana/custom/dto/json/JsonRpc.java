package com.solana.custom.dto.json;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JsonRpc {
    private Integer id;
    private String jsonrpc; // version
    private Object result;
    private String error;
}
