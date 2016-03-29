package chess_server.common.core;

import org.json.simple.JSONArray;
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
	
	private String getUnit(int x, int y){
		return board[x][y];
	}
	
	private String getUnit(String tile){
		int x[]= getPosition(tile);
		return board[x[0]][x[1]];
	}
	
	private int[] getPosition(String tile){
		int x[] = {tile.charAt(1) - '1' , tile.charAt(0) - 'a'};
		return x;
	}
	
	private boolean isInRange(int x, int y){
		if(x>= 0 && x <= 7 && y >= 0 && y <= 7) return true;
		return false;
	}
	
	private String getTile(int x, int y){
		String tile="";
		char a = '1',b = 'a';
		a += x;
		b += y;
		tile = tile + b + a;
		return tile;
	}
	
	
	private JSONArray getKingMove(int x,int y){
		JSONArray moves = new JSONArray();
		char color = getUnit(x,y).charAt(0);
		int dx[] = {1, 1, 0, -1, -1, -1, 0, 1};
		int dy[] = {0, 1, 1, 1, 0, -1, -1, -1};
		
		int i, nx, ny;
		String unit;
		
		for(i=0;i<8;i++){
			nx = x + dx[i];
			ny = y + dy[i];
			
			if(!isInRange(nx,ny)) continue;
			unit = getUnit(nx,ny);
			
			if(unit.isEmpty()){
				moves.add(getTile(nx,ny));
			}
			else{
				// (nx,ny)에 내 말이 없는지, 또 적 킹이 없는지 확인 --> 이동 가능 범위에 킹이 있으면 체크메이트 상태이므로, 무브가 될 수 없다.
				if(getUnit(nx,ny).charAt(0) == color) continue; //같은 플레이어의 말, 넘김
				moves.add(getTile(nx,ny)); 
			}
		}
		return moves;
	}
	
	private JSONArray getQueenMove(int x,int y){
		JSONArray moves = new JSONArray();
		int i,j, nx, ny;
		int dx[] = {1, 1, 0, -1, -1, -1, 0, 1};
		int dy[] = {0, 1, 1, 1, 0, -1, -1, -1};
		String unit;
		char color = getUnit(x,y).charAt(0);
		
		
		for(i=0;i<8;i++){ //방향
			for(j=1;j<=8;j++){  //길이
				nx = x + dx[i]*j;
				ny = y + dy[i]*j;
				
				if(!isInRange(nx,ny)) break; //보드 범위 밖이므로 더이상 체크 하지 않는다
				unit = getUnit(nx,ny);
				
				if(unit.isEmpty()){
					moves.add(getTile(nx,ny));
				}
				else{
					// (nx,ny)에 내 말이 없는지, 또 적 킹이 없는지 확인 --> 이동 가능 범위에 킹이 있으면 체크메이트 상태이므로, 무브가 될 수 없다.
					if(getUnit(nx,ny).charAt(0) != color) moves.add(getTile(nx,ny)); 
					break;
				}
			}
		}
		
		return moves;
	}
	
	private JSONArray getBishopMove(int x,int y){
		JSONArray moves = new JSONArray();
		int dx[] = {1,-1,-1,1};
		int dy[] = {1,1,-1,-1};
		int i,j,nx,ny;
		String unit;
		char color = getUnit(x,y).charAt(0);
		
		
		for(i=0;i<4;i++){  //방향
			for(j=1;j<=8;j++){ //길이
				nx = x + dx[i]*j;
				ny = y + dy[i]*j;
				if(!isInRange(nx,ny)) break;
				unit = getUnit(nx,ny);
				
				if(unit.isEmpty()){
					moves.add(getTile(nx,ny));
				}
				else{
					// (nx,ny)에 내 말이 없는지, 또 적 킹이 없는지 확인 --> 이동 가능 범위에 킹이 있으면 체크메이트 상태이므로, 무브가 될 수 없다.
					if(getUnit(nx,ny).charAt(0) != color) moves.add(getTile(nx,ny)); 
					break;
				}
				
			}
		}
		return moves;
	}
	
	private JSONArray getRookMove(int x,int y){
		JSONArray moves = new JSONArray();
		int dx[] = {1,0,-1,0};
		int dy[] = {0,1,0,-1};
		int i,j,nx,ny;
		String unit;
		char color = getUnit(x,y).charAt(0);
		
		
		for(i=0;i<4;i++){  //방향
			for(j=1;j<=8;j++){ //길이
				nx = x + dx[i]*j;
				ny = y + dy[i]*j;
				if(!isInRange(nx,ny)) break;
				unit = getUnit(nx,ny);
				
				if(unit.isEmpty()){
					moves.add(getTile(nx,ny));
				}
				else{
					// (nx,ny)에 내 말이 없는지, 또 적 킹이 없는지 확인 --> 이동 가능 범위에 킹이 있으면 체크메이트 상태이므로, 무브가 될 수 없다.
					if(getUnit(nx,ny).charAt(0) != color) moves.add(getTile(nx,ny)); 
					break;
				}
				
			}
		}
		return moves;
	}
	
	private JSONArray getKnightMove(int x,int y){
		JSONArray moves = new JSONArray();
		int dx[] = {2, 1, -1, -2, -2, -1, 1, 2};
		int dy[] = {1, 2, 2, 1, -1, -2, -2, -1};
		int i,j,nx,ny;
		String unit;
		char color = getUnit(x,y).charAt(0);
			
		for(i=0;i<8;i++){  //방향
			nx = x + dx[i];
			ny = y + dy[i];
			if(!isInRange(nx,ny)) continue;
			unit = getUnit(nx,ny);
			
			if(unit.isEmpty()){
				moves.add(getTile(nx,ny));
			}
			else{
				// (nx,ny)에 내 말이 없는지, 또 적 킹이 없는지 확인 --> 이동 가능 범위에 킹이 있으면 체크메이트 상태이므로, 무브가 될 수 없다.
				if(getUnit(nx,ny).charAt(0) != color) moves.add(getTile(nx,ny)); 
			}
		}
		return moves;
	}
	
	private JSONArray getPawnMove(int x,int y){
		/*2칸 전진, 대각선에 적 기물 잡기, 1칸 전진*/
		JSONArray moves = new JSONArray();
		String unit = getUnit(x,y);
		String oneFrontUnit = "", twoFrontUnit = "", crossUnit;
		char color = unit.charAt(0);
		if(color == 'W'){
			//전진 체크
			if(isInRange(x+1,y)) oneFrontUnit = getUnit(x+1,y);
			if(oneFrontUnit.isEmpty()){
				moves.add(getTile(x+1,y));
				if(x == 1){ //2칸 전진 가능
					twoFrontUnit = getUnit(x+2,y);
					if(twoFrontUnit.isEmpty()) moves.add(getTile(x+2,y));
				}
			}
			//대각체크
			if(isInRange(x+1,y-1)){
				crossUnit = getUnit(x+1,y-1);
				if(color != crossUnit.charAt(0)) moves.add(getTile(x+1,y-1));
			}
			if(isInRange(x+1,y+1)){
				crossUnit = getUnit(x+1,y+1);
				if(color != crossUnit.charAt(0)) moves.add(getTile(x+1,y+1));			
			}			
		}
		else{
			if(isInRange(x-1,y)) oneFrontUnit = getUnit(x-1,y);
			if(oneFrontUnit.isEmpty()){
				moves.add(getTile(x-1,y));
				if(x == 6){ //2칸 전진 가능
					twoFrontUnit = getUnit(x-2,y);
					if(twoFrontUnit.isEmpty()) moves.add(getTile(x-2,y));
				}
			}
			//대각체크
			if(isInRange(x-1,y-1)){
				crossUnit = getUnit(x-1,y-1);
				if(color != crossUnit.charAt(0)) moves.add(getTile(x-1,y-1));
			}
			if(isInRange(x-1,y+1)){
				crossUnit = getUnit(x-1,y+1);
				if(color != crossUnit.charAt(0)) moves.add(getTile(x-1,y+1));		
			}	
		}
		return moves;
	}
	
	
	
	private JSONArray getMovable(String tile){
		JSONArray moves = new JSONArray();
		
		int position[] = getPosition(tile);
		int x = position[0];
		int y = position[1];
		String unit = getUnit(tile);
		
		char c = unit.charAt(1);
		if(c ==  'K'){
			moves = getKingMove(x,y);
		}
		else if(c == 'Q'){
			moves = getQueenMove(x,y);
		}
		else if(c == 'B'){
			moves = getBishopMove(x,y);
		}
		else if(c == 'R'){
			moves = getRookMove(x,y);
		}
		else if(c == 'N'){
			moves = getKnightMove(x,y);
		}
		else if(c == 'P'){
			moves = getPawnMove(x,y);
		}
		return moves;
	}
	
	
	public JSONObject move(Player player, String srcTile, String destTile) {
		JSONObject message = new JSONObject();
		
		char color = player.getColor().charAt(0);
		color = Character.toUpperCase(color);
		int src_position[] = getPosition(srcTile); 
		String src_unit = getUnit(srcTile);
		
		String dest_unit = getUnit(destTile);
		int dest_position[] = getPosition(destTile);
		
		message.put("type", "MOVE_SUCCESS");
		message.put("srcPiece",src_unit);
		message.put("destTile", destTile);
		message.put("targetPiece", dest_unit);
		return message;
	}
	
	@Override
	public JSONObject select(Player player, String tile) {
		JSONObject message = new JSONObject();
		String unit = getUnit(tile);
		char color = player.getColor().charAt(0);
		color = Character.toUpperCase(color);
		
		if( color == unit.charAt(0)){
			//이동가능한 타일들 모아서 보내줌
			JSONArray moves = getMovable(tile);
			
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
