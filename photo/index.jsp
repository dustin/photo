<%@ taglib uri='/tlds/struts-template.tld' prefix='template' %>
<template:insert template='/templates/page.jsp'>
  <template:put name='title' content='My Photo Album' direct='true'/>
  <template:put name='content' content='/content/_index.jsp'/>
</template:insert>
