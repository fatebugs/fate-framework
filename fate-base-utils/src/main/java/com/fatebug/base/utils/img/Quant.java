package com.fatebug.base.utils.img;

/**
 * 颜色量化器
 */
public class Quant {
    protected static final int netsize = 256; /* 使用的颜色数量 */

    /* 接近500的四个质数 - 假设没有图像的长度大到 */
    /* 能被所有这四个质数整除 */
    protected static final int prime1 = 499;
    protected static final int prime2 = 491;
    protected static final int prime3 = 487;
    protected static final int prime4 = 503;

    protected static final int minpicturebytes = (3 * prime4);
    /* 输入图像的最小大小 */

	/* 程序骨架
	   ----------------
	   [在1..30范围内选择samplefac]
	   [从输入文件读取图像]
	   pic = (unsigned char*) malloc(3*width*height);
	   initnet(pic,3*width*height,samplefac);
	   learn();
	   unbiasnet();
	   [使用writecolourmap(f)写入输出图像头]
	   inxbuild();
	   使用inxsearch(b,g,r)写入输出图像      */

	/* 网络定义
	   ------------------- */

    protected static final int maxnetpos = (netsize - 1);
    protected static final int netbiasshift = 4; /* 颜色值的偏移量 */
    protected static final int ncycles = 100; /* 学习周期数 */

    /* freq和bias的定义 */
    protected static final int intbiasshift = 16; /* 小数的偏移量 */
    protected static final int intbias = (((int) 1) << intbiasshift);
    protected static final int gammashift = 10; /* gamma = 1024 */
    protected static final int gamma = (((int) 1) << gammashift);
    protected static final int betashift = 10;
    protected static final int beta = (intbias >> betashift); /* beta = 1/1024 */
    protected static final int betagamma =
            (intbias << (gammashift - betashift));

    /* 递减半径因子的定义 */
    protected static final int initrad = (netsize >> 3); /* 对于256种颜色，半径从32.0开始 */
    protected static final int radiusbiasshift = 6; /* 偏移6位 */
    protected static final int radiusbias = (((int) 1) << radiusbiasshift);
    protected static final int initradius = (initrad * radiusbias); /* 并且每个周期 */
    protected static final int radiusdec = 30; /* 以1/30的因子递减 */

    /* alpha因子递减的定义 */
    protected static final int alphabiasshift = 10; /* alpha从1.0开始 */
    protected static final int initalpha = (((int) 1) << alphabiasshift);

    protected int alphadec; /* 偏移10位 */

    /* 用于radpower计算的radbias和alpharadbias */
    protected static final int radbiasshift = 8;
    protected static final int radbias = (((int) 1) << radbiasshift);
    protected static final int alpharadbshift = (alphabiasshift + radbiasshift);
    protected static final int alpharadbias = (((int) 1) << alpharadbshift);

	/* 类型和全局变量
	-------------------------- */

    protected byte[] thepicture; /* 输入图像本身 */
    protected int lengthcount; /* lengthcount = 高*宽*3 */

    protected int samplefac; /* 采样因子1..30 */

    //   typedef int pixel[4];                /* BGRc */
    protected int[][] network; /* 网络本身 - [netsize][4] */

    protected int[] netindex = new int[256];
    /* 用于网络查找 - 实际为256 */

    protected int[] bias = new int[netsize];
    /* 用于学习的bias和freq数组 */
    protected int[] freq = new int[netsize];
    protected int[] radpower = new int[initrad];
    /* 用于预计算的radpower */

    /* 在范围(0,0,0)到(255,255,255)初始化网络并设置参数
       ----------------------------------------------------------------------- */
    public Quant(byte[] thepic, int len, int sample) {

        int i;
        int[] p;

        thepicture = thepic;
        lengthcount = len;
        samplefac = sample;

        network = new int[netsize][];
        for (i = 0; i < netsize; i++) {
            network[i] = new int[4];
            p = network[i];
            p[0] = p[1] = p[2] = (i << (netbiasshift + 8)) / netsize;
            freq[i] = intbias / netsize; /* 1/netsize */
            bias[i] = 0;
        }
    }

    public byte[] colorMap() {
        byte[] map = new byte[3 * netsize];
        int[] index = new int[netsize];
        for (int i = 0; i < netsize; i++)
            index[network[i][3]] = i;
        int k = 0;
        for (int i = 0; i < netsize; i++) {
            int j = index[i];
            map[k++] = (byte) (network[j][0]);
            map[k++] = (byte) (network[j][1]);
            map[k++] = (byte) (network[j][2]);
        }
        return map;
    }

