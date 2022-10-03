package jms.messagestructure;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;

public class Consumer {

	
	public static void main(String[] args) throws Exception {

		InitialContext initialContext = new InitialContext();

		ConnectionFactory cf = (ConnectionFactory) initialContext.lookup("ConnectionFactory");
		Connection connection = cf.createConnection();
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Queue queue = (Queue) initialContext.lookup("queue/myQueue");
		
		
		MessageConsumer consumer = session.createConsumer(queue);
		connection.start();
		TextMessage messageReceived = (TextMessage) consumer.receive(5000);
		System.out.println("Message Received: " + messageReceived.getText());

		initialContext.close();
		connection.close();

	}
}
