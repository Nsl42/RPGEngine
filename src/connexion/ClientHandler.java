/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package connexion;

import io.netty.channel.ChannelHandlerContext;

/**
 *
 * @author Anas
 */
public class ClientHandler extends io.netty.channel.SimpleChannelInboundHandler<String>{
	
	
    public void messageReceived(ChannelHandlerContext ctx, String msg) {
	    ClientSocket.notif(Jason.unpack(msg));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

	@Override
	protected void channelRead0(ChannelHandlerContext chc, String i) throws Exception {
		messageReceived(chc, i);
	}
}