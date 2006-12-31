// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>

package net.spy.photo;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.InitialContext;

/**
 * Simple mail sender.
 */
public class Mailer extends Object {

	private String mailConfig=null;
	private String recipient=null;
	private String subject=null;
	private String body=null;

	/**
	 * Get an instance of Mailer.
	 */
	public Mailer(String conf) {
		super();
		mailConfig=conf;
	}

	public Mailer() {
		this("java:Mail");
	}

	/**
	 * Set the recipient address.
	 */
	public void setRecipient(String to) {
		this.recipient=to;
	}

	/**
	 * Set the subject of the message.
	 */
	public void setSubject(String to) {
		this.subject=to;
	}

	/**
	 * Set the body of the message.
	 */
	public void setBody(String to) {
		this.body=to;
	}

	/**
	 * Send the message.
	 */
	public void send() throws Exception {
		Address addrs[]=new InternetAddress[1];
		addrs[0]=new InternetAddress(recipient);

		Session session=(Session)new InitialContext().lookup(mailConfig);
		Message msg=new MimeMessage(session);
		msg.setRecipients(Message.RecipientType.TO, addrs);
		msg.setSubject(subject);
		msg.setText(body);
		Transport.send(msg);
	}

}
