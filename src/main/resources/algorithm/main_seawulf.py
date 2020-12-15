import json
import cProfile, pstats
import os
import sys
from multiprocessing import Pool
import multiprocessing as mp
from redistricting import redistricting
from seed import generateSeed
from graph import Graph, Node
import copy

iterationLimit = 1000  # 测试速度或者会不会报错的时候改成50，实际run的时候改成1000

GA = './algorithm/georgia.json' # 测试速度或者会不会报错的时候用ga速度最快
districtsGA = 14

MI = './algorithm/mississippi.json'
districtsMI = 4

LA = './algorithm/louisiana.json'
districtsLA = 6

# popDifference = 0.03
# compactness = 0.1

path = None
num = 0

def generatePlan(arguments):  # 被imap调用的的函数必须至少接受一个参数，这里的dummy_arg并不会被使用到
    num = arguments[0]
    populationDifference = arguments[1]
    compactnessGoal = arguments[2]
    graph = Graph(num, populationDifference, compactnessGoal)

    for i in range(len(data['features'])):
        node = Node(data['features'][i]['properties']['ID'], data['features'][i]['properties']['TOTPOP'])
        graph.addNode(node)

    for i in range(len(data['features'])):
        id = data['features'][i]['properties']['ID']
        neighborsId = data['features'][i]['properties']['Neighbors']

        for neighborId in neighborsId:
            graph.addEdge(id, neighborId)

    graph.idealPop = graph.getIdealPop()
    graph.upper = graph.getUpper()
    graph.lower = graph.getLower()

    generateSeed(graph)
    redistricting(graph, iterationLimit)

    plan = []
    for cluster in graph.clusters:
        district = []
        for node in cluster.nodes:
            district.append(node.id)
        plan.append(district)

    return plan

if __name__ == '__main__':
    print(os.getcwd())

    # 接受arguments
    jobId = sys.argv[1]
    state = sys.argv[2]
    numberOfDistrictings = int(sys.argv[3]) # 进程池要完成的plan总量
    populationDifference = float(sys.argv[4])  # 0.03 ~ 0.05
    compactnessGoal = float(sys.argv[5])  # 0-1 -> 0.2~0.5
    rank = sys.argv[6]

    # 确认路径
    if state == 'GEORGIA':
        path = GA
        num = districtsGA
    if state == 'LOUISIANA':
        path = LA
        num = districtsLA
    if state == 'MISSISSIPPI':
        path = MI
        num = districtsMI

    # 导入数据只需要一次，不需要多进程
    with open(path) as f:
        data = json.load(f)

    # 多进程
    arguments = [num, populationDifference, compactnessGoal]
    arguments_list = [arguments for x in range(numberOfDistrictings)]
    # print(arguments_list)

    pool_size = mp.cpu_count()
    print("NUM CPUs", pool_size)

    result = []

    with Pool(processes=pool_size) as pool:
        # 这里明细了多进程要完成的plan总量，进程池会自动分配进程，直到所有plan都生成
        for i in pool.imap_unordered(generatePlan, arguments_list):
            result.append(i)

    with open('./'+jobId+"_"+rank+'.json', 'w') as fp:
        json.dump(result, fp)

    # exiting the 'with'-block has stopped the pool
    print("Now the pool is closed and no longer available")
