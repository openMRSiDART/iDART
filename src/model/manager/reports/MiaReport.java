package model.manager.reports;


import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import model.manager.excel.conversion.exceptions.ReportException;








import org.celllife.idart.commonobjects.LocalObjects;
import org.celllife.idart.database.dao.ConexaoJDBC;
import org.celllife.idart.database.dao.ConexaoODBC;
import org.celllife.idart.database.hibernate.StockCenter;
import org.eclipse.swt.widgets.Shell;

public class MiaReport extends AbstractJasperReport {
	
	private final StockCenter stockCenter;
	private final Date theEndDate;
	private Date theStartDate;
	


	public MiaReport(Shell parent, StockCenter stockCenter, Date theStartDate,
			Date theEndDate) {
		super(parent);
		this.stockCenter = stockCenter;
		this.theStartDate=theStartDate;
		this.theEndDate = theEndDate;
	}

	@Override
	protected void generateData() throws ReportException {
	}

	@Override
	protected Map<String, Object> getParameterMap() throws ReportException {
//		Calendar calStart = Calendar.getInstance();
//		calStart.setTime(theStartDate);
//		calStart.set(Calendar.DATE, 1);
//		calStart.set(Calendar.HOUR_OF_DAY, 0);
//		calStart.set(Calendar.MINUTE, 0);
//		calStart.set(Calendar.SECOND, 0);
//		calStart.set(Calendar.MILLISECOND, 0);
//		
//		
//		Calendar calEnd = Calendar.getInstance();
//		 calEnd.setTime(theEndDate);
//		 calEnd.set(Calendar.DATE,1);
//		 calEnd.set(Calendar.HOUR_OF_DAY, 0);
//		 calEnd.set(Calendar.MINUTE, 0);
//		 calEnd.set(Calendar.SECOND, 0);
//		 calEnd.set(Calendar.MILLISECOND, 0);
		
		//total de pacientes
		ConexaoJDBC conn=new ConexaoJDBC();

		
		// Set the parameters for the report
				Map<String, Object> map = new HashMap<String, Object>();
				
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd");
			
			//Total de pacientes que levantaram arv 20 a 20
			int totalpacientesfarmacia = conn.totalPacientesFarmacia(dateFormat.format(theStartDate), dateFormat.format(theEndDate));
			System.out.println("Total de pacientes "+ totalpacientesfarmacia);
		
		
			int totalpacientesinicio = conn.totalPacientesInicio(dateFormat.format(theStartDate), dateFormat.format(theEndDate));
			System.out.println("Total de pacientes Inicio arv "+ totalpacientesinicio);
			
			
			int totalpacientesmanter = conn.totalPacientesManter(dateFormat.format(theStartDate), dateFormat.format(theEndDate));
			System.out.println("Total de pacientes a manter arv "+ totalpacientesmanter);
		
             int totalpacientesalterar =conn.totalPacientesAlterar(dateFormat.format(theStartDate), dateFormat.format(theEndDate));
 			System.out.println("Total de pacientes a alterar arv "+ totalpacientesalterar);
 			
 			
 			
 			
 			 int totalpacientesppe =conn.totalPacientesPPE(dateFormat.format(theStartDate), dateFormat.format(theEndDate));
  			System.out.println("Total de pacientes ppe "+ totalpacientesppe);
  			
  			
  			
  			 int totalpacientesptvmanter =conn.totalPacientesPTVManter(dateFormat.format(theStartDate), dateFormat.format(theEndDate));
  			System.out.println("Total de pacientes ptv manter "+ totalpacientesptvmanter);
  			
  			
  			
  			 int totalpacientesptvinicia =conn.totalPacientesPTVInicio(dateFormat.format(theStartDate), dateFormat.format(theEndDate));
   			System.out.println("Total de pacientes ptv inicia "+ totalpacientesptvinicia);
   			
   			
   		 int totalpacientesptvalterar =conn.totalPacientesPTVAlterar(dateFormat.format(theStartDate), dateFormat.format(theEndDate));
			System.out.println("Total de pacientes ptv alterar"+ totalpacientesptvalterar);
			
			
			int mesesdispensados=conn.mesesDispensados(dateFormat.format(theStartDate), dateFormat.format(theEndDate));
			System.out.println("Meses dispensados "+ mesesdispensados);
			
			ConexaoODBC conn2=new ConexaoODBC();
			int pacientesEmTarv=conn2.pacientesActivosEmTarv();
			
			System.out.println("Pacientes em tarv  "+ pacientesEmTarv);
			
		map.put("stockCenterId", new Integer(stockCenter.getId()));
		map.put("date", theStartDate);
		map.put("dateFormat", dateFormat.format(theStartDate));
		map.put("monthStart", dateFormat.format(theStartDate));
		//calStart.add(Calendar.MONTH, 1);
		

		
		map.put("monthEnd", dateFormat.format(theEndDate));
		map.put("dateEnd", theEndDate);
		map.put("stockCenterName", stockCenter.getStockCenterName());
		map.put("path", getReportPath());

		map.put("facilityName", LocalObjects.currentClinic.getClinicName());
		map.put("pharmacist1", LocalObjects.pharmacy.getPharmacist());
		map.put("pharmacist2", LocalObjects.pharmacy.getAssistantPharmacist());
		map.put("totalpacientesfarmacia", totalpacientesfarmacia);
		map.put("totalpacientesinicio",totalpacientesinicio);
		map.put("totalpacientesmanter",totalpacientesmanter);
		map.put("totalpacientesalterar",totalpacientesalterar);
		map.put("totalpacientesppe",totalpacientesppe);
		map.put("totalpacientesptvmanter",totalpacientesptvmanter);
		map.put("totalpacientesptvinicia",totalpacientesptvinicia);
		map.put("totalpacientesptvalterar",totalpacientesptvalterar);		
		map.put("mesesdispensados",mesesdispensados);
		map.put("pacientesEmTarv",pacientesEmTarv);
		map.put("dataelaboracao", new SimpleDateFormat("dd/MM/yyyy").format(new Date())); 
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return map;
	}


	@Override
	protected String getReportFileName() {
		return "MmiaReport";
	}
	



}
