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

public class MessageExpiration {

	public static void main(String[] args) throws Exception {

		InitialContext initialContext = null;
		Connection connection = null;
		try {
			initialContext = new InitialContext();

			ConnectionFactory cf = (ConnectionFactory) initialContext.lookup("ConnectionFactory");
			connection = cf.createConnection();
			connection.start();
			
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Queue queue = (Queue) initialContext.lookup("queue/myQueue");
			Queue expiryQueue = (Queue) initialContext.lookup("queue/ActiveMQ.DLQ");			
			
			MessageProducer producer = session.createProducer(queue);
			TextMessage message = session.createTextMessage("Message Expiration");
			producer.setTimeToLive(2000);
			producer.send(message);
			System.out.println("Message Sent: " + message.getText());

			Thread.sleep(6000);
			
			MessageConsumer consumer = session.createConsumer(queue);
			TextMessage messageReceived = (TextMessage) consumer.receive(5000);
			//System.out.println("Message Received: " + messageReceived.getText());
			
			TextMessage msg = (TextMessage) session.createConsumer(expiryQueue).receive();
			
			System.out.println("Expiration Msg Received: " + msg.getText());
			
		} finally {
			initialContext.close();
			connection.close();
		}

	}

}
