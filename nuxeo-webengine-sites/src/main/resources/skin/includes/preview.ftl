<#macro wikiPreview>
<html>
<head>
  <title>Preview of ${Document.title}</title>
  <link rel="stylesheet" href="${skinPath}/css/webengine.css" type="text/css" media="screen" charset="utf-8">
  <link rel="stylesheet" href="${skinPath}/css/preview.css" type="text/css" media="screen" charset="utf-8">
  <link rel="stylesheet" href="${skinPath}/css/wiki.css" type="text/css" media="screen" charset="utf-8">
</head>

<body>
  <div id="main">
    <div class="closeWindow">
      <form>
        <input type="button" value=" Close this window " onclick="self.close();" />
      </form>
    </div>
    <div class="main-content">
      <h1>${This.title}</h1>

      <@wiki>${Context.request.getParameter('wiki_editor')}</@wiki>
    </div>
    <div class="closeWindow">
      <form>
        <input type="button" value=" Close this window " onclick="self.close();" />
      </form>
    </div>
  </div>
</body>
</html>

</#macro>