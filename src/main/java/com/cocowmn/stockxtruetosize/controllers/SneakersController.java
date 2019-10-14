package com.cocowmn.stockxtruetosize.controllers;

import com.cocowmn.stockxtruetosize.dto.NewCrowdsourceData;
import com.cocowmn.stockxtruetosize.models.Sneaker;
import com.cocowmn.stockxtruetosize.models.SneakerCrowdsourceData;
import com.cocowmn.stockxtruetosize.repositories.SneakerCrowdsourceRepository;
import com.cocowmn.stockxtruetosize.repositories.SneakerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class SneakersController {

    @Autowired SneakerRepository sneakers;
    @Autowired SneakerCrowdsourceRepository sneakerCrowdsourceData;

    @PostMapping("/sneakers/crowdsource")
    public ResponseEntity addCrowdsourcedData(@RequestBody NewCrowdsourceData requestBody) {
        if(!isValidTrueToSize(requestBody.getTrueToSizeValue())) {
            return ResponseEntity.badRequest().build();
        }

        Optional<Sneaker> findSneaker = sneakers.findById(requestBody.getId());

        return findSneaker
                .map(sneaker -> {
                    persistCrowdsourceData(sneaker, requestBody.getTrueToSizeValue());
                    return ResponseEntity.ok().build();
                }).orElseGet(() -> {
                    Sneaker sneaker = new Sneaker(requestBody.getId());
                    sneakers.save(sneaker);

                    persistCrowdsourceData(sneaker, requestBody.getTrueToSizeValue());
                    return ResponseEntity.ok().build();
                });
    }

    private void persistCrowdsourceData(Sneaker sneaker, int trueToSizeValue) {
        SneakerCrowdsourceData crowdsourceData = new SneakerCrowdsourceData();
        crowdsourceData.setSneaker(sneaker);
        crowdsourceData.setTrueToSizeValue(trueToSizeValue);

        sneakerCrowdsourceData.save(crowdsourceData);
    }

    @GetMapping("/sneakers/{productId}")
    public ResponseEntity<Integer> getTrueToSizeValue(@PathVariable String productId) {
        return ResponseEntity.ok().build();
    }


    private static boolean isValidTrueToSize(int value) {
        return value >= 1 && value <= 5;
    }

}
