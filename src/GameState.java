import java.util.ArrayList;
import java.util.Arrays;


public class GameState {
	
	public GameState() {
		
	}
	
	public GameState(int[] board, int p1Score, int p2Score) {
		this.board = board;
		this.p1Score = p1Score;
		this.p2Score = p2Score;
		
	}

	public int[] board;
	public int p1Score, p2Score;
	public int score = Integer.MIN_VALUE;
	public ArrayList<GameState> children;
	public int position = -1;
	public GameState parent = null;
	
	@Override
	public String toString() {
		return "[GameState:\n\tboard="
				+ Arrays.toString(board)
				+ "\n\tp1=" + p1Score
				+ "\n\tp2=" + p2Score
				+ "\n\tscore=" + score
				+ "\n\tposition=" + position
				+ "\n\tparent=" + parent
				+ "]";
	}
	
	public GameState getBestChild() {
		if (children == null || children.size() == 0) {
			return null;
		}
		int score = Integer.MIN_VALUE;
		GameState gs = null;
		for (GameState state : children) {
			if (state.score > score) {
				gs = state;
				score = state.score;
			}
		}
		return gs;
	}
	
	public GameState getWorstChild() {
		if (children == null || children.size() == 0) {
			return null;
		}
		int score = Integer.MAX_VALUE;
		GameState gs = null;
		for (GameState state : children) {
			if (state.score < score) {
				gs = state;
				score = state.score;
			}
		}
		return gs;
	}
	
}
