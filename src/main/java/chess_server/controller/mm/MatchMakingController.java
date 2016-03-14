package chess_server.controller.mm;

import java.util.Map;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import chess_server.common.common.CommandMap;
import chess_server.common.core.MatchMaker;
import chess_server.common.core.Player;

@Controller
public class MatchMakingController {
	Logger log = Logger.getLogger(this.getClass());
	
	@RequestMapping(value="/mm/matchRequest.cao", method=RequestMethod.GET)
	public ModelAndView matchMaking(CommandMap commandMap) throws Exception {
		ModelAndView mv = new ModelAndView("matchResult");

		if(commandMap.isEmpty() == false){
			Map arg = commandMap.getMap();
			
	        String type = (String) arg.get("type");
	        log.debug("type: " + type);
	        
	        if (type.equals("MMR")) {
	        	MatchMaker mm = MatchMaker.getInstance();
	        	
	        	String gcmToken = (String)arg.get("gcmToken");
//	        	String nick = (String)arg.get("nick");
	        	String nick = "Player_" + MatchMaker.getInstance().getAccumulateUsers();
	        	Player player = new Player(gcmToken, nick); 
	        	
	        	JSONObject jsonObj = new JSONObject();
	        	
	        	if (!mm.enqueue(player)) {
	        		jsonObj.put("type", "MMR_FAILED");
	        	}
	        	else {
		        	mm.printQueue();
		        	jsonObj.put("type", "MMR_SUCCESS");
		        	jsonObj.put("id", player.getId());
	        	}
	        	mv.addObject("json", jsonObj.toJSONString());
	        }
	        else {
	        	log.debug("Request type not valid");
	        	JSONObject jsonObj = new JSONObject();
	        	jsonObj.put("type", "MMR_FAILED");
	        	mv.addObject("json", jsonObj.toJSONString());
	        }
	    }
		
		return mv;
	}
}
