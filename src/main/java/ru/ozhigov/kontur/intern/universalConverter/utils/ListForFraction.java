package ru.ozhigov.kontur.intern.universalConverter.utils;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

public class ListForFraction<T> extends ArrayList<T> {
    public int idxDel = -1;
    HashMap<T, Integer> leftCounter;
    HashMap<T, Integer> rightCounter;

    public ListForFraction() {
        super();
        leftCounter = new HashMap<>();
        rightCounter = new HashMap<>();
    }

    @Override
    public boolean add(T value){
        if (idxDel == -1)
            leftCounter.put(value, leftCounter.getOrDefault(value, 0) + 1);
        else
            rightCounter.put(value, rightCounter.getOrDefault(value, 0) + 1);
        return super.add(value);
    }

    @Override
    public boolean addAll(Collection<? extends T> list){
        boolean flag = true;
        for (T item : list)
            if (!add(item))
                flag = false;
            return flag;
    }

    public void addDel(){
        idxDel = this.size();
    }

    public int numSize(){
        return idxDel == -1
                ? this.size()
                : idxDel;
    }

    public int denSize(){
        return idxDel == -1
                ? 0
                : this.size() - numSize();
    }

    public CopyOnWriteArraySet<T> numKeys(){
        return new CopyOnWriteArraySet<>(leftCounter.keySet());
    }

    public HashMap<T, Integer> numCounter(){
        return leftCounter;
    }

    public CopyOnWriteArraySet<T> denKeys(){
        return new CopyOnWriteArraySet<>(rightCounter.keySet());
    }

    public HashMap<T, Integer> denCounter(){
        return rightCounter;
    }

    public int getIdxDel(){
        return idxDel;
    }
}
