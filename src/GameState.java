
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
	
}