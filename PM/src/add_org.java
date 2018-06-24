

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
 * Servlet implementation class add_org
 */
@WebServlet("/add_org")
public class add_org extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public add_org() {
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
				String name = jb.getString("name");
				String serve_office_id = jb.getString("serve_office_id");
				String higher_org_id = jb.getString("higher_org_id");
				String admin_name = jb.getString("admin_name");
				String soldier_id = jb.getString("soldier_id");
				int admin_id = 0;
				ResultSet rs = stmt.executeQuery("SELECT * FROM Organizations ORDER BY org_id DESC");
				int org_id = 0;
				if (rs.next()) {
					org_id = rs.getInt("org_id") + 1;
				} else {
					org_id++;
				}
				if (higher_org_id.equals("")) {
					higher_org_id = String.valueOf(org_id);
				}
				System.out.println(higher_org_id);
				rs = stmt.executeQuery("SELECT * FROM Offices WHERE office_id = " + serve_office_id);
				ResultSet rs1 = stmt.executeQuery("SELECT * FROM Organizations WHERE org_id = " + higher_org_id);
				if (!rs1.next()) {
					response.sendError(400, "higher_org does not exist");
				}
				ResultSet rs2 = stmt.executeQuery("SELECT * FROM Soldiers WHERE soldier_id = " + soldier_id);
				if (!rs2.next()) {
					response.sendError(400, "The soldier does not exist");
				}
				ResultSet rs3 = stmt.executeQuery("SELECT * FROM Admins WHERE admin_account = '" + admin_name + "'");
				if (rs3.next()) {
					response.sendError(400, "Account has been used");
				}
				stmt.executeUpdate("INSERT INTO Organizations VALUES ( " + org_id + ", " + serve_office_id + ", '" + name + "')");
				stmt.executeUpdate("INSERT INTO OrgRelationships VALUES (NULL, " + higher_org_id + ", " + org_id + ")");
				stmt.executeUpdate("INSERT INTO Admins VALUES ( NULL, '" + admin_name + "', '88888888', 'OR', 1)");
				rs3 = stmt.executeQuery("SELECT * FROM Admins WHERE admin_account = '" + admin_name + "'");
				if (rs3.next()) {
					admin_id = rs3.getInt("admin_id");
				}
				stmt.executeUpdate("INSERT INTO OrgAdminRelationships VALUES (NULL, " + admin_id + ", " + org_id + ", " + soldier_id + ")");
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
