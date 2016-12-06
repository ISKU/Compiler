int global;
int hexNumber = 0xf;
int octalNumber = 012;
int zero = 0;
int globalArray[10];

int getList(int list[], int position) {
	return list[position];
}

void setList(int list[], int size) {
	int i = 00;
	while ((size - 1) > 0) {
		list[i] = calculate(i, i + 1, i % 4);
		++i;
		--size;
	}
}

int calculate(int a, int b, int op) {
	if (op == 1)
		return a + b;
	if (op == 2)
		return a - b;
	if (op == 3)
		return a * b;
	if (op == 4)
		return a / b;
	else
		return 0;
}

void main() {
	int array[0xa];
	global = 10;
	
	setList(array, 10);
	while ((global < 11) and (global >= 0)) {
		write(getList(array, global));
		--global;
	}
}
