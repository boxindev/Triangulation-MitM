#include"ascon.h"
#include <iostream>
#include <ctime>
#include <vector>
#include <map>
#include <cassert>
#include <cmath>

using namespace std;

void generate_ascon_4r_initial_state(UINT64* state) {
	// 50-bit Conditions
	UINT32 cond0x[4] = { 9, 10, 19, 28 };
	UINT32 cond1x[10] = { 0, 4, 5, 7, 12, 14, 20, 22, 24, 26 };
	UINT32 condPlusx[11] = { 0, 4, 7, 14, 19, 20, 24, 25, 26, 28, 29 };
	UINT32 gray_pos_1[2] = { 11, 50 };
	for (int i = 0; i < 5; i++)
	{
		state[i] = { 0 };
	}
	state[0] = 2;
	for (int i = 0; i < 2; i++)
	{
		state[0] |= (UINT64(1) << (63 - gray_pos_1[i]));
	}
	for (int i = 0; i < 10; i++)
	{
		state[1] |= (UINT64(1) << (63 - cond1x[i]));
		state[1] |= (UINT64(1) << (63 - cond1x[i] - 32));
	}
	for (int i = 0; i < 11; i++)
	{
		state[3] |= (UINT64(1) << (63 - condPlusx[i]));
		state[3] |= (UINT64(1) << (63 - condPlusx[i] - 32));
	}
	//displayState(state);
}

void process_A_1_18(bool v2, bool v4, bool v14, bool v36, bool& v44, bool v46, bool v57, bool A_1_18) {
	v44 = v2 ^ v4 ^ v14 ^ v36 ^ v46 ^ v57 ^ A_1_18;
}
void process_A_1_50(bool v4, bool& v12, bool v14, bool v25, bool v34, bool v46, bool v56, bool A_1_50) {
	v12 = v4 ^ v14 ^ v25 ^ v34 ^ v46 ^ v56 ^ (bool)1 ^ A_1_50;
}

void process_A_3_21(bool v2, bool& v7, bool v14, bool v24, bool v29, bool v36, bool v44, bool v46, bool v57, bool A_3_21) {
	v7 = (v2 & v14) ^ (v2 & v44) ^ (v14 & v57) ^ (v44 & v57) ^ v2 ^ v14 ^ v24 ^ v29 ^ v36 ^ v46 ^ v57 ^ A_3_21;
}
void process_A_3_53(bool v4, bool v12, bool v14, bool v25, bool v34, bool v36, bool& v39, bool v46, bool v56, bool v61, bool A_3_53) {
	v39 = (v12 & v25) ^ (v12 & v34) ^ (v25 & v46) ^ (v34 & v46) ^ v4 ^ v14 ^ v36 ^ v46 ^ v56 ^ v61 ^ A_3_53;
}

void process_A_1_11(bool v4, bool v7, bool v14, bool v36, bool& v37, bool A_1_11) {
	v37 = v4 ^ v7 ^ v14 ^ v36 ^ (bool)1 ^ A_1_11;
}
void process_A_1_43(bool& v5, bool v36, bool v39, bool A_1_43) {
	v5 = v36 ^ v39 ^ A_1_43;
}

void process_A_1_21(bool v2, bool v5, bool v14, bool v39, bool v44, bool v46, bool v57, bool& v60, bool A_1_21) {
	v60 = v2 ^ v5 ^ v14 ^ v39 ^ v44 ^ v46 ^ v57 ^ A_1_21;
}
void process_A_1_53(bool v7, bool v12, bool v25, bool& v28, bool v34, bool v37, bool v46, bool v56, bool A_1_53) {
	v28 = v7 ^ v12 ^ v25 ^ v34 ^ v37 ^ v46 ^ v56 ^ A_1_53;
}

