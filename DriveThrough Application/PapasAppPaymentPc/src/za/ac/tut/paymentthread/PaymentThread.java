/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.ac.tut.paymentthread;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import papasapppaymentpc.PapasAppPaymentPc;
import za.ac.tut.dish.Dish;
import za.ac.tut.order.Order;

/**
 *
 * @author hp
 */
public class PaymentThread extends Thread {

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private ObjectOutputStream obj;
    private ObjectInputStream inObj;
    private ArrayList<PaymentThread> clients;
    private String userName;

    public PaymentThread(Socket socket/*,ArrayList<PaymentThread> clients*/) throws IOException {
        this.socket = socket;
        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        obj = new ObjectOutputStream(socket.getOutputStream());
        inObj = new ObjectInputStream(socket.getInputStream());
        this.clients = clients;
       
       
       

    }

    @Override
    public void run() {
        String data, data2 = null;
        Queue<Order> orders = new LinkedList<>();
        double amtDue = 0;

        try {

            while (true) {
                

                data = in.readLine();

                orders = populateQueuWitheOrders(orders);
                amtDue = processOrders(data, orders);
                data2 = MakePayment(amtDue);
                sendDataToCollection(data2, orders);

            }

        } catch (IOException ex) {
            Logger.getLogger(PaymentThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(PaymentThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Queue<Order> populateQueuWitheOrders(Queue<Order> orders) throws ClassNotFoundException, IOException {
        Order order;

        order = (Order) inObj.readObject();
        orders.add(order);

        return orders;
    }

    public double processOrders(String data, Queue<Order> orders) {
        int choice, qty = 0;
        String name;
        double price = 0, amtDue = 0;
        Dish dishA, dishB, dishC;
         String [] token = data.split("");

        choice = Integer.parseInt(token[1]);

        for (Order order : orders) {

            switch (choice) {

                case 1:

                    dishA = order.getDish();
                    price = dishA.getPrice();
                    qty = dishA.getQty();
                    name = dishA.getName();

                    break;
                case 2:

                    dishB = order.getDish();
                    price = dishB.getPrice();
                    qty = dishB.getQty();
                    name = dishB.getName();

                    break;
                case 3:

                    dishC = order.getDish();
                    price = dishC.getPrice();
                    qty = dishC.getQty();
                    name = dishC.getName();

                    break;
            }

            amtDue = price * qty;

        }

        return amtDue;
    }

    public String MakePayment(double amtDue) {
        double payment = 0, change = 0;
        boolean isValid = true;
        String data = "";
        Scanner sc = new Scanner(System.in);

        do {

            System.out.println("The amount due is" + amtDue + "\n"
                    + "Please make the payment ");
            payment = sc.nextDouble();

            if (payment < amtDue) {

                System.out.println(payment + "is Invalid Please make the right payment...");
                isValid = false;

            } else {

                isValid = true;
                change = payment - amtDue;
                System.out.println("Payment successfull!!!" + "\n"
                        + "Your change is: " + change);

            }

        } while (!isValid);

        data = data.concat(payment + "#" + change);

        return data;

    }

    public void sendDataToCollection(String data, Queue<Order> orders) throws IOException {
        System.out.println("did you send??????");

        for (PaymentThread pt : clients) {
            if ("collection".equals(pt.userName)) {
                for (Order order : orders) {
                    System.out.println("did you send??????");

                    obj.writeObject(order);
                    out.println(data);
                }
            }

        }
    }

}
