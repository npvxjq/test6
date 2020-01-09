package test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class ServiceExecutor {
    class MyTask {
        Integer sid;
        Service serv;
        ArrayList<Integer> dep;
        Future future;
        boolean isRunning() {
            return future!=null && !future.isDone() && !future.isCancelled();
        }

        public MyTask(Integer sid, Service serv, ArrayList<Integer> dep) {
            this.sid = sid;
            this.serv = serv;
            this.dep = dep;
        }
    }
    ExecutorService executorService;
    List<MyTask> tasks;


    void exec(Map<Integer,Service> serviceList, Map<Integer,ArrayList<Integer>> dependencies,int maxThreads) throws Exception {

        tasks = new ArrayList<MyTask>();

        for(Integer i:serviceList.keySet()) {
            ArrayList<Integer> dep=new ArrayList<Integer>(dependencies.get(i));
            tasks.add(new MyTask(i,serviceList.get(i),dep));
        }
        //TODO check for invalid input

        executorService = Executors.newFixedThreadPool(maxThreads);
        boolean isContinue;

        do  {
            isContinue=tasks.stream().anyMatch(MyTask::isRunning);
            for (MyTask task : tasks) {
                if(task.future==null) {
                    if (task.dep.size() == 0) {
                        task.future = executorService.submit(task.serv, task.sid);
                        isContinue=true;
                    }
                } else
                {
                    if( task.future.isDone()) {
                        if(removeCompletedFromDependencies(task.sid) && !isContinue) {
                            isContinue=true;
                        };
                    }
                }

            }
        } while (isContinue);
        if( tasks.stream().anyMatch(x->{return x.future==null;}) ){
            //TODO figure out and display Cyclic dependencies
            System.out.println("Cyclic dependencies");
            throw new CyclicException();
        }
    }


    boolean removeCompletedFromDependencies(Integer id) {
        boolean r=false;
        for (MyTask task : tasks) {
            if(task.dep.remove(id) ) {
                r=true;
            };
        }
        return r;
    }

    void waitTerminationAndShutdown(int sec) {
        executorService.shutdown();
        try {
            if(executorService.awaitTermination(sec, TimeUnit.SECONDS)) {
                System.out.println("All finished");
            } else {
                System.out.println("Time over");
            }
        } catch (InterruptedException e) {
            System.out.println("Interrupted");
        } finally {
            executorService.shutdown();
        }

    }
}
