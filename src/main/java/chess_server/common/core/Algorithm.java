package chess_server.common.core;

import org.json.simple.JSONArray;

import org.json.simple.JSONObject;



public class Algorithm implements UserRequest {
	class Piece{
		char unit;
		String name;
		boolean isFirstMove;
		boolean enPassant;
		boolean pawnTwoMoved;
		char color;
		
		Piece(String name){
			this.name = name;
			this.unit = name.charAt(1);
			isFirstMove = true;
			enPassant = false;
			color = name.charAt(0);
			pawnTwoMoved = false;
		}
	}
	
	private Piece[][] board = {
			{new Piece("WR1"),new Piece("WN1"),new Piece("WB1"),new Piece("WQ"),new Piece("WK"),new Piece("WB2"),new Piece("WN2"),new Piece("WR2")},
			{new Piece("WP1"),new Piece("WP2"),new Piece("WP3"),new Piece("WP4"),new Piece("WP5"),new Piece("WP6"),new Piece("WP7"),new Piece("WP8")},
			{},
			{},
			{},
			{},
			{new Piece("BP8"),new Piece("BP7"),new Piece("BP6"),new Piece("BP5"),new Piece("BP4"),new Piece("BP3"),new Piece("BP2"),new Piece("BP1")},
			{new Piece("BR2"),new Piece("BN2"),new Piece("BB2"),new Piece("BQ"),new Piece("BK"),new Piece("BB1"),new Piece("BN1"),new Piece("BR1")}};

	
	private Piece getPiece(int x, int y){
		return board[x][y];
	}
	
	private Piece getPiece(String tile){
		int x[]= getPosition(tile);
		return board[x[0]][x[1]];
	}
	
	private int[] getPosition(String tile){
		int x[] = {tile.charAt(1) - '1' , tile.charAt(0) - 'A'};
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
		char color = getPiece(x,y).color;
		int dx[] = {1, 1, 0, -1, -1, -1, 0, 1};
		int dy[] = {0, 1, 1, 1, 0, -1, -1, -1};
		
		int i, nx, ny;
		Piece unit;
		
		for(i=0;i<8;i++){
			nx = x + dx[i];
			ny = y + dy[i];
			
			if(!isInRange(nx,ny)) continue;
			unit = getPiece(nx,ny);
			
			if(unit == null){
				moves.add(getTile(nx,ny));
			}
			else{
				// (nx,ny)에 내 말이 없는지, 또 적 킹이 없는지 확인 --> 이동 가능 범위에 킹이 있으면 체크메이트 상태이므로, 무브가 될 수 없다.
				if(unit.color == color) continue; //같은 플레이어의 말, 넘김
				moves.add(getTile(nx,ny)); 
			}
		}
		
		//캐슬링 체크하기 
		return moves;
	}
	
