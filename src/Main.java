import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Not enough arguments. \n" +
                    "The client needs a HOST and PORT, for the server switch -s and PORT");
            System.exit(0);
        }

        if (args[0].equals("-s")){
            try (ServerSocket server = new ServerSocket(Integer.parseInt(args[1]))){
                while (true){
                    Socket clientConn = server.accept();
                    new Thread(()->{
                        try (OutputStream outputStream = clientConn.getOutputStream();
                             ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                             InputStream inputStream = clientConn.getInputStream()) {
                            try {
                                while (true) {
                                    try {
                                        int n = inputStream.read();
                                        objectOutputStream.writeObject(getFibonacciNumberByIndex(n));
                                    } catch (Exception e) {
                                        break;
                                    }
                                }
                            } finally {
                                Thread.currentThread().interrupt();
                                clientConn.close();
                            }
                        } catch (IOException ex) {
                            System.out.println(ex.getMessage());
                        }
                    }).start();
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        } else {

            try (Socket clientSocket = new Socket (args[0], Integer.parseInt(args[1]));
                 Scanner in = new Scanner(System.in);
                 OutputStream outputStream = clientSocket.getOutputStream();
                 InputStream inputStream = clientSocket.getInputStream();
                 ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)){
                while (true) {
                    System.out.print("Enter the Fibonacci number index: ");
                    int num;
                    try {
                        num = Integer.parseInt(in.nextLine());
                    } catch (Exception e){
                        System.out.println("Goodbye!");
                        break;
                    }

                    outputStream.write(num);
                    System.out.println("Fibonacci number: " + objectInputStream.readObject());
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static BigInteger getFibonacciNumberByIndex(int n){
        if (n < 1) return BigInteger.ZERO;

        BigInteger prePrevious = BigInteger.valueOf(1),
                previous = BigInteger.valueOf(1),
                current = BigInteger.valueOf(1);

        for (int i = 3; i <= n; i++, prePrevious = previous, previous = current)
            current = prePrevious.add(previous);

        return current;
    }
}
