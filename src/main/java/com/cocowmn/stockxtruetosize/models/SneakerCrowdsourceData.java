package com.cocowmn.stockxtruetosize.models;

import lombok.*;

import javax.persistence.*;

@Table(name = "sneaker_crowdsource_data")
@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class SneakerCrowdsourceData {

    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false)
    private int trueToSizeValue;

    @ManyToOne
    private Sneaker sneaker;

}
