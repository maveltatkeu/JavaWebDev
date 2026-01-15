package com.udacity.jdnd.course1.model;

public class TacoOrder {
  private Integer orderId;
  private String tacoName;
  private Double tacoPrice;
  private Integer count;

  public Integer getOrderId() {
    return orderId;
  }

  public void setOrderId(Integer orderId) {
    this.orderId = orderId;
  }

  public String getTacoName() {
    return tacoName;
  }

  public void setTacoName(String tacoName) {
    this.tacoName = tacoName;
  }

  public Double getTacoPrice() {
    return tacoPrice;
  }

  public void setTacoPrice(Double tacoPrice) {
    this.tacoPrice = tacoPrice;
  }

  public Integer getCount() {
    return count;
  }

  public void setCount(Integer count) {
    this.count = count;
  }
}
