<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<!-- Change password form -->
<xsl:template match="change_password_form">

	<form method="POST" action="{/page/meta_stuff/self_uri}">
		<input type="hidden" name="func" value="changepw"/>
		<center>
		<table border="0">
			<tr>
				<td>Old Password:</td>
				<td><input type="password" name="oldpw"/></td>
			</tr>
			<tr>
				<td>New Password:</td>
				<td><input type="password" name="newpw1"/></td>
			</tr>
			<tr>
				<td>New Password (confirm):</td>
				<td><input type="password" name="newpw2"/></td>
			</tr>
			<tr>
				<td align="center" colspan="2">
					<input type="submit" value="Set Password"/>
				</td>
			</tr>
		</table>
		</center>
	</form>

</xsl:template>

<!-- Authenticate form -->
<xsl:template match="auth_form">
	<form method="POST" action="{/page/meta_stuff/self_uri}">
		<input type="hidden" name="func" value="setcred"/>

		<table>
			<tr>
				<td>Username:</td><td><input name="username" size="8"/></td>
			</tr>
			<tr>
				<td>Password:</td>
				<td><input type="password" name="password"/></td>
			</tr>
		</table>
		<input type="submit" value="Authenticate"/>
		<input type="reset" value="Clear"/>
	</form>

	<p/>

	<a href="{/page/meta_stuff/self_uri}?func=setadmin">Request
		Administrative Privileges</a>
	<br/>
	<a href="{/page/meta_stuff/self_uri}?func=changepwform">Change Password</a>
</xsl:template>

<!-- This is the form for adding new images. -->
<xsl:template match="add_form">
	<p>
		<xsl:call-template name="section_header">
			<xsl:with-param name="title">
				Add a Photo
			</xsl:with-param>
		</xsl:call-template>

		<xsl:choose>
			<xsl:when test="/page/meta_stuff/photo_user/canadd">
			</xsl:when>
			<xsl:otherwise>
				<center>
					<blink>
						<font size="+3"
							color="red">You have no permission to add images.</font>
					</blink>
				</center>
			</xsl:otherwise>
		</xsl:choose>

		<form method="POST" enctype="multipart/form-data"
			action="{/page/meta_stuff/self_uri}">
			<input type="hidden" name="func" value="addimage"/>
			<table border="0" width="100%">

				<tr>
					<td align="left" width="50%">
						<table border="0">
						<tr>
							<td>Category:</td>
							<td>
								<select name="category" size="5">
									<xsl:for-each select="cat_list/option">
										<option value="{@value}">
											<xsl:value-of select="."/>
										</option>
									</xsl:for-each>
								</select>
							</td>
						</tr>
						</table>
					</td>
					<td align="right" width="50%">
						<table border="0">

						<tr>
							<td>Date Taken:</td>
							<td><input name="taken" value="{today}"/></td>
						</tr>

						<tr>
							<td>Keywords:</td>
							<td><input name="keywords"/></td>
						</tr>

						<tr>
							<td>Picture:</td>
							<td><input type="file" name="picture"/></td>
						</tr>
						</table>
					</td>
				</tr>

			</table>

			<center>
				<table border="0">
					<tr>
						<td>
							Short Description:<br/>
							<textarea name="info" cols="60" rows="5" wrap="hard"></textarea>
						</td>
						<td>
						</td>
					</tr>
				</table>

				<input type="submit" value="Add Image"/>
				<input type="reset" value="Clear"/>
			</center>

		</form>
	</p>
</xsl:template>

<!-- Advanced search form -->
<xsl:template match="find_form">

	<xsl:call-template name="simple_search_form"/>
	<xsl:call-template name="by_id_search_form"/>
	<xsl:call-template name="advanced_search_form"/>

</xsl:template>

<xsl:template name="by_id_search_form">
<p>

		<xsl:call-template name="section_header">
			<xsl:with-param name="title">
				Find Image by ID
			</xsl:with-param>
		</xsl:call-template>

		<form method="GET" action="{/page/meta_stuff/self_uri}">
			<input type="hidden" name="func" value="display"/>
			ID:  <input name="id" size="6"/>
			<input type="submit" value="Lookup"/>
		</form>

</p>
</xsl:template>

<xsl:template name="simple_search_form">
	<p>

		<xsl:call-template name="section_header">
			<xsl:with-param name="title">
				Simple Search
			</xsl:with-param>
		</xsl:call-template>

		<form method="POST" action="{/page/meta_stuff/self_uri}">
			<input type="hidden" name="func" value="search"/>
			<input type="hidden" name="maxret" value="6"/>
			<input type="hidden" name="fieldjoin" value="and"/>
			<input type="hidden" name="keyjoin" value="and"/>
			<input type="hidden" name="order" value="a.ts"/>
			<input type="hidden" name="sdirection" value="desc"/>
			Find all images whose
			<select name="field">
				<option value="keywords">Keyword</option>
				<option value="descr">Info (slow)</option>
			</select>
			contains
			<input name="what"/><br/>
			<input type="submit" value="Find"/>
		</form>

	</p>
</xsl:template>

<xsl:template name="advanced_search_form">
	<p>
		<xsl:call-template name="section_header">
			<xsl:with-param name="title">
				Advanced Search
			</xsl:with-param>
		</xsl:call-template>

		Only fields that are filled out will be used, default search
		will return all images.

		<form method="POST" action="{/page/meta_stuff/self_uri}">
			<input type="hidden" name="func" value="search"/>
			<p>
				Category:<br/>
				<select name="cat" size="5" multiple="">
					<xsl:for-each select="cat_list/option">
						<option value="{@value}">
							<xsl:value-of select="."/>
						</option>
					</xsl:for-each>
				</select>
			</p>
			<p>
				<select name="fieldjoin">
					<option value="and">and</option>
					<option value="or">or</option>
				</select>
				<select name="field">
					<option value="keywords">Keyword</option>
					<option value="descr">Info</option>
				</select>

				contains

				<select name="keyjoin">
					<option value="or">one of</option>
					<option value="and">all of</option>
				</select>

				<input name="what"/><br/>

				<table>
					<tr>

						<td>
							<select name="tstartjoin">
								<option value="and">and</option>
								<option value="or">or</option>
							</select>
							was taken since (date)
							<input name="tstart"/>
						</td>

						<td>
							<select name="tendjoin">
								<option value="and">and</option>
								<option value="or">or</option>
							</select>
							was taken before (date)
							<input name="tend"/>
						</td>

					</tr>

					<tr>

						<td>
							<select name="startjoin">
								<option value="and">and</option>
								<option value="or">or</option>
							</select>
							was added since (date)
							<input name="start"/>
						</td>

						<td>
							<select name="endjoin">
								<option value="and">and</option>
								<option value="or">or</option>
							</select>
							was added before (date)
							<input name="end"/>
						</td>

					</tr>
				</table>

				Sort by when the picture was
				<select name="order">
					<option value="a.taken">taken</option>
					<option value="a.ts">added</option>
				</select>
				and show
				<select name="sdirection">
					<option value="">oldest</option>
					<option value="desc">newest</option>
				</select>
				images first.

				<br/>

				Show
				<select name="maxret">
					<option value="6">6</option>
					<option value="10">10</option>
				</select>
				images per page.

				<br/>

				<input type="submit" value="Find"/>
				<input type="reset" value="Clear Form"/>

			</p>
		</form>

	</p>
</xsl:template>

</xsl:stylesheet>
