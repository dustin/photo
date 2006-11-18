// Copyright (c) 2006  Dustin Sallings <dustin@spy.net>
// arch-tag: E5AA9C14-EF8F-468B-BC24-0EC634BD8A5A

package net.spy.photo.jms;

import java.util.ArrayList;
import java.util.Collection;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.InitialContext;

import net.spy.SpyObject;
import net.spy.photo.ImageServer;
import net.spy.photo.Persistent;
import net.spy.photo.PhotoConfig;
import net.spy.photo.PhotoDimensions;
import net.spy.photo.PhotoImage;
import net.spy.photo.impl.PhotoDimensionsImpl;
import net.spy.stat.Stats;

/**
 * Example consumer for JMS messages posted via ImageMessagePoster.
 */
public class ImageMessageConsumer extends SpyObject
	implements MessageListener {

	private QueueConnection conn=null;
	private QueueSession session=null;
	private Queue queue=null;
	private QueueReceiver receiver=null;

	private Collection<PhotoDimensions> sizes=null;

	public ImageMessageConsumer() throws Exception {
		PhotoConfig conf=PhotoConfig.getInstance();
		// The different sizes we want to cache.
		sizes = new ArrayList<PhotoDimensions>();
		sizes.add(new PhotoDimensionsImpl("50x50"));
		sizes.add(new PhotoDimensionsImpl(conf.get("thumbnail_size")));
		sizes.add(new PhotoDimensionsImpl("800x600"));

		InitialContext ic=new InitialContext();
		QueueConnectionFactory tcf=(QueueConnectionFactory)ic.lookup(
				"ConnectionFactory");
		conn=tcf.createQueueConnection();
		String theQueue=conf.get("newImgQueue");
		getLogger().info("Looking up queue:  %s", theQueue);
		queue=(Queue)ic.lookup(theQueue);
		session = conn.createQueueSession(false, Session.CLIENT_ACKNOWLEDGE);

		receiver=session.createReceiver(queue);
		receiver.setMessageListener(this);

		conn.start();
	}

	public void onMessage(Message msg) {
		try {
			ObjectMessage bm=(ObjectMessage)msg;
			int id=bm.getIntProperty("imgId");
			PhotoImage pi=(PhotoImage)bm.getObject();
			getLogger().info(
					"Processing message with id %s for image %s: %s",
					msg.getJMSMessageID(), id, pi);
			cacheVariations(bm.getIntProperty("imgId"), pi);
			msg.acknowledge();
		} catch (Exception e) {
			getLogger().warn("Error processing message", e);
		}
	}

	private void cacheVariations(int id, PhotoImage pi) throws Exception {
		long start=System.currentTimeMillis();
		ImageServer is=Persistent.getImageServer();
		is.getThumbnail(id);
		for(PhotoDimensions dim : sizes) {
			getLogger().info("Caching %d at %s", id, dim);
			is.getImage(id, dim);
		}
		Stats.getComputingStat(
				"precache." + pi.getWidth() + "x" + pi.getHeight())
			.add(System.currentTimeMillis() - start);
	}

	public void close() throws Exception {
		conn.stop();
		session.close();
		conn.close();
	}

}
