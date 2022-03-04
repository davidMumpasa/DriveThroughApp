/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package papasapporderpc;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import java.util.logging.Level;
import java.util.logging.Logger;
import za.ac.tut.dish.Dish;
import za.ac.tut.disha.DishA;
import za.ac.tut.dishb.DishB;
import za.ac.tut.dishc.DishC;
import za.ac.tut.order.Order;

/**
 *
 * @author hp
 */
public class PapasAppOrderPc {

    /**
     * @param args the command line arguments
     */
    public static List generateUniqueOderNumber() {

        ArrayList<Integer> list = new ArrayList<Integer>();

        for (int i = 100; i < 1000; i++) {
            list.add(new Integer(i));
        }
        Collections.shuffle(list);

        return list;

    }

    public static int userChoice() {
        Scanner sc = new Scanner(System.in);
        int choice;
        System.out.println("Please select an option below." + "\n"
                + "1 --> dish A" + "\n"
                + "2 --> dish B" + "\n"
                + "3 --> dish C" + "\n"
                + "4 --> Exit" + "\n"
                + "Your choice: ");
        choice = sc.nextInt();

        return choice;
    }

    public static void main(String[] args) {
        // TODO code application logic here

        Socket socket = null;
        PrintWriter out = null;
        ObjectOutputStream obj = null;
        InetAddress innet = null;
        Dish dishA, dishB, dishC, dish = null;
        Scanner sc = new Scanner(System.in);
        int choice, count = 0, unOrder = 0, qty = 0;
        boolean isValid = true;
        String concatChoices = "";
        char confirmation;
        List<Integer> uniqueOrers = new ArrayList();
        List<Dish> dishes = new ArrayList();

        try {

            innet = InetAddress.getByName("localhost");
            socket = new Socket(innet, 3000);

            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            obj = new ObjectOutputStream(socket.getOutputStream());

            choice = userChoice();
            concatChoices = concatChoices.concat(choice + "");

            while (choice != 4) {

                do {

                    System.out.println("How may do you want?");
                    qty = sc.nextInt();

                    switch (choice) {
                        case 1:
                            dishA = new DishA("peperoni", 100.0, qty);
                            dish = (Dish) dishA;
                            dishes.add(dish);
                            break;
                        case 2:
                            dishB = new DishB("frize", 200.0, qty);
                            dish = (Dish) dishB;
                            dishes.add(dish);
                            break;
                        case 3:
                            dishC = new DishC("mayo", 300.0, qty);
                            dish = (Dish) dishC;
                            dishes.add(dish);
                            break;

                    }

                    System.out.println("Do you want to take anything else?" + "\n"
                            + "Y/N: ");
                    confirmation = sc.next().charAt(0);

                    if (confirmation == 'Y' || confirmation == 'y') {
                        isValid = false;
                        choice = userChoice();
                        concatChoices = concatChoices.concat(choice + "");

                    } else if (confirmation == 'N' || confirmation == 'n') {
                        isValid = true;
                        System.out.println("Order confirmed...");
                        choice = 4;
                    }

                } while (!isValid);

                count++;

            }

            uniqueOrers = generateUniqueOderNumber();
            for (int i = 0; i < uniqueOrers.size(); i++) {

                unOrder = uniqueOrers.get(i);
                Order order = new Order(unOrder, dishes);

                out.println(concatChoices);
                obj.writeObject(order);

            }

            obj.close();

        } catch (UnknownHostException ex) {
            Logger.getLogger(PapasAppOrderPc.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PapasAppOrderPc.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                System.out.println("Connection closing...");
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(PapasAppOrderPc.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

}
