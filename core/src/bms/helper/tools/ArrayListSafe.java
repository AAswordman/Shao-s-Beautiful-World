package bms.helper.tools;
import java.util.ArrayList;

public class ArrayListSafe<T> extends ArrayList<T> {
    private ArrayList<T> addlist=new ArrayList<>();
    private ArrayList<T> relist=new ArrayList<>();
    private boolean lock;

    private boolean clear;
    public ArrayListSafe() {
        super();
    }
    public ArrayListSafe(int size) {
        super(size);
    }
    public synchronized void lock() {
        lock = true;
    }
    public synchronized void unlock() {
        lock = false;
        if(clear){
            clear=false;
            clear();
        }
        for (T i : relist) {
            super.remove(i);
        }
        for (T i : addlist) {
            super.add(i);
        }
        relist.clear();
        addlist.clear();
    }
    @Override
    public synchronized boolean remove(Object e) {
        if (lock) {
            return relist.add((T)e);
        } else {
            return super.remove(e);
        }
    }

    @Override
    public synchronized boolean add(T e) {
        if (lock) {
            return addlist.add(e);
        } else {
            return super.add(e);
        }
    }

    @Override
    public synchronized void clear() {
        if (lock) {
            this.clear=true;
        } else {
            super.clear();
        }
    }


}
