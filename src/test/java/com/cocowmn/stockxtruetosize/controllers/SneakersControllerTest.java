package com.cocowmn.stockxtruetosize.controllers;

import com.cocowmn.stockxtruetosize.models.Sneaker;
import com.cocowmn.stockxtruetosize.models.SneakerCrowdsourceData;
import com.cocowmn.stockxtruetosize.repositories.SneakerCrowdsourceRepository;
import com.cocowmn.stockxtruetosize.repositories.SneakerRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class SneakersControllerTest {

    private static final String TEST_DOMAIN = "sneakers";
    private static final String SNEAKER_IN_DATABASE = "adidas Yeezy";

    private static final long CROWDSOURCE_ID_IN_DATABASE = 1; // Value determined by autogenerated id. prescribed by production code, not test
    private static final int CROWDSOURCE_VALUE_IN_DATABASE = 3;

    @Autowired private SneakerRepository sneakers;
    @Autowired private SneakerCrowdsourceRepository crowdsource;

    @Autowired private MockMvc mockMvc;
    @Autowired private SneakersController controller;


    @Before
    public void setUp() {
        Sneaker sneaker = new Sneaker(SNEAKER_IN_DATABASE);
        SneakerCrowdsourceData crowdsourceData = new SneakerCrowdsourceData(
                CROWDSOURCE_ID_IN_DATABASE,
                CROWDSOURCE_VALUE_IN_DATABASE,
                sneaker
        );

        sneakers.save(sneaker);
        crowdsource.save(crowdsourceData);

        assertThat(sneakers.findById(SNEAKER_IN_DATABASE)).isPresent();
        assertThat(crowdsource.findById(CROWDSOURCE_ID_IN_DATABASE)).isPresent();
    }


    @Test
    public void contextloads() {
        assertThat(controller).isNotNull();
    }

    @Test
    public void addSneakerData_forNewSneaker_whenSuccessful_persistsToDatabase() throws Exception {
        String sneakerName = UUID.randomUUID().toString();
        int trueToSizeValue = 3;

        postSuccessfulCrowdsourceData(sneakerName, trueToSizeValue);

        assertThat(sneakers.findById(sneakerName)).isPresent();
        assertThat(crowdsource.findBySneaker(sneakerName)).isNotEmpty();
    }

    @Test
    public void addSneakerData_forExistingSneaker_whenSuccessful_persistsToDatabase() throws Exception {
        int trueToSizeValue = 5;
        postSuccessfulCrowdsourceData(SNEAKER_IN_DATABASE, trueToSizeValue);

        assertThat(crowdsource.findBySneaker(SNEAKER_IN_DATABASE)).hasSize(2);
    }

    @Test
    public void addSneakerData_returns200_whenValidDataProvided() throws Exception {
        String sneakerName = UUID.randomUUID().toString();
        int trueToSizeValue = 3;

        postSuccessfulCrowdsourceData(sneakerName, trueToSizeValue);
    }

    @Test
    public void addSneakerData_returns400_whenInvalidTrueToSizeProvided() throws Exception {
        String sneakerName = UUID.randomUUID().toString();
        List<Integer> invalidSizes = Arrays.asList(-1, 0, 6, Integer.MAX_VALUE, Integer.MIN_VALUE);

        for (Integer invalidSize : invalidSizes) {
            postFailedCrowdsourceData(sneakerName, invalidSize);
        }
    }

    @Test
    public void getTrueToSizeValue_returns200_andValue_forExistingSneaker() throws Exception {
        String sneakerName = UUID.randomUUID().toString();
        List<Integer> trueToSizeValues = Arrays.asList(1, 2, 3, 3, 3, 3, 3, 4, 4, 5, 5);

        for (Integer trueToSizeValue : trueToSizeValues) {
            JSONObject requestBody = createAddSneakerDataRequest(sneakerName, trueToSizeValue);

            postSuccessfulCrowdsourceData(sneakerName, trueToSizeValue);
        }

        mockMvc.perform(get(uri(TEST_DOMAIN, sneakerName)))
                .andDo(print())
                .andDo(handler -> {
                    double receivedAverageTrueToSizeValue =
                            Double.parseDouble(handler.getResponse().getContentAsString());
                    double actualAverageTrueToSizeValue =
                            ((double) trueToSizeValues.stream().reduce(Integer::sum).get()) / trueToSizeValues.size();

                    assertThat(receivedAverageTrueToSizeValue).isEqualTo(actualAverageTrueToSizeValue);
                })
                .andExpect(status().isOk());
    }

    @Test
    public void getTrueToSizeValue_returns204_andNegativeOne_forSneakerWithNoData() throws Exception {
        String sneakerInDatabase_withNoCrowdsourceData = UUID.randomUUID().toString();
        String sneakerNotInDatabase = UUID.randomUUID().toString();

        sneakers.save(new Sneaker(sneakerInDatabase_withNoCrowdsourceData));

        getSneakerNoDataAvailable(sneakerInDatabase_withNoCrowdsourceData);
        getSneakerNoDataAvailable(sneakerNotInDatabase);
    }


    private void postSuccessfulCrowdsourceData(String sneakerName, int trueToSizeValue) throws Exception {
        mockMvc.perform(
                post(uri(TEST_DOMAIN, "crowdsource"))
                        .content(createAddSneakerDataRequest(sneakerName, trueToSizeValue).toString())
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    private void postFailedCrowdsourceData(String sneakerName, int trueToSizeValue) throws Exception {
        mockMvc.perform(
                post(uri(TEST_DOMAIN, "crowdsource"))
                        .content(createAddSneakerDataRequest(sneakerName, trueToSizeValue).toString())
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    private void getSneakerNoDataAvailable(String sneakerName) throws Exception {
        mockMvc.perform(
                get(uri(TEST_DOMAIN, sneakerName)))
                .andDo(print())
                .andExpect(status().is(204));
    }

    private static String uri(String... path) {
        return "/" + String.join("/", path);
    }

    private static JSONObject createAddSneakerDataRequest(String sneakerName, int trueToSizeValue) {
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