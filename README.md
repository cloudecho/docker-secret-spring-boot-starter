# docker-secret-spring-boot-starter

Inspired by [kwonghung-YIP/spring-boot-docker-secret](https://github.com/kwonghung-YIP/spring-boot-docker-secret)

## How to use it

### Maven dependency

```xml
      <dependency>
        <groupId>com.github.cloudecho</groupId>
        <artifactId>docker-secret-spring-boot-starter</artifactId>
        <version>0.0.4</version>
      </dependency>
```

### spring-boot `application.yml`

e.g.

```yaml
spring:
  application:
    name: myapplication
  redis:
    port: 6379
    host: redis
    password: ${docker-secret-redis-passwd}

#docker-secret:
# (default values)
#  bind-path: /run/secrets
#  exclude-prefix: 
```

See [DockerSecretProcessorTest](src/test/java/clooudecho/spring/boot/DockerSecretProcessorTest.java) & 
[application-test.yml](src/test/resources/application-test.yml) for more details.
