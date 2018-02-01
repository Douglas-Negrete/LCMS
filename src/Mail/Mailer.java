package Mail;

import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class Mailer {

	static String from = "automatedlcms@gmail.com", 
				password = "lawncare5";

	public static void send(String[] to,String sub,String msg, String att){

		//Get properties object    
		Properties props = new Properties();    
		props.put("mail.smtp.host", "smtp.gmail.com");    
		props.put("mail.smtp.socketFactory.port", "465");    
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");    
		props.put("mail.smtp.auth", "true");    
		props.put("mail.smtp.port", "465");    
		//get Session   
		Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {

			protected PasswordAuthentication getPasswordAuthentication() {

				return new PasswordAuthentication(from,password);

			}//end PasswordAuthentication

		});//end getDefaultInstance

		//compose message    
		try {

			MimeMessage message = new MimeMessage(session);

			for(int i = 0; i < to.length; i++)
				message.addRecipient(Message.RecipientType.TO,new InternetAddress(to[i]));

			message.setSubject(sub);   
			//message.setText(msg);

			// Create the message part 
			BodyPart messageBodyPart = new MimeBodyPart();

			// Fill the message
			messageBodyPart.setText(msg);

			// Create a multipar message
			Multipart multipart = new MimeMultipart();

			// Set text message part
			multipart.addBodyPart(messageBodyPart);

			// Part two is attachment
			if(!att.equals("")){
				
				messageBodyPart = new MimeBodyPart();
				//String filename = "C:\\Users\\ama82\\Documents\\TechOSProjectProgrammerManual.pdf";
				DataSource source = new FileDataSource(att);
				messageBodyPart.setDataHandler(new DataHandler(source));
				messageBodyPart.setFileName(att);
				multipart.addBodyPart(messageBodyPart);
				
			}

			// Send the complete message parts
			message.setContent(multipart);

			//send message  
			Transport.send(message);
			System.out.printf("message sent successfully\n");

		} catch (MessagingException e) {

			throw new RuntimeException(e);

		}    

	}//end send array

}//end class mailer

