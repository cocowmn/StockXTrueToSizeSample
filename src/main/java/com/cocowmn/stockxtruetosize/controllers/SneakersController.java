package com.cocowmn.stockxtruetosize.controllers;

import com.cocowmn.stockxtruetosize.dto.NewCrowdsourceData;
import com.cocowmn.stockxtruetosize.models.Sneaker;
import com.cocowmn.stockxtruetosize.models.SneakerCrowdsourceData;
import com.cocowmn.stockxtruetosize.repositories.SneakerCrowdsourceRepository;
import com.cocowmn.stockxtruetosize.repositories.SneakerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Optional;

@RestController
public class SneakersController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SneakersController.class);

    private final SneakerRepository sneakers;
    private final SneakerCrowdsourceRepository sneakerCrowdsourceData;

    public SneakersController(
            @Autowired SneakerRepository sneakers,
            @Autowired SneakerCrowdsourceRepository sneakerCrowdsourceData) {
        this.sneakers = sneakers;
        this.sneakerCrowdsourceData = sneakerCrowdsourceData;
    }

    @PostMapping("/sneakers/crowdsource")
    public ResponseEntity addCrowdsourcedData(@RequestBody NewCrowdsourceData requestBody) {
        if(isMalformedRequest(requestBody)) {
            LOGGER.debug("Received malformed crowdsource data from client");
        }

        if(!isValidTrueToSize(requestBody.getTrueToSizeValue())) {
            LOGGER.debug(
                    "Received invalid True-to-size crowdsource data from client: {}",
                    requestBody.getTrueToSizeValue());
            return ResponseEntity.badRequest().build();
        }

        try {
            return sneakers.findById(requestBody.getId())
                    .map(sneaker -> {
                        try {
                            persistCrowdsourceData(sneaker, requestBody.getTrueToSizeValue());
                            return ResponseEntity.ok().build();
                        } catch (Exception exception) {
                            LOGGER.error("Failure communicating with Crowdsource Repository");
                            return ResponseEntity.status(500).build();
                        }
                    }).orElseGet(() -> {
                        try {
                            Sneaker sneaker = persistSneaker(requestBody.getId());
                            persistCrowdsourceData(sneaker, requestBody.getTrueToSizeValue());
                            return ResponseEntity.ok().build();
                        } catch (Exception exception) {
                            LOGGER.error("Failure communicating with Sneaker Repository");
                            return ResponseEntity.status(500).build();
                        }
                    });
        } catch (Exception exception) {
            LOGGER.error(
                    "Failure retrieving Sneaker( {} ) from Sneaker Repository",
                    requestBody.getId());
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/sneakers/{productId}")
    public ResponseEntity<Double> getTrueToSizeValue(@PathVariable String productId) {
        try {
            return sneakers.findById(productId)
                    .map(sneaker -> {
                        try {
                            Optional<Double> averageTrueToSize = getAverageTrueToSize(sneaker);
                            return averageTrueToSize.isPresent()
                                    ? getTrueToSizeResponseEntity(averageTrueToSize.get())
                                    : getNoDataAvailableResponseEntity();
                        } catch (Exception exception) {
                            return ResponseEntity.status(500).body((double) -1);
                        }
                    }).orElseGet(this::getNoDataAvailableResponseEntity);
        } catch (Exception exception) {
            LOGGER.error(
                    "Failure retrieving Sneaker( {} ) from Sneaker Repository",
                    productId);
            return ResponseEntity.status(500).build();
        }
    }

    private Sneaker persistSneaker(String sneakerName) {
        Sneaker sneaker = sneakers.save(new Sneaker(sneakerName));
        LOGGER.trace("Sneaker( {} ) persisted to database", sneaker.getName());
        return sneaker;
    }

    private void persistCrowdsourceData(Sneaker sneaker, int trueToSizeValue) {
        SneakerCrowdsourceData crowdsourceData = new SneakerCrowdsourceData();
        crowdsourceData.setSneaker(sneaker);
        crowdsourceData.setTrueToSizeValue(trueToSizeValue);

        sneakerCrowdsourceData.save(crowdsourceData);
        LOGGER.trace(
                "Crowdsource Data { Sneaker( {} ), True-to-Size Value( {} ) } persisted to database",
                sneaker.getName(), trueToSizeValue);
    }

    private Optional<Double> getAverageTrueToSize(Sneaker sneaker) {
        int recordsAvailable = sneakerCrowdsourceData.countSneakerCrowdsourceDataBySneaker_Name(sneaker.getName());

        return recordsAvailable == 0
                ? Optional.empty()
                : Optional.of(sneakerCrowdsourceData.getTrueToSizeValue(sneaker.getName()));
    }

    private ResponseEntity<Double> getTrueToSizeResponseEntity(double averageTrueToSize) {
        return ResponseEntity.ok(averageTrueToSize);
    }

    private ResponseEntity<Double> getNoDataAvailableResponseEntity() {
        return ResponseEntity.status(204).body((double) -1);
    }

    private static boolean isValidTrueToSize(int value) {
        return value >= 1 && value <= 5;
    }

    private static boolean isMalformedRequest(NewCrowdsourceData requestBody) {
        return Objects.nonNull(requestBody)
                && Objects.nonNull(requestBody.getId());
    }

}
