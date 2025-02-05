from sage.all import *
from sage.rings.polynomial.pbori import *
import copy
import pdb
import xdrlib, sys
import random
import time

from keccak512_solution import File2MitmSolution, FetchInitialState, FetchConditions, FetchConsumes

def theta(state):
    slice_num = len(state[0])
    tempstate = [[] for i in range(25)]
    for i in range(25):
        for j in range(slice_num):
            tempstate[i].append(state[i][j])
            for k in range(5):
                tempstate[i][j] += state[(i % 5 + 5 - 1) % 5 + 5 * k][j] + state[(i % 5 + 1 + 5) % 5 + 5 * k][
                    (j - 1 + 64) % slice_num]

    for i in range(25):
        for j in range(slice_num):
            state[i][j] = tempstate[i][j]


def theta_threestep(state, tempstate1):
    slice_num = len(state[0])
    tempstate = [[] for i in range(25)]
    for i in range(25):
        for j in range(slice_num):
            tempstate[i].append(state[i][j])
            tempstate[i][j] += tempstate1[(i % 5 + 5 - 1) % 5][j] + tempstate1[(i % 5 + 1 + 5) % 5][
                (j - 1 + 64) % slice_num]

    for i in range(25):
        for j in range(slice_num):
            state[i][j] = tempstate[i][j]


def theta_threestep_D2A(state, tempstate1):
    slice_num = len(state[0])
    tempstate = [[] for i in range(25)]
    for i in range(25):
        for j in range(slice_num):
            tempstate[i].append(state[i][j])
            tempstate[i][j] += tempstate1[i % 5][j]

    for i in range(25):
        for j in range(slice_num):
            state[i][j] = tempstate[i][j]


def rho(state):
    slice_num = len(state[0])
    rot = [0, 1, 62, 28, 27, 36, 44, 6, 55, 20, 3, 10, 43, 25, 39, 41, 45, 15, 21, 8, 18, 2, 61, 56, 14]
    tempstate = [[] for i in range(25)]
    for i in range(25):
        for j in range(slice_num):
            tempstate[i].append(state[i][(j - rot[i] + 64) % slice_num])

    for i in range(25):
        for j in range(slice_num):
            state[i][j] = tempstate[i][j]


def pi(state):
    slice_num = len(state[0])
    tempstate = [[] for i in range(25)]
    for i in range(25):
        y = floor(i / 5)
        x = i % 5
        x1 = y
        y1 = (2 * x + 3 * y) % 5
        temp = 5 * y1 + x1
        for j in range(slice_num):
            tempstate[temp].append(state[i][j])
    for i in range(25):
        for j in range(slice_num):
            state[i][j] = tempstate[i][j]


def chi(state):
    slice_num = len(state[0])
    tempstate = [[] for i in range(25)]
    for i in range(5):
        for j in range(5):
            for k in range(slice_num):
                tempstate[5 * i + j].append(
                    state[5 * i + j][k] + (state[5 * i + (j + 1) % 5][k] + 1) * state[5 * i + (j + 2) % 5][k])

    for i in range(25):
        for j in range(slice_num):
            state[i][j] = tempstate[i][j]


def iota(state, indexround, const0, const1, const2):
    slice_num = len(state[0])
    tempstate = []
    for k in range(slice_num):
        if indexround == 0:
            tempstate.append(state[0][k] + const0(k))
        if indexround == 1:
            tempstate.append(state[0][k] + const1(k))
        if indexround == 2:
            tempstate.append(state[0][k] + const2(k))
    for j in range(slice_num):
        state[0][j] = tempstate[j]


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
# cu1 consume of red or blue cells in column at round 1
# du1 consume of red or blue cells in two C at round 1
# cu1theta consume of red or blue cells in theta operation at round 1
# cu2 consume of red or blue cells in column at round 2
# du2 consume of red or blue cells in two C at round 2
# cu2theta consume of red or blue cells in theta operation at round 2
# const0/1/2 round constant in round 0/1/2

