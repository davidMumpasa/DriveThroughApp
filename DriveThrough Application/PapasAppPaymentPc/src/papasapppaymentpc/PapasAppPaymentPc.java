 /*
  * To change this license header, choose License Headers in Project Properties.
  * To change this template file, choose Tools | Templates
  * and open the template in the editor.
  */
 package papasapppaymentpc;


 import za.ac.tut.dish.Dish;
 import za.ac.tut.order.Order;



 /**
  *
  * @author hp
  */
 public class PapasAppPaymentPc {

     public static void main(String[] args) {
         ServerSocket s = null;
         Socket socket = null;
         PrintWriter out = null;
         BufferedReader in = null;
         ObjectOutputStream obj = null;
         ObjectInputStream inObj = null;

         try {
             Queue<Order> orders = new LinkedList<>();
             s = new ServerSocket(3000);

             socket = s.accept();
             System.out.println("Connection established server is running on port: 3000");

             String data, data2;
             double amtDue = 0;

             out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
             in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             obj = new ObjectOutputStream(socket.getOutputStream());
             inObj = new ObjectInputStream(socket.getInputStream());

             data = in.readLine();

             orders = populateQueuWitheOrders(orders, inObj);
             amtDue = processOrders(data, orders);
             data2 = MakePayment(amtDue);
             sendDataToCollection(data2, orders);

             out.flush();
             out.close();

         } catch (IOException ex) {
             Logger.getLogger(PapasAppPaymentPc.class.getName()).log(Level.SEVERE, null, ex);
         } catch (ClassNotFoundException ex) {
             Logger.getLogger(PapasAppPaymentPc.class.getName()).log(Level.SEVERE, null, ex);
         }
     }

     public static Queue<Order> populateQueuWitheOrders(Queue<Order> orders, ObjectInputStream inObj) throws ClassNotFoundException, IOException {
         Order order;

         order = (Order) inObj.readObject();
         orders.add(order);

         return orders;
     }

     public static double processOrders(String data, Queue<Order> orders) {
         int choice, qty = 0;
         String name;
         double price = 0, amtDue = 0, totalAmtDue = 0;
         Dish dishA, dishB, dishC;
         String[] token = data.split("");
         List<Dish> dishes = new ArrayList();

         choice = Integer.parseInt(token[1]);

         for (Order order : orders) {
             dishes = order.getDishes();

             for (Dish dish : dishes) {
                 switch (choice) {

                     case 1:

                         dishA = dish;
                         price = dishA.getPrice();
                         qty = dishA.getQty();
                         name = dishA.getName();

                         break;
                     case 2:

                         dishB = dish;
                         price = dishB.getPrice();
                         qty = dishB.getQty();
                         name = dishB.getName();

                         break;
                     case 3:

                         dishC = dish;
                         price = dishC.getPrice();
                         qty = dishC.getQty();
                         name = dishC.getName();

                         break;
                 }

                 amtDue = price * qty;
                 totalAmtDue += amtDue;
             }

         }

         return totalAmtDue;
     }

     public static String MakePayment(double amtDue) {
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

     public static void sendDataToCollection(String data, Queue<Order> orders) throws IOException {

         InetAddress inet = null;
         ObjectOutputStream obj = null;
         PrintWriter out = null;
         Socket socket = null;

         inet = InetAddress.getByName("localhost");
         socket = new Socket(inet, 9191);

         obj = new ObjectOutputStream(socket.getOutputStream());
         out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

         for (Order order : orders) {

             obj.writeObject(order);
             out.println(data);

         }

         socket.close();

     }

 }
