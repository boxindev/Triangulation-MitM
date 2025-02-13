#include"ascon.h"
#include <cstdio>


void PermutationOnWords(UINT64* state, int round)
{
    UINT32 i;
    for (i = 0; i < round; i++) {
        pS(state);
        pL(state);
    }
}

void pL(UINT64* A)
{
    A[0] = A[0] ^ ROR64(A[0], 19) ^ ROR64(A[0], 28);
    A[1] = A[1] ^ ROR64(A[1], 61) ^ ROR64(A[1], 39);
    A[2] = A[2] ^ ROR64(A[2], 1) ^ ROR64(A[2], 6);
    A[3] = A[3] ^ ROR64(A[3], 10) ^ ROR64(A[3], 17);
    A[4] = A[4] ^ ROR64(A[4], 7) ^ ROR64(A[4], 41);
}

void pS(UINT64* A)
{
    unsigned int x, y;
    UINT64 C[5];

    C[0] = (A[4] & A[1]) ^ (A[3]) ^ (A[2] & A[1]) ^ (A[2]) ^ (A[1] & A[0]) ^ (A[1]) ^ (A[0]);
    C[1] = (A[4]) ^ (A[3] & A[2]) ^ (A[3] & A[1]) ^ (A[3]) ^ (A[2] & A[1]) ^ (A[2]) ^ (A[1]) ^ (A[0]);
    C[2] = (A[4] & A[3]) ^ (A[4]) ^ (A[2]) ^ (A[1]) ^ (0xFFFFFFFFFFFFFFFF);
    C[3] = (A[4] & A[0]) ^ (A[4]) ^ (A[3] & A[0]) ^ (A[3]) ^ (A[2]) ^ (A[1]) ^ (A[0]);
    C[4] = (A[4] & A[1]) ^ (A[4]) ^ (A[3]) ^ (A[1] & A[0]) ^ (A[1]);
    for (x = 0; x < 5; x++)
        A[x] = C[x];
}

void displayState(UINT64* state)
{
    unsigned int i;
    for (i = 0; i < 5; i++)
    {
        printf("%016llx ", (state[i]));
        printf("\n");
    }
    printf("\n");
}

void set_bits(UINT64* state, UINT32* pos, UINT64 value, int pos_length) {
    for (int i = 0; i < pos_length; i++)
    {
        state[0] |= ((value >> (pos_length - i - 1)) & 0x1) << (63 - pos[i]);
    }
}

UINT64 take_bit(UINT64 state, UINT32 pos) {
    return (state >> (63 - pos)) & 0x1;
}
