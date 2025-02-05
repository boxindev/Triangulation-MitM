# This is a sample Python script.

# Press Shift+F10 to execute it or replace it with your code.
# Press Double Shift to search everywhere for classes, files, tool windows, actions, and settings.
from ascon_solution import *
from keccak384_exp_consume import *
from keccak512_exp_consume import *
from TA import *
import time

# Press the green button in the gutter to run the script.
if __name__ == '__main__':

    '''
    start = time.time()
    verbose = True
    file_name = "outputimm/keccak/keccak512_preimage_4r_eu23.json"
    with_symmetry = False

    var_num = 160 if with_symmetry else 320

    red_relations,keys = KECCAK512_round(FILE_NAME=file_name, WITH_SYMMETRY=with_symmetry, verbose=verbose)
    print(red_relations)

    f = open('state/TA_result.txt', 'w')
    f.close()

    TA_algorithm_loop(red_relations, var_num, verbose=verbose)

    end = time.time()
    print("运行时间: %.2f 秒" % (end - start))
    '''

    '''
    start = time.time()
    verbose = True
    file_name = "outputimm/keccak/keccak512_preimage_4r.json"
    with_symmetry = True

    var_num = 160 if with_symmetry else 320

    red_relations, keys = KECCAK512_round(FILE_NAME=file_name, WITH_SYMMETRY=with_symmetry, verbose=verbose)
    print(red_relations)

    f = open('state/TA_result.txt', 'w')
    f.close()

    TA_algorithm_loop(red_relations, var_num, verbose=verbose)

    end = time.time()
    print("运行时间: %.2f 秒" % (end - start))
    '''

    '''
    start = time.time()
    verbose = True
    file_name = "outputimm/keccak384/keccak384_preimage_4r.json"
    with_symmetry = False

    var_num = 320 if with_symmetry else 640

    red_relations, keys = KECCAK384_round(FILE_NAME=file_name, WITH_SYMMETRY=with_symmetry, verbose=verbose)
    print(red_relations)

    f = open('state/TA_result.txt', 'w')
    f.close()

    TA_algorithm_loop(red_relations, var_num, verbose=verbose)

    end = time.time()
    print("运行时间: %.2f 秒" % (end - start))
    '''