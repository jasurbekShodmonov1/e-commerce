package com.example.e_commerce.controller;

import com.example.e_commerce.ECommerceApplicationTests;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;

public class ActuatorControllerTest extends ECommerceApplicationTests {

	@Test
	void health_ShouldWorkForPublic() {
		given(publicSpecification)
				.accept(ContentType.JSON)
				.when()
				.get("/actuator/health")
				.then()
				.statusCode(HttpStatus.OK.value())
				.body("status", notNullValue());
	}

	@Test
	void metrics_ShouldNotWorkForPublic() {
		given(publicSpecification)
				.accept(ContentType.JSON)
				.when()
				.get("/actuator/metrics")
				.then()
				.statusCode(HttpStatus.UNAUTHORIZED.value());
	}

	@Test
	void metrics_ShouldWorkForAuthenticatedUser() {
		given(userSpecification)
				.accept(ContentType.JSON)
				.when()
				.get("/actuator/metrics")
				.then()
				.statusCode(HttpStatus.OK.value())
				.body("names", notNullValue())
				.body("names", hasItem("jvm.memory.used"));
	}
}
