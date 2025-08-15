package com.fatebug.base.utils.img;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Gif生成工具
 * 类 AnimatedGifEncoder - 编码一个由一帧或多帧组成的GIF文件。
 * <pre>
 * 示例:
 *    AnimatedGifEncoder e = new AnimatedGifEncoder();
 *    e.start(outputFileName);
 *    e.setDelay(1000);   // 每秒1帧
 *    e.addFrame(image1);
 *    e.addFrame(image2);
 *    e.finish();
 * </pre>
 * 本类源代码不主张版权。可用于任何目的，但是，请参考Unisys LZW专利
 * 了解相关Encoder类使用的限制。请将任何更正转发至
 * questions at fmsware.com。
 */
public class GifEncoder {
    protected int width; // 图像宽度
    protected int height; // 图像高度
    protected Color transparent = null; // 透明颜色（如果有）
    protected int transIndex; // 颜色表中的透明索引
    protected int repeat = -1; // 不重复
    protected int delay = 0; // 帧延迟（百分之一秒）
    protected boolean started = false; // 准备输出帧
    protected OutputStream out;
    protected BufferedImage image; // 当前帧
    protected byte[] pixels; // 来自帧的BGR字节数组
    protected byte[] indexedPixels; // 转换为索引到调色板的帧
    protected int colorDepth; // 位平面数
    protected byte[] colorTab; // RGB调色板
    protected boolean[] usedEntry = new boolean[256]; // 活动调色板条目
    protected int palSize = 7; // 颜色表大小（位数-1）
    protected int dispose = -1; // 处理代码（-1表示使用默认值）
    protected boolean closeStream = false; // 完成时关闭流
    protected boolean firstFrame = true;
    protected boolean sizeSet = false; // 如果为false，从第一帧获取大小
    protected int sample = 10; // 量化器的默认采样间隔

    /**
     * 设置每帧之间的延迟时间，或更改后续帧的延迟时间
     * （应用于最后添加的帧）。
     *
     * @param ms int 以毫秒为单位的延迟时间
     */
    public void setDelay(int ms) {
        delay = Math.round(ms / 10.0f);
    }

    /**
     * 为最后添加的帧和任何后续帧设置GIF帧处理代码。
     * 如果没有设置透明颜色，默认为0，否则为2。
     *
     * @param code int 处理代码。
     */
    public void setDispose(int code) {
        if (code >= 0) {
            dispose = code;
        }
    }

    /**
     * 设置GIF帧组应该播放的次数。
     * 默认为1；0表示无限播放。
     * 必须在添加第一个图像之前调用。
     *
     * @param iter int 迭代次数。
     */
    public void setRepeat(int iter) {
        if (iter >= 0) {
            repeat = iter;
        }
    }

    /**
     * 为最后添加的帧和任何后续帧设置透明颜色。
     * 由于所有颜色在量化过程中都会被修改，
     * 因此每帧最终调色板中最接近给定颜色的颜色
     * 将成为该帧的透明颜色。
     * 可以设置为null以表示没有透明颜色。
     *
     * @param c 在显示时被视为透明的颜色。
     */
    public void setTransparent(Color c) {
        transparent = c;
    }

    /**
     * 添加下一个GIF帧。帧不会立即写入，而是
     * 实际上延迟到接收到下一帧，以便可以插入计时
     * 数据。调用<code>finish()</code>刷新所有
     * 帧。如果未调用<code>setSize</code>，则第一个
     * 图像的大小将用于所有后续帧。
     *
     * @param im 包含要写入帧的BufferedImage。
     * @return 如果成功则返回true。
     */
    public boolean addFrame(BufferedImage im) {
        if ((im == null) || !started) {
            return false;
        }
        boolean ok = true;
        try {
            if (!sizeSet) {
                // 使用第一帧的大小
                setSize(im.getWidth(), im.getHeight());
            }
            image = im;
            getImagePixels(); // 如有必要，转换为正确格式
            analyzePixels(); // 构建颜色表和映射像素
            if (firstFrame) {
                writeLSD(); // 逻辑屏幕描述符
                writePalette(); // 全局颜色表
                if (repeat >= 0) {
                    // 使用NS应用扩展表示重复
                    writeNetscapeExt();
                }
            }
            writeGraphicCtrlExt(); // 写入图形控制扩展
            writeImageDesc(); // 图像描述符
            if (!firstFrame) {
                writePalette(); // 本地颜色表
            }
            writePixels(); // 编码并写入像素数据
            firstFrame = false;
        } catch (IOException e) {
            ok = false;
        }

        return ok;
    }

    // 由alvaro添加
    public boolean outFlush() {
        boolean ok = true;
        try {
            out.flush();
            return ok;
        } catch (IOException e) {
            ok = false;
        }

        return ok;
    }

