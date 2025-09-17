package org.nutriGuideBuddy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NutriGuideBuddyApplication {

  // TODO:: ADD SEEDER FOR FOOD
  // -> ADD BETWEEN 5 TO 10 FOODS FOR EACH MEAL
  // -> ADD 1-4 SERVINGS PER FOOD
  // -> ADD 5-15 NUTRIENTS PER FOOD
  public static void main(String[] args) {
    SpringApplication.run(NutriGuideBuddyApplication.class, args);
  }
}
