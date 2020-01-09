package test;

public class Service implements Runnable {
    int id;

    public Service(int id) {
        this.id = id;
    }

    public void run() {
        System.out.println("Service started:"+id);
        try {
            Thread.sleep((int)(Math.random() * 1000));
        } catch (InterruptedException e) {
            System.out.println("Interrupted:"+id);
        }
        System.out.println("Service finished:"+id);
    }
}
