package com.cocowmn.stockxtruetosize.controllers;

import com.cocowmn.stockxtruetosize.dto.NewCrowdsourceData;
import com.cocowmn.stockxtruetosize.models.Sneaker;
import com.cocowmn.stockxtruetosize.models.SneakerCrowdsourceData;
import com.cocowmn.stockxtruetosize.repositories.SneakerCrowdsourceRepository;
import com.cocowmn.stockxtruetosize.repositories.SneakerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class SneakersController {

    @Autowired SneakerRepository sneakers;
    @Autowired SneakerCrowdsourceRepository sneakerCrowdsourceData;

    @PostMapping("/sneakers/crowdsource")
    public ResponseEntity addCrowdsourcedData(@RequestBody NewCrowdsourceData requestBody) {
        if(!isValidTrueToSize(requestBody.getTrueToSizeValue())) {
            return ResponseEntity.badRequest().build();
        }

        return sneakers.findById(requestBody.getId())
                .map(sneaker -> {
                    persistCrowdsourceData(sneaker, requestBody.getTrueToSizeValue());
                    return ResponseEntity.ok().build();
                }).orElseGet(() -> {
                    Sneaker sneaker = persistSneaker(requestBody.getId());
                    persistCrowdsourceData(sneaker, requestBody.getTrueToSizeValue());
                    return ResponseEntity.ok().build();
                });
    }

    @GetMapping("/sneakers/{productId}")
    public ResponseEntity<Double> getTrueToSizeValue(@PathVariable String productId) {
        return sneakers.findById(productId)
                .map(sneaker -> {
                    List<SneakerCrowdsourceData> crowdsourceData =
                            sneakerCrowdsourceData.findBySneaker(sneaker.getName());

                    List<Integer> trueToSizeValues = crowdsourceData.stream()
                            .map(SneakerCrowdsourceData::getTrueToSizeValue)
                            .collect(Collectors.toList());

                    Optional<Integer> sum = trueToSizeValues.stream().reduce(Integer::sum);

                    if(sum.isEmpty()) {
                        return ResponseEntity.status(204).body((double) -1);
                    }

                    double averageTrueToSize =
                            ((double) sum.get()) / trueToSizeValues.size();

                    return ResponseEntity.ok(averageTrueToSize);
                }).orElseGet(() -> {
                    return ResponseEntity.status(204).body((double) -1);
                });
    }


    private static boolean isValidTrueToSize(int value) {
        return value >= 1 && value <= 5;
    }

    private Sneaker persistSneaker(String sneakerName) {
        return sneakers.save(new Sneaker(sneakerName));
    }

    private void persistCrowdsourceData(Sneaker sneaker, int trueToSizeValue) {
        SneakerCrowdsourceData crowdsourceData = new SneakerCrowdsourceData();
        crowdsourceData.setSneaker(sneaker);
        crowdsourceData.setTrueToSizeValue(trueToSizeValue);

        sneakerCrowdsourceData.save(crowdsourceData);
    }

}
