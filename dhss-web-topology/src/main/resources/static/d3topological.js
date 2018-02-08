/**
 * Created by john on 2016/8/2.
 */
//位置参数

//var global_url = "";

var unitId = "";
var isTesting = false;
$(function () {
	$(".one-click").hide();
//	$.ajax({
//        url: "resource/global_url/"+$("#token").val(),
//        cache: false,
//        success: function(data){
//        	$.each(data,function(index,item){
//        		if(item["apiName"] == "ONE_CLICK_URL"){
//        			global_url = item["apiURL"] + 'mml-interface/';
//        		}
//        	})
//        }
//    });
	
	$(".one-click").on("click",function(){
		$(".waitFor").show();
		$(".timg,.warn,.error").hide();
		isTesting = true;
		$.ajax({
	        url: "connection/test/"+unitId,
	        success: function(data){
	        	$(".waitFor").hide();
	        	if(data != null && data != ""){
		        	if(data.indexOf("100%") != -1) $(".error").show();
		        	else if(data.indexOf("0%") != -1) $(".timg").show();
		        	else  $(".warn").show();
	        	}
	        },complete:function(data){
	        	isTesting = false;
	        }
	    });
	})
//    var nename = "";
//    if(getQueryString("NENAME")){
//        nename = getQueryString("NENAME");
//        nodePanelObj.init();
//    }
	nename = $("#dhssName").val();
    nodePanelObj.init();

    var margin = {top: 20, right: 120, bottom: 20, left: 120},
        width = 1000 - margin.right - margin.left,
        height = 3000 - margin.top - margin.bottom;

    var i = 0,
        duration = 750,
        root;
// 声明树布局
    var tree = d3.layout.tree()
        .size([height, width]);
// 指定为径向布局
    var diagonal = d3.svg.diagonal()
        .projection(function(d) { return [d.y, d.x]; });

    var svg = d3.select("#topologicalmap").append("svg")
        .attr("width", width + margin.right + margin.left)
        .attr("height", height + margin.top + margin.bottom)
        .append("g")
        .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

//    d3.json("dhss-result/"+$("#token").val()+"/"+nename, function(error, flare) {
//
//        root = flare.result[0];
////        root = topoData;
//        root.x0 = height / 2;
//        root.y0 = 0;
//        function collapse(d) {
//            if (d.children) {
//                console.log(d);
//                d._children = d.children;
//                d._children.forEach(collapse);
//                d.children = null;
//            }
//        }
//        function collapseroot(d) {
//            if (d.children) {
//                d.children.forEach(collapse);
//            }
//        }
//        root.children.forEach(collapseroot);
//        update(root);
//    });
    
    $.ajax({
        url: "dhss-result/"+$("#token").val()+"/"+nename,
        cache: false,
        success: function(data){
        	root = data.result[0];
//          root = topoData;
          root.x0 = height / 2;
          root.y0 = 0;
          function collapse(d) {
              if (d.children) {
                  d._children = d.children;
                  d._children.forEach(collapse);
                  d.children = null;
              }
          }
          function collapseroot(d) {
              if (d.children) {
                  d.children.forEach(collapse);
              }
          }
//          root.children.forEach(collapseroot);
          update(root);
        }
    });

    d3.select(self.frameElement).style("height", "800px");
    function update(source){
        var nodes = tree.nodes(root).reverse(),
            links = tree.links(nodes);

        nodes.forEach(function(d) {
            d.y = d.depth * 160;
        });

        var node = svg.selectAll("g.node")
            .data(nodes, function(d) {
                return d.id
                    || (d.id = ++i);
            });

        var nodeEnter = node.enter().append("g")
            .attr("class", "node")
            .attr("transform", function(d) { return "translate(" + source.y0 + "," + source.x0 + ")"; });
        nodeEnter.append("circle")
            .attr("r", 1e-6)
            .style("fill", function(d) { return d._children ? "lightsteelblue" : "#fff"; })
            .on("click", click);
        nodeEnter.append("svg:image")
            .attr("xlink:href", function (d) {
            	
            	if(d.iconLevel==0){
                    return "./images/iconALLHSS.png";
                }else if(d.iconLevel==1){
                    return "./images/iconSite.png";
                }else if(d.iconLevel==2){
                    return "./images/iconHSS.png";
                }else if(d.iconLevel==3){
                    return "./images/iconGWF.png";
                }else if(d.iconLevel==4){
                	return "./images/iconHSSUNIT_"+d.alarm+".png";
//                	if(d.alarm){
//                		return "../images/iconHSSUNITred.png";
//                	}else{
//                		return "../images/iconHSSUNIT.png";
//                	}
                }else{
                    return "./images/iconWIFI.png";
                }
            	
//                if(getClassLevel(d.netype)==0){
//                    return "../images/iconALLHSS.png";
//                }else if(getClassLevel(d.netype)==1){
//                    return "../images/iconSite.png";
//                }else if(getClassLevel(d.netype)==2){
//                    return "../images/iconHSS.png";
//                }else if(getClassLevel(d.netype)==3){
//                    return "../images/iconGWF.png";
//                }else if(getClassLevel(d.netype)==4){
//                    return "../images/iconHSSUNIT.png";
//                }else{
//                    return "../images/iconWIFI.png";
//                }

            })
            .attr("x", "-36px")
            .attr("y", "-16px")
            .attr("width", "32px")
            .attr("height", "32px").on("click", click2);;



        nodeEnter.append("text")
            .attr("x", function(d) { return d.children || d._children ? -36 : 10; })
            .attr("dy", ".35em")
            .attr("text-anchor", function(d) { return d.children || d._children ? "end" : "start"; })
            .text(function(d) { return d.nename; })
            .style("fill-opacity", 1e-6);

        //(2-5)过渡到新位置
        var nodeUpdate = node.transition()
            .duration(duration)
            .attr("transform", function(d) { return "translate(" + d.y + "," + d.x + ")"; });

        nodeUpdate.select("circle")
            .attr("r", 4.5)
            .style("fill", function(d) { return d._children ? "lightsteelblue" : "#fff"; });

        nodeUpdate.select("text")
            .style("fill-opacity", 1);

        var nodeExit = node.exit().transition()
            .duration(duration)
            .attr("transform", function(d) {
                return "translate(" + source.y + "," + source.x + ")";
            })
            .remove();

        nodeExit.select("circle")
            .attr("r", 1e-6);

        nodeExit.select("text")
            .style("fill-opacity", 1e-6);

        var link = svg.selectAll("path.link")
            .data(links, function(d) { return d.target.id; });

        link.enter().insert("path", "g")
            .attr("class", "link")
            .attr("d", function(d) {
                var o = {x: source.x0, y: source.y0};
                return diagonal({source: o, target: o});
            });

        link.transition()
            .duration(duration)
            .attr("d", diagonal);

        link.exit().transition()
            .duration(duration)
            .attr("d", function(d) {
                var o = {x: source.x, y: source.y};
                return diagonal({source: o, target: o});
            })
            .remove();

        nodes.forEach(function(d) {
            d.x0 = d.x;
            d.y0 = d.y;
        });
    }


    function click(d) {
        if (d.children) {
            d._children = d.children;
            d.children = null;
        } else {
            d.children = d._children;
            d._children = null;
        }
        update(d);
    }
    function click2(d) {
        if (d.children) {
            d._children = d.children;
            d.children = null;
        } else {
            d.children = d._children;
            d._children = null;
        }
        update(d);//要更新
        nodePanelObj.eConsole(d);
    }
});



