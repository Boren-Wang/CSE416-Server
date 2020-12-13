from shapely.geometry import Polygon
from shapely.ops import cascaded_union
from shapely.geometry import shape
import json
import math
import sys

###############################################
polygon1 = Polygon([(0, 0), (5, 3), (5, 0)])
polygon2 = Polygon([(0, 0), (3, 10), (3, 0)])

polygons = [polygon1, polygon2]

u = cascaded_union(polygons)
###############################################

districting_json_path = sys.argv[1]
state_json_path = sys.argv[2]
jobIdString = sys.argv[3]
type = sys.argv[4]

# 读取districting信息
path = districting_json_path
with open(path) as f:
    districting = json.load(f)
districts = districting["districts"]
# for d in districts:
# print(d)

# 读取precincts geojson
path = state_json_path
with open(path) as f:
    precincts = json.load(f)["features"]

# for p in precincts:
#    print(p)

# 根据districting，merge precincts
districtsPoly = []  # 这里集合的是所有district的形状，和下面那个不一样

new_data = {}
new_data['type'] = 'FeatureCollection'
new_data['features'] = []

for district in districts:

    poly = {}
    poly['type'] = 'Feature'
    poly['geometry'] = {}
    poly['properties'] = {}
    poly['properties']['ID'] = district["districtId"]

    # 人口数据1 在人口数据的comment下补一下缺少的人口数据
    pop = 0
    vap = 0
    hvap = 0
    wvap = 0
    bvap = 0
    aminvap = 0
    asianvap = 0
    nhpivap = 0

    WHITE = 0
    BLACK = 0
    HISP = 0
    AMIN = 0
    OTHER = 0
    ASIAN = 0
    NHPI = 0

    precinctIds = district["precinctIds"]
    districtPolygons = []  # 相当于于我之前发你例子中的 polygons = [polygon1, polygon2]，这个和上面那个不一样
    for precinctId in precinctIds:
        for i in range(len(precincts)):
            if precincts[i]["properties"]["ID"] == precinctId:
                districtPolygons.append(shape(precincts[i]["geometry"]))
                # 人口数据2 在人口数据的comment下补一下缺少的人口数据
                pop += precincts[i]["properties"]["TOTPOP"]
                vap += precincts[i]["properties"]["VAP"]
                hvap += precincts[i]["properties"]["HVAP"]
                wvap += precincts[i]["properties"]["WVAP"]
                bvap += precincts[i]["properties"]["BVAP"]
                aminvap += precincts[i]["properties"]["AMINVAP"]
                asianvap += precincts[i]["properties"]["ASIANVAP"]
                nhpivap += precincts[i]["properties"]["NHPIVAP"]
                WHITE += precincts[i]["properties"]["WHITE"]
                BLACK += precincts[i]["properties"]["BLACK"]
                HISP += precincts[i]["properties"]["HISP"]
                AMIN += precincts[i]["properties"]["AMIN"]
                OTHER += precincts[i]["properties"]["OTHER"]
                ASIAN += precincts[i]["properties"]["ASIAN"]
                NHPI += precincts[i]["properties"]["NHPI"]

    u = cascaded_union(districtPolygons)
    districtsPoly.append(u)

    poly['geometry']['type'] = u.type
    poly['geometry']['coordinates'] = [list(u.exterior.coords)]

    # 人口数据3 在人口数据的comment下补一下缺少的人口数据
    poly['properties']['TOTPOP'] = pop
    poly["properties"]["VAP"] = vap
    poly["properties"]["HVAP"] = hvap
    poly["properties"]["WVAP"] = wvap
    poly["properties"]["BVAP"] = bvap
    poly["properties"]["AMINVAP"] = aminvap
    poly["properties"]["ASIANVAP"] = asianvap
    poly["properties"]["NHPIVAP"] = nhpivap
    poly["properties"]["WHITE"] = WHITE
    poly["properties"]["BLACK"] = BLACK
    poly["properties"]["HISP"] = HISP
    poly["properties"]["AMIN"] = AMIN
    poly["properties"]["OTHER"] = OTHER
    poly["properties"]["ASIAN"] = ASIAN
    poly["properties"]["NHPI"] = NHPI

    new_data['features'].append(poly)

def getComp(p):
    area = p.area
    length = p.length
    pi = math.pi
    comp = 4 * pi * area / (length) ** 2
    return comp

# 打印看看ga 14个选区的compactness
# for d in districtsPoly:
#     print(getComp(d))

# 生成 json
with open('src/main/resources/results/' + jobIdString + '_' + type + '_geo.json', 'w') as outfile:
    json.dump(new_data, outfile)