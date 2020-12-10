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
    def __init__(self, numCluster, popDifference, compact):
        self.nodes = []
        self.nodesDic = {}
        self.clusters = []
        self.edges = []
        self.numCluster = numCluster
        self.pop = 0
        self.lower = 0
        self.upper = 0
        self.idealPop = 0
        self.popDifference = popDifference
        self.compact = compact

    def findCluster(self, node):
        for cluster in self.clusters:
            if node in cluster.nodes:
                return cluster
        return None

    def addNode(self, node):
        self.nodes.append(node)
        self.nodesDic[node.id] = node
        self.clusters.append(Cluster(node))
        self.pop += node.pop

    def addEdge(self, uid, vid):
        u = self.nodesDic[uid]
        v = self.nodesDic[vid]
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

    def removeCluster(self, cluster):
        self.clusters.remove(cluster)

    def getUpper(self):
        return int(self.idealPop + self.idealPop * self.popDifference * 0.5)

    def getLower(self):
        return int(self.idealPop - self.idealPop * self.popDifference * 0.5)

    def getIdealPop(self):
        return int(self.pop/self.numCluster)

    def addNodeForms(self, nodeForms, popForms):
        for i in range(len(nodeForms)):
            node = Node(nodeForms[i], popForms[i])
            self.addNode(node)

    def addEdgeForms(self, edgeForms):
        for uid, vid in edgeForms:
            self.addEdge(uid, vid)


