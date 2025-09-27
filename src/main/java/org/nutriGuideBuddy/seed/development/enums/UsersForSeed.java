package org.nutriGuideBuddy.seed.development.enums;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.user.enums.UserRole;

@Getter
@RequiredArgsConstructor
public enum UsersForSeed {
  GOD("god@example.com", UserRole.GOD),
  USER1("user1@example.com", UserRole.USER),
  USER2("user2@example.com", UserRole.USER),
  USER3("user3@example.com", UserRole.USER),
  USER4("user4@example.com", UserRole.USER),
  USER5("user5@example.com", UserRole.USER),
  USER6("user6@example.com", UserRole.USER),
  USER7("user7@example.com", UserRole.USER),
  USER8("user8@example.com", UserRole.USER),
  USER9("user9@example.com", UserRole.USER),
  USER10("user10@example.com", UserRole.USER);

  private final String email;
  private final UserRole role;

  public static Set<UsersForSeed> allExceptGod() {
    return EnumSet.complementOf(EnumSet.of(GOD));
  }

  public static Set<String> emailsExceptGod() {
    return allExceptGod().stream().map(UsersForSeed::getEmail).collect(Collectors.toSet());
  }

  public static List<UserSeed> usersExceptGod() {
    return allExceptGod().stream().map(e -> new UserSeed(e.email, e.role)).toList();
  }

  public record UserSeed(String email, UserRole role) {}
}
