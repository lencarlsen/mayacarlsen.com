//Establish the WebSocket connection and set up event handlers
//var url = "wss://" + location.hostname + ":" + location.port + "/chat?username=" + id("username").value
var url = (location.protocol == "https" ? "wss://" : "ws://") + location.hostname + (location.port ? ':' + location.port : '') + "/wschat?username=" + id("username").value;
console.log("websocket url: "+url);
var webSocket = new WebSocket(url);

webSocket.onmessage = function (msg) { updateChat(msg); };
//webSocket.onclose = function () { alert(url + ": Chat WebSocket connection timed out") };
webSocket.onclose = function () { console.log(url + ": Chat WebSocket connection timed out") };

//Send message if "Send" is clicked
var sendElementId = id("sendMsg");
if (sendElementId) {
	sendElementId.addEventListener("click", function () {
	    sendMessage(id("message").value);
	});
}

//Send message if enter is pressed in the input field
var msgElementID = id("message");
if (msgElementID) {
	msgElementID.addEventListener("keypress", function (e) {
	    if (e.keyCode === 13) { sendMessage(e.target.value); }
	});
}

//Send a message if it's not empty, then clear the input field
function sendMessage(message) {
    if (message !== "") {
        webSocket.send(message);
        id("message").value = "";
    }
}

//Update the chat-panel, and the list of connected users
function updateChat(msg) {
	var userlistElementId =id("userlist");
	if (userlistElementId) {
	    var data = JSON.parse(msg.data);
	    insert("chat", data.userMessage);
	    userlistElementId.innerHTML = "";
	    data.userlist.forEach(function (user) {
	        insert("userlist", "<li>" + user.alias + "</li>");
	    });
	}
}

//Helper function for inserting HTML as the first child of an element
function insert(targetId, message) {
    var elementId = id(targetId);
    if (elementId) {
    	elementId.insertAdjacentHTML("afterbegin", message);
    }
}

//Helper function for selecting element by id
function id(id) {
    return document.getElementById(id);
}