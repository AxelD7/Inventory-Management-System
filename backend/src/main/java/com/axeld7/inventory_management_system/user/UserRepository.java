package com.axeld7.inventory_management_system.user;

import com.axeld7.inventory_management_system.common.UserSummaryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  User findByEmailIgnoreCase(String email);

  boolean existsByEmailIgnoreCase(String email);

  @Query(
      "SELECT new com.axeld7.inventory_management_system.common.UserSummaryDTO(u.id,"
          + "u.email,"
          + "u.firstName,"
          + "u.lastName,"
          + "u.role) "
          + "FROM User u "
          + "WHERE LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%')) "
          + " OR LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :query, '%'))")
  Page<UserSummaryDTO> findBySearch(@Param("query") String query, Pageable pageable);
}
