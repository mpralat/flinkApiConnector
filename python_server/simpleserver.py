import time

from flask import Flask, Response
from json_creator import JSONCreator

json_creator = JSONCreator()

app = Flask(__name__)

@app.route('/hello')
def generate_large_csv():
    def generate():
        while True:
            json_data = json_creator.create_json()
            print(json_data)
            print('/n')
            yield json_data
            time.sleep(3)
    return Response(generate(), mimetype='text/json')


if __name__ == "__main__":
    app.run(host="0.0.0.0")