package com.cocowmn.stockxtruetosize.controllers;

import com.cocowmn.stockxtruetosize.dto.NewCrowdsourceData;
import com.cocowmn.stockxtruetosize.repositories.SneakerCrowdsourceRepository;
import com.cocowmn.stockxtruetosize.repositories.SneakerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class SneakersController {

    @Autowired SneakerRepository sneakers;
    @Autowired SneakerCrowdsourceRepository sneakerCrowdsourceData;

    @PostMapping("/sneakers/crowdsource")
    public ResponseEntity addCrowdsourcedData(@RequestBody NewCrowdsourceData requestBody) {
        return isValidTrueToSize(requestBody.getTrueToSizeValue())
                ? ResponseEntity.ok().build()
                : ResponseEntity.badRequest().build();
    }

    @GetMapping("/sneakers/{productId}")
    public ResponseEntity<Integer> getTrueToSizeValue(@PathVariable String productId) {
        return ResponseEntity.ok().build();
    }


    private static boolean isValidTrueToSize(int value) {
        return value >= 1 && value <= 5;
    }

}
