import java.sql.*;

public class Transform {
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/sparsedb?allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF8&useSSL=false&serverTimezone=UTC";
    static final String USER = "root";
    static final String PASS = "123123";

    /**
     * 向sc表中插入数据
     *
     */
    public static void insertSC(Connection con,int sno,String name,int value){
        String sql="insert into sc values(?,?,?)";
        try{
            PreparedStatement pp=con.prepareStatement(sql);
            pp.setInt(1,sno);
            pp.setString(2,name);
            pp.setInt(3,value);
            pp.executeUpdate();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try{
            Class.forName(JDBC_DRIVER);
            Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
            String[] subject = {"chinese", "math", "english", "physics", "chemistry", "biology", "history", "geography", "politics"};
            ResultSet res=con.createStatement().executeQuery("select * from entrance_exam");
            while(res.next()){
                int sno=res.getInt("sno");
                for(String str:subject){
                    int value=res.getInt(str);
                    if(!res.wasNull())
                        insertSC(con,sno,str,value);
                }
            }
        }
        catch(ClassNotFoundException e){
            e.printStackTrace();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
