<jsp:useBean id="props" class="net.spy.photo.PhotoProperties" />

<%@ taglib uri='/tlds/struts-logic.tld' prefix='logic' %>
<%@ taglib uri='/tlds/struts-html.tld' prefix='html' %>
<%@ taglib uri='/tlds/struts-bean.tld' prefix='bean' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<p>

<div class="sectionheader">Property Editor</div>

<html:form action="/admin/saveProperties.do">
	<html:errors/>

	<table border="1">
		<tr>
			<th>Property Name</th>
			<th>Property Value</th>
		</tr>
		<logic:iterate id="i" type="java.util.Map.Entry"
			collection="<%= props.entrySet() %>">
			<tr>
				<td>
					<%= i.getKey() %>
				</td>
				<td>
					<html:text
						property="<%= (String)i.getKey() %>"
						size="30"
						value="<%= (String)i.getValue() %>"/>
				</td>
			</tr>
		</logic:iterate>
	</table>
	<br/>
	<html:submit>Save Properties</html:submit>
</html:form>

</p>
<%-- arch-tag: C9E35953-5D6F-11D9-BF9D-000A957659CC --%>