//【DHSS基本信息】
var nodePanelObj = {
    hssName:"",
    currentNode:null,
    //更新拓扑图数据
    getTopoData: function(inhss) {
        nodePanelObj.hssName = inhss;
        $.ajax({
            url: "../nehss.json",
            data:{"HSS" :inhss},
            cache: false,
            success: function(data){
                var mydatas = JSON.stringify(data);
                nodePanelObj.updateTopoData(data.result);
                //alert(mydatas);
                //JSON.parse()
            }
        });
    },
    getNodeKPIData: function(inNodeInfo) {
        $.ajax({
            url: /*"../nekpilist.json"*/"kpi-result/"+inNodeInfo.neid+"/"+$("#dhssName").val(),
            data:{"NENAME" :inNodeInfo.neid},
            cache: false,
            success: function(data){
                var mydatas = JSON.stringify(data);
                nodePanelObj.initResultList(data.result);
            }
        });
    },
    getNodeAlarmData: function(inNodeInfo) {
        $.ajax({
            url: /*"../nealarmlist.json"*/"alarm-result/"+inNodeInfo.neid+"/"+$("#token").val()+"/"+$("#dhssName").val(),
            data:{"NENAME" :inNodeInfo.neid},
            cache: false,
            success: function(data){
                var mydatas = JSON.stringify(data);
                nodePanelObj.initResultList2(data.result);
            }
        });
    },
    getNodeAhubData: function(inNodeInfo) {
        $.ajax({
            url: /*"../neahubdata.json"*/"ahub-result/"+inNodeInfo.neid+"/"+$("#dhssName").val(),
            data:{"NENAME" :inNodeInfo.neid},
            cache: false,
            success: function(data){
                var mydatas = JSON.stringify(data);
                nodePanelObj.resultAhubData(data.result);
            }
        });
    },
    updateTopoData: function(indata) {
        var tmpdata = formatTopoData(indata);
        var tmp = option.series[0].data;
        option.title.text = nodePanelObj.hssName;
        option.series[0].data = tmpdata;
        myChart.setOption(option, true);
        // myChart.addData(tmpdata);
    },
    // 点击拓扑图上面的节点之后
    eConsole:function (param) {
    	if(isTesting){
    		return;
    	}
        nodePanelObj.currentNode = param;
        var tmpNeType = param.netype;
        $(".one-click").hide();
        unitId = "";
        if(param.unitId != null && param.unitId != ""){
        	$(".one-click").show();
        	unitId = param.unitId;
        }
        if(tmpNeType=="AHUB" || tmpNeType=="HLR-AHUB" || tmpNeType=="HSS-AHUB"){
            nodePanelObj.showAhubPanel(param);
            nodePanelObj.getNodeAhubData(param);
        }else{
            nodePanelObj.showPanel(param);
            nodePanelObj.getNodeKPIData(param);
            nodePanelObj.getNodeAlarmData(param);
        }

    },
    //指标滚动条样式
    initScrollStyle: function() {
        $("#resultWrap").height(265);
        $("#resultWrap").mCustomScrollbar({
            theme: "minimal-dark",
            scrollbarPosition: "inside",
            autoHideScrollbar: true
        });
    },
    //指标滚动条内添加数据
    initResultList: function(inData) {
        var liHtml = "";
        for (var i = 0; i < inData.length; i++) {
            var item = inData[i];
            liHtml += '<li class="liList" >'+item.kpiName+'<br>'+item.periodStartTime+'<span style="float:right;font-size: 18px">'+ item.kpiOutputValue + '' +'</span></li>';
        }
        $("#resultWrap ul").html(liHtml);
    },
    //指标-绑定事件
    resultListClick: function() {
        $("#result2Wrap").delegate("li.liList", "click", function() {
            $("#result2Wrap li.active").removeClass("active");
            var that = $(this);
            that.addClass("active");
        });
    },
    //告警滚动条样式
    initScrollStyle2: function() {
        $("#result2Wrap").height(265);
        $("#result2Wrap").mCustomScrollbar({
            theme: "minimal-dark",
            scrollbarPosition: "inside",
            autoHideScrollbar: true
        });
    },
    //告警滚动条内添加数据
    initResultList2: function(inData) {
        var liHtml = "";
        for (var i = 0; i < inData.length; i++) {
            var item = inData[i];
            liHtml += '<li class="liList">'+item.alarmText+'<br>'+item.receiveStartTime+'<span style="float:right;font-size: 18px">'+ item.alarmLevel +'</span></li>';
        }
        $("#result2Wrap ul").html(liHtml);
    },
    //告警-绑定事件
    resultListClick2: function() {
        $("#result2Wrap").delegate("li.liList", "click", function() {
            $("#result2Wrap li.active").removeClass("active");
            var that = $(this);
            that.addClass("active");
        });
    },
    initScrollStyle3: function() {
        $("#result3Wrap").height(430);
        $("#result3Wrap").mCustomScrollbar({
            horizontalScroll:true,
            theme: "minimal-dark",
            scrollbarPosition: "inside",
            autoHideScrollbar: true
        });
    },
    resultAhubData: function(inData) {
        var liHtml = "";
        
        for (var i = 0; i < inData.length; i++) {
            var item = inData[i];
            liHtml += '<tr><td>'+ item.selfPort+'</td><td>'+item.targetMode+'</td><td>'+item.targetLan+'</td><td>'+item.vlanId+'</td><td>'+item.ipAddress+'</td><td>'+item.targetEquipment+'</td><td>'+item.targetPort+'</td><td>'+item.upLinkIpAddress+'</td></tr>';
        }
        $("#ahubContent").html(liHtml);
    },
    showPanel:function(inItem){
        $("#nodePanel").show();
        $("#nodePanel .eNodeBName").html(inItem.nename);

        $("#topKpi .location").html(inItem.location/*?inItem.location: Math.floor(Math.random()*10)*/);
        $("#topKpi .idsVersion").html(inItem.idsVersion);
        $("#topKpi .swVersion").html(inItem.swVersion);
        $("#topKpi .neState").html(inItem.neState == '0' ?"正常":"异常");
        $("#topKpi .remarks").html(inItem.remarks);
        unitId = inItem.unitId;
        $(".timg,.warn,.error").hide();
//        if(inItem.pingText != null && inItem.pingText != ""){
//	    	if(inItem.pingText.indexOf("100%") != -1) $(".error").show();
//	    	else if(inItem.pingText.indexOf("0%") != -1) $(".timg").show();
//	    	else  $(".warn").show();
//        }

        $("#nodePanel .closePanel").on("click",function(){
            nodePanelObj.hidePanel();
        });
        nodePanelObj.hideAhubPanel();
    },
    hidePanel:function(){
        $("#nodePanel").hide();
    },
    showAhubPanel:function(inItem){
        $("#nodeAhubPanel").show();
        $("#nodeAhubPanel .eNodeBName").html(inItem.nename);
        
        $(".location").html(inItem.location/*?inItem.location: Math.floor(Math.random()*10)*/);
        $(".idsVersion").html(inItem.idsVersion);
        $(".swVersion").html(inItem.swVersion);
        $(".neState").html(inItem.neState == '0' ?"正常":"异常");
        $(".remarks").html(inItem.remarks);
        
        unitId = inItem.unitId;
        $(".timg,.warn,.error").hide();
//    	if(inItem.pingText != null && inItem.pingText != ""){
//    		if(inItem.pingText.indexOf("100%") != -1) $(".error").show();
//        	else if(inItem.pingText.indexOf("0%") != -1) $(".timg").show();
//        	else  $(".warn").show();
//    	}

        $("#nodeAhubPanel .closePanel").on("click",function(){
            nodePanelObj.hideAhubPanel();
        });
        nodePanelObj.hidePanel();
    },
    hideAhubPanel:function(){
        $("#nodeAhubPanel").hide();
    },
    init: function() {
        nodePanelObj.initScrollStyle();
        nodePanelObj.resultListClick();
        nodePanelObj.initScrollStyle2();
        nodePanelObj.resultListClick2();
        nodePanelObj.initScrollStyle3();

    }
};



