package com.axeld7.inventory_management_system.user;

import com.axeld7.inventory_management_system.common.UserSummaryDTO;
import com.axeld7.inventory_management_system.exception.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

  private UserRepository userRepository;

  public Page<UserSummaryDTO> findPagedUsers(String query, Pageable pageable) {
    Page<UserSummaryDTO> pages = userRepository.findBySearch(query == null ? "" : query, pageable);
    return pages;
  }

  public UserSummaryDTO getUserSummary(Long userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found."));

    UserSummaryDTO userSummary =
        new UserSummaryDTO(
            user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(), user.getRole());

    return userSummary;
  }
}
