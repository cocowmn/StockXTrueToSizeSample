package com.cocowmn.stockxtruetosize.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SneakersController {

    @PostMapping("/sneakers/crowdsource")
    public ResponseEntity addCrowdsourcedData() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/sneakers/{productId}")
    public ResponseEntity<Integer> getTrueToSizeValue() {
        return ResponseEntity.ok().build();
    }

}