void process_A_3_28(bool& v9, bool v14, bool v36, bool A_3_28) {
	v9 = v14 ^ v36 ^ (bool)1 ^ A_3_28;
}
void process_A_3_60(bool v4, bool v36, bool& v41, bool v46, bool A_3_60) {
	v41 = v4 ^ v36 ^ v46 ^ (bool)1 ^ A_3_60;
}

void process_A_3_18(bool v4, bool& v26, bool A_3_18) {
	v26 = v4 ^ A_3_18;
}
void process_A_3_50(bool v36, bool& v58, bool A_3_50) {
	v58 = v36 ^ (bool)1 ^ A_3_50;
}

void process_A_1_28(bool v9, bool v12, bool v14, bool v24, bool v25, bool v34, bool v46, bool& v54, bool v56, bool A_1_28) {
	v54 = v9 ^ v12 ^ v14 ^ v24 ^ v25 ^ v34 ^ v46 ^ v56 ^ (bool)1 ^ A_1_28;
}
void process_A_1_60(bool v2, bool v14, bool& v22, bool v41, bool v44, bool v56, bool v57, bool A_1_60) {
	v22 = v2 ^ v14 ^ v41 ^ v44 ^ v56 ^ v57 ^ A_1_60;
}

int process_A_1_19(bool v2, bool v5, bool v9, bool v12, bool v16, bool v22, bool v25, bool v34, bool v37, bool v44, bool A_1_19) {
	// deduce v19
	bool tmp1 = v2 ^ v9;
	bool tmp2 = ((v2 ^ v5 ^ v9 ^ v12 ^ v22) & v22) ^ ((v5 ^ v12 ^ v22) & v25) ^ (v5 & v34) ^ ((v2 ^ v5 ^ v9 ^ v34 ^ v44) & v44) ^ v2 ^ v5 ^ v9 ^ v16 ^ v22 ^ v25 ^ v34 ^ v37 ^ v44 ^ (bool)1 ^ A_1_19;
	if (tmp1)
		return (int)tmp2;
	else
	{
		if (tmp2)
			return -1;
		else
			return 2;
	}
}
int process_A_1_51(bool v2, bool v5, bool v12, bool v34, bool v37, bool v41, bool v44, bool v48, bool v54, bool v57, bool A_1_51) {
	// deduce v51
	bool tmp1 = v34 ^ v41;
	bool tmp2 = ((v2 ^ v12 ^ v34 ^ v37 ^ v41) & v12) ^ (v2 & v37) ^ ((v34 ^ v37 ^ v41 ^ v44 ^ v54) & v54) ^ ((v37 ^ v44 ^ v54) & v57) ^ v2 ^ v5 ^ v12 ^ v34 ^ v37 ^ v41 ^ v48 ^ v54 ^ v57 ^ (bool)1 ^ A_1_51;
	if (tmp1)
		return (int)tmp2;
	else
	{
		if (tmp2)
			return -1;
		else
			return 2;
	}
}

int process_A_4_12(bool v2, bool v5, bool v12, bool v16, bool v37, bool v41, bool v48, bool v57, bool v58, bool v60, bool A_4_12) {
	// deduce v38
	bool tmp1 = v16 ^ v58;
	bool tmp2 = ((v5 ^ v12 ^ v37 ^ v41) & v5) ^ ((v12 ^ v37 ^ v48 ^ v57) & v12) ^ (v37 & v48) ^ (v37 & v57) ^ (v16 & v60) ^ (v58 & v60) ^ v2 ^ v5 ^ v12 ^ v16 ^ v37 ^ v41 ^ (bool)1 ^ A_4_12;
	if (tmp1)
		return (int)tmp2;
	else
	{
		if (tmp2)
			return -1;
		else
			return 2;
	}
}
int process_A_4_44(bool v5, bool v16, bool v25, bool v26, bool v28, bool v37, bool v9, bool v44, bool v48, bool v34, bool A_4_44) {
	// deduce v6
	bool tmp1 = v26 ^ v48;
	bool tmp2 = (v5 & v16) ^ (v5 & v25) ^ (v26 & v28) ^ (v5 & v37) ^ ((v9 ^ v37) & v37) ^ (v5 & v44) ^ (v16 & v44) ^ (v25 & v44) ^ ((v37 ^ v44) & v44) ^ (v28 & v48) ^ v5 ^ v34 ^ v37 ^ v44 ^ v48 ^ (bool)1 ^ A_4_44;
	if (tmp1)
		return (int)tmp2;
	else
	{
		if (tmp2)
			return -1;
		else
			return 2;
	}
}

