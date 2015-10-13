import java.util.ArrayList;


public class Brain {
	
	public static int getMove(int[] board, int offset, int p1Score, int p2Score) {
		// Make the move with be best heuristic (1 level deep)
		GameState state = new GameState(board, p1Score, p2Score);
		final int DEPTH = 13;
		GameState bottomState = minMax(state, DEPTH, offset);
		System.out.println("SCORE: " + bottomState.score);
		
		int position = -2;
		while (true) {
			if (bottomState.parent == null) {
				break;
			}
			bottomState = bottomState.parent;
			if (bottomState.position == -1) {
				break;
			}
			position = bottomState.position;
		}
		if (position == -2) {
			position = getBestGreedyMove(state, offset);
		}
		System.out.println("Brain returning " + position);
		return position;

	}
	
	private static int getBestGreedyMove(GameState state, int offset) {
		int h = Integer.MIN_VALUE;
		GameState chosenState = null;
		for (GameState s : expandState(state, offset)) {
			int heur = getHeuristic(state, offset);
			if (heur > h) {
				chosenState = s;
				h = heur;
			}
		}
		return chosenState.position;
	}
	
	
	private static GameState minMax(GameState state, int depth, int offset) {
		return maxMove(state, depth, offset);
	}
		 
	private static GameState maxMove(GameState state, int depth, int offset) {
	  if (depth == 0) {
		  state.score = getHeuristic(state, offset);
		  return state;
	  }
	  else {
		  GameState bestMove = null;
		  ArrayList<GameState> moves = expandState(state, offset);
		  for (GameState move : moves) {
			  move = minMove(move, depth - 1, offset);
			  if (move == null) {
				  continue;
			  }
			  if (bestMove == null || move.score > bestMove.score) {
				  bestMove = move;
			}
	    }
		if (bestMove == null) {
			state.score = getHeuristic(state, offset);
			return state;
		}
	    return bestMove;
	  }
	}
		 
	private static GameState minMove(GameState state, int depth, int offset) {
		  if (depth == 0) {
			  state.score = getHeuristic(state, offset);
			  return state;
		  }
		  else {
			  GameState bestMove = null;
			  ArrayList<GameState> moves = expandState(state, offset);
			  for (GameState move : moves) {
				  move = maxMove(move, depth - 1, offset);
				  if (move == null) {
					  continue;
				  }
				  if (bestMove == null || move.score < bestMove.score) {
					  bestMove = move;
				}
		    }
			if (bestMove == null) {
				state.score = getHeuristic(state, offset);
				return state;
			}
		    return bestMove;
		  }
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
	
	private static int getHeuristic(GameState state, int offset) {
		// If this is a end state
		if (state.p2Score > 36) {
			return (offset == 6) ? 10000 : -10000;
		} else if (state.p1Score > 36) {
			return (offset == 6) ? -10000 : 10000;
		}
		
		// Just a dumb one, to begin with
		int sum = 0;
		for (int i = offset; i < 6 + offset; i++) {
			sum += state.board[i];
		}
		if (offset == 6) {
			return state.p2Score - state.p1Score + sum / 4;
		}
		return state.p1Score - state.p2Score + sum / 4;
	}
	
	private static GameState getNextState(GameState state, int field) {
		GameState newState = new GameState();
		newState.board = new int[12];
		newState.p1Score = state.p1Score + 0;
		newState.p2Score = state.p2Score + 0;
		newState.position = field;
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
		newState.parent = state;
		return newState;
	}
	
	public static void main(String[] args) {
		GameState gs = new GameState(new int[]{ 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6 }, 0, 0);
		//System.out.println(minMax(gs, 5, 6));
	}
}
