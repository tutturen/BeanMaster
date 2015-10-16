
public class AI {
	private static String PlayerName = "NichtSoEinsamerSpieler";
	private static int p1Score = 0;
	private static int p2Score = 0;
	
	public static void main(String[] args) throws Exception {
		if (args.length == 3) {
			PlayerName = args[1];
			boolean status = Api.canStartGame(args[0], PlayerName);
			if (status) {
				play(args[0], Integer.parseInt(args[2]));
			} else {
				System.out.println("Game not started.");
			}

		} else if (args.length == 1 && args[0].charAt(0) == 'c') {
			PlayerName = "EinsamerSpieler";
			String gameID = Api.createGame(PlayerName);
			if (!gameID.equals("0")) {
				play(gameID, 0);
			}
			
		} else {
			printOpenGames();
			String[] openGames = Api.getOpenGames();
			if (openGames[0].length() > 0) {
				System.out.println("GAME-ID: " + openGames[0]);
				boolean game = Api.joinGame(openGames[0], PlayerName);
				if (game) {
					play(openGames[0], 6);
				}
			} else {
				String gameID = Api.createGame(PlayerName);
				if (!gameID.equals("0")) {
					play(gameID, 0);
				}
			}
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
			Thread.sleep(20);
			int moveState = Api.getMoveState(gameID, PlayerName);
			int stateID = Api.getStateId(gameID);
			//System.out.println("moveState:" + moveState);
			//System.out.println("stateId:" + stateID);

			if ((stateID != 2) && (((start <= moveState) && (moveState <= end)) || (moveState == -1))) {
				if (moveState != -1) {
					int selectedField = moveState - 1;
					board = updateBoard(board, selectedField);
					//System.out.println("Opponent chose: " + moveState);
					//printScore();
					//printBoard(board);
				}

				/*
				 * HERE GOES THE AI CODE
				 * 
				 */
				int selectedField = Brain.getMove(board, offset, p1Score, p2Score);
				/*
				 * AND THE AI CODE IS DONE
				 */
				Api.move(gameID, selectedField + 1, PlayerName);
				board = updateBoard(board, selectedField);
				//System.out.println("Choose field: " + (selectedField + 1));
				//printScore();
				//printBoard(board);

			} else if ((moveState == -2) || (stateID == 2)) {
				System.out.println("GAME Finished");
				System.out.println(Api.getStatesMessage(gameID));
				return;
			} else {
				//System.out.println("- " + moveState + "\t\t" +
				//Api.getStatesMessage(gameID));
			}

		}
	}
	
	static void printScore() {
		System.out.println("Game score: " + p1Score + " - " + p2Score);
	}

	static void printBoard(int[] board) {
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

		System.out.println(s);
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
