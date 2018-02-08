<!DOCTYPE html>
<html>
<head>
    <title>Unit Avaiable</title>
    <link href="/webjars/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    
</head>
<body>
<div class="panel panel-default" height="100%">
  <table class="table">
   <th>address</th>
<#list unitList as unit>
  <tr>
   <td><a href="/mml-interface/${unit.id}/testToken" target="_blank">${unit.loginName!""}@${unit.serverIp!""}</a></td>
  </tr>
</#list>
</table>

</div>
</body>
</html>