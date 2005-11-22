<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-logic' prefix='logic' %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-html' prefix='html' %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-bean' prefix='bean' %>

<html:xhtml/>

<h1>Bulk Keyword Fixups</h1>

<c:if test="${not empty updated}">
	<div id="message" class="message">
		Updated <fmt:formatNumber value="${updated}"/> images.
	</div>
	<script type="text/javascript">
		Event.observe(window, 'load', function() {
			new Effect.Highlight($("message"));
			}, false);
	</script>
</c:if>

<html:errors/>

<form method="post" action="<c:url value='/admin/bulkkwsave.do'/>">
	<div>
		<label for="match">Match keywords</label>
		<input type="text" name="match" id="match"/>
	</div>
	<div>
		<label for="modify">Modify keywords</label>
		<input type="text" name="modify" id="modify"/>
	</div>
	<div>
		<input type="submit" value="Save"/>
	</div>
</form>
<%-- arch-tag: 5D86065B-4D2C-403B-95F7-2827436826DD --%>
