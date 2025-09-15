package org.nutriGuideBuddy.features.meal.dto;

import org.nutriGuideBuddy.features.meal.entity.Meal;
import org.nutriGuideBuddy.features.shared.annotaions.ValidSortFields;
import org.nutriGuideBuddy.features.shared.dto.CustomPageable;

@ValidSortFields(entity = Meal.class)
public class CustomPageableMeal extends CustomPageable {}
