<%@ taglib uri='/tlds/struts-template.tld' prefix='template' %>
<%@ taglib uri='/tlds/struts-html.tld' prefix='html' %>
<html:form action="/newuser" focus="profile">

	<div align="center">
		<p>
			Registering an account requires a valid profile ID.  If you
			don't have one, you probably shouldn't be here.
		</p>

		<html:errors/>

		<table>
			<tr>
				<td>Profile</td>
				<td><html:text property="profile"/></td>
			</tr>
			<tr>
				<td>Username</td>
				<td><html:text property="username"/></td>
			</tr>
			<tr>
				<td>Password</td>
				<td><html:password property="password"/></td>
			</tr>
			<tr>
				<td>Password (confirm)</td>
				<td><html:password property="pass2"/></td>
			</tr>
			<tr>
				<td>Real Name</td>
				<td><html:text property="realname"/></td>
			</tr>
			<tr>
				<td>Email address</td>
				<td><html:text property="email"/></td>
			</tr>
			<tr>
				<td colspan="2">
					<div align="center">
						<html:submit>Add Me</html:submit>
						<html:reset>Clear</html:reset>
					</div>
				</td>
			</tr>
		</table>
	</div>

</html:form>
