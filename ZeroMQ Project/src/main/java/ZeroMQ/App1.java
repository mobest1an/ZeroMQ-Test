package ZeroMQ;

import org.zeromq.SocketType;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZContext;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Scanner;

public class App1 {

    public void run() {
        try (ZContext ctx = new ZContext()) {
            Socket socket = ctx.createSocket(SocketType.PAIR);
            System.out.print("IP для соединения: ");
            String ip = new Scanner(System.in).nextLine();
            System.out.print("Порт для соединения: ");
            String port = new Scanner(System.in).nextLine();
            String addr = "tcp://" + ip + ":" + port;
            socket.bind(addr);

            double minNetTime = Double.MAX_VALUE;
            double maxNetTime = 0;
            double avgNetTime = 0;
            double minLocalTime = Double.MAX_VALUE;
            double maxLocalTime = 0;
            double avgLocalTime = 0;
            long startLocalTime = 0;
            long endLocalTime = 0;
            boolean firstTimeFlag = true;

            DataManager manager = new DataManager();

            for (int i = 0; i < 50; i++) {
                byte[] bytes = manager.createData(40000); //5 kb
                DataObject dataObject = new DataObject(bytes, System.nanoTime());

                if (firstTimeFlag) {
                    firstTimeFlag = false;
                } else {
                    endLocalTime = System.nanoTime();
                    long elapsedLocalTime = endLocalTime - startLocalTime;
                    double elapsedLocalTimeInSecond = (double) elapsedLocalTime / 1000000000;
                    avgLocalTime = avgLocalTime + elapsedLocalTimeInSecond;
                    if (elapsedLocalTimeInSecond < minLocalTime)
                        minLocalTime = elapsedLocalTimeInSecond;
                    if (elapsedLocalTimeInSecond > maxLocalTime)
                        maxLocalTime = elapsedLocalTimeInSecond;
                }

                manager.send(socket, dataObject);
                DataObject dataObject1 = manager.recv(socket);

                startLocalTime = System.nanoTime();

                long elapsedNetTime = System.nanoTime() - dataObject1.getSendTime();
                double elapsedNetTimeInSecond = (double) elapsedNetTime / 1000000000;
                avgNetTime = avgNetTime + elapsedNetTimeInSecond;
                if (elapsedNetTimeInSecond < minNetTime)
                    minNetTime = elapsedNetTimeInSecond;
                if (elapsedNetTimeInSecond > maxNetTime)
                    maxNetTime = elapsedNetTimeInSecond;
            }

            avgNetTime = avgNetTime / 50;
            avgLocalTime = avgLocalTime / 49;

            System.out.printf("Среднее время локальной обработки: " + "%.6f\n", avgLocalTime);
            System.out.printf("Минимальное время локальной обработки: " + "%.6f\n", minLocalTime);
            System.out.printf("Максимальное время локальной обработки: " + "%.6f\n\n", maxLocalTime);
            System.out.printf("Среднее время задержки в сети: " + "%.6f\n", avgNetTime);
            System.out.printf("Минимальное время задержки в сети: " + "%.6f\n", minNetTime);
            System.out.printf("Максимальное время задержки в сети: " + "%.6f\n\n", maxNetTime);
            System.out.println("Количество пакетов отправлено: " + manager.getSendCount());
            System.out.println("Количество пакетов получено: " + manager.getReceiveCount());
            System.out.println("IP адрес: " + InetAddress.getLocalHost().getHostAddress());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new App1().run();
    }
}
