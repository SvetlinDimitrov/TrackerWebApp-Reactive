package org.nutriGuideBuddy.infrastructure.rdi.dto;

import lombok.Getter;

@Getter
public enum JsonAllowedNutrients {
  CARBOHYDRATE("g"),
  PROTEIN("g"),
  FAT("g"),
  FIBER("g"),
  SUGAR("g"),
  OMEGA6("g"),
  OMEGA3("g"),
  SATURATED_FAT("g"),
  TRANS_FAT("g"),

  VITAMIN_A("µg"),
  VITAMIN_D_D2_D3("µg"),
  VITAMIN_E("mg"),
  VITAMIN_K("µg"),
  VITAMIN_C("mg"),
  VITAMIN_B1_THIAMIN("mg"),
  VITAMIN_B2_RIBOFLAVIN("mg"),
  VITAMIN_B3_NIACIN("mg"),
  VITAMIN_B5_PANTOTHENIC_ACID("mg"),
  VITAMIN_B6("mg"),
  VITAMIN_B7_BIOTIN("µg"),
  VITAMIN_B9_FOLATE("µg"),
  VITAMIN_B12("µg"),

  CHOLINE("mg"),
  CALCIUM_CA("mg"),
  CHROMIUM_CR("µg"),
  PHOSPHORUS_P("mg"),
  FLUORIDE("mg"),
  CHLORIDE("g"),
  MAGNESIUM_MG("mg"),
  SODIUM_NA("mg"),
  POTASSIUM_K("mg"),
  IRON_FE("mg"),
  ZINC_ZN("mg"),
  COPPER_CU("µg"),
  MANGANESE_MN("mg"),
  IODINE_I("µg"),
  SELENIUM_SE("µg"),
  MOLYBDENUM_MO("µg"),

  CHOLESTEROL("mg"),
  WATER("L");
  private final String unit;

  JsonAllowedNutrients(String unit) {
    this.unit = unit;
  }
}
