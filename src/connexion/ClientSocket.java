package connexion;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLException;

/**
 *
 * @author Anas
 */
public  class ClientSocket extends Observable{
	
	private static ClientSocket cs;
	private static EventLoopGroup group;
	private static Channel ch;
	private static ChannelFuture lastWriteFuture;
	public static ClientHandler handler;
	public static int nJ = 0;

	public static String HOST;
	public static int PORT;
	public static String pseudo;
	
	public static void open(String host, int port, String pseudo, Observer o) throws SSLException, InterruptedException{
		cs = new ClientSocket();
		cs.addObserver(o);
		final SslContext sslCtx = SslContext.newClientContext(InsecureTrustManagerFactory.INSTANCE);
		ClientSocket.pseudo = pseudo;	
		group = new NioEventLoopGroup();
		Bootstrap b = new Bootstrap();
		b.group(group)
			.channel(NioSocketChannel.class)
			.handler(new ClientInitializer(sslCtx));
		
		// Start the connection attempt.
		ch = b.connect(host, port).sync().channel();
	}
	public static void notif(Msg m) {
		cs.setChanged();
		cs.notifyObservers(m);
			
	}
	public static void send(String m) {
		Msg msg = new Msg(pseudo, new ChatMsg(m, new Date()), "ChatMsg");
		lastWriteFuture = ch.writeAndFlush(Jason.pack(msg) + '\0');
	}
	public static void send(Msg m) {
		lastWriteFuture = ch.writeAndFlush(Jason.pack(m) + '\0');
	}
	public static void close() {
		if (lastWriteFuture != null) {
			try {
				lastWriteFuture.sync();
			} catch (InterruptedException ex) {
				Logger.getLogger(ClientSocket.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		group.shutdownGracefully();
	}
	public static void main(String[] args) throws IOException{
		try{
			// Initiates the connection
			open(System.getProperty("host", "127.0.0.1"),
				Integer.parseInt(System.getProperty("port", "8992")),
				"joueur", new Observer() {

				@Override
				public void update(Observable o, Object arg) {
					System.out.println("Test d'intégration " + ((ChatMsg)((Msg)arg).content).text);
				}
			});

			// Read commands from the stdin.
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			for (;;) {
				final String line = in.readLine();
				if (line == null) {
					break;
				}

				// Send line to server
				send(line);
			}
		} catch (SSLException | InterruptedException ex) {
			Logger.getLogger(ClientSocket.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			// The connection is closed automatically on shutdown.
			close();
		}
	}
}
