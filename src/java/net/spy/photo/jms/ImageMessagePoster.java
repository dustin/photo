// Copyright (c) 2006  Dustin Sallings <dustin@spy.net>
// arch-tag: D44FAAC8-4898-4143-93EF-14B497961F82

package net.spy.photo.jms;

import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.InitialContext;

import net.spy.SpyObject;
import net.spy.photo.PhotoConfig;
import net.spy.photo.PhotoImage;
import net.spy.photo.PhotoImageData;
import net.spy.photo.observation.NewImageData;
import net.spy.photo.observation.Observable;
import net.spy.photo.observation.Observation;
import net.spy.photo.observation.Observer;

/**
 * Post JMS messages when images are added.
 */
public class ImageMessagePoster extends SpyObject
	implements Observer<NewImageData> {

	private QueueConnection conn=null;
	private QueueSession session=null;
	private Queue queue=null;
	private QueueSender sender=null;

	public ImageMessagePoster() throws Exception {
		InitialContext ic=new InitialContext();
		QueueConnectionFactory tcf=(QueueConnectionFactory)ic.lookup(
				"ConnectionFactory");
		conn=tcf.createQueueConnection();
		queue=(Queue)ic.lookup(PhotoConfig.getInstance().get("newImgQueue"));
		session = conn.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);

		sender=session.createSender(queue);
		conn.start();
	}

	private void postMessage(PhotoImageData pid, PhotoImage pi)
		throws Exception {

		ObjectMessage bm=session.createObjectMessage(pi);
		bm.setIntProperty("imgId", pid.getId());
		sender.send(bm);
	}

	public void close() throws Exception {
		conn.stop();
		session.close();
		conn.close();
	}

	public void observe(Observable<NewImageData> obserable,
			Observation<NewImageData> observation) {
		PhotoImageData pid=observation.getData().getPhotoImageData();
		PhotoImage pi=observation.getData().getPhotoImage();

		try {
			postMessage(pid, pi);
		} catch (Exception e) {
			getLogger().warn("Problem sending JMS message for new image", e);
		}
	}
}
