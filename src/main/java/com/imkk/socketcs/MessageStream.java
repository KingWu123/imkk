package com.imkk.socketcs;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by kingwu on 7/25/16.
 *
 *
 * 用于将Socket InputStream流的内容缓存下来, 进行 半包 和 黏包的处理, 最终返回完整的 Message包信息
 *
 * 1. socket通信过程中, 包很可能被拆分掉, 需要对传输过来的内容, 根据 包头标记位, 确定一个包的起始位置,然后读出一整条消息
 * {@link #getMessage()}方法处理已读取一条信息, 但没有处理黏包的问题
 *
 *
 *   1). 寻找包头。这个包头就是一个int整型。但是写代码的时候要非常注意，一个int实际上占据了4个byte，
 *        而可悲的是这4个byte在传输过程中也可能被socket 分割了，因此读取判断的逻辑是：
 *       a) 判断剩余长度是否大于4, <4 直接抛出异常
 *       b) 读取一个int，判断是否包头，如果是就跳出循环。
 *       c) 如果不是包头，则倒退3个byte，回到第一点。
 *       d) 如果读取完毕也没有找到，则有可能包头被分割了，需要回到起始位置处, 等待下一个包到达后合并判断。
 *
 *   2). 读取包体类型, short型, 这个short行也可能被分为2个包, 所以要判断剩余内容是否够 一个short型
 *   3). 读取包体长度。由于长度也是一个int，因此判断的时候也要小心，所以要判断剩余内容是否够 一个int型。
 *   4). 读取包体，由于已知包体长度，因此读取包体就变得非常简单了，只要一直读取相应的长度，剩余的又回到第一条寻找包头。
 *
 *
 * 2. 发送方很有可能一次发多个包,这样接收方读取的流很有可能一次含有多个包(黏包), {@link #getMessages()}可以处理黏包的问题
 *
 * attention: 当接受的内容满足了一个完整Message要求的时候,才会返回这个Message,处理的过程在内存里。 所以,对于大型的文件,并不适用。
 *
 */


public class MessageStream {

    private static final int CHUNK_SIZE = 8 * 1024;

    private byte[] mBuffer;  //存储信息流的buffer
    private int mPosition;   //当前读取的位置处
    private int mLength;     //当前信息流的的长度,
    private int mCapacity;   //buffer容量, 容量不够时,会自动根据加入的内容大小进行扩展


    public MessageStream(){
        mBuffer = new byte[0];
        mPosition = 0;
        mLength = 0;
        mCapacity = 0;
    }


    /**
     * MessageStream 接收 stream流
     * @param inputStream 输入流
     * @throws IOException
     *
     * @return   the total number of bytes read into the buffer, or
     *             -1 if there is no more data because the end of
     *             the stream has been reached.
     */
    public  int receive(InputStream inputStream) throws IOException {

        int length =  1024 * 8;
        byte[] buffer = new byte[length];

        int count =  inputStream.read(buffer, 0, length);

        //将读取到的信息流写入MessageStream中
        writeMessageStream(buffer, 0, count);

        return count;
    }


    /**
     * 从MessageStream中找出一个完成的包,并封装成Message对象返回
     * @return Message对象
     */
    public Message getMessage(){

        int currentPosition = this.mPosition;

        try {
            Message message = readMessageStream(); //读取不成功,会抛出EOFException异常
            return message;
        }
        catch (EOFException e) {

            //如果抛出越界异常,说明不存在一个完成的Message包,则恢复到开始判断包头的位置
            this.mPosition = currentPosition;
            //e.printStackTrace();
            return null;
        }
    }

    /**
     * 从MessageStream中找出多个完成包,并封装成ArrayList<Message> 对象返回
     *
     * 这是由于 发送方 很有可能同时发送多个包, 出现黏包的现象
     *
     * @return ArrayList<Message>对象
     */
    public ArrayList<Message> getMessages(){

        int currentPosition = this.mPosition;

        ArrayList<Message> messages  = new ArrayList<Message>();

        boolean flag = true;
        while (flag) {
            try {


                Message message = readMessageStream(); //读取不成功,会抛出EOFException异常
                messages.add(message);

                currentPosition = this.mPosition;

            } catch (EOFException e) {

                //如果抛出越界异常,说明不存在一个完成的Message包,则恢复到开始判断包头的位置
                this.mPosition = currentPosition;
                flag = false;
            }
        }

        if (messages.size() > 0){
            return messages;
        }else {
            return null;
        }
    }






    /**
     * 往 MessageStream  buffer里写入 输入流 的内容
     * 会根据写入的内容,动态的调整MessageStream buffer的大小
     *
     * @param buffer 写入的内容
     * @param offset buffer写入起始位置
     * @param count  buffer需要写入的数目
     *
     * @throws EOFException
     */
    private void writeMessageStream(byte[] buffer, int offset, int count){

        if (buffer.length - offset < count){
            count = buffer.length - offset;
        }
        if (count <= 0){
            return;
        }

        ensureCapacity(this.mLength + count);
        Arrays.fill(this.mBuffer, this.mLength, this.mCapacity, (byte)0);
        System.arraycopy(buffer, offset, this.mBuffer, this.mLength, count);
        this.mLength += count;

    }


    /**
     *  读取一条消息, 读取不成功,会抛出异常
     *
     *  方法里调用的 readInt/readShort/readBytes等,如果位数不够,会抛出EOFException异常, 方法返回
     * @return  Message
     * @throws EOFException
     */
    private Message readMessageStream() throws  EOFException{

        while (true){

            /**
             * 读取一个int,判断是否是包头,如果是,则退出循环; 如果不是,则回退3个byte,从新判断。
             * readInt()不够一个int型时,会抛出异常,循环打破
             */
            int packageFlag = readInt();
            if (packageFlag == Message.PACKAGE_FLAG) {
                break;
            }else {
                this.mPosition -= 3;
            }
        }

        short packageType = readShort();
        int  bodyLength = readInt();
        byte[] packageBody = readBytes(bodyLength);

        //到这里,一个Message包的内容组装完成
        Message message = new Message(packageType, packageBody);

        //从 MessageStream buffer里的移除这条消息内容
        remove(message.getTotalLength());

        return message;
    }



    /**
     * 确保 MessageStream里的容量大小足够用
     *
     * @param count  需要满足的最大容量数
     */
    private void ensureCapacity(int count){
        if (count < this.mCapacity){
            return;
        }

        int destCapacity = count;
        if (destCapacity < 1024){
            destCapacity = 1024;
        }else if (destCapacity < this.mCapacity * 2){
            destCapacity = this.mCapacity * 2;
        }

        byte[] tempBuf = new byte[destCapacity];
        if (this.mLength > 0){
           System.arraycopy(this.mBuffer, 0, tempBuf, 0, this.mLength);
        }
        this.mBuffer = tempBuf;
        this.mCapacity = destCapacity;
    }



    /**
     * 将 buffer缓冲区里的前面count大小的内容清除
     * @param count  清空的大小
     */
    private void remove(int count){
        if (this.mLength >= count){

            System.arraycopy(this.mBuffer, count, this.mBuffer, 0, this.mLength -count);

            this.mLength -= count;
            this.mPosition -= count;
            if (mPosition < 0){
                mPosition = 0;
            }

            Arrays.fill(this.mBuffer, this.mLength, this.mCapacity, (byte) 0);

        }else {
            this.mLength = 0;
            this.mPosition = 0;
            Arrays.fill(this.mBuffer, (byte)0);
        }
    }





    private final byte[] readBytes(int count) throws  EOFException{

        if (count <= 0){
            return null;
        }

        if (this.mLength - this.mPosition < count){
            throw  new EOFException();
        }

        byte[] tempBuffer = new byte[count];
        System.arraycopy(this.mBuffer, this.mPosition, tempBuffer, 0, count);

        this.mPosition += count;
        return tempBuffer;
    }



    private final int readInt() throws EOFException {

        if (this.mLength - this.mPosition < 4){
            throw new EOFException();
        }

        int ch1 = this.mBuffer[this.mPosition];
        int ch2 = this.mBuffer[this.mPosition + 1];
        int ch3 = this.mBuffer[this.mPosition + 2];
        int ch4 = this.mBuffer[this.mPosition + 3];
        byte[] bytes = new byte[]{(byte) ch1, (byte)ch2,(byte)ch3,(byte)ch4};

        this.mPosition += 4;
        return ByteBuffer.wrap(bytes).getInt();
    }


    private final short readShort() throws EOFException {

        if (this.mLength - this.mPosition < 2){
            throw new EOFException();
        }


        int ch1 = this.mBuffer[this.mPosition];
        int ch2 = this.mBuffer[this.mPosition + 1];
        byte[] bytes = new byte[]{(byte) ch1, (byte)ch2};

        this.mPosition += 2;
        return ByteBuffer.wrap(bytes).getShort();

    }


    private final byte readByte() throws EOFException{

        if (this.mLength - this.mPosition < 1){
            throw new EOFException();
        }

        return this.mBuffer[this.mPosition++];
    }
}
