package org.nutriGuideBuddy.features.user.dto;

import org.nutriGuideBuddy.features.user_details.dto.UserDetailsView;

public record UserWithDetailsView(UserView user, UserDetailsView userDetails) {}
