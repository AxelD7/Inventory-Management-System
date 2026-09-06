package com.axeld7.inventory_management_system.auth.dto;

import com.axeld7.inventory_management_system.common.UserSummaryDTO;

public record LoginResponseDTO(String accessToken, UserSummaryDTO user) {}
