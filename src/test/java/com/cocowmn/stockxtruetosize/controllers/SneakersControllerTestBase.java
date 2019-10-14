package com.cocowmn.stockxtruetosize.controllers;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class SneakersControllerTestBase {

    static final String TEST_DOMAIN = "sneakers";

    @Autowired MockMvc mockMvc;
    @Autowired SneakersController controller;


    @Test public void contextloads() {
        assertThat(controller).isNotNull();
    }

    void postSuccessfulCrowdsourceData(String sneakerName, int trueToSizeValue) throws Exception {
        mockMvc.perform(
                post(uri(TEST_DOMAIN, "crowdsource"))
                        .content(createAddSneakerDataRequest(sneakerName, trueToSizeValue).toString())
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    void postFailedCrowdsourceData(String sneakerName, int trueToSizeValue) throws Exception {
        mockMvc.perform(
                post(uri(TEST_DOMAIN, "crowdsource"))
                        .content(createAddSneakerDataRequest(sneakerName, trueToSizeValue).toString())
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    void getSneakerNoDataAvailable(String sneakerName) throws Exception {
        mockMvc.perform(
                get(uri(TEST_DOMAIN, sneakerName)))
                .andDo(print())
                .andExpect(status().is(204));
    }

    static String uri(String... path) {
        return "/" + String.join("/", path);
    }

    static JSONObject createAddSneakerDataRequest(String sneakerName, int trueToSizeValue) {
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("id", sneakerName);
            requestBody.put("trueToSizeValue", trueToSizeValue);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return requestBody;
    }

}
