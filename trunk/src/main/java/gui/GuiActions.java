package gui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import utility.MyLogger;

//TODO explore EVENTHANDLER
/**
 * 
 */
public class GuiActions extends AbstractAction {
	JFrame f;
	GenericThread task;

	// GenericNewTask showProgDiag;

	GuiActions(JFrame f) {
		this.f = f;
	}

	// @Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource().equals(BaseFrame.fileNewItem)) {
			//System.out.println("New has been pressed");
			MyLogger.info(this, "New has been pressed");
			// TODO remove this
			final ProgressDialog progressDialog = new ProgressDialog(f);
			// final Thread progressBarThread = new Thread(progressDialog);

			@SuppressWarnings("rawtypes")
			SwingWorker sw = new SwingWorker() {

				@Override
				protected Object doInBackground() throws Exception {

					try {
					progressDialog.start();					
					
					new MoonWorkspaceInternalFrame();
					
					progressDialog.stop();
					} catch(Exception e) {
						Exception e1 = new Exception("Error while workspace creation.", e);
						MyLogger.error(this, "Error while workspace creation.", e1);
						throw e1;
					}

					return null;
				}

			};

			sw.execute();

		}

		if (e.getSource().equals(BaseFrame.fileExitItem)) {
			if (JOptionPane.showConfirmDialog(null,
					"Are you sure you wish to exit?") == JOptionPane.YES_OPTION) {
				f.setVisible(false);
				f.dispose();
				MyLogger.info(this, "Application closed on request of user");
				System.exit(0);
			}

		}
	}

}
