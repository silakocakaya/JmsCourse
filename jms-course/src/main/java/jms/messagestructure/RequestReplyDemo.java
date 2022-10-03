package jms.messagestructure;

import java.util.HashMap;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TemporaryQueue;
import javax.jms.TextMessage;
import javax.naming.InitialContext;

public class RequestReplyDemo {

	
	public static void main(String[] args) throws Exception {
		
		InitialContext initialContext = null;
		Connection connection = null;
		
		initialContext = new InitialContext();
		ConnectionFactory cf = (ConnectionFactory) initialContext.lookup("ConnectionFactory");
		connection = cf.createConnection();
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Queue queue = (Queue) initialContext.lookup("queue/myQueue");
		MessageProducer producer = session.createProducer(queue);
		
		Map<String, TextMessage> mapMsgId = new HashMap<String, TextMessage>();
		
		TemporaryQueue temporaryQueue = session.createTemporaryQueue();
		
		TextMessage message = session.createTextMessage("I am request reply demo");
		message.setJMSReplyTo(temporaryQueue);
		
		producer.send(message);
		System.out.println("Message Sent: " + message.getText());
		System.out.println("Message sent: " + message.getJMSMessageID() );
		
		mapMsgId.put(message.getJMSMessageID(), message);
		
		MessageConsumer consumer = session.createConsumer(queue);
		connection.start();
		TextMessage messageReceived = (TextMessage) consumer.receive();
		System.out.println("Message Received: " + messageReceived.getText());
		
		MessageProducer replyProducer = session.createProducer(messageReceived.getJMSReplyTo());
		TextMessage replyMessage = session.createTextMessage("Reply Producer "); 
		replyMessage.setJMSCorrelationID(messageReceived.getJMSMessageID());
		replyProducer.send(replyMessage);
		
		MessageConsumer replyConsumer = session.createConsumer(temporaryQueue);
		TextMessage replyMessageReceived = (TextMessage) replyConsumer.receive();
		System.out.println("Message Received Reply: " + replyMessageReceived.getText());
		
		System.out.println("Message Received Reply CorrelationId: " + replyMessageReceived.getJMSCorrelationID());
		System.out.println("Message Received Reply First Msg: " + mapMsgId.get(replyMessageReceived.getJMSCorrelationID()).getText());
		
		connection.close();
		initialContext.close();
		
	}
}
