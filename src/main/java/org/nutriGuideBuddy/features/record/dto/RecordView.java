package org.nutriGuideBuddy.features.record.dto;

import java.util.List;
import lombok.Data;
import org.nutriGuideBuddy.features.shared.dto.NutritionIntakeView;

@Data
public class RecordView {

  private Double dailyCaloriesToConsume;
  private Double dailyCaloriesConsumed;
  private List<NutritionIntakeView> vitaminIntake;
  private List<NutritionIntakeView> mineralIntakes;
  private List<NutritionIntakeView> macroIntakes;
}
