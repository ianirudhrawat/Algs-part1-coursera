/*
问题：渗透问题。将一个不透水的均质方块分割为矩阵N*N，最上方为水源。随机打开矩阵中任意格子，重复此项操作多次，直到产生一条路径使水能穿过这个方块到达最下方。

思路4：
采用weighted-quick-union算法（with Path compression）。

数据结构：和课上所用的weighted-quick-union算法一致，采用PPT的写法，模拟出2个虚拟点，分别是top和bottom。isFull就是判断这个点是否 is_connected(self,top)
*/
/**
Version 5.2
- unsolving backwash: (method-1) 
instead of using a BOTTOM point connecting to all the last points in last row. just deciding whether percolate by testing any point in last row connected to TOP point.

TODO:
---1. fix correction---
---2. fix time consume: reuse BOT---

因为使用 method-1 虽然解决了 backwash 问题，但导致每次 percolation() 调用都产生了 N 次 connected()。
如果不使用 method-1, 每次只调用 1 次 connected()。

其他解决办法：
http://www.sigmainfy.com/blog/avoid-backwash-in-percolation.html

**/

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {

    private WeightedQuickUnionUF uf;

    private boolean[] is_open;

    private int TOP;
    private int BOT;

    private int N;
    // count of all site, including top and bottom
    private int COUNT;

    public Percolation(int n) {

        // constructor only throws exception when n<0, while requirement need throw also n=0
        if (n <= 0) {
            throw new IllegalArgumentException("n<=0");
        }

        N = n;
        TOP = N * N;
        BOT = TOP + 1;
        COUNT = N * N + 2;
        // COUNT = N * N + 1;

        uf = new WeightedQuickUnionUF(COUNT);
        is_open = new boolean[COUNT];

        for (int i = 0; i < COUNT; i++) {
            is_open[i] = false;
        }

        for (int i = 0; i < N; i++) {
            uf.union(i, TOP);
            uf.union(N * N - 1 - i, BOT);
        }

    }


    public void open(int i, int j) {
        // start from 1,1
        i--;
        j--;

        // corner case test
        int[] paras = {i, j};
        for (int para : paras) {
            if (para < 0 || para >= N) {
                throw new IndexOutOfBoundsException();
            }
        }

        int self = i * N + j;
        if (is_open[self]) return;
        is_open[self] = true;

        int NOT_EXIST = -1;

        int[] x = new int[4]; // up, down, left, right
        x[0] = i > 0 ? ((i - 1) * N + j) : NOT_EXIST;
        x[1] = i < N - 1 ? (i + 1) * N + j : NOT_EXIST;
        x[2] = j > 0 ? i * N + j - 1 : NOT_EXIST;
        x[3] = j < N - 1 ? i * N + j + 1 : NOT_EXIST;

        for (int k = 0; k < 4; k++) {
            if (x[k] != NOT_EXIST && is_open[x[k]]) {
                uf.union(x[k], self);
            }
        }
        return;
    }

    public boolean isOpen(int i, int j) {
        i--;
        j--;
        // corner case test
        int[] paras = {i, j};
        for (int para : paras) {
            if (para < 0 || para >= N) {
                throw new IndexOutOfBoundsException();
            }
        }
        return is_open[i * N + j];
    }
    public boolean isFull(int i, int j) {
        i--;
        j--;
        // corner case test
        int[] paras = {i, j};
        for (int para : paras) {
            if (para < 0 || para >= N) {
                throw new IndexOutOfBoundsException();
            }
        }

        return is_open[i * N + j] && uf.connected(i * N + j, TOP);
    }
    public boolean percolates() {
        // for (int i = 0; i < N; i++) {
        //     // is_open[N * N - 1 - i]: corner case N = 0
        //     if (is_open[N * N - 1 - i] && uf.connected(TOP, N * N - 1 - i)) {
        //         return true;
        //     }
        // }
        // return false;
        if (N==1)
            return is_open[0];
        return uf.connected(TOP, BOT);
    }
}
