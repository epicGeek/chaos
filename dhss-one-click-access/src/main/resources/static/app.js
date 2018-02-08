var stompClient = null;
var sessionId = null;
var token = null;
var shellprompt = '$ ';

var term = null;
var clientConnected = false;
function setConnected(connected) {

}

var unloadCallback = function(event) {
	var message = 'Close terminal? this will also terminate the command.';
	(event || window.event).returnValue = message;
	return message;
};
function connect() {
	var socket = new SockJS('/oca/endpoint');
	stompClient = Stomp.over(socket);
	stompClient.connect({}, function(frame) {
		stompClient.subscribe('/queue/terminal/' + sessionId,
				function(response) {
					term.write(JSON.parse(response.body).message);
					if (JSON.parse(response.body).type === 'disconnect') {
						disconnect();
					}
				});

		stompClient.subscribe('/topic/global-notice', function(response) {
			term.write(JSON.parse(response.body).message + "\n");
		});

		clientConnected = (true);

		term.open(document.getElementById('terminal-container'));
		window.dispatchEvent(new Event('resize'));
	});

}
function fireOnResizeEvent() {
	var width, height;

	if (navigator.appName.indexOf("Microsoft") != -1) {
		width = document.body.offsetWidth;
		height = document.body.offsetHeight;
	} else {
		width = window.outerWidth;
		height = window.outerHeight;
	}

	window.resizeTo(width - 1, height);
	window.resizeTo(width + 1, height);
}

function disconnect() {
	if (stompClient != null) {
		stompClient.disconnect();
	}
	clientConnected = (false);
	console.log("Disconnected");
}

function sendTerminal(cmd, type) {
	stompClient.send("/api/v1/terminal", {}, JSON.stringify({
		'message' : cmd,
		'type' : "command",
		'sessionId' : sessionId,
		'token' : token
	}));
}

function sendResize(size) {
	stompClient.send("/api/v1/terminal", {}, JSON.stringify({
		'message' : size,
		'type' : "resize",
		'sessionId' : sessionId,
		'token' : token
	}));
}

$(function() {

	//	term.prompt = function() {
	//		term.write('\r\n' + shellprompt);
	//	};
	if (typeof term !== 'undefined' && term !== null && term) {
		term.destroy();
	}

	term = new Terminal({
		screenKeys : false,
		cursorBlink : true,
		convertEol : true
	});

	term.on('resize', function(size) {
		//        if (ws.readyState === WebSocket.OPEN) {
		//            ws.send("2" + JSON.stringify({columns: size.cols, rows: size.rows}));
		//        }
		sendResize(size.cols + "x" + size.rows);
		setTimeout(function() {
			term.showOverlay(size.cols + 'x' + size.rows);
		}, 500);
		term.resize(size.cols, size.rows);

	});

	term.on("data", function(data) {
		if (clientConnected)
			sendTerminal(data, "cmd");
		else
			alert("lost connection,reconnect pls");
	});

	term.on('open', function() {
		// https://stackoverflow.com/a/27923937/1727928
		window.addEventListener('resize', function() {
			clearTimeout(window.resizedFinished);
			window.resizedFinished = setTimeout(function() {
				term.fit();
			}, 250);
		});
		window.addEventListener('beforeunload', unloadCallback);

	});
	connect();

});