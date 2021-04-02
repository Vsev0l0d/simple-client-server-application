import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
                        OutputStream outputStream;
                        InputStream inputStream;
                        try {
                            outputStream = clientConn.getOutputStream();
                            inputStream = clientConn.getInputStream();
                            try {
                                while (true) {
                                    try {
                                        int n = inputStream.read();
                                        outputStream.write(getFibonacciNumberByIndex(n));
                                    } catch (Exception e) {
                                        break;
                                    }
                                }
                            } finally {
                                Thread.currentThread().interrupt();
                                clientConn.close();
                                if (inputStream != null) inputStream.close();
                                if (outputStream != null) outputStream.close();
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

            try (Socket clientSocket = new Socket (args[0], Integer.parseInt(args[1]))){
                OutputStream outputStream = clientSocket.getOutputStream();
                InputStream inputStream = clientSocket.getInputStream();

                Scanner in = new Scanner(System.in);
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
                    System.out.println("Fibonacci number: " + inputStream.read());
                }
                in.close();
                outputStream.close();
                inputStream.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static int getFibonacciNumberByIndex(int n){
        if (n < 1) return -1;
        int prePrevious = 1, previous = 1, current = 1;
        for (int i = 3; i <= n; i++){
            current = prePrevious + previous;
            prePrevious = previous;
            previous = current;
        }
        return current;
    }
}
