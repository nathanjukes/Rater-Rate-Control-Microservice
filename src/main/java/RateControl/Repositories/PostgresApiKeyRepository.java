package RateControl.Repositories;

import RateControl.Models.ApiKey.ApiKeyEntity;
import RateControl.Models.Org.Org;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PostgresApiKeyRepository extends JpaRepository<ApiKeyEntity, String> {
    @Query(value = "SELECT * FROM api_keys WHERE api_key = ?1", nativeQuery = true)
    Optional<ApiKeyEntity> getByApiKey(String apiKey);

    @Query(value = "SELECT * FROM api_keys WHERE service_id = ?1", nativeQuery = true)
    Optional<ApiKeyEntity> getByServiceId(UUID serviceId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM api_keys WHERE api_key = ?1", nativeQuery = true)
    void deleteByApiKey(String apiKey);
}
