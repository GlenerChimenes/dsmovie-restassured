package com.devsuperior.dsmovie.controllers;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.devsuperior.dsmovie.tests.TokenUtil;

import io.restassured.http.ContentType;

public class ScoreControllerRA {
	
	private String clientUserName, clientPassWord;
	private String clientToken;
	
	private  Map<String, Object> postScoreInstance;
	
	private  Map<String, Object> postScoreSemMovieId;
	
	private  Map<String, Object> postScoreInvalido;
	
	@BeforeEach
	void setUp() throws Exception {
		baseURI = "http://localhost:8080"; 
		
		clientUserName = "alex@gmail.com";
		clientPassWord = "123456";
		
		clientToken = TokenUtil.obtainAccessToken(clientUserName, clientPassWord);
		
		//movieId inexistente
		postScoreInstance = new HashMap<>();
		postScoreInstance.put("movieId", 100L);
		postScoreInstance.put("score", 0.0D);
		
		// movieId nao informado
		postScoreSemMovieId = new HashMap<>();
		postScoreSemMovieId.put("score", 0.0D);
		
		// score negativo
		postScoreInvalido = new HashMap<>();
		postScoreInvalido.put("movieId", 100L);
		postScoreInvalido.put("score", -1D);
		
		
	}
	
	@Test
	public void saveScoreShouldReturnNotFoundWhenMovieIdDoesNotExist() throws Exception {		
		JSONObject newScore= new  JSONObject(postScoreInstance);
		
		given()
		.header("Content-type", "Application/json")
        .header("Authorization", "Bearer " + clientToken)
        .contentType(ContentType.JSON)
        .body(newScore)
        .when()
            .put("/scores")
        .then()
            .statusCode(404);
	}
	
	@Test
	public void saveScoreShouldReturnUnprocessableEntityWhenMissingMovieId() throws Exception {
		JSONObject newScore= new  JSONObject(postScoreSemMovieId);
		
		given()
		.header("Content-type", "Application/json")
        .header("Authorization", "Bearer " + clientToken)
        .contentType(ContentType.JSON)
        .body(newScore)
        .when()
            .put("/scores")
        .then()
            .statusCode(422);
	}
	
	@Test
	public void saveScoreShouldReturnUnprocessableEntityWhenScoreIsLessThanZero() throws Exception {	
		JSONObject newScore= new  JSONObject(postScoreInvalido);
		
		given()
		.header("Content-type", "Application/json")
        .header("Authorization", "Bearer " + clientToken)
        .contentType(ContentType.JSON)
        .body(newScore)
        .when()
            .put("/scores")
        .then()
            .statusCode(422);
	}
}
