from sage.all import *
from sage.rings.polynomial.pbori import *
from ascon_solution import *


def sbox(state):
    tempstate = [[[] for j in range(5)] for i in range(len(state))]
    for i in range(len(state)):
        tempstate[i][0] = state[i][4] * state[i][1] + state[i][3] + state[i][2] * state[i][1] + state[i][2] + \
                          state[i][1] * state[i][0] + state[i][1] + state[i][0]
        tempstate[i][1] = state[i][4] + state[i][3] * state[i][2] + state[i][3] * state[i][1] + state[i][3] + \
                          state[i][2] * state[i][1] + state[i][2] + state[i][1] + state[i][0]
        tempstate[i][2] = state[i][4] * state[i][3] + state[i][4] + state[i][2] + state[i][1] + 1
        tempstate[i][3] = state[i][4] * state[i][0] + state[i][4] + state[i][3] * state[i][0] + state[i][3] + \
                          state[i][2] + state[i][1] + state[i][0]
        tempstate[i][4] = (state[i][4] * state[i][1]) + state[i][4] + state[i][3] + state[i][1] * state[i][0] + \
                          state[i][1]
    for i in range(len(state)):
        for j in range(5):
            state[i][j] = tempstate[i][j]


def linear_layer(state):
    tempstate = [[] for i in range(len(state))]
    for i in range(len(state)):
        for j in range(5):
            tempstate[i].append(state[i][j])
        tempstate[i][0] += state[(i - 19 + 64) % len(state)][0] + state[(i - 28 + 64) % len(state)][0]
        tempstate[i][1] += state[(i - 61 + 64) % len(state)][1] + state[(i - 39 + 64) % len(state)][1]
        tempstate[i][2] += state[(i - 1 + 64) % len(state)][2] + state[(i - 6 + 64) % len(state)][2]
        tempstate[i][3] += state[(i - 10 + 64) % len(state)][3] + state[(i - 17 + 64) % len(state)][3]
        tempstate[i][4] += state[(i - 7 + 64) % len(state)][4] + state[(i - 41 + 64) % len(state)][4]
    for i in range(len(state)):
        for j in range(5):
            state[i][j] = tempstate[i][j]


def FetchRelations(s):
    res = []
    temp = s.strip()
    temp = temp.replace(')', '')
    temp = temp.replace('*', ' ')
    temp = temp.replace(' + ', ' ')
    temp = temp.replace('(', ' ')
    temp = temp.split()
    # print(temp)
    for i in range(len(temp)):
        if temp[i] == 'vr':
            if int(temp[i + 1]) not in res:
                res.append(int(temp[i + 1]))
    return res


def print_state_file(state, statename, filename):
    f = open(filename, 'w')
    for i in range(len(state)):
        for j in range(len(state[i])):
            f.write(str(state[i][j]))
            f.write(f" = {statename}({i + len(state) * j})")
            f.write('\n')
    f.close()