void process_A_3_49(bool& v0, bool v22, bool v39, bool v57, bool A_3_49) {
	// deduce v0
	v0 = v22 ^ v39 ^ v57 ^ A_3_49;
}
void process_A_3_17(bool v0, bool v25, bool& v32, bool A_3_17) {
	// deduce v32
	v32 = v25 ^ v0 ^ A_3_17;
}

void solve_TA(vector<bool> e, vector<bool> v, vector<vector<bool>>& Sols) {
	// 0, 2, 4, 5, 6, 7, 9, 12, 14, 16, 19, 22, 24, 25, 26, 28, 29, 32, 34, 36, 37, 38, 39, 41, 44, 46, 48, 51, 54, 56, 57, 58, 60, 61
	// 0  1  2  3  4  5  6  7   8   9   10  11  12  13  14  15  16  17  18  19  20  21  22  23  24  25  26  27  28  29  30  31  32  33 
	bool v_new[34] = { 0 };
	for (int i = 0; i < 34; i++)
	{
		v_new[i] = v[i];
	}
	process_A_1_18(v_new[1], v_new[2], v_new[8], v_new[19], v_new[24], v_new[25], v_new[30], e[0]);
	process_A_1_50(v_new[2], v_new[7], v_new[8], v_new[13], v_new[18], v_new[25], v_new[29], e[1]);
	process_A_3_21(v_new[1], v_new[5], v_new[8], v_new[12], v_new[16], v_new[19], v_new[24], v_new[25], v_new[30], e[2]);
	process_A_3_53(v_new[2], v_new[7], v_new[8], v_new[13], v_new[18], v_new[19], v_new[22], v_new[25], v_new[29], v_new[33], e[3]);
	process_A_1_11(v_new[2], v_new[5], v_new[8], v_new[19], v_new[20], e[4]);
	process_A_1_43(v_new[3], v_new[19], v_new[22], e[5]);
	process_A_1_21(v_new[1], v_new[3], v_new[8], v_new[22], v_new[24], v_new[25], v_new[30], v_new[32], e[6]);
	process_A_1_53(v_new[5], v_new[7], v_new[13], v_new[15], v_new[18], v_new[20], v_new[25], v_new[29], e[7]);
	process_A_3_28(v_new[6], v_new[8], v_new[19], e[8]);
	process_A_3_60(v_new[2], v_new[19], v_new[23], v_new[25], e[9]);
	process_A_3_18(v_new[2], v_new[14], e[10]);
	process_A_3_50(v_new[19], v_new[31], e[11]);
	process_A_1_28(v_new[6], v_new[7], v_new[8], v_new[12], v_new[13], v_new[18], v_new[25], v_new[28], v_new[29], e[12]);
	process_A_1_60(v_new[1], v_new[8], v_new[11], v_new[23], v_new[24], v_new[29], v_new[30], e[13]);

	process_A_3_49(v_new[0], v_new[11], v_new[22], v_new[30], e[18]);
	process_A_3_17(v_new[0], v_new[13], v_new[17], e[19]);

	int A19 = process_A_1_19(v_new[1], v_new[3], v_new[6], v_new[7], v_new[9], v_new[11], v_new[13], v_new[18], v_new[20], v_new[24], e[14]);
	if (A19 < 0)
		return;
	int A51 = process_A_1_51(v_new[1], v_new[3], v_new[7], v_new[18], v_new[20], v_new[23], v_new[24], v_new[26], v_new[28], v_new[30], e[15]);
	if (A51 < 0)
		return;
	int A12 = process_A_4_12(v_new[1], v_new[3], v_new[7], v_new[9], v_new[20], v_new[23], v_new[26], v_new[30], v_new[31], v_new[32], e[16]);
	if (A12 < 0)
		return;
	int A44 = process_A_4_44(v_new[3], v_new[9], v_new[13], v_new[14], v_new[15], v_new[20], v_new[6], v_new[24], v_new[26], v_new[18], e[17]);
	if (A44 < 0)
		return;
	vector<vector<bool>> sol_tmp;
	if (A19 == 2) {
		sol_tmp.push_back({ 0, 1 });
	}
	else
	{
		sol_tmp.push_back({ (bool)A19 });
	}
	if (A51 == 2) {
		sol_tmp.push_back({ 0, 1 });
	}
	else
	{
		sol_tmp.push_back({ (bool)A51 });
	}
	if (A12 == 2) {
		sol_tmp.push_back({ 0, 1 });
	}
	else
	{
		sol_tmp.push_back({ (bool)A12 });
	}
	if (A44 == 2) {
		sol_tmp.push_back({ 0, 1 });
	}
	else
	{
		sol_tmp.push_back({ (bool)A44 });
	}
	for (auto v19 : sol_tmp[0]) {
		for (auto v51 : sol_tmp[1]) {
			for (auto v38 : sol_tmp[2]) {
				for (auto v6 : sol_tmp[3]) {
					v_new[10] = v19;
					v_new[27] = v51;
					v_new[21] = v38;
					v_new[4] = v6;
					
					vector<bool> v_tmp_vector(v_new, v_new + 34);
					Sols.push_back(v_tmp_vector);
				}
			}
		}
	}
}

