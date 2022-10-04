package com.jms.guaranteedmessaging;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;

public class ConsumerTransactional {

	public static void main(String[] args) throws Exception {

		InitialContext initialContext = new InitialContext();

		ConnectionFactory cf = (ConnectionFactory) initialContext.lookup("ConnectionFactory");
		Connection connection = cf.createConnection();
		Session session = connection.createSession(true, Session.SESSION_TRANSACTED);
		Queue queue = (Queue) initialContext.lookup("queue/guaranteedQueue");
		
		
		MessageConsumer consumer = session.createConsumer(queue);
		connection.start();
		TextMessage messageReceived = (TextMessage) consumer.receive();
		System.out.println("Message Received: " + messageReceived.getText());

		session.commit();
		initialContext.close();
		connection.close();

	}
}
