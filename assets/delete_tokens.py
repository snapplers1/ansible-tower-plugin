#!/usr/bin/env python

import requests
from requests.auth import HTTPBasicAuth

keep_going = True
while keep_going:
    response = requests.get('https://tower.example.com/api/v2/tokens/', auth=HTTPBasicAuth('admin', 'password'), verify=False)
    data = response.json()
    if data['count'] == 0:
        keep_going = False
    else:
        for token_data in data['results']:
            print("Deleting token {}".format(token_data['id']))
            response = requests.delete('https://tower.example.com/api/v2/tokens/{}/'.format(token_data['id']), auth=HTTPBasicAuth('admin', 'password'), verify=False)
