<%@ taglib uri='http://jakarta.apache.org/struts/tags-logic' prefix='logic' %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-html' prefix='html' %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-bean' prefix='bean' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<html:xhtml/>

<h1>Reporting</h1>

<ul>
	<li><photo:link url="/report/userCatXd.do"
		message="reporting.links.userCatXd"/></li>
	<li><photo:link url="/report/userCat.do"
		message="reporting.links.userCat"/></li>
	<li><photo:link url="/report/topImagesXd.do"
		message="reporting.links.topImagesXd"/></li>
	<li><photo:link url="/report/topImages.do"
		message="reporting.links.topImages"/></li>
	<li><photo:link url="/report/mostActiveUsersXd.do"
		message="reporting.links.mostActiveUsersXd"/></li>
	<li><photo:link url="/report/mostActiveUsers.do"
		message="reporting.links.mostActiveUsers"/></li>
</ul>

<%-- arch-tag: CB429078-5D6F-11D9-80C7-000A957659CC --%>
