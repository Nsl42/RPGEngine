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
public class Msg {
	public String author;
	public Object content;
	public String type;
	
	public Msg() { }
	public Msg(String author, Object o, String type) {
		this.author = author;
		this.content = o;
		this.type = type;
	}
}
