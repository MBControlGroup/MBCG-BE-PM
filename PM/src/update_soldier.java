

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

/**
 * Servlet implementation class update_soldier
 */
@WebServlet("/update_soldier")
public class update_soldier extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public update_soldier() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		final String URL = "jdbc:mysql://222.200.180.59:9000/MBDB?useSSL=false&characterEncoding=UTF-8";
		final String USER = "mbcsdev";
	    final String PASSWORD = "mbcsdev2018";
	    try {
			Class.forName("com.mysql.jdbc.Driver");		//注册驱动
		} catch (ClassNotFoundException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
			response.sendError(500, "JDBC initialization failed");
		}
		String acceptjson = "";
		Connection conn;
		System.out.println("连接服务器……");
		try {
			conn = DriverManager.getConnection(URL, USER, PASSWORD);
			Statement stmt = conn.createStatement();
			acceptjson = request.getParameter("data");		//获得json数据
			if (!acceptjson.equals("")) {
				request.setCharacterEncoding("UTF-8");
				//设置相应头的数据类型
				JSONObject jb = JSONObject.fromObject(acceptjson);		//转换为JSONObject
				String update_obj = jb.getString("update_obj");
				String soldier_id = jb.getString("soldier_id");
				String key = jb.getString("key");
				System.out.println(update_obj + " " + soldier_id + " " + key);
				if (update_obj.equals("rank")) {
					if (key.equals("SD") || key.equals("CM")) {
						stmt.executeUpdate("UPDATE Soldiers SET " + update_obj + " = '" + key + "'" + " WHERE soldier_id = " + soldier_id);
					} else {
						response.sendError(400, "Invalid input value");
					}
				} else if (update_obj.equals("name") || update_obj.equals("phone_num")) {
					stmt.executeUpdate("UPDATE Soldiers SET " + update_obj + " = '" + key + "'" + " WHERE soldier_id = " + soldier_id);
				} else if (update_obj.equals("serve_office_id")) {
					stmt.executeUpdate("UPDATE Soldiers SET " + update_obj + " =" + key + " WHERE soldier_id = " + soldier_id);
				} else if (update_obj.equals("serve_org_id")) {
					ResultSet rs = stmt.executeQuery("SELECT * FROM OrgSoldierRelationships WHERE soldier_id = " + soldier_id);
					int count = 0;
					while (rs.next()) {
						count ++;
					}
					if (count == 1) {
						stmt.executeUpdate("UPDATE OrgSoldierRelationships SET " + update_obj + " = " + key + " WHERE soldier_id = " + soldier_id);
					} else {
						response.sendError(403, "Cannot operate on soldier who belongs to multiple organizations");
					}
				} else {
					response.sendError(400, "Invalid input value");
				}
				
			} else {
				response.sendError(400, "no input");
			}
		} catch (SQLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
			response.sendError(400, "Invalid input value");
		}
		
	}

}
