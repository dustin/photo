// Copyright (c) 2005  Dustin Sallings <dustin@spy.net>

package net.spy.photo;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.spy.jwebkit.JWHttpServlet;
import net.spy.photo.impl.PhotoDimensionsImpl;
import net.spy.photo.search.SearchResults;
import net.spy.util.CloseUtil;
import net.spy.util.SpyToker;
import net.spy.util.SpyUtil;

/**
 * Export images in zip format.
 */
public class ZipExportServlet extends JWHttpServlet {

	private static final String PATH_FMT="yyyy/MM";
	private static final String DATE_FMT="yyyy/MM/dd";

	private String pageHTML=null;

	private boolean fullExport=false;

	@Override
	public void init(ServletConfig cf) throws ServletException {
		super.init(cf);
		try {
			InputStream is=getClass().getClassLoader().getResourceAsStream(
				"image.html");
			try {
				pageHTML=SpyUtil.getReaderAsString(new InputStreamReader(is));
			} finally {
				CloseUtil.close(is);
			}
		} catch (IOException e) {
			throw new ServletException("Can't initialize HTML pages", e);
		}
		String fullExportString=cf.getInitParameter("fullExport");
		if(fullExportString != null) {
			fullExport=Boolean.valueOf(fullExportString);
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		HttpSession ses=req.getSession(false);
		PhotoSessionData sessionData=(PhotoSessionData)ses.getAttribute(
			PhotoSessionData.SES_ATTR);
		SearchResults sr=sessionData.getResults();
		if(sr == null) {
			throw new ServletException("No results.");
		}
		
		res.setContentType("application/zip");
		res.setHeader("Content-Disposition",
				"attachment; filename=photoexport.zip");
		OutputStream os=null;
		ZipOutputStream zos=null;
		try {
			os=res.getOutputStream();
			zos=new ZipOutputStream(res.getOutputStream());
			zos.setComment("Photoservlet export for "
					+ sessionData.getUser().getRealname() + " on "
					+ new Date());

			Collection<PhotoImage> images=new ArrayList<PhotoImage>(
					sr.getAllObjects());
			addIndexes(images, zos);
			addStatic(zos);
			addImages(images, sessionData.getOptimalDimensions(), zos);
		} catch(IOException e) {
			throw e;
		} catch(Exception e) {
			throw new ServletException("Problem making zip file", e);
		} finally {
			CloseUtil.close(zos);
			CloseUtil.close(os);
		}
	}

	private void addStatic(ZipOutputStream zos) throws Exception {
		addFile(zos, "search.html");
		addFile(zos, "searchfun.js");
		addFile(zos, "prototype.js");
		addFile(zos, "offline.css");
	}

	private void addFile(ZipOutputStream zos, String filename)
		throws Exception {
		ZipEntry ze=new ZipEntry(filename);
		zos.putNextEntry(ze);
		InputStream is=getClass().getClassLoader().getResourceAsStream(
				filename);
		try {
			int bytesRead = 0;
			byte buffer[] = new byte[1024];
			while ((bytesRead = is.read(buffer)) >= 0) {
				zos.write(buffer, 0, bytesRead);
			}
		} finally {
			CloseUtil.close(is);
	        zos.closeEntry();
		}

	}

	private void addImages(Collection<PhotoImage> sr, PhotoDimensions dims,
			ZipOutputStream zos) throws Exception {

		PhotoConfig cf=PhotoConfig.getInstance();
		PhotoDimensions thumbdim= new PhotoDimensionsImpl(
				cf.get("thumbnail_size"));

		getLogger().info("Adding " + sr.size() + " images");
		for(PhotoImage pid : sr) {
			getLogger().info("Adding photos for " + pid.getId());

			addImage(dims, zos, pid, "_normal");

			// Add the thumbnail
			addImage(thumbdim, zos, pid, "_tn");

			if(fullExport) {
				addImage(null, zos, pid, "_full");
			}

			addImageHTML(zos, pid);
			zos.flush();
		}
	}

	private void addImage(PhotoDimensions dims, ZipOutputStream zos,
			PhotoImage pid, String suffix) throws Exception, IOException {
		PhotoImageHelper p=PhotoImageHelper.getInstance();
		SimpleDateFormat sdf=new SimpleDateFormat(PATH_FMT);
		CRC32 crc=new CRC32();
		ZipEntry ne=new ZipEntry("pages/" + sdf.format(pid.getTaken()) + "/"
				+ pid.getId() + suffix + "." + pid.getFormat().getExtension());
		byte ndata[]=p.getImage(pid, dims);
		ne.setSize(ndata.length);
		ne.setTime(pid.getTimestamp().getTime());
		crc.reset();
		crc.update(ndata);
		ne.setCrc(crc.getValue());
		zos.putNextEntry(ne);
		zos.write(ndata);
		zos.closeEntry();
	}

	private void addImageHTML(ZipOutputStream zos, PhotoImage pid)
		throws Exception {
		Map<String, String> stuff=new HashMap<String, String>();
		// XXX:  Fixme
		stuff.put("URL", "http://bleu.west.spy.net/photo/");
		stuff.put("ID", String.valueOf(pid.getId()));
		Collection<String> keywords=new ArrayList<String>();
		for(Keyword k : pid.getKeywords()) {
			keywords.add(k.getKeyword());
		}
		stuff.put("KEYWORDS", SpyUtil.join(keywords, " "));
		stuff.put("DESCR", pid.getDescr());
		stuff.put("YEAR", new SimpleDateFormat("yyyy").format(pid.getTaken()));
		stuff.put("MONTH", new SimpleDateFormat("MM").format(pid.getTaken()));
		stuff.put("TAKEN",
				new SimpleDateFormat("EEE, MMM d, yyyy").format(pid.getTaken()));

		SpyToker st=new SpyToker();
		String out=st.tokenizeString(pageHTML, stuff);

		SimpleDateFormat sdf=new SimpleDateFormat(PATH_FMT);
		ZipEntry ze=new ZipEntry("pages/" + sdf.format(pid.getTaken()) + "/"
				+ pid.getId() + ".html");
		zos.putNextEntry(ze);
		zos.write(out.getBytes());
		zos.closeEntry();
	}

	private void addIndexes(Collection<PhotoImage> sr, ZipOutputStream zos)
		throws IOException {
		SimpleDateFormat sdf=new SimpleDateFormat(DATE_FMT);

		ZipEntry ze=new ZipEntry("searchdata.js");
		ze.setMethod(ZipEntry.DEFLATED);
		zos.putNextEntry(ze);

		ByteArrayOutputStream bos=new ByteArrayOutputStream();
		OutputStreamWriter osw=new OutputStreamWriter(bos);
		BufferedWriter bw=new BufferedWriter(osw);
		
		bw.write("var photloc=new Object();\n");

		final Map<String, TreeSet<Integer>> kws=
			new HashMap<String, TreeSet<Integer>>();
		for(PhotoImage pid : sr) {
			getLogger().info("Processing image " + pid.getId());
			bw.write("photloc[" + pid.getId() + "]='"
					+ sdf.format(pid.getTaken()) + "';\n");

			for(Keyword kw : pid.getKeywords()) {
				String kwstring="\"" + kw.getKeyword() + "\"";
				TreeSet<Integer> al=kws.get(kwstring);
				if(al == null) {
					al=new TreeSet<Integer>();
					kws.put(kwstring, al);
				}
				al.add(pid.getId());
			}
		}

		ArrayList<String> keywords=new ArrayList<String>(kws.keySet().size());
		keywords.addAll(kws.keySet());
		Collections.sort(keywords, new Comparator<String>() {
			public int compare(String a, String b) {
				int rv=0;
				int s1=kws.get(a).size();
				int s2=kws.get(b).size();
				if(s1 > s2) {
					rv=-1;
				} else if(s1 < s2) {
					rv=1;
				} else {
					rv=a.compareTo(b);
				}
				return(rv);
			}});

		bw.write("\nvar keywords=[");
		bw.write(SpyUtil.join(keywords, ", "));
		bw.write("];\n");

		bw.write("\nvar imgs=new Array();\n");
		for(int i=0; i<keywords.size(); i++) {
			bw.write("imgs[" + i + "]=["
					+ SpyUtil.join(kws.get(keywords.get(i)), ", ") + "];\n");
		}

		bw.flush();
		osw.flush();
		bos.flush();

		zos.write(bos.toByteArray());
		zos.closeEntry();

		bw.close();
		osw.close();
		bos.close();
	}

}
