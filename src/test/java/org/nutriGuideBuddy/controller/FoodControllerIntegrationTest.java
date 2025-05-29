package org.nutriGuideBuddy.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.nutriGuideBuddy.domain.dto.meal.*;
import org.nutriGuideBuddy.domain.dto.user.JwtResponse;
import org.nutriGuideBuddy.domain.dto.user.UserCreate;
import org.nutriGuideBuddy.domain.dto.user.UserDetailsDto;
import org.nutriGuideBuddy.domain.enums.Gender;
import org.nutriGuideBuddy.domain.enums.WorkoutState;
import org.nutriGuideBuddy.enums.Credentials;
import org.nutriGuideBuddy.repository.UserRepository;
import org.nutriGuideBuddy.utils.JWTUtilEmailValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.web.header.Header;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.nutriGuideBuddy.utils.FoodUtils.*;

@SpringBootTest
@AutoConfigureWebTestClient
@ActiveProfiles("secret")
@Testcontainers
class FoodControllerIntegrationTest {

  @Autowired
  private WebTestClient webTestClient;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private JWTUtilEmailValidation jwtUtilEmailValidation;

  @Container
  public static GenericContainer<?> mysqlContainer = new GenericContainer<>("mysql:latest")
      .withExposedPorts(3306)
      .withEnv("MYSQL_ROOT_PASSWORD", "12345")
      .withEnv("MYSQL_DATABASE", "reactiveDB");

  @BeforeAll
  static void beforeAll() {
    mysqlContainer.start();
  }

  @AfterEach
  void afterEach() {
    cleanupDatabase().block();
  }

  private Mono<Void> cleanupDatabase() {
    return userRepository.findAllUsers()
        .flatMap(user -> userRepository.deleteUserById(user.getId()))
        .then();
  }

