package model.manager.reports;




import java.text.SimpleDateFormat;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import model.manager.excel.conversion.exceptions.ReportException;








import org.celllife.idart.commonobjects.LocalObjects;


import org.eclipse.swt.widgets.Shell;

public class RegisteredIdart1 extends AbstractJasperReport {
	
	
	private final Date theEndDate;
	private Date theStartDate;
	


	public RegisteredIdart1(Shell parent, Date theStartDate,
			Date theEndDate) {
		super(parent);
		
		this.theStartDate=theStartDate;
		this.theEndDate = theEndDate;
	}

	@Override
	protected void generateData() throws ReportException {
	}

	@Override
	protected Map<String, Object> getParameterMap() throws ReportException {




		
		// Set the parameters for the report
				Map<String, Object> map = new HashMap<String, Object>();
				

			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd");

		map.put("date", theStartDate);
		map.put("dateFormat", dateFormat.format(theStartDate));
		map.put("monthStart", dateFormat.format(theStartDate));
		//calStart.add(Calendar.MONTH, 1);


		map.put("monthEnd", dateFormat.format(theEndDate));
		map.put("dateEnd", theEndDate);


		map.put("path", getReportPath());

		map.put("facilityName", LocalObjects.currentClinic.getClinicName());



 


		
		return map;
	}


	@Override
	protected String getReportFileName() {
		return "RegisteredIdart";
	}
	



}