    public byte[] getFrameByteArray() {
        return ((ByteArrayOutputStream) out).toByteArray();
    }

    /**
     * 刷新任何挂起的数据并关闭输出文件。
     * 如果写入到OutputStream，则不会
     * 关闭该流。
     *
     * @return boolean
     */
    public boolean finish() {
        if (!started) return false;
        boolean ok = true;
        started = false;
        try {
            out.write(0x3b); // gif尾部标记
            out.flush();
            if (closeStream) {
                out.close();
            }
        } catch (IOException e) {
            ok = false;
        }

        return ok;
    }

    public void reset() {
        // 重置以供后续使用
        transIndex = 0;
        out = null;
        image = null;
        pixels = null;
        indexedPixels = null;
        colorTab = null;
        closeStream = false;
        firstFrame = true;
    }

    /**
     * 设置每秒帧数。相当于
     * <code>setDelay(1000/fps)</code>。
     *
     * @param fps float 帧率（每秒帧数）
     */
    public void setFrameRate(float fps) {
        if (fps != 0f) {
            delay = Math.round(100f / fps);
        }
    }

    /**
     * 设置颜色量化的质量（将图像转换为
     * GIF规范允许的最多256种颜色）。
     * 较低的值（最小为1）产生更好的颜色，但会
     * 显著减慢处理速度。10是默认值，并在
     * 合理的速度下产生良好的颜色映射。大于
     * 20的值在速度上不会产生显著改善。
     *
     * @param quality int 大于0。
     */
    public void setQuality(int quality) {
        if (quality < 1) quality = 1;
        sample = quality;
    }

    /**
     * 设置GIF帧大小。如果未调用此方法，
     * 默认大小是添加的第一帧的大小。
     *
     * @param w int 帧宽度。
     * @param h int 帧高度。
     */
    public void setSize(int w, int h) {
        if (started && !firstFrame) return;
        width = w;
        height = h;
        if (width < 1) width = 320;
        if (height < 1) height = 240;
        sizeSet = true;
    }

    /**
     * 在给定的流上启动GIF文件创建。
     * 不会自动关闭该流。
     *
     * @param os 用于写入GIF图像的OutputStream。
     * @return 如果初始写入失败则返回false。
     */
    public boolean start(OutputStream os) {
        if (os == null) return false;
        boolean ok = true;
        closeStream = false;
        out = os;
        try {
            writeString("GIF89a"); // 头部
        } catch (IOException e) {
            ok = false;
        }
        return started = ok;
    }

    /**
     * 开始写入具有指定名称的GIF文件。
     *
     * @param file 包含输出文件名的String。
     * @return 如果打开或初始写入失败则返回false。
     */
    public boolean start(String file) {
        boolean ok = true;
        try {
            out = new BufferedOutputStream(new FileOutputStream(file));
            ok = start(out);
            closeStream = true;
        } catch (IOException e) {
            ok = false;
        }
        return started = ok;
    }

    /**
     * 分析图像颜色并创建颜色映射。
     */
    protected void analyzePixels() {
        int len = pixels.length;
        int nPix = len / 3;
        indexedPixels = new byte[nPix];
        Quant nq = new Quant(pixels, len, sample);
        // 初始化量化器
        colorTab = nq.process(); // 创建减少的调色板
        // 将映射从BGR转换为RGB
        for (int i = 0; i < colorTab.length; i += 3) {
            byte temp = colorTab[i];
            colorTab[i] = colorTab[i + 2];
            colorTab[i + 2] = temp;
            usedEntry[i / 3] = false;
        }
        // 将图像像素映射到新调色板
        int k = 0;
        for (int i = 0; i < nPix; i++) {
            int index =
                    nq.map(pixels[k++] & 0xff,
                            pixels[k++] & 0xff,
                            pixels[k++] & 0xff);
            usedEntry[index] = true;
            indexedPixels[i] = (byte) index;
        }
        pixels = null;
        colorDepth = 8;
        palSize = 7;
        // 如果指定了透明颜色，获取最接近的匹配
        if (transparent != null) {
            transIndex = findClosest(transparent);
        }
    }

    /**
     * 返回最接近c的调色板颜色的索引
     *
     * @param c 颜色
     * @return int
     */
    protected int findClosest(Color c) {
        if (colorTab == null) return -1;
        int r = c.getRed();
        int g = c.getGreen();
        int b = c.getBlue();
        int minpos = 0;
        int dmin = 256 * 256 * 256;
        int len = colorTab.length;
        for (int i = 0; i < len; ) {
            int dr = r - (colorTab[i++] & 0xff);
            int dg = g - (colorTab[i++] & 0xff);
            int db = b - (colorTab[i] & 0xff);
            int d = dr * dr + dg * dg + db * db;
            int index = i / 3;
            if (usedEntry[index] && (d < dmin)) {
                dmin = d;
                minpos = index;
            }
            i++;
        }
        return minpos;
    }

