<%@ page import="java.sql.*, net.spy.*, net.spy.photo.*" %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<%
	PhotoSessionData sessionData=
		(PhotoSessionData)session.getAttribute("photoSession");

	SpyDB photo=new SpyDB(new PhotoConfig());

	String query = "select name,id,catsum(id) as cs from cat\n"
		+ "where id in\n"
		+ "  (select cat from wwwacl where\n"
		+ "   (userid=? or userid=?) and canview=true)\n"
		+ " order by cs desc";
	PreparedStatement st = photo.prepareStatement(query);
	st.setInt(1, sessionData.getUser().getId());
	st.setInt(2, PhotoUtil.getDefaultId());
	ResultSet rs = st.executeQuery();
%>

<h1>Category View</h1>

<table border="1">
	<tr>
		<th>Category</th>
		<th>Entries</th>
	</tr>

<% while(rs.next()) { %>

<%
	String t=null;
	int number=rs.getInt("cs");
	if(number==1) {
		t=" image";
	} else {
		t=" images";
	}
	int id=rs.getInt("id");
	String name=rs.getString("name");

%>

	<tr>
		<td>
			<a href="search.do?order=a.ts&amp;sdirection=desc&amp;maxret=6&amp;cat=<%= id %>"><%= name %></a>
		</td>
		<td>
			<a href="search.do?order=a.ts&amp;sdirection=desc&amp;maxret=6&amp;cat=<%= id %>"><%= number %> <%=
				t %></a>
		</td>
	</tr>

<% } %>

<% photo.close(); %>

</table>
