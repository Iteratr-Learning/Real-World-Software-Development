
var deleteTwoot;

$(function () {

    var logon = $('#logon');
    var sendTwoot = $('#sendTwoot');
    var follow = $('#follow');

    var topAlert = $('#top-alert');
    var twootBox = $('#twootBox');

    var profileName = $('#profileName');

    profileName.hide();

    sendTwoot.hide();
    follow.hide();

    var socket;
    var userName;

    // BEGIN MESSAGING

    function send(json) {
        socket.send(JSON.stringify(json));
    }

    socket = new WebSocket("ws://localhost:9000");

    socket.onmessage = function (event) {
        var message = JSON.parse(event.data);
        var time = new Date(message.date).toLocaleTimeString();

        console.log(message);

        switch (message.cmd)
        {
            case 'statusUpdate':
                showStatuUpdate(message.status);
                break;

            case 'twoot':
                showTwoot(message.user, message.msg);
                break;

            case 'sent':
                var position = message.position;
                break;
        }
    };

    // END MESSAGING

    // BEGIN VIEWS

    function showStatuUpdate(status) {
        topAlert.append(
            '<div class="alert alert-success alert-dismissable fade in\">' +
            '<a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>' +
            status +
            '</div>');
    }

    function showTwoot(user, msg, id) {

        var close = '';

        if (id)
        {
            close =
                '<button type="button" class="close" onclick="deleteTwoot(\''+ id +'\', this)" aria-label="Close">\n' +
                '  <span aria-hidden="true">&times;</span>\n' +
                '</button>\n';
        }

        twootBox.append(
            '<div class="row">' +
            '<div class="panel panel-default">' +
            '  <div class="panel-heading">' + user + close +'</div>' +
            '  <div class="panel-body">' + msg + '</div>' +
            '</div>' +
            '</div>');
    }

    // END VIEWS

    // BEGIN CONTROLLERS

    logon.submit(function (event) {
        event.preventDefault();

        userName = $('#userName').val();

        send({
            "cmd": 'logon',
            "userName": userName,
            "password": $('#password').val()
        });

        logon.hide();
        profileName.html("Hello <strong>" + userName + "</strong>").show();
        sendTwoot.show();
        follow.show();
    });

    sendTwoot.submit(function (event) {
        event.preventDefault();

        var content = $('#content').val();
        var id = uuid();

        showTwoot(userName, content, id);

        send({
            "cmd": 'sendTwoot',
            "content": content,
            "id": id
        });
    });

    follow.submit(function (event) {
        event.preventDefault();

        send({
            "cmd": 'follow',
            "userName": $('#userToFollow').val()
        });
    });

    deleteTwoot = function (id, button) {
        $(button).parent().parent().parent().remove();

        send({
            "cmd": 'deleteTwoot',
            "id": id
        });
    };

    // END CONTROLLERS

    // See: https://stackoverflow.com/questions/105034/create-guid-uuid-in-javascript
    function uuid() {
        return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
            var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
            return v.toString(16);
        });
    }

});
