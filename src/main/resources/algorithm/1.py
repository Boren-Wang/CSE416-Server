import json

with open('./416443_0.json', 'r') as fp:
    data = json.load(fp)
    print("Number of districting plans in 416443_0.json is {}".format(len(data)))

with open('./416443_1.json', 'r') as fp:
    data = json.load(fp)
    print("Number of districting plans in 416443_1.json is {}".format(len(data)))

# [[[1,2], [3, 4]], [[1,3], [2, 4]], [[1,4], [2, 3]]]

# [1,2] -> one district
# [[1,2], [3, 4]] -> one districting
# [[[1,2], [3, 4]], [[1,3], [2, 4]], [[1,4], [2, 3]]] -> one list of districtings