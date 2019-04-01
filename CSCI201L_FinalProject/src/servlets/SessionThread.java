package servlets;

import java.io.IOException;

import javax.websocket.Session;

public class SessionThread extends Thread {
	private Session session;
	private int messageNumber;
	private boolean closed;
	
	public SessionThread(Session session) {
		this.session = session;
		messageNumber = Server.getMessages().size();
		closed = false;
		this.start();
	}
	
	public void close() {
		closed = true;
	}
	
	@Override
	public void run() {
		while (true) {
			if (closed) {
				return;
			}
			if (Server.getMessages().size() - 1 >= messageNumber) {
				try {
					String text = Server.getMessages().get(messageNumber);
					int temp = messageNumber;
					messageNumber = Server.getMessages().size();
					if (session == Server.getAuthors().get(temp))
						continue;
					session.getBasicRemote().sendText("9," + text);
					System.out.println("Push: " + text);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
