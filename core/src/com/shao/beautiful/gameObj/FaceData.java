package com.shao.beautiful.gameObj;

public class FaceData {
    private int data;
    private boolean restart;
    public FaceData(int msg) {
        this.data = msg;
        restart = analysisFace(Block.FACE_RESTART);
    }
    public FaceData() {}
    public boolean analysisFace(int type) {
        return (data & (1 << type)) != 0;
    }
    public void addFace(int type) {
        data += (int)Math.pow(2, type);
    }
    public byte getData() {
        return (byte)data;
    }
}
