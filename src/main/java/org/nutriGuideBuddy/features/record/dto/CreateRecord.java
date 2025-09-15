package org.nutriGuideBuddy.features.record.dto;

import java.util.List;
import org.nutriGuideBuddy.features.record.enums.Goals;

public record CreateRecord(
    Goals goal, DistributedMacros distributedMacros, List<NutritionView> nutritions) {}
