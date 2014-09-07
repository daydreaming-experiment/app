package com.brainydroid.daydreaming.sequence;

public interface NodeFactory<T> {

    public Node<T> create(T data);

}