    /* 对网络进行插入排序并构建netindex[0..255]（在unbiasnet之后执行）
       ------------------------------------------------------------------------------- */
    public void inxbuild() {

        int i, j, smallpos, smallval;
        int[] p;
        int[] q;
        int previouscol, startpos;

        previouscol = 0;
        startpos = 0;
        for (i = 0; i < netsize; i++) {
            p = network[i];
            smallpos = i;
            smallval = p[1]; /* 基于g的索引 */
            /* 在i..netsize-1中找到最小值 */
            for (j = i + 1; j < netsize; j++) {
                q = network[j];
                if (q[1] < smallval) { /* 基于g的索引 */
                    smallpos = j;
                    smallval = q[1]; /* 基于g的索引 */
                }
            }
            q = network[smallpos];
            /* 交换p(i)和q(smallpos)条目 */
            if (i != smallpos) {
                j = q[0];
                q[0] = p[0];
                p[0] = j;
                j = q[1];
                q[1] = p[1];
                p[1] = j;
                j = q[2];
                q[2] = p[2];
                p[2] = j;
                j = q[3];
                q[3] = p[3];
                p[3] = j;
            }
            /* smallval条目现在位于位置i */
            if (smallval != previouscol) {
                netindex[previouscol] = (startpos + i) >> 1;
                for (j = previouscol + 1; j < smallval; j++)
                    netindex[j] = i;
                previouscol = smallval;
                startpos = i;
            }
        }
        netindex[previouscol] = (startpos + maxnetpos) >> 1;
        for (j = previouscol + 1; j < 256; j++)
            netindex[j] = maxnetpos; /* 实际为256 */
    }

    /* 主要学习循环
       ------------------ */
    public void learn() {

        int i, j, b, g, r;
        int radius, rad, alpha, step, delta, samplepixels;
        byte[] p;
        int pix, lim;

        if (lengthcount < minpicturebytes)
            samplefac = 1;
        alphadec = 30 + ((samplefac - 1) / 3);
        p = thepicture;
        pix = 0;
        lim = lengthcount;
        samplepixels = lengthcount / (3 * samplefac);
        delta = samplepixels / ncycles;
        alpha = initalpha;
        radius = initradius;

        rad = radius >> radiusbiasshift;
        if (rad <= 1)
            rad = 0;
        for (i = 0; i < rad; i++)
            radpower[i] =
                    alpha * (((rad * rad - i * i) * radbias) / (rad * rad));

        //fprintf(stderr,"beginning 1D learning: initial radius=%d\n", rad);

        if (lengthcount < minpicturebytes)
            step = 3;
        else if ((lengthcount % prime1) != 0)
            step = 3 * prime1;
        else {
            if ((lengthcount % prime2) != 0)
                step = 3 * prime2;
            else {
                if ((lengthcount % prime3) != 0)
                    step = 3 * prime3;
                else
                    step = 3 * prime4;
            }
        }

        i = 0;
        while (i < samplepixels) {
            b = (p[pix + 0] & 0xff) << netbiasshift;
            g = (p[pix + 1] & 0xff) << netbiasshift;
            r = (p[pix + 2] & 0xff) << netbiasshift;
            j = contest(b, g, r);

            altersingle(alpha, j, b, g, r);
            if (rad != 0)
                alterneigh(rad, j, b, g, r); /* 改变邻居 */

            pix += step;
            if (pix >= lim)
                pix -= lengthcount;

            i++;
            if (delta == 0)
                delta = 1;
            if (i % delta == 0) {
                alpha -= alpha / alphadec;
                radius -= radius / radiusdec;
                rad = radius >> radiusbiasshift;
                if (rad <= 1)
                    rad = 0;
                for (j = 0; j < rad; j++)
                    radpower[j] =
                            alpha * (((rad * rad - j * j) * radbias) / (rad * rad));
            }
        }
        //fprintf(stderr,"finished 1D learning: final alpha=%f !\n",((float)alpha)/initalpha);
    }

    /* 搜索0..255的BGR值（在网络去偏置后）并返回颜色索引
       ---------------------------------------------------------------------------- */
    public int map(int b, int g, int r) {

        int i, j, dist, a, bestd;
        int[] p;
        int best;

        bestd = 1000; /* 可能的最大距离是256*3 */
        best = -1;
        i = netindex[g]; /* 基于g的索引 */
        j = i - 1; /* 从netindex[g]开始向外工作 */

        while ((i < netsize) || (j >= 0)) {
            if (i < netsize) {
                p = network[i];
                dist = p[1] - g; /* 索引键 */
                if (dist >= bestd)
                    i = netsize; /* 停止迭代 */
                else {
                    i++;
                    if (dist < 0)
                        dist = -dist;
                    a = p[0] - b;
                    if (a < 0)
                        a = -a;
                    dist += a;
                    if (dist < bestd) {
                        a = p[2] - r;
                        if (a < 0)
                            a = -a;
                        dist += a;
                        if (dist < bestd) {
                            bestd = dist;
                            best = p[3];
                        }
                    }
                }
            }
            if (j >= 0) {
                p = network[j];
                dist = g - p[1]; /* 索引键 - 反向差 */
                if (dist >= bestd)
                    j = -1; /* 停止迭代 */
                else {
                    j--;
                    if (dist < 0)
                        dist = -dist;
                    a = p[0] - b;
                    if (a < 0)
                        a = -a;
                    dist += a;
                    if (dist < bestd) {
                        a = p[2] - r;
                        if (a < 0)
                            a = -a;
                        dist += a;
                        if (dist < bestd) {
                            bestd = dist;
                            best = p[3];
                        }
                    }
                }
            }
        }
        return (best);
    }

