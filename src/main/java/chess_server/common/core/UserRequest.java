package chess_server.common.core;

import org.json.simple.JSONObject;

public interface UserRequest {
	public JSONObject move(Player player, String srcTile, String destTile);
	public JSONObject select(Player player, String tile);
	public JSONObject surrender(Player player);
	public boolean isCheckmate();
	public boolean isCheck();
	// return black or white
	public String whoWin();
}
