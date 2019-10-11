package com.cocowmn.stockxtruetosize.repositories;


import com.cocowmn.stockxtruetosize.models.SneakerCrowdsourceData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SneakerCrowdsourceRepository extends JpaRepository<SneakerCrowdsourceData, Long> {

}