function formatTopoData(indata) {
    var returnArr = [];
    for(var i=0;i<indata.length;i++){
        var tmp = indata[i];
        var cur = tmp;
        cur.name = tmp.nename;
        
//        if(tmp.iconLevel==0){
//            cur.symbolSize = [48, 48];
//            cur.symbol = "image://../assets/images/iconALLHSS.png";
//        }else if(tmp.iconLevel==1){
//            cur.symbolSize = [48, 32];
//            cur.symbol = "image://../assets/images/iconSite.png";
//        }else if(tmp.iconLevel==2){
//            cur.symbolSize = [48, 48];
//            cur.symbol = "image://../assets/images/iconHSS.png";
//        }else if(tmp.iconLevel==3){
//            cur.symbolSize = [32, 32];
//            cur.symbol = "image://../assets/images/iconGWF.png";
//        }else if(tmp.iconLevel==4){
//            cur.symbolSize = [32, 32];
//            cur.symbol = "image://../assets/images/iconHSSUNIT.png";
//        }else{
//            cur.symbolSize = [32, 32];
//            cur.symbol = "image://../assets/images/iconALLHSS.png";
//        }
        
        if(getClassLevel(tmp.netype)==0){
            cur.symbolSize = [48, 48];
            cur.symbol = "image://../assets/images/iconALLHSS.png";
        }else if(getClassLevel(tmp.netype)==1){
            cur.symbolSize = [48, 32];
            cur.symbol = "image://../assets/images/iconSite.png";
        }else if(getClassLevel(tmp.netype)==2){
            cur.symbolSize = [48, 48];
            cur.symbol = "image://../assets/images/iconHSS.png";
        }else if(getClassLevel(tmp.netype)==3){
            cur.symbolSize = [32, 32];
            cur.symbol = "image://../assets/images/iconGWF.png";
        }else if(getClassLevel(tmp.netype)==4){
            cur.symbolSize = [32, 32];
            cur.symbol = "image://../assets/images/iconHSSUNIT.png";
        }else{
            cur.symbolSize = [32, 32];
            cur.symbol = "image://../assets/images/iconALLHSS.png";
        }
        if(tmp.children){
            cur.children = arguments.callee(tmp.children)
        }
        returnArr.push(cur);
    }
    return returnArr;
}

