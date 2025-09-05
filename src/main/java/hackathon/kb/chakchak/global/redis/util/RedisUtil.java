package hackathon.kb.chakchak.global.redis.util;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final StringRedisTemplate stringRedisTemplate;

    @Value("${jwt.refresh-token-validity-seconds}")
    private long refreshTtl;

    public void set(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value, refreshTtl, TimeUnit.SECONDS);
    }

    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    public boolean delete(String key) {
        return Boolean.TRUE.equals(stringRedisTemplate.delete(key));
    }

    /** 블랙리스트(로그아웃/탈퇴 등): 분 단위 TTL */
    public void setBlackList(String key, String value, int minutes) {
        stringRedisTemplate.opsForValue().set(key, value, minutes, TimeUnit.MINUTES);
    }

    public boolean hasKeyBlackList(String key) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
    }
}

