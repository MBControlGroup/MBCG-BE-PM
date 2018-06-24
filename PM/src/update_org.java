

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
 * Servlet implementation class update_org
 */
@WebServlet("/update_org")
public class update_org extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public update_org() {
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
				JSONObject jb = JSONObject.fromObject(acceptjson);		//转换为JSONObject
				String org_id = jb.getString("org_id");
				String name = jb.getString("name");
				String soldier_id = jb.getString("soldier_id");
				ResultSet rs = stmt.executeQuery("SELECT * FROM Soldiers WHERE soldier_id = " + soldier_id);
				if (!rs.next()) {
					response.sendError(400, "The soldier does not exist");
				}
				rs = stmt.executeQuery("SELECT * FROM Organizations WHERE org_id = " + org_id);
				if (!rs.next()) {
					response.sendError(400, "The org does not exist");
				}
				rs = stmt.executeQuery("SELECT * FROM OrgAdminRelationships WHERE leader_sid = " + soldier_id + " and org_id = " + org_id);
				if (rs.next()) {
					response.sendError(400, "The soldier is already an administrator");
				}
				stmt.executeUpdate("UPDATE Organizations SET name = '" + name + "' WHERE org_id = " + org_id);
				stmt.executeUpdate("UPDATE OrgAdminRelationships SET leader_sid = " + soldier_id + " WHERE org_id = " + org_id);
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
