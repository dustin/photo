<%@ taglib uri='/tlds/struts-template.tld' prefix='template' %>
<template:insert template='/templates/page.jsp'>
  <template:put name='title' content='Image Uploaded' direct='true'/>
  <template:put name='content' content='/content/_uploaded.jsp'/>
</template:insert>
