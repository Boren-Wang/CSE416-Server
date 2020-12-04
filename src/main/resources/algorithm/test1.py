###################
    G1 = nx.Graph()
    G2 = nx.Graph()
    fig, axes = plt.subplots(nrows=1, ncols=2)
    ax = axes.flatten()

    nodes = []
    edges = []

    for cluster in graph.clusters:
        nodes.append(cluster.id)
    G1.add_nodes_from(nodes)

    for cluster in graph.clusters:
        u_id = cluster.id
        for neighbor_cluster in cluster.neighbors:
            v_id = neighbor_cluster.id
            if (u_id, v_id) not in edges and (v_id, u_id) not in edges:
                edges.append((u_id, v_id))

    G1.add_edges_from(edges)

    nx.draw(G1, with_labels=True, ax=ax[0])
    ax[0].set_axis_off()
    ###################





    graph.printClusters()

    #print("\n\nRebalance...\n")
    #print("--------------------------------------------------------------------------")
    #rebalance(graph)












    ###################
    nodes = []
    edges = []

    for cluster in graph.clusters:
        nodes.append(cluster.id)
    G2.add_nodes_from(nodes)

    for cluster in graph.clusters:
        u_id = cluster.id
        for neighbor_cluster in cluster.neighbors:
            v_id = neighbor_cluster.id
            if (u_id, v_id) not in edges and (v_id, u_id) not in edges:
                edges.append((u_id, v_id))

    G2.add_edges_from(edges)

    nx.draw(G2, with_labels=True, ax=ax[1])
    ax[1].set_axis_off()

    #plt.show()