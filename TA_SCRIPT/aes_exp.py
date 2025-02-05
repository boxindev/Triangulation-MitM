from TA import TA_algorithm_loop, TA_algorithm_loop_tag


def s_box(state):
    tmp = [[] for i in range(len(state))]
    for i in range(len(state)):
        if not state[i]:
            continue
        exp = ' + '.join(state[i])
        tmp[i].append("S(" + exp + ")")
    state = tmp
    return state


def s_box_single(element):
    if not element:
        return []
    else:
        exp = ' + '.join(element)
        return ["S(" + exp + ")"]


def s_box_inv(state):
    tmp = [[] for i in range(len(state))]
    for i in range(len(state)):
        if not state[i]:
            continue
        exp = ' + '.join(state[i])
        tmp[i].append("S-1(" + exp + ")")
    state = tmp
    return state


def s_box_inv_single(element):
    if not element:
        return []
    else:
        exp = ' + '.join(element)
        return ["S-1(" + exp + ")"]


def shift_row(state):
    sr = [0, 5, 10, 15, 4, 9, 14, 3, 8, 13, 2, 7, 12, 1, 6, 11]
    tmp = []
    for i in range(len(state)):
        tmp.append(state[sr[i]])
    state = tmp
    return state


def shift_row_inv(state):
    sr = [0, 13, 10, 7, 4, 1, 14, 11, 8, 5, 2, 15, 12, 9, 6, 3]
    tmp = []
    for i in range(len(state)):
        tmp.append(state[sr[i]])
    state = tmp
    return state


def shift_row_192(state):
    sr = [0, 1, 2, 3]
    tmp = []
    for i in range(len(state)):
        row = i % 4
        tmp.append(state[(i + 4 * sr[row]) % len(state)])
    state = tmp
    return state


def shift_row_inv_192(state):
    sr = [0, 1, 2, 3]
    tmp = []
    for i in range(len(state)):
        row = i % 4
        tmp.append(state[(i - 4 * sr[row]) % len(state)])
    state = tmp
    return state


def shift_row_256(state):
    sr = [0, 1, 3, 4]
    tmp = []
    for i in range(len(state)):
        row = i % 4
        tmp.append(state[(i + 4 * sr[row]) % len(state)])
    state = tmp
    return state


def shift_row_inv_256(state):
    sr = [0, 1, 3, 4]
    tmp = []
    for i in range(len(state)):
        row = i % 4
        tmp.append(state[(i - 4 * sr[row]) % len(state)])
    state = tmp
    return state


def coefficient(ele):
    if ele == 1:
        return ""
    else:
        return str(ele) + "*"


def multi(coef, eles):
    if coef == "":
        return ' + '.join(eles)
    else:
        if len(eles) == 1:
            return coef + ' + '.join(eles)
        else:
            return coef + "(" + ' + '.join(eles) + ")"


def mix_column(state):
    mc = [[2, 3, 1, 1],
          [1, 2, 3, 1],
          [1, 1, 2, 3],
          [3, 1, 1, 2]]
    tmp = []
    for i in range(len(state)):
        col = int(i / 4)
        row = i % 4
        exp = []
        for j in range(4):
            if not state[4 * col + j]:
                continue
            exp.append(multi(coefficient(mc[row][j]), state[4 * col + j]))
        tmp.append(exp)
    state = tmp
    for i in range(len(state)):
        state[i] = remove_dup(state[i])
    return state


def mix_column_inv(state):
    mc = [["0E", "0B", "0D", "09"],
          ["09", "0E", "0B", "0D"],
          ["0D", "09", "0E", "0B"],
          ["0B", "0D", "09", "0E"]]
    tmp = []
    for i in range(len(state)):
        col = int(i / 4)
        row = i % 4
        exp = []
        for j in range(4):
            if not state[4 * col + j]:
                continue
            exp.append(multi(coefficient(mc[row][j]), state[4 * col + j]))
        tmp.append(exp)
    state = tmp
    for i in range(len(state)):
        state[i] = remove_dup(state[i])
    return state


def add_key(state, key):
    for i in range(len(state)):
        state[i] = state[i] + key[i]
    for i in range(len(state)):
        state[i] = remove_dup(state[i])
    return state


def add_key_2(state, key1, key2):
    for i in range(len(state)):
        state[i] = state[i] + key1[i] + key2[i]
    for i in range(len(state)):
        state[i] = remove_dup(state[i])
    return state


def aes_round(state, key):
    state = s_box(state)
    state = shift_row(state)
    state = mix_column(state)
    state = add_key(state, key)
    return state


def key_exp_128(key):
    tmp = []
    index = [13, 14, 15, 12]
    for i in range(4):
        exp = key[i].copy()
        if key[index[i]]:
            exp.append("S(" + ' + '.join(key[index[i]]) + ")")
        tmp.append(exp)
    for i in range(4, len(key)):
        exp = key[i] + tmp[i - 4]
        exp = remove_dup(exp)
        tmp.append(exp)
    key = tmp
    return key


def key_exp_128_s2s(S):
    newS = [[] for _ in range(len(S))]
    newS[0] = S[13] + s_box_single(S[12])
    newS[1] = S[14]
    newS[2] = S[15]
    newS[3] = S[12]

    newS[4] = S[1] + s_box_single(S[0])
    newS[5] = S[2]
    newS[6] = S[3]
    newS[7] = S[0]

    newS[8] = S[5] + s_box_single(S[4])
    newS[9] = S[6]
    newS[10] = S[7]
    newS[11] = S[4]

    newS[12] = S[9] + s_box_single(S[8])
    newS[13] = S[10]
    newS[14] = S[11]
    newS[15] = S[8]
    for i in range(len(newS)):
        newS[i] = remove_dup(newS[i])
    return newS


def key_exp_128_s2s_inv(S):
    newS = [[] for _ in range(len(S))]
    newS[0] = S[7]
    newS[1] = S[4] + s_box_single(S[7])
    newS[2] = S[5]
    newS[3] = S[6]

    newS[4] = S[11]
    newS[5] = S[8] + s_box_single(S[11])
    newS[6] = S[9]
    newS[7] = S[10]

    newS[8] = S[15]
    newS[9] = S[12] + s_box_single(S[15])
    newS[10] = S[13]
    newS[11] = S[14]

    newS[12] = S[3]
    newS[13] = S[0] + s_box_single(S[3])
    newS[14] = S[1]
    newS[15] = S[2]
    for i in range(len(newS)):
        newS[i] = remove_dup(newS[i])
    return newS


def key_exp_128_s2k(S):
    K = [[] for _ in range(len(S))]
    K[0] = S[3] + S[6] + S[9] + S[12]
    K[1] = S[2] + S[5] + S[8] + S[15]
    K[2] = S[1] + S[4] + S[11] + S[14]
    K[3] = S[0] + S[7] + S[10] + S[13]

    K[4] = S[6] + S[12]
    K[5] = S[2] + S[8]
    K[6] = S[4] + S[14]
    K[7] = S[0] + S[10]

    K[8] = S[3] + S[12]
    K[9] = S[8] + S[15]
    K[10] = S[4] + S[11]
    K[11] = S[0] + S[7]

    K[12] = S[12]
    K[13] = S[8]
    K[14] = S[4]
    K[15] = S[0]

    for i in range(len(K)):
        K[i] = remove_dup(K[i])
    return K


def key_exp_192(key):
    tmp = []
    index = [21, 22, 23, 20]
    for i in range(4):
        exp = key[i].copy()
        if key[index[i]]:
            exp.append("S(" + ' + '.join(key[index[i]]) + ")")
        tmp.append(exp)
    for i in range(4, len(key)):
        exp = key[i] + tmp[i - 4]
        exp = remove_dup(exp)
        tmp.append(exp)
    key = tmp
    return key


