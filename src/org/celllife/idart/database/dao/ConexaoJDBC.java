package org.celllife.idart.database.dao;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.celllife.idart.commonobjects.iDartProperties;
import org.celllife.idart.database.hibernate.PrescriptionToPatient;
import org.celllife.idart.gui.alert.RiscoRoptura;

/**
 * Esta classe efectua conexao com a BD postgres e tem metodo para a manipulacao dos dados
 * @author EdiasJambaia
 *
 */

public class ConexaoJDBC {
	

Connection conn_db;  // Conexão com o servidor de banco de dados
Statement st;   // Declaração para executar os comandos
	
	public void conecta(String usr, String pwd) throws SQLException, ClassNotFoundException 
{



String url = "jdbc:postgresql://localhost/pharm?charSet=LATIN1";

// Carregar o driver
Class.forName("org.postgresql.Driver");

// Conectar com o servidor de banco de dados
System.out.println("Conectando ao banco de dados\nURL = " + url);
conn_db = DriverManager.getConnection(url, usr, pwd);

System.out.println("Conectado...Criando a declaração");
st = conn_db.createStatement();


}
	
	public Connection retornaConexao(String usr, String pwd) throws SQLException, ClassNotFoundException 
{



String url = "jdbc:postgresql://localhost/pharm?charSet=LATIN1";

// Carregar o driver
Class.forName("org.postgresql.Driver");

// Conectar com o servidor de banco de dados
System.out.println("Conectando ao banco de dados\nURL = " + url);
conn_db = DriverManager.getConnection(url, usr, pwd);

System.out.println("Conectado...Criando a declaração");
st = conn_db.createStatement();

return conn_db;

}
	
	
	
/**
 * Devolve a lista de PrescriptionToPatient, na verdade so devolve lista de tamanho 1
 * @param patientid
 * @return
 * @throws ClassNotFoundException
 * @throws SQLException
 */

public List<PrescriptionToPatient> listPtP(String patientid ) throws ClassNotFoundException, SQLException{
	
	conecta(iDartProperties.hibernateUsername, iDartProperties.hibernatePassword);
	
	String query=""
			+ "SELECT "
			+ "p.id, "
			+ "p.current, "
			+ "p.duration, "
			+ "p.reasonforupdate, "
			+ "p.notes, "
			+ "pt.patientid, "
			+ "rt.regimeesquema, "
			+" date_part(\'YEAR\',now())-date_part(\'YEAR\',pt.dateofbirth) as idade,  "
			+" p.motivomudanca AS motivomudanca, "
			+" p.datainicionoutroservico as datainicionoutroservico, "
			+ "lt.linhanome "
			+" FROM "
			+ "  patient pt, "
			+ "regimeterapeutico rt,  "
			+ "linhat lt, "
			+ "prescription AS p "
			+ "WHERE ("
			+ "(p.current = \'T\'::bpchar) "
			+ "AND "
			+ "(pt.id = p.patient) "
			+ "AND "
			+ "(pt.patientid=\'"+patientid+"\') "
			+ "AND "
			+ "(rt.regimeid=p.regimeid) "
			+ "AND "
			+ "(lt.linhaid=p.linhaid)) ";
	
	
	//ResultSet rs = st.executeQuery("select id, current, duration, reasonforupdate, notes, patientid from PrescriptioToPatient where patientid=\'"+patientid+"\'");
	List <PrescriptionToPatient> ptp = new ArrayList();
	ResultSet rs=st.executeQuery(query);
	if (rs != null)
        {
           
            while (rs.next())
            {

            	ptp.add(new PrescriptionToPatient(rs.getInt("id"), rs.getString("current"), rs.getInt("duration"),
            			rs.getString("reasonforupdate"),rs.getString("notes"), rs.getString("patientid"), rs.getString("regimeesquema"), rs.getInt("idade"), rs.getString("motivomudanca"),rs.getDate("datainicionoutroservico"),rs.getString("linhanome")));



           
            }
            rs.close(); // é necessário fechar o resultado ao terminar
        }
        System.out.println("Fechando a conexão");
        st.close();
        conn_db.close();
        return ptp;
	}

/**
 * Converte uma data para o formato DD Mon YYYY
 * @param date
 * @return
 * @throws ClassNotFoundException
 * @throws SQLException
 */
public Date converteData(String date) throws ClassNotFoundException, SQLException{
	
	Date data=new Date();
	conecta(iDartProperties.hibernateUsername, iDartProperties.hibernatePassword);
	
	String query="select to_date(\'"+date+"\', \'DD Mon YYYY\')";
	ResultSet rs=st.executeQuery(query);
	
	rs.next();
	data=rs.getDate("to_date");
	
	  st.close();
      conn_db.close();
      return data;
}



/**
 * devolve um vector de todos medicamentos com seus AMC, SALDO E QUANTIDADE DE REQUISICAO
 * @return
 * @throws ClassNotFoundException
 * @throws SQLException
 */
public Vector<RiscoRoptura> selectRiscoDeRopturaStock() throws ClassNotFoundException, SQLException{

	
		
	
	String query="SELECT drugname, consumo_max_ult_3meses, saldos "
			+ "FROM "
			+ "alimenta_risco_roptura";
	
	
    Vector<RiscoRoptura> riscos=new Vector<RiscoRoptura>();
 conecta(iDartProperties.hibernateUsername, iDartProperties.hibernatePassword);
   
	ResultSet rs=st.executeQuery(query);
	if (rs != null)
        {
           
            while (rs.next())
            {

RiscoRoptura rr=new RiscoRoptura(rs.getString("drugname"), rs.getInt("consumo_max_ult_3meses"), rs.getInt("saldos"), rs.getInt("consumo_max_ult_3meses")*3 - rs.getInt("saldos"));

riscos.add(rr);
System.out.println(" \n");

            } 
            rs.close(); // é necessário fechar o resultado ao terminar
        }
	
	
        System.out.println("Fechando a conexão");
        st.close();
        conn_db.close();
return riscos;

	
}

public int totalPacientesFarmacia(String startDate, String endDate) throws ClassNotFoundException, SQLException{
	System.out.println(" dadadadadadadadadadadadaddddddddddddd "+startDate+" AND "+endDate);
	String query=""
			+ "SELECT "
			+ " distinct packagedruginfotmp.patientid "
			+ " FROM  "
			+ " packagedruginfotmp "
			+ "  WHERE "
			+ "  packagedruginfotmp.pickupdate::timestamp::date >=  "
			+ "\'"+startDate+"\'"
			+ "AND packagedruginfotmp.pickupdate::timestamp::date <= "
			+ " \'"+endDate+"\'"
					+ " AND "
					+ " pickupdate IS NOT NULL"
;
     
	int total=0;

	 conecta(iDartProperties.hibernateUsername, iDartProperties.hibernatePassword);


		ResultSet rs=st.executeQuery(query);
		if (rs != null)
	        {
	           
	            while (rs.next())
	            {

	total++;

	

	            } 
	            rs.close(); //
	        }
    
	
	return total;
}

/**
 * Total de pacientes que iniciaram o tratamento de ARV
 * 
 */
public int totalPacientesInicio(String startDate, String endDate)throws ClassNotFoundException, SQLException{
	int total=0;
	

	

	String query=""
			+"SELECT  "
			+ "  distinct prescription.patient "
		
			+ " 	FROM "
			+ " packagedruginfotmp ,"
			+ " prescription, "
			+ "  patient "
			+ "  WHERE "
			+ " packagedruginfotmp.patientid=patient.patientid "
			+ " AND "
			+ "  patient.id=prescription.patient "
					+ " AND "
					+ " prescription.current=\'T\' AND prescription.ppe=\'F\' AND prescription.ptv=\'F\'  "
					+ " AND "
					+ " prescription.reasonforupdate=\'Inicia\' "
					+ " AND "
					+ "  packagedruginfotmp.pickupdate::timestamp::date >= "
					+ "\'"+startDate+"\'::timestamp::date "
							+ "AND  packagedruginfotmp.pickupdate::timestamp::date <= "
							+ " \'"+endDate+"\'::timestamp::date";


	 conecta(iDartProperties.hibernateUsername, iDartProperties.hibernatePassword);


		ResultSet rs=st.executeQuery(query);
		if (rs != null)
	        {
	           
	            while (rs.next())
	            {

 	total++;

	            } 
	            rs.close(); //
	        }
		return total;
	
}

/**
 * Total de pacientes na manutencao de ARV
 * 
 */
public int totalPacientesManter(String startDate, String endDate)throws ClassNotFoundException, SQLException{
	int total=0;
	
	
	

	String query=""
			+"SELECT  "
			+ "  distinct prescription.patient "
		
			+ " 	FROM "
			+ " packagedruginfotmp ,"
			+ " prescription, "
			+ "  patient "
			+ "  WHERE "
			+ " packagedruginfotmp.patientid=patient.patientid "
			+ " AND "
			+ "  patient.id=prescription.patient "
					+ " AND "
					+ " prescription.current=\'T\' AND prescription.ppe=\'F\' AND prescription.ptv=\'F\' "
					+ " AND "
					+ " prescription.reasonforupdate=\'Manter\' "
					+ " AND "
					+ "  packagedruginfotmp.pickupdate::timestamp::date >= "
					+ "\'"+startDate+"\'::timestamp::date "
							+ "AND  packagedruginfotmp.pickupdate::timestamp::date <= "
							+ " \'"+endDate+"\'::timestamp::date";


	 conecta(iDartProperties.hibernateUsername, iDartProperties.hibernatePassword);


		ResultSet rs=st.executeQuery(query);
		if (rs != null)
	        {
	           
	            while (rs.next())
	            {

 	total++;

	            } 
	            rs.close(); //
	        }
		return total;
	
}


/**
 * Total de pacientes na manutencao de ARV
 * 
 */
public int totalPacientesAlterar(String startDate, String endDate)throws ClassNotFoundException, SQLException{
	int total=0;
	
	
	

	String query=""
			+"SELECT  "
			+ "  distinct prescription.patient "
		
			+ " 	FROM "
			+ " packagedruginfotmp ,"
			+ " prescription, "
			+ "  patient "
			+ "  WHERE "
			+ " packagedruginfotmp.patientid=patient.patientid "
			+ " AND "
			+ "  patient.id=prescription.patient "
					+ " AND "
					+ " prescription.current=\'T\' AND prescription.ppe=\'F\' AND prescription.ptv=\'F\' "
					+ " AND "
					+ " prescription.reasonforupdate=\'Alterar\' "
					+ " AND "
					+ "  packagedruginfotmp.pickupdate::timestamp::date >= "
					+ "\'"+startDate+"\'::timestamp::date "
							+ "AND  packagedruginfotmp.pickupdate::timestamp::date <= "
							+ " \'"+endDate+"\'::timestamp::date";


	 conecta(iDartProperties.hibernateUsername, iDartProperties.hibernatePassword);


		ResultSet rs=st.executeQuery(query);
		if (rs != null)
	        {
	           
	            while (rs.next())
	            {

 	total++;

	            } 
	            rs.close(); //
	        }
		return total;
	
}

/**
 * Total de pacientes PPE
 * 
 */
public int totalPacientesPPE(String startDate, String endDate)throws ClassNotFoundException, SQLException{
	int total=0;
	
	
	

	String query=""
			+"SELECT  "
			+ "  distinct prescription.patient "
		
			+ " 	FROM "
			+ " packagedruginfotmp ,"
			+ " prescription, "
			+ "  patient "
			+ "  WHERE "
			+ " packagedruginfotmp.patientid=patient.patientid "
			+ " AND "
			+ "  patient.id=prescription.patient "
					+ " AND "
					+ " prescription.current=\'T\' "
					+ " AND "
					+ " prescription.ppe=\'T\' "
					+ " AND "
					+ "  packagedruginfotmp.pickupdate::timestamp::date >= "
					+ "\'"+startDate+"\'::timestamp::date "
							+ "AND  packagedruginfotmp.pickupdate::timestamp::date <= "
							+ " \'"+endDate+"\'::timestamp::date";


	 conecta(iDartProperties.hibernateUsername, iDartProperties.hibernatePassword);


		ResultSet rs=st.executeQuery(query);
		if (rs != null)
	        {
	           
	            while (rs.next())
	            {

 	total++;

	            } 
	            rs.close(); //
	        }
		return total;
	
}

/**
 * Total de pacientes PTV iNICIO
 * 
 */
public int totalPacientesPTVInicio(String startDate, String endDate)throws ClassNotFoundException, SQLException{
	int total=0;
	
	
	

	String query=""
			+"SELECT  "
			+ "  distinct prescription.patient "
		
			+ " 	FROM "
			+ " packagedruginfotmp ,"
			+ " prescription, "
			+ "  patient "
			+ "  WHERE "
			+ " packagedruginfotmp.patientid=patient.patientid "
			+ " AND "
			+ "  patient.id=prescription.patient "
					+ " AND "
					+ " prescription.current=\'T\' AND prescription.reasonforupdate=\'Inicia\'"
					+ " AND "
					+ " prescription.ptv=\'T\' "
					+ " AND "
					+ " packagedruginfotmp.pickupdate::timestamp::date >= "
					+ "\'"+startDate+"\'::timestamp::date "
							+ "AND  packagedruginfotmp.pickupdate::timestamp::date <= "
							+ " \'"+endDate+"\'::timestamp::date";


	 conecta(iDartProperties.hibernateUsername, iDartProperties.hibernatePassword);


		ResultSet rs=st.executeQuery(query);
		if (rs != null)
	        {
	           
	            while (rs.next())
	            {

 	total++;

	            } 
	            rs.close(); //
	        }
		return total;
	
}

/**
 * Total de pacientes PTV Manter
 * 
 */
public int totalPacientesPTVManter(String startDate, String endDate)throws ClassNotFoundException, SQLException{
	int total=0;
	
	
	

	String query=""
			+"SELECT  "
			+ "  distinct prescription.patient "
		
			+ " 	FROM "
			+ " packagedruginfotmp ,"
			+ " prescription, "
			+ "  patient "
			+ "  WHERE "
			+ " packagedruginfotmp.patientid=patient.patientid "
			+ " AND "
			+ "  patient.id=prescription.patient "
					+ " AND "
					+ " prescription.current=\'T\' AND prescription.reasonforupdate=\'Manter\'"
					+ " AND "
					+ " prescription.ptv=\'T\' "
					+ " AND "
					+ "  packagedruginfotmp.pickupdate::timestamp::date >= "
					+ "\'"+startDate+"\'::timestamp::date "
							+ "AND packagedruginfotmp.pickupdate::timestamp::date <= "
							+ " \'"+endDate+"\'::timestamp::date";


	 conecta(iDartProperties.hibernateUsername, iDartProperties.hibernatePassword);


		ResultSet rs=st.executeQuery(query);
		if (rs != null)
	        {
	           
	            while (rs.next())
	            {

 	total++;

	            } 
	            rs.close(); //
	        }
		return total;
	
}

/**
 * Total de pacientes PTV Alterar
 * 
 */
public int totalPacientesPTVAlterar(String startDate, String endDate)throws ClassNotFoundException, SQLException{
	int total=0;
	
	
	

	String query=""
			+"SELECT  "
			+ "  distinct prescription.patient "
		
			+ " 	FROM "
			+ " packagedruginfotmp ,"
			+ " prescription, "
			+ "  patient "
			+ "  WHERE "
			+ " packagedruginfotmp.patientid=patient.patientid "
			+ " AND "
			+ "  patient.id=prescription.patient "
					+ " AND "
					+ " prescription.current=\'T\' AND prescription.reasonforupdate=\'Alterar\'"
					+ " AND "
					+ " prescription.ptv=\'T\' "
					+ " AND "
					+ "  packagedruginfotmp.pickupdate::timestamp::date >= "
					+ "\'"+startDate+"\'::timestamp::date "
							+ "AND packagedruginfotmp.pickupdate::timestamp::date <="
							+ " \'"+endDate+"\'::timestamp::date";


	 conecta(iDartProperties.hibernateUsername, iDartProperties.hibernatePassword);


		ResultSet rs=st.executeQuery(query);
		if (rs != null)
	        {
	           
	            while (rs.next())
	            {

 	total++;

	            } 
	            rs.close(); //
	        }
		return total;
	
}

/**
 * Devolve o regime anterior de uma prescricao
 * 
 * @param idpaciente
 * @return
 * @throws ClassNotFoundException
 * @throws SQLException
 */
public String carregaRegime(int idpaciente) throws ClassNotFoundException, SQLException

{
	
	String query=" "
			+ " SELECT "
			+ " regimeterapeutico.regimeesquema "
			+ "  FROM "
			+ "  regimeterapeutico , "
			+ "  prescription "
			+ "  WHERE "
			+ "  prescription.regimeid =regimeterapeutico.regimeid "
			+ "  AND "
			+ "  prescription.patient="+idpaciente
			+ "  AND "
			+ "  prescription.current=\'T\'"
			+ "";
	
	
	 conecta(iDartProperties.hibernateUsername, iDartProperties.hibernatePassword);

String regime="";
		ResultSet rs=st.executeQuery(query);
		
		if (rs != null)
        {
           
            while (rs.next())
            {


regime=rs.getString("regimeesquema");

            } 
            rs.close(); //
        }
		
		return regime;
		
		
}


/**
 * Devolve ppe  duma prescricao
 * 
 * @param idpaciente
 * @return
 * @throws ClassNotFoundException
 * @throws SQLException
 */

public String carregaPpe(int idpaciente) throws ClassNotFoundException, SQLException

{
	
	String query=" "
			+ " SELECT "
			+ " ppe "
			+ "  FROM "
			+ "   "
			+ "  prescription "
			+ "  WHERE "
			+ "   "
			+ "  "
			+ "  prescription.patient="+idpaciente
			+ "  AND "
			+ "  prescription.current=\'T\'"
			+ "";
	
	
	 conecta(iDartProperties.hibernateUsername, iDartProperties.hibernatePassword);

	 String ppe="";
		ResultSet rs=st.executeQuery(query);
		
		if (rs != null)
        {
           
            while (rs.next())
            {


ppe=rs.getString("ppe");

            } 
            rs.close(); //
        }
		
		return ppe;
		
		
}

/**
 * Devolve a linha anterior duma prescricao
 * 
 * @param idpaciente
 * @return
 * @throws ClassNotFoundException
 * @throws SQLException
 */

public String carregaLinha(int idpaciente) throws ClassNotFoundException, SQLException

{
	
	String query=" "
			+ " SELECT "
			+ " linhat.linhanome "
			+ "  FROM "
			+ "  linhat , "
			+ "  prescription "
			+ "  WHERE "
			+ "  prescription.linhaid =linhat.linhaid "
			+ "  AND "
			+ "  prescription.patient="+idpaciente
			+ "  AND "
			+ "  prescription.current=\'T\'"
			+ "";
	
	
	 conecta(iDartProperties.hibernateUsername, iDartProperties.hibernatePassword);

String linha="";
		ResultSet rs=st.executeQuery(query);
		
		if (rs != null)
        {
           
            while (rs.next())
            {


linha=rs.getString("linhanome");

            } 
            rs.close(); //
        }
		
		return linha;
		
		
}



/**
 * Devolve ptv  duma prescricao
 * 
 * @param idpaciente
 * @return
 * @throws ClassNotFoundException
 * @throws SQLException
 */

public String carregaPtv(int idpaciente) throws ClassNotFoundException, SQLException

{
	
	String query=" "
			+ " SELECT "
			+ " ptv "
			+ "  FROM "
			+ "   "
			+ "  prescription "
			+ "  WHERE "
			+ "   "
			+ "  "
			+ "  prescription.patient="+idpaciente
			+ "  AND "
			+ "  prescription.current=\'T\'"
			+ "";
	
	
	 conecta(iDartProperties.hibernateUsername, iDartProperties.hibernatePassword);

	 String ptv="";
		ResultSet rs=st.executeQuery(query);
		
		if (rs != null)
        {
           
            while (rs.next())
            {


ptv=rs.getString("ptv");

            } 
            rs.close(); //
        }
		
		return ptv;
		
		
}

public int mesesDispensados(String startDate, String endDate) throws SQLException{
	
	int meses=0;
	double somaSemanas=0;
	
	String query=" SELECT "
			+ " weekssupply"
			+ " FROM packagedruginfotmp "
			+ ""
			+ " WHERE "
			+ "  packagedruginfotmp.pickupdate::timestamp::date >= "
			+ "\'"+startDate+"\'::timestamp::date "
					+ "AND packagedruginfotmp.pickupdate::timestamp::date <="
					+ " \'"+endDate+"\'::timestamp::date";
	
	ResultSet rs=st.executeQuery(query);
	
	if (rs != null)
    {
       
        while (rs.next())
        {


somaSemanas+=rs.getInt("weekssupply");

        } 
        rs.close(); //
        
        meses=(int) Math.round(somaSemanas/4);
    }
	
	
	return meses;
}

//Insere pacientes que nao estao ainda no SESP

public void inserPacienteIdart(String nid, String nomes, String apelido, Date dataderegisto) throws ClassNotFoundException, SQLException
{
	 conecta(iDartProperties.hibernateUsername, iDartProperties.hibernatePassword);
	 
	 st.executeUpdate(""
	 		+ " INSERT INTO registadosnoidart (nid, nomes, apelido, dataderegisto) "
	 		+ "  VALUES( \'"
	 		+ nid
	 		+ "\',\'"
	 		+ nomes
	 		+ "\',\'"
	 		+ apelido
	 		+ "\',\'"
	 		+new SimpleDateFormat("yyyy-MM-dd").format(dataderegisto)
	 		+ "\')");
	
}


//VE se o paciente foi dispensado ARV no periodo
public boolean dispensadonoperiodo(String patientid) throws ClassNotFoundException
{
	
	
	boolean foidispensado=false;
	try {
		conecta(iDartProperties.hibernateUsername, iDartProperties.hibernatePassword);
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	try {
		ResultSet rs= st.executeQuery(""
				+ " SELECT "
				+ "  patientid FROM  "
				+ "   packagedruginfotmp "
				+ "  WHERE "
				+ " to_timestamp(dateexpectedstring, \'DD Mon YYYY\')::DATE > now()::DATE "
				+ "  AND patientid = \'"+patientid
				+ ""
				+ "\'");
		
		
		if(rs!=null)
			while(rs.next())
				foidispensado=true;
				
		
		
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
			
	

	return foidispensado;
	

}
}