function getClassLevel(param) {
    var classArr = [
        ["ALLHSS"],
        ["SITE"],
        ["SGW","NTHLR","HSS","ONE-NDS"],
        ["HSSTYPE"],
        ["SGWUNIT","HLR-FE","HLR-PCC","HLR-TIAMS","HLR-AHUB","HSS-FE","HSS-PCC","HSS-TIAMS","HSS-AHUB","AHUB","ROUTING-DSA","BE-DSA","PGW","PGW-DSA","ADM","INS","DRA"]
    ];
    for (var i = 0; i < classArr.length; i++) {
        var tmpClassArr = classArr[i];
        if(tmpClassArr.indexOf(param)>= 0){
            return i;
        }
    }
    return 0;
}
//接收url参数
function getQueryString(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
    var r = window.location.search.substr(1).match(reg);
    if (r != null) return unescape(r[2]); return null;
}

$(function () {
    $(window).on("resize",function(){
        //窗口调整时，更新拓扑图的展示区域
        $("#topologicalmap").height($(window).height()-120);

        if ($("#nodePanel").is(":hidden") == false) {
            var tmpgridHeight = $(window).height()-360;
            $("#resultWrap").height(tmpgridHeight<260?260:tmpgridHeight);
            $("#result2Wrap").height(tmpgridHeight<260?260:tmpgridHeight);
            $("#result2Wrap").height(tmpgridHeight<260?260:tmpgridHeight);
        }
        if ($("#nodeAhubPanel").is(":hidden") == false) {
            var tmpgridHeight = $(window).height()-220;
            $("#result3Wrap").height(tmpgridHeight<430?430:tmpgridHeight);
        }
    });

    // 高亮显示当前模块导航
    // getQueryString("NENAME")
    var urlstr = location.href;　　　　//获取浏览器的url
    var urlstatus=false;　　　　　　　　//标记
    //遍历导航div
    $("#navList a").each(function () {
        //判断导航里面的rel和url地址是否相等
        if ((urlstr + '/').indexOf($(this).attr('href')) > -1&&$(this).attr('href')!='') {
            $(this).addClass('active');
            urlstatus = true;
        } else {
            $(this).removeClass('active');
        }
    });
    if (!urlstatus) {$("#navList a").eq(0).addClass('active'); }

    if(getQueryString("NENAME")){
        nodePanelObj.init();
        //nodePanelObj.getTopoData(getQueryString("NENAME"));
    }else{
        nodePanelObj.init();
        //nodePanelObj.getTopoData("HSS50");
    }

});

