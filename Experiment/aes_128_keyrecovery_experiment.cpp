#include <iostream>
#include <stdio.h>
#include <stdint.h>
#include <string.h>
#include <stdlib.h>  // rand(), srand()
#include <time.h>
#include <stdbool.h>
#include <vector>
#include <map>
#include <cmath>

using namespace std;


#define Nb 4
#define BLOCKLEN 16
#define Nk 8
#define Nr 10
#define keyExpSize 176


static const uint8_t sbox[256] = {
    //0     1    2      3     4    5     6     7      8    9     A      B    C     D     E     F
    0x63, 0x7c, 0x77, 0x7b, 0xf2, 0x6b, 0x6f, 0xc5, 0x30, 0x01, 0x67, 0x2b, 0xfe, 0xd7, 0xab, 0x76,
    0xca, 0x82, 0xc9, 0x7d, 0xfa, 0x59, 0x47, 0xf0, 0xad, 0xd4, 0xa2, 0xaf, 0x9c, 0xa4, 0x72, 0xc0,
    0xb7, 0xfd, 0x93, 0x26, 0x36, 0x3f, 0xf7, 0xcc, 0x34, 0xa5, 0xe5, 0xf1, 0x71, 0xd8, 0x31, 0x15,
    0x04, 0xc7, 0x23, 0xc3, 0x18, 0x96, 0x05, 0x9a, 0x07, 0x12, 0x80, 0xe2, 0xeb, 0x27, 0xb2, 0x75,
    0x09, 0x83, 0x2c, 0x1a, 0x1b, 0x6e, 0x5a, 0xa0, 0x52, 0x3b, 0xd6, 0xb3, 0x29, 0xe3, 0x2f, 0x84,
    0x53, 0xd1, 0x00, 0xed, 0x20, 0xfc, 0xb1, 0x5b, 0x6a, 0xcb, 0xbe, 0x39, 0x4a, 0x4c, 0x58, 0xcf,
    0xd0, 0xef, 0xaa, 0xfb, 0x43, 0x4d, 0x33, 0x85, 0x45, 0xf9, 0x02, 0x7f, 0x50, 0x3c, 0x9f, 0xa8,
    0x51, 0xa3, 0x40, 0x8f, 0x92, 0x9d, 0x38, 0xf5, 0xbc, 0xb6, 0xda, 0x21, 0x10, 0xff, 0xf3, 0xd2,
    0xcd, 0x0c, 0x13, 0xec, 0x5f, 0x97, 0x44, 0x17, 0xc4, 0xa7, 0x7e, 0x3d, 0x64, 0x5d, 0x19, 0x73,
    0x60, 0x81, 0x4f, 0xdc, 0x22, 0x2a, 0x90, 0x88, 0x46, 0xee, 0xb8, 0x14, 0xde, 0x5e, 0x0b, 0xdb,
    0xe0, 0x32, 0x3a, 0x0a, 0x49, 0x06, 0x24, 0x5c, 0xc2, 0xd3, 0xac, 0x62, 0x91, 0x95, 0xe4, 0x79,
    0xe7, 0xc8, 0x37, 0x6d, 0x8d, 0xd5, 0x4e, 0xa9, 0x6c, 0x56, 0xf4, 0xea, 0x65, 0x7a, 0xae, 0x08,
    0xba, 0x78, 0x25, 0x2e, 0x1c, 0xa6, 0xb4, 0xc6, 0xe8, 0xdd, 0x74, 0x1f, 0x4b, 0xbd, 0x8b, 0x8a,
    0x70, 0x3e, 0xb5, 0x66, 0x48, 0x03, 0xf6, 0x0e, 0x61, 0x35, 0x57, 0xb9, 0x86, 0xc1, 0x1d, 0x9e,
    0xe1, 0xf8, 0x98, 0x11, 0x69, 0xd9, 0x8e, 0x94, 0x9b, 0x1e, 0x87, 0xe9, 0xce, 0x55, 0x28, 0xdf,
    0x8c, 0xa1, 0x89, 0x0d, 0xbf, 0xe6, 0x42, 0x68, 0x41, 0x99, 0x2d, 0x0f, 0xb0, 0x54, 0xbb, 0x16 };
  
  static const uint8_t rsbox[256] = {
    0x52, 0x09, 0x6a, 0xd5, 0x30, 0x36, 0xa5, 0x38, 0xbf, 0x40, 0xa3, 0x9e, 0x81, 0xf3, 0xd7, 0xfb,
    0x7c, 0xe3, 0x39, 0x82, 0x9b, 0x2f, 0xff, 0x87, 0x34, 0x8e, 0x43, 0x44, 0xc4, 0xde, 0xe9, 0xcb,
    0x54, 0x7b, 0x94, 0x32, 0xa6, 0xc2, 0x23, 0x3d, 0xee, 0x4c, 0x95, 0x0b, 0x42, 0xfa, 0xc3, 0x4e,
    0x08, 0x2e, 0xa1, 0x66, 0x28, 0xd9, 0x24, 0xb2, 0x76, 0x5b, 0xa2, 0x49, 0x6d, 0x8b, 0xd1, 0x25,
    0x72, 0xf8, 0xf6, 0x64, 0x86, 0x68, 0x98, 0x16, 0xd4, 0xa4, 0x5c, 0xcc, 0x5d, 0x65, 0xb6, 0x92,
    0x6c, 0x70, 0x48, 0x50, 0xfd, 0xed, 0xb9, 0xda, 0x5e, 0x15, 0x46, 0x57, 0xa7, 0x8d, 0x9d, 0x84,
    0x90, 0xd8, 0xab, 0x00, 0x8c, 0xbc, 0xd3, 0x0a, 0xf7, 0xe4, 0x58, 0x05, 0xb8, 0xb3, 0x45, 0x06,
    0xd0, 0x2c, 0x1e, 0x8f, 0xca, 0x3f, 0x0f, 0x02, 0xc1, 0xaf, 0xbd, 0x03, 0x01, 0x13, 0x8a, 0x6b,
    0x3a, 0x91, 0x11, 0x41, 0x4f, 0x67, 0xdc, 0xea, 0x97, 0xf2, 0xcf, 0xce, 0xf0, 0xb4, 0xe6, 0x73,
    0x96, 0xac, 0x74, 0x22, 0xe7, 0xad, 0x35, 0x85, 0xe2, 0xf9, 0x37, 0xe8, 0x1c, 0x75, 0xdf, 0x6e,
    0x47, 0xf1, 0x1a, 0x71, 0x1d, 0x29, 0xc5, 0x89, 0x6f, 0xb7, 0x62, 0x0e, 0xaa, 0x18, 0xbe, 0x1b,
    0xfc, 0x56, 0x3e, 0x4b, 0xc6, 0xd2, 0x79, 0x20, 0x9a, 0xdb, 0xc0, 0xfe, 0x78, 0xcd, 0x5a, 0xf4,
    0x1f, 0xdd, 0xa8, 0x33, 0x88, 0x07, 0xc7, 0x31, 0xb1, 0x12, 0x10, 0x59, 0x27, 0x80, 0xec, 0x5f,
    0x60, 0x51, 0x7f, 0xa9, 0x19, 0xb5, 0x4a, 0x0d, 0x2d, 0xe5, 0x7a, 0x9f, 0x93, 0xc9, 0x9c, 0xef,
    0xa0, 0xe0, 0x3b, 0x4d, 0xae, 0x2a, 0xf5, 0xb0, 0xc8, 0xeb, 0xbb, 0x3c, 0x83, 0x53, 0x99, 0x61,
    0x17, 0x2b, 0x04, 0x7e, 0xba, 0x77, 0xd6, 0x26, 0xe1, 0x69, 0x14, 0x63, 0x55, 0x21, 0x0c, 0x7d };

