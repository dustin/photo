<%@ taglib uri='/tlds/struts-template.tld' prefix='template' %>
<%@ taglib uri='/tlds/photo.tld' prefix='photo' %>

<template:insert template='/templates/page.jsp'>
  <template:put name='title' content='Image Display' direct='true'/>
  <photo:admin>
    <template:put name='content' content='/content/_displayAdmin.jsp'/>
  </photo:admin>
  <photo:admin negate="true">
    <template:put name='content' content='/content/_display.jsp'/>
  </photo:admin>
</template:insert>
