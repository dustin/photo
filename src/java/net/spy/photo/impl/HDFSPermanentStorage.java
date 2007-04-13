package net.spy.photo.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashSet;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import net.spy.SpyObject;
import net.spy.photo.PermanentStorage;
import net.spy.photo.Persistent;
import net.spy.photo.PhotoConfig;
import net.spy.photo.PhotoImage;
import net.spy.photo.PhotoImageFactory;
import net.spy.util.CloseUtil;

/**
 * Hadoop DFS permanent image storage.
 */
public class HDFSPermanentStorage extends SpyObject
	implements PermanentStorage {

	private String prefix=null;
	private Configuration hadoopConf = null;

	public HDFSPermanentStorage() {
		String dn = PhotoConfig.getInstance().get("hadoop.config.name");
		assert dn != null : "hdfs config name not configured";
		getLogger().info("Name node:  %s", dn);

		hadoopConf = new Configuration();
		hadoopConf.set("fs.default.name", dn);

		prefix="/photo" + Persistent.getContextPath() + "/";
	}

	public void init() throws Exception {
		getFilesystem().mkdirs(new Path(prefix));
	}

	private FileSystem getFilesystem() throws IOException {
		FileSystem rv = FileSystem.get(hadoopConf);
		assert rv != null : "No filesystem.";
		return rv;
	}

	private void copyStream(InputStream in, OutputStream out)
			throws IOException {
		byte buf[] = new byte[4096];
		int bytesRead;
		while ((bytesRead = in.read(buf)) != -1) {
			out.write(buf, 0, bytesRead);
		}
	}

	private Path getFile(PhotoImage pi) {
		return new Path(prefix + pi.getId() + "."
				+ pi.getFormat().getExtension());
	}

	public byte[] fetchImage(PhotoImage pi) throws Exception {
		FileSystem fs = getFilesystem();
		FSDataInputStream is = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream(pi.getSize());
		try {
			is = fs.open(getFile(pi));
			copyStream(is, bos);
		} finally {
			CloseUtil.close(is);
			CloseUtil.close(bos);
			fs.close();
		}
		return bos.toByteArray();
	}

	public Collection<Integer> getMissingIds() throws Exception {
		Collection<Integer> rv=new HashSet<Integer>();
		for(PhotoImage i : PhotoImageFactory.getInstance().getObjects()) {
			rv.add(i.getId());
		}
		getLogger().warn("getMissingIds is not implemented");
		FileSystem fs=getFilesystem();
		try {
			for(Path p : fs.listPaths(new Path(prefix))) {
				String n=p.getName();
				int dot=n.indexOf('.');
				assert dot > 0 : "Unexpected path: " + p;
				String num=n.substring(0, dot);
				assert num.indexOf('.') < 0 : "Substringed wrong";
				rv.remove(new Integer(num));
			}
		} finally {
			fs.close();
		}
		return rv;
	}

	public void storeImage(PhotoImage pi, byte[] data) throws Exception {
        FileSystem fs=getFilesystem();
        FSDataOutputStream out=null;
        InputStream in=new ByteArrayInputStream(data);
        try {
            out = fs.create(getFile(pi));
            copyStream(in, out);
        } finally {
            CloseUtil.close(in);
            CloseUtil.close(out);
            fs.close();
        }
	}

}
