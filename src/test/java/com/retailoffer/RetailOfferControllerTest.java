package com.retailoffer;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.Month;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.retailoffer.controller.RetailOfferController;
import com.retailoffer.dto.RewardDetailsDTO;
import com.retailoffer.dto.TransactionDTO;
import com.retailoffer.exception.RetailerException;
import com.retailoffer.service.RetailService;

@WebMvcTest(RetailOfferController.class)
public class RetailOfferControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private RetailService retailService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void recordTransaction_Success_ShouldReturnCreated() throws Exception {
	    
	    TransactionDTO transactionInput = new TransactionDTO();
	    transactionInput.setCustomerId(101);
	    transactionInput.setRetailerId(101);         
	    transactionInput.setAmountSpent(120.0);      
	    transactionInput.setTransactionDate(LocalDate.now()); 

	    when(retailService.recordTransaction(any(TransactionDTO.class))).thenReturn(transactionInput);
	    mockMvc.perform(post("/retail/transactions") 
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(transactionInput)))
	            .andExpect(status().isCreated()) 
	            .andExpect(jsonPath("$.customerId", is(101)));
	}

	@Test
	void getCustomerRewards_DefaultSummaryView_ShouldReturnSummaryDTO() throws Exception {
		int customerId = 101;
		RewardDetailsDTO summary = RewardDetailsDTO.builder().customerId(customerId).customerName("Ayaz Mujawar")
				.totalRewardPoints(150).monthlyPoints(Map.of(Month.OCTOBER.name(), 90, Month.NOVEMBER.name(), 60))
				.build();

		when(retailService.getThreeMonthRewardSummary(customerId)).thenReturn(summary);

		mockMvc.perform(get("/retail/rewards/customers/{customerId}", customerId)).andExpect(status().isOk())
				.andExpect(jsonPath("$.customerId", is(101))).andExpect(jsonPath("$.totalRewardPoints", is(150)))
				.andExpect(jsonPath("$.monthlyPoints.OCTOBER", is(90)));
	}

	@Test
	void getCustomerRewards_TotalView_ShouldReturnPointsDTO() throws Exception { // CHANGED

		int customerId = 101;
		when(retailService.getTotalRewardPointsForCustomer(customerId)).thenReturn(250);

		mockMvc.perform(get("/retail/rewards/customers/{customerId}", customerId).param("view", "total"))
				.andExpect(status().isOk()).andExpect(jsonPath("$.customerId", is(101)))
				.andExpect(jsonPath("$.points", is(250)));
	}

	@Test
	void getCustomerRewards_MonthlyView_ShouldReturnPointsDTO() throws Exception {
		int customerId = 101;
		when(retailService.getMonthlyRewardPointsForCustomer(customerId)).thenReturn(95);

		mockMvc.perform(get("/retail/rewards/customers/{customerId}", customerId).param("view", "monthly"))
				.andExpect(status().isOk()).andExpect(jsonPath("$.customerId", is(101)))
				.andExpect(jsonPath("$.points", is(95)));
	}

	@Test
	void getCustomerRewards_CustomerNotFound_ShouldReturnBadRequest() throws Exception {
		int customerId = 999;
		when(retailService.getThreeMonthRewardSummary(customerId))
				.thenThrow(new RetailerException("Customer not found"));

		mockMvc.perform(get("/retail/rewards/customers/{customerId}", customerId)).andExpect(status().isBadRequest());
	}
}