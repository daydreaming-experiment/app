package com.brainydroid.daydreaming.ui.filtering;

import java.util.HashMap;

public class Tree<T> {

    private static String TAG = "Tree";

    private T data;
    private HashMap<Integer, Tree<T>> children = new HashMap<Integer, Tree<T>>();

    public Tree() {}

    public Tree(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean hasEdge(int e) {
        return children.containsKey(e);
    }

    public void addChild(int e, T child) {
        children.put(e, new Tree<T>(child));
    }

    public Tree<T> getChild(int e) {
        return children.get(e);
    }

    public HashMap<Integer, Tree<T>> getChildren() {
        return children;
    }
}