static const uint8_t Rcon[11] = {0x8d, 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80, 0x1b, 0x36};
static uint8_t RoundKey[keyExpSize] = {0};


uint8_t take8(uint32_t x, int i){
    uint8_t out = (x>>(i*8)) & 0xff;
    return out;
}

uint8_t mul2(uint8_t x){
    uint8_t out = ((x<<1) ^ (((x>>7) & 1) * 0x1b));
    return out;
}

uint8_t mul3(uint8_t x){
    uint8_t out = mul2(x) ^ x;
    return out;
}

uint8_t mul4(uint8_t x){
    uint8_t out = mul2(mul2(x));
    return out;
}


uint8_t mul8(uint8_t x){
    uint8_t out = mul2(mul2(mul2(x)));
    return out;
}

//乘246： 0x03的逆
uint8_t mulF6(uint8_t x){
    uint8_t out = mul8(mul8(mul3(x))) ^ mul4(mul4(mul3(x))) ^ mul2(mul3(x));
    return out;
}
//乘141： 0x02的逆
uint8_t mul8D(uint8_t x){
   uint8_t out = mul8(mul8(mul2(x))) ^ (mul4(mul3(x))) ^ (x);
   return out;
}


uint8_t mul9(uint8_t x){
    uint8_t out = mul8(x) ^ x;
    return out;
}

