

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
 * Servlet implementation class get_org
 */
@WebServlet("/get_org")
public class get_org extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public get_org() {
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
	    PrintWriter out = response.getWriter();
	    int count = 0;
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
				response.setHeader("Content-type", "application/json; charset=utf-8");
				response.setCharacterEncoding("utf-8");
				JSONObject jb = JSONObject.fromObject(acceptjson);		//转换为JSONObject
				String get_by =  jb.getString("get_by");
				String key = jb.getString("key");
				ResultSet rs = null;
				if (get_by.equals("office_id")) {
					rs = stmt.executeQuery("SELECT * FROM Offices WHERE office_id = " + key);
					if (rs.next()) {
						rs = stmt.executeQuery("SELECT Organizations.*, OrgRelationships.* FROM Organizations, OrgRelationships WHERE Organizations.serve_office_id = " + key + " and OrgRelationships.higher_org_id = OrgRelationships.lower_org_id");
						while (rs.next()) {
							count++;
							String org_id = rs.getString("org_id");
							String name = rs.getString("name");
							JSONObject item = new JSONObject();
							item.put("org_id", org_id);
							item.put("name", name);
							result.put(item);
						}
						resultjson.put("total", count);
						resultjson.put("result", result);
					} else {
						response.sendError(400, "The office does not exist");
					}
				} else if (get_by.equals("org_id")) {
					rs = stmt.executeQuery("SELECT * FROM Organizations WHERE org_id = " + key);
					if (rs.next()) {
						rs = stmt.executeQuery("SELECT Organizations.*, OrgRelationships.* FROM Organizations, OrgRelationships WHERE OrgRelationships.higher_org_id = " + key + " and OrgRelationships.higher_org_id != OrgRelationships.lower_org_id and Organizations.org_id = OrgRelationships.lower_org_id");
						while (rs.next()) {
							count++;
							String org_id = rs.getString("org_id");
							String name = rs.getString("name");
							JSONObject item = new JSONObject();
							item.put("org_id", org_id);
							item.put("name", name);
							result.put(item);
						}
						resultjson.put("total", count);
						resultjson.put("result", result);
					} else {
						response.sendError(400, "The org does not exist");
					}
				} else {
					response.sendError(400, "Invalid input value");
				}
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
