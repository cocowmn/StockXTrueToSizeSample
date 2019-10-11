package com.cocowmn.stockxtruetosize.repositories;

import com.cocowmn.stockxtruetosize.models.Sneaker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SneakerRepository extends JpaRepository<Sneaker, String> {

}
