package com.cocowmn.stockxtruetosize.models;

import javax.persistence.*;

@Table(name = "sneaker-crowdsource-data")
@Entity
public class SneakerCrowdsourceData {

    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false)
    private int trueToSizeValue;

    @ManyToOne
    private Sneaker sneaker;

}