def key_exp_192_s2s(S):
    newS = [[] for _ in range(len(S))]
    newS[0] = S[12] + S[13] + S[14] + s_box_single(S[15])
    newS[1] = S[13] + S[14] + s_box_single(S[15])
    newS[2] = S[14] + s_box_single(S[15])
    newS[3] = S[15]
    newS[4] = S[16]
    newS[5] = S[17]
    newS[6] = S[18] + S[19] + S[20] + s_box_single(S[21])
    newS[7] = S[19] + S[20] + s_box_single(S[21])
    newS[8] = S[20] + s_box_single(S[21])
    newS[9] = S[21]
    newS[10] = S[22]
    newS[11] = S[23]
    newS[12] = S[0]
    newS[13] = S[1]
    newS[14] = S[2]
    newS[15] = S[3] + S[4] + S[5] + s_box_single(S[6])
    newS[16] = S[4] + S[5] + s_box_single(S[6])
    newS[17] = S[5] + s_box_single(S[6])
    newS[18] = S[6]
    newS[19] = S[7]
    newS[20] = S[8]
    newS[21] = S[9] + S[10] + S[11] + s_box_single(S[0])
    newS[22] = S[10] + S[11] + s_box_single(S[0])
    newS[23] = S[11] + s_box_single(S[0])
    for i in range(len(newS)):
        newS[i] = remove_dup(newS[i])
    return newS


def key_exp_192_s2s_inv(S):
    newS = [[] for _ in range(len(S))]
    newS[0] = S[12]
    newS[1] = S[13]
    newS[2] = S[14]
    newS[3] = S[15] + S[16]
    newS[4] = S[16] + S[17]
    newS[5] = S[17] + s_box_single(S[18])
    newS[6] = S[18]
    newS[7] = S[19]
    newS[8] = S[20]
    newS[9] = S[21] + S[22]
    newS[10] = S[22] + S[23]
    newS[11] = S[23] + s_box_single(S[12])
    newS[12] = S[0] + S[1]
    newS[13] = S[1] + S[2]
    newS[14] = S[2] + s_box_single(S[3])
    newS[15] = S[3]
    newS[16] = S[4]
    newS[17] = S[5]
    newS[18] = S[6] + S[7]
    newS[19] = S[7] + S[8]
    newS[20] = S[8] + s_box_single(S[9])
    newS[21] = S[9]
    newS[22] = S[10]
    newS[23] = S[11]
    for i in range(len(newS)):
        newS[i] = remove_dup(newS[i])
    return newS


def key_exp_192_s2k(S):
    K = [[] for _ in range(len(S))]
    K[0] = S[2] + S[14]
    K[1] = S[5] + S[17]
    K[2] = S[8] + S[20]
    K[3] = S[11] + S[23]

    K[4] = S[2]
    K[5] = S[17]
    K[6] = S[8]
    K[7] = S[23]

    K[8] = S[1] + S[13]
    K[9] = S[4] + S[16]
    K[10] = S[7] + S[19]
    K[11] = S[10] + S[22]

    K[12] = S[1]
    K[13] = S[16]
    K[14] = S[7]
    K[15] = S[22]

    K[16] = S[0] + S[12]
    K[17] = S[3] + S[15]
    K[18] = S[6] + S[18]
    K[19] = S[9] + S[21]

    K[20] = S[0]
    K[21] = S[15]
    K[22] = S[6]
    K[23] = S[21]
    for i in range(len(K)):
        K[i] = remove_dup(K[i])
    return K


def key_exp_192_k2rk(K, RK, Rounds):
    for r in range(Rounds + 1):
        for i in range(4):
            for j in range(4):
                indRK = 4 * i + j
                indK = 4 * ((4 * r + i) % 6) + j
                RK[r][indRK] = K[int((4 * r + i) / 6)][indK]


def key_exp_256_s2s(S):
    newS = [[] for _ in range(len(S))]
    newS[0] = S[26] + s_box_single(S[25])
    newS[1] = S[27] + s_box_single(newS[0])
    newS[2] = S[28]
    newS[3] = S[29]
    newS[4] = S[30]
    newS[5] = S[31]
    newS[6] = S[24]
    newS[7] = S[25]

    newS[8] = S[2] + s_box_single(S[1])
    newS[9] = S[3] + s_box_single(newS[8])
    newS[10] = S[4]
    newS[11] = S[5]
    newS[12] = S[6]
    newS[13] = S[7]
    newS[14] = S[0]
    newS[15] = S[1]

    newS[16] = S[10] + s_box_single(S[9])
    newS[17] = S[11] + s_box_single(newS[16])
    newS[18] = S[12]
    newS[19] = S[13]
    newS[20] = S[14]
    newS[21] = S[15]
    newS[22] = S[8]
    newS[23] = S[9]

    newS[24] = S[18] + s_box_single(S[17])
    newS[25] = S[19] + s_box_single(newS[24])
    newS[26] = S[20]
    newS[27] = S[21]
    newS[28] = S[22]
    newS[29] = S[23]
    newS[30] = S[16]
    newS[31] = S[17]
    for i in range(len(newS)):
        newS[i] = remove_dup(newS[i])
    return newS


def key_exp_256_s2s_inv(S):
    newS = [[] for _ in range(len(S))]
    newS[0] = S[14]
    newS[1] = S[15]
    newS[2] = S[8] + s_box_single(S[15])
    newS[3] = S[9] + s_box_single(S[8])
    newS[4] = S[10]
    newS[5] = S[11]
    newS[6] = S[12]
    newS[7] = S[13]

    newS[8] = S[22]
    newS[9] = S[23]
    newS[10] = S[16] + s_box_single(S[23])
    newS[11] = S[17] + s_box_single(S[16])
    newS[12] = S[18]
    newS[13] = S[19]
    newS[14] = S[20]
    newS[15] = S[21]

    newS[16] = S[30]
    newS[17] = S[31]
    newS[18] = S[24] + s_box_single(S[31])
    newS[19] = S[25] + s_box_single(S[24])
    newS[20] = S[26]
    newS[21] = S[27]
    newS[22] = S[28]
    newS[23] = S[29]

    newS[24] = S[6]
    newS[25] = S[7]
    newS[26] = S[0] + s_box_single(S[7])
    newS[27] = S[1] + s_box_single(S[0])
    newS[28] = S[2]
    newS[29] = S[3]
    newS[30] = S[4]
    newS[31] = S[5]
    for i in range(len(newS)):
        newS[i] = remove_dup(newS[i])
    return newS


def key_exp_256_s2k(S):
    K = [[] for _ in range(len(S))]
    K[0] = S[6] + S[12] + S[18] + S[24]
    K[1] = S[4] + S[10] + S[16] + S[30]
    K[2] = S[2] + S[8] + S[22] + S[28]
    K[3] = S[0] + S[14] + S[20] + S[26]

    K[4] = S[12] + S[24]
    K[5] = S[4] + S[16]
    K[6] = S[8] + S[28]
    K[7] = S[0] + S[20]

    K[8] = S[6] + S[24]
    K[9] = S[16] + S[30]
    K[10] = S[8] + S[22]
    K[11] = S[0] + S[14]

    K[12] = S[24]
    K[13] = S[16]
    K[14] = S[8]
    K[15] = S[0]

    K[16] = S[7] + S[13] + S[19] + S[25]
    K[17] = S[5] + S[11] + S[17] + S[31]
    K[18] = S[3] + S[9] + S[23] + S[29]
    K[19] = S[1] + S[15] + S[21] + S[27]

    K[20] = S[13] + S[25]
    K[21] = S[5] + S[17]
    K[22] = S[9] + S[29]
    K[23] = S[1] + S[21]

    K[24] = S[7] + S[25]
    K[25] = S[17] + S[31]
    K[26] = S[9] + S[23]
    K[27] = S[1] + S[15]

    K[28] = S[25]
    K[29] = S[17]
    K[30] = S[9]
    K[31] = S[1]
    for i in range(len(K)):
        K[i] = remove_dup(K[i])
    return K


def key_exp_256_k2rk(K, RK, Rounds):
    for r in range(Rounds + 1):
        for i in range(4):
            for j in range(4):
                indRK = 4 * i + j
                indK = 4 * (i + (r % 2) * 4) + j
                RK[r][indRK] = K[int(r / 2)][indK]


