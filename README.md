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

## Step 2: Install and run Elasticsearch 
```
wget https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-5.5.2.zip
unzip elasticsearch-5.5.2.zip
cd elasticsearch-5.5.2/
./bin/elasticsearch
```
[More](https://www.elastic.co/guide/en/elasticsearch/reference/current/zip-targz.html)

## Step 3: Install and run Kibana
```
wget https://artifacts.elastic.co/downloads/kibana/kibana-5.5.2-linux-x86_64.tar.gz
tar -xzf kibana-5.5.2-linux-x86_64.tar.gz
cd kibana-5.5.2-linux-x86_64
./bin/kibana 
```

## Step 4: Run flink app
Run the project in your IDE, or run
```
mvn exec:java -Dexec.mainClass="org.flinkproject.MainClass"
```

## Visualize with Kibana
Open [http://localhost:5601](Open http://localhost:5601)
Navigate to `Discover` and add an index pattern `total-price`. Hit `Create`.
Go to `Discover` again to see incoming data.

## Testing
You can run example tests with maven
```
mvn test
```

## Using run_all script
Helper script, because I'm too lazy to run commands on three separate terminals. Works for me, but not sure if it actually does on all systems.
Creates a new `additional` directory, creates virtualenv, downloads Kibana, ElasticSearch and
runs all three, then waits for Ctrl-C to kill them all. 
```
chmod +x run_all.sh
./run_all.sh
```
It does not run flink app though, need to run this one yourself ;_;