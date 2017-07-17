
import static com.jayway.restassured.RestAssured.given;
import java.io.IOException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;

/**
 * 
 * @author nachiketatripathi
 *
 */
public class SnLServicesV2Test {

	@BeforeTest
	public void setBaseUri() {
		RestAssured.baseURI = "http://10.0.1.86/snl/";
	}

	@Test
	public void testingStatusCodeJSON() {
		RestAssured.given().param("response.status", "1").authentication().basic("su", "root_pass").when()
				.get("http://10.0.1.86/snl/rest/v2/board.json").then().assertThat().statusCode(200);
	}

	/**
	 * Getting id's of all the boards
	 * 
	 * @throws IOException
	 * @throws org.json.simple.parser.ParseException
	 */
	@Test
	public void testingListOfTheBoards() throws IOException, org.json.simple.parser.ParseException {
		String s = RestAssured.given().authentication().basic("su", "root_pass").when()
				.get("http://10.0.1.86/snl/rest/v2/board.json").getBody().asString();
		// System.out.println(s);

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
		String newId = RestAssured.given().authentication().basic("su", "root_pass").when()
				.get("http://10.0.1.86/snl/rest/v2/board/new.json").getBody().asString();

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

		int newId = given().authentication().basic("su", "root_pass").when().get("rest/v2/board/new.json").then()
				.extract().jsonPath().getInt("response.board.id");
		System.out.println(newId);
		Response res = given().authentication().basic("su", "root_pass").when().contentType(ContentType.JSON)
				.delete("/rest/v2/board/" + newId + ".json");
		Assert.assertEquals(res.statusCode(), 200);

	}

	/**
	 * Adding new player
	 */
	@Test
	public void testingForAddingNewPlayer() {
		int newId = given().authentication().basic("su", "root_pass").when().get("rest/v2/board/new.json").then()
				.extract().jsonPath().getInt("response.board.id");
		System.out.println(newId);
		String new_player = " {\"board\":" + newId + ", \"player\":{\"name\": \"NACHI\"}}";
		given().authentication().basic("su", "root_pass").contentType("application/json").body(new_player).when()
				.post("rest/v2/player.json").then().statusCode(200);
	}

	/**
	 * Testing player details
	 * 
	 * @throws ParseException
	 */
	@Test
	public void testingPlayerDetail() throws ParseException {
		int newId = given().authentication().basic("su", "root_pass").when().get("rest/v2/board/new.json").then()
				.extract().jsonPath().getInt("response.board.id");
		String new_player = " {\"board\":" + newId + ", \"player\":{\"name\": \"Rishabh\"}}";
		given().authentication().basic("su", "root_pass").contentType("application/json").body(new_player).when()
				.post("rest/v2/player.json");
		String s = RestAssured.given().authentication().basic("su", "root_pass").when()
				.get("http://10.0.1.86/snl/rest/v2/board/" + newId + ".json").getBody().asString();

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
		int newId = given().authentication().basic("su", "root_pass").when().get("rest/v2/board/new.json").then()
				.extract().jsonPath().getInt("response.board.id");
		String new_player = " {\"board\":" + newId + ", \"player\":{\"name\": \"NACHI\"}}";
		given().contentType("application/json").body(new_player).when().post("rest/v1/player.json");
		int playerId = given().authentication().basic("su", "root_pass").when().get("rest/v2/board/" + newId + ".json")
				.then().extract().jsonPath().getInt("response.board.players[0].id");
		String updated_new_player = " {\"board\":" + newId + ", \"player\":{\"name\": \"NACHIKETA\"}}";
		given().authentication().basic("su", "root_pass").contentType("application/json").body(updated_new_player)
				.when().put("rest/v2/player/" + playerId + ".json").then().statusCode(200);

	}

	/**
	 * quit player from game and destroy player
	 */
	@Test
	public void testingForDeletingPlayer() {
		int newId = given().authentication().basic("su", "root_pass").when().get("rest/v2/board/new.json").then()
				.extract().jsonPath().getInt("response.board.id");
		String new_player = " {\"board\":" + newId + ", \"player\":{\"name\": \"NACHI\"}}";
		given().authentication().basic("su", "root_pass").contentType("application/json").body(new_player).when()
				.post("rest/v2/player.json");
		int playerId = given().authentication().basic("su", "root_pass").when().get("rest/v2/board/" + newId + ".json")
				.then().extract().jsonPath().getInt("response.board.players[0].id");
		given().authentication().basic("su", "root_pass").contentType("application/json").when()
				.delete("rest/v2/player/" + playerId + ".json").then().statusCode(200);
	}

	/**
	 * Testing for roll dice and move player on board
	 */
	@Test
	public void testingForPlayerMovement() {
		int newId = given().authentication().basic("su", "root_pass").when().get("rest/v2/board/new.json").then()
				.extract().jsonPath().getInt("response.board.id");
		String new_player = " {\"board\":" + newId + ", \"player\":{\"name\": \"NACHI\"}}";
		given().authentication().basic("su", "root_pass").contentType("application/json").body(new_player).when()
				.post("rest/v2/player.json");
		int playerId = given().authentication().basic("su", "root_pass").when().get("rest/v2/board/" + newId + ".json")
				.then().extract().jsonPath().getInt("response.board.players[0].id");
		given().authentication().basic("su", "root_pass").contentType("application/json").when()
				.get("rest/v2/move/" + newId + ".json?player_id=" + playerId + "").then().statusCode(200);

	}

}
