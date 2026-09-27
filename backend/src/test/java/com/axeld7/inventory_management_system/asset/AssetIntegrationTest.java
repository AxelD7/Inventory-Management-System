package com.axeld7.inventory_management_system.asset;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.axeld7.inventory_management_system.user.User;
import com.axeld7.inventory_management_system.user.UserRepository;
import com.axeld7.inventory_management_system.user.UserRoles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.*;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
public class AssetIntegrationTest {

  @Container @ServiceConnection
  static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine");

  @Autowired private UserRepository userRepository;
  @Autowired private AssetRepository assetRepository;
  @Autowired private AssetCirculationRepository circulationRepository;

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
  void optimisticLocking_FailsOnConcurrentUpdate() {
    User testUser = userRepository.findByEmailIgnoreCase("jackadmin@gmail.com");

    Asset asset = new Asset();
    asset.setName("Dell XPS 15");
    asset.setAssetTag("LAPTOP-999");
    asset.setStatus(AssetStatus.AVAILABLE);
    asset.setCreatedBy(testUser);
    asset.setUpdatedBy(testUser);
    asset = assetRepository.saveAndFlush(asset);

    Asset assetUpdateA = assetRepository.findById(asset.getId()).orElseThrow();
    Asset assetUpdateB = assetRepository.findById(asset.getId()).orElseThrow();

    assetUpdateA.setName("Dell XPS 15 - Admin A Edit");
    assetRepository.saveAndFlush(assetUpdateA);

    assetUpdateB.setName("Dell XPS 15 - Admin B Edit");

    assertThrows(
        ObjectOptimisticLockingFailureException.class,
        () -> {
          assetRepository.saveAndFlush(assetUpdateB);
        });
  }
}
