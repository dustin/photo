<%@ taglib uri='/tlds/struts-template.tld' prefix='template' %>
<template:insert template='/templates/page.jsp'>
  <template:put name='title' content='User Administration' direct='true'/>
  <template:put name='content' content='/content/admin/_users.jsp'/>
</template:insert>
