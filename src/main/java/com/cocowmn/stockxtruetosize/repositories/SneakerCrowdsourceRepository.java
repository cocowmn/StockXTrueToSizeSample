package com.cocowmn.stockxtruetosize.repositories;


import com.cocowmn.stockxtruetosize.models.SneakerCrowdsourceData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SneakerCrowdsourceRepository extends JpaRepository<SneakerCrowdsourceData, Long> {

    List<SneakerCrowdsourceData> findBySneaker_Name(String sneaker);

    Integer countSneakerCrowdsourceDataBySneaker_Name(String sneaker);

    @Query("SELECT avg(data.trueToSizeValue) FROM SneakerCrowdsourceData data WHERE lower(data.sneaker) LIKE lower(?1)")
    Double getTrueToSizeValue(String sneaker);

}
