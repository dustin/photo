<%@ taglib uri='/tlds/struts-template.tld' prefix='template' %>
<template:insert template='/templates/page.jsp'>
  <template:put name='title' content='Log View' direct='true'/>
  <template:put name='content' content='/content/_logview.jsp'/>
</template:insert>
