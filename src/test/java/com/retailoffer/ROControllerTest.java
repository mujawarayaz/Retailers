package com.retailoffer;


import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.retailoffer.controller.RetailOfferController;
import com.retailoffer.dto.RetailerDTO;
import com.retailoffer.dto.TransactionDTO;
import com.retailoffer.exception.RetailerException;
import com.retailoffer.service.RetailService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(RetailOfferController.class)
public class ROControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RetailService retailService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testRecordTransaction_success() throws Exception {
        TransactionDTO input = new TransactionDTO();
        input.setRetailerId(1);
        input.setAmountSpent(120.0);
        input.setTransactionDate(LocalDate.now());

        RetailerDTO output = new RetailerDTO();
        output.setRetailerId(1);
        output.setName("Test Retailer");
        output.setRewardPoint(140);

        when(retailService.recordTransaction(any(TransactionDTO.class))).thenReturn(output);

        mockMvc.perform(post("/retail/transaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.retailerId", is(1)))
            .andExpect(jsonPath("$.name", is("Test Retailer")))
            .andExpect(jsonPath("$.rewardPoint", is(140)));
    }

    @Test
    void testRecordTransaction_retailerException() throws Exception {
        TransactionDTO input = new TransactionDTO();
        input.setRetailerId(99);
        input.setAmountSpent(50.0);
        input.setTransactionDate(LocalDate.now());

        when(retailService.recordTransaction(any(TransactionDTO.class)))
            .thenThrow(new RetailerException("retailer.not.found"));

        mockMvc.perform(post("/retail/transaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode", is(400)))
            .andExpect(jsonPath("$.errorMessage", not(emptyString())));
    }

    @Test
    void testGetMonthlyRewardPoint_success() throws Exception {
        int retailerId = 1;
        when(retailService.getMonthlyRewardPoint(retailerId)).thenReturn(90);

        mockMvc.perform(get("/retail/transactionPerMonth/{retailerId}", retailerId))
            .andExpect(status().isOk())
            .andExpect(content().string("90"));
    }

    @Test
    void testGetMonthlyRewardPoint_retailerException() throws Exception {
        int retailerId = 99;
        when(retailService.getMonthlyRewardPoint(retailerId))
            .thenThrow(new RetailerException("retailer.not.found"));

        mockMvc.perform(get("/retail/transactionPerMonth/{retailerId}", retailerId))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode", is(400)))
            .andExpect(jsonPath("$.errorMessage", not(emptyString())));
    }

    @Test
    void testGetTotalRewardPoint_success() throws Exception {
        int retailerId = 1;
        when(retailService.getTotalRewardPoint(retailerId)).thenReturn(150);

        mockMvc.perform(get("/retail/totalReward/{retailerId}", retailerId))
            .andExpect(status().isOk())
            .andExpect(content().string("150"));
    }

    @Test
    void testGetTotalRewardPoint_retailerException() throws Exception {
        int retailerId = 99;
        when(retailService.getTotalRewardPoint(retailerId))
            .thenThrow(new RetailerException("retailer.not.found"));

        mockMvc.perform(get("/retail/totalReward/{retailerId}", retailerId))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode", is(400)))
            .andExpect(jsonPath("$.errorMessage", not(emptyString())));
    }

    @Test
    void testGetThreeMonthSummary_success() throws Exception {
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("retailer", Map.of("retailerId", 1, "name", "Test Retailer"));
        summary.put("totalPoints", 120);
        summary.put("monthlyPoints", Map.of("SEPTEMBER", 70, "AUGUST", 50));
        summary.put("transactions", Collections.emptyList());

        when(retailService.getThreeMonthRewardSummary())
            .thenReturn(Collections.singletonList(summary));

        mockMvc.perform(get("/retail/summary"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].totalPoints", is(120)))
            .andExpect(jsonPath("$[0].retailer.retailerId", is(1)))
            .andExpect(jsonPath("$[0].retailer.name", is("Test Retailer")));
    }

    @Test
    void testGetThreeMonthSummary_retailerException() throws Exception {
        when(retailService.getThreeMonthRewardSummary())
            .thenThrow(new RetailerException("some.error"));

        mockMvc.perform(get("/retail/summary"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode", is(400)))
            .andExpect(jsonPath("$.errorMessage", not(emptyString())));
    }

}

