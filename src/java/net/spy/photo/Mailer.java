// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
// arch-tag: EC632EC6-5D6C-11D9-94DF-000A957659CC

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

	private String recipient=null;
	private String subject=null;
	private String body=null;

	/**
	 * Get an instance of Mailer.
	 */
	public Mailer() {
		super();
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

		Session session=(Session)new InitialContext().lookup("java:Mail");
		Message msg=new MimeMessage(session);
		msg.setRecipients(Message.RecipientType.TO, addrs);
		msg.setSubject(subject);
		msg.setText(body);
		Transport.send(msg);
	}

}
