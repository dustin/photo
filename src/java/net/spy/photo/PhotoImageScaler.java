// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
// arch-tag: 1E730BDC-5D6D-11D9-ABA3-000A957659CC

package net.spy.photo;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;

import net.spy.SpyObject;

/**
 * Scales images.
 */
public class PhotoImageScaler extends SpyObject {

	private PhotoImage pi=null;

	/**
	 * Get an instance of PhotoImageScaler.
	 */
	public PhotoImageScaler(PhotoImage img) {
		super();
		this.pi=img;
	}

	/**
	 * Get a scaled image as a stream.
	 */
	public InputStream getJpegStream(PhotoDimensions dim, int quality)
		throws Exception {

		PhotoImage tmp=getScaledImage(dim, quality);
		return(new ByteArrayInputStream(tmp.getData()));
	}

	/**
	 * Get a scaled jpeg as a byte array input stream.
	 */
	public PhotoImage getScaledImage(PhotoDimensions dim, int quality)
		throws Exception {

		getLogger().debug("Scaling %s to %s", pi, dim);

		// Get the original image.
		Image image=Toolkit.getDefaultToolkit().createImage(pi.getData());

		// Make the dimensions have the proper aspect ratio
		PhotoDimensions sdim=PhotoUtil.scaleTo(pi, dim);

		// Scale it
		BufferedImage img=new BufferedImage(sdim.getWidth(), sdim.getHeight(),
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g=img.createGraphics();
		g.drawImage(image, 0, 0, sdim.getWidth(), sdim.getHeight(), null);

		// Write it out
		ByteArrayOutputStream os=new ByteArrayOutputStream();
		IIOImage toWrite=new IIOImage(img, null, null);
		ImageWriter iw=ImageIO.getImageWritersByFormatName("jpg").next();
		ImageWriteParam param=iw.getDefaultWriteParam();
		param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		param.setCompressionQuality(quality/100.0f);
		iw.setOutput(ImageIO.createImageOutputStream(os));
		iw.prepareWriteSequence(null);
		iw.writeToSequence(toWrite, param);

		PhotoImage ri=new PhotoImage(os.toByteArray());

		// flush the image.
		img.flush();

		return(ri);
	}

}
