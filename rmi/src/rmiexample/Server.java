package rmiexample;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Server {
    public static void main(String[] args) throws NamingException, RemoteException {
        Context context = new InitialContext();
        context .bind("rmi://localhost:1099/imath", new IMathImpl());
    }
}
interface IMath extends Remote {
    int add(int a, int b) throws RemoteException;
}
class IMathImpl extends UnicastRemoteObject implements  IMath{
    protected IMathImpl() throws RemoteException {
    }

    @Override
    public int add(int a, int b) {
        return a + b;
    }
}
