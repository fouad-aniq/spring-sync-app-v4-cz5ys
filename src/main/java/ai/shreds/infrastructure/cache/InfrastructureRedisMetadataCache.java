package ai.shreds.infrastructure.cache;

import ai.shreds.domain.entities.DomainEntityFileMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
public class InfrastructureRedisMetadataCache {

    private static final Logger logger = LoggerFactory.getLogger(InfrastructureRedisMetadataCache.class);
    private static final String CACHE_PREFIX = "metadata:";
    private static final long CACHE_DURATION = 30; // 30 minutes

    private final RedisTemplate<String, Object> redisTemplate;

    public InfrastructureRedisMetadataCache(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void cacheMetadata(DomainEntityFileMetadata metadata) {
        try {
            String key = CACHE_PREFIX + metadata.getFileID();
            redisTemplate.opsForValue().set(key, metadata, CACHE_DURATION, TimeUnit.MINUTES);
            logger.debug("Cached metadata for file: {}", metadata.getFileID());
        } catch (Exception e) {
            logger.error("Error caching metadata for file: {}", metadata.getFileID(), e);
        }
    }

    public Optional<DomainEntityFileMetadata> getMetadata(String fileID) {
        try {
            String key = CACHE_PREFIX + fileID;
            Object cached = redisTemplate.opsForValue().get(key);
            if (cached != null) {
                logger.debug("Cache hit for file: {}", fileID);
                return Optional.of((DomainEntityFileMetadata) cached);
            }
            logger.debug("Cache miss for file: {}", fileID);
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Error retrieving cached metadata for file: {}", fileID, e);
            return Optional.empty();
        }
    }

    public void invalidateCache(String fileID) {
        try {
            String key = CACHE_PREFIX + fileID;
            redisTemplate.delete(key);
            logger.debug("Invalidated cache for file: {}", fileID);
        } catch (Exception e) {
            logger.error("Error invalidating cache for file: {}", fileID, e);
        }
    }

    public void clearAllCache() {
        try {
            String pattern = CACHE_PREFIX + "*";
            redisTemplate.delete(redisTemplate.keys(pattern));
            logger.info("Cleared all metadata cache");
        } catch (Exception e) {
            logger.error("Error clearing all metadata cache", e);
        }
    }
}
