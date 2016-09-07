#include <stdio.h>
int main()
{
int list1[] = { 1, 2, 3, 2, 3 };
int _list1_size = 5;
int _AD_i = 0;
int result = 0;
for(_AD_i = 0; _AD_i < _list1_size; _AD_i++){
result += list1[_AD_i];
}
printf("%d", result);
return 0;
}
