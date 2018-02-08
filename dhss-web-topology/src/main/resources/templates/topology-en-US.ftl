<!DOCTYPE html>
<html>
<head lang="zh-cn">
	
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
	
    <link rel="stylesheet" href="./css/bootstrap.min.css" />
    <link rel="stylesheet" href="./css/nsn-home-style.css" />
	<link rel="stylesheet" href="./css/jquery.mCustomScrollbar.css" />
	
   <style>

        .node circle {
            fill:yellow ;
            stroke: #ccc;
            stroke-width: 1.5px;
            cursor: pointer;
        }

        .node {
            font: 10px sans-serif ;
        }
        .node image{
            cursor: pointer;
        }

        .link {
            fill: none;
            stroke: #ccc;
            stroke-width: 1.5px;
        }
		.fixed 
		{ 
			position:fixed;height:20px;width:100px;
			z-index:100000;text-align:center;top:10px;
		} 
    </style>
    <script type="text/javascript"  src="./jquery-1.11.1.min.js"></script>

	<!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
	<!--[if lt IE 9]>
	<script type="text/javascript" src="./html5shiv.min.js"></script>
	<script type="text/javascript" src="./respond.min.js"></script>
</head>


<body>

<!--//顶部导航栏-->
<div id="dhss" style="font-size: 12px; width:320px;position:fixed ;">
	&nbsp;&nbsp;&nbsp;&nbsp;
	<#list dhssList as dhss>
		<a role="button" href="topology?token=${token}&dhss=${dhss}&userName=${userName}&language=${language}" id="dhss${dhss_index}" tabindex="0" class="btn btn-default btn-xs">${dhss}</a>
	</#list>
</div>
<style>
	$(#nodePanel).css("height", document.body.scrollHeight-100+"px");
</style>
<div id="topologicalmap" style="">
 	
</div>
<input id="token" type="hidden" value="${token}" />
<input id="userName" type="hidden" value="${userName}" />
<input id="dhssName" type="hidden" value="${dhssName}" />
<input id="language" type="hidden" value="${language}" />
<div id='nodePanel' class="pull-right" style="font-size: 12px; width:320px;position:fixed ;right: 10px;top:20px;">
    <div class="panel panel-default">
        <div class="panel-heading"><span class="eNodeBName" style="width: auto"></span>&nbsp;
            <div class="pull-right">
            	<img class="waitFor" style="display:none" width="20" height="20" src="./images/wait_for.gif"  alt="wait for" />
                <img class="timg" style="display:none" width="20" height="20" src="./images/timg.jpg"  alt="" />
                <img class="warn" style="display:none" width="20" height="20" src="./images/warn.jpg"  alt="" />
                <img class="error" style="display:none" width="20" height="20" src="./images/error.jpg"  alt="" />
            	<a role="button" tabindex="0" class="btn btn-default btn-xs one-click">Connection Test</a>
            	<!-- <a role="button" tabindex="0" class="btn btn-default btn-xs one-click">Terminal</a> -->
                <a role="button" tabindex="0" class="btn btn-default btn-xs closePanel">Close</a>
            </div>
        </div>
        <div class="panel-body" style="padding: 5px"  style="margin:0px;"  >
            <style>
                #topKpi{
                    margin: 0px;
                    padding: 0px;
                    clear: both;
                }
                .customScrollbar{
                    min-height: 200px;
                    margin-left: 0px;
                    margin-right: 0px;
                }
                .customScrollbar li{
                    line-height: 18px;
                    font-size: 12px;
                    color: #777;
                    padding: 5px 12px;
                    cursor: pointer;

                }
                .customScrollbar li:hover{
                    background: #EBF1FB;
                    color: #0066CC;

                }
                .customScrollbar li.active{
                    background: #EBF1FB;
                    color:#0066CC;

                }
            </style>
            <ul class="" id="topKpi">
                <li>
                    <span class="kpiname">Position：</span>
                    <span class="kpinum location"> </span>
                </li>
                <li>
                    <span>Hardware Version：</span><span class="idsVersion"> </span>
                </li>
                <li>
                    <span class="kpiname">Software version：</span><span class="kpinum swVersion"> </span>
                </li>
                <li  style="display:none;">
                    <span class="kpiname">State：</span>
                    <span class="kpinum neState"> </span>
                </li>
                <li>
                    <span class="kpiname">Describe：</span><span class="kpinum remarks"></span>
                </li>
            </ul>
            <div style="clear: both"></div>
            <!--<hr style="border-top: 1px solid #dddddd;width:100%; margin-bottom: 10px">-->

            <ul class="nav nav-tabs nav-justified" id="mytab">
                <li role="presentation" class="active">
                    <a class="dropdown-toggle" style="padding: 10px 2px" data-toggle="tab" href="#tabvranKpi" role="button" aria-haspopup="true" aria-expanded="false">
                        KPI
                    </a>
                </li>
                <li role="presentation">
                    <a class="dropdown-toggle" style="padding: 10px 2px" data-toggle="tab" href="#tabvranAlarm" role="button" aria-haspopup="true" aria-expanded="false">
						Alarm
                    </a>
                </li>
            </ul>

            <div class="tab-content" style="padding-top: 5px;padding:0 15px;overflow-y:scroll;overflow-x:hidden;">
                <div class="tab-pane active" id="tabvranKpi">
                    <div id='resultWrap' class="customScrollbar">
                        <ul>
                             
                        </ul>
                    </div>
                </div>
                <div class="tab-pane" id="tabvranAlarm">
                    <div id='result2Wrap' class="customScrollbar">
                        <ul>
                            
                        </ul>
                    </div>
                </div>
            </div>


        </div>
    </div>
