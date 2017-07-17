
import static com.jayway.restassured.RestAssured.given;
import java.io.IOException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import junit.framework.Assert;

/**
 * 
 * @author nachiketatripathi
 *
 */
public class SnLServiceV3Test {

	@BeforeTest
	public void setBaseUri() {
		RestAssured.baseURI = "http://10.0.1.86/snl/";
	}

	@Test
	public void testingTokenGeneration() {

		Response res = (Response) RestAssured.given()
				.parameters("username", "su", "password", "root_pass", "grant_type", "client_credentials", "client_id",
						"f76c0da35f6402b9ac6f8a3dbcf834c9ec994bd30fad0fe348450f57c591272c", "client_secret",
						"c81ac956249ea1ec4aeb2228678e7cde517b95712735431cc5c3b2ca6e016a52")
				.auth().preemptive()
				.basic("f76c0da35f6402b9ac6f8a3dbcf834c9ec994bd30fad0fe348450f57c591272c",
						"c81ac956249ea1ec4aeb2228678e7cde517b95712735431cc5c3b2ca6e016a52")
				.when().post("oauth/token").then().contentType(ContentType.JSON).extract().response();
		System.out.println(res.asString());
		String token = res.jsonPath().getString("access_token");
		Response res1 = (Response) RestAssured.given().auth().oauth2(token).when().get("rest/v3/board.json");
		System.out.println(token);
		Assert.assertEquals(res1.statusCode(), 200);
	}

	/**
	 * Getting id's of all the boards
	 * 
	 * @throws IOException
	 * @throws org.json.simple.parser.ParseException
	 */
	@Test
	public void testingListOfTheBoards() throws IOException, org.json.simple.parser.ParseException {
		Response res = (Response) RestAssured.given()
				.parameters("username", "su", "password", "root_pass", "grant_type", "client_credentials", "client_id",
						"f76c0da35f6402b9ac6f8a3dbcf834c9ec994bd30fad0fe348450f57c591272c", "client_secret",
						"c81ac956249ea1ec4aeb2228678e7cde517b95712735431cc5c3b2ca6e016a52")
				.auth().preemptive()
				.basic("f76c0da35f6402b9ac6f8a3dbcf834c9ec994bd30fad0fe348450f57c591272c",
						"c81ac956249ea1ec4aeb2228678e7cde517b95712735431cc5c3b2ca6e016a52")
				.when().post("oauth/token").then().contentType(ContentType.JSON).extract().response();
		System.out.println(res.asString());
		String token = res.jsonPath().getString("access_token");

		String s = RestAssured.given().auth().oauth2(token).when().get("rest/v3/board.json").asString();
		System.out.println(s);
		JSONParser parser = new JSONParser();
		JSONObject jobj = (JSONObject) parser.parse(s);
		JSONObject jobj1 = (JSONObject) jobj.get("response");
		JSONArray jarr = (JSONArray) jobj1.get("board");

		for (int i = 0; i < jarr.size(); i++) {
			JSONObject jobj2 = (JSONObject) jarr.get(i);
			String id = jobj2.get("id").toString();
			System.out.println(id);
		}
	}

	/**
	 * Testing new board entry
	 * 
	 * @throws ParseException
	 */
	@Test
	public void testingNewBoardEntry() throws ParseException {

		Response res = (Response) RestAssured.given()
				.parameters("username", "su", "password", "root_pass", "grant_type", "client_credentials", "client_id",
						"f76c0da35f6402b9ac6f8a3dbcf834c9ec994bd30fad0fe348450f57c591272c", "client_secret",
						"c81ac956249ea1ec4aeb2228678e7cde517b95712735431cc5c3b2ca6e016a52")
				.auth().preemptive()
				.basic("f76c0da35f6402b9ac6f8a3dbcf834c9ec994bd30fad0fe348450f57c591272c",
						"c81ac956249ea1ec4aeb2228678e7cde517b95712735431cc5c3b2ca6e016a52")
				.when().post("oauth/token").then().contentType(ContentType.JSON).extract().response();
		System.out.println(res.asString());
		String token = res.jsonPath().getString("access_token");

		String newId = RestAssured.given().auth().oauth2(token).when().get("rest/v3/board/new.json").asString();
		JSONParser parser = new JSONParser();
		JSONObject obj = (JSONObject) parser.parse(newId);
		JSONObject obj1 = (JSONObject) obj.get("response");
		JSONObject obj2 = (JSONObject) obj1.get("board");
		String idnew = obj2.get("id").toString();
		System.out.println(idnew);

	}

