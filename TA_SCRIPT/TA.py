# This is a sample Python script.

# Press Shift+F10 to execute it or replace it with your code.
# Press Double Shift to search everywhere for classes, files, tool windows, actions, and settings.
import numpy as np


def delete_row_col(matrix, row, col):
    matrix = np.delete(matrix, row, axis=0)
    matrix = np.delete(matrix, col, axis=1)
    # print("New matrix size: ", len(matrix), len(matrix[0]))
    # print(matrix)
    return matrix


def delete_row(matrix, row):
    matrix = np.delete(matrix, row, axis=0)
    return matrix


def delete_blank_col(matrix):
    need_del = []
    for j in range(matrix.shape[1]):
        blank = True
        for i in range(len(matrix)):
            if matrix[i][j] > 0:
                blank = False
        if blank:
            need_del.append(j)
    for i in range(len(need_del)):
        matrix = np.delete(matrix, need_del[i] - i, axis=1)
    return matrix


def swap_matrix(matrix, row1, row2, col1, col2):
    for i in range(row1, row2, -1):
        matrix[[i, i - 1], :] = matrix[[i - 1, i], :]
    for i in range(col1, col2, -1):
        matrix[:, [i, i - 1]] = matrix[:, [i - 1, i]]
    return matrix


def swap_matrix_row(matrix, row1, row2):
    for i in range(row1, row2, -1):
        matrix[[i, i - 1], :] = matrix[[i - 1, i], :]
    return matrix


def swap_matrix_col(matrix, col1, col2):
    for i in range(col1, col2, -1):
        matrix[:, [i, i - 1]] = matrix[:, [i - 1, i]]
    return matrix


def independent_vars(matrix, row_index, free_statistics):
    if row_index >= len(matrix):
        return 0
    result = 0
    for j in range(len(matrix[row_index])):
        free_var = 0
        if matrix[row_index][j] > 0:
            count = 0
            for i in range(len(matrix)):
                if matrix[i][j] > 0:
                    count += 1
                    free_var = matrix[i][j]
            if count == 1:
                # print(j)
                result += 1
                free_statistics.append(free_var)
    return result


def all_index(matrix):
    result = []
    for j in range(matrix.shape[1]):
        blank = True
        var_ind = 0
        for i in range(matrix.shape[0]):
            if matrix[i][j] > 0:
                blank = False
                var_ind = matrix[i][j]
        if not blank:
            result.append(var_ind)
    return result


# execute once
def TA_algorithm(relations, var_num, verbose=False):
    free_vars_num = 0
    free_vars = []
    finished = False

    eq_num = len(relations)

    TAMatrix = np.zeros((eq_num, var_num), dtype='int32')
    for i in range(eq_num):
        for j in relations[i]:
            TAMatrix[i][j] = j
            if j == 0:
                TAMatrix[i][j] = var_num

    TAMatrix = delete_blank_col(TAMatrix)
    var_num = TAMatrix.shape[1]
    if verbose:
        print("Initial matrix size :", TAMatrix.shape)
        print(TAMatrix)
    all_vars_index = all_index(TAMatrix)
    if verbose:
        print("all_vars_index :", all_vars_index)

    dependent_vars_num = 0
    dependent_vars = []
    while not finished:
        delete_v = False
        for i in range(var_num):
            count = 0
            row = 0
            var_index = 0
            for j in range(eq_num):
                if TAMatrix[j][i] > 0:
                    count += 1
                    row = j
                    var_index = TAMatrix[j][i]
            if count == 1:
                delete_v = True
                # print("var ", var_index)
                tmp_free = []
                independent_num = independent_vars(TAMatrix, row, tmp_free)
                free_vars_num += independent_num - 1
                for k in range(len(tmp_free)):
                    if tmp_free[k] != var_index:
                        free_vars.append(tmp_free[k])
                TAMatrix = delete_row(TAMatrix, row)
                dependent_vars_num += 1
                dependent_vars.append(var_index)
                eq_num -= 1

        if delete_v == False:
            finished = True
    # remain some vars that can not be single in the column of matrix
    if dependent_vars_num + free_vars_num < var_num:
        for ind in all_vars_index:
            if ind not in dependent_vars and ind not in free_vars:
                free_vars.append(ind)
                free_vars_num += 1
    if verbose:
        print("Number of dependent vars: ", dependent_vars_num)
        print("All dependent vars are: ", dependent_vars)
        print("Number of free vars: ", free_vars_num)
        print("All free vars are: ", free_vars)
        f1 = open('state/ascon_relations_0.txt', 'a')
        f1.write("\n\nNumber of equations: " + str(len(relations)))
        f1.write("\nNumber of vars: " + str(len(all_vars_index)))
        f1.write("\nAll_vars_index: " + str(all_vars_index))
        f1.write("\nNumber of dependent vars: " + str(dependent_vars_num))
        f1.write("\nAll dependent vars are: " + str(dependent_vars))
        f1.write("\nNumber of free vars: " + str(free_vars_num))
        f1.write("\nAll free vars are: " + str(free_vars))
        f1.close()


