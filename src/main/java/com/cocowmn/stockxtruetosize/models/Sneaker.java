package com.cocowmn.stockxtruetosize.models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "sneakers")
@Entity
public class Sneaker {

    @Id private String name;

}