void get_unprocessed_cons(UINT64* state, UINT64 vr, UINT32* red_pos, UINT64& unprocessed_cons) {
	UINT64 tmpStateRed[5] = { 0 };
	for (int i = 0; i < 5; i++)
	{
		tmpStateRed[i] = state[i];
	}
	for (int i = 0; i < 34; i++)
	{
		tmpStateRed[0] ^= ((vr >> (33 - i)) & 0x1) << (63 - red_pos[i]);
	}
	/*set_bits(tmpStateRed, red_pos, vr, 34);*/
	/*printf("%016llx ", vr);
	printf("%016llx ", (tmpStateRed[0]));*/
	PermutationOnWords(tmpStateRed, 2);
	unprocessed_cons = ((unprocessed_cons << 1) | take_bit(tmpStateRed[3], 11));
	unprocessed_cons = ((unprocessed_cons << 1) | take_bit(tmpStateRed[3], 43));
	PermutationOnWords(tmpStateRed, 1);
	unprocessed_cons = ((unprocessed_cons << 1) | take_bit(tmpStateRed[1], 18));
	unprocessed_cons = ((unprocessed_cons << 1) | take_bit(tmpStateRed[1], 28));
	unprocessed_cons = ((unprocessed_cons << 1) | take_bit(tmpStateRed[1], 50));
	unprocessed_cons = ((unprocessed_cons << 1) | take_bit(tmpStateRed[1], 60));
	unprocessed_cons = ((unprocessed_cons << 1) | (take_bit(tmpStateRed[0], 18) ^ take_bit(tmpStateRed[2], 18) ^ take_bit(tmpStateRed[4], 18)));
	unprocessed_cons = ((unprocessed_cons << 1) | (take_bit(tmpStateRed[0], 28) ^ take_bit(tmpStateRed[2], 28) ^ take_bit(tmpStateRed[4], 28)));
	unprocessed_cons = ((unprocessed_cons << 1) | (take_bit(tmpStateRed[0], 50) ^ take_bit(tmpStateRed[2], 50) ^ take_bit(tmpStateRed[4], 50)));
	unprocessed_cons = ((unprocessed_cons << 1) | (take_bit(tmpStateRed[0], 60) ^ take_bit(tmpStateRed[2], 60) ^ take_bit(tmpStateRed[4], 60)));
	//cout << unprocessed_cons << endl;
}

