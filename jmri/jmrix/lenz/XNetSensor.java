// XNetSensor.java

package jmri.jmrix.lenz;

import jmri.implementation.AbstractSensor;
import jmri.Sensor;

/**
 * Extend jmri.AbstractSensor for XPressNet layouts.
 * <P>
 * @author			Paul Bender Copyright (C) 2003-2010
 * @version         $Revision: 2.14 $
 */
public class XNetSensor extends AbstractSensor implements XNetListener {

    private int address;
    private int baseaddress; /* The result of integer division of the 
                                sensor address by 8 */
    private int nibble;      /* Is this sensor in the upper or lower 
				nibble for the feedback encoder */
    private int nibblebit;   /* Which bit in the nibble represents this 
				sensor */
    private String systemName;

    protected XNetTrafficController tc = null;

    public XNetSensor(String systemName, String userName,XNetTrafficController controller) {
        super(systemName, userName);
        tc=controller;
        init(systemName);
    }

    public XNetSensor(String systemName,XNetTrafficController controller) {
        super(systemName);
        tc=controller;
        init(systemName);
    }

    /**
     * Common initialization for both constructors
     */
    private void init(String id) {
        // store address
	systemName=new String(id);
        address = Integer.parseInt(id.substring(2,id.length()));
	// calculate the base address, the nibble, and the bit to examine
	baseaddress = ((address-1) / 8);
	int temp = (address-1) % 8;
	if(temp<4) {
	   // This address is in the lower nibble
	   nibble = 0x00;
	} else {
	   nibble = 0x10;
	}
	switch(temp%4) {
		case 0: nibblebit=0x01;
			break;
		case 1: nibblebit=0x02;
			break;
		case 2: nibblebit=0x04;
			break;
		case 3: nibblebit=0x08;
			break;
		default: nibblebit=0x00;
	}
        if (log.isDebugEnabled())
        	log.debug("Created Sensor " + systemName  + 
 				  " (Address " + baseaddress + 
                                  " position " + (((address-1) % 8) + 1) +
				  ")");
        this.requestUpdateFromLayout();
    }

    /**
     * request an update on status by sending an XPressNet message
     */
    public void requestUpdateFromLayout() {
       // To do this, we send an XpressNet Accessory Decoder Information 
       // Request.
       // The generated message works for Feedback modules and turnouts 
       // with feedback, but the address passed is translated as though it 
       // is a turnout address.  As a result, we substitute our base 
       // address in for the address. after the message is returned.
       XNetMessage msg = XNetMessage.getFeedbackRequestMsg(baseaddress,
                                                          (nibble==0x00));
       msg.setElement(1,baseaddress);
       tc.sendXNetMessage(msg, this);
    }

    /**
     * implementing classes will typically have a function/listener to get
     * updates from the layout, which will then call
     *      public void firePropertyChange(String propertyName,
     *      					Object oldValue,
     *                                          Object newValue)
     * _once_ if anything has changed state (or set the commanded state directly)
     * @param l
     */
    public void message(XNetReply l) {
	   if(log.isDebugEnabled()) log.debug("recieved message: " +l);
	   if(l.isFeedbackBroadcastMessage()) {
	     int numDataBytes = l.getElement(0)&0x0f;
	     for(int i=1;i<numDataBytes; i+=2) {
	        if((l.getFeedbackMessageType(i)==2) &&
                baseaddress==l.getFeedbackEncoderMsgAddr(i) &&
	        nibble == (l.getElement(i+1) & 0x10)) {
                   if(log.isDebugEnabled())
                        log.debug("Message for sensor " + systemName  + 
 				  " (Address " + baseaddress + 
                                  " position " + (address-(baseaddress*8)) +
				  ")");
		    if( ((l.getElement(i+1) & nibblebit)!=0) ^ _inverted) {
			    setOwnState(Sensor.ACTIVE);
		    }
		        else setOwnState(Sensor.INACTIVE);
	        }
	     }
	}
        return;
    }

    // listen for the messages to the LI100/LI101
    public void message(XNetMessage l) {
    }

    // Handle a timeout notification
    public void notifyTimeout(XNetMessage msg)
    {
       if(log.isDebugEnabled()) log.debug("Notified of timeout on message" + msg.toString());
    }

    public void dispose() {
        super.dispose();
    }

    static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(XNetSensor.class.getName());

}


/* @(#)XNetSensor.java */
