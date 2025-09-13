package org.nutriGuideBuddy.domain.dto.user;

import org.nutriGuideBuddy.domain.dto.user_details.UserDetailsView;

public record UserWithDetailsView(UserView user, UserDetailsView userDetails) {}