def KECCAK512_round(FILE_NAME="outputimm/keccak/result0.json", WITH_SYMMETRY=False, verbose=True):
    SLICES = 32 if WITH_SYMMETRY else 64
    red_relations = []
    keys = []
    Keccak = declare_ring(
        [Block('vr', SLICES * 5), Block('vb', SLICES * 5), Block('vg', SLICES * 25), Block('cu0', SLICES * 5),
         Block('cu1', SLICES * 5), Block('du1', SLICES * 5), Block('cu1theta', SLICES * 25),
         Block('cu2', SLICES * 5), Block('du2', SLICES * 5), Block('cu2theta', SLICES * 25),
         Block('const0', 64), Block('const1', 64), Block('const2', 64)],
        globals())

    tsol = File2MitmSolution(FILE_NAME)
    red_z, blue_z, gray_z = FetchInitialState(tsol)

    state = [[] for i in range(25)]

    for i in range(25):
        for k in range(SLICES):
            state[i].append(Keccak(0))

    for k in range(SLICES):
        if k in gray_z[0]:
            state[0][k] = vg(5 * k)
            state[5][k] = vg(5 * SLICES + 5 * k)
        elif k in blue_z[0]:
            state[0][k] = vb(5 * k)
            state[5][k] = vb(5 * k) + cu0(5 * k)
        else:
            state[0][k] = vr(5 * k)
            state[5][k] = vr(5 * k) + cu0(5 * k)

    for k in range(SLICES):
        if k in gray_z[1]:
            state[1][k] = vg(5 * k + 1)
            state[6][k] = vg(5 * SLICES + 5 * k + 1)
        elif k in blue_z[1]:
            state[1][k] = vb(5 * k + 1)
            state[6][k] = vb(5 * k + 1) + cu0(5 * k + 1)
        else:
            state[1][k] = vr(5 * k + 1)
            state[6][k] = vr(5 * k + 1) + cu0(5 * k + 1)

    for k in range(SLICES):
        if k in gray_z[2]:
            state[2][k] = vg(5 * k + 2)
            state[7][k] = vg(5 * SLICES + 5 * k + 2)
        elif k in blue_z[2]:
            state[2][k] = vb(5 * k + 2)
            state[7][k] = vb(5 * k + 2) + cu0(5 * k + 2)
        else:
            state[2][k] = vr(5 * k + 2)
            state[7][k] = vr(5 * k + 2) + cu0(5 * k + 2)

    for k in range(SLICES):
        if k in gray_z[3]:
            state[3][k] = vg(5 * k + 3)
            state[8][k] = vg(5 * SLICES + 5 * k + 3)
        elif k in blue_z[3]:
            state[3][k] = vb(5 * k + 3)
            state[8][k] = vb(5 * k + 3) + cu0(5 * k + 3)
        else:
            state[3][k] = vr(5 * k + 3)
            state[8][k] = vr(5 * k + 3) + cu0(5 * k + 3)

    for k in range(SLICES):
        if k in gray_z[4]:
            state[4][k] = vg(5 * k + 4)
            state[9][k] = vg(5 * SLICES + 5 * k + 4)
        elif k in blue_z[4]:
            state[4][k] = vb(5 * k + 4)
            state[9][k] = vb(5 * k + 4) + cu0(5 * k + 4)
        else:
            state[4][k] = vr(5 * k + 4)
            state[9][k] = vr(5 * k + 4) + cu0(5 * k + 4)
    
    for i in range(25):
        if i > 9:
            for k in range(SLICES):
                state[i][k] = vg(5 * (int(i / 5)) * SLICES + 5 * k + (i % 5))
    
    f0 = open('state/KECCAK512/keccak512state.txt', 'w')
    for i0 in range(25):
        for j0 in range(SLICES):
            f0.write(str(state[i0][j0]) + '\n')
    f0.close()
    
    # round 0
    theta(state)
    f = open('state/KECCAK512/keccak512cond.txt', 'w')

    # 0-1 condition in theta
    lane0, zero_z, lane1, one_z = FetchConditions(tsol)

    print("lane0: ", lane0)
    print("zero_z: ", zero_z)
    print("lane1: ", lane1)
    print("one_z: ", one_z)


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

    rho(state)
    pi(state)
    chi(state)
    iota(state, 0,const0,const1,const2)

    consume_c, consume_d, consume_t = FetchConsumes(tsol)

    # round 1
    # C
    CState = [[] for i in range(5)]
    for i in range(5):
        for j in range(SLICES):
            CState[i].append(0)
            for k in range(5):
                CState[i][j] += state[i + 5 * k][j]

    # C consume
    # consume1lane = [0, 1, 2, 3, 4]
    consume1z = consume_c[0]
    f1 = open('state/KECCAK512/keccak512_C_consume.txt', 'w')
    for i0 in range(5):
        for j0 in range(SLICES):
            if j0 in consume1z[i0]:
                f1.write(str(CState[i0][j0]))
                red_relations.append(FetchRelations(str(CState[i0][j0])))
                keys.append(f"C^{{({1})}}_{{{i0},{j0}}}")
                #if (i0 == 1) & (j0 == 11):
                #    print(FetchRelations(str(CState[i0][j0])))
                # CState[i0][j0] = cu1(i0 + 5 * j0)
                f1.write(' = ')
                f1.write(str(CState[i0][j0]))
                f1.write('\n')
    f1.close()

    f2 = open('state/KECCAK512/keccak512_D_consume.txt', 'w')
    # D
    DState = [[] for i in range(5)]
    for i in range(5):
        for j in range(SLICES):
            DState[i].append(0)
            DState[i][j] += CState[(i - 1 + 5) % 5][j] + CState[(i + 1) % 5][(j - 1 + 64) % SLICES]

    ftest = open('state/KECCAK512/keccak512_test.txt', 'w')
    ftest.write(str(CState[4][23]))
    ftest.write(" = CState[4][23]\n")
    ftest.write(str(CState[1][22]))
    ftest.write(" = CState[1][22]\n")
    ftest.write(str(DState[0][23]))
    ftest.write(" = DState[0][23]\n")
    # ftest.close()


    # D consume
    consume1lane_D = [0, 1, 2, 3, 4]
    consume1z_D = consume_d[0]

    for i0 in range(len(consume1lane_D)):
        for j0 in range(SLICES):
            if j0 in consume1z_D[i0]:
                f2.write(str(DState[consume1lane_D[i0]][j0]))
                red_relations.append(FetchRelations(str(DState[consume1lane_D[i0]][j0])))
                keys.append(f"D^{{({1})}}_{{{i0},{j0}}}")
                # DState[consume1lane_D[i0]][j0] = du1(consume1lane_D[i0] + 5 * j0)
                f2.write(' = ')
                f2.write(str(DState[consume1lane_D[i0]][j0]))
                f2.write('\n')
    f2.close()

    ftest.write(str(state[2][28]))
    ftest.write(" = state[2][28]\n")
    ftest.write(str(DState[2][28]))
    ftest.write(" = DState[2][28]\n")

    theta_threestep_D2A(state, DState)

    ftest.write(str(state[2][28]))
    ftest.write(" = state[2][28]\n")
    ftest.close()

    f3 = open('state/KECCAK512/keccak512_theta1_consume.txt', 'w')
    # consume
    consume1lane_theta = consume_t[0][0]
    consume1z_theta = consume_t[0][1]
    for i0 in range(len(consume1lane_theta)):
        tmplane = consume1lane_theta[i0]
        f3.write(str(state[consume1lane_theta[i0]][consume1z_theta[i0]]))
        red_relations.append(FetchRelations(str(state[consume1lane_theta[i0]][consume1z_theta[i0]])))
        keys.append(f"θ^{{({1})}}_{{{consume1lane_theta[i0]%5},{int(consume1lane_theta[i0]/5)},{consume1z_theta[i0]}}}")
        # state[consume1lane_theta[i0]][consume1z_theta[i0]] = cu1theta(
        #     SLICES * 5 * int(tmplane / 5) + 5 * consume1z_theta[i0] + (tmplane % 5))
        f3.write(' = ')
        f3.write(str(state[consume1lane_theta[i0]][consume1z_theta[i0]]))
        f3.write('\n')
    f3.close()



    rho(state)
    pi(state)
    chi(state)
    iota(state, 1, const0, const1, const2)

    # round 2
    # C
    CState = [[] for i in range(5)]
    for i in range(5):
        for j in range(SLICES):
            CState[i].append(0)
            for k in range(5):
                CState[i][j] += state[i + 5 * k][j]

    # C consume
    # consume1lane = [0, 1, 2, 3, 4]
    consume1z = consume_c[1]
    f1 = open('state/KECCAK512/keccak512_C_consume_1.txt', 'w')
    for i0 in range(5):
        for j0 in range(SLICES):
            if j0 in consume1z[i0]:
                f1.write(str(CState[i0][j0]))
                red_relations.append(FetchRelations(str(CState[i0][j0])))
                keys.append(f"C^{{({2})}}_{{{i0},{j0}}}")
                # if (i0 == 1) & (j0 == 11):
                #    print(FetchRelations(str(CState[i0][j0])))
                # CState[i0][j0] = cu2(i0 + 5 * j0)
                f1.write(' = ')
                f1.write(str(CState[i0][j0]))
                f1.write('\n')
    f1.close()

    f2 = open('state/KECCAK512/keccak512_D_consume_1.txt', 'w')
    # D
    DState = [[] for i in range(5)]
    for i in range(5):
        for j in range(SLICES):
            DState[i].append(0)
            DState[i][j] += CState[(i - 1 + 5) % 5][j] + CState[(i + 1) % 5][(j - 1 + 64) % SLICES]

    # D consume
    consume1lane_D = [0, 1, 2, 3, 4]
    consume1z_D = consume_d[1]

    for i0 in range(len(consume1lane_D)):
        for j0 in range(SLICES):
            if j0 in consume1z_D[i0]:
                f2.write(str(DState[consume1lane_D[i0]][j0]))
                red_relations.append(FetchRelations(str(DState[consume1lane_D[i0]][j0])))
                keys.append(f"D^{{({2})}}_{{{i0},{j0}}}")
                # DState[consume1lane_D[i0]][j0] = du2(consume1lane_D[i0] + 5 * j0)
                f2.write(' = ')
                f2.write(str(DState[consume1lane_D[i0]][j0]))
                f2.write('\n')
    f2.close()

    theta_threestep_D2A(state, DState)

    f3 = open('state/KECCAK512/keccak512_theta1_consume_1.txt', 'w')
    # consume
    consume1lane_theta = consume_t[1][0]
    consume1z_theta = consume_t[1][1]
    for i0 in range(len(consume1lane_theta)):
        tmplane = consume1lane_theta[i0]
        f3.write(str(state[consume1lane_theta[i0]][consume1z_theta[i0]]))
        red_relations.append(FetchRelations(str(state[consume1lane_theta[i0]][consume1z_theta[i0]])))
        keys.append(f"θ^{{({2})}}_{{{consume1lane_theta[i0]%5},{int(consume1lane_theta[i0]/5)},{consume1z_theta[i0]}}}")
        # state[consume1lane_theta[i0]][consume1z_theta[i0]] = cu2theta(
        #     SLICES * 5 * int(tmplane / 5) + 5 * consume1z_theta[i0] + (tmplane % 5))
        f3.write(' = ')
        f3.write(str(state[consume1lane_theta[i0]][consume1z_theta[i0]]))
        f3.write('\n')
    f3.close()


    return red_relations,keys
