package com.axeld7.inventory_management_system.common;

import com.axeld7.inventory_management_system.user.User;

public record UserSummaryDTO(Long id, String email, String firstName, String lastName) {
  public static UserSummaryDTO toUserSummaryDTO(Object userRef) {
    if (userRef == null) {
      return null;
    }

    if (userRef instanceof User user) {
      return new UserSummaryDTO(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName());
    }

    if (userRef instanceof Number number) {
      return new UserSummaryDTO(number.longValue(), null, null, null);
    }

    return null;
  }
}
