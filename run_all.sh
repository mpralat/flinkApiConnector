#!/bin/bash
trap 'killall' INT
killall() {
    trap '' INT TERM     # ignore INT and TERM while shutting down
    echo "**** Shutting down... ****"
    kill -TERM 0         # fixed order, send TERM not INT
    wait
    echo DONE
}

mkdir -p additional
cd additional
# INSTALL ELASTICSEARCH
if  [ ! -d  elasticsearch-5.5.2 ]
then 
    wget https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-5.5.2.zip
fi
unzip elasticsearch-5.5.2.zip
cd elasticsearch-5.5.2/
./bin/elasticsearch &
# KIBANA
cd ..
if [ ! -d kibana-5.5.2-linux-x86_64 ]
then
    wget https://artifacts.elastic.co/downloads/kibana/kibana-5.5.2-linux-x86_64.tar.gz
fi
tar -xzf kibana-5.5.2-linux-x86_64.tar.gz
cd kibana-5.5.2-linux-x86_64
./bin/kibana &
# PYTHON GENERATOR SERVER
cd ..
python3 -m venv virtualenv
source ./virtualenv/bin/activate
cd ../python_server
pip install -r requirements.txt
export FLASK_APP=simpleserver.py
flask run &


cat # WAIT FOR CTRL-C
