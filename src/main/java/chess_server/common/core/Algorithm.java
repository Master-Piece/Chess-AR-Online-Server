package chess_server.common.core;

import org.json.simple.JSONObject;

public class Algorithm implements UserRequest {
	private String[][] board ={
			{"WR1","WN1","WB1","WQ","WK","WB2","WN2","WR2"},
			{"WP1","WP2","WP3","WP4","WP5","WP6","WP7","WP8"},
			{},
			{},
			{},
			{},
			{"BP8","BP7","BP6","BP5","BP4","BP3","BP2","BP1"},
			{"BR2","BN2","BB2","BQ","BK","BB1","BN1","BR1"}};
	
	
	private String readMap(String tile){
		int y = tile.charAt(0) - 'a', x = tile.charAt(1) - '0';		
		return board[x][y];
	}
	
	public String move(Player player, String srcTile, String destTile) {
		String color = player.getColor();
		
		String src_unit = readMap(srcTile);
		
		String dest_unit = readMap(destTile);
		
		return null;
	}

	@Override
	public JSONObject select(Player player, String tile) {
		JSONObject message = new JSONObject();
		String unit = readMap(tile);
		char color = player.getColor().charAt(0);
		if( color == unit.charAt(0)){
			
		}
		else{
			
		}
			
			
		return message;
	}

	@Override
	public String surrender(Player player) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEnd() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String whoWin() {
		// TODO Auto-generated method stub
		return null;
	}

}
