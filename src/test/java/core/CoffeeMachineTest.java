package core;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import models.Beverage;
import models.CoffeeMachineModel;
import models.Ingredient;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import utils.CoffeeMachineOperation;
import utils.CoffeeMachineProcessor;

public class CoffeeMachineTest {

  private CoffeeMachineModel coffeeMachineModel;
  private CoffeeMachine coffeeMachine;

  @Before
  public void initialiseData() throws IOException, ParseException {
    JSONParser parser = new JSONParser();
    Object obj = parser.parse(new FileReader(
        "/Users/ashutosh.rai/workspace/coffe-machine/src/test/resources/data/CoffeeMachineData.json"));

    JSONObject jsonObject = (JSONObject) obj;

    coffeeMachineModel = new ObjectMapper()
        .readValue(jsonObject.toJSONString(), CoffeeMachineModel.class);

    List<Ingredient> ingredients = coffeeMachineModel.getIngredients().entrySet().stream()
        .map(es -> new Ingredient(es.getKey(), es.getValue())).collect(
            Collectors.toList());

    List<Beverage> beverages = coffeeMachineModel.getBeverages().entrySet().stream()
        .map(es -> new Beverage(
            es.getKey(),
            es.getValue().entrySet().stream()
                .map(ingred -> new Ingredient(ingred.getKey(), ingred.getValue())).collect(
                Collectors.toList()))).collect(Collectors.toList());

    coffeeMachine = new CoffeeMachine(coffeeMachineModel.getOutlets(), ingredients, beverages);
  }

  //Sample test 1, 2, 3 are sequential execution of test cases in examples
  @Test
  public void test1() throws InterruptedException {

    coffeeMachine.getBeverage("hot_tea");
    coffeeMachine.getBeverage("hot_coffee");
    coffeeMachine.getBeverage("green_tea");
    coffeeMachine.getBeverage("black_tea");
  }

  @Test
  public void test2() throws InterruptedException {

    coffeeMachine.getBeverage("hot_tea");
    coffeeMachine.getBeverage("black_tea");
    coffeeMachine.getBeverage("green_tea");
    coffeeMachine.getBeverage("hot_coffee");

  }

  @Test
  public void test3() throws InterruptedException {

    coffeeMachine.getBeverage("hot_coffee");
    coffeeMachine.getBeverage("black_tea");
    coffeeMachine.getBeverage("green_tea");
    coffeeMachine.getBeverage("hot_tea");

  }


  //Test4 is to test refill
  @Test
  public void test4() throws InterruptedException {

    coffeeMachine.getBeverage("hot_tea");
    coffeeMachine.getBeverage("hot_coffee");

    refillIngredients1();

    coffeeMachine.getBeverage("green_tea");
    coffeeMachine.getBeverage("black_tea");

  }

  //Checking execution with multiple tasks executed together.
  @Test
  public void test5() {

    ExecutorService executorService = Executors.newFixedThreadPool(coffeeMachine.getOutlets());

    Runnable processor1 = new CoffeeMachineProcessor(coffeeMachine, CoffeeMachineOperation.GET_BEVERAGE, "hot_tea", null);
    executorService.execute(processor1);

    Runnable processor2 = new CoffeeMachineProcessor(coffeeMachine, CoffeeMachineOperation.GET_BEVERAGE, "hot_coffee", null);
    executorService.execute(processor2);

    Runnable processor3 = new CoffeeMachineProcessor(coffeeMachine, CoffeeMachineOperation.GET_BEVERAGE, "green_tea", null);
    executorService.execute(processor3);

    Runnable processor4 = new CoffeeMachineProcessor(coffeeMachine, CoffeeMachineOperation.GET_BEVERAGE, "black_tea", null);
    executorService.execute(processor4);


    executorService.shutdown();
  }

  //Checking execution with multiple tasks executed together with refill
  @Test
  public void test6() {

    ExecutorService executorService = Executors.newFixedThreadPool(coffeeMachine.getOutlets());

    Runnable processor1 = new CoffeeMachineProcessor(coffeeMachine, CoffeeMachineOperation.GET_BEVERAGE, "hot_tea", null);
    executorService.execute(processor1);

    Runnable processor2 = new CoffeeMachineProcessor(coffeeMachine, CoffeeMachineOperation.GET_BEVERAGE, "hot_coffee", null);
    executorService.execute(processor2);

    Runnable processor3 = new CoffeeMachineProcessor(coffeeMachine, CoffeeMachineOperation.GET_BEVERAGE, "green_tea", null);
    executorService.execute(processor3);

    Runnable processor4 = new CoffeeMachineProcessor(coffeeMachine, CoffeeMachineOperation.GET_BEVERAGE, "black_tea", null);
    executorService.execute(processor4);

    refillIngredients1();

    executorService.shutdown();
  }

  private void refillIngredients1() {
    List<Ingredient> ingredients = Arrays.asList(
        new Ingredient("hot_water", 500),
        new Ingredient("hot_milk", 500),
        new Ingredient("ginger_syrup", 100),
        new Ingredient("sugar_syrup", 100),
        new Ingredient("tea_leaves_syrup", 100),
        new Ingredient("green_mixture", 100)

        );
    coffeeMachine.refillIngredients(ingredients);
  }
}
