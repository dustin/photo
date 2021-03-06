<%@ taglib uri='http://jakarta.apache.org/struts/tags-logic' prefix='logic' %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-html' prefix='html' %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-bean' prefix='bean' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<html:xhtml/>

<h1>Reporting</h1>

<ul>
	<li><photo:link url="/report/logins.do"
		message="reporting.links.logins"/></li>
	<li><photo:link url="/report/failedLogins.do"
		message="reporting.links.failedlogins"/></li>
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
	<li><photo:link url="/report/recentVotes.do"
		message="reporting.links.recentVotes"/></li>
	<li><photo:link url="/report/mostVoted.do"
		message="reporting.links.mostVoted"/></li>
</ul>
