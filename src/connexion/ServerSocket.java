/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package connexion;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLException;

/**
 *
 * @author Anas
 */
public class ServerSocket {
	private static EventLoopGroup bossGroup;
	private static EventLoopGroup workerGroup;
	public static ServerHandler handler;
	
	public static void bind(int port) throws InterruptedException, SSLException, CertificateException {
		// Configure SSL.
		SelfSignedCertificate ssc = new SelfSignedCertificate();
		SslContext sslCtx = SslContext.newServerContext(ssc.certificate(), ssc.privateKey());

		// Configure Group
		bossGroup = new NioEventLoopGroup(1);
		workerGroup = new NioEventLoopGroup();
		ServerBootstrap b = new ServerBootstrap();
		b.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.handler(new LoggingHandler(LogLevel.INFO))
				.childHandler(new ServerInitializer(sslCtx));
		
		b.bind(port).sync().channel().closeFuture().sync();
	}

	public static void broadcast(String m) {
		Msg msg = new Msg("MJ", new ChatMsg(m, new Date()), "ChatMsg");
		ServerSocket.handler.broadcast(Jason.pack(msg));
	}
	public static void broadcast(Msg m) {
		ServerSocket.handler.broadcast(Jason.pack(m));
	}
	public static void close() {
		bossGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();
	}
	
	public static void main(String[] args) {
		try {
			bind(8992);
		} catch (InterruptedException |SSLException | CertificateException ex) {
			Logger.getLogger(ServerSocket.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			close();
		}
	}
}




