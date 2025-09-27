package org.nutriGuideBuddy.infrastructure.nutritionx_api.service;

import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nutriGuideBuddy.features.shared.dto.FoodCreateRequest;
import org.nutriGuideBuddy.infrastructure.exceptions.BadRequestException;
import org.nutriGuideBuddy.infrastructure.exceptions.NotFoundException;
import org.nutriGuideBuddy.infrastructure.exceptions.ServiceUnavailableException;
import org.nutriGuideBuddy.infrastructure.nutritionx_api.dto.FoodItemResponse;
import org.nutriGuideBuddy.infrastructure.nutritionx_api.dto.ListFoodsResponse;
import org.nutriGuideBuddy.infrastructure.nutritionx_api.utils.NutritionxApiFoodMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class NutritionixApiService {

  private static final int BUFFER_SIZE = 64 * 1024 * 1024;
  private static final String BASE_URL = "https://trackapi.nutritionix.com";
  private final NutritionxApiFoodMapper nutritionxApiFoodMapper;
  @Value("${api.id}")
  public String X_API_ID;
  @Value("${api.key}")
  public String X_API_KEY;
  private WebClient webClient;

  @PostConstruct
  private void init() {
    webClient =
        WebClient.builder()
            .exchangeStrategies(
                ExchangeStrategies.builder()
                    .codecs(c -> c.defaultCodecs().maxInMemorySize(BUFFER_SIZE))
                    .build())
            .baseUrl(BASE_URL)
            .defaultHeader("x-app-id", X_API_ID)
            .defaultHeader("x-app-key", X_API_KEY)
            .defaultHeader(
                org.springframework.http.HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .build();
  }

  public Mono<List<FoodCreateRequest>> getCommonFoodBySearchTerm(String query) {
    if (query.isBlank()) {
      return Mono.error(
          BadRequestException.message("Required request parameter 'term' must not be blank."));
    }
    return webClient
        .post()
        .uri(uriBuilder -> uriBuilder.path("/v2/natural/nutrients").build())
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .bodyValue(Map.of("query", query))
        .retrieve()
        .onStatus(HttpStatusCode::isError, this::handleErrorResponse)
        .bodyToMono(new ParameterizedTypeReference<Map<String, List<FoodItemResponse>>>() {})
        .map(m -> m.getOrDefault("foods", List.of()))
        .map(list -> list.stream().map(nutritionxApiFoodMapper::toCreateRequest).toList());
  }

  public Mono<List<FoodCreateRequest>> getBrandedFoodById(String id) {

    if (id == null || id.isBlank()) {
      return Mono.error(BadRequestException.of("Path variable 'id'", "must not be blank"));
    }

    return webClient
        .get()
        .uri(uriBuilder -> uriBuilder.path("/v2/search/item").queryParam("nix_item_id", id).build())
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .onStatus(HttpStatusCode::isError, this::handleErrorResponse)
        .bodyToMono(new ParameterizedTypeReference<Map<String, List<FoodItemResponse>>>() {})
        .map(m -> m.getOrDefault("foods", List.of()))
        .map(list -> list.stream().map(nutritionxApiFoodMapper::toCreateRequest).toList());
  }

  public Mono<ListFoodsResponse> getAllFoodsByFoodName(String foodName) {

    if (foodName.isBlank()) {
      return Mono.error(
          BadRequestException.message("Required request parameter 'foodName' must not be blank."));
    }

    return webClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder.path("/v2/search/instant/").queryParam("query", foodName).build())
        .acceptCharset(StandardCharsets.UTF_8)
        .retrieve()
        .onStatus(HttpStatusCode::isError, this::handleErrorResponse)
        .bodyToMono(ListFoodsResponse.class);
  }

  private Mono<? extends Throwable> handleErrorResponse(ClientResponse response) {
    return response
        .bodyToMono(String.class)
        .defaultIfEmpty("")
        .flatMap(
            body -> {
              log.error(
                  "API error. Status: {}, Content-Type: {}, Body: {}",
                  response.statusCode(),
                  response.headers().asHttpHeaders().getContentType(),
                  body);

              if (response.statusCode().value() == 404) {
                return Mono.error(NotFoundException.of("Food"));
              }

              return response
                  .createException()
                  .flatMap(
                      ex ->
                          Mono.error(
                              ServiceUnavailableException.withCause(
                                  "Nutritionix API", ex.getMessage(), ex)));
            });
  }
}
