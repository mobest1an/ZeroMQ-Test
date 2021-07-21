package ZeroMQ;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ.Socket;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Scanner;

public class App2 {

    public void run() {
        try (ZContext ctx = new ZContext()) {
            Socket socket = ctx.createSocket(SocketType.PAIR);
            System.out.print("IP для соединения: ");
            String ip = new Scanner(System.in).nextLine();
            System.out.print("Порт для соединения: ");
            String port = new Scanner(System.in).nextLine();
            String addr = "tcp://" + ip + ":" + port;
            socket.connect(addr);

            double minNetTime = Double.MAX_VALUE;
            double maxNetTime = 0;
            double avgNetTime = 0;
            double minLocalTime = Double.MAX_VALUE;
            double maxLocalTime = 0;
            double avgLocalTime = 0;

            DataManager manager = new DataManager();

            for (int i = 0; i < 50; i++) {
                DataObject dataObject = manager.recv(socket);

                long startLocalTime = System.nanoTime();
                long elapsedNetTime = System.nanoTime() - dataObject.getSendTime();
                double elapsedNetTimeInSecond = (double) elapsedNetTime / 1000000000;
                avgNetTime = avgNetTime + elapsedNetTimeInSecond;
                if (elapsedNetTimeInSecond < minNetTime)
                    minNetTime = elapsedNetTimeInSecond;
                if (elapsedNetTimeInSecond > maxNetTime)
                    maxNetTime = elapsedNetTimeInSecond;

                byte[] bytes = manager.createData(40000); //5 kb
                DataObject dataObject1 = new DataObject(bytes, System.nanoTime());

                long endLocalTime = System.nanoTime();

                manager.send(socket, dataObject1);

                long elapsedLocalTime = endLocalTime - startLocalTime;
                double elapsedLocalTimeInSecond = (double) elapsedLocalTime / 1000000000;
                avgLocalTime = avgLocalTime + elapsedLocalTimeInSecond;
                if (elapsedLocalTimeInSecond < minLocalTime)
                    minLocalTime = elapsedLocalTimeInSecond;
                if (elapsedLocalTimeInSecond > maxLocalTime)
                    maxLocalTime = elapsedLocalTimeInSecond;
            }

            avgNetTime = avgNetTime / 50;
            avgLocalTime = avgLocalTime / 50;

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
        new App2().run();
    }
}
