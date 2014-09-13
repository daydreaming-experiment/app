package com.brainydroid.daydreaming.ui.filtering;

public class DoubleStringKey {

    private static String TAG = "DoubleStringKey";

    private String first;
    private String second;

    public DoubleStringKey(String first, String second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public int hashCode() {
        return (first + second).hashCode() + (second + first).hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || !(o instanceof DoubleStringKey)) return false;
        DoubleStringKey k = DoubleStringKey.class.cast(o);
        return (first.equals(k.getFirst()) && second.equals(k.getSecond())) ||
                (first.equals(k.getSecond()) && second.equals(k.getFirst()));
    }

    public String getFirst() {
        return first;
    }

    public String getSecond() {
        return second;
    }

    public String toString() {
        return "<" + first + "," + second + ">";
    }
}
