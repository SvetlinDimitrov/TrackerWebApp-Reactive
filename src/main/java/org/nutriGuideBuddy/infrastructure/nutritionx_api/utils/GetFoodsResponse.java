package org.nutriGuideBuddy.infrastructure.nutritionx_api.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nutriGuideBuddy.infrastructure.nutritionx_api.dto.FoodItem;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetFoodsResponse {

  private List<FoodItem> foods;
}
