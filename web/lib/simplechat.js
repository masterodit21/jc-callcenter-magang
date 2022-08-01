function postMessage() {
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.open("POST", "shoutServlet?t="+new Date(), false);
    xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    var nameText = escape(document.getElementById("name").value);
    var messageText = escape(document.getElementById("message").value);
    document.getElementById("message").value = "";
    xmlhttp.send("name="+nameText+"&message="+messageText);
}
var messagesWaiting = false;
function getMessages(){
    if(!messagesWaiting){
        messagesWaiting = true;
        var xmlhttp = new XMLHttpRequest();
        xmlhttp.onreadystatechange=function(){
            if (xmlhttp.readyState==4 && xmlhttp.status==200) {
                messagesWaiting = false;
                var contentElement = document.getElementById("content");
                contentElement.innerHTML = xmlhttp.responseText + contentElement.innerHTML;
            }
        }
        xmlhttp.open("GET", "shoutServlet?t="+new Date(), true);
        xmlhttp.send();
    }
}
//setInterval(getMessages, 1000);

