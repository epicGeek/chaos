<!DOCTYPE html>
<html>
<head>
    <title>Console via DHSS</title>
    <link href="/oca/bootstrap.min.css" rel="stylesheet">
    
    <link href="/oca/xterm.css" rel="stylesheet">
    <link href="/oca/main.css" rel="stylesheet">
    
    <script src="/oca/jquery.min.js"></script>
    <script src="/oca/sockjs.min.js"></script>
    <script src="/oca/stomp.min.js"></script>
    <script src="/oca/xterm.js"></script>
    <script src="/oca/attach.js"></script>
    <script src="/oca/overlay.js"></script>
    <script src="/oca/utf8.js"></script>
    <script src="/oca/fit.js"></script>
    <script src="/oca/app.js"></script>
</head>
<body>
<div class="panel panel-default" height="100%">
  <div class="panel-heading">${equipmentUnit.unitName!""} [ ${equipmentUnit.serverIp!""} ]</div>
  <div class="panel-body" >
	<div id="terminal-container"></div>
  </div>
  <script>
     var token='${token!""}';
     var sessionId='${sessionId!""}';
  </script>
</div>
</body>
</html>