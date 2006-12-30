package net.spy.photo.mail;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.TimerTask;

import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Flags.Flag;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import net.spy.SpyObject;
import net.spy.log.Logger;
import net.spy.log.LoggerFactory;
import net.spy.photo.Category;
import net.spy.photo.CategoryFactory;
import net.spy.photo.Persistent;
import net.spy.photo.PhotoConfig;
import net.spy.photo.PhotoImage;
import net.spy.photo.PhotoImageDataFactory;
import net.spy.photo.User;
import net.spy.photo.UserFactory;
import net.spy.photo.impl.SavablePhotoImageData;
import net.spy.photo.log.PhotoLogUploadEntry;
import net.spy.photo.util.LCS;
import net.spy.photo.util.StreamUtil;
import net.spy.util.CloseUtil;
import net.spy.util.SpyUtil;

/**
 * The mail poller is used to periodically check the incoming mail for images
 * that may be arriving.
 */
public class MailPoller extends TimerTask {

	private String jndiName=null;

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Construct a mail poller with the given JNDI name pointing to a mail
     * configuration that may be used to receive email.
     * 
     * @param jname
     * @throws NamingException
     */
    public MailPoller(String jname) throws NamingException {
    	super();
    	jndiName=jname;
    	Session session = (Session)new InitialContext().lookup(jndiName);
    	assert session != null : "Retrieved null mail session from " + jndiName;
    }

    @Override
    public void run() {
        try {
            poll();
        } catch(Exception e) {
            logger.warn("Polling error", e);
        }
    }

    private void poll() throws Exception {
        logger.debug("Polling for for mail");
        Session session = (Session)new InitialContext().lookup(jndiName);
        Store store = session.getStore();
        assert store != null : "Didn't get sesion store";
        Folder folder = null;

        try {
            store.connect();
            folder=store.getFolder("INBOX");
            folder.open(Folder.READ_WRITE);

            for(Message m : folder.getMessages()) {
                processMessage(m);
            }
            folder.close(true);
        } finally {
            store.close();
        }
    }

    private void processMessage(Message m) throws Exception {
        Address fromAddr = m.getFrom()[0];
        assert fromAddr instanceof InternetAddress;

        String subj=m.getSubject();
        String type=m.getContentType();
        logger.info("Processing mail from %s, subject: %s, type: %s",
                fromAddr, subj, type);
        if(type.startsWith("multipart/")) {
            User u = UserFactory.getInstance()
                .getUser(((InternetAddress)fromAddr).getAddress());
            if(u == null) {
                logger.warn("Couldn't find a user from %s for subj %s",
                        fromAddr, subj);
            } else {
            	MailImage img=new MailImage(subj);
                processMultipart("", (MimeMultipart)m.getContent(), img);
                storeImage(u, img);
            }
        } else {
            logger.info("%s/%s contained nothing interesting",
                    fromAddr, subj);
        }
        m.setFlag(Flag.DELETED, true);
    }

    private void storeImage(User u, MailImage img) throws Exception {
    	Category cat=img.computeCategory(u);
    	if(cat == null) {
    		logger.warn("Couldn't find a usable category in %s", img);
    	} else {
    		// Create a PhotoImage from the raw data.
    		PhotoImage photoImage=new PhotoImage(img.getBytes());

    		// Get the new image ID
    		SavablePhotoImageData savable=new SavablePhotoImageData(photoImage);
    		// Populate the fields.
    		savable.setKeywords(img.getKeywords());
    		savable.setDescr(img.getInfo());
    		savable.setCatId(cat.getId());
    		savable.setTaken(new Date());
    		savable.setAddedBy(u);

    		PhotoImageDataFactory pidf=PhotoImageDataFactory.getInstance();
    		pidf.store(savable, true);

    		// Log it.
    		// XXX  I really would like a way to get a meaningful remote IP
    		// address.
    		Persistent.getPipeline().addTransaction(new PhotoLogUploadEntry(
    			u.getId(), savable.getId(), "127.0.0.1", "XMLRPC Image Upload"),
    			PhotoConfig.getInstance());
    	}
    }

    private void processMultipart(String base,
            MimeMultipart parts, MailImage img)
        throws Exception {

        logger.debug("Mime body type:  %s", parts.getContentType());

        for(int i=0; i < parts.getCount(); i++) {

            String partString = (base.length() == 0 ? "" : ".");
            MimeBodyPart bp = (MimeBodyPart) parts.getBodyPart(i);
            logger.debug(" part %s%s%d:  %s", base, partString, i,
                    bp.getContentType());

            if(bp.getContentType().toLowerCase().startsWith("multipart/")) {
                logger.debug("Multipart:  %s", bp);
                processMultipart(base + partString + i,
                    (MimeMultipart) bp.getContent(), img);

            } else {

            	String type=bp.getContentType().toLowerCase();
            	logger.info("Processing type: %s", type);
            	if(type.startsWith("text/plain")) {
            		InputStream is = bp.getInputStream();
            		try {
            			img.setInfo(new String(
            					StreamUtil.getInstance().getBytes(is)));
            		} finally {
            			CloseUtil.close(is);
            		}
            	} else if(type.startsWith("image/jpeg")) {
            		InputStream is=bp.getInputStream();
            		try {
            			img.setBytes(StreamUtil.getInstance().getBytes(is));
            		} finally {
            			CloseUtil.close(is);
            		}
            	} else {
            		logger.info("Not handling mime part %s%s%d:  ", base,
            				partString, i, type);
            	}
            } // Not a multipart type

        } // Looping through each part
    } // processMultipart() 

    private static class MailImage extends SpyObject {
    	private String keywords=null;
    	private String info=null;
    	private String catGuess="";
    	private byte[] bytes=null;

    	public MailImage(String kw) {
    		super();
    		keywords=kw.trim();
    	}

    	/**
    	 * Find the most appropriate category for this user based on the
    	 * contents of the email.
    	 * 
    	 * @param u the user for whom we want the category
    	 * @return the best-fit category
    	 */
    	public Category computeCategory(User u) throws Exception {
    		Category rv=null;
    		Collection<Category> allCats=CategoryFactory
    			.getInstance().getCatList(u.getId(),
    					CategoryFactory.ACCESS_WRITE);
    		String match="";
    		LCS lcs=LCS.getInstance();
    		for(Category c : allCats) {
    			String m=lcs.lcs(catGuess, c.getName().toLowerCase());
    			if(m.length() > match.length()) {
    				match=m;
    				rv=c;
    			}
    		}
    		getLogger().info(
    				"Chosen category is %s with guess ``%s'', match ``%s''",
    				rv, catGuess, match);
    		return rv;
    	}

    	public boolean complete() {
    		return keywords != null && info != null && bytes != null;
    	}

		public String getInfo() {
			return info;
		}
		public void setInfo(String to) {
			// Process the info and look for a category.
			Collection<String> lines=new ArrayList<String>();
			for(String s : to.split("\r?\n")) {
				// Skip the sig
				if(s.startsWith("--")) {
					break;
				}
				// Look for a category marker
				int pos=s.toLowerCase().indexOf("cat:");
				if(pos >= 0) {
					catGuess=s.substring(pos+4).toLowerCase();
				} else {
					// Otherwise, just add the line to the description.
					lines.add(s);
				}
			}
			info = SpyUtil.join(lines, "\n").trim();
		}
		public String getKeywords() {
			return keywords;
		}
		public byte[] getBytes() {
			return bytes;
		}
		public void setBytes(byte[] to) {
			bytes = to;
		}
    }

}
