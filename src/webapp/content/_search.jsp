<%@ page import="net.spy.photo.Category" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>

<%@ taglib uri='http://jakarta.apache.org/struts/tags-logic' prefix='logic' %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-html' prefix='html' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<html:xhtml/>

<h1>Simple Search</h1>

<div>

<html:form action="/search.do">
	<p>
	<html:errors/>
	<html:hidden property="maxret" value="6"/>
	<html:hidden property="fieldjoin" value="and"/>
	<html:hidden property="keyjoin" value="and"/>
	<html:hidden property="order" value="a.ts"/>
	<html:hidden property="sdirection" value="desc"/>
	<fmt:message key="forms.search.simple.findimages"/>
	<html:select property="field">
		<html:option value="keywords">
			<fmt:message key="forms.search.simple.field.kw"/></html:option>
		<html:option value="descr">
			<fmt:message key="forms.search.simple.field.info"/></html:option>
	</html:select>
	<fmt:message key="forms.search.simple.contains"/>
	<input type="text" id="kcInput1" name="what"/>
	</p>
	<div id="kc1" class="kcSuggestions"
		style="display:none; width: 250px;"></div>
	<p>
		<html:submit>
			<fmt:message key="forms.search.simple.submit"/>
		</html:submit>
	</p>
</html:form>
<script type="text/javascript">
	new Ajax.Autocompleter('kcInput1','kc1',
		'<c:url value="/matchKeyword.do"/>', { tokens: ' '} );
</script>

</div>

<h1>Find image by ID</h1>

<div>

<form method="get" action="display.do">
	<div>
		ID:  <input name="id" size="6" />
		<html:submit>Lookup</html:submit>
	</div>
</form>

</div>

<h1>Advanced Search</h1>

<div>

<fmt:message key="forms.search.adv.pre"/>

<html:form action="/search.do">

	<fieldset>
		<legend><fmt:message key="forms.search.adv.cat"/></legend>
		<html:select property="cat" size="5" multiple="true">
			<photo:getCatList showViewable="true">
				<logic:iterate type="net.spy.photo.Category" id="i" name="catList">
					<html:option value="<%= "" + i.getId() %>">
						<%= i.getName() %></html:option>
				</logic:iterate>
			</photo:getCatList>
		</html:select>
	</fieldset>

	<fieldset>
		<legend><fmt:message key="forms.search.adv.textsearch"/></legend>
		<html:select property="field">
			<html:option value="keywords">
				<fmt:message key="forms.search.adv.keywords"/>
			</html:option>
			<html:option value="descr">
				<fmt:message key="forms.search.adv.info"/>
			</html:option>
		</html:select>

		<fmt:message key="forms.search.adv.contains"/>

		<html:select property="keyjoin">
			<html:option value="and">
				<fmt:message key="forms.search.adv.allof"/>
			</html:option>
			<html:option value="or">
				<fmt:message key="forms.search.adv.oneof"/>
			</html:option>
		</html:select>

		<input type="text" id="kcInput2" name="what"/>
		</fieldset>

		<div id="kc2" class="kcSuggestions"
			style="display:none; width:250px;"></div>

		<fieldset>
			<legend><fmt:message key="forms.search.adv.takenconst"/></legend>
			<fmt:message key="forms.search.adv.after"/>
				<html:text property="tstart"/>
			<fmt:message key="forms.search.adv.and"/>
			<fmt:message key="forms.search.adv.before"/>
				<html:text property="tend"/>
		</fieldset>
		<fieldset>
			<legend><fmt:message key="forms.search.adv.tsconst"/></legend>
			<fmt:message key="forms.search.adv.after"/>
				<html:text property="start"/>
			<fmt:message key="forms.search.adv.and"/>
			<fmt:message key="forms.search.adv.before"/>
				<html:text property="end"/>
		</fieldset>

		<fieldset>
			<legend><fmt:message key="forms.search.adv.dispops"/></legend>
			<fmt:message key="forms.search.adv.sortby"/>
			<html:select property="order">
				<html:option value="a.taken">taken</html:option>
				<html:option value="a.ts">added</html:option>
			</html:select>
			<fmt:message key="forms.search.adv.andshow"/>
			<html:select property="sdirection">
				<html:option value="">oldest</html:option>
				<html:option value="desc">newest</html:option>
			</html:select>
			<fmt:message key="forms.search.adv.imagesfirst"/>

			<br/>

			<fmt:message key="forms.search.adv.show"/>
			<html:select property="maxret">
				<html:option value="6">6</html:option>
				<html:option value="10">10</html:option>
			</html:select>
			<fmt:message key="forms.search.adv.imagespp"/>

			<br/>

			<fmt:message key="forms.search.adv.filter"/>
			<html:select property="filter">
				<html:option value="">
					<fmt:message key="forms.search.adv.filter.none"/>
				</html:option>
				<html:option value="onceamonth">
					<fmt:message key="forms.search.adv.filter.onceamonth"/>
				</html:option>
				<html:option value="onceaweek">
					<fmt:message key="forms.search.adv.filter.onceaweek"/>
				</html:option>
				<html:option value="onceaday">
					<fmt:message key="forms.search.adv.filter.onceaday"/>
				</html:option>
			</html:select>

			<br/>

			<fmt:message key="forms.search.adv.action"/>
			<html:select property="action">
				<html:option value="next">
					<fmt:message key="forms.search.adv.action.success"/>
				</html:option>
				<html:option value="showResults">
					<fmt:message key="forms.search.adv.action.showResults"/>
				</html:option>
			</html:select>
		</fieldset>

		<p>
			<html:submit>
				<fmt:message key="forms.search.adv.submit"/>
			</html:submit>
			<html:reset>
				<fmt:message key="forms.search.adv.reset"/>
			</html:reset>
		</p>

</html:form>
<script type="text/javascript">
	new Ajax.Autocompleter('kcInput2','kc2',
		'<c:url value="/matchKeyword.do"/>', { tokens: ' '} );
</script>

</div>
