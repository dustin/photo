<%@ taglib uri='/tlds/struts-template.tld' prefix='template' %>
<template:insert template='/templates/page.jsp'>
  <template:put name='title' content='Comment on an Image' direct='true'/>
  <template:put name='content' content='/content/_commentinput.jsp'/>
</template:insert>
