/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connexion;
import java.util.Date;

/**
 *
 * @author Anas
 */
public class ChatMsg {
	public String text;
	public Date d;
	public ChatMsg(String c, Date d) {
		this.text = c;
		this.d = d;
	}
}
