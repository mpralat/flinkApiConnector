## Step 1: Run python server
To run the server, you need to have Flask installed.

[Installation guide](http://flask.pocoo.org/docs/0.12/installation/)

With Flask installed, run:
```
cd python_server
export FLASK_APP=simpleserver.py
flask run
```
API endpoint: localhost:5000/hello

## Step 2: Run flink app
In another terminal, run
```
mvn exec:java -Dexec.mainClass="org.flinkproject.MainClass"
```

## Testing
You can ran example tests with maven
```
mvn test
```