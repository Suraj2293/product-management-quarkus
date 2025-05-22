package com.product;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;

@QuarkusTest
public class ProductControllerTest {

    @Test
    public void testCreateProduct() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                {
                    "name": "Mouse",
                    "description": "Testing",
                    "price": 10.0,
                    "quantity": 5
                }
                """)
                .when()
                .post("/products")
                .then()
                .statusCode(201)
                .body("name", is("Mouse"));
    }

    @Test
    public void testQuantity() {
        // Create a product first
        Integer id = given()
                .contentType(ContentType.JSON)
                .body("""
                {
                    "name": "Mouse",
                    "description": "Testing",
                    "price": 20.0,
                    "quantity": 100
                }
                """)
                .when()
                .post("/products")
                .then()
                .extract().path("id");

        // Then update it
        given()
                .contentType(ContentType.JSON)
                .pathParam("id", id)
                .pathParam("count", 10)
                .when()
                .get("/products/{id}/quantity/{count}")
                .then()
                .statusCode(200)
                .body("message", is("Stock is available for product " + id));
    }

    @Test
    public void testWithLessQuantity() {
        // Create a product first
        Integer id = given()
                .contentType(ContentType.JSON)
                .body("""
                {
                    "name": "Mouse",
                    "description": "Testing",
                    "price": 20.0,
                    "quantity": 100
                }
                """)
                .when()
                .post("/products")
                .then()
                .extract().path("id");

        // Then update it
        given()
                .contentType(ContentType.JSON)
                .pathParam("id", id)
                .pathParam("count", 300)
                .when()
                .get("/products/{id}/quantity/{count}")
                .then()
                .statusCode(400)
                .body("message", is("Stock is not available for product " + id));
    }


    @Test
    public void testUpdateProduct() {
        // Create a product first
        Integer id = given()
                .contentType(ContentType.JSON)
                .body("""
                {
                    "name": "Mouse",
                    "description": "Testing",
                    "price": 20.0,
                    "quantity": 10
                }
                """)
                .when()
                .post("/products")
                .then()
                .extract().path("id");

        // Then update it
        given()
                .contentType(ContentType.JSON)
                .body("""
                {
                    "id": %d,
                    "name": "MouseUpdated",
                    "description": "Testing",
                    "price": 30.0,
                    "quantity": 15
                }
                """.formatted(id))
                .pathParam("id", id)
                .when()
                .put("/products/{id}")
                .then()
                .statusCode(200)
                .body("name", is("MouseUpdated"));
    }

    @Test
    public void testDeleteProduct() {
        // Create a product to delete
        Integer id = given()
                .contentType(ContentType.JSON)
                .body("""
                {
                    "name": "Mouse",
                    "description": "Testing",
                    "price": 9.99,
                    "quantity": 2
                }
                """)
                .when()
                .post("/products")
                .then()
                .extract().path("id");

        // Delete it
        given()
                .pathParam("id", id)
                .when()
                .delete("/products/{id}")
                .then()
                .statusCode(is(200));

    }

    @Test
    public void testGetAllProducts() {
        when()
                .get("/products")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(0));
    }

    @Test
    public void testHello() {

        given()
                .when().get("/products/hello")
                .then()
                .statusCode(200)
                .body(is("Hello..."));
//        Assert.assertTrue(s.extract().response().asString().equalsIgnoreCase("Hello..."));

    }
}
