package org.nutriGuideBuddy.features.shared.enums;

import lombok.Getter;

@Getter
public enum AllowedNutrients {
  CARBOHYDRATE("Carbohydrate", "g"),
  PROTEIN("Protein", "g"),
  FAT("Fat", "g"),
  FIBER("Fiber", "g"),
  SUGAR("Sugar", "g"),
  OMEGA6("Linoleic Acid", "g"),
  OMEGA3("α-Linolenic Acid", "g"),
  CHOLESTEROL("Cholesterol", "mg"),
  WATER("Water", "L"),
  SATURATED_FAT("Saturated Fat", "g"),
  TRANS_FAT("Trans Fat", "g"),

  VITAMIN_A("Vitamin A", "µg"),
  VITAMIN_D_D2_D3("Vitamin D (D2 + D3)", "µg"),
  VITAMIN_E("Vitamin E", "mg"),
  VITAMIN_K("Vitamin K", "µg"),
  VITAMIN_C("Vitamin C", "mg"),
  VITAMIN_B1_THIAMIN("Vitamin B1 (Thiamin)", "mg"),
  VITAMIN_B2_RIBOFLAVIN("Vitamin B2 (Riboflavin)", "mg"),
  VITAMIN_B3_NIACIN("Vitamin B3 (Niacin)", "mg"),
  VITAMIN_B5_PANTOTHENIC_ACID("Vitamin B5 (Pantothenic acid)", "mg"),
  VITAMIN_B6("Vitamin B6", "mg"),
  VITAMIN_B7_BIOTIN("Vitamin B7 (Biotin)", "µg"),
  VITAMIN_B9_FOLATE("Vitamin B9 (Folate)", "µg"),
  VITAMIN_B12("Vitamin B12", "µg"),
  CHOLINE("Choline", "mg"),

  CALCIUM_CA("Calcium , Ca", "mg"),
  CHROMIUM_CR("Chromium , Cr", "μg"),
  PHOSPHORUS_P("Phosphorus , P", "mg"),
  FLUORIDE("Fluoride", "mg"),
  CHLORIDE("Chloride", "g"),
  MAGNESIUM_MG("Magnesium , Mg", "mg"),
  SODIUM_NA("Sodium , Na", "mg"),
  POTASSIUM_K("Potassium , K", "mg"),
  IRON_FE("Iron , Fe", "mg"),
  ZINC_ZN("Zinc , Zn", "mg"),
  COPPER_CU("Copper , Cu", "μg"),
  MANGANESE_MN("Manganese , Mn", "mg"),
  IODINE_I("Iodine , I", "µg"),
  SELENIUM_SE("Selenium , Se", "µg"),
  MOLYBDENUM_MO("Molybdenum , Mo", "µg");

  private final String nutrientName;
  private final String nutrientUnit;

  AllowedNutrients(String nutrientName, String nutrientUnit) {
    this.nutrientName = nutrientName;
    this.nutrientUnit = nutrientUnit;
  }
}
