import json

rho = [[0, 36, 3, 41, 18], [1, 44, 10, 45, 2], [62, 6, 43, 15, 61], [28, 55, 25, 21, 56], [27, 20, 39, 8, 14]]

cond = [[[-1, -1, -1, -1], [-1, -1, -1, -1], [-1, -1, -1, -1], [-1, -1, -1, -1], [-1, -1, -1, -1], [-1, -1, -1, -1],
         [-1, -1, -1, -1], [-1, -1, -1, -1]],
        [[-1, 1, -1, 0], [-1, -1, -1, -1], [-1, 1, -1, -1], [-1, -1, -1, -1], [-1, -1, -1, 0], [-1, -1, -1, -1],
         [-1, -1, -1, -1], [-1, -1, -1, -1]],
        [[-1, -1, -1, -1], [-1, -1, -1, -1], [1, -1, 0, -1], [-1, -1, 0, -1], [-1, -1, -1, -1], [-1, -1, -1, -1],
         [1, -1, -1, -1], [-1, -1, -1, -1]],
        [[-1, -1, -1, -1], [-1, -1, -1, -1], [-1, -1, -1, -1], [-1, -1, -1, -1], [-1, -1, -1, -1], [-1, -1, -1, -1],
         [1, -1, -1, -1], [-1, -1, -1, -1]],
        [[-1, -1, -1, -1], [-1, -1, -1, -1], [-1, -1, -1, -1], [-1, -1, -1, -1], [-1, 0, -1, -1], [-1, -1, -1, -1],
         [-1, -1, -1, -1], [-1, -1, -1, -1]],
        [[-1, -1, -1, -1], [-1, -1, -1, -1], [-1, -1, -1, -1], [-1, -1, -1, -1], [-1, 0, -1, 0], [-1, -1, -1, 0],
         [-1, 0, -1, -1], [-1, -1, -1, -1]],
        [[-1, -1, -1, -1], [-1, -1, -1, -1], [-1, -1, -1, -1], [-1, -1, -1, -1], [-1, -1, -1, -1], [-1, -1, -1, -1],
         [-1, -1, -1, -1], [-1, -1, -1, -1]],
        [[-1, -1, -1, -1], [-1, -1, -1, -1], [-1, -1, -1, -1], [-1, -1, -1, -1], [-1, -1, -1, -1], [-1, -1, -1, -1],
         [-1, -1, -1, -1], [-1, -1, -1, -1]]]


class MitmSolution:
    def __int__(self):
        self.Rounds = 0
        self.objective = 0
        self.Pi_init = None
        self.Cond = None
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
    red_z = [[], [], [], [], []]
    blue_z = [[], [], [], [], []]
    gray_z = [[], [], [], [], []]
    slice_num = len(mitmSolution.Pi_init[0][0])
    for k in range(slice_num):
        if (mitmSolution.Pi_init[0][0][k][0] == 1) & (mitmSolution.Pi_init[0][0][k][2] == 0):
            blue_z[0].append((k - rho[0][0]) % slice_num)
        if (mitmSolution.Pi_init[0][0][k][0] == 0) & (mitmSolution.Pi_init[0][0][k][2] == 1):
            red_z[0].append((k - rho[0][0]) % slice_num)
        if (mitmSolution.Pi_init[0][0][k][0] == 0) & (mitmSolution.Pi_init[0][0][k][2] == 0):
            gray_z[0].append((k - rho[0][0]) % slice_num)

        if (mitmSolution.Pi_init[0][2][k][0] == 1) & (mitmSolution.Pi_init[0][2][k][2] == 0):
            blue_z[1].append((k - rho[1][0]) % slice_num)
        if (mitmSolution.Pi_init[0][2][k][0] == 0) & (mitmSolution.Pi_init[0][2][k][2] == 1):
            red_z[1].append((k - rho[1][0]) % slice_num)
        if (mitmSolution.Pi_init[0][2][k][0] == 0) & (mitmSolution.Pi_init[0][2][k][2] == 0):
            gray_z[1].append((k - rho[1][0]) % slice_num)

        if (mitmSolution.Pi_init[0][4][k][0] == 1) & (mitmSolution.Pi_init[0][4][k][2] == 0):
            blue_z[2].append((k - rho[2][0]) % slice_num)
        if (mitmSolution.Pi_init[0][4][k][0] == 0) & (mitmSolution.Pi_init[0][4][k][2] == 1):
            red_z[2].append((k - rho[2][0]) % slice_num)
        if (mitmSolution.Pi_init[0][4][k][0] == 0) & (mitmSolution.Pi_init[0][4][k][2] == 0):
            gray_z[2].append((k - rho[2][0]) % slice_num)

        if (mitmSolution.Pi_init[0][1][k][0] == 1) & (mitmSolution.Pi_init[0][1][k][2] == 0):
            blue_z[3].append((k - rho[3][0]) % slice_num)
        if (mitmSolution.Pi_init[0][1][k][0] == 0) & (mitmSolution.Pi_init[0][1][k][2] == 1):
            red_z[3].append((k - rho[3][0]) % slice_num)
        if (mitmSolution.Pi_init[0][1][k][0] == 0) & (mitmSolution.Pi_init[0][1][k][2] == 0):
            gray_z[3].append((k - rho[3][0]) % slice_num)

        if (mitmSolution.Pi_init[0][3][k][0] == 1) & (mitmSolution.Pi_init[0][3][k][2] == 0):
            blue_z[4].append((k - rho[4][0]) % slice_num)
        if (mitmSolution.Pi_init[0][3][k][0] == 0) & (mitmSolution.Pi_init[0][3][k][2] == 1):
            red_z[4].append((k - rho[4][0]) % slice_num)
        if (mitmSolution.Pi_init[0][3][k][0] == 0) & (mitmSolution.Pi_init[0][3][k][2] == 0):
            gray_z[4].append((k - rho[4][0]) % slice_num)
    return red_z, blue_z, gray_z


