package chess_server.common.core;

import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class SessionCheckInterceptor extends HandlerInterceptorAdapter {
	private Logger log = Logger.getLogger(this.getClass());
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		GameCoreManager gcm = GameCoreManager.getInstance();
		long sessionKey = Long.parseLong((String)request.getParameter("sessionKey"));
		String userId = (String)request.getParameter("userId");
		log.debug("request arrived: sessionKey: " + sessionKey + ", userId: " + userId);
		GameThread gt = gcm.getGame(sessionKey);
		
		if (gt != null && !gt.isMyTurn(userId)) {
			log.info("==============================================================");
			log.info("Invalid Request : User requested at other user's turn.");
			JSONObject json = new JSONObject();
			json.put("type", "INVALID_REQUEST");
			response.sendError(response.SC_BAD_REQUEST, json.toJSONString());
			//response.sendRedirect("/game/command.cao");
			log.info("==============================================================");
			return false;
		}
		
		return super.preHandle(request, response, handler);
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		
	}
}
