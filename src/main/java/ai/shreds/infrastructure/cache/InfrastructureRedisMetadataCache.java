package ai.shreds.infrastructure.cache;

import ai.shreds.domain.entities.DomainEntityFileMetadata;
import ai.shreds.infrastructure.exceptions.InfrastructureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class InfrastructureRedisMetadataCache {

    private final RedisTemplate<String, DomainEntityFileMetadata> redisTemplate;

    @Value("${cache.metadata.ttl:3600}") // Default 1 hour
    private long cacheTimeToLive;

    @Value("${cache.metadata.prefix:metadata:}")
    private String cacheKeyPrefix;

    public void cacheMetadata(String fileID, DomainEntityFileMetadata metadata) {
        try {
            log.debug("Caching metadata for fileID: {}", fileID);
            ValueOperations<String, DomainEntityFileMetadata> ops = redisTemplate.opsForValue();
            String key = buildKey(fileID);
            ops.set(key, metadata, cacheTimeToLive, TimeUnit.SECONDS);
            log.debug("Cached metadata for fileID: {} with TTL: {} seconds", fileID, cacheTimeToLive);
        } catch (Exception e) {
            log.error("Error caching metadata for fileID: {}", fileID, e);
            throw new InfrastructureException(
                    "Failed to cache metadata: " + e.getMessage(),
                    "CACHE_001"
            );
        }
    }

    public Optional<DomainEntityFileMetadata> getMetadata(String fileID) {
        try {
            log.debug("Retrieving cached metadata for fileID: {}", fileID);
            ValueOperations<String, DomainEntityFileMetadata> ops = redisTemplate.opsForValue();
            String key = buildKey(fileID);
            DomainEntityFileMetadata cacheResult = ops.get(key);

            if (cacheResult != null) {
                log.debug("Cache hit for fileID: {}", fileID);
            } else {
                log.debug("Cache miss for fileID: {}", fileID);
            }

            return Optional.ofNullable(cacheResult);
        } catch (Exception e) {
            log.error("Error retrieving cached metadata for fileID: {}", fileID, e);
            throw new InfrastructureException(
                    "Failed to retrieve cached metadata: " + e.getMessage(),
                    "CACHE_002"
            );
        }
    }

    public void invalidateCache(String fileID) {
        try {
            log.debug("Invalidating cache for fileID: {}", fileID);
            String key = buildKey(fileID);
            Boolean deleted = redisTemplate.delete(key);
            if (Boolean.TRUE.equals(deleted)) {
                log.debug("Cache invalidated for fileID: {}", fileID);
            } else {
                log.debug("No cache entry found to invalidate for fileID: {}", fileID);
            }
        } catch (Exception e) {
            log.error("Error invalidating cache for fileID: {}", fileID, e);
            throw new InfrastructureException(
                    "Failed to invalidate cache: " + e.getMessage(),
                    "CACHE_003"
            );
        }
    }

    private String buildKey(String fileID) {
        return cacheKeyPrefix + fileID;
    }
}
