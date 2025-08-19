package com.DAO;
import java.sql.*;
import java.util.Scanner;

public class AccountDAO {
    static Connection con;

    static {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "system", "meghana47");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isExist(long acc_num, String pin) throws Exception {
        String sql = "select email from accounts where accountid=? and pin=?";
        PreparedStatement st = con.prepareStatement(sql);
        st.setLong(1, acc_num);
        st.setString(2, pin);
        ResultSet res = st.executeQuery();
        return res.next();
    }

    public static boolean isAccountExist(long acc_num) throws Exception {
        String sql = "select 1 from accounts where accountid=?";
        PreparedStatement st = con.prepareStatement(sql);
        st.setLong(1, acc_num);
        ResultSet res = st.executeQuery();
        return res.next();
    }

    public static void CreateAccount(String name, String email, String pin, Double bal) throws Exception {
        String sql = "insert into accounts values(acc_seq.NEXTVAL,?,?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setDouble(3, bal);
            ps.setString(4, pin);
            int res = ps.executeUpdate();
            if (res > 0) {
                String sql1 = "select accountid from accounts where email=?";
                PreparedStatement st = con.prepareStatement(sql1);
                st.setString(1, email);
                ResultSet rs = st.executeQuery();
                if (rs.next()) {
                    long accnum = rs.getLong("accountid");
                    System.out.println("✅ Account created successfully! Your account number is: " + accnum);
                }
            } else {
                System.out.println("❌ Unable to create account. Please try again.");
            }
        }
    }

    public static void ViewAccount(long acc_num, String pin) throws Exception {
        String sql = "select * from accounts where accountid=? and pin=?";
        if (isExist(acc_num, pin)) {
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setLong(1, acc_num);
                ps.setString(2, pin);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    String name = rs.getString("name");
                    String email = rs.getString("email");
                    System.out.println("✅ Account Details:");
                    System.out.println("Name: " + name);
                    System.out.println("Email: " + email);
                }
            }
        } else {
            System.out.println("❌ Incorrect account number or pin.");
        }
    }

    public static boolean WithDraw(long accountid, String pin, Double amount) throws Exception {
        String checkBalance = "select balance from accounts where accountid=? and pin=?";
        String withdrawSql = "update accounts set balance=balance-? where accountid=? and pin=?";
        if (isExist(accountid, pin)) {
            PreparedStatement ps1 = con.prepareStatement(checkBalance);
            ps1.setLong(1, accountid);
            ps1.setString(2, pin);
            ResultSet rs = ps1.executeQuery();
            if (rs.next() && rs.getDouble("balance") >= amount) {
                try (PreparedStatement ps = con.prepareStatement(withdrawSql)) {
                    ps.setDouble(1, amount);
                    ps.setLong(2, accountid);
                    ps.setString(3, pin);
                    ps.executeUpdate();
                    System.out.println("✅ Amount " + amount + " withdrawn successfully.");
                    return true;
                }
            } else {
                System.out.println("❌ Insufficient balance.");
            }
        } else {
            System.out.println("❌ Account not found or incorrect pin.");
        }
        return false;
    }

    public static void Transfer(long sender, String pin, long receiver, Double amount) throws SQLException {
        String recsql = "update accounts set balance=balance+? where accountid=?";
        int recres;
        try {
            con.setAutoCommit(false);

            // validate receiver
            if (!isAccountExist(receiver)) {
                System.out.println("❌ Transfer failed: Receiver account not found.");
                con.rollback();
                return;
            }

            // withdraw from sender
            if (WithDraw(sender, pin, amount)) {
                PreparedStatement ps = con.prepareStatement(recsql);
                ps.setDouble(1, amount);
                ps.setLong(2, receiver);
                recres = ps.executeUpdate();

                if (recres > 0) {
                    con.commit();
                    System.out.println("✅ Transferred Successfully to account number " + receiver);
                    logDetails(sender, amount, "Debited amount");
                    logDetails(receiver, amount, "Credited amount");
                } else {
                    con.rollback();
                    System.out.println("❌ Transfer failed while crediting receiver.");
                }
            } else {
                con.rollback();
                System.out.println("❌ Withdrawal failed. Transfer aborted.");
            }
        } catch (Exception e) {
            con.rollback();
            System.out.println("❌ Exception: Transfer failed and rolled back.");
        } finally {
            con.setAutoCommit(true);
        }
    }

    public static void logDetails(long acc, Double amount, String type) throws Exception {
        String sql = "Insert into transaction values(trans_seq.NEXTVAL,?,?,?,current_timestamp)";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setLong(1, acc);
        ps.setString(2, type);
        ps.setDouble(3, amount);
        ps.executeUpdate();
    }

    public static void TransHistory(long accnum) throws Exception {
        String sql = "select * from transaction where account_id=? order by timestamp desc";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setLong(1, accnum);
        ResultSet rs = ps.executeQuery();
        boolean found = false;
        while (rs.next()) {
            found = true;
            System.out.println("Transaction ID: " + rs.getInt("trans_id"));
            System.out.println("Account ID: " + rs.getLong("account_id"));
            System.out.println("Type: " + rs.getString("type"));
            System.out.println("Amount: " + rs.getDouble("amount"));
            System.out.println("Date & Time: " + rs.getTimestamp("timestamp"));
            System.out.println("--------------------------------------------------");
        }
        if (!found) {
            System.out.println("ℹ️ No transactions found for account " + accnum);
        }
    }

    public static void CheckBalance(long acc_num, String pin) throws Exception {
        String sql = "select balance from accounts where accountid=? and pin=?";
        if (isExist(acc_num, pin)) {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setLong(1, acc_num);
            ps.setString(2, pin);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Double bal = rs.getDouble("balance");
                System.out.println("💰 Available Balance: ₹" + bal);
            }
        } else {
            System.out.println("❌ Invalid account number or pin.");
        }
    }

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n*** ENTER YOUR CHOICE ***");
            System.out.println("1. Create Account");
            System.out.println("2. View Account Details");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer");
            System.out.println("5. Transaction History");
            System.out.println("6. Check Balance");
            System.out.println("7. Exit");
            System.out.print("Choice: ");

            int choice = sc.nextInt();
            sc.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    System.out.println("Enter name:");
                    String name = sc.nextLine();
                    System.out.println("Enter email:");
                    String email = sc.nextLine();
                    System.out.println("Set pin:");
                    String pin = sc.nextLine();
                    System.out.println("Enter initial amount:");
                    Double bal = sc.nextDouble();
                    CreateAccount(name, email, pin, bal);
                    break;

                case 2:
                    System.out.print("Enter account number: ");
                    long acc1 = sc.nextLong();
                    sc.nextLine();
                    System.out.print("Enter pin: ");
                    String pin1 = sc.nextLine();
                    ViewAccount(acc1, pin1);
                    break;

                case 3:
                    System.out.print("Enter account number: ");
                    long acc3 = sc.nextLong();
                    sc.nextLine();
                    System.out.print("Enter pin: ");
                    String pin3 = sc.nextLine();
                    System.out.print("Enter amount to withdraw: ");
                    Double amount1 = sc.nextDouble();
                    WithDraw(acc3, pin3, amount1);
                    break;

                case 4:
                    System.out.print("Enter your account number: ");
                    long acc4 = sc.nextLong();
                    sc.nextLine();
                    System.out.print("Enter your pin: ");
                    String pin4 = sc.nextLine();
                    if (isExist(acc4, pin4)) {
                        System.out.print("Enter receiver's account number: ");
                        long acc5 = sc.nextLong();
                        System.out.print("Enter amount to transfer: ");
                        Double amount2 = sc.nextDouble();
                        Transfer(acc4, pin4, acc5, amount2);
                    } else {
                        System.out.println("❌ Invalid sender account or pin.");
                    }
                    break;

                case 5:
                    System.out.print("Enter your account number: ");
                    long acc6 = sc.nextLong();
                    TransHistory(acc6);
                    break;

                case 6:
                    System.out.print("Enter your account number: ");
                    long acc7 = sc.nextLong();
                    sc.nextLine();
                    System.out.print("Enter pin: ");
                    String pin6 = sc.nextLine();
                    CheckBalance(acc7, pin6);
                    break;

                case 7:
                    System.out.println("✅ Exiting... Thank you!");
                    sc.close();
                    System.exit(0);

                default:
                    System.out.println("❌ Invalid choice. Try again.");
            }
        }
    }
}
