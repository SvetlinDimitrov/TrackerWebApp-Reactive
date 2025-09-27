package org.nutriGuideBuddy.features.shared.annotaions;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.nutriGuideBuddy.features.shared.dto.CustomPageable;
import org.springframework.data.relational.core.mapping.Column;

public class ValidSortFieldsValidator
    implements ConstraintValidator<ValidSortFields, CustomPageable> {

  private Class<?> entityClass;
  private Set<String> excludedFields;

  @Override
  public void initialize(ValidSortFields constraintAnnotation) {
    this.entityClass = constraintAnnotation.entity();
    this.excludedFields = Set.of(constraintAnnotation.excludeFields());
  }

  @Override
  public boolean isValid(CustomPageable pageable, ConstraintValidatorContext context) {
    // If pageable is null or has no sort specified, it's valid.
    if (pageable == null) return true;
    Map<String, String> sort = pageable.getSort();
    if (sort == null || sort.isEmpty()) return true;

    Set<String> validFields = getValidFields(entityClass);
    validFields.removeAll(excludedFields);

    for (String sortField : sort.keySet()) {
      if (!validFields.contains(sortField)) {
        context.disableDefaultConstraintViolation();
        context
            .buildConstraintViolationWithTemplate(
                "Invalid sort field: " + sortField + ". Valid fields are: " + validFields)
            .addPropertyNode("sort.key")
            .addConstraintViolation();
        return false;
      }
    }
    return true;
  }

  private Set<String> getValidFields(Class<?> type) {
    Set<String> fields = new HashSet<>();

    Class<?> current = type;
    while (current != null && current != Object.class) {
      for (Field f : current.getDeclaredFields()) {
        Column col = f.getAnnotation(Column.class);
        if (col != null && !col.value().isBlank()) {
          fields.add(col.value());
        }
      }
      current = current.getSuperclass();
    }

    // Always allow "id" (common primary key)
    fields.add("id");
    return fields;
  }
}
