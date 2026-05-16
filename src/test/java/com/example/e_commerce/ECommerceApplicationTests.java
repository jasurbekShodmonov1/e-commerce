package com.example.e_commerce;

import com.example.e_commerce.dto.auth.request.LoginRequest;
import com.example.e_commerce.entity.user.User;
import com.example.e_commerce.entity.user.UserRoles;
import com.example.e_commerce.repository.UserRepository;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@ActiveProfiles("test")
//@Testcontainers
public class ECommerceApplicationTests {

	private static final GenericContainer<?> REDIS_CONTAINER;

	// 1. Statik blok ichida konteynerni darhol start qilamiz
	static {
		REDIS_CONTAINER = new GenericContainer<>(DockerImageName.parse("redis:7.2-alpine"))
				.withExposedPorts(6379);
		REDIS_CONTAINER.start();
	}

	// 2. Endi konteyner aniq ishga tushgan, portni xavfsiz olish mumkin
	@DynamicPropertySource
	static void configureRedisProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
		registry.add("spring.data.redis.port", REDIS_CONTAINER::getFirstMappedPort);
	}
	protected static final String TEST_USER_USERNAME = "user";
	protected static final String TEST_USER_PASSWORD = "user123";

	private static final String TEST_ADMIN_USERNAME = "admin";
	private static final String TEST_ADMIN_PASSWORD = "admin123";

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@LocalServerPort
	protected Integer port;

	protected RequestSpecification publicSpecification;
	protected RequestSpecification userSpecification;
	protected RequestSpecification adminSpecification;

	@Test
	void contextLoads() {
	}

	@BeforeEach
	protected void setUp(RestDocumentationContextProvider restDocumentation) {
		createUserIfAbsent(TEST_USER_USERNAME, TEST_USER_PASSWORD, UserRoles.USER);
		createUserIfAbsent(TEST_ADMIN_USERNAME, TEST_ADMIN_PASSWORD, UserRoles.ADMIN);


		String userToken = getToken(TEST_USER_USERNAME, TEST_USER_PASSWORD);
		String adminToken = getToken(TEST_ADMIN_USERNAME, TEST_ADMIN_PASSWORD);

		publicSpecification = new RequestSpecBuilder()
				.addFilter(documentationConfiguration(restDocumentation)
						.operationPreprocessors()
						.withRequestDefaults(prettyPrint())
						.withResponseDefaults(prettyPrint()))
				.setPort(port)
				.setAccept(ContentType.JSON)
				.build();

		userSpecification = new RequestSpecBuilder()
				.setPort(port)
				.addHeader("Authorization", "Bearer " + userToken)
				.setAccept(ContentType.JSON)
				.addFilter(documentationConfiguration(restDocumentation))
				.build();


		adminSpecification = new RequestSpecBuilder()
				.addFilter(documentationConfiguration(restDocumentation))
				.addHeader("Authorization", "Bearer " + adminToken)
				.setPort(port)
				.setAccept(ContentType.JSON)
				.build();
	}


	protected String getToken(String username, String password) {

		return io.restassured.RestAssured.given()
				.port(port)
				.contentType(ContentType.JSON)
				.body(new LoginRequest(username, password))
				.when()
				.post("/api/auth/login")
				.then()
				.extract().path("accessToken");
	}

	private void createUserIfAbsent(String username, String password, UserRoles role) {
		if (userRepository.findByUsername(username).isPresent()) {
			return;
		}

		User user = new User();
		user.setFullName("Test User");
		user.setUsername(username);
		user.setPassword(passwordEncoder.encode(password));
		user.setRole(role);
		user.setCreatedAt(LocalDateTime.now());

		userRepository.save(user);
	}

}
