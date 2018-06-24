

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
 * Servlet implementation class get_offices
 */
@WebServlet("/get_offices")
public class get_offices extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public get_offices() {
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
				String office_id = jb.getString("office_id");
				ResultSet rs = stmt.executeQuery("SELECT * FROM Offices WHERE office_id = " + office_id);
				if (!rs.next()) {
					response.sendError(400, "The office does not exist");
				}
				rs = stmt.executeQuery("SELECT * FROM Offices WHERE office_id != " + office_id + " and higher_office_id = " + office_id);
				while(rs.next()) {
					count++;
					String result_office_id = rs.getString("office_id");
					String office_level = rs.getString("office_level");
					String higher_office_id = rs.getString("higher_office_id");
					String result_name = rs.getString("name");
					JSONObject item = new JSONObject();
					item.put("office_id", result_office_id);
					item.put("office_level", office_level);
					item.put("higher_office_id", higher_office_id);
					item.put("name", result_name);
					result.put(item);
				}
				resultjson.put("total", count);
				resultjson.put("reslut", result);
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
