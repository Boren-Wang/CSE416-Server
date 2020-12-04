class Node:
    def __init__(self, id, pop=None, shape=None):
        self.id = id
        self.neighbors = []

        if pop != None:
            self.pop = pop

        if shape != None:
            self.shape = shape

    def addNeighbor(self, neighborNode):
        if neighborNode not in self.neighbors:
            self.neighbors.append(neighborNode)


class Cluster:
    def __init__(self, node=None):
        self.nodes = []
        self.edges = []
        self.neighbors = []
        if node!=None:
            self.id = node.id
            self.pop = node.pop
            self.nodes.append(node)
        else:
            self.id = 0
            self.pop = 0

    def addNeighbor(self, cluster):
        self.neighbors.append(cluster)

    def removeNeighbor(self, cluster):
        self.neighbors.remove(cluster)

    def updatePop(self):
        self.pop = 0
        for node in self.nodes:
            self.pop += node.pop

    def updateEdges(self):
        self.edges = []
        for node in self.nodes:
            uid = node.id
            for neighbor in node.neighbors:
                vid = neighbor.id
                if neighbor in self.nodes:
                    if ((uid, vid) not in self.edges) and ((vid, uid) not in self.edges):
                        self.edges.append((uid, vid))


class Graph:
    def __init__(self, numCluster, populationVariation):
        self.nodes = []
        self.nodesDic = {}
        self.clusters = []
        self.edges = []
        self.pop = 0
        self.numCluster = numCluster
        self.populationVariation = populationVariation
        self.lowerBound = 0
        self.upperBound = 0
        self.idealPop = 0

    def findCluster(self, node):
        for cluster in self.clusters:
            if node in cluster.nodes:
                return cluster
        return None

    def findNode(self, n_id):
        for node in self.nodes:
            if node.id == n_id:
                return node
        return None

    def addNode(self, node):
        self.nodes.append(node)
        self.nodesDic[node.id] = node
        self.clusters.append(Cluster(node))
        self.pop += node.pop
        self.updateBounds()

    def addEdge(self, uid, vid):
        u = self.findNode(uid)
        v = self.findNode(vid)
        if u not in v.neighbors and v not in u.neighbors:
            u.addNeighbor(v)
            v.addNeighbor(u)
            self.edges.append((uid, vid))
            uCluster = self.findCluster(u)
            vCluster = self.findCluster(v)
            if vCluster not in uCluster.neighbors:
                uCluster.addNeighbor(vCluster)
            if uCluster not in vCluster.neighbors:
                vCluster.addNeighbor(uCluster)

    def addNodeForms(self, nodeForms, popForms):
        for i in range(len(nodeForms)):
            node = Node(nodeForms[i], popForms[i])
            self.addNode(node)

    def addEdgeForms(self, edgeForms):
        for uid, vid in edgeForms:
            self.addEdge(uid, vid)

    def printClusters(self):
        outString = [["ID", "Population", "PopulationVariation", "NeighborDistrict", "Precinct"]]

        for cluster in self.clusters:
            neighborsString ="["
            nodesString ="["

            for neighbor in cluster.neighbors:
                if neighbor!=cluster.neighbors[-1]:
                    neighborsString += str(neighbor.id) + ","
                else:
                    neighborsString += str(neighbor.id) + "]"

            for node in cluster.nodes:
                if node!=cluster.nodes[-1]:
                    nodesString += str(node.id) + ","
                else:
                    nodesString += str(node.id) + "]"

            outString.append([str(cluster.id), str(cluster.pop), str(abs(cluster.pop - self.idealPop)), neighborsString,
                              nodesString])

        print('{:<8} {:<8}  {:<8}  {:<8}                              {:<8}'.format(*outString[0]))

        for i in range(1, len(outString)):
            print('{:<8} {:<8}     {:<8}           {:<8}                              {:<8}'.format(*outString[i]))

    def totPop(self):
        return self.pop

    def removeCluster(self, cluster):
        self.clusters.remove(cluster)

    def updateBounds(self):
        ideal = self.pop/self.numCluster
        self.lowerBound = int(ideal - ideal * self.populationVariation)
        self.upperBound = int(ideal + ideal * self.populationVariation)

