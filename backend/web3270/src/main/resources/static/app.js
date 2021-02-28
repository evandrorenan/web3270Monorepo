var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    var socket = new SockJS('/web3270-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/queue/session/20201117192908268', function (message) {
        	// showGreeting(message.body);
            showGreeting(JSON.parse(message.body).sessionId);
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    let s = document.getElementById("name").value;
    if (!s || s === "") {
        s = "20201117192908268";
    }
    stompClient.send("/ws/sendkeys", {}, JSON.stringify({
    "sessionId": s,
    "sendKeys": [
        {
            "row": 24,
            "col": 29,
            "text": "ACCTER",
            "functionKey": "[enter]"
        }
    ]
}));
}

function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendName(); });
});

