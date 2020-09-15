package com.kag;


/**
 * @Description： 测试【1个服务器 + 3个客户端】
 * @Author: 陈子康
 * @Date: 2020/5/8
 */
public class MainTest {

    public static void main(String[] args) {
        new ParaKServer();
    }

}


class Client01 {
    public static void main(String[] args) {
        new ParaKClient();
    }
}

class Client02 {
    public static void main(String[] args) {
        new ParaKClient();
    }
}

class Client03 {
    public static void main(String[] args) {
        new ParaKClient();
    }
}