def remove_dup(alist):
    result = []
    uniqueelements = []
    for v in alist:
        if v not in uniqueelements:
            uniqueelements.append(v)
    for v in uniqueelements:
        if alist.count(v) % 2 == 1:
            result.append(v)
    return result


def print_state(state):
    for i in range(len(state)):
        print("{:>2}: ".format(i), end="")
        if len(state[i]) == 0:
            print('\'\'')
        else:
            print(' + '.join(state[i]))


def print_key(key):
    for i in range(len(key)):
        print("{:>2}: ".format(i), end="")
        if len(key[i]) == 0:
            print('\'\'')
        else:
            print(' + '.join(key[i]))


def get_index(alist):
    result = []
    for ele in alist:
        ele = ele.replace('S', ' ')
        ele = ele.replace('(', ' ')
        ele = ele.replace('2*', ' ')
        ele = ele.replace('3*', ' ')
        ele = ele.replace(')', ' ')
        ele = ele.replace('k', ' ')
        ele = ele.replace('+', ' ')
        ele = ele.split()

        for j in range(0, len(ele)):
            if int(ele[j]) not in result:
                result.append(int(ele[j]))
    return result


def get_red_index(alist):
    result = []
    for ele in alist:
        ele = ele.replace('S-1', ' ')
        ele = ele.replace('S', ' ')
        ele = ele.replace('(', ' ')
        ele = ele.replace('2*', ' ')
        ele = ele.replace('3*', ' ')
        ele = ele.replace('09*', ' ')
        ele = ele.replace('0E*', ' ')
        ele = ele.replace('0B*', ' ')
        ele = ele.replace('0D*', ' ')
        ele = ele.replace(')', ' ')
        ele = ele.replace('+', ' ')
        ele = ele.split()

        for j in range(len(ele)):
            if ele[j] == 'sr':
                if int(ele[j + 1]) not in result:
                    result.append(int(ele[j + 1]))
    # print(result)
    return result


def get_blue_index(alist):
    result = []
    for ele in alist:
        ele = ele.replace('S-1', ' ')
        ele = ele.replace('S', ' ')
        ele = ele.replace('(', ' ')
        ele = ele.replace('2*', ' ')
        ele = ele.replace('3*', ' ')
        ele = ele.replace('09*', ' ')
        ele = ele.replace('0E*', ' ')
        ele = ele.replace('0B*', ' ')
        ele = ele.replace('0D*', ' ')
        ele = ele.replace(')', ' ')
        ele = ele.replace('+', ' ')
        ele = ele.split()

        for j in range(len(ele)):
            if ele[j] == 'sb':
                if int(ele[j + 1]) not in result:
                    result.append(int(ele[j + 1]))
    # print(result)
    return result


'''
def aes_128_5r():
    red_relations = [[5, 10, 15, 12],  # A1(3)
              [9, 14, 13],  # A1(4)
              [12, 1, 6, 14, 10, 2, 15],  # A1(14)
              [12, 13, 9, 5, 1, 14],  # RK2(12)
              [13, 5, 14, 10, 6, 2, 15],  # RK2(13)
              # [8, 13, 2, 7, 9, 5, 1, 14],  # A2(4)
              [9, 14, 13, 8, 2, 7, 5, 1, 12, 6, 10, 15],  # A2(4)
              ]

    TA_algorithm_loop(red_relations, 16, verbose=True)
'''


def aes_128_5r():
    aes_version = 128  # need change
    Rounds = 5
    frounds = 3
    brounds = Rounds - frounds
    start_round = 0
    keys = []
    red = [1, 2, 5, 6, 7, 8, 9, 10, 12, 13, 14, 15]
    blue = [0, 3, 4, 11]
    gray = []

    K_consume = [
        [],
        [],
        [12, 13],
        [],
        [],
        [0]
    ]
    MC_consume = [
        [],
        [],
        []
    ]
    AK_consume = [
        [3, 4, 14],
        [4]
    ]
    MC_consume_inv = [
        [],
        [1, 4]
    ]
    AK_consume_inv = [
        [],
        []
    ]

    if aes_version == 128:
        S_size = 16
        S_round = Rounds + 1
    elif aes_version == 192:
        S_size = 24
        S_round = int((2 * Rounds + 4) / 3)
    elif aes_version == 256:
        S_size = 32
        S_round = int(Rounds / 2) + 1
    else:
        print("AES Version error!")
        exit(1)

    forward_state = [[] for i in range(16)]
    backward_state = [[] for i in range(16)]

    RK = [[[] for i in range(16)] for j in range(Rounds + 1)]
    consume_count = 0
    red_relations = []
    f_consume = open(f"state/AES/AES-{aes_version}-consume.txt", 'w')

    for i in red:
        RK[start_round][i].append(f"sr({i})")
    for i in blue:
        RK[start_round][i].append(f"sb({i})")
    for i in gray:
        RK[start_round][i].append(f"sg({i})")

    print(RK[start_round])

    for i in range(start_round + 1, S_round):
        print("forward ", i)
        RK[i] = key_exp_128(RK[i - 1])
        for c_index in K_consume[i]:
            exp = f"{'+'.join(RK[i][c_index])} = cu({consume_count})\n"
            red_relations.append(get_red_index(RK[i][c_index]))
            # S[i][c_index] = [f"cu({consume_count})"]
            keys.append(f"RK^{{({i})}}[{c_index}]")
            consume_count += 1
            f_consume.write(exp)
    for i in range(len(RK)):
        print(f"RK Round {i}")
        print_key(RK[i])

    # forward round
    for i in range(16):
        forward_state[i] = RK[0][i].copy()
    for i in range(frounds - 1):
        forward_state = s_box(forward_state)
        forward_state = shift_row(forward_state)
        forward_state = mix_column(forward_state)
        for c_index in MC_consume[i]:
            exp = f"{'+'.join(forward_state[c_index])} = cu({consume_count})\n"
            red_relations.append(get_red_index(forward_state[c_index]))
            # forward_state[c_index] = [f"cu({consume_count})"]
            keys.append(f"MC^{{({i})}}[{c_index}]")
            consume_count += 1
            f_consume.write(exp)
        forward_state = add_key(forward_state, RK[i + 1])
        for c_index in AK_consume[i]:
            exp = f"{'+'.join(forward_state[c_index])} = cu({consume_count})\n"
            red_relations.append(get_red_index(forward_state[c_index]))
            # forward_state[c_index] = [f"cu({consume_count})"]
            keys.append(f"A^{{({i + 1})}}[{c_index}]")
            consume_count += 1
            f_consume.write(exp)

    # backward round
    for i in range(16):
        backward_state[i] = RK[-1][i].copy()
    for i in range(brounds):
        if i == 0:
            backward_state = shift_row_inv(backward_state)
            backward_state = s_box_inv(backward_state)
            backward_state = add_key(backward_state, RK[-2 - i])
            for c_index in AK_consume_inv[i]:
                exp = f"{'+'.join(backward_state[c_index])} = cu({consume_count})\n"
                red_relations.append(get_red_index(backward_state[c_index]))
                # backward_state[c_index] = [f"cu({consume_count})"]
                keys.append(f"MC^{{({i})}}[{c_index}]")
                consume_count += 1
                f_consume.write(exp)
        else:
            backward_state = mix_column_inv(backward_state)
            for c_index in MC_consume_inv[i]:
                exp = f"{'+'.join(backward_state[c_index])} = cu({consume_count})\n"
                red_relations.append(get_red_index(backward_state[c_index]))
                # backward_state[c_index] = [f"cu({consume_count})"]
                keys.append(f"SR^{{({Rounds - i - 1})}}[{c_index}]")
                consume_count += 1
                f_consume.write(exp)

            backward_state = shift_row_inv(backward_state)
            backward_state = s_box_inv(backward_state)
            backward_state = add_key(backward_state, RK[-2 - i])
            for c_index in AK_consume_inv[i]:
                exp = f"{'+'.join(backward_state[c_index])} = cu({consume_count})\n"
                red_relations.append(get_red_index(backward_state[c_index]))
                # backward_state[c_index] = [f"cu({consume_count})"]
                keys.append(f"A^{{({i})}}[{c_index}]")
                consume_count += 1
                f_consume.write(exp)
    f_consume.close()
    for i in range(len(red_relations)):
        print(red_relations[i])
    print(keys)
    TA_algorithm_loop_tag(red_relations, keys, S_size, verbose=True)
    # TA_algorithm_loop(red_relations, S_size, verbose=True)


