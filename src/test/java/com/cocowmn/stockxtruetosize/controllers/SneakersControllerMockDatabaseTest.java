package com.cocowmn.stockxtruetosize.controllers;

import com.cocowmn.stockxtruetosize.repositories.SneakerCrowdsourceRepository;
import com.cocowmn.stockxtruetosize.repositories.SneakerRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class SneakersControllerMockDatabaseTest extends SneakersControllerTestBase {

    @MockBean private SneakerRepository sneakers;
    @MockBean private SneakerCrowdsourceRepository crowdsource;

    @Autowired private MockMvc mockMvc;


    @Before
    public void setUp() {
        when(sneakers.save(any())).thenThrow(new RuntimeException());
        when(sneakers.findById(anyString())).thenThrow(new RuntimeException());

        when(crowdsource.save(any())).thenThrow(new RuntimeException());
        when(crowdsource.findBySneaker(anyString())).thenThrow(new RuntimeException());
    }

    @Test
    public void postCrowdsourceData_returns500_whenDatabaseFails() throws Exception {
        postDatabaseUnavailableCrowdsourceData(UUID.randomUUID().toString(), 5);
    }

    @Test
    public void getSneakerData_returns500_whenDatabaseFails() throws Exception {
        getDatabaseUnavailableSneakerData(UUID.randomUUID().toString());
    }


    void postDatabaseUnavailableCrowdsourceData(String sneakerName, int trueToSizeValue) throws Exception {
        mockMvc.perform(
                post(uri(TEST_DOMAIN, "crowdsource"))
                        .content(createAddSneakerDataRequest(sneakerName, trueToSizeValue).toString())
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().is5xxServerError());
    }

    void getDatabaseUnavailableSneakerData(String sneakerName) throws Exception {
        mockMvc.perform(
                get(uri(TEST_DOMAIN, sneakerName)))
                .andDo(print())
                .andExpect(status().is5xxServerError());
    }

}