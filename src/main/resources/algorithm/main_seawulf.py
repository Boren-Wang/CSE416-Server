import json
import sys
import os
from redistricting import rebalance
from seed import generateSeed
from graph import Graph, Node
import multiprocessing as mp
from multiprocessing import Pool

path_GA = './algorithm/GA_precincts_simplified_plus (1).json'
path_LA = ''
path_MI = ''

def generate_plan(arguments):
    state = arguments[0]
    populationDifference = arguments[1]
    compactnessGoal = arguments[2]

    path = None
    if state == 'GEORGIA':
        path = path_GA
    if state == 'LOUISIANA':
        path = path_LA
    if state == 'MISSISSIPPI':
        path = path_MI

    # import data
    with open(path) as f:
        data = json.load(f)
    graph = Graph(14, populationDifference) # number of districts=14 ,1/2population variation: 0.9ideal~1.1ideal

    for i in range(len(data['features'])):
        node = Node(data['features'][i]['properties']['ID'], data['features'][i]['properties']['TOTPOP'])
        graph.addNode(node)

    for i in range(len(data['features'])):
        id = data['features'][i]['properties']['ID']
        neighborsId = data['features'][i]['properties']['Neighbors']

        for neighborId in neighborsId:
            graph.addEdge(id, neighborId)

    graph.idealPop = int(graph.pop/graph.numCluster)

    # generate seed plan
    print("Generating seed plan...\n")
    generateSeed(graph)

    print("Ideal population: " + str(graph.idealPop))
    print("Population variation: " + str(2 * graph.populationVariation))
    print("Population valid range: " + str(graph.lowerBound) + "-" + str(graph.upperBound))
    print('\n')
    print("Seed plan:")
    graph.printClusters()

    # re-balance for 30 iterations
    print("\n\nRebalance...\n")
    print("--------------------------------------------------------------------------")
    rebalance(graph, 30)

    result = []
    for cluster in graph.clusters:
        district = []
        for node in cluster.nodes:
            district.append(node.id)
        result.append(district)

    return result

if __name__ == '__main__':
    print(os.getcwd())
    state = sys.argv[1]
    numberOfDistrictings = int(sys.argv[2])
    populationDifference = float(sys.argv[3])
    compactnessGoal = float(sys.argv[4])

    arguments = [state, populationDifference, compactnessGoal]
    arguments_list = [arguments for x in range(numberOfDistrictings)]
    print(arguments_list)

    pool_size = mp.cpu_count()
    print("NUM CPUs", pool_size)

    with Pool(processes=pool_size) as pool:
        for i in pool.imap_unordered(generate_plan, arguments_list):
            print(i)

    # exiting the 'with'-block has stopped the pool
    print("Now the pool is closed and no longer available")

    

