import networkx as nx
from graph import *
from seed import *
import random


improvingEdge = 0
acceptableEdge = 0
noneEdge = 0


def generateTree(cluster):
    G = nx.Graph()
    edges = cluster.edges
    random.shuffle(edges)

    G.add_edges_from(edges)
    spanningTree = nx.tree.minimum_spanning_edges(G, algorithm="kruskal", data=False)
    ST = nx.Graph()
    ST.add_edges_from(list(spanningTree))

    return ST

# create new cluster
def getNewCluster(graph, cutEdge, nodes, edges):
    newcluster = Cluster()
    oneId, twoId = cutEdge

    if oneId in nodes:
        newcluster.id = oneId
    else:
        newcluster.id = twoId

    for node in nodes:
        newcluster.nodes.append(graph.nodesDic[node])
        newcluster.pop += graph.nodesDic[node].pop
    newcluster.edges = edges

    return newcluster


def updateNeighbors(graph, mergedCluster, clusterOne, clusterTwo):
    for mergedClusterNode in mergedCluster.nodes:
        for neighborNode in mergedClusterNode.neighbors:
            if neighborNode not in clusterOne.nodes and neighborNode not in clusterTwo.nodes:  # it's an external node
                neighborCluster = graph.findCluster(neighborNode)  # find the external cluster the node belongs to

                # update clusterOne's neighbors and surrounded clusters's neighbors
                if mergedClusterNode in clusterOne.nodes:
                    if neighborCluster not in clusterOne.neighbors:
                        clusterOne.neighbors.append(neighborCluster)
                    if clusterOne not in neighborCluster.neighbors:
                        neighborCluster.neighbors.append(clusterOne)

                # update clusterTwo's neighbors and surrounded clusters's neighbors
                if mergedClusterNode in clusterTwo.nodes:
                    if neighborCluster not in clusterTwo.neighbors:
                        clusterTwo.neighbors.append(neighborCluster)
                    if clusterTwo not in neighborCluster.neighbors:
                        neighborCluster.neighbors.append(clusterTwo)

    # add each other to neighbors
    clusterOne.neighbors.append(clusterTwo)
    clusterTwo.neighbors.append(clusterOne)


def getNewClusters(graph, ST, cutEdge):
    # find nodes on the edge to be cut
    nodesOne = max(nx.connected_components(ST), key=len)
    nodesTwo = list(set(ST.nodes) - set(nodesOne))

    # create new clusters
    newClusterOne = getNewCluster(graph, cutEdge, nodesOne,
                                  list(list(ST.subgraph(c).copy() for c in nx.connected_components(ST))[0].nodes))
    newClusterTwo = getNewCluster(graph, cutEdge, nodesTwo,
                                  list(list(ST.subgraph(c).copy() for c in nx.connected_components(ST))[0].nodes))

    return newClusterOne, newClusterTwo


def getCompactness(border, totalNodes):
    return 1 - (border/totalNodes)


def getPopAndComp(graph, nodes):
    pop = 0
    border = 0
    totalNodes = len(nodes)

    for nodeId in nodes:
        node = graph.nodesDic[nodeId]
        pop += node.pop
        for neighborNode in node.neighbors:
            if neighborNode.id not in nodes:
                border += 1
                break

    compact = getCompactness(border, totalNodes)

    return pop, compact


def getComp(graph, nodes):
    border = 0
    totalNodes = len(nodes)

    for nodeId in nodes:
        node = graph.nodesDic[nodeId]
        for neighborNode in node.neighbors:
            if neighborNode.id not in nodes:
                border += 1
                break

    compact = getCompactness(border, totalNodes)

    return compact


def calculateScore(graph, oldDifference, oldCompact, newDifference, newCompact):
    score = 0
    popPercent = 0
    compPercent = 0

    if oldDifference != 0:
        popPercent = (abs(newDifference - oldDifference) / oldDifference)
    if oldCompact !=0:
        compPercent = (abs(newCompact - oldCompact) / oldCompact)

    if oldCompact > 2 * graph.compact and oldDifference >= 2 * (graph.upper-graph.idealPop):
        compPercent = 0

    if oldCompact <= 2 * graph.compact and oldDifference < 2 * (graph.upper - graph.idealPop):
        popPercent = 0

    if oldDifference < newDifference:  # worse
        score -= popPercent
    else:
        score += popPercent

    if newCompact < oldCompact:  # worse
        score -= compPercent
    else:
        score += compPercent

    #print("old compact: " + str(oldCompact) + ", old difference: " + str(oldDifference) +
    #      ", new compact: " +str(newCompact) + ", new difference: " + str(newDifference) + ", score: " + str(score))

    return score