def determine_most_var_eq(matrix):
    most_var_eq = 0
    most_count = 0
    for i in range(matrix.shape[0]):
        tmp_count = 0
        for j in range(matrix.shape[1]):
            if matrix[i][j] > 0:
                tmp_count += 1
        if tmp_count > most_count:
            most_count = tmp_count
            most_var_eq = i
    return most_var_eq


# execute until the TA matrix is nil
def TA_algorithm_loop(relations, var_num, verbose=False):
    free_vars_num = 0
    free_vars = []

    eq_num = len(relations)

    TAMatrix = np.zeros((eq_num, var_num), dtype='int32')
    for i in range(eq_num):
        for j in relations[i]:
            TAMatrix[i][j] = j
            if j == 0:
                TAMatrix[i][j] = var_num
    TAMatrix = delete_blank_col(TAMatrix)
    var_num = TAMatrix.shape[1]
    if verbose:
        print("Initial matrix size :", TAMatrix.shape)
        print(TAMatrix)
    all_vars_index = all_index(TAMatrix)
    if verbose:
        print("all_vars_index :", all_vars_index)

    dependent_vars_num = 0
    dependent_vars = []
    while TAMatrix.shape[0] > 0:
        # print("current rows: ", TAMatrix.shape[0])
        finished = False
        while not finished:
            delete_v = False
            for i in range(var_num):
                count = 0
                row = 0
                var_index = 0
                for j in range(eq_num):
                    if TAMatrix[j][i] > 0:
                        count += 1
                        row = j
                        var_index = TAMatrix[j][i]
                if count == 1:
                    delete_v = True
                    print("var ", var_index)
                    # tmp_free = []
                    # independent_num = independent_vars(TAMatrix, row, tmp_free)
                    # free_vars_num += independent_num - 1
                    # for k in range(len(tmp_free)):
                    #    if tmp_free[k] != var_index:
                    #        free_vars.append(tmp_free[k])
                    TAMatrix = delete_row(TAMatrix, row)
                    dependent_vars_num += 1
                    dependent_vars.append(var_index)
                    eq_num -= 1

            if delete_v == False:
                finished = True
                # print("this time dependent_vars_num: ", dependent_vars_num)
        if TAMatrix.shape[0] > 0:
            most_v = determine_most_var_eq(TAMatrix)
            print("most vars, delete: ", most_v)
            TAMatrix = delete_row(TAMatrix, most_v)
            eq_num -= 1
        # remain some vars that can not be single in the column of matrix
        # if dependent_vars_num + free_vars_num < var_num:
        #    for ind in all_vars_index:
        #        if ind not in dependent_vars and ind not in free_vars:
        #            free_vars.append(ind)
        #            free_vars_num += 1
    for ind in all_vars_index:
        if ind not in dependent_vars:
            free_vars.append(ind)
            free_vars_num += 1

    print("Number of dependent vars: ", dependent_vars_num)
    print("All dependent vars are: ", dependent_vars)
    print("All dependent vars are: ", sorted(dependent_vars))
    print("Number of free vars: ", free_vars_num)
    print("All free vars are: ", free_vars)
    print("\n\n\n")
    if verbose:
        f1 = open('state/TA_result.txt', 'a')
        f1.write("\n\nNumber of equations: " + str(len(relations)))
        f1.write("\nNumber of vars: " + str(len(all_vars_index)))
        f1.write("\nAll_vars_index: " + str(all_vars_index))
        f1.write("\nNumber of dependent vars: " + str(dependent_vars_num))
        f1.write("\nAll dependent vars are: " + str(dependent_vars))
        f1.write("\nNumber of free vars: " + str(free_vars_num))
        f1.write("\nAll free vars are: " + str(free_vars))
        f1.close()
    return dependent_vars, free_vars


