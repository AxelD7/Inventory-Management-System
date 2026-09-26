package com.axeld7.inventory_management_system.user;

import com.axeld7.inventory_management_system.asset.AssetCirculationRepository;
import com.axeld7.inventory_management_system.asset.CirculationStatus;
import com.axeld7.inventory_management_system.asset.dto.AssetSummaryDTO;
import com.axeld7.inventory_management_system.common.UserSummaryDTO;
import com.axeld7.inventory_management_system.exception.ResourceNotFoundException;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UserService {

  private UserRepository userRepository;
  private AssetCirculationRepository circulationRepository;

  @Transactional(readOnly = true)
  public Page<UserSummaryDTO> findPagedUsers(String query, Pageable pageable) {
    Page<UserSummaryDTO> pages = userRepository.findBySearch(query == null ? "" : query, pageable);
    return pages;
  }

  @Transactional(readOnly = true)
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

  @Transactional(readOnly = true)
  public List<AssetSummaryDTO> getUserCirculations(Long id, CirculationStatus status) {
    userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found."));

    List<AssetSummaryDTO> assets =
        circulationRepository.findAllByBorrowerIdAndStatus(id, status).stream()
            .map(circulation -> AssetSummaryDTO.toAssetSummaryDTO(circulation.getAsset()))
            .toList();

    return assets;
  }
}
