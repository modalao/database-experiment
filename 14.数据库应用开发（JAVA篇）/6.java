import java.sql.*;
import java.util.Scanner;

public class Transfer {
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/finance?allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF8&useSSL=false&serverTimezone=UTC";
    static final String USER = "root";
    static final String PASS = "123123";
    /**
     * 转账操作
     *
     * @param connection 数据库连接对象
     * @param sourceCard 转出账号
     * @param destCard 转入账号
     * @param amount  转账金额
     * @return boolean
     *   true  - 转账成功
     *   false - 转账失败
     */
    public static boolean transferBalance(Connection connection,
                             String sourceCard,
                             String destCard, 
                             double amount){
        
        String sql="select b_balance,b_type from bank_card where b_number=?";
        String sql1="update bank_card set b_balance=? where b_number= ?";
        PreparedStatement pp=null;
        ResultSet res=null;
        double ans;
        try{
            //转入账号是否存在
            connection.setAutoCommit(false);
            pp=connection.prepareStatement(sql);
            pp.setString(1,destCard);
            res=pp.executeQuery();
            if(!res.next()) return false;
            //转出账号
            pp=connection.prepareStatement(sql);
            pp.setString(1,sourceCard);
            res=pp.executeQuery();
            if(!res.next()||res.getDouble("b_balance")<amount||(res.getString("b_type")).equals("信用卡")){
                return false;
            }
            //改变转出账号的余额
            pp=connection.prepareStatement(sql1);
            ans=res.getDouble("b_balance")-amount;
            pp.setDouble(1,ans);
            pp.setString(2,sourceCard);
            pp.executeUpdate();
            //改变转入账号余额
            pp=connection.prepareStatement(sql);
            pp.setString(1,destCard);
            res=pp.executeQuery();
            res.next();
            if((res.getString("b_type")).equals("信用卡")){
                ans=res.getDouble("b_balance")-amount;
            }
            else{
                ans=res.getDouble("b_balance")+amount;
            }
            pp=connection.prepareStatement(sql1);
            pp.setDouble(1,ans);
            pp.setString(2,destCard);
            pp.executeUpdate();
            connection.commit();
            return true;
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    // 不要修改main() 
    public static void main(String[] args) throws Exception {

        Scanner sc = new Scanner(System.in);
        Class.forName(JDBC_DRIVER);

        Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);

        while(sc.hasNext())
        {
            String input = sc.nextLine();
            if(input.equals(""))
                break;

            String[]commands = input.split(" ");
            if(commands.length ==0)
                break;
            String payerCard = commands[0];
            String  payeeCard = commands[1];
            double  amount = Double.parseDouble(commands[2]);
            if (transferBalance(connection, payerCard, payeeCard, amount)) {
              System.out.println("转账成功。" );
            } else {
              System.out.println("转账失败,请核对卡号，卡类型及卡余额!");
            }
        }
    }

}