var alarmData = [
    {
        objName: "SGW1",
        time1: "2016/04/28 12:34",
        time2: "2016/05/03 14:32",
        acknowledge: "已确认",
        alarmName: "驻波比重要告警",
        alarmDesc: "VSWR值超规定门限，天线不匹配或损坏",
        alarmLevel: 5,
        alarmExpression: "attach_rate.last( 0 ) <  95%",
    }, {
        objName: "SGW2",
        time1: "2016/04/27 11:45",
        time2: "2016/05/02 14:43",
        acknowledge: "已确认",
        alarmName: "驻波比重要告警",
        alarmDesc: "VSWR值超规定门限，天线不匹配或损坏",
        alarmLevel: 4,
        alarmExpression: "attach_rate.last( 0 ) <  95%",
    }, {
        objName: "PGW1",
        time1: "2016/04/26 09:34",
        time2: "2016/05/02 08:32",
        acknowledge: "未确认",
        alarmName: "驻波比重要告警",
        alarmDesc: "VSWR值超规定门限，天线不匹配或损坏",
        alarmLevel: 2,
        alarmExpression: "attach_rate.last( 0 ) <  95%",
    }, {
        objName: "PGW2",
        time1: "2016/04/25 12:43",
        time2: "2016/05/03 14:07",
        acknowledge: "未确认",
        alarmName: "驻波比重要告警",
        alarmDesc: "VSWR值超规定门限，天线不匹配或损坏",
        alarmLevel: 3,
        alarmExpression: "attach_rate.last( 0 ) <  95%",
    }, {
        objName: "MME1",
        time1: "2016/04/24 10:05",
        time2: "2016/05/03 17:15",
        acknowledge: "未确认",
        alarmName: "驻波比重要告警",
        alarmDesc: "VSWR值超规定门限，天线不匹配或损坏",
        alarmLevel: 1,
        alarmExpression: "attach_rate.last( 0 ) <  95%",
    }, {
        objName: "MME2",
        time1: "2016/04/23 12:34",
        time2: "2016/05/03 16:09",
        acknowledge: "已确认",
        alarmName: "驻波比重要告警",
        alarmDesc: "VSWR值超规定门限，天线不匹配或损坏不匹配或损坏",
        alarmLevel: 1,
        alarmExpression: "attach_rate.last( 0 ) <  95%",
    }, {
        objName: "SGW1",
        time1: "2016/04/22 17:32",
        time2: "2016/05/03 12:08",
        acknowledge: "已确认",
        alarmName: "到某模块的连接丢失",
        alarmDesc: "到基站所属某模块的连接丢失",
        alarmLevel: 1,
        alarmExpression: "attach_rate.last( 0 ) <  95%",
    }, {
        objName: "SGW1",
        time1: "2016/04/21 23:38",
        time2: "2016/05/05 09:44",
        acknowledge: "已确认",
        alarmName: "光接口发射故障",
        alarmDesc: "SFP发生故障或SFP模块不存在（无电气连接）。SFP为光纤的一部分  ",
        alarmLevel: 4,
        alarmExpression: "attach_rate.last( 0 ) <  95%",
    }, {
        objName: "MME1",
        time1: "2016/04/20 12:34",
        time2: "2016/05/03 45:25",
        acknowledge: "未确认",
        alarmName: "网元操作维护连接失败",
        alarmDesc: "基站与iOMS的操作维护连接中断，可能是传输网络或基站传输板件出现故障",
        alarmLevel: 5,
        alarmExpression: "attach_rate.last( 0 ) <  95%",
    }, {
        objName: "MME2",
        time1: "2016/04/20 09:45",
        time2: "2016/05/06 14:32",
        acknowledge: "已确认",
        alarmName: "网元操作维护连接失败",
        alarmDesc: "基站与iOMS的操作维护连接中断，可能是传输网络或基站传输板件出现故障",
        alarmLevel: 3,
        alarmExpression: "attach_rate.last( 0 ) <  95%",
    }];