	/**
	 * Deleting the board
	 */
	@Test
	public void testingForDestroyingTheBoard() {

		Response res = (Response) RestAssured.given()
				.parameters("username", "su", "password", "root_pass", "grant_type", "client_credentials", "client_id",
						"f76c0da35f6402b9ac6f8a3dbcf834c9ec994bd30fad0fe348450f57c591272c", "client_secret",
						"c81ac956249ea1ec4aeb2228678e7cde517b95712735431cc5c3b2ca6e016a52")
				.auth().preemptive()
				.basic("f76c0da35f6402b9ac6f8a3dbcf834c9ec994bd30fad0fe348450f57c591272c",
						"c81ac956249ea1ec4aeb2228678e7cde517b95712735431cc5c3b2ca6e016a52")
				.when().post("oauth/token").then().contentType(ContentType.JSON).extract().response();
		System.out.println(res.asString());
		String token = res.jsonPath().getString("access_token");

		int newId = RestAssured.given().auth().oauth2(token).when().get("rest/v3/board/new.json").then().extract()
				.jsonPath().getInt("response.board.id");
		System.out.println(newId);
		Response res1 = given().authentication().basic("su", "root_pass").when().contentType(ContentType.JSON)
				.delete("/rest/v2/board/" + newId + ".json");
		Assert.assertEquals(res1.statusCode(), 200);
	}

	/**
	 * Adding new player
	 */
	@Test
	public void testingForAddingNewPlayer() {

		Response res = (Response) RestAssured.given()
				.parameters("username", "su", "password", "root_pass", "grant_type", "client_credentials", "client_id",
						"f76c0da35f6402b9ac6f8a3dbcf834c9ec994bd30fad0fe348450f57c591272c", "client_secret",
						"c81ac956249ea1ec4aeb2228678e7cde517b95712735431cc5c3b2ca6e016a52")
				.auth().preemptive()
				.basic("f76c0da35f6402b9ac6f8a3dbcf834c9ec994bd30fad0fe348450f57c591272c",
						"c81ac956249ea1ec4aeb2228678e7cde517b95712735431cc5c3b2ca6e016a52")
				.when().post("oauth/token").then().contentType(ContentType.JSON).extract().response();
		System.out.println(res.asString());
		String token = res.jsonPath().getString("access_token");

		int newId = RestAssured.given().auth().oauth2(token).when().get("rest/v3/board/new.json").then().extract()
				.jsonPath().getInt("response.board.id");
		System.out.println(newId);
		String new_player = " {\"board\":" + newId + ", \"player\":{\"name\": \"NACHI\"}}";
		given().auth().oauth2(token).contentType("application/json").body(new_player).when().post("rest/v3/player.json")
				.then().statusCode(200);
	}

	/**
	 * Testing player details
	 * 
	 * @throws ParseException
	 */
	@Test
	public void testingPlayerDetail() throws ParseException {

		Response res = (Response) RestAssured.given()
				.parameters("username", "su", "password", "root_pass", "grant_type", "client_credentials", "client_id",
						"f76c0da35f6402b9ac6f8a3dbcf834c9ec994bd30fad0fe348450f57c591272c", "client_secret",
						"c81ac956249ea1ec4aeb2228678e7cde517b95712735431cc5c3b2ca6e016a52")
				.auth().preemptive()
				.basic("f76c0da35f6402b9ac6f8a3dbcf834c9ec994bd30fad0fe348450f57c591272c",
						"c81ac956249ea1ec4aeb2228678e7cde517b95712735431cc5c3b2ca6e016a52")
				.when().post("oauth/token").then().contentType(ContentType.JSON).extract().response();
		System.out.println(res.asString());
		String token = res.jsonPath().getString("access_token");

		int newId = RestAssured.given().auth().oauth2(token).when().get("rest/v3/board/new.json").then().extract()
				.jsonPath().getInt("response.board.id");
		String new_player = " {\"board\":" + newId + ", \"player\":{\"name\": \"Rishabh\"}}";
		given().auth().oauth2(token).contentType("application/json").body(new_player).when()
				.post("rest/v3/player.json");
		String s = RestAssured.given().auth().oauth2(token).when()
				.get("http://10.0.1.86/snl/rest/v3/board/" + newId + ".json").getBody().asString();

		JSONParser parser = new JSONParser();
		JSONObject jobj = (JSONObject) parser.parse(s);
		JSONObject jobj1 = (JSONObject) jobj.get("response");
		JSONObject jobj2 = (JSONObject) jobj1.get("board");
		JSONArray jarr = (JSONArray) jobj2.get("players");
		JSONObject jobj3 = (JSONObject) jarr.get(0);

		int pId = Integer.parseInt(jobj3.get("id").toString());
		String name = (String) jobj3.get("name");
		System.out.println(pId);
		System.out.println(name);
	}

