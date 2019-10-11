package com.cocowmn.stockxtruetosize.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "sneakers")
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Sneaker {
    @Id private String name;
}
