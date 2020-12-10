def updateEdges(self):
    self.edges = []
    for node in self.nodes:
        uid = node.id
        for neighbor in node.neighbors:
            vid = neighbor.id
            if neighbor in self.nodes:
                if ((uid, vid) not in self.edges) and ((vid, uid) not in self.edges):
                    self.edges.append((uid, vid))