def aes_128_4rf_obj4():
    aes_version = 128  # need change
    Rounds = 4
    frounds = 2
    brounds = Rounds - frounds
    start_round = 3
    red = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 11, 12, 13, 15]
    blue = [10, 14]
    gray = []
    keys = []

    S_consume = [
        [],
        [],
        [],
        [],
        []
    ]
    K_consume = [
        [2, 3],
        [],
        [],
        [],
        [0, 1, 2, 3]
    ]
    MC_consume = [
        [],
        [],
    ]
    AK_consume = [
        [5, 10]
    ]
    MC_consume_inv = [
        [],
        [0, 7, 10, 13]
    ]
    AK_consume_inv = [
        [],
        []
    ]

    if aes_version == 128:
        S_size = 16
        S_round = Rounds + 1
    elif aes_version == 192:
        S_size = 24
        S_round = int((2 * Rounds + 4) / 3)
    elif aes_version == 256:
        S_size = 32
        S_round = int(Rounds / 2) + 1

    else:
        print("AES Version error!")
        exit(1)

    forward_state = [[] for i in range(16)]
    backward_state = [[] for i in range(16)]
    S = [[[] for i in range(S_size)] for j in range(S_round)]
    RK = [[[] for i in range(16)] for j in range(Rounds + 1)]
    consume_count = 0
    red_relations = []
    f_consume = open(f"state/AES/AES-{aes_version}-consume.txt", 'w')

    for i in red:
        S[start_round][i].append(f"sr({i})")
    for i in blue:
        S[start_round][i].append(f"sb({i})")
    for i in gray:
        S[start_round][i].append(f"sg({i})")

    print(S[start_round])

    for i in range(start_round - 1, -1, -1):
        print("backward S ", i)
        S[i] = key_exp_128_s2s_inv(S[i + 1])
        for c_index in S_consume[i]:
            exp = f"{'+'.join(S[i][c_index])} = cu({consume_count})\n"
            red_relations.append(get_red_index(S[i][c_index]))
            keys.append(f"S^{{({i})}}[{c_index}]")
            # S[i][c_index] = [f"cu({consume_count})"]
            consume_count += 1
            f_consume.write(exp)
    for i in range(start_round + 1, S_round):
        print("forward S ", i)
        S[i] = key_exp_128_s2s(S[i - 1])
        for c_index in S_consume[i]:
            exp = f"{'+'.join(S[i][c_index])} = cu({consume_count})\n"
            red_relations.append(get_red_index(S[i][c_index]))
            keys.append(f"S^{{({i})}}[{c_index}]")
            # S[i][c_index] = [f"cu({consume_count})"]
            consume_count += 1
            f_consume.write(exp)
    for i in range(len(S)):
        print(f"S Round {i}")
        print_key(S[i])

    for i in range(len(S)):
        RK[i] = key_exp_128_s2k(S[i])
        for c_index in K_consume[i]:
            exp = f"{'+'.join(RK[i][c_index])} = cu({consume_count})\n"
            red_relations.append(get_red_index(RK[i][c_index]))
            keys.append(f"RK^{{({i})}}[{c_index}]")
            # K[i][c_index] = [f"cu({consume_count})"]
            consume_count += 1
            f_consume.write(exp)
    for i in range(len(RK)):
        print(f"RK Round {i}")
        print_key(RK[i])

    # forward round
    for i in range(16):
        forward_state[i] = RK[0][i].copy()
    for i in range(frounds - 1):
        forward_state = s_box(forward_state)
        forward_state = shift_row(forward_state)
        forward_state = mix_column(forward_state)
        for c_index in MC_consume[i]:
            exp = f"{'+'.join(forward_state[c_index])} = cu({consume_count})\n"
            red_relations.append(get_red_index(forward_state[c_index]))
            keys.append(f"MC^{{({i})}}[{c_index}]")
            # forward_state[c_index] = [f"cu({consume_count})"]
            consume_count += 1
            f_consume.write(exp)
        forward_state = add_key(forward_state, RK[i + 1])
        for c_index in AK_consume[i]:
            exp = f"{'+'.join(forward_state[c_index])} = cu({consume_count})\n"
            red_relations.append(get_red_index(forward_state[c_index]))
            keys.append(f"A^{{({i + 1})}}[{c_index}]")
            # forward_state[c_index] = [f"cu({consume_count})"]
            consume_count += 1
            f_consume.write(exp)

    # backward round
    for i in range(16):
        backward_state[i] = RK[-1][i].copy()
    for i in range(brounds):
        backward_state = mix_column_inv(backward_state)
        for c_index in MC_consume_inv[i]:
            exp = f"{'+'.join(backward_state[c_index])} = cu({consume_count})\n"
            red_relations.append(get_red_index(backward_state[c_index]))
            keys.append(f"SR^{{({Rounds - i - 1})}}[{c_index}]")
            # backward_state[c_index] = [f"cu({consume_count})"]
            consume_count += 1
            f_consume.write(exp)

        backward_state = shift_row_inv(backward_state)
        backward_state = s_box_inv(backward_state)
        backward_state = add_key(backward_state, RK[-2 - i])
        for c_index in AK_consume_inv[i]:
            exp = f"{'+'.join(backward_state[c_index])} = cu({consume_count})\n"
            red_relations.append(get_red_index(backward_state[c_index]))
            keys.append(f"MC^{{({Rounds - i - 2})}}[{c_index}]")
            # backward_state[c_index] = [f"cu({consume_count})"]
            consume_count += 1
            f_consume.write(exp)
    f_consume.close()
    for i in range(len(red_relations)):
        print(red_relations[i])
    # TA_algorithm_loop(red_relations, S_size, verbose=True)
    TA_algorithm_loop_tag(red_relations, keys, S_size, verbose=True)


