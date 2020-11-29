package core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import models.Beverage;
import models.Ingredient;

public class CoffeeMachine {

  private final int outlets;

  // Storing data in memory and using ConcurrentHashMap for thread safety.
  private Map<String, Integer> ingredientsByName = new ConcurrentHashMap<>();
  private Map<String, Integer> safeIngredientQuantities = new ConcurrentHashMap<>();
  private Map<String, List<Ingredient>> beverageIngredients;


  public CoffeeMachine(int outlets, List<Ingredient> ingredients, List<Beverage> beverages) {
    this.outlets = outlets;

    this.beverageIngredients = beverages.stream()
        .collect(Collectors.toConcurrentMap(Beverage::getName, Beverage::getIngredients));

    setIngredientQuantity(ingredients);
    setSafeIngredientQuantities();
  }

  private void setIngredientQuantity(List<Ingredient> ingredients) {
    for (Ingredient ingredient : ingredients) {
      int quanity = 0;
      if (ingredientsByName.containsKey(ingredient.getName())) {
        quanity = ingredientsByName.get(ingredient.getName());
      }
      ingredientsByName.put(ingredient.getName(), quanity + ingredient.getQuantity());
    }
  }

  private void setSafeIngredientQuantities() {
    for (Entry<String, List<Ingredient>> beverageIngredient : beverageIngredients.entrySet()) {
      List<Ingredient> ingredientsForBeverage = beverageIngredient.getValue();
      for (Ingredient ingredient : ingredientsForBeverage) {
        if (!safeIngredientQuantities.containsKey(ingredient.getName())
            || safeIngredientQuantities.get(ingredient.getName()) < outlets * ingredient
            .getQuantity()) {
          safeIngredientQuantities.put(ingredient.getName(), outlets * ingredient.getQuantity());
        }
      }
    }
  }

  public int getOutlets() {
    return outlets;
  }

  public void refillIngredients(List<Ingredient> refillIngredients) {
    System.out.println(Thread.currentThread().getName() + ": Refilling ingredients");
    for (Ingredient refillIngredient : refillIngredients) {
      int quantity = 0;
      if (ingredientsByName.containsKey(refillIngredient.getName())) {
        quantity = ingredientsByName.get(refillIngredient.getName());
      }
      ingredientsByName.put(refillIngredient.getName(), quantity + refillIngredient.getQuantity());
    }
  }

  public void addBeverages(List<Beverage> newBeverages) {
    for (Beverage newBeverage : newBeverages) {
      if (!beverageIngredients.containsKey(newBeverage.getName())) {
        beverageIngredients.put(newBeverage.getName(), newBeverage.getIngredients());
        System.out.println(Thread.currentThread().getName() + ": Beverage " + newBeverage.getName() + " added successfully.");
      } else {
        System.out.println(Thread.currentThread().getName() + ": Beverage " + newBeverage.getName() + " is already present.");
      }
    }
    setSafeIngredientQuantities();
  }

  public void getBeverage(String beverageName) throws InterruptedException {
    System.out.println(Thread.currentThread().getName() + ": Beverage Selected " + beverageName);

    List<Ingredient> requiredIngredients = beverageIngredients.get(beverageName);

    boolean blockSuccess = blockIngredients(requiredIngredients);
    if (blockSuccess) {
      serveBeverage(beverageName);
    }
    checkIngredientQuanties();
  }

  /**
   * Assuming it takes 2 secs to serve a beverage
   */
  private void serveBeverage(String beverageName) throws InterruptedException {
    System.out.println(Thread.currentThread().getName() + ": Preparing " + beverageName);
    Thread.sleep(2000);
    System.out.println(Thread.currentThread().getName() + ": " + beverageName + " is prepared.");
  }

  public void checkIngredientQuanties() {
    for (Entry<String, Integer> ingredientQuantity : ingredientsByName.entrySet()) {
      if (ingredientQuantity.getValue() < safeIngredientQuantities
          .get(ingredientQuantity.getKey())) {
        System.out.println(Thread.currentThread().getName() + ": Ingredient " + ingredientQuantity.getKey() + " is running low. Required : "
                + safeIngredientQuantities.get(ingredientQuantity.getKey()) + " Present : "
                + ingredientQuantity.getValue());
      }
    }
  }

  private boolean blockIngredients(List<Ingredient> requiredIngredients) {
    List<String> insufficientIngredients = getInsufficientIngredients(requiredIngredients);

    if (insufficientIngredients.isEmpty()) {
      for (Ingredient requiredIngredient : requiredIngredients) {
        ingredientsByName.put(requiredIngredient.getName(),
            ingredientsByName.get(requiredIngredient.getName()) - requiredIngredient
                .getQuantity());
      }
      return true;
    }
    System.out.println(
        Thread.currentThread().getName() + ": Beverage cannot be served. Ingredient not sufficient : " + insufficientIngredients);
    return false;
  }

  /**
   * An ingredient x is termed running low if the beverage b that uses max quantity of x cannot be served all outlets at once.
   *
   */
  private List<String> getInsufficientIngredients(List<Ingredient> requiredIngredients) {
    List<String> insufficientIngredients = new ArrayList<>();
    for (Ingredient requiredIngredient : requiredIngredients) {
      if (!(ingredientsByName.get(requiredIngredient.getName()) != null
          && ingredientsByName.get(requiredIngredient.getName()) >= requiredIngredient
          .getQuantity())) {
        insufficientIngredients.add(requiredIngredient.getName());
      }
    }
    return insufficientIngredients;
  }
}
