package dev.esanchez.timely.backend.module.identity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    boolean existsById(UserRoleId userRoleId);
    List<UserRole> findAllByUser(User user);

}
