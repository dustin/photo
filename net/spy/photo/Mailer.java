// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: Mailer.java,v 1.2 2002/07/10 03:38:08 dustin Exp $

package net.spy.photo;

import java.io.PrintStream;

import com.oreilly.servlet.MailMessage;

/**
 * Simple mail sender.
 */
public class Mailer extends Object {

	private String to=null;
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
	public void setTo(String to) {
		this.to=to;
	}

	/**
	 * Set the subject of the message.
	 */
	public void setSubject(String subject) {
		this.subject=subject;
	}

	/**
	 * Set the body of the message.
	 */
	public void setBody(String body) {
		this.body=body;
	}

	/**
	 * Send the message.
	 */
	public void send() throws Exception {
		PhotoConfig conf=new PhotoConfig();
		MailMessage mail=new MailMessage(conf.get("mail_server"));
		mail.from(conf.get("mail_sender"));
		mail.to(to);
		mail.setSubject(subject);
		PrintStream out=mail.getPrintStream();
		out.print(body);
		mail.sendAndClose();
	}

}
