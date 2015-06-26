Magenta
---------

To start the application:
  mvn install
  mvn -Djetty.http.port=8090 jetty:run

Then browse to:
  http://<myhostname>:8090/static/index.html

Automation Agents should be configured to point to:
  mmsBaseUrl=http://<hyhostname>:8090
