from ascon_exp_consume import *
from  TA import *

if __name__ == '__main__':

    '''
    verbose = True
    file_name = f"outputimm/ascon/ascon-preimage-3r.json"
    with_symmetry = True

    var_num = 160 if with_symmetry else 320

    red_relations, keys = ASCON_round(FILE_NAME=file_name, WITH_SYMMETRY=with_symmetry, verbose=verbose)
    TA_algorithm_loop_tag(red_relations, keys, var_num, verbose=verbose)
    '''

    verbose = True
    file_name = f"outputimm/ascon/ascon-preimage-4r.json"
    with_symmetry = True

    var_num = 160 if with_symmetry else 320

    red_relations, keys = ASCON_round(FILE_NAME=file_name, WITH_SYMMETRY=with_symmetry, verbose=verbose)
    TA_algorithm_loop_tag(red_relations, keys, var_num, verbose=verbose)