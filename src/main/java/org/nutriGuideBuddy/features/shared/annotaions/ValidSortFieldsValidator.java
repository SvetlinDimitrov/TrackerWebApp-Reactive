package org.nutriGuideBuddy.features.shared.annotaions;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.reflect.FieldUtils;
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
    Set<String> validFields = getValidFields(entityClass);

    validFields.removeAll(excludedFields);

    for (String sortField : pageable.getSort().keySet()) {
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

  private Set<String> getValidFields(Class<?> entityClass) {
    Set<String> fields = new HashSet<>();
    for (Field field : FieldUtils.getAllFields(entityClass)) {
      if (field.isAnnotationPresent(Column.class)) {
        fields.add(field.getAnnotation(Column.class).value());
      }
    }
    fields.add("id");

    return fields;
  }
}