	/**
	 * Updating details of the player
	 */
	@Test
	public void testingForTheUpdatingDetailsOfPlayer() {

		Response res = (Response) RestAssured.given()
				.parameters("username", "su", "password", "root_pass", "grant_type", "client_credentials", "client_id",
						"f76c0da35f6402b9ac6f8a3dbcf834c9ec994bd30fad0fe348450f57c591272c", "client_secret",
						"c81ac956249ea1ec4aeb2228678e7cde517b95712735431cc5c3b2ca6e016a52")
				.auth().preemptive()
				.basic("f76c0da35f6402b9ac6f8a3dbcf834c9ec994bd30fad0fe348450f57c591272c",
						"c81ac956249ea1ec4aeb2228678e7cde517b95712735431cc5c3b2ca6e016a52")
				.when().post("oauth/token").then().contentType(ContentType.JSON).extract().response();
		System.out.println(res.asString());
		String token = res.jsonPath().getString("access_token");

		int newId = given().auth().oauth2(token).when().get("rest/v3/board/new.json").then().extract().jsonPath()
				.getInt("response.board.id");
		String new_player = " {\"board\":" + newId + ", \"player\":{\"name\": \"NACHI\"}}";
		given().auth().oauth2(token).contentType("application/json").body(new_player).when()
				.post("rest/v3/player.json");
		int playerId = given().auth().oauth2(token).when().get("rest/v3/board/" + newId + ".json").then().extract()
				.jsonPath().getInt("response.board.players[0].id");
		String updated_new_player = " {\"board\":" + newId + ", \"player\":{\"name\": \"NACHIKETA\"}}";
		given().auth().oauth2(token).contentType("application/json").body(updated_new_player).when()
				.put("rest/v3/player/" + playerId + ".json").then().statusCode(200);

	}

	/**
	 * quit player from game and destroy player
	 */
	@Test
	public void testingForDeletingPlayer() {

		Response res = (Response) RestAssured.given()
				.parameters("username", "su", "password", "root_pass", "grant_type", "client_credentials", "client_id",
						"f76c0da35f6402b9ac6f8a3dbcf834c9ec994bd30fad0fe348450f57c591272c", "client_secret",
						"c81ac956249ea1ec4aeb2228678e7cde517b95712735431cc5c3b2ca6e016a52")
				.auth().preemptive()
				.basic("f76c0da35f6402b9ac6f8a3dbcf834c9ec994bd30fad0fe348450f57c591272c",
						"c81ac956249ea1ec4aeb2228678e7cde517b95712735431cc5c3b2ca6e016a52")
				.when().post("oauth/token").then().contentType(ContentType.JSON).extract().response();
		System.out.println(res.asString());
		String token = res.jsonPath().getString("access_token");

		int newId = given().auth().oauth2(token).when().get("rest/v3/board/new.json").then().extract().jsonPath()
				.getInt("response.board.id");
		String new_player = " {\"board\":" + newId + ", \"player\":{\"name\": \"NACHI\"}}";
		given().auth().oauth2(token).contentType("application/json").body(new_player).when()
				.post("rest/v3/player.json");
		int playerId = given().auth().oauth2(token).when().get("rest/v3/board/" + newId + ".json").then().extract()
				.jsonPath().getInt("response.board.players[0].id");
		given().auth().oauth2(token).contentType("application/json").when()
				.delete("rest/v3/player/" + playerId + ".json").then().statusCode(200);
	}

	/**
	 * Testing for roll dice and move player on board
	 */
	@Test
	public void testingForPlayerMovement() {

		Response res = (Response) RestAssured.given()
				.parameters("username", "su", "password", "root_pass", "grant_type", "client_credentials", "client_id",
						"f76c0da35f6402b9ac6f8a3dbcf834c9ec994bd30fad0fe348450f57c591272c", "client_secret",
						"c81ac956249ea1ec4aeb2228678e7cde517b95712735431cc5c3b2ca6e016a52")
				.auth().preemptive()
				.basic("f76c0da35f6402b9ac6f8a3dbcf834c9ec994bd30fad0fe348450f57c591272c",
						"c81ac956249ea1ec4aeb2228678e7cde517b95712735431cc5c3b2ca6e016a52")
				.when().post("oauth/token").then().contentType(ContentType.JSON).extract().response();
		System.out.println(res.asString());
		String token = res.jsonPath().getString("access_token");

		int newId = given().auth().oauth2(token).when().get("rest/v3/board/new.json").then().extract().jsonPath()
				.getInt("response.board.id");
		String new_player = " {\"board\":" + newId + ", \"player\":{\"name\": \"NACHI\"}}";
		given().auth().oauth2(token).contentType("application/json").body(new_player).when()
				.post("rest/v3/player.json");
		int playerId = given().auth().oauth2(token).when().get("rest/v3/board/" + newId + ".json").then().extract()
				.jsonPath().getInt("response.board.players[0].id");
		given().auth().oauth2(token).contentType("application/json").when()
				.get("rest/v3/move/" + newId + ".json?player_id=" + playerId + "").then().statusCode(200);

	}

}