uint8_t mulB(uint8_t x){
    uint8_t out = mul8(x) ^ mul2(x) ^ x;
    return out;
}

uint8_t mulD(uint8_t md_in){
    uint8_t out = mul8(md_in) ^ mul4(md_in) ^ md_in;
    return out;
}

uint8_t mulE(uint8_t me_in){
    uint8_t out = mul8(me_in) ^ mul4(me_in) ^ mul2(me_in);
    return out;
}

uint8_t mulE1(uint8_t x){
    uint8_t out = mul8(mul8(mul3(x))) ^ mul4(mul4(mul2(x))) ^ x;
    return out;
}

uint8_t mul4F(uint8_t x){
    uint8_t out = mul8(mul8(x)) ^ mul4(mul3(x)) ^ mul3(x);
    return out;
}

uint8_t mulE5(uint8_t x){
    uint8_t out = mul8(mul8(mul3(x))) ^ mul4(mul4(mul2(x))) ^ mul4(x) ^ (x);
    return out;
}


uint8_t XTIME(uint8_t x) {  
	return ((x << 1) ^ ((x & 0x80) ? 0x1b : 0x00));
}

uint8_t multiply(uint8_t a, uint8_t b) {
	unsigned char temp[8] = { a };
    uint8_t tempmultiply = 0x00;
	int i;
	for (i = 1; i < 8; i++) {
		temp[i] = XTIME(temp[i - 1]);
	}
	tempmultiply = (b & 0x01) * a;
	for (i = 1; i <= 7; i++) {
		tempmultiply ^= (((b >> i) & 0x01) * temp[i]);
	}
	return tempmultiply;
}

static void KeyExpansion_S2S(uint8_t *sKey, uint8_t *sKey_new)
{
    sKey_new[0] = sKey[13] ^ sbox[sKey[12]];
    sKey_new[1] = sKey[14];
    sKey_new[2] = sKey[15];
    sKey_new[3] = sKey[12];

    sKey_new[4] = sKey[1] ^ sbox[sKey[0]];
    sKey_new[5] = sKey[2];
    sKey_new[6] = sKey[3];
    sKey_new[7] = sKey[0];

    sKey_new[8] = sKey[5] ^ sbox[sKey[4]];
    sKey_new[9] = sKey[6];
    sKey_new[10] = sKey[7];
    sKey_new[11] = sKey[4];

    sKey_new[12] = sKey[9] ^ sbox[sKey[8]];
    sKey_new[13] = sKey[10];
    sKey_new[14] = sKey[11];
    sKey_new[15] = sKey[8];
}

static void KeyExpansion_S2S_inv(uint8_t *sKey, uint8_t *sKey_new)
{
    sKey_new[0] = sKey[7];
    sKey_new[1] = sKey[4] ^ sbox[sKey[7]];
    sKey_new[2] = sKey[5];
    sKey_new[3] = sKey[6];

    sKey_new[4] = sKey[11];
    sKey_new[5] = sKey[8] ^ sbox[sKey[11]];
    sKey_new[6] = sKey[9];
    sKey_new[7] = sKey[10];

    sKey_new[8] = sKey[15];
    sKey_new[9] = sKey[12] ^ sbox[sKey[15]];
    sKey_new[10] = sKey[13];
    sKey_new[11] = sKey[14];

    sKey_new[12] = sKey[3];
    sKey_new[13] = sKey[0] ^ sbox[sKey[3]];
    sKey_new[14] = sKey[1];
    sKey_new[15] = sKey[2];
}


