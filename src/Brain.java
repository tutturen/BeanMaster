import java.util.Arrays;


public class Brain {
	
	public static int getMove(int[] board, int offset, int p1Score, int p2Score) {
		System.out.println("BRAIN: " + Arrays.toString(board));
		System.out.println("BRAN: offset=" + offset);
		for (int i = 0; i < board.length / 2; i++) {
			if (board[i+offset] != 0) {
				System.out.println("Brain returning " + (i + offset));
				return i + offset;
			}
		}
		return offset;
	}
	
	private static int getHeuristic(GameState state, int offset) {
		// Just a dumb one, to begin with
		int sum = 0;
		for (int i = offset; i < 6 + offset; i++) {
			sum += state.board[i];
		}
		if (offset == 6) {
			return state.p2Score - state.p1Score + sum;
		}
		return state.p1Score - state.p2Score + sum;
	}
	
	private static GameState getNextState(GameState state, int field) {
		GameState newState = new GameState();
		newState.board = new int[12];
		newState.p1Score = state.p1Score + 0;
		newState.p2Score = state.p2Score + 0;
		System.arraycopy(state.board, 0, newState.board, 0, state.board.length);
		
		int startField = field;

		int value = newState.board[field];
		newState.board[field] = 0;
		while (value > 0) {
			field = (++field) % 12;
			newState.board[field]++;
			value--;
		}

		if ((newState.board[field] == 2) || (newState.board[field] == 4) || (newState.board[field] == 6)) {
			do {
				if (startField < 6) {
					newState.p1Score += newState.board[field];
				} else {
					newState.p2Score += newState.board[field];
				}
				newState.board[field] = 0;
				field = (field == 0) ? 11 : --field;
			} while ((newState.board[field] == 2) || (newState.board[field] == 4) || (newState.board[field] == 6));
		}

		return newState;
	}
	
	public static void main(String[] args) {
		GameState gs = new GameState();
		gs.board = new int[]{ 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6 };
		gs.p1Score = 1;
		gs.p2Score = 1;
		GameState newGS = Brain.getNextState(gs, 1);
		System.out.println(Arrays.toString(gs.board));
		System.out.println("" + Brain.getHeuristic(gs, 6));
		System.out.println("" + Brain.getHeuristic(gs, 0));
		System.out.println(Arrays.toString(newGS.board));
		System.out.println("" + Brain.getHeuristic(newGS, 6));
		System.out.println("" + Brain.getHeuristic(newGS, 0));
	}
}
