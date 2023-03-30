package com.solana.custom;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solana.custom.dto.metaplex.MetaplexAccountInfoData;
import com.solana.custom.handler.NftDecodeHandler;
import org.junit.Test;

import java.util.Optional;

/**
 * @author Roylic
 * 2023/3/30
 */
public class NftTest {

    private static final ObjectMapper om = new ObjectMapper();

    @Test
    public void nftInfoDataDecoding() throws JsonProcessingException {
        String base64DataStr = "BJFmu4q8Nqbfbjctj5+IGdJqz1GNfBTwx+ef1OIIT5lEyZHNApSQhhH4+13jqVLCWl2buaAfO1onpJALNxFgpBcgAAAATnVtYmVyICMwMDA1AAAAAAAAAAAAAAAAAAAAAAAAAAAKAAAATkIAAAAAAAAAAMgAAABodHRwczovL2Fyd2VhdmUubmV0L2ZOWjhRaWJzd05UbjZPSXhNOGZIMFZwcFFhYzBtdGsyVk5HMlVET3ptLUUAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAJYAAQIAAABwR1rHRLcV9Pgwql6fuU9yLlnguuhQosZbVaLFO1SDZwEAkWa7irw2pt9uNy2Pn4gZ0mrPUY18FPDH55/U4ghPmUQAZAEBAf8BAAEBi/F3/535aQiLpyuFs9WhGNyT1kiOX782bZ4mKJOp0wUAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA==";
        Optional<MetaplexAccountInfoData> metaplexAccountInfoData = NftDecodeHandler.parseMetaplexAccInfo(base64DataStr);
        System.out.println(om.writerWithDefaultPrettyPrinter().writeValueAsString(metaplexAccountInfoData.get()));
    }
}