def aes_192_obj2():
    aes_version = 192  # need change
    Rounds = 6
    frounds = 2
    brounds = Rounds - frounds
    start_round = 3
    red = [0, 1, 2, 3, 4, 5, 6, 7, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23]
    blue = [8, 9]
    gray = [10]
    keys = []

    S_consume = [
        [],
        [20],
        [],
        [],
        [8]
    ]
    K_consume = [
        [10],
        [],
        [],
        [18],
        []
    ]
    MC_consume = [
        [10],
        [],
    ]
    AK_consume = [
        [0],
        []
    ]
    MC_consume_inv = [
        [],
        [],
        [1, 3, 4, 6, 9, 11, 12, 14],
        [7, 13]
    ]
    AK_consume_inv = [
        [8, 9, 11],
        [],
        [],
        []
    ]

    if aes_version == 128:
        S_size = 16
        S_round = Rounds + 1
    elif aes_version == 192:
        S_size = 24
        S_round = int((2 * Rounds + 4) / 3)
    elif aes_version == 256:
        S_size = 32
        S_round = int(Rounds / 2) + 1

    else:
        print("AES Version error!")
        exit(1)

    forward_state = [[] for i in range(16)]
    backward_state = [[] for i in range(16)]
    S = [[[] for i in range(S_size)] for j in range(S_round)]
    K = [[[] for i in range(S_size)] for j in range(S_round)]
    RK = [[[] for i in range(16)] for j in range(Rounds + 1)]
    consume_count = 0
    red_relations = []
    f_consume = open(f"state/AES/AES-{aes_version}-consume.txt", 'w')

    for i in red:
        S[start_round][i].append(f"sr({i})")
    for i in blue:
        S[start_round][i].append(f"sb({i})")
    for i in gray:
        S[start_round][i].append(f"sg({i})")

    print(S[start_round])

    for i in range(start_round - 1, -1, -1):
        print("backward ", i)
        S[i] = key_exp_192_s2s_inv(S[i + 1])
        for c_index in S_consume[i]:
            exp = f"{'+'.join(S[i][c_index])} = cu({consume_count})\n"
            red_relations.append(get_red_index(S[i][c_index]))
            keys.append(f"S^{{({i})}}[{c_index}]")
            # S[i][c_index] = [f"cu({consume_count})"]
            consume_count += 1
            f_consume.write(exp)
    for i in range(start_round + 1, S_round):
        print("forward ", i)
        S[i] = key_exp_192_s2s(S[i - 1])
        for c_index in S_consume[i]:
            exp = f"{'+'.join(S[i][c_index])} = cu({consume_count})\n"
            red_relations.append(get_red_index(S[i][c_index]))
            keys.append(f"S^{{({i})}}[{c_index}]")
            # S[i][c_index] = [f"cu({consume_count})"]
            consume_count += 1
            f_consume.write(exp)
    for i in range(len(S)):
        print(f"S Round {i}")
        print_key(S[i])

    for i in range(len(S)):
        K[i] = key_exp_192_s2k(S[i])
        for c_index in K_consume[i]:
            exp = f"{'+'.join(K[i][c_index])} = cu({consume_count})\n"
            red_relations.append(get_red_index(K[i][c_index]))
            keys.append(f"K^{{({i})}}[{c_index}]")
            # K[i][c_index] = [f"cu({consume_count})"]
            consume_count += 1
            f_consume.write(exp)
    for i in range(len(K)):
        print(f"K Round {i}")
        print_key(K[i])

    key_exp_192_k2rk(K, RK, Rounds)
    for i in range(len(RK)):
        print(f"RK Round {i}")
        print_key(RK[i])

    # forward round
    for i in range(16):
        forward_state[i] = RK[0][i].copy()
    for i in range(frounds - 1):
        forward_state = s_box(forward_state)
        forward_state = shift_row(forward_state)
        forward_state = mix_column(forward_state)
        for c_index in MC_consume[i]:
            exp = f"{'+'.join(forward_state[c_index])} = cu({consume_count})\n"
            red_relations.append(get_red_index(forward_state[c_index]))
            keys.append(f"MC^{{({i})}}[{c_index}]")
            # forward_state[c_index] = [f"cu({consume_count})"]
            consume_count += 1
            f_consume.write(exp)
        forward_state = add_key(forward_state, RK[i + 1])
        for c_index in AK_consume[i]:
            exp = f"{'+'.join(forward_state[c_index])} = cu({consume_count})\n"
            red_relations.append(get_red_index(forward_state[c_index]))
            keys.append(f"A^{{({i + 1})}}[{c_index}]")
            # forward_state[c_index] = [f"cu({consume_count})"]
            consume_count += 1
            f_consume.write(exp)

    # backward round
    for i in range(16):
        backward_state[i] = RK[-1][i].copy()
    for i in range(brounds):
        if i == 0:
            backward_state = shift_row_inv(backward_state)
            backward_state = s_box_inv(backward_state)
            backward_state = add_key(backward_state, RK[-2 - i])
            for c_index in AK_consume_inv[i]:
                exp = f"{'+'.join(backward_state[c_index])} = cu({consume_count})\n"
                red_relations.append(get_red_index(backward_state[c_index]))
                keys.append(f"MC^{{({Rounds - i - 2})}}[{c_index}]")
                # backward_state[c_index] = [f"cu({consume_count})"]
                consume_count += 1
                f_consume.write(exp)
        else:
            backward_state = mix_column_inv(backward_state)
            for c_index in MC_consume_inv[i]:
                exp = f"{'+'.join(backward_state[c_index])} = cu({consume_count})\n"
                red_relations.append(get_red_index(backward_state[c_index]))
                keys.append(f"SR^{{({Rounds - i - 1})}}[{c_index}]")
                # backward_state[c_index] = [f"cu({consume_count})"]
                consume_count += 1
                f_consume.write(exp)

            backward_state = shift_row_inv(backward_state)
            backward_state = s_box_inv(backward_state)
            backward_state = add_key(backward_state, RK[-2 - i])
            for c_index in AK_consume_inv[i]:
                exp = f"{'+'.join(backward_state[c_index])} = cu({consume_count})\n"
                red_relations.append(get_red_index(backward_state[c_index]))
                keys.append(f"MC^{{({Rounds - i - 2})}}[{c_index}]")
                # backward_state[c_index] = [f"cu({consume_count})"]
                consume_count += 1
                f_consume.write(exp)
    f_consume.close()
    for i in range(len(red_relations)):
        print(red_relations[i])
    print(keys)
    TA_algorithm_loop_tag(red_relations, keys, S_size, verbose=True)


def aes_256_obj1():
    aes_version = 256  # need change
    Rounds = 7
    frounds = 4
    brounds = Rounds - frounds
    start_round = 1
    red = [0, 1, 2, 3, 4, 5, 6, 7, 9, 10, 11, 14, 16, 17, 18, 20, 21, 22, 27, 28, 29, 30, 31]
    blue = [23]
    gray = [8, 12, 13, 15, 19, 24, 25, 26]
    keys = []

    S_consume = [
        [],
        [],
        [],
        [],
        []
    ]
    K_consume = [
        [1],
        [],
        [],
        [10, 18]
    ]
    MC_consume = [
        [14, 15],
        [4, 14],
        []
    ]
    AK_consume = [
        [2, 6, 10, 12, 13],
        [3, 8, 9],
        []
    ]
    MC_consume_inv = [
        [],
        [],
        [1, 4, 11, 14],

    ]
    AK_consume_inv = [
        [8, 9, 11],
        [],
        [],
    ]

    if aes_version == 128:
        S_size = 16
        S_round = Rounds + 1
    elif aes_version == 192:
        S_size = 24
        S_round = int((2 * Rounds + 4) / 3)
    elif aes_version == 256:
        S_size = 32
        S_round = int(Rounds / 2) + 1

    else:
        print("AES Version error!")
        exit(1)

    forward_state = [[] for i in range(16)]
    backward_state = [[] for i in range(16)]
    S = [[[] for i in range(S_size)] for j in range(S_round)]
    K = [[[] for i in range(S_size)] for j in range(S_round)]
    RK = [[[] for i in range(16)] for j in range(Rounds + 1)]
    consume_count = 0
    red_relations = []
    f_consume = open(f"state/AES/AES-{aes_version}-consume.txt", 'w')

    for i in red:
        S[start_round][i].append(f"sr({i})")
    for i in blue:
        S[start_round][i].append(f"sb({i})")
    for i in gray:
        S[start_round][i].append(f"sg({i})")

    print(S[start_round])

    for i in range(start_round - 1, -1, -1):
        print("backward ", i)
        S[i] = key_exp_256_s2s_inv(S[i + 1])
        for c_index in S_consume[i]:
            exp = f"{'+'.join(S[i][c_index])} = cu({consume_count})\n"
            red_relations.append(get_red_index(S[i][c_index]))
            keys.append(f"S^{{({i})}}[{c_index}]")
            # S[i][c_index] = [f"cu({consume_count})"]
            consume_count += 1
            f_consume.write(exp)
    for i in range(start_round + 1, S_round):
        print("forward ", i)
        S[i] = key_exp_256_s2s(S[i - 1])
        for c_index in S_consume[i]:
            exp = f"{'+'.join(S[i][c_index])} = cu({consume_count})\n"
            red_relations.append(get_red_index(S[i][c_index]))
            keys.append(f"S^{{({i})}}[{c_index}]")
            # S[i][c_index] = [f"cu({consume_count})"]
            consume_count += 1
            f_consume.write(exp)
    for i in range(len(S)):
        print(f"S Round {i}")
        print_key(S[i])

    for i in range(len(S)):
        K[i] = key_exp_256_s2k(S[i])
        for c_index in K_consume[i]:
            exp = f"{'+'.join(K[i][c_index])} = cu({consume_count})\n"
            red_relations.append(get_red_index(K[i][c_index]))
            keys.append(f"K^{{({i})}}[{c_index}]")
            # K[i][c_index] = [f"cu({consume_count})"]
            consume_count += 1
            f_consume.write(exp)
    for i in range(len(K)):
        print(f"K Round {i}")
        print_key(K[i])

    key_exp_256_k2rk(K, RK, Rounds)
    for i in range(len(RK)):
        print(f"RK Round {i}")
        print_key(RK[i])

    # forward round
    for i in range(16):
        forward_state[i] = RK[0][i].copy()
    for i in range(frounds - 1):
        forward_state = s_box(forward_state)
        forward_state = shift_row(forward_state)
        forward_state = mix_column(forward_state)
        for c_index in MC_consume[i]:
            exp = f"{'+'.join(forward_state[c_index])} = cu({consume_count})\n"
            red_relations.append(get_red_index(forward_state[c_index]))
            keys.append(f"MC^{{({i})}}[{c_index}]")
            # forward_state[c_index] = [f"cu({consume_count})"]
            consume_count += 1
            f_consume.write(exp)
        forward_state = add_key(forward_state, RK[i + 1])
        for c_index in AK_consume[i]:
            exp = f"{'+'.join(forward_state[c_index])} = cu({consume_count})\n"
            red_relations.append(get_red_index(forward_state[c_index]))
            keys.append(f"A^{{({i + 1})}}[{c_index}]")
            # forward_state[c_index] = [f"cu({consume_count})"]
            consume_count += 1
            f_consume.write(exp)

    # backward round
    for i in range(16):
        backward_state[i] = RK[-1][i].copy()
    for i in range(brounds):
        if i == 0:
            backward_state = shift_row_inv(backward_state)
            backward_state = s_box_inv(backward_state)
            backward_state = add_key(backward_state, RK[-2 - i])
            for c_index in AK_consume_inv[i]:
                exp = f"{'+'.join(backward_state[c_index])} = cu({consume_count})\n"
                red_relations.append(get_red_index(backward_state[c_index]))
                keys.append(f"MC^{{({Rounds - i - 2})}}[{c_index}]")
                # backward_state[c_index] = [f"cu({consume_count})"]
                consume_count += 1
                f_consume.write(exp)
        else:
            backward_state = mix_column_inv(backward_state)
            for c_index in MC_consume_inv[i]:
                exp = f"{'+'.join(backward_state[c_index])} = cu({consume_count})\n"
                red_relations.append(get_red_index(backward_state[c_index]))
                keys.append(f"SR^{{({Rounds - i - 1})}}[{c_index}]")
                # backward_state[c_index] = [f"cu({consume_count})"]
                consume_count += 1
                f_consume.write(exp)

            backward_state = shift_row_inv(backward_state)
            backward_state = s_box_inv(backward_state)
            backward_state = add_key(backward_state, RK[-2 - i])
            for c_index in AK_consume_inv[i]:
                exp = f"{'+'.join(backward_state[c_index])} = cu({consume_count})\n"
                red_relations.append(get_red_index(backward_state[c_index]))
                keys.append(f"MC^{{({Rounds - i - 2})}}[{c_index}]")
                # backward_state[c_index] = [f"cu({consume_count})"]
                consume_count += 1
                f_consume.write(exp)
    f_consume.close()
    for i in range(len(red_relations)):
        print(red_relations[i])
    print(keys)
    TA_algorithm_loop_tag(red_relations, keys, S_size, verbose=True)