</div>
<div id='nodeAhubPanel' class="pull-right" style="display:none;font-size: 12px;position:fixed;right: 10px;bottom:5px;">
    <div class="panel panel-default">
        <div class="panel-heading"><span class="eNodeBName" style="width: auto"></span>&nbsp;
            <div class="pull-right">
            	<img class="waitFor" style="display:none" width="20" height="20" src="./images/wait_for.gif"  alt="wait for" />
                <img class="timg" style="display:none" width="20" height="20" src="./images/timg.jpg"  alt="" />
                <img class="warn" style="display:none" width="20" height="20" src="./images/warn.jpg"  alt="" />
                <img class="error" style="display:none" width="20" height="20" src="./images/error.jpg"  alt="" />
            	<a role="button" tabindex="0" class="btn btn-default btn-xs one-click">Connection Test</a>
            	<!-- <a role="button" tabindex="0" class="btn btn-default btn-xs one-click">Terminal</a> -->
                <a role="button" tabindex="0" class="btn btn-default btn-xs closePanel">Close</a>
            </div>
        </div>
        <div class="panel-body" style="padding: 5px"  style="margin:0px;"  >
            <style>
                #topKpi{
                    margin: 0px;
                    padding: 0px;
                    clear: both;
                }
                .customScrollbar{
                    min-height: 200px;
                    margin-left: 0px;
                    margin-right: 0px;
                }
                .customScrollbar li{
                    line-height: 18px;
                    font-size: 12px;
                    color: #777;
                    padding: 5px 12px;
                    cursor: pointer;

                }
                .customScrollbar li:hover{
                    background: #EBF1FB;
                    color: #0066CC;

                }
                .customScrollbar li.active{
                    background: #EBF1FB;
                    color:#0066CC;

                }
                #table-5 thead th {
                    background-color: #EBF1FB;
                    color: #000;
                    border-bottom-width: 0;
                }

                /* Column Style */
                #table-5 td {
                    color: #000;
                    text-align: center;
                }
                /* Heading and Column Style */
                #table-5 tr, #table-5 th {
                    border-width: 1px;
                    border-style: solid;
                    border-color: #EBF1FB;
                }

                /* Padding and font style */
                #table-5 td, #table-5 th {
                    line-height: 16px;
                    padding: 5px 2px;
                    font-size: 12px;
                    font-family: Verdana;

                }
            </style>
            <div id='result3Wrapa' class="customScrollbar" style="padding:0 15px;overflow:scroll;overflow-y:hidden;">
            	<ul class="">
	                <li>
	                    <span class="kpiname">Position：</span>
	                    <span class="location"> </span>
	                </li>
	                <li>
	                    <span>Hardware Version：</span><span class="idsVersion"> </span>
	                </li>
	                <li>
	                    <span class="kpiname">Software version：</span><span class="swVersion"> </span>
	                </li>
	                <li style="display:none;">
	                    <span class="kpiname">State：</span>
	                    <span class="neState"> </span>
	                </li>
	                <li>
	                    <span class="kpiname">Describe：</span><span class="remarks"></span>
	                </li>
	            </ul>
                <TABLE id="table-5" style="BORDER-COLLAPSE: collapse;" width="800px">
                    <thead>
                    <th width="100px">
                        <div align=center>Port</div></>
                    <th width="150px">
                        <div align=center>Mode</div></th>
                    <th width="150px">
                        <div align=center>LAN</div></th>
                    <th width="150px">
                        <div align=center>VLAN ID</div></th>
                    <th width="150px">
                        <div align=center>IP address</div></th>
                    <th width="150px">
                        <div align=center>Cable to Equipment</div></th>
                    <th width="150px">
                        <div align=center>Cable to Port</div></th>
                    <th width="150px">
                        <div align=center>Up Link IP Address</div></th></>

                    </thead>
                    <tbody id="ahubContent">
                    </tbody>
                </TABLE>

            </div>
            
        </div>
    </div>
</div>
<div style="clear: both"></div>
<script type="text/javascript" src="./d3.v3.min.js"></script>
<script src="./bootstrap.min.js"></script>

<!--自定义滚动条-->
<script type="text/javascript" src="./jquery.mCustomScrollbar.concat.min.js"></script>
<script type="text/javascript" src="./d3topological.js"></script>

</body>
</html>