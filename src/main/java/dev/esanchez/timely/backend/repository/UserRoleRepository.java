package dev.esanchez.timely.backend.repository;

import dev.esanchez.timely.backend.entity.UserRole;
import dev.esanchez.timely.backend.entity.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    boolean existsById(UserRoleId userRoleId);
}
