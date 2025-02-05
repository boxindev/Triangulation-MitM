import json


class MitmSolution:
    def __int__(self):
        self.Rounds = 0
        self.objective = 0
        self.Cond = None
        self.DA = None
        self.DB = None
        self.DC = None
        self.dom = None
        self.obj = None

    '''
    def assign(rounds, objective=0,cond=None, aState=None, sState=None, cancelled_bits=None, matching_bits=None,
               obj=None):
        s = MitmSolution()
        s.Rounds = rounds
        s.objective = objective
        s.Cond = cond
        s.DA = aState
        s.DB = sState
        s.DC = cancelled_bits
        s.dom = matching_bits
        s.obj = obj
        return s
    '''


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
    for i in range(len(mitmSolution.DA[0])):
        for j in range(5):
            if (mitmSolution.DA[0][i][j][0] == 1) & (mitmSolution.DA[0][i][j][2] == 0):
                blue_z[j].append(i)
            elif (mitmSolution.DA[0][i][j][0] == 0) & (mitmSolution.DA[0][i][j][2] == 1):
                red_z[j].append(i)
            else:
                gray_z[j].append(i)
    return red_z, blue_z, gray_z

'''
def FetchConditions(mitmSolution):
    # x1=0
    zero_z = []
    # x1=1 or x3+x4=1
    one_z = [
        [],
        []
    ]
    for i in range(len(mitmSolution.Cond)):
        item = mitmSolution.Cond[i]
        if (item[0] == 1) & (mitmSolution.DA[0][i][0] != mitmSolution.DB[0][i][4]):
            zero_z.append(i)
        if (item[0] == 1) & (mitmSolution.DA[0][i][0] != mitmSolution.DB[0][i][0]):
            one_z[0].append(i)
        if (item[1] == 1) & (mitmSolution.DA[0][i][0] != mitmSolution.DB[0][i][3]):
            one_z[1].append(i)
    return zero_z, one_z
'''

def FetchConditions(mitmSolution):
    # x1=0
    zero_z = []
    # x1=1 or x3+x4=1
    one_z = [
        [],
        []
    ]
    for i in range(len(mitmSolution.DA[0])):
        if (mitmSolution.DA[0][i][0] != mitmSolution.DB[0][i][4]):
            zero_z.append(i)
        if (mitmSolution.DA[0][i][0] != mitmSolution.DB[0][i][0]):
            one_z[0].append(i)
        if (mitmSolution.DA[0][i][0] != mitmSolution.DB[0][i][3]):
            one_z[1].append(i)
    return zero_z, one_z

def FetchConsumes(mitmSolution):
    # consume in linear layer
    consume_z = [[[[], [], [], [], []], [[], [], [], [], []]] for r in range(len(mitmSolution.DC))]
    for r in range(len(mitmSolution.DC)):
        for i in range(len(mitmSolution.DC[r])):
            for j in range(len(mitmSolution.DC[r][i])):
                if type(mitmSolution.DC[r][i][j]) == list:
                    if mitmSolution.DC[r][i][j][0] == 1:
                        consume_z[r][0][j].append(i)
                    if mitmSolution.DC[r][i][j][1] == 1:
                        consume_z[r][1][j].append(i)
                else:
                    if mitmSolution.DC[r][i][j] == 1:
                        consume_z[r][0][j].append(i)
    return consume_z


def FetchConsumes_pink(mitmSolution):
    # consume in linear layer
    consume_z = [[[[], [], [], [], []], [[], [], [], [], []]] for r in range(len(mitmSolution.DC))]
    for r in range(len(mitmSolution.DC)):
        for i in range(len(mitmSolution.DC[r])):
            for j in range(len(mitmSolution.DC[r][i])):
                if type(mitmSolution.DC[r][i][j]) == list:
                    if mitmSolution.DC[r][i][j][1] == 1:
                        consume_z[r][0][j].append(i)
                else:
                    if mitmSolution.DC[r][i][j] == 1:
                        consume_z[r][0][j].append(i)
    return consume_z