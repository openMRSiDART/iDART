package org.celllife.idart.icap.rascunhos;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class M {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		
boolean jadispensado=false;
			if(!jadispensado){
			
			
				MessageBox mbox = new MessageBox(new Shell(), SWT.YES | SWT.NO
									| SWT.ICON_QUESTION);
							mbox.setText("Levantamento de ARVs");
							mbox.setMessage("O paciente NIDNID já foi dispensado medicamentos neste periodo!\n Quer dispensar de novo?");
				
							switch (mbox.open()) {
							case SWT.YES:


								break;
							}
			


		}
			
	}


	
}
