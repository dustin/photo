<%@ taglib uri='/tlds/struts-logic.tld' prefix='logic' %>
<%@ taglib uri='/tlds/struts-html.tld' prefix='html' %>
<%@ taglib uri='/tlds/struts-bean.tld' prefix='bean' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<photo:admin explodeOnImpact="true"/>

<p>

<div class="sectionheader">Reporting</div>

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
