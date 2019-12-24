var stompClient = null;

function connectWebSocket() {
    var socket = new SockJS('/gs-guide-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/greeting', function (greeting) {
            showGreeting(greeting.body);
        });
    });
}

function disconnectWebSocket() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    console.log("Disconnected");
    alert("Disconnected success");
}

function showGreeting(message) {
    $("#log-container div").append(message);
    // 滚动条滚动到最低部
    $("#log-container").scrollTop($("#log-container div").height() - $("#log-container").height());
}