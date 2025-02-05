import json


class MitmSolution:
    def __int__(self):
        self.Rounds = 0
        self.objective = 0
        self.DA = None
        self.DP = None
        self.DP2 = None
        self.DC1 = None
        self.DC12 = None
        self.DB = None
        self.DC2 = None
        self.dom = None
        self.obj = None


def MitmSolution2File(mitmSolution, filename):
    dict_solution = mitmSolution.__dict__
    with open(filename, 'w') as file:
        json.dump(dict_solution, file)
    file.close()


def File2MitmSolution(filename):
    with open(filename, 'r', encoding='utf-8') as file:
        json_Solution = json.load(file)[0]
    file.close()
    mitmSolution = MitmSolution()
    mitmSolution.__dict__ = json_Solution
    return mitmSolution


def FetchInitialState(mitmSolution):
    red_z = [[], [], [], []]
    blue_z = [[], [], [], []]
    gray_z = [[], [], [], []]
    slice_num = len(mitmSolution.DA[0][0][0])
    for k in range(slice_num):
        for i in range(4):
            if (mitmSolution.DA[0][i][2][k][0] == 1) & (mitmSolution.DA[0][i][2][k][2] == 0):
                blue_z[i].append(k)
            if (mitmSolution.DA[0][i][2][k][0] == 0) & (mitmSolution.DA[0][i][2][k][2] == 1):
                red_z[i].append(k)
            if (mitmSolution.DA[0][i][2][k][0] == 0) & (mitmSolution.DA[0][i][2][k][2] == 0):
                gray_z[i].append(k)
    return red_z, blue_z, gray_z


def FetchConsumes(mitmSolution):
    # consume in A to C
    consume_c = [[[], [], [], []] for r in range(len(mitmSolution.DC1))]
    for r in range(len(mitmSolution.DC1)):
        for i in range(len(mitmSolution.DC1[r])):
            for k in range(len(mitmSolution.DC1[r][i])):
                if mitmSolution.DC1[r][i][k][1] == 1:
                    consume_c[r][i].append(k)
    # consume in C to D
    consume_d = [[[], [], [], []] for r in range(len(mitmSolution.DC12))]
    for r in range(len(mitmSolution.DC12)):
        for i in range(len(mitmSolution.DC12[r])):
            for k in range(len(mitmSolution.DC12[r][i])):
                if mitmSolution.DC12[r][i][k][1] == 1:
                    consume_d[r][i].append(k)
    # consume in A ^ D = theta
    consume_t = [[[], []] for r in range(len(mitmSolution.DC2))]
    for r in range(len(mitmSolution.DC2)):
        for i in range(len(mitmSolution.DC2[r])):
            for j in range(len(mitmSolution.DC2[r][i])):
                for k in range(len(mitmSolution.DC2[r][i][j])):
                    if mitmSolution.DC2[r][i][j][k][1] == 1:
                        consume_t[r][0].append(i + 4 * j)
                        consume_t[r][1].append(k)
    return consume_c, consume_d, consume_t


def FetchConditions(mitmSolution):
    slice_num = len(mitmSolution.DA[0][0][0])

    con0 = []
    con1 = []
    for k in range(slice_num):
        for i in range(4):
            if only_one_color(mitmSolution.DB[0][i][0][k], mitmSolution.DB[0][i][1][k], mitmSolution.DB[0][i][2][k]):
                cIndex = color_index(mitmSolution.DB[0][i][0][k], mitmSolution.DB[0][i][1][k],
                                     mitmSolution.DB[0][i][2][k])
                if isGray(fetchChi(mitmSolution, i, (cIndex + 1) % 3, k)):
                    con1.append([i + 4 * ((cIndex + 2) % 3), k])
                if isGray(fetchChi(mitmSolution, i, (cIndex + 2) % 3, k)):
                    con0.append([i + 4 * ((cIndex + 1) % 3), k])
    print(len(con0),len(con1))
    lane0 = []
    zero_z = []
    for item in con0:
        if item[0] not in lane0:
            lane0.append(item[0])
            zero_z.append([item[1]])
        else:
            ind = lane0.index(item[0])
            zero_z[ind].append(item[1])
    lane1 = []
    one_z = []
    for item in con1:
        if item[0] not in lane1:
            lane1.append(item[0])
            one_z.append([item[1]])
        else:
            ind = lane1.index(item[0])
            one_z[ind].append(item[1])
    return lane0, zero_z, lane1, one_z


def FetchConditions_o(mitmSolution):
    slice_num = len(mitmSolution.DA[0][0][0])

    con0 = []
    con1 = []
    for k in range(slice_num):
        for i in range(4):
            if only_one_color(mitmSolution.DB[0][i][0][k], mitmSolution.DB[0][i][1][k], mitmSolution.DB[0][i][2][k]):
                cIndex = color_index(mitmSolution.DB[0][i][0][k], mitmSolution.DB[0][i][1][k],
                                     mitmSolution.DB[0][i][2][k])
                if isGray(fetchChi(mitmSolution, i, (cIndex + 1) % 3, k)):
                    con1.append([i + 4 * ((cIndex + 2) % 3), k])
                if isGray(fetchChi(mitmSolution, i, (cIndex + 2) % 3, k)):
                    con0.append([i + 4 * ((cIndex + 1) % 3), k])
    print(len(con0),len(con1))
    lane0 = []
    zero_z = []
    for item in con0:
        if item[0] not in lane0:
            lane0.append(item[0])
            zero_z.append([item[1]])
        else:
            ind = lane0.index(item[0])
            zero_z[ind].append(item[1])
    lane1 = []
    one_z = []
    for item in con1:
        if item[0] not in lane1:
            lane1.append(item[0])
            one_z.append([item[1]])
        else:
            ind = lane1.index(item[0])
            one_z[ind].append(item[1])
    return lane0, zero_z, lane1, one_z


def only_one_color(in1, in2, in3):
    color1 = 0 if (in1[0] + in1[2] == 0) else 1
    color2 = 0 if (in2[0] + in2[2] == 0) else 1
    color3 = 0 if (in3[0] + in3[2] == 0) else 1
    # one_color = 1 if (color1 + color2 + color3 == 1) else 0
    return color1 + color2 + color3 == 1


def color_index(in1, in2, in3):
    color1 = 0 if (in1[0] + in1[2] == 0) else 1
    color2 = 0 if (in2[0] + in2[2] == 0) else 1
    color3 = 0 if (in3[0] + in3[2] == 0) else 1
    if color1 == 1:
        return 0
    if color2 == 1:
        return 1
    if color3 == 1:
        return 2
    return -1


def fetchChi(mitmSolution, i, j, k):
    if j == 0:
        return mitmSolution.DA[1][i][j][k]
    if j == 1:
        return mitmSolution.DA[1][i][j][(k + 1) % len(mitmSolution.DA[0][0][0])]
    if j == 2:
        return mitmSolution.DA[1][(i + 2) % 4][j][(k + 8) % len(mitmSolution.DA[0][0][0])]


def isGray(in1):
    return (in1[0] == 0) & (in1[1] == 1) & (in1[2] == 0)
