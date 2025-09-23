package org.nutriGuideBuddy.infrastructure.nutritionx_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.List;

public record FoodItemResponse(
    @JsonProperty("food_name") String foodName,
    @JsonProperty("brand_name") String brandName,
    @JsonProperty("serving_qty") Double servingQty,
    @JsonProperty("serving_unit") String servingUnit,
    @JsonProperty("serving_weight_grams") Double servingWeightGrams,
    @JsonProperty("nf_metric_qty") Double nfMetricQty,
    @JsonProperty("nf_metric_uom") String nfMetricUom,
    @JsonProperty("nf_calories") Double nfCalories,
    @JsonProperty("nf_total_fat") Double nfTotalFat,
    @JsonProperty("nf_saturated_fat") Double nfSaturatedFat,
    @JsonProperty("nf_cholesterol") Double nfCholesterol,
    @JsonProperty("nf_sodium") Double nfSodium,
    @JsonProperty("nf_total_carbohydrate") Double nfTotalCarbohydrate,
    @JsonProperty("nf_dietary_fiber") Double nfDietaryFiber,
    @JsonProperty("nf_sugars") Double nfSugars,
    @JsonProperty("nf_protein") Double nfProtein,
    @JsonProperty("nf_potassium") Double nfPotassium,
    @JsonProperty("full_nutrients") List<FullNutrientResponse> fullNutrients,
    @JsonProperty("nix_item_id") String itemId,
    Photo photo,
    Tags tags,
    @JsonProperty("nf_ingredient_statement") String nfIngredientStatement,
    @JsonProperty("alt_measures") List<AltMeasuresResponse> measures) {

  public record Photo(String thumb) {}

  public record Tags(
      String item,
      String measure,
      @JsonProperty("food_group") String foodGroup,
      @JsonProperty("tag_id") String tagId) {}

  public record FullNutrientResponse(
      @JsonProperty("attr_id") Integer tag, @JsonProperty("value") BigDecimal value) {}

  public record AltMeasuresResponse(
      @JsonProperty("serving_weight") Double servingWeight,
      String measure,
      Double seq,
      Double qty) {}
}
