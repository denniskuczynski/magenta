(function() {

    var connection;

    function createConnection() {
        connection = new WebSocket('ws://dennismac:8090/ws');
        connection.onopen = function () {
            console.log('onopen', arguments);
            connection.send('test message');
        };

        connection.onerror = function () {
            console.log('onerror', arguments);
        };

        connection.onclose = function () {
            console.log('onclose', arguments);
        };

        connection.onmessage = function (message) {
            console.log('onmessage', message);
            document.getElementById('response').innerHTML = message.data;
        };
    }

    document.addEventListener('DOMContentLoaded', function(event) {
        createConnection();
    });
})();
