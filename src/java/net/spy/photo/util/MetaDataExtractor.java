package net.spy.photo.util;

import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

import net.spy.SpyObject;
import net.spy.photo.PhotoUtil;

/**
 * Extract metadata from an image.
 */
public class MetaDataExtractor extends SpyObject {

	private static MetaDataExtractor instance=new MetaDataExtractor();

	private MetaDataExtractor() {
		super();
	}

	/**
	 * Get the singleton MetaDataExtractor instance.
	 */
	public static MetaDataExtractor getInstance() {
		return instance;
	}

	/**
	 * Perform the metadata extraction.
	 * 
	 * @param data the byte data
	 * @return the map of keys/values
	 * @throws Exception if the data cannot be read
	 */
	public Map<String, String> getMetaData(byte[] data) throws Exception {
		Map<String, String> metaData=new TreeMap<String, String>();
		ByteArrayInputStream bis=new ByteArrayInputStream(data);
		Metadata md=JpegMetadataReader.readMetadata(bis);
		for(Iterator<?> i=md.getDirectoryIterator(); i.hasNext();) {
			Directory d=(Directory)i.next();
			for(Iterator<?> ti=d.getTagIterator(); ti.hasNext();) {
				Tag t=(Tag)ti.next();
				Object o=metaData.put(t.getTagName(), t.getDescription());
				if(o != null) {
					getLogger().warn("Duplicate tag"
							+ ":  " + t.getTagName() + " -> " + o);
				}
			}
		}
		return metaData;
	}

	/**
	 * Parse the date from an image.
	 */
	public Date getDateTaken(byte[] data) throws Exception {
		Date rv=null;
		Map<String, String> exif=getMetaData(data);
		String tsStr=exif.get("Date/Time Original");
		if(tsStr == null) {
			tsStr=exif.get("Date/Time");
		}
		if(tsStr != null) {
			rv=PhotoUtil.parseDate(tsStr);
			getLogger().info("Parsed exif date from %s to %s", tsStr, rv);
		}
		return rv;
	}
}