void get_match_4_bits(UINT64* state, UINT64 v, UINT32* pos, UINT32 length, UINT32* match_pos, UINT64& match) {
	UINT64 tmpState[5] = { 0 };
	for (int i = 0; i < 5; i++)
	{
		tmpState[i] = state[i];
	}
	for (int i = 0; i < length; i++)
	{
		tmpState[0] ^= ((v >> (length - 1 - i)) & 0x1) << (63 - pos[i]);
	}
	//set_bits(state, pos, v, length);
	PermutationOnWords(tmpState, 3);
	pS(tmpState);
	for (int i = 0; i < 4; i++)
	{
		match = (match << 1) ^ take_bit(tmpState[0], match_pos[i]);
	}
}

bool verify_ascon_4r_preimage(UINT64* state, UINT32* match_pos) {
	UINT64 match = 0;
	for (int i = 0; i < 4; i++)
	{
		match = (match << 1) ^ take_bit(state[0], match_pos[i]);
	}
	if (match == 0)
		return 1;
	else
		return 0;
}

bool find_ascon_4r_preimage(UINT64* state, UINT32* match_pos, vector<UINT64>& preimage_sols) {
	int target_pos[20] = { 16, 17, 19, 29, 30, 31, 48, 49, 51, 52, 53, 54, 55, 56, 57, 58, 59, 61, 62, 63 };
	//int target_pos[28] = { 16, 17, 19, 20, 21, 22, 23, 24, 25, 26, 27, 29, 30, 31, 48, 49, 51, 52, 53, 54, 55, 56, 57, 58, 59, 61, 62, 63 };
	int length = sizeof(target_pos) / sizeof(int);
	UINT64 new_state[5] = { 0 };
	for (int i = 0; i < 5; i++)
	{
		new_state[i] = state[i];
	}
	PermutationOnWords(new_state, 3);
	pS(new_state);
	bool flag1 = 1;
	for (int i = 0; i < length; i++)
	{
		if ((new_state[0] >> (63 - target_pos[i])) & 0x1) {
			flag1 = 0;
			break;
		}
	}
	if (flag1) {
		if (verify_ascon_4r_preimage(new_state, match_pos)) {
			for (int i = 0; i < 5; i++)
			{
				preimage_sols.push_back(state[i]);
			}
			/*displayState(state);
			displayState(new_state);*/
			return 1;
		}
		else
		{
			return 0;
		}
	}
	else
		return 0;
}