def aes_256_pre():
    aes_version = 256  # need change
    Rounds = 10
    frounds = 5
    brounds = Rounds - frounds
    state_start_round = 4
    key_start_round = 3
    S_red = [3]
    S_blue = [0, 1, 2, 4, 6, 7, 8, 9, 10, 12, 16, 17, 18, 20, 24, 25, 26, 30, 31]
    S_gray = [5, 11, 13, 14, 15, 19, 21, 22, 23, 27, 28, 29]

    State_red = [0, 3, 4, 5, 9, 10, 14, 15]
    # State_blue = [1, 2, 6, 7, 8, 11, 12, 13]
    State_blue = []
    State_gray = []

    S_consume = [
        [],
        [11, 19],
        [3, 11, 19, 27],
        [],
        [8],
        []
    ]
    K_consume = [
        [],
        [],
        [0, 3, 4, 5, 9],
        [],
        [],
        []
    ]
    MC_consume = [
        [],
        [],
        [],
        [],
        []
    ]
    AK_consume = [
        [],
        [],
        [],
        []
    ]
    MC_consume_inv = [
        [],
        [],
        [3, 6, 9, 12],
        [],
        []
    ]
    AK_consume_inv = [
        [],  # [1, 2, 6, 7, 8, 11, 12, 13],
        [],
        [],
        [],
        [6, 12],
        []
    ]

    if aes_version == 128:
        S_size = 16
        S_round = Rounds + 1
    elif aes_version == 192:
        S_size = 24
        S_round = int((2 * Rounds + 4) / 3)
    elif aes_version == 256:
        S_size = 32
        S_round = int(Rounds / 2) + 1

    else:
        print("AES Version error!")
        exit(1)

    forward_state = [[] for i in range(16)]
    backward_state = [[] for i in range(16)]
    S = [[[] for i in range(S_size)] for j in range(S_round)]
    K = [[[] for i in range(S_size)] for j in range(S_round)]
    RK = [[[] for i in range(16)] for j in range(Rounds + 1)]
    STATE = [[] for i in range(16)]
    consume_count = 0
    blue_relations = []
    f_consume = open(f"state/AES/AES-{aes_version}-consume.txt", 'w')

    for i in S_red:
        S[key_start_round][i].append(f"sr({i})")
    for i in S_blue:
        S[key_start_round][i].append(f"sb({i})")
    for i in S_gray:
        S[key_start_round][i].append(f"sg({i})")

    for i in State_red:
        STATE[i].append(f"sr({i + 32})")
    for i in State_blue:
        STATE[i].append(f"sb({i + 32})")
    for i in State_gray:
        STATE[i].append(f"sg({i + 32})")

    print(S[key_start_round])
    print(STATE)

    for i in range(key_start_round - 1, -1, -1):
        print("backward ", i)
        S[i] = key_exp_256_s2s_inv(S[i + 1])
        for c_index in S_consume[i]:
            exp = f"{'+'.join(S[i][c_index])} = cu({consume_count})\n"
            blue_relations.append(get_blue_index(S[i][c_index]))
            # S[i][c_index] = [f"cu({consume_count})"]
            consume_count += 1
            f_consume.write(exp)
    for i in range(key_start_round + 1, S_round):
        print("forward ", i)
        S[i] = key_exp_256_s2s(S[i - 1])
        for c_index in S_consume[i]:
            exp = f"{'+'.join(S[i][c_index])} = cu({consume_count})\n"
            blue_relations.append(get_blue_index(S[i][c_index]))
            # S[i][c_index] = [f"cu({consume_count})"]
            consume_count += 1
            f_consume.write(exp)
    for i in range(len(S)):
        print(f"S Round {i}")
        print_key(S[i])

    for i in range(len(S)):
        K[i] = key_exp_256_s2k(S[i])
        for c_index in K_consume[i]:
            exp = f"{'+'.join(K[i][c_index])} = cu({consume_count})\n"
            blue_relations.append(get_blue_index(K[i][c_index]))
            # K[i][c_index] = [f"cu({consume_count})"]
            consume_count += 1
            f_consume.write(exp)
    for i in range(len(K)):
        print(f"K Round {i}")
        print_key(K[i])

    key_exp_256_k2rk(K, RK, Rounds)
    for i in range(len(RK)):
        print(f"RK Round {i}")
        print_key(RK[i])

    # forward round
    for i in range(16):
        forward_state[i] = STATE[i].copy()
    for i in range(frounds - 1):
        forward_state = s_box(forward_state)
        forward_state = shift_row(forward_state)
        forward_state = mix_column(forward_state)
        for c_index in MC_consume[i]:
            exp = f"{'+'.join(forward_state[c_index])} = cu({consume_count})\n"
            blue_relations.append(get_blue_index(forward_state[c_index]))
            # forward_state[c_index] = [f"cu({consume_count})"]
            consume_count += 1
            f_consume.write(exp)
        forward_state = add_key(forward_state, RK[state_start_round + i + 1])
        for c_index in AK_consume[i]:
            exp = f"{'+'.join(forward_state[c_index])} = cu({consume_count})\n"
            blue_relations.append(get_blue_index(forward_state[c_index]))
            # forward_state[c_index] = [f"cu({consume_count})"]
            consume_count += 1
            f_consume.write(exp)

    # backward round
    for i in range(16):
        backward_state[i] = STATE[i].copy()
    for i in range(brounds):
        if state_start_round - i == 0:
            print("test::::")
            print(backward_state[7])
            backward_state = add_key_2(backward_state, RK[state_start_round - i], RK[state_start_round - i - 1])
            print(RK[state_start_round - i][7])
            print(RK[state_start_round - i - 1][7])
            for c_index in AK_consume_inv[i]:
                exp = f"{'+'.join(backward_state[c_index])} = cu({consume_count})\n"
                blue_relations.append(get_blue_index(backward_state[c_index]))
                # backward_state[c_index] = [f"cu({consume_count})"]
                consume_count += 1
                f_consume.write(exp)
            backward_state = shift_row_inv(backward_state)
            backward_state = s_box_inv(backward_state)
        elif i == 0:
            for j in [1, 2, 6, 7, 8, 11, 12, 13]:
                backward_state[j] = []
            backward_state = mix_column_inv(backward_state)
            for c_index in MC_consume_inv[i]:
                exp = f"{'+'.join(backward_state[c_index])} = cu({consume_count})\n"
                blue_relations.append(get_blue_index(backward_state[c_index]))
                # backward_state[c_index] = [f"cu({consume_count})"]
                consume_count += 1
                f_consume.write(exp)

            backward_state = shift_row_inv(backward_state)
            backward_state = s_box_inv(backward_state)

        else:
            backward_state = add_key(backward_state, RK[state_start_round - i])
            if i == 2:
                print("test222222222222")
                for j in range(16):
                    backward_state[j] = RK[2][j].copy()
                for j in range(16):
                    print(get_blue_index(backward_state[j]))
            for c_index in AK_consume_inv[i]:
                exp = f"{'+'.join(backward_state[c_index])} = cu({consume_count})\n"
                blue_relations.append(get_blue_index(backward_state[c_index]))
                # backward_state[c_index] = [f"cu({consume_count})"]
                consume_count += 1
                f_consume.write(exp)

            backward_state = mix_column_inv(backward_state)
            for c_index in MC_consume_inv[i]:
                exp = f"{'+'.join(backward_state[c_index])} = cu({consume_count})\n"
                blue_relations.append(get_blue_index(backward_state[c_index]))
                backward_state[c_index] = [f"cu({consume_count})"]
                consume_count += 1
                f_consume.write(exp)

            backward_state = shift_row_inv(backward_state)
            backward_state = s_box_inv(backward_state)
    f_consume.close()
    for i in range(len(blue_relations)):
        print(blue_relations[i])
    TA_algorithm_loop(blue_relations, 48, verbose=True)