def TA_algorithm_loop_tag(relations, keys, var_num, verbose=False):
    free_vars_num = 0
    free_vars = []

    eq_num = len(relations)

    TAMatrix = np.zeros((eq_num, var_num), dtype='int32')

    result_keys = []
    extract_keys = []
    delete_keys = []
    init_keys = keys.copy()


    for i in range(eq_num):
        for j in relations[i]:
            TAMatrix[i][j] = j
            if j == 0:
                TAMatrix[i][j] = var_num
    TAMatrix = delete_blank_col(TAMatrix)

    var_num = TAMatrix.shape[1]
    if verbose:
        print("Initial matrix size :", TAMatrix.shape)
        print(TAMatrix)
    all_vars_index = all_index(TAMatrix)
    if verbose:
        print("all_vars_index :", all_vars_index)

    output = "\\begin{eqnarray}\\tiny\label{eqn:_matrix}\n\\begin{array}{l}\n\\begin{array}{c}\n" \
             "\\left(\\begin{array}{r"
    for i in range(var_num):
        output += "c"
    output += "}\n"
    for i in range(var_num):
        output += f"&k_{{{all_vars_index[i]}}}"
    output += "\\\\\n\\hline\n"
    for i in range(TAMatrix.shape[0]):
        output += init_keys[i]
        for j in range(TAMatrix.shape[1]):
            if TAMatrix[i][j] > 0:
                output += f"&1 "
            else:
                output += f"&0 "
        output += " \\\\\n"
    output += "\\end{array}\\right)\\\\\n\\text{\large $(a)$}\n\\end{array}"

    ResultMatrix = np.zeros((eq_num, var_num), dtype='int32')
    ExactMatrix = np.zeros((eq_num, var_num), dtype='int32')
    DeleteMatrix = np.zeros((eq_num, var_num), dtype='int32')

    dependent_vars_num = 0
    dependent_vars = []
    delete_row_num = 0
    while TAMatrix.shape[0] > 0:
        # print("current rows: ", TAMatrix.shape[0])
        finished = False
        while not finished:
            delete_v = False
            for i in range(var_num):
                count = 0
                row = 0
                var_index = 0
                for j in range(eq_num):
                    if TAMatrix[j][i] > 0:
                        count += 1
                        row = j
                        var_index = TAMatrix[j][i]
                if count == 1:
                    delete_v = True
                    print("var ", var_index)
                    ExactMatrix[dependent_vars_num] = TAMatrix[row]
                    TAMatrix = delete_row(TAMatrix, row)
                    extract_keys.append(keys[row])
                    del keys[row]
                    dependent_vars_num += 1
                    dependent_vars.append(var_index)
                    eq_num -= 1


            if delete_v == False:
                finished = True
                # print("this time dependent_vars_num: ", dependent_vars_num)
        if TAMatrix.shape[0] > 0:
            most_v = determine_most_var_eq(TAMatrix)
            print("most vars, delete: ", most_v)
            DeleteMatrix[delete_row_num] = TAMatrix[most_v]
            delete_keys.append(keys[most_v])
            del keys[most_v]
            #for jj in range(var_num):
            #    DeleteMatrix[delete_row_num][jj] = TAMatrix[most_v][jj]
            delete_row_num += 1
            TAMatrix = delete_row(TAMatrix, most_v)
            eq_num -= 1
        # remain some vars that can not be single in the column of matrix
        # if dependent_vars_num + free_vars_num < var_num:
        #    for ind in all_vars_index:
        #        if ind not in dependent_vars and ind not in free_vars:
        #            free_vars.append(ind)
        #            free_vars_num += 1
    for ind in all_vars_index:
        if ind not in dependent_vars:
            free_vars.append(ind)
            free_vars_num += 1

    for i in range(delete_row_num):
        for j in range(dependent_vars_num):
            ResultMatrix[i][j] = DeleteMatrix[i][all_vars_index.index(dependent_vars[j])]
        for j in range(free_vars_num):
            ResultMatrix[i][j+dependent_vars_num] = DeleteMatrix[i][all_vars_index.index(free_vars[j])]
    for i in range(dependent_vars_num):
        for j in range(dependent_vars_num):
            ResultMatrix[i+delete_row_num][j] = ExactMatrix[i][all_vars_index.index(dependent_vars[j])]
        for j in range(free_vars_num):
            ResultMatrix[i+delete_row_num][j+dependent_vars_num] = ExactMatrix[i][all_vars_index.index(free_vars[j])]
    result_keys = delete_keys + extract_keys

    output += "\\rightarrow\n"
    output += "\\begin{array}{c}\n" \
             "\\left(\\begin{array}{r"
    for i in range(var_num):
        if i == (dependent_vars_num-1):
            output += "c|"
        else:
            output += "c"
    output += "}\n"
    for i in range(dependent_vars_num):
        output += f"&k_{{{dependent_vars[i]}}}"
    for i in range(free_vars_num):
        output += f"&k_{{{free_vars[i]}}}"
    output += "\\\\\n\\hline\n"

    for i in range(delete_row_num):
        if i == delete_row_num:
            output += "\\hline\n"
        output += result_keys[i]
        for j in range(ResultMatrix.shape[1]):
            if ResultMatrix[i][j] > 0:
                output += "&\\textcolor{cyan}{1} "
            else:
                output += "&\\textcolor{cyan}{0} "
        output += " \\\\\n"
    for i in range(delete_row_num,ResultMatrix.shape[0]):
        if i == delete_row_num:
            output += "\\hline\n"
        output += result_keys[i]
        for j in range(ResultMatrix.shape[1]):
            if ResultMatrix[i][j] > 0:
                output += f"&1 "
            else:
                output += f"&0 "
        output += " \\\\\n"
    output += "\\end{array}\\right)\\\\\n\\text{\large $(b)$}\n\\end{array}\n\\end{array}\n\\end{eqnarray}"


    print("Number of dependent vars: ", dependent_vars_num)
    print("All dependent vars are: ", dependent_vars)
    print("All dependent vars are: ", sorted(dependent_vars))
    print("Number of free vars: ", free_vars_num)
    print("All free vars are: ", free_vars)
    print("\n\n\n")
    if verbose:
        f1 = open('state/TA_result.txt', 'a')
        f1.write("\n\nNumber of equations: " + str(len(relations)))
        f1.write("\nNumber of vars: " + str(len(all_vars_index)))
        f1.write("\nAll_vars_index: " + str(all_vars_index))
        f1.write("\nNumber of dependent vars: " + str(dependent_vars_num))
        f1.write("\nAll dependent vars are: " + str(dependent_vars))
        f1.write("\nNumber of free vars: " + str(free_vars_num))
        f1.write("\nAll free vars are: " + str(free_vars))
        f1.close()
    print(ResultMatrix)
    print(result_keys)
    print(output)
    return dependent_vars, free_vars

