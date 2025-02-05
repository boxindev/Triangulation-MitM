# This is a sample Python script.

# Press Shift+F10 to execute it or replace it with your code.
# Press Double Shift to search everywhere for classes, files, tool windows, actions, and settings.

from xoodyak_exp_consume import *
from TA import *
import time

# Press the green button in the gutter to run the script.
if __name__ == '__main__':

    start = time.time()
    verbose = True
    file_name = "outputimm/xoodyak/xoodyak_preimage_3r.json"
    with_symmetry = False

    var_num = 192 if with_symmetry else 384

    red_relations, keys = xoodyak_round(FILE_NAME=file_name, WITH_SYMMETRY=with_symmetry, verbose=verbose)
    # print(red_relations)
    
    f = open('state/TA_result.txt', 'w')
    f.close()

    TA_algorithm_loop(red_relations, var_num, verbose=verbose)

    end = time.time()
    print("运行时间: %.2f 秒" % (end - start))

