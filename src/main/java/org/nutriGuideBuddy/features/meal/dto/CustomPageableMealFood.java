package org.nutriGuideBuddy.features.meal.dto;

import org.nutriGuideBuddy.features.meal.entity.MealFood;
import org.nutriGuideBuddy.features.shared.annotaions.ValidSortFields;
import org.nutriGuideBuddy.features.shared.dto.CustomPageable;

@ValidSortFields(entity = MealFood.class, excludeFields = "meal_id")
public class CustomPageableMealFood extends CustomPageable {}
