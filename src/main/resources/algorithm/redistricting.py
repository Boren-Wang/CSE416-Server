import random
import networkx as nx
from graph import Cluster
from seed import combine

MERGEDCLUSTERID = 9999


def generateTree(cluster):
    G = nx.Graph()
    G.add_edges_from(cluster.edges)

    spanningTree = nx.tree.minimum_spanning_edges(G, algorithm="kruskal", data=False)
    ST = nx.Graph()
    ST.add_edges_from(list(spanningTree))

    return ST


# if a single cluster is acceptable?
def isAcceptable(graph, cluster):
    upper = graph.upperBound
    lower = graph.lowerBound
    if upper >= cluster.pop >= lower:
        return True
    else:
        return False


# create new cluster
def getNewCluster(graph, id, nodes, edges):
    a = 0
    newcluster = Cluster()

    newcluster.id = id
    for node in nodes:
        newcluster.nodes.append(graph.nodesDic[node])
    a = 0
    newcluster.edges = edges
    newcluster.updatePop()
    a =0

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

    # add each other as neighbor
    clusterOne.neighbors.append(clusterTwo)
    clusterTwo.neighbors.append(clusterOne)


def getNewClusters(graph, ST, cutEdge):
    a = 0
    oneID, twoID = cutEdge
    # find nodes on the edge to be cut
    nodesOne = list(ST.subgraph(c).copy() for c in nx.connected_components(ST))[0].nodes
    nodesTwo = list(ST.subgraph(c).copy() for c in nx.connected_components(ST))[1].nodes
    a = 0
    # create new clusters
    newClusterOne = getNewCluster(graph, oneID, nodesOne, list(list(ST.subgraph(c).copy() for c in nx.connected_components(ST))[0].nodes))
    newClusterTwo = getNewCluster(graph, twoID, nodesTwo, list(list(ST.subgraph(c).copy() for c in nx.connected_components(ST))[0].nodes))
    a = 0
    return newClusterOne, newClusterTwo


def findEdge(graph, mergedCluster, ST, oldDifference):
    # use case 32. Calculate the acceptability of each newly generated sub-graph (required)
    treeEdges = list(ST.edges)
    while (1):
        a = 0
        # randomly chose an edge to cut
        cutEdge = random.choice(treeEdges)
        treeEdges.remove(cutEdge)
        oneID, twoID = cutEdge
        ST.remove_edge(oneID, twoID)

        a = 0
        # generate new clusters
        newClusterOne, newClusterTwo = getNewClusters(graph ,ST, cutEdge)
        a = 0

        # calculate new score
        newDifference = abs(newClusterOne.pop - newClusterTwo.pop)
        a = 0
        ST.add_edge(oneID, twoID)
        # use case 35. Repeat the steps above until you generate satisfy the termination condition (required)
        if isAcceptable(graph, newClusterOne) == True and isAcceptable(graph, newClusterTwo
                                                                       ) == True:  # if acceptable
            print("Edge selected to be cut: " + str(cutEdge))
            print("\nnew variation: " + str(newDifference) + ", old variation: " + str(oldDifference))
            print("new clusters[" + str(newClusterOne.id) + "] and [" + str(newClusterTwo.id) + "] generating...\n")
            return cutEdge
        if newDifference < oldDifference:  # if improved
            print("Edge selected to be cut: " + str(cutEdge))
            print("\nnew variation: " + str(newDifference) + ", old variation: " + str(oldDifference))
            print("new clusters[" + str(newClusterOne.id) + "] and [" + str(newClusterTwo.id) + "] generating...\n")
            return cutEdge
        if len(treeEdges)==0:
            return None
        a = 0


def split(graph, mergedCluster, cutEdge, ST):
    # cut the edge
    oneID, twoID = cutEdge
    ST.remove_edge(oneID, twoID)

    # generate new clusters
    newClusterOne, newClusterTwo = getNewClusters(graph, ST, cutEdge)

    # update new clusters' and surrounded cluster's neighbors
    updateNeighbors(graph, mergedCluster, newClusterOne, newClusterTwo)

    # erase the merged cluster from the graph
    for cluster in graph.clusters:
        if mergedCluster in cluster.neighbors:
            cluster.removeNeighbor(mergedCluster)
    graph.removeCluster(mergedCluster)

    # add new clusters on graph
    graph.clusters.append(newClusterOne)
    graph.clusters.append(newClusterTwo)


def merge(clusterOne, clusterTwo):
    # create an imaginary cluster
    mergedCluster = Cluster()

    #  update properties
    mergedCluster.nodes = clusterOne.nodes + clusterTwo.nodes
    mergedCluster.updatePop()
    mergedCluster.updateEdges()

    #  collect surrounded cluster as neighbors
    for neighborCluster in clusterOne.neighbors:
        if neighborCluster != clusterTwo and neighborCluster not in mergedCluster.neighbors:
            mergedCluster.neighbors.append(neighborCluster)

    for neighborCluster in clusterTwo.neighbors:
        if neighborCluster != clusterOne and neighborCluster not in mergedCluster.neighbors:
            mergedCluster.neighbors.append(neighborCluster)

    # give an id
    mergedCluster.id = MERGEDCLUSTERID

    return mergedCluster


# Algorithm phase 2
def rebalance(graph, iterationLimit):
    n = 0

    while n < iterationLimit:  # use case 36. Terminate a single districting calculation (required)
        # use case 30. Generate a random districting satisfying constraints (required)
        clusterOne = random.choice(graph.clusters)
        clusterTwo = random.choice(clusterOne.neighbors)
        oldVariation = abs(clusterOne.pop - clusterTwo.pop)  # old score
        print("Selected cluster[" + str(clusterOne.id) + "] and cluster[" + str(clusterTwo.id) + "]")

        # assume to merge the clusters, which means, the merged cluster is "imaginary"
        mergedCluster = merge(clusterOne, clusterTwo)

        # use case 31. Generate a spanning tree of the combined sub-graph above (required)
        print("Generating Spinning Tree...")
        ST = generateTree(mergedCluster)

        # use case 33. Generate a feasible set of edges in the spanning tree to cut (required)
        print("Spanning Tree: " + str(ST.edges))
        print("Finding an feasible edge...")
        cutEdge = findEdge(graph, mergedCluster, ST, oldVariation)

        if cutEdge == None:
            print("Feasible edge couldn't be found. Leave the original clusters as they were\n")
        else:
            # merge the clusters in real
            combine(clusterOne, clusterTwo, graph)

            # use case 34. Cut the edge in the combined sub-graph (required)
            split(graph, clusterOne, cutEdge, ST)

            graph.printClusters()
            print("--------------------------------------------------------------------------")

            n += 1