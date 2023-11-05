package dev.angelcruzl;

import dev.angelcruzl.shared.GenericResponse;
import dev.angelcruzl.user.User;
import dev.angelcruzl.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserControllerTest {
  private static final String API_1_0_USERS = "/api/1.0/users";
  @Autowired
  TestRestTemplate testRestTemplate;

  @Autowired
  UserRepository userRepository;

  @BeforeEach
  public void cleanup() {
    userRepository.deleteAll();
  }

  private User createValidUser() {
    User user = new User();
    user.setUsername("test-user");
    user.setDisplayName("test-display");
    user.setPassword("Secret.123");

    return user;
  }

  @Test
  public void postUser_whenUserIsValid_receiveOk() {
    User user = createValidUser();
    ResponseEntity<Object> response = testRestTemplate.postForEntity(API_1_0_USERS, user, Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  public void postUser_whenUserIsValid_userSavedToDatabase() {
    User user = createValidUser();
    testRestTemplate.postForEntity(API_1_0_USERS, user, Object.class);

    assertThat(userRepository.count()).isEqualTo(1);
  }

  @Test
  public void postUser_whenUserIsValid_receiveSuccessMessage() {
    User user = createValidUser();
    ResponseEntity<GenericResponse> response = testRestTemplate.postForEntity(API_1_0_USERS, user, GenericResponse.class);

    assertThat(response.getBody().getMessage()).isNotNull();
  }

  @Test
  public void postUser_whenUserIsValid_passwordIsHashedInDatabase() {
    User user = createValidUser();
    testRestTemplate.postForEntity(API_1_0_USERS, user, Object.class);
    User savedUser = userRepository.findAll().get(0);

    assertThat(savedUser.getPassword()).isNotEqualTo(user.getPassword());
  }
}
