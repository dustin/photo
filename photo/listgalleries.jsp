<%@ taglib uri='/tlds/struts-template.tld' prefix='template' %>
<template:insert template='/templates/page.jsp'>
  <template:put name='title' content='Gallery List' direct='true'/>
  <template:put name='content' content='/content/_listgalleries.jsp'/>
</template:insert>
