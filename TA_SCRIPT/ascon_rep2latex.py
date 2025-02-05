# coding=UTF-8

WITH_SYMMETRY = True
SLICES = 32 if WITH_SYMMETRY else 64


def a2index(input):
    x = int(input)
    i = x % SLICES
    j = int(x / SLICES)
    return i, j


def cu2index(input):
    x = int(input)
    i = x % SLICES
    j = int(x / SLICES)
    return i, j


def str2element(name, index):
    res = ''
    if name == 'vr':
        res += '\\textcolor{red}{A^{(0)}_{\{'
        x, y = a2index(index)
        res += str(x) + ',' + str(y) + '\}}}'

    if name == 'vb':
        res += '\\textcolor{blue}{A^{(0)}_{\{'
        x, y = a2index(index)
        res += str(x) + ',' + str(y) + '\}}}'

    if name == 'vg':
        res += 'A^{(0)}_{\{'
        x, y = a2index(index)
        res += str(x) + ',' + str(y) + '\}}'

    if name == 'S':
        res += 'S^{(0)}_{\{'
        x, y = a2index(index)
        res += str(x) + ',' + str(y) + '\}}'

    if name == 'cur0':
        res += 'C^{(0)}_{\{'
        x, y = cu2index(index)
        res += str(x) + ',' + str(y) + '\}}'

    if name == 'cub0':
        res += 'C^{(0)}_{\{'
        x, y = cu2index(index)
        res += str(x) + ',' + str(y) + '\}}'

    if name == 'cur1':
        x, y = cu2index(index)
        res = str(x) + ',' + str(y) + '\}}}' + res
        res = '{\\bf C^{(1)}_{\{' + res

    if name == 'cub1':
        x, y = cu2index(index)
        res = str(x) + ',' + str(y) + '\}}}' + res
        res = '{\\bf C^{(1)}_{\{' + res

    return res


def representation2latex(s):
    res = ''
    temp = s.strip()
    temp = temp.replace(')', '')
    temp = temp.replace('*', ' * ')
    temp = temp.replace('(', ' ')
    temp = temp.split()

    # print(len(temp))
    # print(temp)

    for j in range(0, len(temp)):
        if (j % 30) == 29:
            if j > 0:
                res += '\\'
                res += '\\'
                res += '\n'
                res += '&'
        res += str2element(temp[j], temp[j + 1])

        if temp[j] == '*':
            res += '\!\cdot\!'

        if temp[j] == '+':
            res += '\!\oplus\!'
            if temp[j + 1] == '1':
                res += '1'

        if temp[j] == '=':
            res = '}\!= \!' + res
            res = '{\\bf ' + str2element(temp[j + 1], temp[j + 2]) + res
            break
    res = '&' + res
    res += '\\'
    res += '\\'
    res += '\n'

    return res


if __name__ == "__main__":

    result = ''
    count = 0
    # result += '\\begin{equation}\n'
    # result += '\\footnotesize\n'
    result += '{\\tiny\\begin{align*}\n'

    fr = open('state/ascon_consume_0.txt', 'r')
    for line in fr:
        result += representation2latex(line)
        count += 1

    result += '\\end{align*}}\n'
    # result += '\\end{equation}\n'

    fw = open('state/latexRep.txt', 'w')
    fw.write(result)

    fr.close()
    fw.close()

    print(count)
