void main();
void swap(int *, int *);
void qsort(int [], int, int);

func void main() {
    int hoge;
    int i;
    int a[10];
    // aに初期値を設定する
    a[0] = 3;
    a[1] = 1;
    a[2] = 4;
    a[3] = 2;
    a[4] = 5;
    a[5] = 9;
    a[6] = 6;
    a[7] = 8;
    a[8] = 7;
    a[9] = 0;
    call qsort(&a, 0, 9);
    while(i < 10) {
        // aを出力する
        output a[i];
        input hoge;
        i = i + 1;
    }
}


func void swap(int *a, int *b) {
    int t;
    t = *a;
    *a = *b;
    *b = t;
}

func void qsort(int a[], int start, int end) {
    // 配列aのa[start]からa[end]までクイックソートで整列する
    int left, right, pivot;
    while(start < end) {
        left = start;
        right = end;
        pivot = a[(start + end) / 2];
        while(left <= right) {
            while(a[left] < pivot) left=left+1;
            while(a[right] > pivot) right=right-1;
            if(left <= right) {
                call swap(&a[left], &a[right]);
                left=left+1;
                right=right-1;
            }
        }
        call qsort(&a, start, right);
        start = left;
    }
}