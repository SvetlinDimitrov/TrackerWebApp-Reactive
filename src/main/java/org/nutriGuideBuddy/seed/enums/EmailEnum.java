package org.nutriGuideBuddy.seed.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EmailEnum {
  USER1("user1@example.com"),
  USER2("user2@example.com"),
  USER3("user3@example.com"),
  USER4("user4@example.com"),
  USER5("user5@example.com"),
  USER6("user6@example.com"),
  USER7("user7@example.com"),
  USER8("user8@example.com"),
  USER9("user9@example.com"),
  USER10("user10@example.com");

  private final String email;
}
