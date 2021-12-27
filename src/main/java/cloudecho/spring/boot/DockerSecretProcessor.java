package cloudecho.spring.boot;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ObjectUtils;

public class DockerSecretProcessor implements EnvironmentPostProcessor {
  private static final Logger LOG = LoggerFactory.getLogger(DockerSecretProcessor.class);
  static final String DEFAULT_BIND_PATH = "file:/run/secrets";

  @Override
  public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
    String bindPathPpty = environment.getProperty("docker-secret.bind-path");
    final String excludePrefix = environment.getProperty("docker-secret.exclude-prefix");
    if (ObjectUtils.isEmpty(bindPathPpty)) {
      bindPathPpty = DEFAULT_BIND_PATH;
    }
    // default to file resource
    if (!bindPathPpty.contains(":")) {
      bindPathPpty = "file:" + bindPathPpty;
    }
    LOG.info("value of \"docker-secret.bind-path\" property:" + bindPathPpty);
    LOG.info("value of \"docker-secret.exclude-prefix\" property:" + excludePrefix);

    Resource secretResource = ResourcePatternUtils
        .getResourcePatternResolver(application.getResourceLoader())
        .getResource(bindPathPpty);

    // if secret resource not found
    if (!secretResource.exists()) {
      return;
    }

    // if resource file not a directory
    File secretDir;
    try {
      secretDir = secretResource.getFile();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    if (!secretDir.isDirectory()) {
      return;
    }

    // find out all the secret files
    File[] secretFiles = secretDir.listFiles((dir, name) ->
        ObjectUtils.isEmpty(excludePrefix) || !name.startsWith(excludePrefix));
    if (secretFiles == null || 0 == secretFiles.length) {
      return;
    }

    Map<String, Object> dockerSecrets = Arrays.stream(secretFiles).collect(
        Collectors.toMap(
            secretFile -> "docker-secret-" + secretFile.getName(),
            secretFile -> {
              try {
                byte[] content = FileCopyUtils.copyToByteArray(secretFile);
                return new String(content);
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            }
        ));

    MapPropertySource pptySource = new MapPropertySource("docker-secrets", dockerSecrets);
    environment.getPropertySources().addLast(pptySource);
  }
}
