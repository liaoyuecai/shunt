package cn.shunt.client;


class MessageWorker implements Runnable{

    private boolean runStatus = true;

    void destroy(){
        runStatus = false;
    }

    public void run() {

    }
}
