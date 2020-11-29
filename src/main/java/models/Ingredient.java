package models;

public class Ingredient {
  private String name;
  private int quantity;

  public Ingredient(String name, int quantity) {
    this.name = name;
    this.quantity = quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public String getName() {
    return name;
  }

  public int getQuantity() {
    return quantity;
  }
}
