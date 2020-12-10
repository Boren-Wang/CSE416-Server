import json
import cProfile, pstats
import os
import sys
from multiprocessing import Pool
import multiprocessing as mp
from redistricting import redistricting
from seed import generateSeed
from graph import Graph, Node

iterationLimit = 1000  # 测试速度或者会不会报错的时候改成50，实际run的时候改成1000

GA = './algorithm/GA.json' # 测试速度或者会不会报错的时候用ga速度最快
districtsGA = 14

MI = './algorithm/MI.json'
districtsMI = 4

LA = './algorithm/LA.json'
districtsLA = 6

# popDifference = 0.03
# compactness = 0.1

path = None
num = 0
graph = None


def generatePlan():  # 这是需要多程去run的function
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
    state = sys.argv[1]
    numberOfDistrictings = int(sys.argv[2])  # <- 我的算法不需要这个
    populationDifference = float(sys.argv[3])  # 0.015 ~ 0.03
    compactnessGoal = float(sys.argv[4])  # 0.2~0.5

    arguments = [state, populationDifference, compactnessGoal]
    arguments_list = [arguments for x in range(numberOfDistrictings)]  # <-这里好像需要改改？
    print(arguments_list)

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

    # 多进程
    pool_size = mp.cpu_count()
    print("NUM CPUs", pool_size)

    with Pool(processes=pool_size) as pool:
        for i in pool.imap_unordered(generatePlan, range(numberOfDistrictings)):  # <- 我把原先这里的argument list去掉了，我的function没有接受任何argument
            print(i)

    # exiting the 'with'-block has stopped the pool
    print("Now the pool is closed and no longer available")