def aes_256_pre_consume():
    aes_version = 256  # need change
    Rounds = 10
    frounds = 5
    brounds = Rounds - frounds
    state_start_round = 4
    key_start_round = 3
    S_red = [3]
    S_blue = [0, 1, 2, 4, 6, 7, 8, 9, 10, 12, 16, 17, 18, 20, 24, 25, 26, 30, 31]
    S_gray = [5, 11, 13, 14, 15, 19, 21, 22, 23, 27, 28, 29]

    State_red = [0, 3, 4, 5, 9, 10, 14, 15]
    # State_blue = [1, 2, 6, 7, 8, 11, 12, 13]
    State_blue = []
    State_gray = []

    S_consume = [
        [],
        [11, 19],
        [3, 11, 19, 27],
        [],
        [8],
        []
    ]
    K_consume = [
        [],
        [],
        [0, 3, 4, 5, 9],
        [],
        [],
        []
    ]
    MC_consume = [
        [],
        [],
        [],
        [],
        []
    ]
    AK_consume = [
        [],
        [],
        [],
        []
    ]
    MC_consume_inv = [
        [],
        [],
        [3, 6, 9, 12],
        [],
        []
    ]
    AK_consume_inv = [
        [],  # [1, 2, 6, 7, 8, 11, 12, 13],
        [],
        [],
        [],
        [6, 12],
        []
    ]

    if aes_version == 128:
        S_size = 16
        S_round = Rounds + 1
    elif aes_version == 192:
        S_size = 24
        S_round = int((2 * Rounds + 4) / 3)
    elif aes_version == 256:
        S_size = 32
        S_round = int(Rounds / 2) + 1

    else:
        print("AES Version error!")
        exit(1)

    forward_state = [[] for i in range(16)]
    backward_state = [[] for i in range(16)]
    S = [[[] for i in range(S_size)] for j in range(S_round)]
    K = [[[] for i in range(S_size)] for j in range(S_round)]
    RK = [[[] for i in range(16)] for j in range(Rounds + 1)]
    STATE = [[] for i in range(16)]
    consume_count = 1
    blue_relations = []
    f_consume = open(f"state/AES/AES-{aes_version}-consume.txt", 'w')

    for i in S_red:
        S[key_start_round][i].append(f"sr({i})")
    for i in S_blue:
        S[key_start_round][i].append(f"sb({i})")
    for i in S_gray:
        S[key_start_round][i].append(f"sg({i})")

    for i in State_red:
        STATE[i].append(f"sr({i + 32})")
    for i in State_blue:
        STATE[i].append(f"sb({i + 32})")
    for i in State_gray:
        STATE[i].append(f"sg({i + 32})")

    print(S[key_start_round])
    print(STATE)

    for i in range(key_start_round - 1, -1, -1):
        print("backward ", i)
        S[i] = key_exp_256_s2s_inv(S[i + 1])
        for c_index in S_consume[i]:
            exp = f"{'+'.join(S[i][c_index])} = cu({consume_count})\n"
            blue_relations.append(get_blue_index(S[i][c_index]))
            S[i][c_index] = [f"cu({consume_count})"]
            consume_count += 1
            f_consume.write(exp)
    for i in range(key_start_round + 1, S_round):
        print("forward ", i)
        S[i] = key_exp_256_s2s(S[i - 1])
        for c_index in S_consume[i]:
            exp = f"{'+'.join(S[i][c_index])} = cu({consume_count})\n"
            blue_relations.append(get_blue_index(S[i][c_index]))
            S[i][c_index] = [f"cu({consume_count})"]
            consume_count += 1
            f_consume.write(exp)
    for i in range(len(S)):
        print(f"S Round {i}")
        print_key(S[i])

    for i in range(len(S)):
        K[i] = key_exp_256_s2k(S[i])
        for c_index in K_consume[i]:
            exp = f"{'+'.join(K[i][c_index])} = cu({consume_count})\n"
            blue_relations.append(get_blue_index(K[i][c_index]))
            K[i][c_index] = [f"cu({consume_count})"]
            consume_count += 1
            f_consume.write(exp)
    # for i in range(len(K)):
    #    print(f"K Round {i}")
    #    print_key(K[i])

    key_exp_256_k2rk(K, RK, Rounds)
    for i in range(len(RK)):
        print(f"RK Round {i}")
        print_key(RK[i])