static void KeyExpansion_S2RK(uint8_t *sKey, int round)
{
    RoundKey[round * 16 + 0] = sKey[3] ^ sKey[6] ^ sKey[9] ^ sKey[12];
    RoundKey[round * 16 + 1] = sKey[2] ^ sKey[5] ^ sKey[8] ^ sKey[15];
    RoundKey[round * 16 + 2] = sKey[1] ^ sKey[4] ^ sKey[11] ^ sKey[14];
    RoundKey[round * 16 + 3] = sKey[0] ^ sKey[7] ^ sKey[10] ^ sKey[13];

    RoundKey[round * 16 + 4] = sKey[6] ^ sKey[12];
    RoundKey[round * 16 + 5] = sKey[2] ^ sKey[8];
    RoundKey[round * 16 + 6] = sKey[4] ^ sKey[14];
    RoundKey[round * 16 + 7] = sKey[0] ^ sKey[10];

    RoundKey[round * 16 + 8] = sKey[3] ^ sKey[12];
    RoundKey[round * 16 + 9] = sKey[8] ^ sKey[15];
    RoundKey[round * 16 + 10] = sKey[4] ^ sKey[11];
    RoundKey[round * 16 + 11] = sKey[0] ^ sKey[7];

    RoundKey[round * 16 + 12] = sKey[12];
    RoundKey[round * 16 + 13] = sKey[8];
    RoundKey[round * 16 + 14] = sKey[4];
    RoundKey[round * 16 + 15] = sKey[0];
}


void subByte16(uint8_t *RoundText){
    for(int i=0;i<16;i++)
        RoundText[i]=sbox[RoundText[i]];
}
void InvSubByte16(uint8_t *RoundText){
    for(int i=0;i<16;i++)
        RoundText[i]=rsbox[RoundText[i]];
}
void ShiftRow16(uint8_t *RoundText) {
    uint8_t t;
    //row1
    t=RoundText[1]; RoundText[1]=RoundText[5]; RoundText[5]=RoundText[9]; RoundText[9]=RoundText[13]; RoundText[13]=t;
    //row2
    t=RoundText[2];RoundText[2]=RoundText[10];RoundText[10]=t;
    t=RoundText[6];RoundText[6]=RoundText[14];RoundText[14]=t;
    //row3
    t=RoundText[15];RoundText[15]=RoundText[11];RoundText[11]=RoundText[7]; RoundText[7]=RoundText[3]; RoundText[3]=t;
}

void InvShiftRow16(uint8_t *RoundText){
    uint8_t t;
    //row1
    t=RoundText[13]; RoundText[13]=RoundText[9];RoundText[9]=RoundText[5]; RoundText[5]=RoundText[1]; RoundText[1]=t;
    //row2
    t=RoundText[2],RoundText[2]=RoundText[10];RoundText[10]=t;
    t=RoundText[6];RoundText[6]=RoundText[14];RoundText[14]=t;
    //row3
    t=RoundText[3]; RoundText[3]=RoundText[7];RoundText[7]=RoundText[11]; RoundText[11]=RoundText[15]; RoundText[15]=t;
}
void MixColumn16(uint8_t* RoundText){
    uint8_t temp[4];
    for(int i=0;i<4;i++){
        temp[0]=mul2(RoundText[4*i])^mul3(RoundText[1+4*i])^RoundText[2+4*i]^RoundText[3+4*i];
        temp[1]=RoundText[4*i]^mul2(RoundText[1+4*i])^mul3(RoundText[2+4*i])^RoundText[3+4*i];
        temp[2]=RoundText[4*i]^RoundText[1+4*i]^mul2(RoundText[2+4*i])^mul3(RoundText[3+4*i]);
        temp[3]=mul3(RoundText[4*i])^RoundText[1+4*i]^RoundText[2+4*i]^mul2(RoundText[3+4*i]);
        RoundText[4*i]=temp[0];
        RoundText[1+4*i]=temp[1];
        RoundText[2+4*i]=temp[2];
        RoundText[3+4*i]=temp[3];
    }
}

