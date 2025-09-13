package org.nutriGuideBuddy.features.record.dto;

import org.nutriGuideBuddy.features.record.enums.Goals;

import java.util.List;

public record CreateRecord(
    Goals goal, DistributedMacros distributedMacros, List<NutritionView> nutritions) {}
