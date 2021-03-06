<%@ taglib uri='http://jakarta.apache.org/struts/tags-html' prefix='html' %>

<html:xhtml/>

<div>
<html:form action="/newuser" focus="profile">

	<div>
		<div>
			Registering an account requires a valid profile ID.  If you
			don't have one, you probably shouldn't be here.
		</div>


		<div class="centered">
		<html:errors/>

		<table class="leftAligned">
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
					<div>
						<html:submit>Add Me</html:submit>
						<html:reset>Clear</html:reset>
					</div>
				</td>
			</tr>
		</table>
		</div>
	</div>

</html:form>
</div>
