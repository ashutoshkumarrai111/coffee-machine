package utils;

import core.CoffeeMachine;
import java.util.List;
import models.Ingredient;
import utils.CoffeeMachineOperation;

public class CoffeeMachineProcessor implements Runnable {
  private CoffeeMachine coffeeMachine;
  private CoffeeMachineOperation operation;
  private String beverageName;
  private List<Ingredient> ingredients;

  public CoffeeMachineProcessor(CoffeeMachine coffeeMachine,
      CoffeeMachineOperation operation, String beverageName,
      List<Ingredient> ingredients) {
    this.coffeeMachine = coffeeMachine;
    this.operation = operation;
    this.beverageName = beverageName;
    this.ingredients = ingredients;
  }

  @Override
  public void run() {
    switch (operation) {
      case GET_BEVERAGE: getBeverage(); break;
      case REFILL_INGREDIENTS: refillIngredients(); break;
    }

  }

  private void getBeverage() {
    try {
      coffeeMachine.getBeverage(beverageName);
    } catch (InterruptedException e) {
      System.out.println("Failed to get Beverage");
    }
  }

  private void refillIngredients() {
    coffeeMachine.refillIngredients(ingredients);
  }
}
