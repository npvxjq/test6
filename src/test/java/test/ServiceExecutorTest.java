package test;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.*;

import java.sql.Array;
import java.util.*;


 class ServiceExecutorTest {
    static HashMap<Integer,Service> serviceList;
    static Map<Integer, ArrayList<Integer>> dependencies;
    static ServiceExecutor executor;

    @BeforeAll
    static void setup() {
        serviceList = new HashMap<Integer,Service>();
        dependencies = new HashMap<Integer, ArrayList<Integer>>();
        executor = new ServiceExecutor();
        for (int i = 0; i < 3; i++) {
            serviceList.put(i,new Service(i));
        }
        dependencies.put(0,new ArrayList<Integer>(Arrays.asList(2)));
        dependencies.put(1,new ArrayList<Integer>());
        dependencies.put(2,new ArrayList<Integer>(Arrays.asList(1)));


    }
     @Test
     void exec() throws Exception {
         executor.exec(serviceList,dependencies,2);
         executor.waitTerminationAndShutdown(10);
     }
    @Test
    void cyclic()  {

        serviceList.put(3,new Service(3));
        dependencies.put(3,new ArrayList<Integer>(Arrays.asList(4)));
        serviceList.put(4,new Service(4));

        dependencies.put(4,new ArrayList<Integer>(Arrays.asList(3)));
        Assertions.assertThrows(CyclicException.class, ()-> {
            executor.exec(serviceList,dependencies,2);
        });

        executor.waitTerminationAndShutdown(2);
    }
}