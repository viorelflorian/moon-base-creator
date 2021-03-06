/**
 * 
 */
package gui.actions.menubar;

import gui.BaseFrame;
import gui.jaccordian.JAcordionBar;
import gui.jaccordian.jAccordionPanels.ChangeHeadingPanel;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;

/**
 * @author Viorel Florian
 * 
 */
@SuppressWarnings("serial")
public class EditHeadingAct extends AbstractAction {
  /** COMPONET_NAME */
  private final String  COMPONET_NAME = "Change Heading component";

	/**
	 * 
	 */
	public EditHeadingAct() {
	    super();
	    setDefaultPropreties();
	}

	/**
	 * Sets the default properties of the button
	 */
	private void setDefaultPropreties() {
		putValue(Action.NAME, this.COMPONET_NAME);
		KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_U,
				InputEvent.CTRL_DOWN_MASK);
		putValue(Action.ACCELERATOR_KEY, key);

	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
	  
	  BaseFrame.getInstance().getPropertiesPanel().addBar(this.COMPONET_NAME, new JAcordionBar(this.COMPONET_NAME, ChangeHeadingPanel.getInstance()));
	  

	}
}
