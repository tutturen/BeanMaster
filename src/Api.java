import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;


public class Api {

	private final static String API_URL = "http://drachten.informatik.uni-mannheim.de/api";
	public static final String GAME_CAN_BE_STARTED = "0";
	public static final String YOUR_TURN = "-1";
	public static final String GAME_FINISHED = "-2";
	public static final String NOT_YOUR_TURN = "-3";
	public static final String MISSING_PLAYER_2 = "-4";
	
	public static String fetch(String url) throws Exception {
		URI uri = new URI(API_URL + url.replace(" ", ""));
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(uri.toURL().openConnection().getInputStream()));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = bufferedReader.readLine()) != null) {
			sb.append(line);
		}
		bufferedReader.close();
		return (sb.toString());
	}
	
	public static int getMoveState(String gameID, String playerName) throws Exception {
		int ret = Integer.parseInt(Api.fetch("/check/" + gameID + "/" + playerName));
		System.out.println("" + ret);
		return ret;
	}
	
	public static String getStatesMessage(String gameID) throws Exception {
		return Api.fetch("/statemsg/" + gameID);
	}
	
	public static int getStateId(String gameID) throws Exception {
		return Integer.parseInt(Api.fetch("/state/" + gameID));
	}
	
	public static String move(String gameID, int fieldID, String playerName) throws Exception {
		return Api.fetch("/move/" + gameID + "/" + playerName + "/" + fieldID);
	}

	public static boolean joinGame(String gameID, String playerName) throws Exception {
		String state = Api.fetch("/joingame/" + gameID + "/" + playerName);
		System.out.println("Join-Game-State: " + state);
		if (state.equals("1")) {
			return true;
		} else if (state.equals("0")) {
			System.out.println("Game has status 'waiting'");
		}
		return false;
	}
	
	public static String[] getOpenGames() throws Exception {
		return Api.fetch("/opengames").split(";");
	}

	public static boolean canStartGame(String gameID, String playerName) throws Exception {
		String status = Integer.toString(Api.getMoveState(gameID, playerName));
		System.out.println(status);
		if (status.equals(GAME_CAN_BE_STARTED) || status.equals(YOUR_TURN) || status.equals(NOT_YOUR_TURN)) {
			System.out.println("Game ready: " + gameID);
			return true;
		} else if (status.equals(GAME_FINISHED)) {
			System.out.println("Timeout. Game is finished.");
			return false;
		} else if (status.equals(MISSING_PLAYER_2)) {
			System.out.println("Missing player 2.");
		}
		
		return false;
	}
	
	public static String createGame(String playerName) throws Exception {
		String gameID = Api.fetch("/creategame/" + playerName);
		while (true) {
			Thread.sleep(3000);
			String state = Api.fetch("/check/" + gameID + "/" + playerName);
			if (state.equals(GAME_CAN_BE_STARTED) || state.equals(YOUR_TURN)) {
				System.out.println("Game ready: " + gameID);
				return gameID;
			} else if (state.equals(GAME_FINISHED)) {
				System.out.println("Timeout. Game is finished.");
				return "0";
			} else if (state.equals(MISSING_PLAYER_2)) {
				System.out.println("Missing player 2.");
			}
		}
	}
	
	
}
