package edu.co.arsw.gridmaster.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class ConfigJedis {
    @Value("${spring.redis.host}")
    private String hostName;

    @Value("${spring.redis.port}")
    private int port;

    @Value("${spring.redis.ssl}")
    private boolean ssl;

    @Value("${spring.redis.password}")
    private String accessKey;

    @Value("${spring.redis.timeout:2000}")
    private int timeout;

    @Value("${spring.redis.pool.maxTotal:20}")
    private int maxTotal;

    @Value("${spring.redis.pool.maxIdle:10}")
    private int maxIdle;

    @Value("${spring.redis.pool.minIdle:2}")
    private int minIdle;

    @Bean
    public JedisPool jedisPool() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(maxTotal);
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMinIdle(minIdle);

        return new JedisPool(poolConfig, hostName, port, timeout, accessKey, ssl);
    }
}
