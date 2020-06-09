/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.thrajon.consistenthash.sample;

import com.github.thrajon.consistenthash.ConsistentHashRouter;
import com.github.thrajon.consistenthash.Node;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.IntStream;

/**
 * a sample usage for routing a request to services based on requester ip
 */
public class MyServiceNode implements Node{
    private final String idc;
    private final String ip;
    private final int port;

    public MyServiceNode(String idc,String ip, int port) {
        this.idc = idc;
        this.ip = ip;
        this.port = port;
    }

    @Override
    public String getKey() {
        return idc + "-"+ip+":"+port;
    }

    @Override
    public String toString(){
        return getKey();
    }

    static Map<String, Integer> map = new LinkedHashMap<>();

    public static void main(String[] args) {

        //initialize 4 service node
        MyServiceNode node1 = new MyServiceNode("IDC1","127.0.0.1",8080);
        MyServiceNode node2 = new MyServiceNode("IDC2","127.0.0.1",8081);
        MyServiceNode node3 = new MyServiceNode("IDC3","127.0.0.1",8082);
        MyServiceNode node4 = new MyServiceNode("IDC4","127.0.0.1",8084);

        //hash them to hash ring
        ConsistentHashRouter<MyServiceNode> consistentHashRouter = new ConsistentHashRouter<>(Arrays.asList(node1,node2,node3,node4),5);//10 virtual node


        String requestIP1 = "192.168.0.1";


        MyServiceNode node5 = new MyServiceNode("IDC5","127.0.0.1",8080);//put new service online
        System.out.println("-------------putting new node online " +node5.getKey()+"------------");
        consistentHashRouter.addNode(node5,20);


        IntStream.range(1, 1000).boxed().forEach(i -> goRoute(consistentHashRouter,
                requestIP1+"-"+String.valueOf(i)));
        System.out.println(map);

        /*goRoute(consistentHashRouter,requestIP1);

        MyServiceNode node5 = new MyServiceNode("IDC2","127.0.0.1",8080);//put new service online
        System.out.println("-------------putting new node online " +node5.getKey()+"------------");
        consistentHashRouter.addNode(node5,2);

        goRoute(consistentHashRouter,requestIP1,requestIP2,requestIP3,requestIP4,requestIP5);

        consistentHashRouter.removeNode(node3);
        System.out.println("-------------remove node online " + node3.getKey() + "------------");
        goRoute(consistentHashRouter,requestIP1,requestIP2,requestIP3,requestIP4,requestIP5);*/


    }


    private static void goRoute(ConsistentHashRouter<MyServiceNode> consistentHashRouter ,String requestIp){


            String nodeKey = consistentHashRouter.routeNode(requestIp).getKey();
            Integer count = map.getOrDefault(nodeKey, 0) + 1;
            map.put(nodeKey, count);
    }
}
