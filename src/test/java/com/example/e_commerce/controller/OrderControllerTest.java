package com.example.e_commerce.controller;


import com.example.e_commerce.ECommerceApplicationTests;
import com.example.e_commerce.dto.request.CreateOrderRequest;
import com.example.e_commerce.dto.request.OrderItemRequest;
import com.example.e_commerce.entity.order.OrderStatus;
import com.example.e_commerce.entity.order.Product;
import com.example.e_commerce.repository.OrderRepository;
import com.example.e_commerce.repository.ProductRepository;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class OrderControllerTest extends ECommerceApplicationTests {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private OrderRepository orderRepository;

	private Long savedProductId;
	private final String customerName = "Jasur";
	private final String customerEmail = "jasur@example.com";

	@BeforeEach
	void initData() {
		orderRepository.deleteAll();
		productRepository.deleteAll();

		Product product = new Product();
		product.setName("Apple");
		product.setPrice(BigDecimal.valueOf(12000));
		product.setStock(10);
		product.setCategory("Fruit");
		product.setIsActive(true);

		savedProductId = productRepository.save(product).getId();
	}

	@Test
	void create_ShouldNotWorkForPublic() {
		given(publicSpecification)
				.contentType(ContentType.JSON)
				.body(createOrderRequest())
				.when()
				.post("/api/order")
				.then()
				.statusCode(HttpStatus.UNAUTHORIZED.value());
	}

	@Test
	void getAll_ShouldNotWorkForPublic(){
		given(publicSpecification)
				.accept(ContentType.JSON)
				.when()
				.get("/api/order")
				.then()
				.statusCode(HttpStatus.UNAUTHORIZED.value());

	}

	@Test
	void getOrder_ShouldWorkForPublic(){
		given(publicSpecification)
				.accept(ContentType.JSON)
				.when()
				.get("/api/order/{orderId}", 1L)
				.then()
				.statusCode(HttpStatus.UNAUTHORIZED.value());

	}

	@Test
	void getOrder_ShouldWorkForUser(){
		Long savedOrderId = createOrder(userSpecification);

		given(userSpecification)
				.accept(ContentType.JSON)
				.when()
				.get("/api/order/{orderId}", savedOrderId)
				.then()
				.statusCode(HttpStatus.OK.value())
				.body("id", equalTo(savedOrderId.intValue()))
				.body("customerName", equalTo(customerName))
				.body("customerEmail", equalTo(customerEmail));

	}

	@Test
	void getOrder_ShouldWorkForAdmin(){
		Long savedOrderId = createOrder(adminSpecification);

		given(adminSpecification)
				.accept(ContentType.JSON)
				.when()
				.get("/api/order/{orderId}", savedOrderId)
				.then()
				.statusCode(HttpStatus.OK.value())
				.body("id", equalTo(savedOrderId.intValue()))
				.body("customerName", equalTo(customerName))
				.body("customerEmail", equalTo(customerEmail));

	}

	@Test
	void getAll_ShouldWorkForAuthenticatedUser(){
		given(userSpecification)
				.accept(ContentType.JSON)
				.when()
				.get("/api/order")
				.then()
				.statusCode(HttpStatus.OK.value());

	}

	@Test
	void getAll_ShouldWorkForAuthenticatedAdmin(){
		given(adminSpecification)
				.accept(ContentType.JSON)
				.when()
				.get("/api/order")
				.then()
				.statusCode(HttpStatus.OK.value());

	}

	@Test
	void create_ShouldWorkForAuthenticatedUser(){
		given(userSpecification)
				.contentType(ContentType.JSON)
				.body(createOrderRequest())
				.when()
				.post("/api/order")
				.then()
				.statusCode(HttpStatus.OK.value())
				.body("customerName", equalTo(customerName))
				.body("customerEmail", equalTo(customerEmail));
	}
	@Test
	void create_ShouldWorkForAuthenticatedAdmin() {

		given(adminSpecification)
				.contentType(ContentType.JSON)
				.body(createOrderRequest())
				.when()
				.post("/api/order")
				.then()
				.statusCode(HttpStatus.OK.value())
				.body("id", notNullValue())
				.body("customerName", equalTo(customerName))
				.body("customerEmail", equalTo(customerEmail));
	}

	@Test
	void updateStatus_ShouldNotWorkForPublic(){
		Long savedOrderId = createOrder(userSpecification);

		given(publicSpecification)
				.log().all()
				.contentType(ContentType.JSON)
				.queryParam("status", OrderStatus.CONFIRMED)
				.when()
				.put("/api/order/{orderId}/status", savedOrderId)
				.then()
				.statusCode(HttpStatus.UNAUTHORIZED.value());
	}

	@Test
	void updateStatus_ShouldWorkForAuthenticatedUser(){
		Long savedOrderId = createOrder(userSpecification);

		given(userSpecification)
				.log().all()
				.contentType(ContentType.JSON)
				.queryParam("status", OrderStatus.CONFIRMED)
				.when()
				.put("/api/order/{orderId}/status", savedOrderId)
				.then()
				.statusCode(HttpStatus.OK.value())
				.body("id", equalTo(savedOrderId.intValue()))
				.body("status", equalTo(String.valueOf(OrderStatus.CONFIRMED)));
	}

	@Test
	void updateStatus_ShouldWorkForAuthenticatedAdmin(){
		Long savedOrderId = createOrder(userSpecification);

		given(adminSpecification)
				.log().all()
				.contentType(ContentType.JSON)
				.queryParam("status", OrderStatus.SHIPPED)
				.when()
				.put("/api/order/{orderId}/status", savedOrderId)
				.then()
				.statusCode(HttpStatus.OK.value())
				.body("id", equalTo(savedOrderId.intValue()))
				.body("status", equalTo(String.valueOf(OrderStatus.SHIPPED)));
	}

	@Test
	void delete_ShouldNotWorkForPublic(){
		Long savedOrderId = createOrder(userSpecification);

		given(publicSpecification)
				.contentType(ContentType.JSON)
				.when()
				.delete("/api/order/{orderId}", savedOrderId)
				.then()
				.statusCode(HttpStatus.UNAUTHORIZED.value());
	}

	@Test
	void delete_ShouldWorkForAuthenticatedUser(){
		Long savedOrderId = createOrder(userSpecification);

		given(userSpecification)
				.contentType(ContentType.JSON)
				.when()
				.delete("/api/order/{orderId}", savedOrderId)
				.then()
				.statusCode(HttpStatus.OK.value());
	}

	@Test
	void delete_ShouldWorkForAuthenticatedAdmin() {
		Long savedOrderId = createOrder(adminSpecification);

		given(adminSpecification)
				.contentType(ContentType.JSON)
				.when()
				.delete("/api/order/{orderId}", savedOrderId)
				.then()
				.statusCode(HttpStatus.OK.value());
	}

	@Test
	void getByCustomerEmail_ShouldNotWorkForPublic(){
		given(publicSpecification)
				.contentType(ContentType.JSON)
				.when()
				.get("/api/order/customer/{email}", customerEmail)
				.then()
				.statusCode(HttpStatus.UNAUTHORIZED.value());
	}

	@Test
	void getByCustomerEmail_ShouldWorkForAuthenticatedUser(){
		given(userSpecification)
				.contentType(ContentType.JSON)
				.when()
				.get("/api/order/customer/{email}", customerEmail)
				.then()
				.statusCode(HttpStatus.OK.value());
	}

	@Test
	void getByCustomerEmail_ShouldWorkForAuthenticatedAdmin() {
		given(adminSpecification)
				.contentType(ContentType.JSON)
				.when()
				.get("/api/order/customer/{email}", customerEmail)
				.then()
				.statusCode(HttpStatus.OK.value());
	}


	private CreateOrderRequest createOrderRequest() {
		return new CreateOrderRequest(
				customerName,
				customerEmail,
				List.of(new OrderItemRequest(savedProductId, 2))
		);
	}

	private Long createOrder(io.restassured.specification.RequestSpecification specification) {
		Number orderId = given(specification)
				.contentType(ContentType.JSON)
				.body(createOrderRequest())
				.when()
				.post("/api/order")
				.then()
				.statusCode(HttpStatus.OK.value())
				.extract()
				.path("id");

		return orderId.longValue();
	}
}
