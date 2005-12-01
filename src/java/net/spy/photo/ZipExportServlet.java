// Copyright (c) 2005  Dustin Sallings <dustin@spy.net>
// arch-tag: 400AA331-BFE9-429E-8BEF-E56525CD56F5

package net.spy.photo;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.spy.jwebkit.JWHttpServlet;
import net.spy.photo.search.SearchResults;
import net.spy.util.SpyUtil;

/**
 * Export images in zip format.
 */
public class ZipExportServlet extends JWHttpServlet {

	private static final String DATE_FMT="yyyy/MM";

	protected void doGetOrPost(HttpServletRequest req, HttpServletResponse res)
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
		ZipOutputStream zos=new ZipOutputStream(res.getOutputStream());
		zos.setComment("Photoservlet export for "
				+ sessionData.getUser().getRealname() + " on " + new Date());

		Collection<PhotoImageData> images=new ArrayList<PhotoImageData>(sr);
		try {
			addIndexes(images, zos);
			addStatic(zos);
			addImages(images, sessionData.getOptimalDimensions(), zos);
		} catch(IOException e) {
			throw e;
		} catch(Exception e) {
			throw new ServletException("Problem making zip file", e);
		}

		zos.close();
	}

	private void addStatic(ZipOutputStream zos) throws Exception {
		addFile(zos, "search.html");
		addFile(zos, "searchfun.js");
		addFile(zos, "prototype.js");
		addFile(zos, "offline.css");
	}

	private void addFile(ZipOutputStream zos, String filename) throws Exception {
		ZipEntry ze=new ZipEntry(filename);
		zos.putNextEntry(ze);
		InputStream is=getClass().getClassLoader().getResourceAsStream(filename);
        int bytesRead = 0;
        byte buffer[] = new byte[1024];
        while ((bytesRead = is.read(buffer)) >= 0) {
            zos.write(buffer, 0, bytesRead);
        }
        zos.closeEntry();
        is.close();
	}

	private void addImages(Collection<PhotoImageData> sr, PhotoDimensions dims,
			ZipOutputStream zos) throws Exception {

		SimpleDateFormat sdf=new SimpleDateFormat(DATE_FMT);
		CRC32 crc=new CRC32();

		getLogger().info("Adding " + sr.size() + " images");
		for(PhotoImageData pid : sr) {
			getLogger().info("Adding photos for " + pid.getId());
			PhotoImageHelper p=new PhotoImageHelper(pid.getId());

			ZipEntry ne=new ZipEntry("pages/" + sdf.format(pid.getTaken()) + "/"
					+ pid.getId() + "_normal."+ pid.getFormat().getExtension());
			byte ndata[]=p.getImage(dims).getData();
			ne.setSize(ndata.length);
			ne.setTime(pid.getTimestamp().getTime());
			crc.reset();
			crc.update(ndata);
			ne.setCrc(crc.getValue());
			zos.putNextEntry(ne);
			zos.write(ndata);
			zos.closeEntry();

			ZipEntry te=new ZipEntry("pages/" + sdf.format(pid.getTaken()) + "/"
					+ pid.getId() + "_tn."+ pid.getFormat().getExtension());
			byte tdata[]=p.getThumbnail().getData();
			te.setSize(tdata.length);
			te.setTime(pid.getTimestamp().getTime());
			crc.reset();
			crc.update(tdata);
			te.setCrc(crc.getValue());
			zos.putNextEntry(te);
			zos.write(tdata);
			zos.closeEntry();

			zos.flush();
		}
	}

	private void addIndexes(Collection<PhotoImageData> sr, ZipOutputStream zos)
		throws IOException {
		SimpleDateFormat sdf=new SimpleDateFormat(DATE_FMT);

		ZipEntry ze=new ZipEntry("searchdata.js");
		ze.setMethod(ZipEntry.DEFLATED);
		zos.putNextEntry(ze);

		ByteArrayOutputStream bos=new ByteArrayOutputStream();
		OutputStreamWriter osw=new OutputStreamWriter(bos);
		BufferedWriter bw=new BufferedWriter(osw);
		
		bw.write("var photloc=new Object();\n");

		final Map<String, ArrayList<String>> kws=
			new HashMap<String, ArrayList<String>>();
		for(PhotoImageData pid : sr) {
			getLogger().info("Processing image " + pid.getId());
			bw.write("photloc[" + pid.getId() + "]='"
					+ sdf.format(pid.getTaken()) + "';\n");

			for(Keyword kw : pid.getKeywords()) {
				String kwstring="\"" + kw.getKeyword() + "\"";
				ArrayList al=kws.get(kwstring);
				if(al == null) {
					al=new ArrayList();
					kws.put(kwstring, al);
				}
				al.add(String.valueOf(pid.getId()));
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
