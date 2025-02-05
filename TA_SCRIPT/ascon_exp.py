from sage.all import *
from sage.rings.polynomial.pbori import *


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
        tempstate[i][4] = state[i][4] * state[i][1] + state[i][4] + state[i][3] + state[i][1] * state[i][0] + \
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

    indexm = [2, 3, 6, 12, 16, 17, 26, 34, 35, 38, 44, 48, 49, 58]

    indexb = [1, 4, 11, 13, 14, 17, 23, 33, 36, 43, 45, 46, 49, 55]

    indexr = [0, 5, 6, 7, 15, 18, 22, 25, 26, 27, 28, 29, 32, 37, 38, 39, 47, 50, 54, 57, 58, 59, 60, 61]

    cond1x = [0, 1, 4, 11, 13, 18, 22, 23, 25, 32, 33, 36, 43, 45, 50, 54, 55, 57]
    condplusx = [0, 5, 6, 7, 11, 14, 15, 17, 22, 25, 26, 27, 32, 37, 38, 39, 43, 46, 47, 49, 54, 57, 58, 59]

    # vr red bits
    # vb blue bits
    # vg gray bits
    ASCON = declare_ring(
        [Block('vr', BITS), Block('vb', BITS), Block('vg', BITS), Block('cur0', BITS), Block('cub0', BITS),
         Block('cur1', BITS), Block('cub1', BITS)], globals())

    state = [[] for i in range(SLICES)]

    for i in range(SLICES):
        for j in range(5):
            state[i].append(ASCON(0))

    for i in range(len(indexr)):
        state[indexr[i]][0] = vr(indexr[i])

    for i in range(len(cond1x)):
        state[cond1x[i]][1] = 1
    for i in range(len(condplusx)):
        state[condplusx[i]][3] = 1

    f0 = open('state/ASCON/ascon_state.txt', 'w')
    for i0 in range(SLICES):
        f0.write(' '.join([str(state[i0][j0]) for j0 in range(5)]) + '\n')
    f0.close()


    # round 0
    sbox(state)
    print_state_file(state, 'S', 'state/ASCON/S0.txt')
    linear_layer(state)
    sbox(state)
    linear_layer(state)

    # consume in linear layer
    f1 = open('state/ASCON/ascon_consume.txt', 'w')
    consume_red = [2,12,16,34,44,48]
    for j0 in consume_red:
        f1.write(str(state[j0][1]))
        f1.write(' = ')
        f1.write(f'cu{j0}')
        f1.write('\n')
    f1.close()


if __name__ == '__main__':

    verbose = True
    file_name = f"outputimm/ascon/ascon_3r_m18.json"
    with_symmetry = False

    red_relations = ASCON_round(FILE_NAME=file_name, WITH_SYMMETRY=with_symmetry, verbose=verbose)

