<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ page import="net.spy.photo.PhotoUtil" %>

<form method="post"
	action="<%= PhotoUtil.getRelativeUri(request, "/search.do") %>">

	<div>
		<input type="hidden" name="maxret" value="6"/>
		<input type="hidden" name="fieldjoin" value="and"/>
		<input type="hidden" name="keyjoin" value="and"/>
		<input type="hidden" name="order" value="a.ts"/>
		<input type="hidden" name="sdirection" value="desc"/>
		<input type="hidden" name="field" value="keywords"/>
		<input type="hidden" name="action" value="next"/>
		Quick Search:  <input id="kcSuggest" name="what" autocomplete="off"/>
		<input type="submit" value="Find"/>
		<div id="qsSuggestions" class="kcSuggestions" style="display:none;"></div>
	</div>
</form>
<script type="text/javascript">
	new Ajax.Autocompleter('kcSuggest','qsSuggestions',
		'<c:url value="/matchKeyword.do"/>', { tokens: ' '} );
</script>
<%-- arch-tag: E4A9A2E0-5D6F-11D9-BD4A-000A957659CC --%>
