'use strict';

var $ = require('jquery');
var Backbone = require('backbone');
var Marionette = require('backbone.marionette');

var connection;

var Agent = Backbone.Model.extend({
    idAttribute: 'ah',
    defaults: function() {
        return {
            data: {
                labels: ["User", "Kernel", "Idle", "Nice"],
                datasets: [
                    {
                        fillColor: "rgba(255,0,255,0.5)",
                        data: [0, 0, 0, 0]
                    }
                ]
            }
        };
    }
});

var AgentCollection = Backbone.Collection.extend({
    model: Agent
});

var EmptyAgentsView = Backbone.View.extend({
    render: function() {
        this.$el.html('<em>No Agents Data Recieved</em>');
    }
});

var AgentView = Marionette.ItemView.extend({
    className: 'agentRow',
    template: require('./agentView.hbs'),
    templateHelpers: function() {
        return {
            lastStatus: new Date(parseInt(this.model.get('t'), 10))
        };
    },
    modelEvents: {
        'change': 'render'
    },
    onRender: function() {
        var ctx = this.$('canvas').get(0).getContext("2d");
        var data = this.model.get('data');
        setTimeout(function() {
            new Chart(ctx).Bar(data);
        }, 0);
    }
});

var AgentCollectionView = Marionette.CollectionView.extend({
    emptyView: EmptyAgentsView,
    childView: AgentView
});

var agentCollection = new AgentCollection();

function createConnection() {
    connection = new WebSocket('ws://localhost:8090/ws');
    connection.onopen = function() {
        console.log('onopen', arguments);
    };

    connection.onerror = function() {
        console.log('onerror', arguments);
    };

    connection.onclose = function() {
        console.log('onclose', arguments);
        setTimeout(createConnection, 5000);
    };

    connection.onmessage = function(message) {
        console.log('onmessage', message);
        processMessage(JSON.parse(message.data));
    };
}

function processMessage(message) {
    var type = message.type;
    var data = message.data;
    switch(type) {
        case 'status': {
            onReceiveStatus(data);
            break;
        }
        case 'metrics': {
            onReceiveMetrics(data);
            break;
        }
        default: {
            console.log('unknown message type', type);
            break;
        }
    }
}

function onReceiveStatus(data) {
    agentCollection.add(data, { merge: true });
}

function onReceiveMetrics(data) {
    var ah = data.ah;
    var agent = agentCollection.get(ah);
    if (agent) {
        // update agent systemCpuMetrics data for bar chart
        var chartData = agent.get('data');
        var systemCpuMetrics = data.metrics.systemCpuMetrics;
        chartData.datasets[0].data = [ systemCpuMetrics.user, systemCpuMetrics.kernel, systemCpuMetrics.idle, systemCpuMetrics.nice ];
    }
}

$(function(event) {
    createConnection();

    // turn off chart animations
    window.Chart.defaults.global.animation = false;

    (new AgentCollectionView({
        el: 'main',
        collection: agentCollection
    })).render();
});
