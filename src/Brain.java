import java.util.*;


public class Brain {
	
	private final static int TIME_LIMIT_MS = 2600;
	private final static int DEPTH = 17;
	private static long StartTime;

	
	public static int getMove(int[] board, int offset, int p1Score, int p2Score) {
		StartTime = System.nanoTime();

		// Make the move with be best heuristic (1 level deep)
		GameState state = new GameState(board, p1Score, p2Score);
		
		GameState bottomState = state;
		GameState currentState = state;
		
		int counter = 13;
		while(true) {
			if (counter > 10000) {
				break;
			}
			try {
				bottomState = minMax(state, counter, offset);
				currentState = bottomState;	
			} catch (Exception e) {
				break;
			}
			counter++;
		}

		System.out.println("Success on depth: " + (counter-1));

		while(currentState.parent.position != -1) {
			currentState = currentState.parent;
		}
		
		System.out.println("New brain h = " + bottomState.score);
		return currentState.position;

	}
	
	private static GameState minMax(GameState state, int depth, int offset) throws Exception{
		GameState dummyAlpha = new GameState(state.board, 0, 0);
		GameState dummyBeta = new GameState(state.board, 0, 0);
		dummyAlpha.score = Integer.MIN_VALUE;
		dummyBeta.score = Integer.MAX_VALUE;
		
		if (offset == 6) {
			return minMove(state, depth, offset, dummyAlpha, dummyBeta);
		} else {
			return maxMove(state, depth, offset, dummyAlpha, dummyBeta);
		}
	}
		 
	private static GameState maxMove(GameState state, int depth, int offset, GameState alpha, GameState beta) throws Exception {
	  
	  if((System.nanoTime() - StartTime)/1000000 > TIME_LIMIT_MS) {
	  	  throw new Exception();
	  }
	  if (depth == 0) {
		  state.score = getHeuristic(state, depth);
		  return state;
	  }
	  else {
		  ArrayList<GameState> moves = expandState(state, offset);

		  if (moves.size() == 0) {
			  state.score = getHeuristic(state, depth);
			  return state;
		  }

		  Collections.sort(moves, new Comparator<GameState>() {
			  @Override
			  public int compare(GameState g1, GameState g2) {
			  	  return -Integer.compare(g1.score, g2.score);
			  }
		  });

		  GameState bestMove = minMove(moves.get(0), depth - 1, (offset + 6) % 12, alpha, beta);
		  if (bestMove.score > alpha.score) {
			  alpha = bestMove;
		  }
		  if (beta.score <= alpha.score) {
			  return beta;
		  }
		  for (int i = 1; i < moves.size(); i++) {
			  GameState move = minMove(moves.get(i), depth - 1, (offset + 6) % 12, alpha, beta);
			  if (move.score > bestMove.score) {
				  bestMove = move;
			  }
			  if (bestMove.score > alpha.score) {
				  alpha = bestMove;
			  }
			  if (beta.score <= alpha.score) {
				  return beta;
			  }
		
	    }
	    return bestMove;
	  }
	}
		 
	private static GameState minMove(GameState state, int depth, int offset, GameState alpha, GameState beta) throws Exception {
	  	if((System.nanoTime() - StartTime)/1000000 > TIME_LIMIT_MS) {
	  		throw new Exception();
	  	}
		if (depth == 0) {
			  state.score = getHeuristic(state, depth);
			  return state;
		  }
		  else {
			  ArrayList<GameState> moves = expandState(state, offset);
			  if (moves.size() == 0) {
				  state.score = getHeuristic(state, depth);
				  return state;
			  }

			  Collections.sort(moves, new Comparator<GameState>() {
				@Override
				public int compare(GameState g1, GameState g2) {
					return Integer.compare(g1.score, g2.score);
				}
			  });
			  
			  GameState bestMove = maxMove(moves.get(0), depth - 1, (offset + 6) % 12, alpha, beta);
			  if (bestMove.score < beta.score) {
				  beta = bestMove;
			  }
			  if (alpha.score >= beta.score) {
				  return alpha;
			  }
			  for (int i = 1; i < moves.size(); i++) {
				  GameState move = maxMove(moves.get(i), depth - 1, (offset + 6) % 12, alpha, beta);
				  if (move.score < bestMove.score) {
					  bestMove = move;
				  }
				  if (bestMove.score < beta.score) {
					  beta = bestMove;
				  }
				  if (alpha.score >= beta.score) {
					  return alpha;
				  }
				  
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
	
	private static int getHeuristic(GameState state, int depth) {
		int p1Sum = 0;
		int p2Sum = 0;
		int p1FreeSpots = 0;
		int p2FreeSpots = 0;
		int h = 0;
		
		for (int i = 0; i < 6; i++) {
			p1Sum += state.board[i];
			if (state.board[i] == 0) {
				p1FreeSpots++;
			}
		}
		for (int i = 6; i < 12; i++) {
			p2Sum += state.board[i];
			if (state.board[i] == 0) {
				p2FreeSpots++;
			}
		}
		
		// If this is a end state
		if (state.p2Score > 36) {
			h = -10000;
			if(p2FreeSpots == 6) {
				h -= 100;
			}
		} else if (state.p1Score > 36) {
			h = 10000;
			if(p1FreeSpots == 6) {
				h += 100;
			}
		}
		
		h += (state.p1Score + p1Sum / 4) - (state.p2Score + p2Sum / 4);
		return h;

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

		int opponentBeans = 0;
		int ourBeans = 0;
		if (field < 6) {
			for(int i = 0; i < 6; i++) {
				opponentBeans += newState.board[i+6];
				ourBeans += newState.board[i];
			}
		} else {
			for(int i = 0; i < 6; i++) {
				opponentBeans += newState.board[i];
				ourBeans += newState.board[i+6];
			}
		}

		if(opponentBeans == 0) {
			newState.board = new int[]{0,0,0,0,0,0,0,0,0,0,0,0};
			if(field < 6) {
				newState.p1Score += ourBeans;
			} else {
				newState.p2Score += ourBeans;
			}
		}

		newState.score = getHeuristic(newState, 0);
		newState.parent = state;
		return newState;
	}

}
