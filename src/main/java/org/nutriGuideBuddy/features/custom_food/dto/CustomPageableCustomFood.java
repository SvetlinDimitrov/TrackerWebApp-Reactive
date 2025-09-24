package org.nutriGuideBuddy.features.custom_food.dto;

import org.nutriGuideBuddy.features.custom_food.entity.CustomFood;
import org.nutriGuideBuddy.features.shared.annotaions.ValidSortFields;
import org.nutriGuideBuddy.features.shared.dto.CustomPageable;

@ValidSortFields(entity = CustomFood.class, excludeFields = "user_id")
public class CustomPageableCustomFood extends CustomPageable {}
