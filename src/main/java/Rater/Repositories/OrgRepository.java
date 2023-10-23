package Rater.Repositories;

import Rater.Models.Org.Org;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrgRepository extends JpaRepository<Org, UUID> {
    Optional<Org> findByName(String name);
    boolean existsByName(String name);
}
