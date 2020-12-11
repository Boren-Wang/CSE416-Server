import json

result = [[1,1,1], [2,2,2]]
with open('1.json', 'w') as fp:
    json.dump(result, fp)