var topoData = {"neid":"HSS50","nename":"HSS50","netype":"ALLHSS","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":"",
    "children":[
        {"neid":"Minsheng","nename":"Minsheng","netype":"SITE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":"",
            "children":[
                {"neid":"Minsheng-NTHLR","nename":"NTHLR","netype":"HSS","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":"",
                    "children":[
                        {"neid":"Minsheng-HLR-FE","nename":"HLR-FE","netype":"HSSTYPE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":"",
                            "children":[
                                {"neid":"shhss50fe01bnkfe101","nename":"shhss50fe01bnkfe101","netype":"HLR-FE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""},
                                {"neid":"shhss50fe01bnkfe102","nename":"shhss50fe01bnkfe102","netype":"HLR-FE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""},
                                {"neid":"shhss50fe01bnkfe103","nename":"shhss50fe01bnkfe103","netype":"HLR-FE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""},
                                {"neid":"shhss50fe01bnkfe104","nename":"shhss50fe01bnkfe104","netype":"HLR-FE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""}]
                        },
                        {"neid":"HLR-PCC","nename":"HLR-PCC","netype":"HSSTYPE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":"",
                            "children":[
                                {"neid":"shhss50fe01bnkpcc101","nename":"shhss50fe01bnkpcc101","netype":"HLR-PCC","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""},
                                {"neid":"shhss50fe01bnkpcc102","nename":"shhss50fe01bnkpcc102","netype":"HLR-PCC","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""}]
                        },
                        {"neid":"HLR-TIMAS","nename":"HLR-TIMAS","netype":"HSSTYPE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":"",
                            "children":[
                                {"neid":"shhss50fe01bnktiams101","nename":"shhss50fe01bnktiams101","netype":"HLR-TIMAS","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""}]
                        },
                        {"neid":"HLR-AHUB","nename":"HLR-AHUB","netype":"HSSTYPE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":"",
                            "children":[
                                {"neid":"shhss50fe01bnkhub101","nename":"shhss50fe01bnkhub101","netype":"HLR-AHUB","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""},
                                {"neid":"shhss50fe01bnkhub102","nename":"shhss50fe01bnkhub102","netype":"HLR-AHUB","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""}]}]
                },
                {"neid":"Minsheng-CMS8200","nename":"CMS8200","netype":"HSS","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":"",
                    "children":[
                        {"neid":"HSS-FE（36）","nename":"HSS-FE（36）","netype":"HSSTYPE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":"",
                            "children":[
                                {"neid":"shhss50fe11bnkfe101","nename":"shhss50fe11bnkfe101","netype":"HSS-FE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""},
                                {"neid":"shhss50fe11bnkfe102","nename":"shhss50fe11bnkfe102","netype":"HSS-FE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""},
                                {"neid":"shhss50fe11bnkfe103","nename":"shhss50fe11bnkfe103","netype":"HSS-FE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""}]
                        },
                        {"neid":"HSS-DRA（24）","nename":"HSS-DRA（24）","netype":"HSSTYPE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":"",
                            "children":[
                                {"neid":"shhss50fe11bnkdra101","nename":"shhss50fe11bnkdra101","netype":"HSS-FE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""},
                                {"neid":"shhss50fe11bnkdra102","nename":"shhss50fe11bnkdra102","netype":"HSS-FE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""}]
                        },
                        {"neid":"HSS-TIAMS（12）","nename":"HSS-TIAMS（12）","netype":"HSSTYPE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":"",
                            "children":[
                                {"neid":"shhss50fe11bnktiams101","nename":"shhss50fe11bnktiams101","netype":"HSS-FE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""}]
                        },
                        {"neid":"HSS-AHUB（24）","nename":"HSS-AHUB（24）","netype":"HSSTYPE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":"",
                            "children":[
                                {"neid":"shhss50fe11bnkhub101","nename":"shhss50fe11bnkhub101","netype":"HSS-AHUB","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""},
                                {"neid":"shhss50fe11bnkhub102","nename":"shhss50fe11bnkhub102","netype":"HSS-AHUB","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""}]
                        }]
                },
                {"neid":"Minsheng-One-NDS","nename":"One-NDS","netype":"HSS","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":"",
                    "children":[
                        {"neid":"Routing-DSA(48)","nename":"Routing-DSA(48)","netype":"HSSTYPE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":"",
                            "children":[
                                {"neid":"shhss50be01bnkr101","nename":"shhss50be01bnkr101","netype":"HSS-FE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""},
                                {"neid":"shhss50be01bnkr102","nename":"shhss50be01bnkr102","netype":"HSS-FE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""}]
                        },
                        {"neid":"BE-DSA","nename":"BE-DSA","netype":"HSSTYPE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":"",
                            "children":[
                                {"neid":"shhss50be01bnkb101","nename":"shhss50be01bnkb101","netype":"HSS-FE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""},
                                {"neid":"shhss50be01bnkb102","nename":"shhss50be01bnkb102","netype":"HSS-FE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""}]
                        },
                        {"neid":"PGW","nename":"PGW","netype":"HSSTYPE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":"",
                            "children":[
                                {"neid":"shhss50be01bnkp101","nename":"shhss50be01bnkp101","netype":"HSS-FE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""},
                                {"neid":"shhss50be01bnkp102","nename":"shhss50be01bnkp102","netype":"HSS-FE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""}]
                        },
                        {"neid":"PGW-DSA","nename":"PGW-DSA","netype":"HSSTYPE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":"",
                            "children":[
                                {"neid":"shhss50be01bnkg101","nename":"shhss50be01bnkg101","netype":"HSS-FE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""},
                                {"neid":"shhss50be01bnkg102","nename":"shhss50be01bnkg102","netype":"HSS-FE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""}]
                        },
                        {"neid":"ADM","nename":"ADM","netype":"HSSTYPE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":"",
                            "children":[
                                {"neid":"shhss50be01bnka101","nename":"shhss50be01bnka101","netype":"HSS-FE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""}]
                        },
                        {"neid":"INS","nename":"INS","netype":"HSSTYPE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":"",
                            "children":[
                                {"neid":"shhss50be01bnki101","nename":"shhss50be01bnki101","netype":"HSS-FE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""}]
                        }]
                }
            ]
        },
        {"neid":"Wanrong","nename":"Wanrong","netype":"SITE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":"",
            "children":[
                {"neid":"Wanrong-NTHLR","nename":"NTHLR","netype":"HSS","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":"",
                    "children":[
                        {"neid":"HLR-FE","nename":"HLR-FE","netype":"HSSTYPE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":"",
                            "children":[
                                {"neid":"shhss50fe02bnkfe131","nename":"shhss50fe02bnkfe131","netype":"HSS-FE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""},
                                {"neid":"shhss50fe02bnkfe132","nename":"shhss50fe02bnkfe132","netype":"HSS-FE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""},
                                {"neid":"shhss50fe02bnkfe133","nename":"shhss50fe02bnkfe133","netype":"HSS-FE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""}]
                        },
                        {"neid":"HLR-PCC","nename":"HLR-PCC","netype":"HSSTYPE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":"",
                            "children":[
                                {"neid":"shhss50fe02bnkpcc131","nename":"shhss50fe02bnkpcc131","netype":"HSS-FE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""},
                                {"neid":"shhss50fe02bnkpcc132","nename":"shhss50fe02bnkpcc132","netype":"HSS-FE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""}]
                        },
                        {"neid":"HLR-TIMAS","nename":"HLR-TIMAS","netype":"HSSTYPE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":"",
                            "children":[
                                {"neid":"shhss50fe02bnktiams131","nename":"shhss50fe02bnktiams131","netype":"HSS-FE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""}]
                        },
                        {"neid":"HLR-AHUB","nename":"HLR-AHUB","netype":"HSSTYPE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":"",
                            "children":[
                                {"neid":"shhss50fe02bnkhub131","nename":"shhss50fe02bnkhub131","netype":"HLR-AHUB","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""},
                                {"neid":"shhss50fe02bnkhub132","nename":"shhss50fe02bnkhub132","netype":"HLR-AHUB","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""}]
                        }]
                },
                {"neid":"Wanrong-CMS8200","nename":"CMS8200","netype":"HSS","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":"",
                    "children":[
                        {"neid":"HLR-FE","nename":"HLR-FE","netype":"HSSTYPE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":"",
                            "children":[
                                {"neid":"shhss50fe12bnkfe131","nename":"shhss50fe12bnkfe131","netype":"HSS-FE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""},
                                {"neid":"shhss50fe12bnkfe132","nename":"shhss50fe12bnkfe132","netype":"HSS-FE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""},
                                {"neid":"shhss50fe12bnkfe133","nename":"shhss50fe12bnkfe133","netype":"HSS-FE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""}]
                        },
                        {"neid":"HLR-DRA","nename":"HLR-DRA","netype":"HSSTYPE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":"",
                            "children":[
                                {"neid":"shhss50fe12bnkdra131","nename":"shhss50fe12bnkdra131","netype":"HSS-FE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""},
                                {"neid":"shhss50fe12bnkdra132","nename":"shhss50fe12bnkdra132","netype":"HSS-FE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""}]
                        },
                        {"neid":"HLR-TIMAS","nename":"HLR-TIMAS","netype":"HSSTYPE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":"",
                            "children":[
                                {"neid":"shhss50fe12bnktiams131","nename":"shhss50fe12bnktiams131","netype":"HSS-FE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""}]
                        },
                        {"neid":"HLR-AHUB","nename":"HLR-AHUB","netype":"HSSTYPE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":"",
                            "children":[
                                {"neid":"shhss50fe12bnkhub131","nename":"shhss50fe12bnkhub131","netype":"HLR-AHUB","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""},
                                {"neid":"shhss50fe12bnkhub132","nename":"shhss50fe12bnkhub132","netype":"HLR-AHUB","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""}]
                        }]
                },
                {"neid":"Wanrong-One-NDS","nename":"One-NDS","netype":"HSS","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":"",
                    "children":[
                        {"neid":"Routing-DSA","nename":"Routing-DSA","netype":"HSSTYPE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":"",
                            "children":[
                                {"neid":"shhss50be02bnkr131","nename":"shhss50be02bnkr131","netype":"HSS-FE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""},
                                {"neid":"shhss50be02bnkr132","nename":"shhss50be02bnkr132","netype":"HSS-FE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""},
                                {"neid":"shhss50be02bnkr133","nename":"shhss50be02bnkr133","netype":"HSS-FE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""}]
                        },
                        {"neid":"BE-DSA","nename":"BE-DSA","netype":"HSSTYPE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":"",
                            "children":[
                                {"neid":"shhss50be02bnkb131","nename":"shhss50be02bnkb131","netype":"HSS-FE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""},
                                {"neid":"shhss50be02bnkb132","nename":"shhss50be02bnkb132","netype":"HSS-FE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""},
                                {"neid":"shhss50be02bnkb133","nename":"shhss50be02bnkb133","netype":"HSS-FE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""}]
                        },
                        {"neid":"PGW","nename":"PGW","netype":"HSSTYPE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":"",
                            "children":[
                                {"neid":"shhss50be02bnkp131","nename":"shhss50be02bnkp131","netype":"HSS-FE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""},
                                {"neid":"shhss50be02bnkp132","nename":"shhss50be02bnkp132","netype":"HSS-FE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""}]
                        },
                        {"neid":"PGW-DSA","nename":"PGW-DSA","netype":"HSSTYPE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":"",
                            "children":[
                                {"neid":"shhss50be02bnkg131","nename":"shhss50be02bnkg131","netype":"HSS-FE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""},
                                {"neid":"shhss50be02bnkg132","nename":"shhss50be02bnkg132","netype":"HSS-FE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""}]
                        },
                        {"neid":"ADM","nename":"ADM","netype":"HSSTYPE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":"",
                            "children":[
                                {"neid":"shhss50be02bnka131","nename":"shhss50be02bnka131","netype":"HSS-FE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""}]
                        },
                        {"neid":"INS","nename":"INS","netype":"HSSTYPE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":"",
                            "children":[
                                {"neid":"shhss50be02bnki131","nename":"shhss50be02bnki131","netype":"HSS-FE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""}]
                        }]
                }]
        },
        {"neid":"Qinzhou","nename":"Qinzhou","netype":"SITE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":"",
            "children":[
                {"neid":"Qinzhou-NTHLR","nename":"NTHLR","netype":"HSS","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":"",
                    "children":[
                        {"neid":"HLR-FE","nename":"HLR-FE","netype":"HSSTYPE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":"",
                            "children":[
                                {"neid":"shhss50fe03bnkfe161","nename":"shhss50fe03bnkfe161","netype":"HSS-FE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""},
                                {"neid":"shhss50fe03bnkfe162","nename":"shhss50fe03bnkfe162","netype":"HSS-FE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""},
                                {"neid":"shhss50fe03bnkfe163","nename":"shhss50fe03bnkfe163","netype":"HSS-FE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""}]
                        },
                        {"neid":"HLR-PCC","nename":"HLR-PCC","netype":"HSSTYPE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":"",
                            "children":[
                                {"neid":"shhss50fe03bnkpcc161","nename":"shhss50fe03bnkpcc161","netype":"HSS-FE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""},
                                {"neid":"shhss50fe03bnkpcc162","nename":"shhss50fe03bnkpcc162","netype":"HSS-FE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""}]
                        },
                        {"neid":"HLR-TIMAS","nename":"HLR-TIMAS","netype":"HSSTYPE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":"",
                            "children":[
                                {"neid":"shhss50fe03bnktiams161","nename":"shhss50fe03bnktiams161","netype":"HSS-FE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""}]
                        },
                        {"neid":"HLR-AHUB","nename":"HLR-AHUB","netype":"HSSTYPE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":"",
                            "children":[
                                {"neid":"shhss50fe03bnkhub161","nename":"shhss50fe03bnkhub161","netype":"HLR-AHUB","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""},
                                {"neid":"shhss50fe03bnkhub162","nename":"shhss50fe03bnkhub162","netype":"HLR-AHUB","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""}]
                        }]
                },
                {"neid":"Qinzhou-CMS8200","nename":"CMS8200","netype":"HSS","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":"",
                    "children":[
                        {"neid":"HLR-FE","nename":"HLR-FE","netype":"HSSTYPE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":"",
                            "children":[
                                {"neid":"shhss50fe13bnkfe161","nename":"shhss50fe13bnkfe161","netype":"HSS-FE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""},
                                {"neid":"shhss50fe13bnkfe162","nename":"shhss50fe13bnkfe162","netype":"HSS-FE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""},
                                {"neid":"shhss50fe13bnkfe163","nename":"shhss50fe13bnkfe163","netype":"HSS-FE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""}]
                        },
                        {"neid":"HLR-DRA","nename":"HLR-DRA","netype":"HSSTYPE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":"",
                            "children":[
                                {"neid":"shhss50fe13bnkdra161","nename":"shhss50fe13bnkdra161","netype":"HSS-FE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""},
                                {"neid":"shhss50fe13bnkdra162","nename":"shhss50fe13bnkdra162","netype":"HSS-FE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""}]
                        },
                        {"neid":"HLR-TIMAS","nename":"HLR-TIMAS","netype":"HSSTYPE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":"",
                            "children":[
                                {"neid":"shhss50fe13bnktiams161","nename":"shhss50fe13bnktiams161","netype":"HSS-FE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""}]
                        },
                        {"neid":"HLR-AHUB","nename":"HLR-AHUB","netype":"HSSTYPE","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":"",
                            "children":[
                                {"neid":"shhss50fe13bnkhub161","nename":"shhss50fe13bnkhub161","netype":"HLR-AHUB","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""},
                                {"neid":"shhss50fe13bnkhub162","nename":"shhss50fe13bnkhub162","netype":"HLR-AHUB","neState":"0","location":"","idsVersion":"","swVersion":"","remarks":""}]
                        }]

                }]
        }]
};