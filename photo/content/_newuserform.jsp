<form method="POST" action="PhotoServlet">
	<input type="hidden" name="func" value="adduser"/>

	<div align="center">
		<p>
			Registering an account requires a valid profile ID.  If you
			don't have one, you probably shouldn't be here.
		</p>

		<table>
			<tr>
				<td>Profile</td>
				<td><input name="profile"/></td>
			</tr>
			<tr>
				<td>Username</td>
				<td><input name="username"/></td>
			</tr>
			<tr>
				<td>Password</td>
				<td><input type=password name="password"/></td>
			</tr>
			<tr>
				<td>Password (confirm)</td>
				<td><input type=password name="pass2"/></td>
			</tr>
			<tr>
				<td>Real Name</td>
				<td><input name="realname"/></td>
			</tr>
			<tr>
				<td>Email address</td>
				<td><input name="email"/></td>
			</tr>
			<tr>
				<td colspan="2">
					<div align="center">
						<input type="submit" value="Add Me"/>
						<input type="reset" value="Clear"/>
					</div>
				</td>
			</tr>
		</table>
	</div>

</form>
