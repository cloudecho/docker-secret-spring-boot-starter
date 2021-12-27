package clooudecho.spring.boot;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles({"default", "test"})
public class DockerSecretProcessorTest {
  @Value("${spring.datasource.password}")
  String password;

  @Value("${docker-secret-tls-secret:}")
  String tlsSecret;

  @Test
  public void contextLoads() {
    Assertions.assertThat(password).isEqualTo("the-passwd");
    Assertions.assertThat(tlsSecret).isEmpty();
  }

  @SpringBootApplication
  public static class App {

  }
}