    /**
     * 将图像像素提取到字节数组"pixels"中
     */
    protected void getImagePixels() {
        int w = image.getWidth();
        int h = image.getHeight();
        int type = image.getType();
        if ((w != width)
                || (h != height)
                || (type != BufferedImage.TYPE_3BYTE_BGR)) {
            // 创建具有正确大小/格式的新图像
            BufferedImage temp =
                    new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
            Graphics2D g = temp.createGraphics();
            g.drawImage(image, 0, 0, null);
            image = temp;
        }
        pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
    }

    /**
     * 写入图形控制扩展
     *
     * @throws IOException IO异常
     */
    protected void writeGraphicCtrlExt() throws IOException {
        out.write(0x21); // 扩展引入符
        out.write(0xf9); // GCE标签
        out.write(4); // 数据块大小
        int transp, disp;
        if (transparent == null) {
            transp = 0;
            disp = 0; // 处理 = 无动作
        } else {
            transp = 1;
            disp = 2; // 如果使用透明颜色则强制清除
        }
        if (dispose >= 0) {
            disp = dispose & 7; // 用户覆盖
        }
        disp <<= 2;

        // 打包字段
        out.write(0 | // 1:3 保留
                disp | // 4:6 处理
                0 | // 7   用户输入 - 0 = 无
                transp); // 8   透明标志

        writeShort(delay); // 延迟 x 1/100 秒
        out.write(transIndex); // 透明颜色索引
        out.write(0); // 块终结符
    }

    /**
     * 写入图像描述符
     *
     * @throws IOException IO异常
     */
    protected void writeImageDesc() throws IOException {
        out.write(0x2c); // 图像分隔符
        writeShort(0); // 图像位置 x,y = 0,0
        writeShort(0);
        writeShort(width); // 图像大小
        writeShort(height);
        // 打包字段
        if (firstFrame) {
            // 没有LCT  - 第一帧（或唯一帧）使用GCT
            out.write(0);
        } else {
            // 指定普通LCT
            out.write(0x80 | // 1 本地颜色表  1=是
                    0 | // 2 交错 - 0=否
                    0 | // 3 排序 - 0=否
                    0 | // 4-5 保留
                    palSize); // 6-8 颜色表大小
        }
    }

    /**
     * 写入逻辑屏幕描述符
     *
     * @throws IOException IO异常
     */
    protected void writeLSD() throws IOException {
        // 逻辑屏幕大小
        writeShort(width);
        writeShort(height);
        // 打包字段
        out.write((0x80 | // 1   : 全局颜色表标志 = 1 (使用gct)
                0x70 | // 2-4 : 颜色分辨率 = 7
                0x00 | // 5   : gct排序标志 = 0
                palSize)); // 6-8 : gct大小

        out.write(0); // 背景颜色索引
        out.write(0); // 像素宽高比 - 假定1:1
    }

    /**
     * 写入Netscape应用程序扩展以定义
     * 重复计数。
     *
     * @throws IOException IO异常
     */
    protected void writeNetscapeExt() throws IOException {
        out.write(0x21); // 扩展引入符
        out.write(0xff); // 应用扩展标签
        out.write(11); // 块大小
        writeString("NETSCAPE" + "2.0"); // 应用ID + 验证码
        out.write(3); // 子块大小
        out.write(1); // 循环子块ID
        writeShort(repeat); // 循环计数（额外迭代，0=永远重复）
        out.write(0); // 块终结符
    }

    /**
     * 写入颜色表
     *
     * @throws IOException IO异常
     */
    protected void writePalette() throws IOException {
        out.write(colorTab, 0, colorTab.length);
        int n = (3 * 256) - colorTab.length;
        for (int i = 0; i < n; i++) {
            out.write(0);
        }
    }

    /**
     * 编码并写入像素数据
     *
     * @throws IOException IO异常
     */
    protected void writePixels() throws IOException {
        Encoder encoder = new Encoder(width, height, indexedPixels, colorDepth);
        encoder.encode(out);
    }

    /**
     * 向输出流写入16位值，LSB在前
     *
     * @param value int
     * @throws IOException IO异常
     */
    protected void writeShort(int value) throws IOException {
        out.write(value & 0xff);
        out.write((value >> 8) & 0xff);
    }

    /**
     * 向输出流写入字符串
     *
     * @param s 字符串
     * @throws IOException IO异常
     */
    protected void writeString(String s) throws IOException {
        for (int i = 0; i < s.length(); i++) {
            out.write((byte) s.charAt(i));
        }
    }
}
