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
	
	
	private String getUnit(String tile){
		int x[]= getPosition(tile);
		return board[x[0]][x[1]];
	}
	
	private int[] getPosition(String tile){
		int x[] = {tile.charAt(1) - '0',  tile.charAt(0) - 'a'};
		return x;
	}
	
	public JSONObject move(Player player, String srcTile, String destTile) {
		String color = player.getColor();
		
		String src_unit = getUnit(srcTile);
		
		String dest_unit = getUnit(destTile);
		
		return null;
	}

	private String getMovable(String tile){
		String moves = "[";
		int position[] = getPosition(tile);
		String unit = getUnit(tile);
		
		
		moves += "]";
		return moves;
	}
	
	@Override
	public JSONObject select(Player player, String tile) {
		JSONObject message = new JSONObject();
		String unit = getUnit(tile);
		char color = player.getColor().charAt(0);
		color = Character.toUpperCase(color);
		
		if( color == unit.charAt(0)){
			//이동가능한 타일들 모아서 보내줌
			String moves = getMovable(tile);
			
			message.put("type", "SELECT_SUCCESS");
			message.put("piece",  unit);
			message.put("tiles", moves);
			message.put("error", "");
		}
		else{
			message.put("type", "SELECT_FAILED");
			message.put("piece",  "");
			message.put("tiles", "");
			message.put("error", "");
		}
			
			
		return message;
	}

	@Override
	public JSONObject surrender(Player player) {
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
