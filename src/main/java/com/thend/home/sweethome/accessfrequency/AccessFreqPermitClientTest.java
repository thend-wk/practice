package com.thend.home.sweethome.accessfrequency;

import java.util.Random;

public class AccessFreqPermitClientTest {
	
    public static void main(String[] args) {
        
        Thread client = new Thread(){
            public void run() {
                Random ran = new Random();
                AccessFreqPermitClient test = new AccessFreqPermitClient();
                while(true) {
                    if(test.permitAccess(23)) {
                        System.out.println("access permitted!");
                    } else {
                        System.out.println("access not permitted!");
                    }
                    try {
                        Thread.sleep(ran.nextInt(10*1000));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
        };
        try {
            client.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        client.start();
    }
}