void attack_ascon_4r_preimage() {
	UINT32 blue_pos[4] = { 10, 20, 42, 52 };
	UINT32 red_pos[34] = { 0, 2, 4, 5, 6, 7, 9, 12, 14, 16, 19, 22, 24, 25, 26, 28, 29, 32, 34, 36, 37, 38, 39, 41, 44, 46, 48, 51, 54, 56, 57, 58, 60, 61 };
	UINT32 red_free_pos[14] = { 2, 4, 14, 16, 24, 25, 29, 34, 36, 46, 48, 56, 57, 61 };
	UINT32 red_free_index[14] = { 1, 2, 8, 9, 12, 13, 16, 18, 19, 25, 26, 29, 30, 33 };
	UINT32 red_dependent_pos[20] = { 0, 5, 6, 7, 9, 12, 19, 22, 26, 28, 32, 37, 38, 39, 41, 44, 51, 54, 58, 60 };
	UINT32 match_pos[4] = {18, 28, 50, 60};
	UINT32 target_match_pos[24] = { 16, 17, 18, 19, 28, 29, 30, 31, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63 };
	UINT32 sol_counter = 0;
	vector<UINT64> preimage_sols;

	clock_t start_time = clock();

	UINT64 InitialState[5] = { 0 };
	generate_ascon_4r_initial_state(InitialState);
	//displayState(InitialState);

	
	for (UINT32 e_value = 0; e_value < pow(2, 10); e_value++)
	{
		vector<bool> e(20, 0); // values of 20 expressions
		for (int i = 0; i < 10; i++)
		{
			e[i + 10] = (bool)((e_value >> (9 - i)) & 0x1);
		}

		// Solve constraints on red bits
		map<UINT64, vector<UINT64>> TableU;  // Hash table U, U[values of 10 unprocessed equations] : values of 34 red bits 
		TableU.clear();
		vector<vector<bool>> tmp_vr_sols;
		UINT32 counter = 0;
		for (UINT64 vr_free = 0; vr_free < pow(2, 14); vr_free++)
		{
			tmp_vr_sols.clear();
			vector<bool> vr(34, 0);
			for (int i = 0; i < 14; i++)
			{
				vr[red_free_index[i]] = (bool)((vr_free >> (13 - i)) & 0x1);
			}
			solve_TA(e, vr, tmp_vr_sols);
			if (!tmp_vr_sols.empty()) {
				//counter += tmp_vr_sols.size();
				for (auto vr_sol : tmp_vr_sols) {
					UINT64 unprocessed_cons = 0;
					UINT64 vr_sol_64 = 0;
					for (int i = 0; i < 34; i++)
					{
						vr_sol_64 |= (UINT64)(vr_sol[i]) << (33 - i);
					}
					//cout << vr_sol_64 << endl;
					get_unprocessed_cons(InitialState, vr_sol_64, red_pos, unprocessed_cons);
					//cout << unprocessed_cons << endl;
					TableU[unprocessed_cons].push_back(vr_sol_64);
				}
			}
		}
		//cout << counter << endl;
		//if (TableU.size() == 1)
		//	/*if (TableU.begin().size() != 0)*/
		//	cout << TableU.begin()->second.size() << endl;
		//cout << TableU.size() << endl;

		for (UINT64 u = 0; u < pow(2, 10); u++)
		{
			map<UINT64, vector<UINT64>> TableL;  // Hash table L for match
			if (!TableU[u].empty()) {
				/*cout << TableU[u].size() << endl;*/
				UINT64 FixedE = TableU[u][0];
				UINT64 TmpMP = 0;
				get_match_4_bits(InitialState, FixedE, red_pos, 34, match_pos, TmpMP);
				for (auto vr_sol : TableU[u]) {
					UINT64 match_red = 0;
					get_match_4_bits(InitialState, vr_sol, red_pos, 34, match_pos, match_red);
					TableL[match_red].push_back(vr_sol);
				}
				
				for (UINT64 vb = 0; vb < pow(2, 4); vb++)
				{
					UINT64 match_blue = 0;
					UINT64 tmpStateBlue[5] = { 0 };
					for (int i = 0; i < 5; i++)
					{
						tmpStateBlue[i] = InitialState[i];
					}
					for (int i = 0; i < 34; i++)
					{
						tmpStateBlue[0] ^= ((FixedE >> (33 - i)) & 0x1) << (63 - red_pos[i]);
					}
					//set_bits(tmpStateBlue, red_pos, FixedE, 34);
					get_match_4_bits(tmpStateBlue, vb, blue_pos, 4, match_pos, match_blue);
					if (!TableL[match_blue ^ TmpMP].empty()) {
						//cout << TableL[match_blue ^ TmpMP].size() << endl;
						for (auto value_red : TableL[match_blue ^ TmpMP]) {
							UINT64 tmpStateRB[5] = { 0 };
							for (int i = 0; i < 5; i++)
							{
								tmpStateRB[i] = InitialState[i];
							}
							for (int i = 0; i < 34; i++)
							{
								tmpStateRB[0] ^= ((value_red >> (33 - i)) & 0x1) << (63 - red_pos[i]);
							}
							for (int i = 0; i < 4; i++)
							{
								tmpStateRB[0] ^= ((vb >> (3 - i)) & 0x1) << (63 - blue_pos[i]);
							}
							/*PermutationOnWords(tmpStateRB, 3);
							pS(tmpStateRB);
							UINT64 match = 0;
							for (int i = 0; i < 4; i++)
							{
								match = (match << 1) ^ take_bit(tmpStateRB[0], match_pos[i]);
							}
							if (match != 0)
								cout << 1 << endl;*/
							/*set_bits(tmpStateRB, red_pos, value_red, 34);
							set_bits(tmpStateRB, blue_pos, vb, 4);*/
							if (find_ascon_4r_preimage(tmpStateRB, match_pos, preimage_sols)) {
								sol_counter += 1;
							}
						}
					}
				}
			}
		}
	}

	clock_t end_time = clock();
	cout << "\nThe time of finding all solutions satisfying the 24-bit partial target: " << (double)(end_time - start_time) / CLOCKS_PER_SEC << "s" << endl;
	cout << "The number of all finding solutions: 2^" << log(double(sol_counter)) / log(2.0) << endl;

	for (int i = 0; i < preimage_sols.size() / 5; i++)
	{
		cout << "++++ Solution " << i << ": ++++" << endl;
		UINT64 states[5] = { 0 };
		for (int j = 0; j < 5; j++)
		{
			states[j] = preimage_sols[5 * i + j];
		}
		displayState(states);
		PermutationOnWords(states, 3);
		pS(states);
		displayState(states);
		UINT64 target = 0;

		for (int i = 0; i < 24; i++)
		{
			target = (target << 1) ^ ((states[0] >> (63 - target_match_pos[i])) & 0x1);
		}
		printf("24-bit target: 0x%016llx\n\n", target);
	}
}

