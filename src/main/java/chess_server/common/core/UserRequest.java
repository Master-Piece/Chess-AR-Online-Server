package chess_server.common.core;

public interface UserRequest {
	public String move(String srcTile, String destTile);
	public String select(String tile, Player player);
	public String surrender(Player player);
	public boolean isEnd();
	
	// return black or white
	public String whoWin();
}
