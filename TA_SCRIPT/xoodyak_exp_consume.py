from sage.all import *
from sage.rings.polynomial.pbori import *
import copy
import pdb
import xdrlib, sys
import random
import time

from xoodyak_solution import File2MitmSolution, FetchInitialState, FetchConsumes, FetchConditions


def theta(state):
    slice_num = len(state[0])
    tempstate = [[] for i in range(12)]
    for i in range(12):
        for j in range(slice_num):
            tempstate[i].append(state[i][j])
            for k in range(3):
                tempstate[i][j] += state[(i % 4 + 4 - 1) % 4 + 4 * k][(j - 5 + 32) % slice_num] + \
                                   state[(i % 4 + 4 - 1) % 4 + 4 * k][(j - 14 + 32) % slice_num]

    for i in range(12):
        for j in range(slice_num):
            state[i][j] = tempstate[i][j]


def theta_threestep_D2A(state, tempstate1):
    slice_num = len(state[0])
    tempstate = [[] for i in range(12)]
    for i in range(12):
        for j in range(slice_num):
            tempstate[i].append(state[i][j])
            tempstate[i][j] += tempstate1[i % 4][j]

    for i in range(12):
        for j in range(slice_num):
            state[i][j] = tempstate[i][j]


def rho_west(state):
    slice_num = len(state[0])
    tempstate = [[] for i in range(12)]
    for i in range(4, 8):
        for j in range(slice_num):
            tempstate[i].append(state[(i - 1) % 4 + 4][j])
    for i in range(8, 12):
        for j in range(slice_num):
            tempstate[i].append(state[i][(j - 11 + 32) % slice_num])

    for i in range(4, 12):
        for j in range(slice_num):
            state[i][j] = tempstate[i][j]


def chi(state):
    slice_num = len(state[0])
    tempstate = [[] for i in range(12)]
    for i in range(4):
        for j in range(3):
            for k in range(slice_num):
                tempstate[4 * j + i].append(
                    state[4 * j + i][k] + (state[(4 * (j + 1) + i) % 12][k] + 1) * state[(4 * (j + 2) + i) % 12][k])

    for i in range(12):
        for j in range(slice_num):
            state[i][j] = tempstate[i][j]


def rho_east(state):
    slice_num = len(state[0])
    tempstate = [[] for i in range(12)]
    for i in range(4, 8):
        for j in range(slice_num):
            tempstate[i].append(state[i][(j - 1 + 32) % slice_num])
    for i in range(8, 12):
        for j in range(slice_num):
            tempstate[i].append(state[(i - 2) % 4 + 8][(j - 8 + 32) % slice_num])

    for i in range(4, 12):
        for j in range(slice_num):
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


# vr red cells
# vb blue cells
# vg gray cells
# cu0 consume of red or blue cells in column at round 0
# du0 consume of red or blue cells in two C at round 0
# cu0theta consume of red or blue cells in theta operation at round 0
# cu1 consume of red or blue cells in column at round 1
# du1 consume of red or blue cells in two C at round 1
# cu1theta consume of red or blue cells in theta operation at round 1
# cu2 consume of red or blue cells in column at round 2
# du2 consume of red or blue cells in two C at round 2
# cu2theta consume of red or blue cells in theta operation at round 2
# const0/1/2 round constant in round 0/1/2


