#include"ascon.h"
#include <iostream>
#include <ctime>
#include <vector>
#include <map>
#include <cassert>
#include <cmath>

using namespace std;

void generate_ascon_3r_initial_state(UINT64* state){
	// 48-bit Conditions
	UINT32 cond0x[3] = { 7, 17, 26 };
	UINT32 cond1x[9] = { 0, 1, 4, 11, 13, 18, 22, 23, 25 };
	UINT32 condPlusx[12] = { 0, 5, 6, 7, 11, 14, 15, 17, 22, 25, 26, 27 };
	UINT32 gray_pos_1[7] = { 12, 16, 19, 30, 44, 48, 51 };
	for (int i = 0; i < 5; i++)
	{
		state[i] = { 0 };
	}
	state[0] = 2;
	for (int i = 0; i < 7; i++)
	{
		state[0] |= (UINT64(1) << (63 - gray_pos_1[i]));
	}
	for (int i = 0; i < 9; i++)
	{
		state[1] |= (UINT64(1) << (63 - cond1x[i]));
		state[1] |= (UINT64(1) << (63 - cond1x[i] - 32));
	}
	for (int i = 0; i < 12; i++)
	{
		state[3] |= (UINT64(1) << (63 - condPlusx[i]));
		state[3] |= (UINT64(1) << (63 - condPlusx[i] - 32));
	}

	//displayState(state);
}

void process_A_16_1(bool v0, bool v61, bool& v22, bool A_16_1) {
	v22 = v0 ^ v61 ^ 1 ^ A_16_1;	// deduce v22
}

void process_A_48_1(bool v29, bool v32, bool& v54, bool A_48_1) {
	v54 = v29 ^ v32 ^ 1 ^ A_48_1;	// deduce v54
}