	private JSONArray getQueenMove(int x,int y){
		JSONArray moves = new JSONArray();
		int i,j, nx, ny;
		int dx[] = {1, 1, 0, -1, -1, -1, 0, 1};
		int dy[] = {0, 1, 1, 1, 0, -1, -1, -1};
		Piece unit = getPiece(x,y), target;
		
		char color = unit.color;
		
		
		for(i=0;i<8;i++){ //방향
			for(j=1;j<=8;j++){  //길이
				nx = x + dx[i]*j;
				ny = y + dy[i]*j;
				
				if(!isInRange(nx,ny)) break; //보드 범위 밖이므로 더이상 체크 하지 않는다
				target = getPiece(nx,ny);
				
				if(target == null){
					moves.add(getTile(nx,ny));
				}
				else{
					// (nx,ny)에 내 말이 없는지, 또 적 킹이 없는지 확인 --> 이동 가능 범위에 킹이 있으면 체크메이트 상태이므로, 무브가 될 수 없다.
					if(target.color != color) moves.add(getTile(nx,ny)); 
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
		Piece unit = getPiece(x,y), target;
		char color = unit.color;
		
		
		for(i=0;i<4;i++){  //방향
			for(j=1;j<=8;j++){ //길이
				nx = x + dx[i]*j;
				ny = y + dy[i]*j;
				if(!isInRange(nx,ny)) break;
				target = getPiece(nx,ny);
				
				if(target == null){
					moves.add(getTile(nx,ny));
				}
				else{
					// (nx,ny)에 내 말이 없는지, 또 적 킹이 없는지 확인 --> 이동 가능 범위에 킹이 있으면 체크메이트 상태이므로, 무브가 될 수 없다.
					if(target.color != color) moves.add(getTile(nx,ny)); 
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
		Piece unit = getPiece(x,y), target;
		char color = unit.color;
		
		
		for(i=0;i<4;i++){  //방향
			for(j=1;j<=8;j++){ //길이
				nx = x + dx[i]*j;
				ny = y + dy[i]*j;
				if(!isInRange(nx,ny)) break;
				target = getPiece(nx,ny);
				
				if(target == null){
					moves.add(getTile(nx,ny));
				}
				else{
					// (nx,ny)에 내 말이 없는지, 또 적 킹이 없는지 확인 --> 이동 가능 범위에 킹이 있으면 체크메이트 상태이므로, 무브가 될 수 없다.
					if(target.color != color) moves.add(getTile(nx,ny)); 
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
		Piece unit = getPiece(x,y), target;
		char color = unit.color;
			
		for(i=0;i<8;i++){  //방향
			nx = x + dx[i];
			ny = y + dy[i];
			if(!isInRange(nx,ny)) continue;
			target = getPiece(nx,ny);
			
			if(target == null){
				moves.add(getTile(nx,ny));
			}
			else{
				// (nx,ny)에 내 말이 없는지, 또 적 킹이 없는지 확인 --> 이동 가능 범위에 킹이 있으면 체크메이트 상태이므로, 무브가 될 수 없다.
				if(target.color != color) moves.add(getTile(nx,ny)); 
			}
		}
		return moves;
	}
	
	private JSONArray getPawnMove(int x,int y){
		/*2칸 전진, 대각선에 적 기물 잡기, 1칸 전진*/
		JSONArray moves = new JSONArray();
		Piece unit = getPiece(x,y);
		Piece oneFrontUnit = null, twoFrontUnit = null, crossUnit;
		char color = unit.color;
		if(color == 'W'){
			//전진 체크
			if(isInRange(x+1,y)) oneFrontUnit = getPiece(x+1,y);
			if(oneFrontUnit == null){
				moves.add(getTile(x+1,y));
				if(!unit.pawnTwoMoved){ //2칸 전진 가능
					twoFrontUnit = getPiece(x+2,y);
					if(twoFrontUnit == null) moves.add(getTile(x+2,y));
				}
			}
			//대각체크
			if(isInRange(x+1,y-1)){
				crossUnit = getPiece(x+1,y-1);
				if(color != crossUnit.color) moves.add(getTile(x+1,y-1));
			}
			if(isInRange(x+1,y+1)){
				crossUnit = getPiece(x+1,y+1);
				if(color != crossUnit.color) moves.add(getTile(x+1,y+1));			
			}			
		}
		else{
			if(isInRange(x-1,y)) oneFrontUnit = getPiece(x-1,y);
			if(oneFrontUnit == null){
				moves.add(getTile(x-1,y));
				if(!unit.pawnTwoMoved){ //2칸 전진 가능
					twoFrontUnit = getPiece(x-2,y);
					if(twoFrontUnit==null) moves.add(getTile(x-2,y));
				}
			}
			//대각체크
			if(isInRange(x-1,y-1)){
				crossUnit = getPiece(x-1,y-1);
				if(color != crossUnit.color) moves.add(getTile(x-1,y-1));
			}
			if(isInRange(x-1,y+1)){
				crossUnit = getPiece(x-1,y+1);
				if(color != crossUnit.color) moves.add(getTile(x-1,y+1));		
			}	
		}
		return moves;
	}
		
	private JSONArray getMovable(String tile){
		JSONArray moves = new JSONArray();
		
		int position[] = getPosition(tile);
		int x = position[0];
		int y = position[1];
		Piece unit = getPiece(tile);
		
		char c = unit.unit;
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
	
	@Override
	public JSONObject select(Player player, String tile) {
		JSONObject message = new JSONObject();
		Piece unit = getPiece(tile);
		char color = player.getColor().charAt(0);
		color = Character.toUpperCase(color);
		
		if( color == unit.color){
			//이동가능한 타일들 모아서 보내줌
			JSONArray moves = getMovable(tile);
			
			message.put("type", "SELECT_SUCCESS");
			message.put("piece",  unit.name);
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

	
	public JSONObject move(Player player, String srcTile, String destTile) {
		JSONObject message = new JSONObject();
		JSONObject move = new JSONObject(), rookMove = new JSONObject();
		String castling = "NONE";
		char color = player.getColor().charAt(0);
		color = Character.toUpperCase(color);
		int src_position[] = getPosition(srcTile); 
		Piece src_unit = getPiece(srcTile);
		
		int dest_position[] = getPosition(destTile);
		Piece dest_unit = getPiece(destTile);	
		
		board[src_position[0]][src_position[1]] = null;
		board[dest_position[0]][dest_position[1]] = src_unit;
		
		if(src_unit.unit == 'P'){
			if(Math.abs(src_position[0] - dest_position[0]) == 2)src_unit.pawnTwoMoved = true;
		}
		else if(src_unit.unit == 'K'){
			/*if(){
				castling = "CASTLING";
			}*/
		}
		
		
		message.put("type", "MOVE_SUCCESS");
		message.put("state", castling);
		move.put("srcPiece",src_unit.name);
		move.put("destTile",destTile);
		message.put("move",move);
		//rookMove.put("srcPiece", value);
		//rookMove.put("destTile", value);
		message.put("rookMove", rookMove);
		return message;
	}	
	
	

	@Override
	public JSONObject surrender(Player player) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String whoWin() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean isCheckmate(){
		
		return false;
	}
	
	@Override
	public boolean isCheck(){
		return false;
	}

}