  @DynamicPropertySource
  static void setDatasourceProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.r2dbc.url", () -> "r2dbc:mysql://" + mysqlContainer.getHost() + ":" + mysqlContainer.getFirstMappedPort() + "/reactiveDB");
    registry.add("spring.liquibase.url", () -> "jdbc:mysql://" + mysqlContainer.getHost() + ":" + mysqlContainer.getFirstMappedPort() + "/reactiveDB");
  }

  @Test
  void givenNoAuth_whenTestingAddFood_thenServerMustReturnUnauthorized() {

    String MEAL_ID = UUID.randomUUID().toString();
    webTestClient
        .post()
        .uri("/api/meals/" + MEAL_ID + "/insertFood")
        .bodyValue(createInsertedFood(null, null, null, null, null, null))
        .exchange()
        .expectStatus().isUnauthorized();
  }

  @Test
  void givenValidAuthAndNotFullUserDetails_whenTestingAddFood_thenServerMustReturnForbidden() {

    String MEAL_ID = UUID.randomUUID().toString();

    Header authHeader = setUpUserAndReturnAuthHeader();

    webTestClient
        .post()
        .uri("/api/meals/" + MEAL_ID + "/insertFood")
        .header(authHeader.getName(), authHeader.getValues().get(0))
        .bodyValue(createInsertedFood(null, null, null, null, null, null))
        .exchange()
        .expectStatus().isForbidden();
  }

  @Test
  void givenValidAuthAndFullUserDetailsAndInvalidMealId_whenTestingAddFood_thenServerMustReturnBadRequest() {

    String INVALID_MEAL_ID = UUID.randomUUID().toString();
    Header authHeader = setUpUserWithFullUserDetailsAndReturnAuthHeader();

    webTestClient
        .post()
        .uri("/api/meals/" + INVALID_MEAL_ID + "/insertFood")
        .header(authHeader.getName(), authHeader.getValues().get(0))
        .bodyValue(createInsertedFood(null, null, null, null, null, null))
        .exchange()
        .expectStatus().isBadRequest();
  }

  @Test
  void givenValidAuthAndFullUserDetailsAndValidMealIdAndInvalidFoodName_whenTestingAddFood_thenServerMustReturnBadRequest() {

    Header authHeader = setUpUserWithFullUserDetailsAndReturnAuthHeader();
    String VALID_MEAL_ID = obtainValidMealId(authHeader);

    getInvalidListOfInsertedFoodNames()
        .forEach(INVALID_NAME -> {
          webTestClient
              .post()
              .uri("/api/meals/" + VALID_MEAL_ID + "/insertFood")
              .header(authHeader.getName(), authHeader.getValues().get(0))
              .bodyValue(createInsertedFood(
                  INVALID_NAME,
                  createValidCalorieView(),
                  createValidServingView(),
                  createValidFoodInfoView(),
                  List.of(),
                  List.of()
              ))
              .exchange()
              .expectStatus().isBadRequest();
        });
  }

  @Test
  void givenValidAuthAndFullUserDetailsAndValidMealIdAndInvalidFoodCalories_whenTestingAddFood_thenServerMustReturnBadRequest() {

    Header authHeader = setUpUserWithFullUserDetailsAndReturnAuthHeader();
    String VALID_MEAL_ID = obtainValidMealId(authHeader);

    getInvalidListOfInsertedCalorieAmount()
        .forEach(INVALID_CALORIE_AMOUNT -> {
          webTestClient
              .post()
              .uri("/api/meals/" + VALID_MEAL_ID + "/insertFood")
              .header(authHeader.getName(), authHeader.getValues().get(0))
              .bodyValue(createInsertedFood(
                  Credentials.VALID_MEAL_NAME.getValue(),
                  new CalorieView(INVALID_CALORIE_AMOUNT, Credentials.VALID_FOOD_MEASURE_UNIT.getValue()),
                  createValidServingView(),
                  createValidFoodInfoView(),
                  List.of(),
                  List.of()
              ))
              .exchange()
              .expectStatus().isBadRequest();
        });

    //Invalid with null
    webTestClient
        .post()
        .uri("/api/meals/" + VALID_MEAL_ID + "/insertFood")
        .header(authHeader.getName(), authHeader.getValues().get(0))
        .bodyValue(createInsertedFood(
            Credentials.VALID_MEAL_NAME.getValue(),
            new CalorieView(null, Credentials.VALID_FOOD_MEASURE_UNIT.getValue()),
            createValidServingView(),
            createValidFoodInfoView(),
            List.of(),
            List.of()
        ))
        .exchange()
        .expectStatus().isBadRequest();
  }

  @Test
  void givenValidAuthAndFullUserDetailsAndValidMealIdAndInvalidServing_whenTestingAddFood_thenServerMustReturnBadRequest() {

    Header authHeader = setUpUserWithFullUserDetailsAndReturnAuthHeader();
    String VALID_MEAL_ID = obtainValidMealId(authHeader);

    getInvalidServings()
        .forEach(INVALID_SERVING -> {
          webTestClient
              .post()
              .uri("/api/meals/" + VALID_MEAL_ID + "/insertFood")
              .header(authHeader.getName(), authHeader.getValues().get(0))
              .bodyValue(createInsertedFood(
                  Credentials.VALID_MEAL_NAME.getValue(),
                  createValidCalorieView(),
                  INVALID_SERVING,
                  createValidFoodInfoView(),
                  List.of(),
                  List.of()
              ))
              .exchange()
              .expectStatus().isBadRequest();
        });

    webTestClient
        .post()
        .uri("/api/meals/" + VALID_MEAL_ID + "/insertFood")
        .header(authHeader.getName(), authHeader.getValues().get(0))
        .bodyValue(createInsertedFood(
            Credentials.VALID_MEAL_NAME.getValue(),
            createValidCalorieView(),
            null,
            createValidFoodInfoView(),
            List.of(),
            List.of()
        ))
        .exchange()
        .expectStatus().isBadRequest();
  }

  @Test
  void givenValidAuthAndFullUserDetailsAndValidMealIdAndInvalidListServings_whenTestingAddFood_thenServerMustReturnBadRequest() {

    Header authHeader = setUpUserWithFullUserDetailsAndReturnAuthHeader();
    String VALID_MEAL_ID = obtainValidMealId(authHeader);

    getInvalidServings()
        .forEach(INVALID_SERVING -> {
          webTestClient
              .post()
              .uri("/api/meals/" + VALID_MEAL_ID + "/insertFood")
              .header(authHeader.getName(), authHeader.getValues().get(0))
              .bodyValue(createInsertedFood(
                  Credentials.VALID_MEAL_NAME.getValue(),
                  createValidCalorieView(),
                  createValidServingView(),
                  createValidFoodInfoView(),
                  List.of(INVALID_SERVING),
                  List.of()
              ))
              .exchange()
              .expectStatus().isBadRequest();
        });

    webTestClient
        .post()
        .uri("/api/meals/" + VALID_MEAL_ID + "/insertFood")
        .header(authHeader.getName(), authHeader.getValues().get(0))
        .bodyValue(createInsertedFood(
            Credentials.VALID_MEAL_NAME.getValue(),
            createValidCalorieView(),
            createValidServingView(),
            createValidFoodInfoView(),
            null,
            List.of()
        ))
        .exchange()
        .expectStatus().isBadRequest();
  }

  @Test
  void givenValidAuthAndFullUserDetailsAndValidMealIdAndInvalidFoodInfo_whenTestingAddFood_thenServerMustReturnBadRequest() {

    Header authHeader = setUpUserWithFullUserDetailsAndReturnAuthHeader();
    String VALID_MEAL_ID = obtainValidMealId(authHeader);

    getInvalidFoodInfoViews()
        .forEach(INVALID_FOOD_INFO -> {
          webTestClient
              .post()
              .uri("/api/meals/" + VALID_MEAL_ID + "/insertFood")
              .header(authHeader.getName(), authHeader.getValues().get(0))
              .bodyValue(createInsertedFood(
                  Credentials.VALID_MEAL_NAME.getValue(),
                  createValidCalorieView(),
                  createValidServingView(),
                  INVALID_FOOD_INFO,
                  List.of(),
                  List.of()
              ))
              .exchange()
              .expectStatus().isBadRequest();
        });
  }

  @Test
  void givenValidAuthAndFullUserDetailsAndValidMealIdAndNullFoodInfo_whenTestingAddFood_thenServerMustReturnOk() {

    Header authHeader = setUpUserWithFullUserDetailsAndReturnAuthHeader();
    String VALID_MEAL_ID = obtainValidMealId(authHeader);

    webTestClient
        .post()
        .uri("/api/meals/" + VALID_MEAL_ID + "/insertFood")
        .header(authHeader.getName(), authHeader.getValues().get(0))
        .bodyValue(createInsertedFood(
            Credentials.VALID_MEAL_NAME.getValue(),
            createValidCalorieView(),
            createValidServingView(),
            null,
            List.of(),
            List.of()
        ))
        .exchange()
        .expectStatus().isOk();
  }

  @Test
  void givenValidAuthAndFullUserDetailsAndValidMealIdAndNullNutrients_whenTestingAddFood_thenServerMustReturnBadRequest() {

    Header authHeader = setUpUserWithFullUserDetailsAndReturnAuthHeader();
    String VALID_MEAL_ID = obtainValidMealId(authHeader);

    //Invalid with null
    webTestClient
        .post()
        .uri("/api/meals/" + VALID_MEAL_ID + "/insertFood")
        .header(authHeader.getName(), authHeader.getValues().get(0))
        .bodyValue(createInsertedFood(
            Credentials.VALID_MEAL_NAME.getValue(),
            createValidCalorieView(),
            createValidServingView(),
            createValidFoodInfoView(),
            List.of(),
            null
        ))
        .exchange()
        .expectStatus().isBadRequest();
  }

  @Test
  void givenValidAuthAndFullUserDetailsAndValidMealIdAndValidBody_whenTestingAddFood_thenServerMustReturnOk() {

    Header authHeader = setUpUserWithFullUserDetailsAndReturnAuthHeader();
    String VALID_MEAL_ID = obtainValidMealId(authHeader);

    webTestClient
        .post()
        .uri("/api/meals/" + VALID_MEAL_ID + "/insertFood")
        .header(authHeader.getName(), authHeader.getValues().get(0))
        .bodyValue(createValidInsertedFoodWithEmptyNutritions())
        .exchange()
        .expectStatus().isOk();
  }

  @Test
  void givenValidAuthAndFullUserDetailsAndValidMealIdAndInvalidNutrientViews_whenTestingAddFood_thenServerMustReturnBadRequest() {

    Header authHeader = setUpUserWithFullUserDetailsAndReturnAuthHeader();
    String VALID_MEAL_ID = obtainValidMealId(authHeader);

    getInvalidNutrientViews()
        .forEach(INVALID_NUTRITION_VIEW -> {
          webTestClient
              .post()
              .uri("/api/meals/" + VALID_MEAL_ID + "/insertFood")
              .header(authHeader.getName(), authHeader.getValues().get(0))
              .bodyValue(createInsertedFood(
                  Credentials.VALID_MEAL_NAME.getValue(),
                  createValidCalorieView(),
                  createValidServingView(),
                  createValidFoodInfoView(),
                  List.of(),
                  List.of(INVALID_NUTRITION_VIEW)
              ))
              .exchange()
              .expectStatus().isBadRequest();
        });
  }

  @Test
  void givenValidAuthAndFullUserDetailsAndValidMealIdAndValidBody_whenTestingAddFoodWithAllPossibleNutrientViews_thenServerMustReturnOk() {

    Header authHeader = setUpUserWithFullUserDetailsAndReturnAuthHeader();
    String VALID_MEAL_ID = obtainValidMealId(authHeader);

    webTestClient
        .post()
        .uri("/api/meals/" + VALID_MEAL_ID + "/insertFood")
        .header(authHeader.getName(), authHeader.getValues().get(0))
        .bodyValue(createValidInsertedFoodWithEveryPossibleNutrientView())
        .exchange()
        .expectStatus().isOk();
  }

  @Test
  void givenNoAuth_whenTestingChangeFood_thenServerMustReturnUnauthorized() {

    String MEAL_ID = UUID.randomUUID().toString();
    String FOOD_ID = UUID.randomUUID().toString();

    webTestClient
        .put()
        .uri("/api/meals/" + MEAL_ID + "/insertFood/" + FOOD_ID)
        .bodyValue(createInsertedFood(null, null, null, null, null, null))
        .exchange()
        .expectStatus().isUnauthorized();
  }

  @Test
  void givenValidAuthAndNotFullUserDetails_whenTestingChangeFood_thenServerMustReturnForbidden() {

    Header authHeader = setUpUserAndReturnAuthHeader();
    String MEAL_ID = UUID.randomUUID().toString();
    String FOOD_ID = UUID.randomUUID().toString();

    webTestClient
        .put()
        .uri("/api/meals/" + MEAL_ID + "/insertFood/" + FOOD_ID)
        .header(authHeader.getName(), authHeader.getValues().get(0))
        .bodyValue(createInsertedFood(null, null, null, null, null, null))
        .exchange()
        .expectStatus().isForbidden();
  }

  @Test
  void givenValidAuthAndFullUserDetailsAndInvalidMealId_whenTestingChangeFood_thenServerMustReturnBadRequest() {

    Header authHeader = setUpUserWithFullUserDetailsAndReturnAuthHeader();
    String INVALID_MEAL_ID = UUID.randomUUID().toString();
    String FOOD_ID = UUID.randomUUID().toString();

    webTestClient
        .put()
        .uri("/api/meals/" + INVALID_MEAL_ID + "/insertFood/" + FOOD_ID)
        .header(authHeader.getName(), authHeader.getValues().get(0))
        .bodyValue(createInsertedFood(null, null, null, null, null, null))
        .exchange()
        .expectStatus().isBadRequest();
  }

  @Test
  void givenValidAuthAndFullUserDetailsAndValidMealIdAndInvalidFoodId_whenTestingChangeFood_thenServerMustReturnBadRequest() {

    Header authHeader = setUpUserWithFullUserDetailsAndReturnAuthHeader();
    String VALID_MEAL_ID = obtainValidMealId(authHeader);
    String INVALID_FOOD_ID = UUID.randomUUID().toString();

    webTestClient
        .put()
        .uri("/api/meals/" + VALID_MEAL_ID + "/insertFood/" + INVALID_FOOD_ID)
        .header(authHeader.getName(), authHeader.getValues().get(0))
        .bodyValue(createInsertedFood(null, null, null, null, null, null))
        .exchange()
        .expectStatus().isBadRequest();
  }

  @Test
  void givenValidAuthAndFullUserDetailsAndValidMealIdAndValidFoodIdAndEmptyBody_whenTestingChangeFood_thenServerMustReturnBadRequest() {

    Header authHeader = setUpUserWithFullUserDetailsAndReturnAuthHeader();
    String VALID_MEAL_ID = obtainValidMealId(authHeader);
    String VALID_INSERTED_FOOD_ID = obtainValidInsertedFoodId(authHeader, VALID_MEAL_ID);

    webTestClient
        .put()
        .uri("/api/meals/" + VALID_MEAL_ID + "/insertFood/" + VALID_INSERTED_FOOD_ID)
        .header(authHeader.getName(), authHeader.getValues().get(0))
        .bodyValue(createInsertedFood(null, null, null, null, null, null))
        .exchange()
        .expectStatus().isBadRequest();
  }

  @Test
  void givenValidAuthAndFullUserDetailsAndValidMealIdAndValidFoodIdAndInvalidFoodName_whenTestingChangeFood_thenServerMustReturnBadRequest() {

    Header authHeader = setUpUserWithFullUserDetailsAndReturnAuthHeader();
    String VALID_MEAL_ID = obtainValidMealId(authHeader);
    String VALID_INSERTED_FOOD_ID = obtainValidInsertedFoodId(authHeader, VALID_MEAL_ID);

    getInvalidListOfInsertedFoodNames()
        .forEach(INVALID_NAME -> {
          webTestClient
              .put()
              .uri("/api/meals/" + VALID_MEAL_ID + "/insertFood/" + VALID_INSERTED_FOOD_ID)
              .header(authHeader.getName(), authHeader.getValues().get(0))
              .bodyValue(createInsertedFood(
                  INVALID_NAME,
                  createValidCalorieView(),
                  createValidServingView(),
                  createValidFoodInfoView(),
                  List.of(),
                  List.of()
              ))
              .exchange()
              .expectStatus().isBadRequest();
        });

    //NULL TEST
    webTestClient
        .put()
        .uri("/api/meals/" + VALID_MEAL_ID + "/insertFood/" + VALID_INSERTED_FOOD_ID)
        .header(authHeader.getName(), authHeader.getValues().get(0))
        .bodyValue(createInsertedFood(
            null,
            createValidCalorieView(),
            createValidServingView(),
            createValidFoodInfoView(),
            List.of(),
            List.of()
        ))
        .exchange()
        .expectStatus().isBadRequest();
  }

  @Test
  void givenValidAuthAndFullUserDetailsAndValidMealIdAndValidFoodIdAndInvalidFoodCalories_whenTestingChangeFood_thenServerMustReturnBadRequest() {

    Header authHeader = setUpUserWithFullUserDetailsAndReturnAuthHeader();
    String VALID_MEAL_ID = obtainValidMealId(authHeader);
    String VALID_INSERTED_FOOD_ID = obtainValidInsertedFoodId(authHeader, VALID_MEAL_ID);

    getInvalidListOfInsertedCalorieAmount()
        .forEach(INVALID_CALORIE_AMOUNT -> {
          webTestClient
              .put()
              .uri("/api/meals/" + VALID_MEAL_ID + "/insertFood/" + VALID_INSERTED_FOOD_ID)
              .header(authHeader.getName(), authHeader.getValues().get(0))
              .bodyValue(createInsertedFood(
                  Credentials.VALID_MEAL_NAME.getValue(),
                  new CalorieView(INVALID_CALORIE_AMOUNT, Credentials.VALID_FOOD_MEASURE_UNIT.getValue()),
                  createValidServingView(),
                  createValidFoodInfoView(),
                  List.of(),
                  List.of()
              ))
              .exchange()
              .expectStatus().isBadRequest();
        });

    //Invalid with null
    webTestClient
        .put()
        .uri("/api/meals/" + VALID_MEAL_ID + "/insertFood/" + VALID_INSERTED_FOOD_ID)
        .header(authHeader.getName(), authHeader.getValues().get(0))
        .bodyValue(createInsertedFood(
            Credentials.VALID_MEAL_NAME.getValue(),
            new CalorieView(null, Credentials.VALID_FOOD_MEASURE_UNIT.getValue()),
            createValidServingView(),
            createValidFoodInfoView(),
            List.of(),
            List.of()
        ))
        .exchange()
        .expectStatus().isBadRequest();
  }

  @Test
  void givenValidAuthAndFullUserDetailsAndValidMealIdAndValidFoodIdAndInvalidServing_whenTestingChangeFood_thenServerMustReturnBadRequest() {

    Header authHeader = setUpUserWithFullUserDetailsAndReturnAuthHeader();
    String VALID_MEAL_ID = obtainValidMealId(authHeader);
    String VALID_INSERTED_FOOD_ID = obtainValidInsertedFoodId(authHeader, VALID_MEAL_ID);

    getInvalidServings()
        .forEach(INVALID_SERVING -> {
          webTestClient
              .put()
              .uri("/api/meals/" + VALID_MEAL_ID + "/insertFood/" + VALID_INSERTED_FOOD_ID)
              .header(authHeader.getName(), authHeader.getValues().get(0))
              .bodyValue(createInsertedFood(
                  Credentials.VALID_MEAL_NAME.getValue(),
                  createValidCalorieView(),
                  INVALID_SERVING,
                  createValidFoodInfoView(),
                  List.of(),
                  List.of()
              ))
              .exchange()
              .expectStatus().isBadRequest();
        });

    webTestClient
        .put()
        .uri("/api/meals/" + VALID_MEAL_ID + "/insertFood/" + VALID_INSERTED_FOOD_ID)
        .header(authHeader.getName(), authHeader.getValues().get(0))
        .bodyValue(createInsertedFood(
            Credentials.VALID_MEAL_NAME.getValue(),
            createValidCalorieView(),
            null,
            createValidFoodInfoView(),
            List.of(),
            List.of()
        ))
        .exchange()
        .expectStatus().isBadRequest();
  }

  @Test
  void givenValidAuthAndFullUserDetailsAndValidMealIdAndValidFoodIdAndInvalidListServings_whenTestingChangeFood_thenServerMustReturnBadRequest() {

    Header authHeader = setUpUserWithFullUserDetailsAndReturnAuthHeader();
    String VALID_MEAL_ID = obtainValidMealId(authHeader);
    String VALID_INSERTED_FOOD_ID = obtainValidInsertedFoodId(authHeader, VALID_MEAL_ID);

    getInvalidServings()
        .forEach(INVALID_SERVING -> {
          webTestClient
              .put()
              .uri("/api/meals/" + VALID_MEAL_ID + "/insertFood/" + VALID_INSERTED_FOOD_ID)
              .header(authHeader.getName(), authHeader.getValues().get(0))
              .bodyValue(createInsertedFood(
                  Credentials.VALID_MEAL_NAME.getValue(),
                  createValidCalorieView(),
                  createValidServingView(),
                  createValidFoodInfoView(),
                  List.of(INVALID_SERVING),
                  List.of()
              ))
              .exchange()
              .expectStatus().isBadRequest();
        });

    webTestClient
        .put()
        .uri("/api/meals/" + VALID_MEAL_ID + "/insertFood/" + VALID_INSERTED_FOOD_ID)
        .header(authHeader.getName(), authHeader.getValues().get(0))
        .bodyValue(createInsertedFood(
            Credentials.VALID_MEAL_NAME.getValue(),
            createValidCalorieView(),
            createValidServingView(),
            createValidFoodInfoView(),
            null,
            List.of()
        ))
        .exchange()
        .expectStatus().isBadRequest();
  }

  @Test
  void givenValidAuthAndFullUserDetailsAndValidMealIdAndInvalidFoodInfo_whenTestingChangeFood_thenServerMustReturnBadRequest() {

    Header authHeader = setUpUserWithFullUserDetailsAndReturnAuthHeader();
    String VALID_MEAL_ID = obtainValidMealId(authHeader);
    String VALID_INSERTED_FOOD_ID = obtainValidInsertedFoodId(authHeader, VALID_MEAL_ID);

    getInvalidFoodInfoViews()
        .forEach(INVALID_FOOD_INFO -> {
          webTestClient
              .put()
              .uri("/api/meals/" + VALID_MEAL_ID + "/insertFood/" + VALID_INSERTED_FOOD_ID)
              .header(authHeader.getName(), authHeader.getValues().get(0))
              .bodyValue(createInsertedFood(
                  Credentials.VALID_MEAL_NAME.getValue(),
                  createValidCalorieView(),
                  createValidServingView(),
                  INVALID_FOOD_INFO,
                  List.of(),
                  List.of()
              ))
              .exchange()
              .expectStatus().isBadRequest();
        });
  }

  @Test
  void givenValidAuthAndFullUserDetailsAndValidMealIdAndNullFoodInfo_whenTestingChangeFood_thenServerMustReturnCreated() {

    Header authHeader = setUpUserWithFullUserDetailsAndReturnAuthHeader();
    String VALID_MEAL_ID = obtainValidMealId(authHeader);
    String VALID_INSERTED_FOOD_ID = obtainValidInsertedFoodId(authHeader, VALID_MEAL_ID);

    webTestClient
        .put()
        .uri("/api/meals/" + VALID_MEAL_ID + "/insertFood/" + VALID_INSERTED_FOOD_ID)
        .header(authHeader.getName(), authHeader.getValues().get(0))
        .bodyValue(createInsertedFood(
            Credentials.VALID_MEAL_NAME.getValue(),
            createValidCalorieView(),
            createValidServingView(),
            null,
            List.of(),
            List.of()
        ))
        .exchange()
        .expectStatus().isOk();
  }

  @Test
  void givenValidAuthAndFullUserDetailsAndValidMealIdAndValidFoodIdAndNullNutrients_whenTestingChangeFood_thenServerMustReturnBadRequest() {

    Header authHeader = setUpUserWithFullUserDetailsAndReturnAuthHeader();
    String VALID_MEAL_ID = obtainValidMealId(authHeader);
    String VALID_INSERTED_FOOD_ID = obtainValidInsertedFoodId(authHeader, VALID_MEAL_ID);

    webTestClient
        .put()
        .uri("/api/meals/" + VALID_MEAL_ID + "/insertFood/" + VALID_INSERTED_FOOD_ID)
        .header(authHeader.getName(), authHeader.getValues().get(0))
        .bodyValue(createInsertedFood(
            Credentials.VALID_MEAL_NAME.getValue(),
            createValidCalorieView(),
            createValidServingView(),
            createValidFoodInfoView(),
            List.of(),
            null
        ))
        .exchange()
        .expectStatus().isBadRequest();
  }

  @Test
  void givenValidAuthAndFullUserDetailsAndValidMealIdAndValidFoodIdAndValidBody_whenTestingChangeFood_thenServerMustReturnOk() {

    Header authHeader = setUpUserWithFullUserDetailsAndReturnAuthHeader();
    String VALID_MEAL_ID = obtainValidMealId(authHeader);
    String VALID_INSERTED_FOOD_ID = obtainValidInsertedFoodId(authHeader, VALID_MEAL_ID);

    webTestClient
        .put()
        .uri("/api/meals/" + VALID_MEAL_ID + "/insertFood/" + VALID_INSERTED_FOOD_ID)
        .header(authHeader.getName(), authHeader.getValues().get(0))
        .bodyValue(createValidInsertedFoodWithEmptyNutritions())
        .exchange()
        .expectStatus().isOk();
  }

  @Test
  void givenValidAuthAndFullUserDetailsAndValidMealIdAndValidFoodIdAndInvalidNutrientViews_whenTestingChangeFood_thenServerMustReturnBadRequest() {

    Header authHeader = setUpUserWithFullUserDetailsAndReturnAuthHeader();
    String VALID_MEAL_ID = obtainValidMealId(authHeader);
    String VALID_INSERTED_FOOD_ID = obtainValidInsertedFoodId(authHeader, VALID_MEAL_ID);

    getInvalidNutrientViews()
        .forEach(INVALID_NUTRIENT_VIEW -> {
          webTestClient
              .put()
              .uri("/api/meals/" + VALID_MEAL_ID + "/insertFood/" + VALID_INSERTED_FOOD_ID)
              .header(authHeader.getName(), authHeader.getValues().get(0))
              .bodyValue(createInsertedFood(
                  Credentials.VALID_MEAL_NAME.getValue(),
                  createValidCalorieView(),
                  createValidServingView(),
                  createValidFoodInfoView(),
                  List.of(),
                  List.of(INVALID_NUTRIENT_VIEW)
              ))
              .exchange()
              .expectStatus().isBadRequest();
        });

    webTestClient
        .put()
        .uri("/api/meals/" + VALID_MEAL_ID + "/insertFood/" + VALID_INSERTED_FOOD_ID)
        .header(authHeader.getName(), authHeader.getValues().get(0))
        .bodyValue(createInsertedFood(
            Credentials.VALID_MEAL_NAME.getValue(),
            createValidCalorieView(),
            createValidServingView(),
            createValidFoodInfoView(),
            List.of(),
            null
        ))
        .exchange()
        .expectStatus().isBadRequest();
  }

  @Test
  void givenValidAuthAndFullUserDetailsAndValidMealIdAndValidBody_whenTestingChangeFoodWithAllPossibleNutrientViews_thenServerMustReturnOk() {

    Header authHeader = setUpUserWithFullUserDetailsAndReturnAuthHeader();
    String VALID_MEAL_ID = obtainValidMealId(authHeader);
    String VALID_INSERTED_FOOD_ID = obtainValidInsertedFoodId(authHeader, VALID_MEAL_ID);

    webTestClient
        .put()
        .uri("/api/meals/" + VALID_MEAL_ID + "/insertFood/" + VALID_INSERTED_FOOD_ID)
        .header(authHeader.getName(), authHeader.getValues().get(0))
        .bodyValue(createValidInsertedFoodWithEveryPossibleNutrientView())
        .exchange()
        .expectStatus().isOk();
  }

  @Test
  void givenValidAuthAndFullUserDetailsAndValidMealIdAndValidBody_whenTestingChangeFoodWith_thenServerMustReturnOkAndRemovePreviousFood() {

    Header authHeader = setUpUserWithFullUserDetailsAndReturnAuthHeader();
    String VALID_MEAL_ID = obtainValidMealId(authHeader);
    String VALID_INSERTED_FOOD_ID = obtainValidInsertedFoodId(authHeader, VALID_MEAL_ID);

    webTestClient
        .put()
        .uri("/api/meals/" + VALID_MEAL_ID + "/insertFood/" + VALID_INSERTED_FOOD_ID)
        .header(authHeader.getName(), authHeader.getValues().get(0))
        .bodyValue(createValidInsertedFoodWithEveryPossibleNutrientView())
        .exchange()
        .expectStatus().isOk();

    webTestClient
        .get()
        .uri("/api/meals/" + VALID_MEAL_ID)
        .header(authHeader.getName(), authHeader.getValues().get(0))
        .exchange()
        .expectStatus().isOk()
        .expectBody(MealView.class)
        .value(meal -> assertEquals(1, meal.foods().size()))
        .value(meal -> assertNotEquals(VALID_INSERTED_FOOD_ID, meal.foods().get(0).id()));

    webTestClient
        .put()
        .uri("/api/meals/" + VALID_MEAL_ID + "/insertFood/" + VALID_INSERTED_FOOD_ID)
        .header(authHeader.getName(), authHeader.getValues().get(0))
        .bodyValue(createValidInsertedFoodWithEveryPossibleNutrientView())
        .exchange()
        .expectStatus().isBadRequest();
  }

  @Test
  void givenValidAuthAndFullUserDetailsAndValidMealIdAndValidBody_whenTestingChangeFood_thenServerMustReturnOkAndRemovePreviousFoodAndTheCurrentFoodShouldMatch() {

    Header authHeader = setUpUserWithFullUserDetailsAndReturnAuthHeader();
    String VALID_MEAL_ID = obtainValidMealId(authHeader);
    String VALID_INSERTED_FOOD_ID = obtainValidInsertedFoodId(authHeader, VALID_MEAL_ID);

    InsertFoodDto foodBody = createValidInsertedFoodWithEveryPossibleNutrientView();

    webTestClient
        .put()
        .uri("/api/meals/" + VALID_MEAL_ID + "/insertFood/" + VALID_INSERTED_FOOD_ID)
        .header(authHeader.getName(), authHeader.getValues().get(0))
        .bodyValue(foodBody)
        .exchange()
        .expectStatus().isOk();

    webTestClient
        .get()
        .uri("/api/meals/" + VALID_MEAL_ID)
        .header(authHeader.getName(), authHeader.getValues().get(0))
        .exchange()
        .expectStatus().isOk()
        .expectBody(MealView.class)
        .value(meal -> assertEquals(1, meal.foods().size()))
        .value(meal -> assertNotEquals(VALID_INSERTED_FOOD_ID, meal.foods().get(0).id()))
        .value(meal -> assertEquals(foodBody.name(), meal.foods().get(0).name()))
//        .value(meal -> assertEquals(foodBody.measurement(), meal.foods().get(0).measurement()))
        .value(meal -> assertEquals(foodBody.calories().unit(), meal.foods().get(0).calorie().unit()))
        .value(meal -> assertEquals(0, foodBody.calories().amount().compareTo(meal.foods().get(0).calorie().amount())))
        .value(meal -> {
          Map<String, NutritionView> map = foodBody.nutrients()
              .stream()
              .collect(Collectors.toMap(NutritionView::name, data -> data));

          meal.foods()
              .stream()
              .map(FoodView::nutritionList)
              .flatMap(Collection::stream)
              .forEach(nutrient -> {
                assertEquals(0, map.get(nutrient.name()).amount().compareTo(nutrient.amount()));
              });
        });
//        .value(meal -> assertEquals(0, foodBody.s().compareTo(meal.foods().get(0).size())));
  }

  @Test
  void givenNoAuth_whenTestingDeleteFoodFromMeal_thenServerMustReturnUnauthorized() {

    String MEAL_ID = UUID.randomUUID().toString();
    String FOOD_ID = UUID.randomUUID().toString();

    webTestClient
        .delete()
        .uri("/api/meals/" + MEAL_ID + "/insertFood/" + FOOD_ID)
        .exchange()
        .expectStatus().isUnauthorized();
  }

  @Test
  void givenValidAuthAndNotFullUserDetails_whenTestingDeleteFoodFromMeal_thenServerMustReturnForbidden() {

    Header authHeader = setUpUserAndReturnAuthHeader();
    String MEAL_ID = UUID.randomUUID().toString();
    String FOOD_ID = UUID.randomUUID().toString();

    webTestClient
        .delete()
        .uri("/api/meals/" + MEAL_ID + "/insertFood/" + FOOD_ID)
        .header(authHeader.getName(), authHeader.getValues().get(0))
        .exchange()
        .expectStatus().isForbidden();
  }

  @Test
  void givenValidAuthAndFullUserDetailsAndInvalidMealId_whenTestingDeleteFoodFromMeal_thenServerMustReturnBadRequest() {

    Header authHeader = setUpUserWithFullUserDetailsAndReturnAuthHeader();
    String INVALID_MEAL_ID = UUID.randomUUID().toString();
    String FOOD_ID = UUID.randomUUID().toString();

    webTestClient
        .delete()
        .uri("/api/meals/" + INVALID_MEAL_ID + "/insertFood/" + FOOD_ID)
        .header(authHeader.getName(), authHeader.getValues().get(0))
        .exchange()
        .expectStatus().isBadRequest();
  }

  @Test
  void givenValidAuthAndFullUserDetailsAndValidMealIdAndInvalidFoodId_whenTestingDeleteFoodFromMeal_thenServerMustReturnBadRequest() {

    Header authHeader = setUpUserWithFullUserDetailsAndReturnAuthHeader();
    String VALID_MEAL_ID = obtainValidMealId(authHeader);
    String INVALID_FOOD_ID = UUID.randomUUID().toString();

    webTestClient
        .delete()
        .uri("/api/meals/" + VALID_MEAL_ID + "/insertFood/" + INVALID_FOOD_ID)
        .header(authHeader.getName(), authHeader.getValues().get(0))
        .exchange()
        .expectStatus().isBadRequest();
  }

  @Test
  void givenValidAuthAndFullUserDetailsAndValidMealIdAndValidFoodId_whenTestingDeleteFoodFromMeal_thenServerMustReturnNoContent() {

    Header authHeader = setUpUserWithFullUserDetailsAndReturnAuthHeader();
    String VALID_MEAL_ID = obtainValidMealId(authHeader);
    String VALID_INSERTED_FOOD_ID = obtainValidInsertedFoodId(authHeader, VALID_MEAL_ID);

    webTestClient
        .delete()
        .uri("/api/meals/" + VALID_MEAL_ID + "/insertFood/" + VALID_INSERTED_FOOD_ID)
        .header(authHeader.getName(), authHeader.getValues().get(0))
        .exchange()
        .expectStatus().isNoContent();
  }

  @Test
  void givenValidAuthAndFullUserDetailsAndValidMealIdAndValidFoodId_whenTestingDeleteFoodFromMealMultipleTimes_thenServerMustReturnNoContentAndBadRequests() {

    Header authHeader = setUpUserWithFullUserDetailsAndReturnAuthHeader();
    String VALID_MEAL_ID = obtainValidMealId(authHeader);
    String VALID_INSERTED_FOOD_ID = obtainValidInsertedFoodId(authHeader, VALID_MEAL_ID);

    webTestClient
        .delete()
        .uri("/api/meals/" + VALID_MEAL_ID + "/insertFood/" + VALID_INSERTED_FOOD_ID)
        .header(authHeader.getName(), authHeader.getValues().get(0))
        .exchange()
        .expectStatus().isNoContent();

    webTestClient
        .delete()
        .uri("/api/meals/" + VALID_MEAL_ID + "/insertFood/" + VALID_INSERTED_FOOD_ID)
        .header(authHeader.getName(), authHeader.getValues().get(0))
        .exchange()
        .expectStatus().isBadRequest();

    webTestClient
        .delete()
        .uri("/api/meals/" + VALID_MEAL_ID + "/insertFood/" + VALID_INSERTED_FOOD_ID)
        .header(authHeader.getName(), authHeader.getValues().get(0))
        .exchange()
        .expectStatus().isBadRequest();
  }

  @Test
  void givenValidAuthAndFullUserDetailsAndValidMealIdAndValidFoodId_whenTestingDeleteFoodFromMeal_thenServerMustReturnNoContentAndRemoveTheFood() {

    Header authHeader = setUpUserWithFullUserDetailsAndReturnAuthHeader();
    String VALID_MEAL_ID = obtainValidMealId(authHeader);
    String VALID_INSERTED_FOOD_ID = obtainValidInsertedFoodId(authHeader, VALID_MEAL_ID);

    webTestClient
        .delete()
        .uri("/api/meals/" + VALID_MEAL_ID + "/insertFood/" + VALID_INSERTED_FOOD_ID)
        .header(authHeader.getName(), authHeader.getValues().get(0))
        .exchange()
        .expectStatus().isNoContent();

    webTestClient
        .get()
        .uri("/api/meals/" + VALID_MEAL_ID)
        .header(authHeader.getName(), authHeader.getValues().get(0))
        .exchange()
        .expectStatus().isOk()
        .expectBody(MealView.class)
        .value(meal -> assertEquals(0, meal.foods().size()));
  }


  private UserDetailsDto createDetails(BigDecimal kilograms, BigDecimal height, Integer age, WorkoutState workoutState, Gender gender) {
    return new UserDetailsDto(kilograms, height, age, workoutState, gender);
  }

  private Header setUpUserAndReturnAuthHeader() {
    String token = jwtUtilEmailValidation.generateToken(Credentials.VALID_EMAIL.getValue());

    UserCreate newUser = createUser(
        Credentials.VALID_USERNAME.getValue(),
        Credentials.VALID_EMAIL.getValue(),
        Credentials.VALID_PASSWORD.getValue(),
        token
    );

    JwtResponse responseBody = webTestClient.post()
        .uri("/api/user")
        .bodyValue(newUser)
        .exchange()
        .expectStatus().isCreated()
        .expectBody(JwtResponse.class)
        .returnResult()
        .getResponseBody();

    assert responseBody != null;
    return new Header("Authorization", "Bearer " + responseBody.accessToken().value());
  }

  private Header setUpUserWithFullUserDetailsAndReturnAuthHeader() {

    Header header = setUpUserAndReturnAuthHeader();

    UserDetailsDto validDetails = createDetails(
        new BigDecimal(Credentials.VALID_DETAIL_KILOGRAMS.getValue()),
        new BigDecimal(Credentials.VALID_DETAIL_HEIGHT.getValue()),
        Integer.valueOf(Credentials.VALID_DETAIL_AGE.getValue()),
        WorkoutState.valueOf(Credentials.VALID_DETAIL_WORKOUT.getValue()),
        Gender.valueOf(Credentials.VALID_DETAIL_GENDER.getValue())
    );

    JwtResponse responseBody = webTestClient.patch()
        .uri("/api/user/details")
        .header(header.getName(), header.getValues().get(0))
        .bodyValue(validDetails)
        .exchange()
        .expectStatus().isOk()
        .expectBody(JwtResponse.class)
        .returnResult()
        .getResponseBody();

    assert responseBody != null;
    return new Header("Authorization", "Bearer " + responseBody.accessToken().value());
  }

  private UserCreate createUser(String username, String email, String password, String token) {
    return new UserCreate(username, email, password, token);
  }

  private String obtainValidMealId(Header authHeader) {
    return Objects.requireNonNull(webTestClient
            .post()
            .uri("/api/meals")
            .header(authHeader.getName(), authHeader.getValues().get(0))
            .bodyValue(new CreateMeal(null))
            .exchange()
            .expectStatus().isCreated()
            .expectBody(MealView.class)
            .returnResult()
            .getResponseBody())
        .id();
  }

  private String obtainValidInsertedFoodId(Header authHeader, String VALID_MEAL_ID) {

    webTestClient
        .post()
        .uri("/api/meals/" + VALID_MEAL_ID + "/insertFood")
        .header(authHeader.getName(), authHeader.getValues().get(0))
        .bodyValue(createValidInsertedFoodWithEmptyNutritions())
        .exchange()
        .expectStatus().isOk()
        .expectBody(Void.class)
        .returnResult();

    return Objects.requireNonNull(
            webTestClient
                .get()
                .uri("/api/meals/" + VALID_MEAL_ID)
                .header(authHeader.getName(), authHeader.getValues().get(0))
                .exchange()
                .expectStatus().isOk()
                .expectBody(MealView.class)
                .returnResult()
                .getResponseBody()
        ).foods()
        .get(0)
        .id();
  }


}