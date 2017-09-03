import json
import random

from copy import deepcopy

class JSONCreator:
    def __init__(self):
        with open('example.json') as data_file:
            self._example_data = json.load(data_file)

    def create_json(self):
        data = deepcopy(self._example_data)
        sex = random.sample(set(['F', 'M']), 1)[0]
        for product in data['products']:
            product['pricePerUnit'] = random.randint(1, 700)
            product['amount'] = random.randint(1, 10)
        data['client']['sex'] = sex
        return json.dumps(data)