def FetchConditions(mitmSolution):
    slice_num = len(mitmSolution.Pi_init[0][0])

    con0 = []
    con1 = []
    for k in range(slice_num):
        for i in range(5):
            tmpCond = getCond([mitmSolution.Pi_init[0][i][k][0], mitmSolution.Pi_init[0][i][k][1],
                               mitmSolution.Pi_init[0][i][k][2], mitmSolution.Pi_init[1][i][k][0],
                               mitmSolution.Pi_init[1][i][k][1], mitmSolution.Pi_init[1][i][k][2],
                               mitmSolution.Pi_init[2][i][k][0], mitmSolution.Pi_init[2][i][k][1],
                               mitmSolution.Pi_init[2][i][k][2],
                               mitmSolution.DA[0][0][i][k][0], mitmSolution.DA[0][0][i][k][1],
                               mitmSolution.DA[0][0][i][k][2], mitmSolution.DA[0][1][i][k][0],
                               mitmSolution.DA[0][1][i][k][1], mitmSolution.DA[0][1][i][k][2],
                               mitmSolution.DA[0][4][i][k][0], mitmSolution.DA[0][4][i][k][1],
                               mitmSolution.DA[0][4][i][k][2]])
            for j in range(4):
                if tmpCond[j] == 0:
                    con0.append([(j % 5 + 3 * i) % 5 + 5 * j, (k - rho[(j % 5 + 3 * i) % 5][j % 5] + 64) % slice_num])
                    # theta
                    #print(f"{(j % 5 + 3 * i) % 5},{j},{(k - rho[(j % 5 + 3 * i) % 5][j % 5] + 64) % slice_num}=0")
                    #print(f"[{((j % 5 + 3 * i) % 5 + 5 * j)},{(k - rho[(j % 5 + 3 * i) % 5][j % 5] + 64) % slice_num}],")

                if tmpCond[j] == 1:
                    con1.append([(j % 5 + 3 * i) % 5 + 5 * j, (k - rho[(j % 5 + 3 * i) % 5][j % 5] + 64) % slice_num])
                    # print(f"{(j % 5 + 3 * i) % 5},{j},{(k - rho[(j % 5 + 3 * i) % 5][j % 5] + 64) % slice_num}=1")
                    # print(f"[{((j % 5 + 3 * i) % 5 + 5 * j)},{(k - rho[(j % 5 + 3 * i) % 5][j % 5] + 64) % slice_num}],")
            if ((not isGray([mitmSolution.Pi_init[0][i][k][0], mitmSolution.Pi_init[0][i][k][1], mitmSolution.Pi_init[0][i][k][2]])) & isGray(
                    [mitmSolution.DA[0][3][i][k][0], mitmSolution.DA[0][3][i][k][1], mitmSolution.DA[0][3][i][k][2]])):
                con1.append([(4 % 5+3 * i) % 5+5 * 4, (k-rho[(4 % 5+3 * i) % 5][4 % 5]+64) % slice_num])
                # print(f"{(4 % 5+3 * i) % 5},{4},{(k-rho[(4 % 5+3 * i) % 5][4 % 5]+64) % slice_num}=1")
                # print(f"[{((4 % 5+3 * i) % 5+5 * 4)},{(k-rho[(4 % 5+3 * i) % 5][4 % 5]+64) % slice_num}],")
    # print(len(con0),len(con1))
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


def FetchConsumes(mitmSolution):
    # consume in A to C
    consume_c = [[[], [], [], [], []] for r in range(len(mitmSolution.DC1))]
    for r in range(len(mitmSolution.DC1)):
        for i in range(len(mitmSolution.DC1[r])):
            for k in range(len(mitmSolution.DC1[r][i])):
                if mitmSolution.DC1[r][i][k][1] == 1:
                    consume_c[r][i].append(k)
    # consume in C to D
    consume_d = [[[], [], [], [], []] for r in range(len(mitmSolution.DC12))]
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
                        consume_t[r][0].append(i+5*j)
                        consume_t[r][1].append(k)
    return consume_c, consume_d, consume_t



def getCond(in_vars):
    _in = 0
    _out = 0
    _in = getIndex(in_vars[0:9])
    out = getIndex(in_vars[9:18])
    return cond[_in][out]


def getIndex(in_vars):
    result = 0
    if not isGray(in_vars[0:3]):
        result = result + 4
    if not isGray(in_vars[3:6]):
        result = result + 2
    if not isGray(in_vars[6:9]):
        result = result + 1
    return result


def isGray(in_vars):
    return in_vars[0] == 0 & in_vars[1] == 1 & in_vars[2] == 0
