<%@ taglib uri='/tlds/struts-template.tld' prefix='template' %>
<template:insert template='/templates/page.jsp'>
  <template:put name='title' content='Comments' direct='true'/>
  <template:put name='content' content='/content/_comments.jsp'/>
</template:insert>
