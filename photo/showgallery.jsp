<%@ taglib uri='/tlds/struts-template.tld' prefix='template' %>
<template:insert template='/templates/page.jsp'>
  <template:put name='title' content='Display a Gallery' direct='true'/>
  <template:put name='content' content='/content/_showgallery.jsp'/>
</template:insert>
