import java.util.ArrayList;
import java.util.Arrays;


public class Brain {
	
	public static int getMove(int[] board, int offset, int p1Score, int p2Score) {
		// Make the move with be best heuristic (1 level deep)
		GameState state = new GameState(board, p1Score, p2Score);
		
		int bestIndex = -100000;
		int bestValue = -100000;
		for (int i = offset; i < 6 + offset; i++) {
			
			if (board[i] == 0) {
				continue;
			}
			
			GameState nextState = getNextState(state, i);
			int heuristic = getHeuristic(nextState, offset);
			System.out.println("H=" + heuristic);
			if (heuristic > bestValue) {
				bestIndex = i;
				bestValue = heuristic;
			}
			
		}
		System.out.println("Brain returning " + bestIndex + " with h=" + bestValue);
		return bestIndex;

	}
	
	private static ArrayList<GameState> expandState(GameState state, int offset) {
		ArrayList<GameState> list = new ArrayList<GameState>();
		for (int i = offset; i < 6 + offset; i++) {
			if (state.board[i] == 0) {
				continue;
			}
			list.add(getNextState(state, i));
		}
		return list;
	}
	
	private static int min(int[] arr) {
		int res = arr[0];
		for (int i = 1; i < arr.length; i++) {
			if (arr[i] < res) {
				res = arr[i];
			}
		}
		return res;
	}
	
	private static int max(int[] arr) {
		int res = arr[0];
		for (int i = 1; i < arr.length; i++) {
			if (arr[i] > res) {
				res = arr[i];
			}
		}
		return res;
	}
	
	private static int getHeuristic(GameState state, int offset) {
		// Just a dumb one, to begin with
		int sum = 0;
		for (int i = offset; i < 6 + offset; i++) {
			sum += state.board[i];
		}
		if (offset == 6) {
			return state.p2Score - state.p1Score + sum / 2;
		}
		return state.p1Score - state.p2Score + sum / 2;
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
		gs.p1Score = 0;
		gs.p2Score = 0;
		GameState newGS = Brain.getNextState(gs, 1);
		System.out.println(Arrays.toString(gs.board));
		System.out.println("" + Brain.getHeuristic(gs, 6));
		System.out.println("" + Brain.getHeuristic(gs, 0));
		System.out.println(Arrays.toString(newGS.board));
		System.out.println("" + Brain.getHeuristic(newGS, 6));
		System.out.println("" + Brain.getHeuristic(newGS, 0));
		System.out.println(min(new int[]{1,3,40,3,2}));
		ArrayList<GameState> states = expandState(gs, 6);
		System.out.println(states);
	}
}