void test() {
	UINT64 states[5] = { 0 };
	generate_ascon_4r_initial_state(states);
}

void test_TA() {
	UINT32 red_free_index[14] = { 1, 2, 8, 9, 12, 13, 16, 18, 19, 25, 26, 29, 30, 33 };
	
	for (UINT64 e_v = 0; e_v < pow(2, 10); e_v++)
	{
		vector<bool> e(20, 0);
		vector<vector<bool>> Sols;
		map<vector<bool>, int> sol_counts;
		for (int i = 0; i < 20; i++) {
			e[i] = (bool)((e_v >> (19 - i)) & 0x1);
		}
		for (UINT64 vr_free = 0; vr_free < pow(2, 14); vr_free++) {
			Sols.clear();
			vector<bool> v(34, 0);
			for (int i = 0; i < 14; i++)
			{
				v[red_free_index[i]] = (bool)((vr_free >> (13 - i)) & 0x1);
			}
			solve_TA(e, v, Sols);
			if (!Sols.empty()) {
				for (auto sol : Sols) {
					if (sol_counts.find(sol) != sol_counts.end()) {
						sol_counts[sol] += 1;
						/*for (int j = 0; j < 34; j++)
						{
							cout << sol[j] << " ";
						}
						cout << endl;*/
					}
					else
						sol_counts[sol] = 1;
				}
			}
		}
		int sol_sum = 0;
		map<vector<bool>, int>::iterator iter;
		for (iter = sol_counts.begin(); iter != sol_counts.end(); iter++)
		{
			sol_sum += iter->second;
			if (iter->second != 1)
				cout << iter->second << endl;
			//cout << iter->second << endl;
		}
		cout << sol_sum << endl;
	}
	/*cout << Sols.size() << endl;
	for (int i = 0; i < 34; i++)
	{
		for (int j = 0; j < 34; j++)
		{
			cout << Sols[i][j] << " ";
		}
		cout << endl;
	}*/
}

int main() {
	//test();
	//test_TA();
	attack_ascon_4r_preimage();
	return 1;
}