void InvMixColumn16(uint8_t* RoundText) {
    uint8_t temp[4];
    for(int i=0;i<4;i++){
        temp[0]=mulE(RoundText[4*i])^mulB(RoundText[1+4*i])^mulD(RoundText[2+4*i])^mul9(RoundText[3+4*i]);
        temp[1]=mul9(RoundText[4*i])^mulE(RoundText[1+4*i])^mulB(RoundText[2+4*i])^mulD(RoundText[3+4*i]);
        temp[2]=mulD(RoundText[4*i])^mul9(RoundText[1+4*i])^mulE(RoundText[2+4*i])^mulB(RoundText[3+4*i]);
        temp[3]=mulB(RoundText[4*i])^mulD(RoundText[1+4*i])^mul9(RoundText[2+4*i])^mulE(RoundText[3+4*i]);
        RoundText[4*i]=temp[0];
        RoundText[1+4*i]=temp[1];
        RoundText[2+4*i]=temp[2];
        RoundText[3+4*i]=temp[3];
    }
}
void AddRoundKey16(uint8_t* RoundText,int round){
    for(int i=0;i<16;i++)
        RoundText[i]^=RoundKey[round*16+i];
}

bool if_sol(uint8_t s14, uint8_t target){
    for(uint32_t x = 0; x <= 0xff; x++){
        uint8_t tmp = take8(x,0);
        if ((mul2(sbox[s14 ^ tmp]) ^ tmp) == target){
            return true;
        }
    }
    return false;
}

uint8_t compute_s(uint8_t s14, uint8_t target){
    for(uint32_t x = 0; x <= 0xff; x++){
        uint8_t tmp = take8(x,0);
        if ((mul2(sbox[s14 ^ tmp]) ^ tmp) == target){
            return x;
        }
    }
    return 0;
}

void aes_128_enc(uint8_t ciphertext[16], uint8_t sKey[32], int rounds, const uint8_t plainText[16], int skey_start_round, int skey_round){
    uint8_t state[16] = {0};
    uint8_t tmpS[16];
    uint8_t newS[16];

    for(int i = 0; i < 16; i++){
        newS[i] = sKey[i];
    }
    for(int i = skey_start_round; i > -1; i--) {
        KeyExpansion_S2RK(newS,i);
        KeyExpansion_S2S_inv(newS,tmpS);
        for(int j = 0; j < 16; j++){
            newS[j] = tmpS[j];
        }
    }
    for(int i = 0; i < 16; i++){
        newS[i] = sKey[i];
    }
    for(int i = skey_start_round+1; i < skey_round ; i++) {
        KeyExpansion_S2S(newS,tmpS);
        for(int j = 0; j < 16; j++){
            newS[j] = tmpS[j];
        }
        KeyExpansion_S2RK(newS,i);
    }
    for(int i = 0; i < 16; i++){
        state[i] = RoundKey[i] ^ plainText[i];
    }
    for(int i = 1; i < rounds+1; i++){
        subByte16(state);
        ShiftRow16(state);
        MixColumn16(state);
        AddRoundKey16(state,i);
    }
    for(int i = 0; i < 16; i++){
        ciphertext[i] = state[i];
    }
}



