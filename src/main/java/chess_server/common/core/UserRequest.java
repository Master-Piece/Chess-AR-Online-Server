package chess_server.common.core;

public interface UserRequest {
	public String move(Player player, String srcTile, String destTile);
	public String select(Player player, String tile);
	public String surrender(Player player);
	public boolean isEnd();
	
	// return black or white
	public String whoWin();
}
