package com.retron.robot.utils;

/**
 * eventbus发送通用的消息
 *
 * @param <T>
 */
public class EventBusMessage<T> {

    private int state;
    private T t;

    public EventBusMessage(int state) {
        this.state = state;
    }

    public EventBusMessage(int state, T t) {
        this.state = state;
        this.t = t;
    }

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "EventBusMessage{" +
                "state=" + state +
                ", t=" + t +
                '}';
    }
}
