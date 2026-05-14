package com.example.e_commerce.controller;

import com.example.e_commerce.ECommerceApplicationTests;
import com.example.e_commerce.dto.request.ProductRequest;
import com.example.e_commerce.repository.OrderItemRepository;
import com.example.e_commerce.repository.OrderRepository;
import com.example.e_commerce.repository.ProductRepository;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;

public class ProductControllerTest extends ECommerceApplicationTests {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private OrderItemRepository orderItemRepository;

	@Autowired
	private OrderRepository orderRepository;

	private final String name = "Apple";
	private final BigDecimal price = new BigDecimal("100");
	private final Integer stock = 100;
	private final String category = "Fruit";

	@BeforeEach
	void init() {
		orderItemRepository.deleteAll();
		orderRepository.deleteAll();
		productRepository.deleteAll();
	}

	@Test
	void getAllProducts_ShouldNotWorkForPublic() {
		given(publicSpecification)
				.accept(ContentType.JSON)
				.when()
				.get("/api/products")
				.then()
				.statusCode(HttpStatus.UNAUTHORIZED.value());
	}

	@Test
	void getAllProducts_ShouldWorkForAuthenticatedUser() {
		createProduct(userSpecification);

		given(userSpecification)
				.accept(ContentType.JSON)
				.queryParam("page", 0)
				.queryParam("size", 10)
				.when()
				.get("/api/products")
				.then()
				.statusCode(HttpStatus.OK.value())
				.body("content.size()", greaterThanOrEqualTo(1))
				.body("content[0].name", equalTo(name))
				.body("content[0].category", equalTo(category));
	}

	@Test
	void createProduct_ShouldNotWorkForPublic() {
		given(publicSpecification)
				.contentType(ContentType.JSON)
				.body(createProductRequest())
				.when()
				.post("/api/products")
				.then()
				.statusCode(HttpStatus.UNAUTHORIZED.value());
	}

	@Test
	void createProduct_ShouldWorkForAuthenticatedUser() {
		given(userSpecification)
				.contentType(ContentType.JSON)
				.body(createProductRequest())
				.when()
				.post("/api/products")
				.then()
				.statusCode(HttpStatus.OK.value())
				.body("id", notNullValue())
				.body("name", equalTo(name))
				.body("stock", equalTo(stock))
				.body("category", equalTo(category))
				.body("isActive", equalTo(true));
	}

	@Test
	void getProductById_ShouldNotWorkForPublic() {
		given(publicSpecification)
				.accept(ContentType.JSON)
				.when()
				.get("/api/products/{productId}", 1L)
				.then()
				.statusCode(HttpStatus.UNAUTHORIZED.value());
	}

	@Test
	void getProductById_ShouldWorkForAuthenticatedUser() {
		Long savedProductId = createProduct(userSpecification);

		given(userSpecification)
				.accept(ContentType.JSON)
				.when()
				.get("/api/products/{productId}", savedProductId)
				.then()
				.statusCode(HttpStatus.OK.value())
				.body("id", equalTo(savedProductId.intValue()))
				.body("name", equalTo(name))
				.body("category", equalTo(category));
	}

	@Test
	void getProductById_ShouldReturnNotFound() {
		given(userSpecification)
				.accept(ContentType.JSON)
				.when()
				.get("/api/products/{productId}", 999L)
				.then()
				.statusCode(HttpStatus.NOT_FOUND.value());
	}

	@Test
	void updateProduct_ShouldWorkForAuthenticatedUser() {
		Long savedProductId = createProduct(userSpecification);
		ProductRequest updateRequest = new ProductRequest(
				"Updated Apple",
				new BigDecimal("150"),
				50,
				"Updated Fruit"
		);

		given(userSpecification)
				.contentType(ContentType.JSON)
				.body(updateRequest)
				.when()
				.put("/api/products/{productId}", savedProductId)
				.then()
				.statusCode(HttpStatus.OK.value())
				.body("id", equalTo(savedProductId.intValue()))
				.body("name", equalTo("Updated Apple"))
				.body("stock", equalTo(50))
				.body("category", equalTo("Updated Fruit"));
	}

	@Test
	void deleteProduct_ShouldWorkForAuthenticatedUser() {
		Long savedProductId = createProduct(userSpecification);

		given(userSpecification)
				.accept(ContentType.JSON)
				.when()
				.delete("/api/products/{productId}", savedProductId)
				.then()
				.statusCode(HttpStatus.OK.value())
				.body(equalTo("Product deleted successfully"));
	}

	@Test
	void searchProducts_ShouldWorkForAuthenticatedUser() {
		createProduct(userSpecification);

		given(userSpecification)
				.accept(ContentType.JSON)
				.queryParam("name", "app")
				.queryParam("category", category)
				.when()
				.get("/api/products/search")
				.then()
				.statusCode(HttpStatus.OK.value())
				.body("size()", equalTo(1))
				.body("[0].name", equalTo(name))
				.body("[0].category", equalTo(category));
	}

	private ProductRequest createProductRequest() {
		return new ProductRequest(name, price, stock, category);
	}

	private Long createProduct(io.restassured.specification.RequestSpecification specification) {
		Number productId = given(specification)
				.contentType(ContentType.JSON)
				.body(createProductRequest())
				.when()
				.post("/api/products")
				.then()
				.statusCode(HttpStatus.OK.value())
				.extract()
				.path("id");

		return productId.longValue();
	}
}