def TA_algorithm_loop_txt(relations, keys, var_num, verbose=False):
    free_vars_num = 0
    free_vars = []

    eq_num = len(relations)

    TAMatrix = np.zeros((eq_num, var_num), dtype='int32')

    result_keys = []
    extract_keys = []
    delete_keys = []
    init_keys = keys.copy()


    for i in range(eq_num):
        for j in relations[i]:
            TAMatrix[i][j] = j
            if j == 0:
                TAMatrix[i][j] = var_num
    TAMatrix = delete_blank_col(TAMatrix)

    var_num = TAMatrix.shape[1]
    if verbose:
        print("Initial matrix size :", TAMatrix.shape)
        print(TAMatrix)
    all_vars_index = all_index(TAMatrix)
    if verbose:
        print("all_vars_index :", all_vars_index)

    output = "{:^20}".format('')
    for i in range(var_num):
        output += "{:^5}".format(f"v{all_vars_index[i]}")
    output += "\n"
    for i in range(TAMatrix.shape[0]):
        output += "{:^20}".format(init_keys[i])
        for j in range(TAMatrix.shape[1]):
            if TAMatrix[i][j] > 0:
                output += "{:^5}".format("1")
            else:
                output += "{:^5}".format("0")
        output += "\n"
    output += "\n\n\n"
    #print(output)
    #print("\n\n")

    ResultMatrix = np.zeros((eq_num, var_num), dtype='int32')
    ExactMatrix = np.zeros((eq_num, var_num), dtype='int32')
    DeleteMatrix = np.zeros((eq_num, var_num), dtype='int32')

    dependent_vars_num = 0
    dependent_vars = []
    delete_row_num = 0
    while TAMatrix.shape[0] > 0:
        # print("current rows: ", TAMatrix.shape[0])
        finished = False
        while not finished:
            delete_v = False
            for i in range(var_num):
                count = 0
                row = 0
                var_index = 0
                for j in range(eq_num):
                    if TAMatrix[j][i] > 0:
                        count += 1
                        row = j
                        var_index = TAMatrix[j][i]
                if count == 1:
                    delete_v = True
                    print("var ", var_index)
                    ExactMatrix[dependent_vars_num] = TAMatrix[row]
                    TAMatrix = delete_row(TAMatrix, row)
                    extract_keys.append(keys[row])
                    del keys[row]
                    dependent_vars_num += 1
                    dependent_vars.append(var_index)
                    eq_num -= 1


            if delete_v == False:
                finished = True
                # print("this time dependent_vars_num: ", dependent_vars_num)
        if TAMatrix.shape[0] > 0:
            most_v = determine_most_var_eq(TAMatrix)
            print("most vars, delete: ", most_v)
            DeleteMatrix[delete_row_num] = TAMatrix[most_v]
            delete_keys.append(keys[most_v])
            del keys[most_v]
            #for jj in range(var_num):
            #    DeleteMatrix[delete_row_num][jj] = TAMatrix[most_v][jj]
            delete_row_num += 1
            TAMatrix = delete_row(TAMatrix, most_v)
            eq_num -= 1
        # remain some vars that can not be single in the column of matrix
        # if dependent_vars_num + free_vars_num < var_num:
        #    for ind in all_vars_index:
        #        if ind not in dependent_vars and ind not in free_vars:
        #            free_vars.append(ind)
        #            free_vars_num += 1
    for ind in all_vars_index:
        if ind not in dependent_vars:
            free_vars.append(ind)
            free_vars_num += 1

    for i in range(delete_row_num):
        for j in range(dependent_vars_num):
            ResultMatrix[i][j] = DeleteMatrix[i][all_vars_index.index(dependent_vars[j])]
        for j in range(free_vars_num):
            ResultMatrix[i][j+dependent_vars_num] = DeleteMatrix[i][all_vars_index.index(free_vars[j])]
    for i in range(dependent_vars_num):
        for j in range(dependent_vars_num):
            ResultMatrix[i+delete_row_num][j] = ExactMatrix[i][all_vars_index.index(dependent_vars[j])]
        for j in range(free_vars_num):
            ResultMatrix[i+delete_row_num][j+dependent_vars_num] = ExactMatrix[i][all_vars_index.index(free_vars[j])]
    result_keys = delete_keys + extract_keys

    output += "{:^20}".format('')
    for i in range(dependent_vars_num):
        output += "{:^5}".format(f"v{dependent_vars[i]}")
    for i in range(free_vars_num):
        output += "{:^5}".format(f"v{free_vars[i]}")
    output += "\n"

    for i in range(delete_row_num):
        if i == delete_row_num:
            output += "\n"
        output += "{:^20}".format(result_keys[i])
        for j in range(ResultMatrix.shape[1]):
            if ResultMatrix[i][j] > 0:
                output += "{:^5}".format("1")
            else:
                output += "{:^5}".format("0")
        output += " \n"
    for i in range(delete_row_num,ResultMatrix.shape[0]):
        if i == delete_row_num:
            output += "\n"
        output += "{:^20}".format(result_keys[i])
        for j in range(ResultMatrix.shape[1]):
            if ResultMatrix[i][j] > 0:
                output += "{:^5}".format("1")
            else:
                output += "{:^5}".format("0")
        output += "\n"
    #print(output)


    print("Number of dependent vars: ", dependent_vars_num)
    print("All dependent vars are: ", dependent_vars)
    print("All dependent vars are: ", sorted(dependent_vars))
    print("Number of free vars: ", free_vars_num)
    print("All free vars are: ", free_vars)
    print("\n\n\n")
    if verbose:
        f1 = open('state/TA_result.txt', 'a')
        f1.write("\n\nNumber of equations: " + str(len(relations)))
        f1.write("\nNumber of vars: " + str(len(all_vars_index)))
        f1.write("\nAll_vars_index: " + str(all_vars_index))
        f1.write("\nNumber of dependent vars: " + str(dependent_vars_num))
        f1.write("\nAll dependent vars are: " + str(dependent_vars))
        f1.write("\nNumber of free vars: " + str(free_vars_num))
        f1.write("\nAll free vars are: " + str(free_vars))
        f1.close()
    print(ResultMatrix)
    print(result_keys)
    print(output)
    return dependent_vars, free_vars



# Press the green button in the gutter to run the script.
if __name__ == '__main__':
    var_num = 320
    relations = [
        [1,2,3,4,5,6,7],
        [2,3,4,5,6,7],
        [3,4,5,6,7],
        [4,5,6,7]
    ]
    eq_num = len(relations)

    TAMatrix = np.zeros((eq_num, var_num), dtype='int32')

    for i in range(eq_num):
        for j in relations[i]:
            TAMatrix[i][j] = j
            if j == 0:
                TAMatrix[i][j] = var_num
    TAMatrix = delete_blank_col(TAMatrix)
    var_num = TAMatrix.shape[1]

    print("Initial matrix size :", TAMatrix.shape)
    print(TAMatrix)
    all_vars_index = all_index(TAMatrix)

    print("all_vars_index :", all_vars_index)

    TAMatrix = swap_matrix(TAMatrix,3,1,4,2)
    print(TAMatrix)
