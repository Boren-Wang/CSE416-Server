from networkx.algorithms import tree
import networkx as nx




#G = nx.Graph()
#G.add_edges_from([(6, 7), (7, 8), (8, 5), (5, 6), (8, 9), (9, 10), (10, 5), (5, 4), (4, 3), (3, 11), (11, 10), (10, 12), (11, 12), (12, 15)])
#mst = tree.minimum_spanning_edges(G, algorithm="kruskal", data=False)
#edgelist = list(mst)
#print(sorted(sorted(e) for e in edgelist))
edgelist = [(6, 7), (6, 5), (7, 8), (5, 10), (5, 4), (8, 9), (10, 11), (10, 12), (4, 3), (12, 15)]
S = nx.Graph()
S.add_edges_from(edgelist)

print(S.edges)
S.remove_edge(5,10)
sub_graphs = (S.subgraph(c).copy() for c in nx.connected_components(S))


b = list(S.subgraph(c).copy() for c in nx.connected_components(S))[0].nodes
a = list(S.subgraph(c).copy() for c in nx.connected_components(S))[1].nodes
print(list(sub_graphs)[1].nodes)