int process_A_6_1(bool v32, bool v54, bool v60, bool A_6_1) {
	// deduce v6
	bool tmp1 = v60 ^ 1;
	bool tmp2 = v32 ^ v54 ^ A_6_1;
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

int process_A_38_1(bool v0, bool v22, bool v28, bool A_38_1) {
	// deduce v38
	bool tmp1 = v28 ^ 1;
	bool tmp2 = v0 ^ v22 ^ A_38_1;
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

void process_A_12_1(bool v15, bool v18, bool v60, bool& v37, bool A_12_1) {
	// deduce v37
	v37 = v15 ^ v18 ^ v60 ^ A_12_1;
}

void process_A_44_1(bool v28, bool v47, bool v50, bool& v5, bool A_44_1) {
	// deduce v5
	v5 = v28 ^ v47 ^ v50 ^ A_44_1;
}

void process_A_2_1(bool v5, bool v27, bool v38, bool v47, bool v50, bool& v25, bool A_2_1) {
	// deduce v25
	v25 = v27 ^ v38 ^ v47 ^ v50 ^ 1 ^ A_2_1;
}

void process_A_34_1(bool v6, bool v15, bool v18, bool v37, bool v59, bool& v57, bool A_34_1) {
	// deduce v57
	v57 = v6 ^ v15 ^ v18 ^ v59 ^ 1 ^ A_34_1;
}

void process_A_26_1(bool v22, bool v29, bool v32, bool v54, bool& v7, bool A_26_1) {
	// deduce v7
	v7 = (v29 & (v29 ^ v32 ^ v54)) ^ v22 ^ v32 ^ A_26_1;
}

void process_A_58_1(bool v0, bool v22, bool v54, bool v61, bool& v39, bool A_58_1) {
	// deduce v39
	v39 = (v61 & (v0 ^ v22 ^ v61)) ^ v0 ^ v54 ^ A_58_1;
}

void solve_TA(vector<bool> e, vector<bool> v, vector<vector<bool>>& Sols) {
	// 0  15 18 26 27 28 29 32 47 50 58 59 60 61  --> free
	// 0  1  2  3  4  5  6  7  8  9  10 11 12 13

	// 5  6  7  22 25 37 38 39 54 57  --> dependent
	// 0  1  2  3  4  5  6  7  8  9

	// 3  8  20 21 35 40 52 53  --> constant
	// 0  1  2  3  4  5  6  7
	
	bool v_dependent[10] = { 0 };
	process_A_16_1(v[0], v[13], v_dependent[3], e[0]);
	process_A_48_1(v[6], v[7], v_dependent[8], e[1]);
	int v_6 = process_A_6_1(v[7], v_dependent[8], v[12], e[2]);
	if (v_6 < 0)
		return;
	int v_38 = process_A_38_1(v[0], v_dependent[3], v[5], e[3]);
	if (v_38 < 0)
		return;
	vector<vector<bool>> a;
	if (v_6 == 2)
		a.push_back({ 0, 1 });
	else 
	{
		if (v_6 == 0)
			a.push_back({ 0 });
		else
			a.push_back({ 1 });
	}
	if (v_38 == 2)
		a.push_back({ 0, 1 });
	else
	{
		if (v_38 == 0)
			a.push_back({ 0 });
		else
			a.push_back({ 1 });
	}
	for (auto i : a[0]) { // v_6
		for (auto j : a[1]) { // v_38
			bool v_tmp[10] = { 0 };
			for (int n = 0; n < 10; n++)
			{
				v_tmp[n] = v_dependent[n];
			}
			v_tmp[1] = i;
			v_tmp[6] = j;
			process_A_12_1(v[1], v[2], v[12], v_tmp[5], e[4]);
			process_A_44_1(v[5], v[8], v[9], v_tmp[0], e[5]);
			process_A_2_1(v_tmp[0], v[4], v_tmp[6], v[8], v[9], v_tmp[4], e[6]);
			process_A_34_1(v_tmp[1], v[1], v[2], v_tmp[5], v[11], v_tmp[9], e[7]);
			process_A_26_1(v_tmp[3], v[6], v[7], v_tmp[8], v_tmp[2], e[8]);
			process_A_58_1(v[0], v_tmp[3], v_tmp[8], v[13], v_tmp[7], e[9]);
			vector<bool> v_tmp_vector(v_tmp, v_tmp + 10);
			Sols.push_back(v_tmp_vector);
		}
	}
}

void get_match_bits(UINT64* state, UINT64& match, UINT32 match_pos[]) {
	for (int i = 0; i < 14; i++)
	{
		match = (match << 1) ^ ((state[0] >> (63 - match_pos[i])) & 0x1);
	}
}

bool verify_ascon_3r_preimage(UINT64* state) {
	UINT64 match = 0;
	UINT32 match_pos[14] = { 2,3,6,12,16,17,26,34,35,38,44, 48, 49, 58 };
	get_match_bits(state, match, match_pos);
	if (match == 0)
		return 1;
	else
		return 0;
}

bool find_ascon_3r_preimage(UINT64* state, vector<UINT64>& preimage_sols) {
	int target_pos[18] = { 0, 1, 4, 5, 7, 8, 9, 10, 11, 13, 14, 15, 18, 19, 20, 21, 22, 23 };
	//int target_pos[] = { 0, 1, 4, 5, 7, 8 };
	int length = sizeof(target_pos) / sizeof(int);
	UINT64 new_state[5] = { 0 };
	for (int i = 0; i < 5; i++)
	{
		new_state[i] = state[i];
	}
	PermutationOnWords(new_state, 2);
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
		if (verify_ascon_3r_preimage(new_state)) {
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

void test_TA() {
	vector<bool> e(10, 0);
	vector<bool> v(14, 0);
	for (UINT64 e_value = 0; e_value < pow(2, 10); e_value++){
		vector<vector<bool>> Sols;
		Sols.clear();
		for (int i = 0; i < 10; i++){
			e[i] = (bool)((e_value >> (9 - i)) & 0x1);
		}
		for (UINT64 i = 0; i < pow(2, 14); i++){
			for (int j = 0; j < 14; j++)
			{
				v[j] = (bool)((i >> (13 - j)) & 0x1);
			}
			solve_TA(e, v, Sols);
			// if (Sols.size() == 1) {
			// 	for (int j = 0; j < 14; j++) {
			// 		cout << v[j] << " ";
			// 	}
			// 	cout << endl;
			// }
		}
		cout << Sols.size() << endl;
	}
	
	// for (int i = 0; i < Sols[0].size(); i++)
	// {
	// 	cout << Sols[0][i] << " ";
	// }
	// cout << endl;
	/*for (auto sol : Sols)
	{
		for (int i = 0; i < sol.size(); i++)
		{
			cout << sol[i];
		}
		cout << endl;
	}*/
}

void test() {
	UINT64 state[5] = { 0 };
	state[0] = 14;
	UINT64 match = 0;
	UINT32 match_pos[] = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 60, 61, 62, 63};
	get_match_bits(state, match, match_pos);
	printf("%016llx ", (match));
}

void attack_ascon_3r_preimage() {
	UINT32 blue_pos[14] = { 1, 4, 11, 13, 14, 17, 23, 33, 36, 43, 45, 46, 49, 55 };
	UINT32 red_pos[24] = { 0, 15, 18, 26, 27, 28, 29, 32, 47, 50, 58, 59, 60, 61, 5, 6, 7, 22, 25, 37, 38, 39, 54, 57  };
	UINT32 red_free_pos[14] = { 0, 15, 18, 26, 27, 28, 29, 32, 47, 50, 58, 59, 60, 61 };
	UINT32 red_dependent_pos[10] = { 5, 6, 7, 22, 25, 37, 38, 39, 54, 57 };
	//UINT32 gray_pos[24] = { 2, 3, 8, 9, 10, 12, 16, 19, 20, 21, 24, 30, 31, 34, 35, 40, 41, 42, 44, 48, 51, 52, 53, 56 };
	UINT32 match_pos[14] = { 2,3,6,12,16,17,26,34,35,38,44, 48, 49, 58 };
	UINT32 target_pos[18] = { 0, 1, 4, 5, 7, 8, 9, 10, 11, 13, 14, 15, 18, 19, 20, 21, 22, 23 };
	UINT32 target_match_pos[32] = { 0, 1, 2,3,4, 5, 6,7, 8, 9, 10, 11, 12,13, 14, 15, 16,17,18, 19, 20, 21, 22, 23,26,34,35,38,44, 48, 49, 58 };
	UINT32 sol_counter = 0;
	vector<UINT64> preimage_sols;

	clock_t start_time = clock();

	UINT64 InitialState[5] = { 0 };
	generate_ascon_3r_initial_state(InitialState);

	vector<bool> e; // values of 10 expressions
	for (UINT32 expression_value = 0; expression_value < pow(2, 8); expression_value++)
	{
		e.clear();
		UINT32 counter = 0;
		for (int i = 0; i < 10; i++)
		{
			e.push_back((expression_value >> (9 - i)) & 0x1);
		}

		map<UINT64, vector<UINT64>> TableL;  // Hash Table L

		// red propagation
		UINT64 FixedE = 0;
		UINT64 TmpMP = 0;
		vector<bool> vr_free;
		vector<vector<bool>> Sols;
		UINT64 TmpStateRed[5] = { 0 };
		for (UINT64 vr = 0; vr < pow(2, 14); vr++)
		{
			Sols.clear();
			vr_free.clear();
			for (int i = 0; i < 14; i++)
			{
				vr_free.push_back((vr >> (13 - i)) & 0x1);
			}
			solve_TA(e, vr_free, Sols);
			if (!Sols.empty()) {
				for (auto sol : Sols) {
					UINT64 vr_dependet = 0;
					for (int i = 0; i < 10; i++)
					{
						vr_dependet = (vr_dependet << 1) ^ (sol[i]);
					}
					for (int i = 0; i < 5; i++)
					{
						TmpStateRed[i] = InitialState[i];
					}
					set_bits(TmpStateRed, red_free_pos, vr, 14);
					set_bits(TmpStateRed, red_dependent_pos, vr_dependet, 10);
					PermutationOnWords(TmpStateRed, 2);
					pS(TmpStateRed);
					//TmpStateRed[0] ^= TmpMP;
					UINT64 match_red = 0;
					get_match_bits(TmpStateRed, match_red, match_pos);
					UINT64 vr_total = (vr << 10) | vr_dependet;
					TableL[match_red].push_back(vr_total);
				}
			}
		}
		if (!TableL.empty()) {
			for (TmpMP = 0; TmpMP < pow(2, 14); TmpMP++)
			{
				if (TableL.find(TmpMP) != TableL.end()) {
					FixedE = TableL[TmpMP][0];
					break;
				}
			}
			// blue propagation
			UINT64 TmpStateBlue[5] = { 0 };
			for (UINT64 vb = 0; vb < pow(2, 14); vb++)
			{
				for (int i = 0; i < 5; i++)
				{
					TmpStateBlue[i] = InitialState[i];
				}
				set_bits(TmpStateBlue, blue_pos, vb, 14);
				set_bits(TmpStateBlue, red_pos, FixedE, 24);
				PermutationOnWords(TmpStateBlue, 2);
				pS(TmpStateBlue);

				UINT64 match_blue = 0;
				get_match_bits(TmpStateBlue, match_blue, match_pos);
				if (TableL.find(match_blue ^ TmpMP) != TableL.end()) {
					UINT64 TmpStateRB[5] = { 0 };
					for (auto value_r : TableL[match_blue ^ TmpMP]) {
						counter += 1;
						for (int i = 0; i < 5; i++)
						{
							TmpStateRB[i] = InitialState[i];
						}
						set_bits(TmpStateRB, blue_pos, vb, 14);
						set_bits(TmpStateRB, red_pos, value_r, 24);
						/*PermutationOnWords(TmpStateRB, 2);
						pS(TmpStateRB);
						if (verify_ascon_3r_preimage(TmpStateRB) == 1)
							counter += 1;*/
						if (find_ascon_3r_preimage(TmpStateRB, preimage_sols)) {
							sol_counter += 1;
						}
					}
				}
			}
		}
		else
		{
			continue;
		}
		//cout << "episodes " << expression_value << ": " << counter << " pairs pass the filter." << endl;
		//cout << "episodes " << expression_value << ": 2^" << log(double(counter)) / log(2.0) << " pairs pass the filter." << endl;
	}

	clock_t end_time = clock();
	cout << "\nThe time of finding all solutions satisfying the 32-bit partial target: " << (double)(end_time - start_time) / CLOCKS_PER_SEC << "s" << endl;
	cout << "The number of all finding solutions: 2^" << log(double(sol_counter)) / log(2.0) << endl;
	cout << endl;
	for (int i = 0; i < preimage_sols.size() / 5; i++)
	{
		cout << "++++ Solution " << i << ": ++++" << endl;
		UINT64 states[5] = { 0 };
		for (int j = 0; j < 5; j++)
		{
			states[j] = preimage_sols[5 * i + j];
		}
		displayState(states);
		PermutationOnWords(states, 2);
		pS(states);
		displayState(states);
		UINT64 target = 0;

		for (int i = 0; i < 32; i++)
		{
			target = (target << 1) ^ ((states[0] >> (63 - target_match_pos[i])) & 0x1);
		}
		printf("32-bit target: 0x%016llx\n\n", target);
	}
}

int main() {
	// test_TA();
	//test();
	attack_ascon_3r_preimage();
	return 0;
}