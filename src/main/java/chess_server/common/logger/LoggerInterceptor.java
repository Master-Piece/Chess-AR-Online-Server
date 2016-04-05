package chess_server.common.logger;

import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class LoggerInterceptor extends HandlerInterceptorAdapter {
	protected Log log = LogFactory.getLog(LoggerInterceptor.class);
	
	protected String START_TAG = "========================================= START =========================================";
	protected String END_TAG = "=========================================  END  =========================================";
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (request.getRequestURI().equals("/chess_server/manager/refreshAll.cao")) {
			return super.preHandle(request, response, handler);
		}
		if (log.isDebugEnabled()) {
			log.debug(START_TAG);
			log.debug("\tRequest Host\t:\t" + request.getRemoteAddr());
			log.debug("\tRequest Method\t:\t" + request.getMethod());
			log.debug("\tRequest URI\t:\t" + request.getRequestURI());
			log.debug("\tRequest Parameter\t:");
			Map parameter = request.getParameterMap();
			Iterator keyIter = parameter.keySet().iterator();
			while(keyIter.hasNext()) {
				String key = (String)keyIter.next();
				log.debug(key + " => " + request.getParameter(key));
			}
		}
		return super.preHandle(request, response, handler);
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		if (request.getRequestURI().equals("/chess_server/manager/refreshAll.cao"))
			return;
		if (log.isDebugEnabled()) {
			log.debug(END_TAG);
		}
	}
}
