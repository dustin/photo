<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles" prefix="tiles" %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html:html xhtml="true">

	<head>
		<title><tiles:insert attribute='errormessage'/></title>
		<photo:stylesheet url="/css/layout.css"/>
	</head>

	<body>
		<h1><tiles:insert attribute='errormessage'/></h1>

		<p>
			The document you requested is unavailable to you.
		</p>
	</body>
</html:html>

<%-- arch-tag: 166D9540-46A0-4993-93E6-1D8C6447CD84 --%>
