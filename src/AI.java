
public class AI {
	private static String PLAYER_NAME = "NOOB-AI";
	private static int p1Score = 0;
	private static int p2Score = 0;
	
	public static void main(String[] args) throws Exception {
		printOpenGames();
		
		/*String gameID = Api.createGame(PLAYER_NAME);
		if (!gameID.equals("0")) {
			play(gameID, 0);
		}*/
		String gameID = "74";
		if (Api.joinGame(gameID, PLAYER_NAME)) {
			play(gameID, 6);
		}
	}

	static void printOpenGames() throws Exception {
		for (String game : Api.getOpenGames()) {
			System.out.println(game);
		}
	}

	static void play(String gameID, int offset) throws Exception {
		System.out.println("Playing game: " + gameID);
		int[] board = { 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6 }; // position 1-12
		int start, end;
		if (offset == 0) {
			start = 7;
			end = 12;
		} else {
			start = 1;
			end = 6;
		}

		while (true) {
			Thread.sleep(2000);
			int moveState = Api.getMoveState(gameID, PLAYER_NAME);
			int stateID = Api.getStateId(gameID);
			System.out.println("moveState:" + moveState);
			System.out.println("stateId:" + stateID);

			if ((stateID != 2) && (((start <= moveState) && (moveState <= end)) || (moveState == -1))) {
				if (moveState != -1) {
					int selectedField = moveState - 1;
					board = updateBoard(board, selectedField);
					System.out.println("Opponent chose: " + moveState +
					" /\t" + p1Score + " - " + p2Score);
					System.out.println(printBoard(board) + "\n");
				}

				/*
				 * HERE GOES THE AI CODE
				 * 
				 */
				int selectField;
				System.out.println("Finde Zahl: ");
				do {
					selectField = (int) (Math.random() * 6) + offset;
					System.out.println("\t-> " + selectField );
				} while (board[selectField] == 0);

				board = updateBoard(board, selectField);
				System.out.println("Choose field: " + (selectField + 1) +
				" /\t" + p1Score + " - " + p2Score);
				System.out.println(printBoard(board) + "\n\n");

				Api.move(gameID, selectField + 1, PLAYER_NAME);
			} else if ((moveState == -2) || (stateID == 2)) {
				System.out.println("GAME Finished");
				System.out.println(Api.getStatesMessage(gameID));
				return;
			} else {
				System.out.println("- " + moveState + "\t\t" +
				Api.getStatesMessage(gameID));
			}

		}
	}

	static String printBoard(int[] board) {
		String s = "";
		for (int i = 11; i >= 6; i--) {
			if (i != 6) {
				s += board[i] + "; ";
			} else {
				s += board[i];
			}
		}

		s += "\n";
		for (int i = 0; i <= 5; i++) {
			if (i != 5) {
				s += board[i] + "; ";
			} else {
				s += board[i];
			}
		}

		return s;
	}

	static int[] updateBoard(int[] board, int field) {
		int startField = field;

		int value = board[field];
		board[field] = 0;
		while (value > 0) {
			field = (++field) % 12;
			board[field]++;
			value--;
		}

		if ((board[field] == 2) || (board[field] == 4) || (board[field] == 6)) {
			do {
				if (startField < 6) {
					p1Score += board[field];
				} else {
					p2Score += board[field];
				}
				board[field] = 0;
				field = (field == 0) ? field = 11 : --field;
			} while ((board[field] == 2) || (board[field] == 4) || (board[field] == 6));
		}
		return board;
	}
}