    public byte[] process() {
        learn();
        unbiasnet();
        inxbuild();
        return colorMap();
    }

    /* 将网络去偏置以得到0..255的字节值并记录位置i以准备排序
       ----------------------------------------------------------------------------------- */
    public void unbiasnet() {

        int i, j;

        for (i = 0; i < netsize; i++) {
            network[i][0] >>= netbiasshift;
            network[i][1] >>= netbiasshift;
            network[i][2] >>= netbiasshift;
            network[i][3] = i; /* 记录颜色编号 */
        }
    }

    /* 通过预先计算的alpha*(1-((i-j)^2/[r]^2))移动相邻神经元，存储在radpower[|i-j|]中
       --------------------------------------------------------------------------------- */
    protected void alterneigh(int rad, int i, int b, int g, int r) {

        int j, k, lo, hi, a, m;
        int[] p;

        lo = i - rad;
        if (lo < -1)
            lo = -1;
        hi = i + rad;
        if (hi > netsize)
            hi = netsize;

        j = i + 1;
        k = i - 1;
        m = 1;
        while ((j < hi) || (k > lo)) {
            a = radpower[m++];
            if (j < hi) {
                p = network[j++];
                try {
                    p[0] -= (a * (p[0] - b)) / alpharadbias;
                    p[1] -= (a * (p[1] - g)) / alpharadbias;
                    p[2] -= (a * (p[2] - r)) / alpharadbias;
                } catch (Exception e) {
                } // 防止1.3版本编译错误
            }
            if (k > lo) {
                p = network[k--];
                try {
                    p[0] -= (a * (p[0] - b)) / alpharadbias;
                    p[1] -= (a * (p[1] - g)) / alpharadbias;
                    p[2] -= (a * (p[2] - r)) / alpharadbias;
                } catch (Exception e) {
                }
            }
        }
    }

    /* 通过因子alpha将神经元i向偏置的(b,g,r)移动
       ---------------------------------------------------- */
    protected void altersingle(int alpha, int i, int b, int g, int r) {

        /* 改变命中的神经元 */
        int[] n = network[i];
        n[0] -= (alpha * (n[0] - b)) / initalpha;
        n[1] -= (alpha * (n[1] - g)) / initalpha;
        n[2] -= (alpha * (n[2] - r)) / initalpha;
    }

    /* 搜索偏置的BGR值
       ---------------------------- */
    protected int contest(int b, int g, int r) {

        /* 找到最接近的神经元（最小距离）并更新频率 */
        /* 找到最佳神经元（最小距离-偏置）并返回位置 */
        /* 对于经常选择的神经元，freq[i]很高并且bias[i]为负 */
        /* bias[i] = gamma*((1/netsize)-freq[i]) */

        int i, dist, a, biasdist, betafreq;
        int bestpos, bestbiaspos, bestd, bestbiasd;
        int[] n;

        bestd = ~(((int) 1) << 31);
        bestbiasd = bestd;
        bestpos = -1;
        bestbiaspos = bestpos;

        for (i = 0; i < netsize; i++) {
            n = network[i];
            dist = n[0] - b;
            if (dist < 0)
                dist = -dist;
            a = n[1] - g;
            if (a < 0)
                a = -a;
            dist += a;
            a = n[2] - r;
            if (a < 0)
                a = -a;
            dist += a;
            if (dist < bestd) {
                bestd = dist;
                bestpos = i;
            }
            biasdist = dist - ((bias[i]) >> (intbiasshift - netbiasshift));
            if (biasdist < bestbiasd) {
                bestbiasd = biasdist;
                bestbiaspos = i;
            }
            betafreq = (freq[i] >> betashift);
            freq[i] -= betafreq;
            bias[i] += (betafreq << gammashift);
        }
        freq[bestpos] += beta;
        bias[bestpos] -= betagamma;
        return (bestbiaspos);
    }
}