def xoodyak_round(FILE_NAME="outputimm/xoodyak/result0.json", WITH_SYMMETRY=False, verbose=False):
    SLICES = 16 if WITH_SYMMETRY else 32
    red_relations = []
    keys = []
    Xoodyak = declare_ring(
        [Block('vr', SLICES * 12), Block('vb', SLICES * 12), Block('vg', SLICES * 12),
         Block('cu0', SLICES * 4), Block('du0', SLICES * 4), Block('cu0theta', SLICES * 12),
         Block('cu1', SLICES * 4), Block('du1', SLICES * 4), Block('cu1theta', SLICES * 12),
         Block('cu2', SLICES * 4), Block('du2', SLICES * 4), Block('cu2theta', SLICES * 12),
         Block('const0', 32), Block('const1', 32), Block('const2', 32)],
        globals())

    tsol = File2MitmSolution(FILE_NAME)
    red_z, blue_z, gray_z = FetchInitialState(tsol)
    print(red_z)
    print(blue_z)
    print(gray_z)

    state = [[] for i in range(12)]

    for i in range(12):
        for k in range(SLICES):
            state[i].append(Xoodyak(0))

    for k in range(SLICES):
        if k in gray_z[0]:
            state[8][k] = vg(4 * k + 8 * SLICES)
        elif k in blue_z[0]:
            state[8][k] = vb(4 * k + 8 * SLICES)
        else:
            state[8][k] = vr(4 * k + 8 * SLICES)

    for k in range(SLICES):
        if k in gray_z[1]:
            state[9][k] = vg(4 * k + 1 + 8 * SLICES)
        elif k in blue_z[1]:
            state[9][k] = vb(4 * k + 1 + 8 * SLICES)
        else:
            state[9][k] = vr(4 * k + 1 + 8 * SLICES)

    for k in range(SLICES):
        if k in gray_z[2]:
            state[10][k] = vg(4 * k + 2 + 8 * SLICES)
        elif k in blue_z[2]:
            state[10][k] = vb(4 * k + 2 + 8 * SLICES)
        else:
            state[10][k] = vr(4 * k + 2 + 8 * SLICES)

    for k in range(SLICES):
        if k in gray_z[3]:
            state[11][k] = vg(4 * k + 3 + 8 * SLICES)
        elif k in blue_z[3]:
            state[11][k] = vb(4 * k + 3 + 8 * SLICES)
        else:
            state[11][k] = vr(4 * k + 3 + 8 * SLICES)

    for i in range(8):
        for k in range(SLICES):
            state[i][k] = vg(4 * (int(i / 4)) * SLICES + 4 * k + (i % 4))

    f0 = open('state/XOODYAK/xoodyak_state.txt', 'w')
    for i0 in range(12):
        for j0 in range(SLICES):
            f0.write(str(state[i0][j0]) + '\n')
    f0.close()

    consume_c, consume_d, consume_t = FetchConsumes(tsol)

    print("round 0")
    # round 0
    # C
    CState = [[] for i in range(4)]
    for i in range(4):
        for j in range(SLICES):
            CState[i].append(0)
            for k in range(3):
                #if i == 1 and j == 11:
                #    print(i + 4 * k, j)
                CState[i][j] += state[i + 4 * k][j]

    # C consume
    # consume1lane = [0, 1, 2, 3]
    consume1z = consume_c[0]
    f1 = open('state/XOODYAK/xoodyak_C_consume_0.txt', 'w')
    for i0 in range(4):
        for j0 in range(SLICES):
            if j0 in consume1z[i0]:
                f1.write(str(CState[i0][j0]))
                red_relations.append(FetchRelations(str(CState[i0][j0])))
                keys.append(f"C^{{({0})}}_{{{i0},{j0}}}")
                # CState[i0][j0] = cu0(i0 + 4 * j0)
                f1.write(' = ')
                f1.write(str(cu0(i0 + 4 * j0)))
                f1.write('\n')
    f1.close()

    f2 = open('state/XOODYAK/xoodyak_D_consume_0.txt', 'w')
    # D
    DState = [[] for i in range(4)]
    for i in range(4):
        for j in range(SLICES):
            DState[i].append(0)
            DState[i][j] += CState[(i - 1 + 4) % 4][(j - 5 + 32) % SLICES] + CState[(i - 1 + 4) % 4][
                (j - 14 + 32) % SLICES]

    ftest = open('state/XOODYAK/xoodyak_test.txt', 'w')
    ftest.write(str(CState[1][11]))
    ftest.write(" = CState[1][11]\n")
    ftest.write(str(CState[1][20]))
    ftest.write(" = CState[1][20]\n")
    ftest.write(str(DState[2][25]))
    ftest.write(" = DState[2][25]\n")
    ftest.close()

    # D consume
    consume1lane_D = [0, 1, 2, 3]
    consume1z_D = consume_d[0]

    for i0 in range(len(consume1lane_D)):
        for j0 in range(SLICES):
            if j0 in consume1z_D[i0]:
                f2.write(str(DState[consume1lane_D[i0]][j0]))
                red_relations.append(FetchRelations(str(DState[consume1lane_D[i0]][j0])))
                keys.append(f"D^{{({0})}}_{{{i0},{j0}}}")
                # DState[consume1lane_D[i0]][j0] = du0(consume1lane_D[i0] + 4 * j0)
                f2.write(' = ')
                f2.write(str(du0(consume1lane_D[i0] + 4 * j0)))
                f2.write('\n')
    f2.close()

    theta_threestep_D2A(state, DState)

    f3 = open('state/XOODYAK/xoodyak_theta1_consume_0.txt', 'w')
    # consume
    consume1lane_theta = consume_t[0][0]
    consume1z_theta = consume_t[0][1]
    for i0 in range(len(consume1lane_theta)):
        tmplane = consume1lane_theta[i0]
        f3.write(str(state[consume1lane_theta[i0]][consume1z_theta[i0]]))
        red_relations.append(FetchRelations(str(state[consume1lane_theta[i0]][consume1z_theta[i0]])))
        keys.append(f"θ^{{({0})}}_{{{consume1lane_theta[i0] % 4},{int(consume1lane_theta[i0] / 4)},{consume1z_theta[i0]}}}")
        # state[consume1lane_theta[i0]][consume1z_theta[i0]] = cu0theta(
        #     SLICES * 4 * int(tmplane / 4) + 4 * consume1z_theta[i0] + (tmplane % 4))
        f3.write(' = ')
        f3.write(str(cu0theta(SLICES * 4 * int(tmplane / 4) + 4 * consume1z_theta[i0] + (tmplane % 4))))
        f3.write('\n')
    f3.close()

    rho_west(state)


    f = open('state/XOODYAK/xoodyak_cond.txt', 'w')

    # 0-1 condition in theta
    lane0, zero_z, lane1, one_z = FetchConditions(tsol)

    for i0 in range(len(lane0)):
        for j0 in range(SLICES):
            if j0 in zero_z[i0]:
                f.write(str(state[lane0[i0]][j0]))
                f.write(' = 0\n')
                state[lane0[i0]][j0] = 0

    for i1 in range(len(lane1)):
        for j1 in range(SLICES):
            if j1 in one_z[i1]:
                f.write(str(state[lane1[i1]][j1]))
                f.write(' = 1\n')
                state[lane1[i1]][j1] = 1
    f.close()

    chi(state)
    rho_east(state)

    #print(str(state[1][15]))
    #print(str(state[6][9]))

    print("round 1")
    # round 1
    # C
    CState = [[] for i in range(4)]
    for i in range(4):
        for j in range(SLICES):
            CState[i].append(0)
            for k in range(3):
                CState[i][j] += state[i + 4 * k][j]

    # C consume
    # consume1lane = [0, 1, 2, 3]
    consume1z = consume_c[1]
    f1 = open('state/XOODYAK/xoodyak_C_consume_1.txt', 'w')
    for i0 in range(4):
        for j0 in range(SLICES):
            if j0 in consume1z[i0]:
                f1.write(str(CState[i0][j0]))
                red_relations.append(FetchRelations(str(CState[i0][j0])))
                keys.append(f"C^{{({1})}}_{{{i0},{j0}}}")
                # CState[i0][j0] = cu1(i0 + 4 * j0)
                f1.write(' = ')
                f1.write(str(cu1(i0 + 4 * j0)))
                f1.write('\n')
    f1.close()

    f2 = open('state/XOODYAK/xoodyak_D_consume_1.txt', 'w')
    # D
    DState = [[] for i in range(4)]
    for i in range(4):
        for j in range(SLICES):
            DState[i].append(0)
            DState[i][j] += CState[(i - 1 + 4) % 4][(j - 5 + 32) % SLICES] + CState[(i - 1 + 4) % 4][
                (j - 14 + 32) % SLICES]

    # D consume
    consume1lane_D = [0, 1, 2, 3]
    consume1z_D = consume_d[1]

    for i0 in range(len(consume1lane_D)):
        for j0 in range(SLICES):
            if j0 in consume1z_D[i0]:
                f2.write(str(DState[consume1lane_D[i0]][j0]))
                red_relations.append(FetchRelations(str(DState[consume1lane_D[i0]][j0])))
                keys.append(f"D^{{({1})}}_{{{i0},{j0}}}")
                # DState[consume1lane_D[i0]][j0] = du1(consume1lane_D[i0] + 4 * j0)
                f2.write(' = ')
                f2.write(str(du1(consume1lane_D[i0] + 4 * j0)))
                f2.write('\n')
    f2.close()

    theta_threestep_D2A(state, DState)

    f3 = open('state/XOODYAK/xoodyak_theta1_consume_1.txt', 'w')
    # consume
    consume1lane_theta = consume_t[1][0]
    consume1z_theta = consume_t[1][1]
    for i0 in range(len(consume1lane_theta)):
        tmplane = consume1lane_theta[i0]
        f3.write(str(state[consume1lane_theta[i0]][consume1z_theta[i0]]))
        red_relations.append(FetchRelations(str(state[consume1lane_theta[i0]][consume1z_theta[i0]])))
        keys.append(f"θ^{{({1})}}_{{{consume1lane_theta[i0] % 4},{int(consume1lane_theta[i0] / 4)},{consume1z_theta[i0]}}}")
        # state[consume1lane_theta[i0]][consume1z_theta[i0]] = cu1theta(
        #     SLICES * 4 * int(tmplane / 4) + 4 * consume1z_theta[i0] + (tmplane % 4))
        f3.write(' = ')
        f3.write(str(cu1theta(SLICES * 4 * int(tmplane / 4) + 4 * consume1z_theta[i0] + (tmplane % 4))))
        f3.write('\n')
    f3.close()

    rho_west(state)
    chi(state)
    rho_east(state)

    print("round 2")
    # round 2
    # C
    CState = [[] for i in range(4)]
    for i in range(4):
        for j in range(SLICES):
            CState[i].append(0)
            for k in range(3):
                CState[i][j] += state[i + 4 * k][j]

    # C consume
    # consume1lane = [0, 1, 2, 3]
    consume1z = consume_c[2]
    f1 = open('state/XOODYAK/xoodyak_C_consume_2.txt', 'w')
    for i0 in range(4):
        for j0 in range(SLICES):
            if j0 in consume1z[i0]:
                f1.write(str(CState[i0][j0]))
                red_relations.append(FetchRelations(str(CState[i0][j0])))
                keys.append(f"C^{{({2})}}_{{{i0},{j0}}}")
                # CState[i0][j0] = cu2(i0 + 4 * j0)
                f1.write(' = ')
                f1.write(str(cu2(i0 + 4 * j0)))
                f1.write('\n')
    f1.close()

    f2 = open('state/XOODYAK/xoodyak_D_consume_2.txt', 'w')
    # D
    DState = [[] for i in range(4)]
    for i in range(4):
        for j in range(SLICES):
            DState[i].append(0)
            DState[i][j] += CState[(i - 1 + 4) % 4][(j - 5 + 32) % SLICES] + CState[(i - 1 + 4) % 4][
                (j - 14 + 32) % SLICES]

    # D consume
    consume1lane_D = [0, 1, 2, 3]
    consume1z_D = consume_d[2]

    for i0 in range(len(consume1lane_D)):
        for j0 in range(SLICES):
            if j0 in consume1z_D[i0]:
                f2.write(str(DState[consume1lane_D[i0]][j0]))
                red_relations.append(FetchRelations(str(DState[consume1lane_D[i0]][j0])))
                keys.append(f"D^{{({2})}}_{{{i0},{j0}}}")
                # DState[consume1lane_D[i0]][j0] = du2(consume1lane_D[i0] + 4 * j0)
                f2.write(' = ')
                f2.write(str(du2(consume1lane_D[i0] + 4 * j0)))
                f2.write('\n')
    f2.close()

    theta_threestep_D2A(state, DState)

    f3 = open('state/XOODYAK/xoodyak_theta1_consume_2.txt', 'w')
    # consume
    consume1lane_theta = consume_t[2][0]
    consume1z_theta = consume_t[2][1]
    for i0 in range(len(consume1lane_theta)):
        tmplane = consume1lane_theta[i0]
        f3.write(str(state[consume1lane_theta[i0]][consume1z_theta[i0]]))
        red_relations.append(FetchRelations(str(state[consume1lane_theta[i0]][consume1z_theta[i0]])))
        keys.append(f"θ^{{({2})}}_{{{consume1lane_theta[i0] % 4},{int(consume1lane_theta[i0] / 4)},{consume1z_theta[i0]}}}")
        # state[consume1lane_theta[i0]][consume1z_theta[i0]] = cu2theta(
        #     SLICES * 4 * int(tmplane / 4) + 4 * consume1z_theta[i0] + (tmplane % 4))
        f3.write(' = ')
        f3.write(str(cu2theta(SLICES * 4 * int(tmplane / 4) + 4 * consume1z_theta[i0] + (tmplane % 4))))
        f3.write('\n')
    f3.close()

    return red_relations, keys