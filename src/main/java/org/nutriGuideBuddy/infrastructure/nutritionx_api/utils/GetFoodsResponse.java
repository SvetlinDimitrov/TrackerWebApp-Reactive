package org.nutriGuideBuddy.infrastructure.nutritionx_api.utils;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nutriGuideBuddy.infrastructure.nutritionx_api.dto.FoodItem;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetFoodsResponse {

  private List<FoodItem> foods;
}
