<%@ taglib uri='/tlds/struts-template.tld' prefix='template' %>
<template:insert template='/templates/page.jsp'>
  <template:put name='title' content='Search Results (JSP)' direct='true'/>
  <template:put name='content' content='/content/_results.jsp'/>
</template:insert>
