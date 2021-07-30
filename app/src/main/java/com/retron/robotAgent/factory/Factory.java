package com.retron.robotAgent.factory;

public class Factory {

    public static WorkFactory createFactory(String ipAddress){
        WorkFactory workFactory = null;
        switch (ipAddress) {
            case "A":
                workFactory = new GxFactory();
                break;
            case "B":
                workFactory = new UsLamFactory();
                break;
            default:
                break;
        }
        return workFactory;
    }
}
