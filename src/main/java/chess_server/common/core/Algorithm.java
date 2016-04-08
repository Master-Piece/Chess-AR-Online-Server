package chess_server.common.core;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Algorithm {
	class Piece{
		char unit;
		String name;
		boolean isFirstMove;
		String enPassant;
		char color;
		
		Piece(String name){
			this.name = name;
			this.unit = name.charAt(1);
			isFirstMove = true;
			enPassant = null;
			color = name.charAt(0);
		}
	}
	
	private Piece[][] board = {
			
			{null, null, null, null, null, null, null, null},
			{null, null, null, null, null, null, null, null},
			{null, null, null, null, new Piece("WK"), null, null, null},
			{null, null, null, null, null, null, null, null},
			{null, null, null, new Piece("WQ"), new Piece("WB1"), null, null, null},
			{null, null, null, null, null, null, null, null},
			{null, null, null, null, null, null, null, null},
			{null, null, new Piece("BK"), null, null, null, null, null}
				
	};

	public void print(){
		int i,j;
		for(i=0;i<8;i++){
			for(j=0;j<8;j++){
				if(board[i][j]!=null) System.out.print(board[i][j].name + " ");
				else System.out.print("   ");
			}
			System.out.println();
		}
	}
	
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
		char a = '1',b = 'A';
		a += x;
		b += y;
		tile = tile + b + a;
		return tile;
	}
	
	private JSONArray getKingMove(int x,int y, int flag){
		JSONArray moves = new JSONArray();		
		int dx[] = {1, 1, 0, -1, -1, -1, 0, 1};
		int dy[] = {0, 1, 1, 1, 0, -1, -1, -1};	
		int i, nx, ny;
		Piece target, unit = getPiece(x,y);
		char color = unit.color;
		
		for(i=0;i<8;i++){
			nx = x + dx[i];
			ny = y + dy[i];
			if(!isInRange(nx,ny)) continue;
			target = getPiece(nx,ny);
			if(target != null && target.color == color) continue;
			pushMoves(x,y,nx,ny,flag,color,moves);		
		}
		
		//캐슬링 체크하기 
		if(flag == 1 && unit.isFirstMove && !isCheck(color)){
			//왼쪽
			if(isInRange(x,y-1) && isInRange(x,y-2) && isInRange(x,y-3) && isInRange(x,y-4) && board[x][y-1] == null && board[x][y-2] == null && board[x][y-3] == null && board[x][y-4] != null && board[x][y-4].isFirstMove){
				Piece king = board[x][y];
				board[x][y-1] = king;
				board[x][y] = null;
				if(!isCheck(color)){
					board[x][y-2] = king;
					board[x][y-1] = null;
					if(!isCheck(color)){
						moves.add(getTile(x,y-2));
					}
				}
				board[x][y] = king;
				board[x][y-1] = null;
				board[x][y-2] = null;
			}
			//오른쪽
			if(isInRange(x,y+1) && isInRange(x,y+2) && isInRange(x,y+3) && board[x][y+1] == null && board[x][y+2] == null  && board[x][y+2] == null && board[x][y+3] != null && board[x][y+3].isFirstMove){
				Piece king = board[x][y];
				board[x][y+1] = king;
				board[x][y] = null;
				if(!isCheck(color)){
					board[x][y+2] = king;
					board[x][y+1] = null;
					if(!isCheck(color)){
						moves.add(getTile(x,y+2));
					}
				}
				board[x][y] = king;
				board[x][y+1] = null;
				board[x][y+2] = null;
			}
		}
		return moves;
	}
	
	private JSONArray getQueenMove(int x,int y, int flag){
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
				if(target != null && target.color == color) break;
				pushMoves(x,y,nx,ny,flag,color,moves);
				if(target != null) break;
			}
		}
		
		return moves;
	}
	
	private JSONArray getBishopMove(int x,int y, int flag){
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
				
				if(target != null && target.color == color) break;
				pushMoves(x,y,nx,ny,flag,color,moves);	
				if(target != null) break;
			}
		}
		return moves;
	}
	
	private JSONArray getRookMove(int x,int y, int flag){
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
				
				if(target != null && target.color == color) break;
				pushMoves(x,y,nx,ny,flag,color,moves);
				if(target != null) break;
			}
		}
		return moves;
	}
	
	private JSONArray getKnightMove(int x,int y, int flag){
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
			
			if(target != null && target.color == color) continue;
			pushMoves(x,y,nx,ny,flag,color,moves);
		}
		return moves;
	}
	
	private JSONArray getPawnMove(int x,int y, int flag){
		/*2칸 전진, 대각선에 적 기물 잡기, 1칸 전진*/
		JSONArray moves = new JSONArray();
		Piece unit = getPiece(x,y);
		Piece oneFrontUnit = null, twoFrontUnit = null, crossUnit;
		char color = unit.color;
		int dx;
		if(color == 'W') dx = 1;
		else dx = -1;
		
		//전진 체크
		if(isInRange(x + dx,y)) oneFrontUnit = getPiece(x+dx,y);
		if(oneFrontUnit == null){
			pushMoves(x,y,x+dx,y,flag,color,moves);
			if(unit.isFirstMove){ //2칸 전진 가능
				twoFrontUnit = getPiece(x+2*dx,y);
				if(twoFrontUnit == null) pushMoves(x,y,x+2*dx,y,flag,color,moves);
			}
		}
		
		//대각체크
		if(isInRange(x+dx,y-1)){
			crossUnit = getPiece(x+dx,y-1);
			if(crossUnit != null && color != crossUnit.color) pushMoves(x,y,x+dx,y-1,flag,color,moves);
		}
		if(isInRange(x+dx,y+1)){
			crossUnit = getPiece(x+dx,y+1);
			if(crossUnit != null && color != crossUnit.color) pushMoves(x,y,x+dx,y+1,flag,color,moves);			
		}
		
		//앙파상 체크
		if(unit.enPassant != null){
			int positions[] = getPosition(unit.enPassant);
			moves.add(getTile(positions[0]+dx,positions[1]));
		}
		return moves;
	}
	
	private boolean pushMoves(int x,int y, int nx, int ny, int flag, char color, JSONArray moves){
		boolean result = false;
		if(flag == 1){
			//nx,ny로 미리 옮겨놓고 체크인지 확인해보고 체크면 안넣고, 아니면 넣고
			Piece tmp = board[nx][ny];
			board[nx][ny] = board[x][y];
			board[x][y] = null;
			result = isCheck(color);
			if(!result)moves.add(getTile(nx,ny));
			board[x][y] = board[nx][ny];
			board[nx][ny] = tmp;
		}
		else moves.add(getTile(nx,ny));		
		return !result;
	}	
	
	private JSONArray getMovable(String tile, int flag){
		//tile의 이동 가능 경로 리턴, flag는 이동했을시 check인지 아닌지 확인 여부 
		JSONArray moves = new JSONArray();
		
		int position[] = getPosition(tile);
		int x = position[0];
		int y = position[1];
		Piece unit = getPiece(tile);
		
		char c = unit.unit;
		if(c ==  'K'){
			moves = getKingMove(x,y, flag);
		}
		else if(c == 'Q'){
			moves = getQueenMove(x,y, flag);
		}
		else if(c == 'B'){
			moves = getBishopMove(x,y, flag);
		}
		else if(c == 'R'){
			moves = getRookMove(x,y, flag);
		}
		else if(c == 'N'){
			moves = getKnightMove(x,y, flag);
		}
		else if(c == 'P'){
			moves = getPawnMove(x,y, flag);
		}
		return moves;
	}
	
	
	public JSONObject select(Player player, String tile) {
		JSONObject message = new JSONObject();
		Piece unit = getPiece(tile);
		char color = player.getColor().charAt(0);
		color = Character.toUpperCase(color);
		
		if(unit != null && color == unit.color){
			//이동가능한 타일들 모아서 보내줌
			JSONArray moves = getMovable(tile, 1);	
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
		String castlingTile = null, castlingUnit = null;
		char color = player.getColor().charAt(0);
		color = Character.toUpperCase(color);
		int src_position[] = getPosition(srcTile); 
		Piece src_unit = getPiece(srcTile);
		
		int dest_position[] = getPosition(destTile);
		Piece dest_unit = getPiece(destTile);	
		
		board[src_position[0]][src_position[1]] = null;
		board[dest_position[0]][dest_position[1]] = src_unit;
		src_unit.isFirstMove = false;
			
		if(src_unit.unit == 'P'){
			if(Math.abs(src_position[0] - dest_position[0]) == 2){
				int nx = dest_position[0], ny = dest_position[1]+1;
				if(isInRange(nx,ny) && board[nx][ny] != null && board[nx][ny].unit == 'P' && board[nx][ny].color != src_unit.color){
					board[nx][ny].enPassant = getTile(nx,ny);
				}
				nx = dest_position[0]; ny = dest_position[1]-1;
				if(isInRange(nx,ny) && board[nx][ny] != null && board[nx][ny].unit == 'P' && board[nx][ny].color != src_unit.color){
					board[nx][ny].enPassant = getTile(nx,ny);
				}
			}
			if(src_unit.enPassant != null){				
				//앙파상 플래그 true에 대각 움직임 -> 100% 앙파상
				if(Math.abs(src_position[1] - dest_position[1]) == 1){
					dest_unit = getPiece(src_unit.enPassant);
				}
				src_unit.enPassant = null;
			}
		}
		else if(src_unit.unit == 'K'){
			if(Math.abs(src_position[1] - dest_position[1]) == 2){
				castling = "CASTLING";
			}
			if(dest_position[1] == 2){
				if(src_unit.color == 'W'){
					//WR1 -> 3
					castlingUnit = "WR1";
					castlingTile = "C1";
				}
				else{
					//BR2 -> 3
					castlingUnit = "BR2";
					castlingTile = "C8";
				}
			}
			else if(dest_position[1] == 6){
				if(src_unit.color == 'W'){
					//WR2 -> 5
					castlingUnit = "WR2";
					castlingTile = "F1";
				}
				else{
					//BR1 -> 5
					castlingUnit = "BR1";
					castlingTile = "F8";
				}
			}
		}	
		for(int i=0;i<8;i++){
			for(int j=0;j<8;j++){
				if(board[i][j] == null) continue;
				if(board[i][j].color == color && board[i][j].unit == 'P') board[i][j].enPassant = null;
			}
		}
		
		message.put("type", "MOVE_SUCCESS");
		message.put("state", castling);
		move.put("srcPiece",src_unit.name);
		move.put("destTile",destTile);
		move.put("targetPiece", dest_unit.name);
		message.put("move",move);
		
		rookMove.put("srcPiece", castlingUnit);
		rookMove.put("destTile", castlingTile);
		message.put("rookMove", rookMove);
		return message;
	}	
	
	
	public boolean isCheckmate(){
		return isCheckmate('W') || isCheckmate('B');
	}
	
	public boolean isCheckmate(char color){
		int i,j;
		JSONArray moves = null;
		if(isCheck(color)){
			for(i=0;i<8;i++){
				for(j=0;j<8;j++){
					if(board[i][j].unit == 'K'){
						if(board[i][j].color == color){
							moves = getKingMove(i,j, 0);
						}
					}
				}
			}	
		}
		if(moves.size() == 0) return true;
		return false;
	}
	
	
	public boolean isCheck(){	
		return isCheck('W') || isCheck('B');
	}
	
	public boolean isCheck(char color){
		System.out.println("ischeck called\n");
		int check[][] = {
				{0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0}
		};
		int kingX = 0, kingY = 0, i, j;
		JSONArray moves = new JSONArray();
		for(i=0;i<8;i++){
			for(j=0;j<8;j++){
				if(board[i][j] != null && board[i][j].unit == 'K'){
					if(board[i][j].color == color){
						kingX = i;
						kingY = j;
					}
				}
			}
		}
		
		for(i=0;i<8;i++){
			for(j=0;j<8;j++){
				if(board[i][j] != null && board[i][j].color != color){
					moves = getMovable(getTile(i,j),0);
					for(int k = 0; k < moves.size();k++){
						int positions[] = getPosition(moves.get(k).toString());
						check[positions[0]][positions[1]] = 1;
					}
				}
			}
		}
		if(check[kingX][kingY] == 1) return true;
		return false;
	}

	public boolean isStalemate(char color){
		int i,j;
		JSONArray moves;
		for(i=0;i<8;i++){
			for(j=0;j<8;j++){
				if(board[i][j] != null &&  board[i][j].color == color){
					moves = getMovable(getTile(i,j),1);
					if(moves.size() != 0) return false;
				}
			}
		}
		return true;
	}
}
