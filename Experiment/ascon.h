#ifndef ASCON_H_
#define ASCON_H_

typedef unsigned char UINT8;
typedef unsigned long int UINT32;
typedef unsigned long long int UINT64;

#define ROL64(a, offset) ((offset != 0) ? ((((UINT64)a) << offset) ^ (((UINT64)a) >> (64-offset))) : a)
#define ROR64(a, offset) ((offset != 0) ? ((((UINT64)a) >> offset) ^ (((UINT64)a) << (64-offset))) : a)

void PermutationOnWords(UINT64* state, int round);
void pL(UINT64* A);
void pS(UINT64* A);
void displayState(UINT64* state);
void set_bits(UINT64* state, UINT32* pos, UINT64 value, int pos_length);
UINT64 take_bit(UINT64 state, UINT32 pos);

#endif // !ASCON_H_
