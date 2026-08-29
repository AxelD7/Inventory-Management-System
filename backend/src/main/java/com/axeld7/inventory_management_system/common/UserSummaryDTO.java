package com.axeld7.inventory_management_system.common;

import com.axeld7.inventory_management_system.user.User;
import com.axeld7.inventory_management_system.user.UserRoles;

public record UserSummaryDTO(
    Long id, String email, String firstName, String lastName, UserRoles role) {
  public static UserSummaryDTO toUserSummaryDTO(Object userRef) {
    if (userRef == null) {
      return null;
    }

    if (userRef instanceof User user) {
      return new UserSummaryDTO(
          user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(), user.getRole());
    }

    if (userRef instanceof Number number) {
      return new UserSummaryDTO(number.longValue(), null, null, null, null);
    }

    return null;
  }
}
