<%@ page import="net.spy.photo.Category" %>
<%@ taglib uri='/tlds/struts-logic.tld' prefix='logic' %>
<%@ taglib uri='/tlds/struts-html.tld' prefix='html' %>
<%@ taglib uri='/tlds/struts-bean.tld' prefix='bean' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<p>

<div class="sectionheader">Simple Search</div>

<html:form action="/search.do">
	<html:errors/>
	<html:hidden property="maxret" value="6"/>
	<html:hidden property="fieldjoin" value="and"/>
	<html:hidden property="keyjoin" value="and"/>
	<html:hidden property="order" value="a.ts"/>
	<html:hidden property="sdirection" value="desc"/>
	<bean:message key="forms.search.simple.findimages"/>
	<html:select property="field">
		<html:option value="keywords">
			<bean:message key="forms.search.simple.field.kw"/></html:option>
		<html:option value="descr">
			<bean:message key="forms.search.simple.field.info"/></html:option>
	</html:select>
	<bean:message key="forms.search.simple.contains"/>
	<html:text property="what"/><br/>
	<html:submit>
		<bean:message key="forms.search.simple.submit"/>
	</html:submit>
</html:form>

</p>

<p>

<div class="sectionheader">Find image by ID</div>

<form method="GET" action="display.do">
	ID:  <input name="id" size="6">
	<html:submit>Lookup</html:submit>
</form>

</p>

<p>

<div class="sectionheader">Advanced Search</div>

<bean:message key="forms.search.adv.pre"/>

<html:form action="/search.do">

	<p>
		<bean:message key="forms.search.adv.cat"/>:<br/>
		<html:select property="cat" size="5" multiple="true">
			<photo:getCatList showViewable="true">
				<logic:iterate type="net.spy.photo.Category" id="i" name="catList">
					<html:option value="<%= "" + i.getId() %>">
						<%= i.getName() %></html:option>
				</logic:iterate>
			</photo:getCatList>
		</html:select>
	</p>
	<p>
		<html:select property="fieldjoin">
			<html:option value="and">
				<bean:message key="forms.search.adv.and"/>
			</html:option>
			<html:option value="or">
				<bean:message key="forms.search.adv.or"/>
			</html:option>
		</html:select>
		<html:select property="field">
			<html:option value="keywords">
				<bean:message key="forms.search.adv.keywords"/>
			</html:option>
			<html:option value="descr">
				<bean:message key="forms.search.adv.info"/>
			</html:option>
		</html:select>

		<bean:message key="forms.search.adv.contains"/>

		<html:select property="keyjoin">
			<html:option value="or">
				<bean:message key="forms.search.adv.oneof"/>
			</html:option>
			<html:option value="and">
				<bean:message key="forms.search.adv.allof"/>
			</html:option>
		</html:select>

		<html:text property="what"/><br/>

		<table>
			<tr>
				<td>
					<html:select property="tstartjoin">
						<html:option value="and">
							<bean:message key="forms.search.adv.and"/>
						</html:option>
						<html:option value="or">
							<bean:message key="forms.search.adv.or"/>
						</html:option>
					</html:select>
					<bean:message key="forms.search.adv.takensince"/>
					<html:text property="tstart"/>
				</td>
				
				<td>
					<html:select property="tendjoin">
						<html:option value="and">
							<bean:message key="forms.search.adv.and"/>
						</html:option>
						<html:option value="or">
							<bean:message key="forms.search.adv.or"/>
						</html:option>
					</html:select>
					<bean:message key="forms.search.adv.takenbefore"/>
					<html:text property="tend"/>
				</td>
			</tr>

			<tr>
				<td>
					<html:select property="startjoin">
						<html:option value="and">
							<bean:message key="forms.search.adv.and"/>
						</html:option>
						<html:option value="or">
							<bean:message key="forms.search.adv.or"/>
						</html:option>
					</html:select>
					<bean:message key="forms.search.adv.addedsince"/>
					<html:text property="start"/>
				</td>

				<td>
					<html:select property="endjoin">
						<html:option value="and">
							<bean:message key="forms.search.adv.and"/>
						</html:option>
						<html:option value="or">
							<bean:message key="forms.search.adv.or"/>
						</html:option>
					</html:select>
					<bean:message key="forms.search.adv.addedbefore"/>
					<html:text property="end"/>
				</td>
			</tr>
		</table>

		<bean:message key="forms.search.adv.sortby"/>
		<html:select property="order">
			<html:option value="a.taken">taken</html:option>
			<html:option value="a.ts">added</html:option>
		</html:select>
		<bean:message key="forms.search.adv.andshow"/>
		<html:select property="sdirection">
			<html:option value="">oldest</html:option>
			<html:option value="desc">newest</html:option>
		</html:select>
		<bean:message key="forms.search.adv.imagesfirst"/>

		<br/>

		<bean:message key="forms.search.adv.show"/>
		<html:select property="maxret">
			<html:option value="6">6</html:option>
			<html:option value="10">10</html:option>
		</html:select>
		<bean:message key="forms.search.adv.imagespp"/>

		<br/>

		<bean:message key="forms.search.adv.filter"/>
		<html:select property="filter">
			<html:option value="">
				<bean:message key="forms.search.adv.filter.none"/>
			</html:option>
			<html:option value="onceamonth">
				<bean:message key="forms.search.adv.filter.onceamonth"/>
			</html:option>
			<html:option value="onceaweek">
				<bean:message key="forms.search.adv.filter.onceaweek"/>
			</html:option>
			<html:option value="onceaday">
				<bean:message key="forms.search.adv.filter.onceaday"/>
			</html:option>
		</html:select>

		<br/>

		<bean:message key="forms.search.adv.action"/>
		<html:select property="action">
			<html:option value="next">
				<bean:message key="forms.search.adv.action.success"/>
			</html:option>
			<html:option value="showResults">
				<bean:message key="forms.search.adv.action.showResults"/>
			</html:option>
		</html:select>

		<br/>

		<html:submit>
			<bean:message key="forms.search.adv.submit"/>
		</html:submit>
		<html:reset>
			<bean:message key="forms.search.adv.reset"/>
		</html:reset>
	</p>

</html:form>

</p>
