package com.axeld7.inventory_management_system.asset;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.axeld7.inventory_management_system.asset.dto.CheckinRequestDTO;
import com.axeld7.inventory_management_system.asset.dto.CheckoutRequestDTO;
import com.axeld7.inventory_management_system.user.User;
import com.axeld7.inventory_management_system.user.UserRepository;
import com.axeld7.inventory_management_system.user.UserRoles;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.*;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
public class AssetCirculationIntegrationTest {

  @Container @ServiceConnection
  static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine");

  @Autowired private MockMvc mockMvc;
  @Autowired private UserRepository userRepository;
  @Autowired private AssetRepository assetRepository;
  @Autowired private AssetCirculationRepository circulationRepository;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setUp() {
    circulationRepository.deleteAllInBatch();
    assetRepository.deleteAllInBatch();
    userRepository.deleteAllInBatch();

    User employee = new User();
    employee.setEmail("jackemployee@gmail.com");
    employee.setFirstName("Jack");
    employee.setLastName("Employee");
    employee.setPasswordHash("password");
    employee.setRole(UserRoles.EMPLOYEE);
    userRepository.save(employee);

    User admin = new User();
    admin.setEmail("jackadmin@gmail.com");
    admin.setFirstName("Jack");
    admin.setLastName("ADMIN");
    admin.setPasswordHash("password");
    admin.setRole(UserRoles.ADMIN);
    userRepository.save(admin);

    User borrower = new User();
    borrower.setEmail("joeborrower@gmail.com");
    borrower.setFirstName("Joe");
    borrower.setLastName("Borrower");
    borrower.setPasswordHash("password");
    borrower.setRole(UserRoles.USER);
    userRepository.save(borrower);
  }

  @Test
  void testAssetCheckout_Success() throws Exception {

    User admin = userRepository.findByEmailIgnoreCase("jackadmin@gmail.com");
    User employee = userRepository.findByEmailIgnoreCase("jackemployee@gmail.com");
    User borrower = userRepository.findByEmailIgnoreCase("joeborrower@gmail.com");

    Asset asset = new Asset();
    asset.setAssetTag("LAPTOP-001");
    asset.setName("MacBook Pro");
    asset.setStatus(AssetStatus.AVAILABLE);
    asset.setCreatedBy(admin);
    asset.setUpdatedBy(admin);
    asset = assetRepository.save(asset);

    CheckoutRequestDTO request = new CheckoutRequestDTO(borrower.getId(), "P7d");

    UsernamePasswordAuthenticationToken auth =
        new UsernamePasswordAuthenticationToken(employee, null, employee.getAuthorities());

    mockMvc
        .perform(
            post("/api/v1/assets/{id}/checkout", asset.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(authentication(auth)))
        .andExpectAll(
            status().isOk(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$.asset.id").value(asset.getId()),
            jsonPath("$.borrower.id").value(borrower.getId()),
            jsonPath("$.checkedOutBy.id").value(employee.getId()),
            jsonPath("$.checkedInBy.id").doesNotExist(),
            jsonPath("$.status").value(CirculationStatus.ACTIVE.toString()));
  }

  @Test
  void testAssetCheckIn_Success() throws Exception {

    User admin = userRepository.findByEmailIgnoreCase("jackadmin@gmail.com");
    User employee = userRepository.findByEmailIgnoreCase("jackemployee@gmail.com");
    User borrower = userRepository.findByEmailIgnoreCase("joeborrower@gmail.com");

    Asset asset = new Asset();
    asset.setAssetTag("LAPTOP-002");
    asset.setName("Dell XPS");
    asset.setStatus(AssetStatus.UNAVAILABLE);
    asset.setCreatedBy(admin);
    asset.setUpdatedBy(admin);
    asset = assetRepository.save(asset);

    AssetCirculation activeCirculation = new AssetCirculation();
    activeCirculation.setAsset(asset);
    activeCirculation.setBorrower(borrower);
    activeCirculation.setCheckedOutBy(employee);
    activeCirculation.setDueDate(Instant.now().plus(Duration.ofDays(7)));
    activeCirculation.setStatus(CirculationStatus.ACTIVE);
    circulationRepository.save(activeCirculation);

    UsernamePasswordAuthenticationToken auth =
        new UsernamePasswordAuthenticationToken(employee, null, employee.getAuthorities());

    CheckinRequestDTO request = new CheckinRequestDTO(false, "the device is all good");

    mockMvc
        .perform(
            post("/api/v1/assets/{id}/checkin", asset.getId())
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(authentication(auth)))
        .andExpectAll(
            status().isOk(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$.asset.id").value(asset.getId()),
            jsonPath("$.checkedInBy.id").value(employee.getId()),
            jsonPath("isDamaged").value(request.isDamaged()),
            jsonPath("notes").value(request.notes()),
            jsonPath("$.status").value(CirculationStatus.RETURNED.toString()));
    Asset updatedAsset = assetRepository.findById(asset.getId()).orElseThrow();
    assertThat("", updatedAsset.getStatus().equals(AssetStatus.AVAILABLE));
  }
}
