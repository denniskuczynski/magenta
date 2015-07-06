'use strict';

var $ = require('jquery');

var connection;

function createConnection() {
    connection = new WebSocket('ws://dennismac:8090/ws');
    connection.onopen = function () {
        console.log('onopen', arguments);
    };

    connection.onerror = function () {
        console.log('onerror', arguments);
    };

    connection.onclose = function () {
        console.log('onclose', arguments);
    };

    connection.onmessage = function (message) {
        console.log('onmessage', message);
        $('#response').html(message.data);
    };
}

$(function(event) {
    createConnection();
});
