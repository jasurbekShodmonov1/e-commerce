package com.example.e_commerce.controller;

import com.example.e_commerce.ECommerceApplicationTests;
import com.example.e_commerce.dto.request.UserRequest;
import com.example.e_commerce.entity.user.UserRoles;
import com.example.e_commerce.repository.UserRepository;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class UserControllerTest extends ECommerceApplicationTests {

	@Autowired
	private UserRepository userRepository;

	private final String fullName = "John Doe";
	private final String username = "johndoe";
	private final String password = "johndoe123";

	@BeforeEach
	void initData() {
		userRepository.findByUsername(username).ifPresent(userRepository::delete);
	}

	@Test
	void getAllUsers_ShouldNotWorkForPublic() {
		given(publicSpecification)
				.accept(ContentType.JSON)
				.when()
				.get("/api/users")
				.then()
				.statusCode(HttpStatus.UNAUTHORIZED.value());
	}

	@Test
	void getAllUsers_ShouldWorkForAuthenticatedUser() {
		given(userSpecification)
				.accept(ContentType.JSON)
				.when()
				.get("/api/users")
				.then()
				.statusCode(HttpStatus.FORBIDDEN.value());
	}

	@Test
	void getAllUsers_ShouldWorkForAuthenticatedAdmin() {
		given(adminSpecification)
				.accept(ContentType.JSON)
				.when()
				.get("/api/users")
				.then()
				.statusCode(HttpStatus.OK.value());
	}

	@Test
	void registerUser_ShouldWorkForPublic() {
		given(publicSpecification)
				.contentType(ContentType.JSON)
				.body(userRequest())
				.when()
				.post("/api/users/register")
				.then()
				.statusCode(HttpStatus.OK.value())
				.body("userId", notNullValue())
				.body("fullName", equalTo(fullName))
				.body("username", equalTo(username))
				.body("role", equalTo(UserRoles.USER.name()));
	}

	@Test
	void getByUserId_ShouldWorkForAuthenticatedUser() {
		Long savedUserId = registerUser();

		given(userSpecification)
				.accept(ContentType.JSON)
				.when()
				.get("/api/users/{userId}", savedUserId)
				.then()
				.statusCode(HttpStatus.FORBIDDEN.value());
	}
	@Test
	void getByUserId_ShouldWorkForAuthenticatedAdmin() {
		Long savedUserId = registerUser();

		given(adminSpecification)
				.accept(ContentType.JSON)
				.when()
				.get("/api/users/{userId}", savedUserId)
				.then()
				.statusCode(HttpStatus.OK.value())
				.body("userId", equalTo(savedUserId.intValue()))
				.body("fullName", equalTo(fullName))
				.body("username", equalTo(username));
	}

	@Test
	void createAdmin_ShouldNotWorkForPublic() {
		given(publicSpecification)
				.contentType(ContentType.JSON)
				.body(userRequest())
				.when()
				.post("/api/users/createAdmin")
				.then()
				.statusCode(HttpStatus.UNAUTHORIZED.value());
	}

	@Test
	void createAdmin_ShouldWorkForAuthenticatedUser() {
		given(userSpecification)
				.contentType(ContentType.JSON)
				.body(userRequest())
				.when()
				.post("/api/users/createAdmin")
				.then()
				.statusCode(HttpStatus.FORBIDDEN.value());
	}

	@Test
	void createAdmin_ShouldWorkForAuthenticatedAdmin() {
		given(adminSpecification)
				.contentType(ContentType.JSON)
				.body(userRequest())
				.when()
				.post("/api/users/createAdmin")
				.then()
				.statusCode(HttpStatus.OK.value())
				.body("userId", notNullValue())
				.body("fullName", equalTo(fullName))
				.body("username", equalTo(username))
				.body("role", equalTo(UserRoles.ADMIN.name()));
	}

	private UserRequest userRequest() {
		return new UserRequest(fullName, username, password);
	}

	private Long registerUser() {
		Number userId = given(publicSpecification)
				.contentType(ContentType.JSON)
				.body(userRequest())
				.when()
				.post("/api/users/register")
				.then()
				.statusCode(HttpStatus.OK.value())
				.extract()
				.path("userId");

		return userId.longValue();
	}
}
