<%@ page import="net.spy.photo.PhotoUtil" %>

<form method="POST"
	action="<%= PhotoUtil.getRelativeUri(request, "/search.do") %>">

	<input type="hidden" name="maxret" value="6"/>
	<input type="hidden" name="fieldjoin" value="and"/>
	<input type="hidden" name="keyjoin" value="and"/>
	<input type="hidden" name="order" value="a.ts"/>
	<input type="hidden" name="sdirection" value="desc"/>
	<input type="hidden" name="field" value="keywords"/>
	<input type="hidden" name="action" value="next"/>
	Quick Search:  <input name="what"/>
	<input type="submit" value="Find"/>
</form>

