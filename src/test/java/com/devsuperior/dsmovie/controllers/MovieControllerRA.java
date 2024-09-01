package com.devsuperior.dsmovie.controllers;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.devsuperior.dsmovie.tests.TokenUtil;

import io.restassured.http.ContentType;

public class MovieControllerRA {
	
	private String clientUserName, clientPassWord, admuserName, admPassWord;
	private String clientToken, adminToken, invalidToken;
	private long nonExistingId;
	private long existingId;
	
	private  Map<String, Object> postMovieInstance;
	private  Map<String, Object> postMovieInstance2;
	
	@BeforeEach
	void setUp() throws Exception {
		baseURI = "http://localhost:8080"; 
		existingId = 29L;
		nonExistingId = 100;
		
		clientUserName = "alex@gmail.com";
		clientPassWord = "123456";
		
		admuserName = "maria@gmail.com";
		admPassWord = "123456";
		
		clientToken = TokenUtil.obtainAccessToken(clientUserName, clientPassWord);
		adminToken = TokenUtil.obtainAccessToken(admuserName, admPassWord);
		invalidToken = adminToken + "xpto";
		
		postMovieInstance = new HashMap<>();
		postMovieInstance.put("title", "   ");
		postMovieInstance.put("score", 0.0);
		postMovieInstance.put("count", 0);
		postMovieInstance.put("image", "https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg");
		
		postMovieInstance2 = new HashMap<>();
		postMovieInstance2.put("title", "test movie");
		postMovieInstance2.put("score", 0.0);
		postMovieInstance2.put("count", 0);
		postMovieInstance2.put("image", "https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg");
		
	}
	
	@Test
	public void findAllShouldReturnOkWhenMovieNoArgumentsGiven() {
		  given()
		  	.get("/movies")
          		.then()
          			.statusCode(200);
	}
	
	@Test
	public void findAllShouldReturnPagedMoviesWhenMovieTitleParamIsNotEmpty() {		
		 given()
		  	.get("/movies?title=Aquaman")
       		.then()
       			.statusCode(200)
		        .body("content.title", hasItem("Aquaman"));
	}
	
	@Test
	public void findByIdShouldReturnMovieWhenIdExists() {
		 given()
		  	.get("/movies/{id}", existingId)
    		.then()
    			.statusCode(200)
    			.body("id", is(29))
		        .body("title", equalTo("Aquaman"));
	}
	
	@Test
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() {
		 given()
		  	.get("/movies/{id}", nonExistingId)
 		.then()
 			.statusCode(404);
	}
	
	@Test
	public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndBlankTitle() throws JSONException {
		JSONObject newMovie = new  JSONObject(postMovieInstance);
		
		given()
		.header("Content-type", "Application/json")
        .header("Authorization", "Bearer " + adminToken)
        .contentType(ContentType.JSON)
        .body(newMovie)
        .when()
            .post("/movies")
        .then()
            .statusCode(422);
	}
	
	@Test
	public void insertShouldReturnForbiddenWhenClientLogged() throws Exception {
		JSONObject newMovie = new  JSONObject(postMovieInstance2);
		
		given()
		.header("Content-type", "Application/json")
        .header("Authorization", "Bearer " + clientToken)
        .contentType(ContentType.JSON)
        .body(newMovie)
        .when()
            .post("/movies")
        .then()
            .statusCode(403);
	}
	
	@Test
	public void insertShouldReturnUnauthorizedWhenInvalidToken() throws Exception {
		JSONObject newMovie = new  JSONObject(postMovieInstance2);
		
		given()
		.header("Content-type", "Application/json")
        .header("Authorization", "Bearer " + invalidToken)
        .contentType(ContentType.JSON)
        .body(newMovie)
        .when()
            .post("/movies")
        .then()
            .statusCode(401);
	}
}