def ifImproved(graph, oldDifference, oldCompact, newDifference, newCompact):
    score = calculateScore(graph, oldDifference, oldCompact, newDifference, newCompact)

    if score > 0:
        return True
    else:
        return False


def findEdge(graph, ST, oldDifference, oldCompact, careAllAcceptable, mergedCluster):
    # use case 32. Calculate the acceptability of each newly generated sub-graph (required)
    treeEdges = list(ST.edges)
    notFind = 0
    totalPop = mergedCluster.pop
    global improvingEdge
    global acceptableEdge
    global noneEdge


    while (notFind < 100):  # after 100 iterations if not found than stop
        # randomly chose an edge to cut
        cutEdge = random.choice(treeEdges)
        treeEdges.remove(cutEdge)
        oneID, twoID = cutEdge
        ST.remove_edge(oneID, twoID)

        nodesOne = max(nx.connected_components(ST), key=len)
        nodesTwo = list(set(ST.nodes) - set(nodesOne))

        # calculate new population score
        popOne, compactOne = getPopAndComp(graph, nodesOne)
        compactTwo = getComp(graph, nodesTwo)
        popTwo = totalPop - popOne
        newDifference = abs(popOne - popTwo)
        newCompact = compactOne + compactTwo

        ST.add_edge(oneID, twoID)

        oneAcceptable = isAcceptable(graph, popOne, compactOne)
        twoAcceptable = isAcceptable(graph, popTwo, compactTwo)
        # use case 35. Repeat the steps above until you generate satisfy the termination condition (required)
        if oneAcceptable and twoAcceptable and careAllAcceptable==True:
            #print("Edge selected to be cut: " + str(cutEdge))
            acceptableEdge += 1
            return cutEdge
        if ifImproved(graph, oldDifference, oldCompact, newDifference, newCompact):
            improvingEdge +=1
            return cutEdge
        if oneAcceptable and twoAcceptable and careAllAcceptable==False:
            #print("Edge selected to be cut: " + str(cutEdge))
            acceptableEdge += 1
            return cutEdge
        if len(treeEdges)==0:
            noneEdge += 1
            return None


def split(graph, mergedCluster, cutEdge, ST):
    # cut the edge
    oneID, twoID = cutEdge
    ST.remove_edge(oneID, twoID)
    # print("new clusters[" + str(oneID) + "] and [" + str(twoID) + "] generating...\n")

    # generate new clusters
    newClusterOne, newClusterTwo = getNewClusters(graph, ST, cutEdge)

    # update new clusters' and surrounded cluster's neighbors
    updateNeighbors(graph, mergedCluster, newClusterOne, newClusterTwo)

    # clean the graph
    for neighborCluster in mergedCluster.neighbors:
        neighborCluster.removeNeighbor(mergedCluster)
    graph.removeCluster(mergedCluster)

    # add new clusters on graph
    graph.clusters.append(newClusterOne)
    graph.clusters.append(newClusterTwo)


def merge(clusterOne, clusterTwo):
    # create an imaginary cluster
    mergedCluster = Cluster()

    mergedCluster.nodes = clusterOne.nodes + clusterTwo.nodes
    mergedCluster.pop = clusterOne.pop + clusterTwo.pop
    mergedCluster.updateEdges()

    return mergedCluster


def getCompact(cluster):
    border = 0

    for node in cluster.nodes:
        for neighborNode in node.neighbors:
            if neighborNode not in cluster.nodes:
                border += 1
                break

    compact = getCompactness(border, len(cluster.nodes))

    return compact


def isAcceptable(graph, pop, comp):
    upper = graph.upper
    lower = graph.lower
    if upper >= pop >= lower and comp > graph.compact:
        return True
    else:
        return False


def ifAllAcceptable(graph):
    upper = graph.upper
    lower = graph.lower

    for cluster in graph.clusters:
        comp = getCompact(cluster)
        if not (upper >= cluster.pop >= lower and comp > graph.compact):
            return False
    return True


