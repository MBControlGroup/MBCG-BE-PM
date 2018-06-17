

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

import net.sf.json.JSONObject;

/**
 * Servlet implementation class delete_soldier
 */
@WebServlet("/delete_soldier")
public class delete_soldier extends HttpServlet {
	private static final long serialVersionUID = 1L;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public delete_soldier() {
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
		final String URL = "jdbc:mysql://222.200.180.59:9000/MBDB?useSSL=false";
		final String USER = "mbcsdev";
	    final String PASSWORD = "mbcsdev2018";
	    try {
			Class.forName("com.mysql.jdbc.Driver");		//ע������
		} catch (ClassNotFoundException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
			response.sendError(500, "JDBC initialization failed");
		}
		request.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		String acceptjson = "";
		Connection conn;
		System.out.println("���ӷ���������");
		try {
			conn = DriverManager.getConnection(URL, USER, PASSWORD);
			Statement stmt = conn.createStatement();
			acceptjson = request.getParameter("data");		//���json����
			if (!acceptjson.equals("")) {
				response.setContentType("application/json; charset=utf-8");		//������Ӧͷ����������
				JSONObject jb = JSONObject.fromObject(acceptjson);		//ת��ΪJSONObject
				int soldier_id = jb.getInt("soldier_id");
				ResultSet rs1 = stmt.executeQuery("SELECT * FROM OrgAdminRelationships WHERE leader_sid=" + soldier_id);		//�������Ƿ��Ǹ�����
				if (rs1.next()) {
					response.sendError(403, "Cannot operate on leaders");
				}
				rs1.close();
				ResultSet rs = stmt.executeQuery("SELECT * FROM Soldiers WHERE soldier_id=" + soldier_id);		//��ѯ����Ƿ����
				if (rs.next()) {
					stmt.executeUpdate("DELETE FROM OrgSoldierRelationships WHERE soldier_id=" + soldier_id);
					stmt.executeUpdate("DELETE FROM SoldierRelationships WHERE lower_sid=" + soldier_id + " or " + "higher_sid=" + soldier_id);
					stmt.executeUpdate("UPDATE Soldiers SET serve_office_id = 4 WHERE soldier_id = " + soldier_id);
				} else {
					response.sendError(400, "User does not exist");
				}
				rs.close();
			} else {
				response.sendError(400, "no input");
			}
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
			response.sendError(400, "Invalid input value");
		}
		
	}

}
