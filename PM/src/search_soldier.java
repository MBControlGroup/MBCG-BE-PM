

import java.io.IOException;
import java.io.PrintWriter;
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

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Servlet implementation class search_by_office
 */
@WebServlet("/search_by_office")
public class search_soldier extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public search_soldier() {
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
	    JSONArray result = new JSONArray();
	    JSONObject resultjson = new JSONObject();
	    int count = 0;
	    try {
			Class.forName("com.mysql.jdbc.Driver");		//注册驱动
		} catch (ClassNotFoundException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
			response.sendError(500, "JDBC initialization failed");
		}
		request.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		String acceptjson = "";
		Connection conn;
		System.out.println("连接服务器……");
		try {
			conn = DriverManager.getConnection(URL, USER, PASSWORD);
			Statement stmt = conn.createStatement();
			acceptjson = request.getParameter("data");		//获得json数据
			if (!acceptjson.equals("")) {
				request.setCharacterEncoding("utf-8");
				response.setHeader("Content-type", "application/json; charset=utf-8");
				response.setCharacterEncoding("utf-8");		//设置相应头的数据类型
				JSONObject jb = JSONObject.fromObject(acceptjson);		//转换为JSONObject
				String search_by = jb.getString("search_by");
				if (!(search_by.equals("soldier_id") || search_by.equals("rank") || search_by.equals("id_num") || search_by.equals("name") || search_by.equals("phone_num") || search_by.equals("serve_office_id") || search_by.equals("serve_org_id"))) {
					response.sendError(400, "Invalid input value");
				}
				String key = jb.getString("key");
				ResultSet rs = null;
				if (search_by.equals("serve_org_id")) {
					rs = stmt.executeQuery("SELECT * FROM Soldiers WHERE soldier_id in (SELECT soldier_id FROM OrgSoldierRelationships WHERE "+ search_by + " = " + key + ")");
				} else if (search_by.equals("soldier_id") || search_by.equals("phone_num") || search_by.equals("serve_office_id")) {
					rs = stmt.executeQuery("SELECT * FROM Soldiers WHERE "+ search_by + " = " + key);
				} else if (search_by.equals("rank") || search_by.equals("id_num") || search_by.equals("name")) {
					rs = stmt.executeQuery("SELECT * FROM Soldiers WHERE "+ search_by + " = '" + key + "'");
				} else {
					response.sendError(400, "Invalid input value");
				}
				while (rs.next()) {
					count++;
					JSONObject item = new JSONObject();
					String soldier_id = rs.getString("soldier_id");
					String rank = rs.getString("rank");
					String id_num = rs.getString("id_num");
					String name = rs.getString("name");
					String phone_num = rs.getString("phone_num");
					item.put("soldier_id", soldier_id);
					item.put("rank", rank);
					item.put("id_num", id_num);
					item.put("name", name);
					item.put("phone_num", phone_num);
					result.put(item);
				}
				resultjson.put("total", count);
				resultjson.put("result", result);
				out.write(resultjson.toString());
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
