/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connexion;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author Anas
 */
public class Jason implements JsonDeserializer<Msg>{
	private Jason() {}

	public static String pack(Msg g) {
		String ret=null;
		Gson gson = new Gson();
		ret = gson.toJson(g);
		System.out.println("JASON - pack : " + ret);
		return ret;
	}
	public static Msg unpack(String js) {
		System.out.println("JASON - unpack : " + js);
		Msg ret=null;
		Gson gson = new GsonBuilder().registerTypeAdapter(Msg.class, new Jason()).create();
		ret = gson.fromJson(js, Msg.class);
		return ret;
	}

	@Override
	public Msg deserialize(JsonElement je, Type t, JsonDeserializationContext jdc) throws JsonParseException {
		Gson gson = new Gson();
		final JsonObject msgObj = je.getAsJsonObject();
		String author = msgObj.get("author").getAsString();
		String type = msgObj.get("type").getAsString();
		Object o = null;
		try {
			/*
			if(type.equals("ChatMsg")) {
			
			final JsonObject chatobj = msgObj.getAsJsonObject("content");
			o = new ChatMsg(chatobj.get("text").getAsString(),new Date(chatobj.get("d").getAsString()));
			}
			*/
			o = gson.fromJson(msgObj.get("content"), Class.forName("connexion."+type));
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(Jason.class.getName()).log(Level.SEVERE, null, ex);
		}
	
		
			
		return new Msg(author, o, type);
	}
	
}
