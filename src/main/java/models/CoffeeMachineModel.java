package models;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CoffeeMachineModel {

  private int outlets;
  Map<String, Integer> ingredients;
  Map<String, Map<String, Integer>> beverages;

  public CoffeeMachineModel(int outlets,
      Map<String, Integer> ingredients,
      Map<String, Map<String, Integer>> beverages) {
    this.outlets = outlets;
    this.ingredients = ingredients;
    this.beverages = beverages;
  }

  public CoffeeMachineModel() {
  }

  public int getOutlets() {
    return outlets;
  }

  public Map<String, Integer> getIngredients() {
    return ingredients;
  }

  public Map<String, Map<String, Integer>> getBeverages() {
    return beverages;
  }
}
