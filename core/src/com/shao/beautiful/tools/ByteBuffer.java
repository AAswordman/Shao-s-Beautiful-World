package com.shao.beautiful.tools;

public class ByteBuffer {

    private byte[] b;

    public void put(int blockPosition, byte data) {
        b[blockPosition]=data;
    }

    public byte[] array() {
        return b;
    }
    public static ByteBuffer wrap(byte[] b){
        ByteBuffer a=new ByteBuffer();
        a.b=b;
        return a;
    }
    public byte get(int pos){
        return b[pos];
    }
    
}