# blue_z, gray_z, zero_z, one_z, consume_red, consume_blue,
def ASCON_round(FILE_NAME="outputimm/result1.json", WITH_SYMMETRY=False, verbose=False):
    SLICES = 32 if WITH_SYMMETRY else 64
    BITS = 160 if WITH_SYMMETRY else 320
    # vr red bits
    # vb blue bits
    # vg gray bits
    ASCON = declare_ring(
        [Block('vr', BITS), Block('vb', BITS), Block('vg', BITS), Block('cur0', BITS), Block('cub0', BITS),
         Block('cur1', BITS), Block('cub1', BITS)], globals())

    tsol = File2MitmSolution(FILE_NAME)

    red_z, blue_z, gray_z = FetchInitialState(tsol)

    state = [[] for i in range(SLICES)]

    for i in range(SLICES):
        for j in range(5):
            state[i].append(ASCON(0))
    for j in range(5):
        for i in range(SLICES):
            if i in gray_z[j]:
                state[i][j] = vg(SLICES * j + i)
            elif i in blue_z[j]:
                state[i][j] = vb(SLICES * j + i)
            else:
                state[i][j] = vr(SLICES * j + i)

    if verbose:
        f0 = open('state/ASCON/ascon_state.txt', 'w')
        for i0 in range(SLICES):
            f0.write(' '.join([str(state[i0][j0]) for j0 in range(5)]) + '\n')
        f0.close()

    if verbose:
        f = open('state/ASCON/ascon_cond.txt', 'w')

    # 0-1 condition in first round
    # zero_z -- x1=0
    # one_z -- x1=1 or x3+x4=1

    zero_z, one_z = FetchConditions(tsol)
    # zero_z, one_z = [], [[], []]

    for i0 in range(SLICES):
        if i0 in zero_z:
            if verbose:
                f.write(str(state[i0][1]))
                f.write(' = 0\n')
            state[i0][1] = ASCON(0)

    for i1 in range(SLICES):
        if i1 in one_z[0]:
            if verbose:
                f.write(str(state[i1][1]))
                f.write(' = 1\n')
            state[i1][1] = ASCON(1)
        if i1 in one_z[1]:
            if verbose:
                f.write(str(state[i1][3]) + str(state[i1][4]))
                f.write(' = 1\n')
            state[i1][4] = state[i1][3] + 1
    if verbose:
        f.close()

    if verbose:
        f0 = open('state/ASCON/ascon_state_0.txt', 'w')
        for i0 in range(SLICES):
            f0.write(' '.join([str(state[i0][j0]) for j0 in range(5)]) + '\n')
        f0.close()

    # round 0
    sbox(state)
    if verbose:
        print_state_file(state, 'S', 'state/ASCON/S0.txt')
    linear_layer(state)

    # consume in linear layer
    consume_z = FetchConsumes_pink(tsol)

    consume_red_z0 = consume_z[0][0]
    consume_blue_z0 = consume_z[0][1]

    # print(consume_red_z0)
    # print(consume_blue_z0)

    red_relations_0 = []
    red_relations_1 = []
    red_relations = []
    keys = []

    if verbose:
        f1 = open('state/ASCON/ascon_consume_0.txt', 'w')
    for j0 in range(len(consume_red_z0)):
        for i0 in range(SLICES):
            if i0 in consume_red_z0[j0]:
                if verbose:
                    f1.write(str(state[i0][j0]))
                red_relations_0.append(FetchRelations(str(state[i0][j0])))
                red_relations.append(FetchRelations(str(state[i0][j0])))
                keys.append(f"A^{{({1})}}_{{\\{{{i0},{j0}\\}}}}")
                # state[i0][j0] = cur0(SLICES * j0 + i0)
                if verbose:
                    f1.write(' = ')
                    f1.write(str(cur0(SLICES * j0 + i0)))
                    f1.write('\n')
            if i0 in consume_blue_z0[j0]:
                if verbose:
                    f1.write(str(state[i0][j0]))
                # state[i0][j0] = cub0(SLICES * j0 + i0)
                if verbose:
                    f1.write(' = ')
                    f1.write(str(cub0(SLICES * j0 + i0)))
                    f1.write('\n')
    if verbose:
        f1.close()

    if verbose:
        f1 = open('state/ASCON/ascon_relations_0.txt', 'w')
        f1.write("[\n")
        for item in red_relations_0:
            f1.write(str(item) + ",\n")
        f1.write("]\n")
        f1.close()

    # round 1
    sbox(state)
    linear_layer(state)

    # consume in linear layer
    consume_red_z1 = consume_z[1][0]

    consume_blue_z1 = consume_z[1][1]
    if verbose:
        f1 = open('state/ASCON/ascon_consume_1.txt', 'w')
    for j1 in range(len(consume_red_z1)):
        for i1 in range(SLICES):
            if i1 in consume_red_z1[j1]:
                if verbose:
                    f1.write(str(state[i1][j1]))
                red_relations_1.append(FetchRelations(str(state[i1][j1])))
                red_relations.append(FetchRelations(str(state[i1][j1])))
                keys.append(f"A^{{({2})}}_{{\\{{{i1},{j1}\\}}}}")
                # state[i1][j1] = cur1(SLICES * j1 + i1)
                if verbose:
                    f1.write(' = ')
                    f1.write(str(cur1(SLICES * j1 + i1)))
                    f1.write('\n')
            if i1 in consume_blue_z1[j1]:
                if verbose:
                    f1.write(str(state[i1][j1]))
                #  state[i1][j1] = cub1(SLICES * j1 + i1)
                if verbose:
                    f1.write(' = ')
                    f1.write(str(cub1(SLICES * j1 + i1)))
                    f1.write('\n')
    if verbose:
        f1.close()

    if verbose:
        f1 = open('state/ASCON/ascon_relations_1.txt', 'w')
        f1.write("[\n")
        for item in red_relations_1:
            f1.write(str(item) + ",\n")
        f1.write("]\n")
        f1.close()


    # round 2
    sbox(state)
    linear_layer(state)

    # consume in linear layer
    consume_red_z2 = consume_z[2][0]

    consume_blue_z2 = consume_z[2][1]
    if verbose:
        f1 = open('state/ASCON/ascon_consume_2.txt', 'w')
    for j1 in range(len(consume_red_z2)):
        for i1 in range(SLICES):
            if i1 in consume_red_z2[j1]:
                if verbose:
                    f1.write(str(state[i1][j1]))
                red_relations_1.append(FetchRelations(str(state[i1][j1])))
                red_relations.append(FetchRelations(str(state[i1][j1])))
                keys.append(f"A^{{({3})}}_{{\\{{{i0},{j0}\\}}}}")
                # state[i1][j1] = cur1(SLICES * j1 + i1)
                if verbose:
                    f1.write(' = ')
                    f1.write(str(cur1(SLICES * j1 + i1)))
                    f1.write('\n')
            if i1 in consume_blue_z1[j1]:
                if verbose:
                    f1.write(str(state[i1][j1]))
                #  state[i1][j1] = cub1(SLICES * j1 + i1)
                if verbose:
                    f1.write(' = ')
                    f1.write(str(cub1(SLICES * j1 + i1)))
                    f1.write('\n')
    if verbose:
        f1.close()

    if verbose:
        f1 = open('state/ASCON/ascon_relations_2.txt', 'w')
        f1.write("[\n")
        for item in red_relations_1:
            f1.write(str(item) + ",\n")
        f1.write("]\n")
        f1.close()

    return red_relations, keys

