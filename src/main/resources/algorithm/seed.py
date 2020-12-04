import random


def combine(cluster, target, graph):
    #  update properties. Add all the properties of the target on the cluster.
    cluster.nodes = cluster.nodes + target.nodes
    cluster.updatePop()
    cluster.updateEdges()

    # update cluster's neighbors and surrounded clusters's neighbors
    for clu in target.neighbors:
        if cluster == clu:
            clu.removeNeighbor(target)
            continue
        if cluster not in clu.neighbors:
            clu.addNeighbor(cluster)
        if clu not in cluster.neighbors:
            cluster.addNeighbor(clu)
        clu.removeNeighbor(target)

    # remove the target from the graph
    graph.removeCluster(target)


# use case 29. Generate seed districting (required)
def generateSeed(graph):
    n = graph.numCluster

    for cluster in graph.clusters:
        cluster.updateEdges()

    while len(graph.clusters) != n:  # until n districts on graphs
        clusters = graph.clusters
        cluster = random.choice(clusters)  # randomly select a cluster
        target = random.choice(cluster.neighbors)  # randomly select one of its neighbors
        combine(cluster, target, graph)  # combine them

