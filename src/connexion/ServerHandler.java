/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package connexion;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 *
 * @author Anas
 */
public class ServerHandler extends SimpleChannelInboundHandler<String>{
	
	static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	
	@Override
	public void channelActive(final ChannelHandlerContext ctx) {
		// Once session is secured, send a greeting and register the channel to the global channel
		// list so the channel received the messages from others.
		ctx.pipeline().get(SslHandler.class).handshakeFuture().addListener(
			new GenericFutureListener<Future<Channel>>() {
				@Override
				public void operationComplete(Future<Channel> future) throws Exception {
					ctx.writeAndFlush("");
					
					channels.add(ctx.channel());
				}
			});
	}
	
	public void broadcast(String msg) {
		for (Channel c: channels) {
			c.writeAndFlush(msg + '\0');
		}
	}	
		
	protected void messageReceived(ChannelHandlerContext ctx, String msg) throws Exception {
		// Send the received message to all channels but the current one.
		broadcast(msg);	
		// Close the connection if the client has sent 'bye'.
		if ("bye".equals(msg.toLowerCase())) {
			ctx.close();
		}
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