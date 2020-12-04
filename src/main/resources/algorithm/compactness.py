from shapely.geometry import Polygon
from shapely.ops import cascaded_union
import math

#[740,750,824,906,900,739,754,899,905]


def calculate(cluster):
    shapes = []
    for node in cluster.nodes:
        shapes.append(node.shape)
    u = cascaded_union(shapes)
    area = u.area
    length = u.length
    compactness = 4*math.pi*u.area/(u.length**2)

    return compactness