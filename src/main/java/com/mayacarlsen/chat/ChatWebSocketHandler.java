package com.mayacarlsen.chat;

import java.util.List;
import java.util.Map;

import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;

import com.mayacarlsen.MayaCarlsen;
import com.mayacarlsen.user.User;
import com.mayacarlsen.user.UserDAO;

@WebSocket
public class ChatWebSocketHandler {

    private String sender, msg;

    @OnWebSocketConnect
    public void onConnect(Session userSession) throws Exception {
    	List<String> list = userSession.getUpgradeRequest().getParameterMap().get("username");
    	
    	if(list != null) {
	    	String username = (String) list.get(0);
	        
			if (username != null) {
		    	User user = UserDAO.getUserByUsername(username);
		
				if (user != null) {
			    	MayaCarlsen.chatuserUsernameMap.put(userSession, user);
			        MayaCarlsen.broadcastMessage(sender = "Server", msg = (user.getName() + " joined the chat"));
				}
			}
    	}
    }

    @OnWebSocketClose
    public void onClose(Session userSession, int statusCode, String reason) {
        User user = MayaCarlsen.chatuserUsernameMap.get(userSession);
        MayaCarlsen.chatuserUsernameMap.remove(userSession);
        MayaCarlsen.broadcastMessage(sender = "Server", msg = (user.getAlias() + " left the chat"));
    }

    @OnWebSocketMessage
    public void onMessage(Session userSession, String message) {
        MayaCarlsen.broadcastMessage(sender = MayaCarlsen.chatuserUsernameMap.get(userSession).getName(), msg = message);
    }

}