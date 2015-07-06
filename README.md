Magenta
=======

To start the application:
* `mvn install`
* `mvn -Djetty.http.port=8090 jetty:run`

Then browse to: `http://<myhostname>:8090/static/index.html`

Automation Agents should be configured to point to:
```
mmsBaseUrl=http://<hyhostname>:8090
```


Building the JS App
-------------------

Magenta comes packaged with a fully built JS application.
You can rebuild it with the following steps.

* `npm install`
* `grunt`

(Assumes node, npm, and grunt are installed and available globally.)
