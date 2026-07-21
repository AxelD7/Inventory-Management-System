package com.axeld7.inventory_management_system.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

  User findByEmail(String email);
  boolean existsByEmail(String email);
}
