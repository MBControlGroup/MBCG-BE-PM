

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

/**
 * Servlet implementation class add_soldier
 */
@WebServlet("/add_soldier")
public class add_soldier extends HttpServlet {
	private static final long serialVersionUID = 1L;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public add_soldier() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		final String URL = "jdbc:mysql://222.200.180.59:9000/MBDB?useSSL=false&characterEncoding=UTF-8";
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
		try {
			System.out.println("���ӷ���������");
			conn = DriverManager.getConnection(URL, USER, PASSWORD);		//�������ݿ�
			Statement stmt = conn.createStatement();
			acceptjson = request.getParameter("data");		//���json����
			if (!acceptjson.equals("")) {
				response.setContentType("application/json; charset=utf-8");		//������Ӧͷ����������
				JSONObject jb = JSONObject.fromObject(acceptjson);		//ת��ΪJSONObject
				String serve_office_id = null;			//ȡ��ֵ
				serve_office_id = jb.getString("serve_office_id");
				String org_id = null;
				org_id = jb.getString("org_id");
				String commander_id = null;
				commander_id = jb.getString("commander_id");
				String rank = null;
				rank = jb.getString("rank");
				String name = null;
				name = jb.getString("name");
				String phone_num = null;
				phone_num = jb.getString("phone_num");
				String id_num = null;
				id_num = jb.getString("id_num");
				int soldier_id;
				ResultSet rs = stmt.executeQuery("SELECT id_num FROM Soldiers WHERE id_num='"+id_num+"'");		//��ѯ���֤�Ƿ����
				if (rs.next()) {
					response.sendError(400, "User already exists");
				} else {
					stmt.executeUpdate("INSERT INTO Soldiers VALUES (null, '"+rank+"','"+id_num+"','"+name+"',"+phone_num+", null,"+commander_id+","+serve_office_id+",1)");
					//����һ�������¼
					rs = stmt.executeQuery("SELECT soldier_id FROM Soldiers WHERE id_num='"+id_num+"'");		//��ѯ�²�������ID
					if (rs.next()) {
						soldier_id = rs.getInt("soldier_id");
						stmt.executeUpdate("INSERT INTO OrgSoldierRelationships VALUES (null," + org_id + "," + soldier_id + ")");		//�����-��֯��ϵ���������һ������
						ResultSet rs1 = stmt.executeQuery("SELECT soldier_id FROM Soldiers WHERE soldier_id="+commander_id);		//��ѯָ�ӹ��Ƿ����
						if (rs1.next()) {
							stmt.executeUpdate("INSERT INTO SoldierRelationships VALUES (null," + commander_id + "," +soldier_id + ")");	//�������ϵ�����һ��������
						} else {
							response.sendError(400, "Commander does not exist");
						}
						rs1.close();
						response.setStatus(201);
						jb.put("soldier_id", soldier_id);		//���������soldier_id��json����
						out.write(jb.toString());
					} else {
						response.sendError(400, "Failed to create" );
					}
				}
				rs.close();
			} else {
				response.sendError(400, "no input");
			}
	        stmt.close();
	        conn.close();
		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(400, "Invalid input value");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
