package org.nutriGuideBuddy.features.user.dto;

import org.nutriGuideBuddy.features.shared.annotaions.ValidSortFields;
import org.nutriGuideBuddy.features.shared.dto.CustomPageable;
import org.nutriGuideBuddy.features.user.entity.User;

@ValidSortFields(
    entity = User.class,
    excludeFields = {"user_id"})
public class CustomPageableUserDetailsSnapshot extends CustomPageable {}