def aes_em_128_7r_obj2():
    aes_version = 128  # need change
    Rounds = 7
    frounds = 3
    brounds = Rounds - frounds
    red = [0, 2, 3, 5, 7, 9, 10, 13, 15]
    blue = [1, 11]
    gray = [4, 6, 8, 12, 14]
    keys = []

    if len(red) + len(blue) + len(gray) != 16:
        print("Color ERROR")
        exit(1)
    MC_consume = [
        [],
        [3, 4, 9, 14],
        []
    ]
    MC_consume_inv = [
        [],
        [],
        [3, 6, 9, 12],
        []
    ]

    if aes_version == 128:
        S_size = 16
    elif aes_version == 192:
        S_size = 24
    elif aes_version == 256:
        S_size = 32
    else:
        print("AES Version error!")
        exit(1)
    forward_state = [[] for i in range(S_size)]
    backward_state = [[] for i in range(S_size)]
    consume_count = 0
    red_relations = []
    f_consume = open(f"state/AES/AES-EM-{aes_version}-consume.txt", 'w')
    for i in red:
        forward_state[i].append(f"sr({i})")
        backward_state[i].append(f"sr({i})")
    for i in blue:
        forward_state[i].append(f"sb({i})")
        backward_state[i].append(f"sb({i})")
    for i in gray:
        forward_state[i].append(f"sg({i})")
        backward_state[i].append(f"sg({i})")

    # forward round
    for i in range(frounds - 1):
        forward_state = s_box(forward_state)
        forward_state = shift_row_192(forward_state)
        forward_state = mix_column(forward_state)
        for c_index in MC_consume[i]:
            exp = f"{'+'.join(forward_state[c_index])} = cu({consume_count})\n"
            red_relations.append(get_red_index(forward_state[c_index]))
            keys.append(f"MC^{{({i})}}[{c_index}]")
            # forward_state[c_index] = [f"cu({consume_count})"]
            consume_count += 1
            f_consume.write(exp)

    # backward round
    for i in range(brounds):
        if i == 0:
            backward_state = shift_row_inv_192(backward_state)
            backward_state = s_box_inv(backward_state)
        else:
            backward_state = mix_column_inv(backward_state)
            for c_index in MC_consume_inv[i]:
                exp = f"{'+'.join(backward_state[c_index])} = cu({consume_count})\n"
                red_relations.append(get_red_index(backward_state[c_index]))
                keys.append(f"SR^{{({Rounds - i - 1})}}[{c_index}]")
                # backward_state[c_index] = [f"cu({consume_count})"]
                consume_count += 1
                f_consume.write(exp)

            backward_state = shift_row_inv_192(backward_state)
            backward_state = s_box_inv(backward_state)
    f_consume.close()
    for i in range(len(red_relations)):
        print(red_relations[i])
    print(keys)
    TA_algorithm_loop_tag(red_relations, keys, S_size, verbose=True)


def aes_em_192_8r_obj4():
    aes_version = 192  # need change
    Rounds = 8
    frounds = 3
    brounds = Rounds - frounds
    red = [0, 1, 3, 4, 6, 7, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22]
    blue = [8, 23]
    gray = [2, 5]
    keys = []

    if len(red) + len(blue) + len(gray) != 24:
        print("Color ERROR")
        exit(1)
    MC_consume = [
        [],
        [],
        []
    ]
    MC_consume_inv = [
        [],
        [2, 3, 5, 6, 7, 12, 13, 14, 16, 17, 20, 23],
        [0, 1, 4, 7],
        [8, 15],
        []
    ]

    if aes_version == 128:
        S_size = 16
    elif aes_version == 192:
        S_size = 24
    elif aes_version == 256:
        S_size = 32
    else:
        print("AES Version error!")
        exit(1)
    forward_state = [[] for i in range(S_size)]
    backward_state = [[] for i in range(S_size)]
    consume_count = 0
    red_relations = []
    f_consume = open(f"state/AES/AES-EM-{aes_version}-consume.txt", 'w')
    for i in red:
        forward_state[i].append(f"sr({i})")
        backward_state[i].append(f"sr({i})")
    for i in blue:
        forward_state[i].append(f"sb({i})")
        backward_state[i].append(f"sb({i})")
    for i in gray:
        forward_state[i].append(f"sg({i})")
        backward_state[i].append(f"sg({i})")

    # forward round
    for i in range(frounds - 1):
        forward_state = s_box(forward_state)
        forward_state = shift_row_192(forward_state)
        forward_state = mix_column(forward_state)
        for c_index in MC_consume[i]:
            exp = f"{'+'.join(forward_state[c_index])} = cu({consume_count})\n"
            red_relations.append(get_red_index(forward_state[c_index]))
            keys.append(f"MC^{{({i})}}[{c_index}]")
            # forward_state[c_index] = [f"cu({consume_count})"]
            consume_count += 1
            f_consume.write(exp)

    # backward round
    for i in range(brounds):
        if i == 0:
            backward_state = shift_row_inv_192(backward_state)
            backward_state = s_box_inv(backward_state)
        else:
            backward_state = mix_column_inv(backward_state)
            for c_index in MC_consume_inv[i]:
                exp = f"{'+'.join(backward_state[c_index])} = cu({consume_count})\n"
                red_relations.append(get_red_index(backward_state[c_index]))
                keys.append(f"SR^{{({Rounds-i-1})}}[{c_index}]")
                # backward_state[c_index] = [f"cu({consume_count})"]
                consume_count += 1
                f_consume.write(exp)

            backward_state = shift_row_inv_192(backward_state)
            backward_state = s_box_inv(backward_state)
    f_consume.close()
    for i in range(len(red_relations)):
        print(red_relations[i])
    print(keys)
    TA_algorithm_loop_tag(red_relations,keys, S_size, verbose=True)


def aes_em_256_9r_obj2():
    aes_version = 256  # need change
    Rounds = 9
    frounds = 3
    brounds = Rounds - frounds
    red = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 20, 21, 23, 24, 25, 26, 27, 28, 30, 31]
    blue = [22]
    gray = [0, 19, 29]
    keys = []

    if len(red) + len(blue) + len(gray) != 32:
        print("Color ERROR")
        exit(1)
    MC_consume = [
        [],
        [],
        []
    ]
    MC_consume_inv = [
        [],
        [4, 6, 9, 12, 13, 15, 16, 18, 19, 22, 23, 25, 26, 28, 29, 31],
        [8, 9, 10, 20, 22, 23, 25, 26, 27],
        [26, 27],
        [],
        []
    ]

    if aes_version == 128:
        S_size = 16
    elif aes_version == 192:
        S_size = 24
    elif aes_version == 256:
        S_size = 32
    else:
        print("AES Version error!")
        exit(1)
    forward_state = [[] for i in range(S_size)]
    backward_state = [[] for i in range(S_size)]
    consume_count = 0
    red_relations = []
    f_consume = open(f"state/AES/AES-EM-{aes_version}-consume.txt", 'w')
    for i in red:
        forward_state[i].append(f"sr({i})")
        backward_state[i].append(f"sr({i})")
    for i in blue:
        forward_state[i].append(f"sb({i})")
        backward_state[i].append(f"sb({i})")
    for i in gray:
        forward_state[i].append(f"sg({i})")
        backward_state[i].append(f"sg({i})")

    # forward round
    for i in range(frounds - 1):
        forward_state = s_box(forward_state)
        forward_state = shift_row_256(forward_state)
        forward_state = mix_column(forward_state)
        for c_index in MC_consume[i]:
            exp = f"{'+'.join(forward_state[c_index])} = cu({consume_count})\n"
            red_relations.append(get_red_index(forward_state[c_index]))
            keys.append(f"MC^{{({i})}}[{c_index}]")
            # forward_state[c_index] = [f"cu({consume_count})"]
            consume_count += 1
            f_consume.write(exp)

    # backward round
    for i in range(brounds):
        if i == 0:
            backward_state = shift_row_inv_256(backward_state)
            backward_state = s_box_inv(backward_state)
        else:
            backward_state = mix_column_inv(backward_state)
            for c_index in MC_consume_inv[i]:
                exp = f"{'+'.join(backward_state[c_index])} = cu({consume_count})\n"
                red_relations.append(get_red_index(backward_state[c_index]))
                keys.append(f"SR^{{({Rounds - i - 1})}}[{c_index}]")
                # backward_state[c_index] = [f"cu({consume_count})"]
                consume_count += 1
                f_consume.write(exp)

            backward_state = shift_row_inv_256(backward_state)
            backward_state = s_box_inv(backward_state)
    f_consume.close()
    for i in range(len(red_relations)):
        print(red_relations[i])
    print(keys)
    TA_algorithm_loop_tag(red_relations,keys, S_size, verbose=True)


if __name__ == "__main__":
    aes_em_256_9r_obj2()
