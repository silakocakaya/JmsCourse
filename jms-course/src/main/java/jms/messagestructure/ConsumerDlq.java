package jms.messagestructure;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;

import org.apache.activemq.command.ActiveMQObjectMessage;

public class ConsumerDlq {

	
	public static void main(String[] args) throws Exception {

		InitialContext initialContext = new InitialContext();

		ConnectionFactory cf = (ConnectionFactory) initialContext.lookup("ConnectionFactory");
		Connection connection = cf.createConnection();
		connection.start();
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Queue expiryQueue = (Queue) initialContext.lookup("queue/ActiveMQ.DLQ");
		
		MessageConsumer consumer = session.createConsumer(expiryQueue);
		
		for(int i = 0; i < 20; i++) {
			consumer.receive();
		}
//		System.out.println("Message Received: " + messageReceived.getText());

		initialContext.close();
		connection.close();

	}
}