void aes_128_keyrecovery_attack() {
    int rounds = 4;
    int frounds = 2;
    int brounds = rounds - frounds;
    int skey_start_round = 2;
    int skey_round = 5;
    int red_index[6] = {0, 1, 4, 7, 10, 13};

    int red_number = 6;
    int MatchNum = 0;
    vector<uint8_t> recovery_key;

    clock_t start_time = clock();
    clock_t end_time = clock();

    
    uint8_t sKey[16] = {0};
    uint8_t plainText[16] = {0};
    uint8_t cipherText[16] = {0x0b, 0x4f, 0x82, 0xfc, 0xa4, 0x6c, 0xd1, 0x9d, 0x5b, 0xb1, 0xbc, 0x1f, 0x28, 0x11, 0x16, 0xf4};
    uint8_t e1 = 0x75;
    uint8_t e2 = 0x00;
    uint8_t e3 = 0xc6;
    printf("The experiment is conducted under:\n");
    printf("Plaintext:\n");
    for(int i = 0; i < 16; i++){
        printf("0x%02x, ", plainText[i]);
    }printf("\n");
    printf("S^{2}:\n");
    for(int i = 0; i < 16; i++){
        printf("0x%02x, ", sKey[i]);
    }printf("\n");
    printf("CipherText:\n");
    for(int i = 0; i < 16; i++){
        printf("0x%02x, ", cipherText[i]);
    }printf("\n");
    printf("e1 = 0x%02x\n", e1);
    printf("e2 = 0x%02x\n", e2);
    printf("e3 = 0x%02x\n", e3);
    

    map<uint32_t, vector< vector<uint8_t>>> RedInitState;
    for(uint32_t i = 0; i <= 0xffffff; i++){
        sKey[7] = take8(i,0);
        sKey[10] = take8(i,1);
        sKey[13] = take8(i,2);

        sKey[4] = rsbox[mulF6(e1 ^ sbox[sKey[10] ^ sKey[13]])] ^ sKey[14] ^ sbox[sKey[7]];
        uint8_t tmpValue = e2 ^ mul3(sbox[sKey[10]]) ^ sKey[4] ^ sbox[sKey[7]] ^ sKey[11] ^ sKey[14];
        if (!if_sol(sKey[14],tmpValue)){
            continue;
        }
        sKey[1] = compute_s(sKey[14],tmpValue);
        sKey[0] = rsbox[mul8D(e3 ^ sKey[7] ^ sKey[13])] ^ sKey[10] ^ sKey[13] ^ sbox[sKey[3]] ^ sKey[7] ^ sbox[sKey[6]]; 
        
        vector<uint8_t> Statered;
        for(int j = 0; j < red_number; j++){
            Statered.push_back(sKey[red_index[j]]);
        }
        
        uint8_t SR_3_0  = mulE(sKey[9] ^ sbox[sKey[8]] ^ sKey[12] ^ sKey[3] ^ sKey[6] ^ sbox[sKey[5] ^ sbox[sKey[4]]]) ^ mulB(sKey[8] ^ sKey[2] ^ sbox[sKey[1] ^ sbox[sKey[0]]] ^ sKey[5] ^ sbox[sKey[4]]) ^ mulD(sKey[11] ^ sKey[14] ^ sbox[sKey[13] ^ sbox[sKey[12]]] ^ sKey[1] ^ sbox[sKey[0]] ^ sKey[4])^mul9(sKey[10] ^ sbox[sKey[9] ^ sbox[sKey[8]]] ^ sKey[13] ^ sbox[sKey[12]] ^ sKey[0] ^ sKey[7]);
        uint8_t SR_2_2 = mul9(rsbox[mul9(sKey[6] ^ sbox[sKey[5] ^ sbox[sKey[4]]] ^ cipherText[12]) ^ mulE(sKey[2] ^ sbox[sKey[1] ^ sbox[sKey[0]]] ^ cipherText[13]) ^ mulB(sKey[14] ^ sbox[sKey[13] ^ sbox[sKey[12]]] ^ cipherText[14]) ^ mulD(sKey[10] ^ sbox[sKey[9] ^ sbox[sKey[8]]] ^ cipherText[15])] ^ sKey[2] ^ sKey[5] ^ sbox[sKey[4]] ^ sKey[8]) ^ mulE(rsbox[mulD(sKey[9] ^ sbox[sKey[8]] ^ sKey[6] ^ sbox[sKey[5] ^ sbox[sKey[4]]] ^ cipherText[8]) ^ mul9(sKey[2] ^ sbox[sKey[1] ^ sbox[sKey[0]]] ^ sKey[5] ^ sbox[sKey[4]] ^ cipherText[9]) ^ mulE(sKey[14] ^ sbox[sKey[13] ^ sbox[sKey[12]]] ^ sKey[1] ^ sbox[sKey[0]] ^ cipherText[10]) ^ mulB(sKey[10] ^ sbox[sKey[9] ^ sbox[sKey[8]]] ^ sKey[13] ^ sbox[sKey[12]] ^ cipherText[11])] ^ sKey[14] ^ sKey[1] ^ sbox[sKey[0]] ^ sKey[4] ^ sKey[11]) ^ mulB(rsbox[mulB(sKey[12] ^ sKey[6] ^ sbox[sKey[5] ^ sbox[sKey[4]]] ^ cipherText[4]) ^ mulD(sKey[8] ^ sKey[2] ^ sbox[sKey[1] ^ sbox[sKey[0]]] ^ cipherText[5]) ^ mul9(sKey[14] ^ sbox[sKey[13] ^ sbox[sKey[12]]] ^ sKey[4] ^ cipherText[6]) ^ mulE(sKey[10] ^ sbox[sKey[9] ^ sbox[sKey[8]]] ^ sKey[0] ^ cipherText[7])] ^ sKey[13] ^ sbox[sKey[12]] ^ sKey[0] ^ sKey[7] ^ sKey[10]);

        uint32_t red_consume = 0;
        red_consume = red_consume ^ SR_3_0;
        red_consume = (red_consume << 8) ^ SR_2_2;

        if(RedInitState.find(red_consume) != RedInitState.end())
        {
            vector< vector<uint8_t>> ttmp =RedInitState[red_consume];
            ttmp.push_back(Statered);
            RedInitState[red_consume] = ttmp;
        } else {
            vector< vector<uint8_t>> ttmp;
            ttmp.push_back(Statered);
            RedInitState[red_consume] = ttmp;
        }        
    }

    for(uint32_t mi = 0; mi < 0x10000; mi++){ 
        if(RedInitState.find(mi) != RedInitState.end()) {
            vector< vector<uint8_t>> redset =RedInitState[mi];
            uint8_t fix_r[6] = {0};
            for(int i = 0; i < red_number; i++){
                fix_r[i] = redset[0][i];
            }
            map<uint8_t, vector<uint8_t>> TableBlue;
            for(uint32_t blue =0; blue < 0x100; blue++){
                uint8_t tmpS[16];
                uint8_t newS[16];
                for(int i = 0; i < 16; i++){
                    sKey[i] = 0;
                }
                sKey[15] = take8(blue,0);
                for(int i = 0; i < red_number; i++){
                    sKey[red_index[i]] = fix_r[i];
                } 

                for(int i = 0; i < 16; i++){
                    newS[i] = sKey[i];
                }
                for(int i = skey_start_round; i > -1; i--) {
                    KeyExpansion_S2RK(newS,i);
                    KeyExpansion_S2S_inv(newS,tmpS);
                    for(int j = 0; j < 16; j++){
                        newS[j] = tmpS[j];
                    }
                }
                for(int i = 0; i < 16; i++){
                    newS[i] = sKey[i];
                }
                for(int i = skey_start_round+1; i < skey_round ; i++) {
                    KeyExpansion_S2S(newS,tmpS);
                    for(int j = 0; j < 16; j++){
                        newS[j] = tmpS[j];
                    }
                    KeyExpansion_S2RK(newS,i);
                }
                uint8_t state[16] = {0};
                for(int i = 0; i < 16; i++){
                    state[i] = RoundKey[rounds*16+i] ^ cipherText[i];
                }
                {
                    InvMixColumn16(state);
                    InvShiftRow16(state); 
                    InvSubByte16(state);
                    AddRoundKey16(state,3);
                    InvMixColumn16(state);
                    InvShiftRow16(state); 
                    InvSubByte16(state);
                }

                uint8_t tmpBlueMatch = 0; 
                tmpBlueMatch = state[10];
                

                for(int i = 0; i < 16; i++){
                    state[i] = RoundKey[i] ^ plainText[i];
                }
                {
                    subByte16(state);
                    ShiftRow16(state);
                    MixColumn16(state);
                    AddRoundKey16(state,1);
                    subByte16(state);
                    ShiftRow16(state);
                }
                

                tmpBlueMatch = tmpBlueMatch ^ state[9] ^ mul2(state[10]) ^ mul3(state[11]);
                if(TableBlue.find(tmpBlueMatch) != TableBlue.end())
                {
                    vector<uint8_t> ttmp =TableBlue[tmpBlueMatch];
                    ttmp.push_back(sKey[15]);
                    TableBlue[tmpBlueMatch] = ttmp;
                } else {
                    vector<uint8_t> ttmp;
                    ttmp.push_back(sKey[15]);
                    TableBlue[tmpBlueMatch] = ttmp;
                }
            }
            for (uint32_t r_index=0; r_index<redset.size(); r_index++) {
                uint8_t state[16] = {0};
                uint8_t tmpS[16];
                uint8_t newS[16];
                
                for(int i = 0; i < 16; i++){
                    sKey[i] = 0;
                }

                for(int i = 0; i < red_number; i++){
                    sKey[red_index[i]] = redset[r_index][i];
                } 

                for(int i = 0; i < 16; i++){
                    newS[i] = sKey[i];
                }
                for(int i = skey_start_round; i > -1; i--) {
                    KeyExpansion_S2RK(newS,i);
                    KeyExpansion_S2S_inv(newS,tmpS);
                    for(int j = 0; j < 16; j++){
                        newS[j] = tmpS[j];
                    }
                }
                for(int i = 0; i < 16; i++){
                    newS[i] = sKey[i];
                }
                for(int i = skey_start_round+1; i < skey_round ; i++) {
                    KeyExpansion_S2S(newS,tmpS);
                    for(int j = 0; j < 16; j++){
                        newS[j] = tmpS[j];
                    }
                    KeyExpansion_S2RK(newS,i);
                }
                uint8_t tmpRedMatch = 0; 
                tmpRedMatch = RoundKey[2*16+10];

                for(int i = 0; i < 16; i++){
                    state[i] = RoundKey[i] ^ plainText[i];
                }
                {
                    subByte16(state);
                    ShiftRow16(state);
                    MixColumn16(state);
                    AddRoundKey16(state,1);
                    subByte16(state);
                    ShiftRow16(state);
                }
                

                tmpRedMatch = tmpRedMatch ^ state[8];
                if(TableBlue.find(tmpRedMatch) != TableBlue.end()) {
                    vector<uint8_t> ttmp = TableBlue[tmpRedMatch];
                    MatchNum += ttmp.size();
        
                    //verify
                    for (uint32_t k=0; k<ttmp.size(); k++) {
                        uint8_t state[16] = {0};
                        uint8_t tmpS[16];
                        uint8_t newS[16];

                        sKey[15] = ttmp[k];
                        for(int i = 0; i < 16; i++){
                            newS[i] = sKey[i];
                        }
                        for(int i = skey_start_round; i > -1; i--) {
                            KeyExpansion_S2RK(newS,i);
                            KeyExpansion_S2S_inv(newS,tmpS);
                            for(int j = 0; j < 16; j++){
                                newS[j] = tmpS[j];
                            }
                        }
                        for(int i = 0; i < 16; i++){
                            newS[i] = sKey[i];
                        }
                        for(int i = skey_start_round+1; i < skey_round ; i++) {
                            KeyExpansion_S2S(newS,tmpS);
                            for(int j = 0; j < 16; j++){
                                newS[j] = tmpS[j];
                            }
                            KeyExpansion_S2RK(newS,i);
                        }
                        for(int i = 0; i < 16; i++){
                            state[i] = RoundKey[i] ^ plainText[i];
                        }
                        for(int i = 1; i < 5; i++){
                            subByte16(state);
                            ShiftRow16(state);
                            MixColumn16(state);
                            AddRoundKey16(state,i);
                        }
                        bool succed = true;
                        for(int i = 0; i < 16; i++){
                            if (state[i] != cipherText[i]){
                                succed = false;
                            }
                        }
                        if (succed){
                            for(int i = 0; i < 16; i++){
                                recovery_key.push_back(sKey[i]);
                            }
                        }
                    }
        
                }
            }
        }
    }
    if(recovery_key.size()>0){
        printf("successfully  recover the secret key:\n");
        for(int i = 0; i < 16; i++){
            printf("0x%02x, ", recovery_key[i]);
        }printf("\n");
    }
    else{
        printf("Fail to recover the secret key:\n");
    }
    end_time = clock();
    cout << "Finish! time is: " <<(double)(end_time - start_time) / CLOCKS_PER_SEC << "s" << endl;
}

int main()
{
    printf("begin main\n");    

    aes_128_keyrecovery_attack();

    printf("end main\n");
    return 0;
}
