import random


def combine(source, target, graph):
    #  update properties. Add all the properties of the target on the source cluster.
    source.nodes = source.nodes + target.nodes
    source.pop = source.pop + target.pop
    source.updateEdges()

    # update cluster's neighbors and surrounded clusters's neighbors
    for cluster in target.neighbors:
        cluster.removeNeighbor(target)

        if source == cluster:
            continue
        if source not in cluster.neighbors:
            cluster.addNeighbor(source)
        if cluster not in source.neighbors:
            source.addNeighbor(cluster)

    # remove the target from the graph
    graph.removeCluster(target)

    return source


# use case 29. Generate seed districting (required)
def generateSeed(graph):
    n = graph.numCluster

    # until n districts on graphs
    while len(graph.clusters) != n:
        clusters = graph.clusters

        source = random.choice(clusters)
        target = random.choice(source.neighbors)

        combine(source, target, graph)