def printDistricts(graph):
    outString = [["ID", "Population", "PopulationVariation", "Compactness"]]

    for cluster in graph.clusters:
        neighborsString = "["
        nodesString = "["

        for neighbor in cluster.neighbors:
            if neighbor != cluster.neighbors[-1]:
                neighborsString += str(neighbor.id) + ","
            else:
                neighborsString += str(neighbor.id) + "]"

        for node in cluster.nodes:
            if node != cluster.nodes[-1]:
                nodesString += str(node.id) + ","
            else:
                nodesString += str(node.id) + "]"

        outString.append(
            [str(cluster.id), str(cluster.pop), str(abs(cluster.pop - graph.idealPop)), str(getCompact(cluster))])

    print('{:<8} {:<8}  {:<8}  {:<8}'.format(*outString[0]))

    for i in range(1, len(outString)):
        print('{:<8} {:<8}     {:<8}            {:<8}'.format(*outString[i]))


def printDistrictsForTest(graph):
    outString = [["ID", "Population", "PopulationVariation", "NeighborDistrict", "Precinct"]]

    for cluster in graph.clusters:
        neighborsString = "["
        nodesString = "["

        for neighbor in cluster.neighbors:
            if neighbor != cluster.neighbors[-1]:
                neighborsString += str(neighbor.id) + ","
            else:
                neighborsString += str(neighbor.id) + "]"

        for node in cluster.nodes:
            if node != cluster.nodes[-1]:
                nodesString += str(node.id) + ","
            else:
                nodesString += str(node.id) + "]"

        outString.append(
            [str(cluster.id), str(cluster.pop), str(abs(cluster.pop - graph.idealPop)), neighborsString,
             nodesString])

    print('{:<8} {:<8}  {:<8}  {:<8}                              {:<8}'.format(*outString[0]))

    for i in range(1, len(outString)):
        print('{:<8} {:<8}     {:<8}           {:<8}                              {:<8}'.format(*outString[i]))
    print("--------------------------------------------------------------------------")


# Algorithm phase 2
def redistricting(graph, iterationLimit):
    allAcceptableIteration = 0
    careAllAcceptable = True
    n = 0

    while n < iterationLimit:  # use case 36. Terminate a single districting calculation (required)
        # use case 30. Generate a random districting satisfying constraints (required)
        clusterOne = random.choice(graph.clusters)
        clusterTwo = random.choice(clusterOne.neighbors)
        #print("Selected cluster[" + str(clusterOne.id) + "] and cluster[" + str(clusterTwo.id) + "]")

        # old score
        oldDifference = abs(clusterOne.pop - clusterTwo.pop)
        oldCompact = getCompact(clusterOne) + getCompact(clusterTwo)

        # create an "imaginary" cluster
        mergedCluster = merge(clusterOne, clusterTwo)

        # use case 31. Generate a spanning tree of the combined sub-graph above (required)
        ST = generateTree(mergedCluster)

        # use case 33. Generate a feasible set of edges in the spanning tree to cut (required)
        #print("Spanning Tree: " + str(ST.edges))
        cutEdge = findEdge(graph, ST, oldDifference, oldCompact, careAllAcceptable, mergedCluster)

        if cutEdge != None:
            # merge the clusters
            mergedCluster = combine(clusterOne, clusterTwo, graph)

            # use case 34. Cut the edge in the combined sub-graph (required)
            split(graph, mergedCluster, cutEdge, ST)

            #printDistricts(graph)
            #printDistrictsForTest(graph)

            if careAllAcceptable:
                allAcceptable = ifAllAcceptable(graph)
                if allAcceptable:
                    allAcceptableIteration = n
                    careAllAcceptable = False

            n += 1

    #totalEdge = acceptableEdge + improvingEdge + noneEdge
    #print("Ideal population: " + str(graph.idealPop))
    #print("Population variation: " + str(graph.popDifference))
    #print("Population valid range: " + str(graph.lower) + "-" + str(graph.upper))
    #print("Compactness goal: >" + str(graph.compact) + '\n')
    printDistricts(graph)
    #print("\nEdges are acceptable: " + str(acceptableEdge) + "(" + str(acceptableEdge/totalEdge*100) + "%)")
    #print("Edges improve the graph: : " + str(improvingEdge) + "(" + str(improvingEdge/totalEdge*100) + "%)")
    #print("Edges are not acceptable and not improve the graph: " + str(noneEdge) + "(" + str(noneEdge/totalEdge*100) + "%)")
    # print(str(n) + " iterations over. All clusters acceptable after: " + str(allAcceptableIteration) + " iterations.")

