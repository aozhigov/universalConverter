package ru.ozhigov.kontur.intern.universalConverter.utils;

public class Tuple<T1, T2> {
    private final T1 key;

    private final T2 value;

    public Tuple(T1 key, T2 value) {
        this.key = key;
        this.value = value;
    }

    public T1 getKey() {
        return key;
    }

    public T2 getValue() {
        return value;
    }

    @Override
    public boolean equals(Object other){
        if (other.getClass() == Tuple.class)
            return getKey().equals(((Tuple<T1, T2>) other).getKey())
                    && getValue().equals(((Tuple<T1, T2>) other).getValue());

        return false;
    }

    @Override
    public int hashCode(){
        return key.hashCode();
    }
}