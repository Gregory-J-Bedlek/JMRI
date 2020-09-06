package jmri.jmrit.voltmeter;

import java.awt.event.*;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import jmri.*;
import jmri.jmrit.catalog.NamedIcon;
import jmri.util.JmriJFrame;

/**
 * Frame providing a simple LCD-based display of track voltage.
 * <p>
 * @author Ken Cameron Copyright (C) 2007
 * @author Mark Underwood Copyright (C) 2007
 * @author Andrew Crosland Copyright (C) 2020
 *
 * Adapted from ampmeter to display voltage.
 */
public class VoltMeterFrame extends JmriJFrame {

    private final List<Meter> voltageMeters = new ArrayList<>();
    private final List<Meter> currentMeters = new ArrayList<>();
    
    // GUI member declarations
    private JMenuBar menuBar;
    ArrayList<JLabel> digitIcons;
    JLabel decimal;
    JLabel milliVolt;
    JLabel volt;
    JLabel milliAmp;
    JLabel amp;
    JLabel percent;
    
    int iconWidth;
    int iconHeight;

    private PropertyChangeListener propertyChangeListener;

    private int displayLength;
    private boolean displayDP;

    private Meter meter;

    NamedIcon digits[] = new NamedIcon[10];
    NamedIcon decimalIcon;
    NamedIcon milliVoltIcon;
    NamedIcon voltIcon;
    NamedIcon milliAmpIcon;
    NamedIcon ampIcon;
    NamedIcon percentIcon;

    JPanel pane1;
    JPanel meterPane;
    
    public VoltMeterFrame() {
        super(Bundle.getMessage("TrackVoltageMeterTitle"));

        MeterManager mm = InstanceManager.getNullableDefault(MeterManager.class);
        if (mm == null) throw new RuntimeException("No meter manager exists");
        
        for (Meter m : mm.getNamedBeanSet()) {
            if ((m != null) && (m instanceof VoltageMeter)) voltageMeters.add(m);
            if ((m != null) && (m instanceof CurrentMeter)) currentMeters.add(m);
        }
        if (voltageMeters.isEmpty() && currentMeters.isEmpty()) throw new RuntimeException("No volt meter or amp meter exists");
        
        if (!voltageMeters.isEmpty()) {
            meter = voltageMeters.get(0);
            setTitle(Bundle.getMessage("TrackVoltageMeterTitle2", meter.getDisplayName()));
        } else {
            meter = currentMeters.get(0);
            setTitle(Bundle.getMessage("TrackCurrentMeterTitle2", meter.getDisplayName()));
        }
    }

    @Override
    public void initComponents() {
        //Load the images (these are now the larger version of the original gifs
        for (int i = 0; i < 10; i++) {
            digits[i] = new NamedIcon("resources/icons/misc/LCD/Lcd_" + i + "b.GIF", "resources/icons/misc/LCD/Lcd_" + i + "b.GIF");
        }
        decimalIcon = new NamedIcon("resources/icons/misc/LCD/decimalb.gif", "resources/icons/misc/LCD/decimalb.gif");
        milliVoltIcon = new NamedIcon("resources/icons/misc/LCD/millivoltb.gif", "resources/icons/misc/LCD/millivoltb.gif");
        voltIcon = new NamedIcon("resources/icons/misc/LCD/voltb.gif", "resources/icons/misc/LCD/voltb.gif");
        milliAmpIcon = new NamedIcon("resources/icons/misc/LCD/milliampb.gif", "resources/icons/misc/LCD/milliampb.gif");
        ampIcon = new NamedIcon("resources/icons/misc/LCD/ampb.gif", "resources/icons/misc/LCD/ampb.gif");
        percentIcon = new NamedIcon("resources/icons/misc/LCD/percentb.gif", "resources/icons/misc/LCD/percentb.gif");
        
        iconWidth = digits[0].getIconWidth();
        iconHeight = digits[0].getIconHeight();

        // Voltage readings are displayed as 3 digits with one decimal place
        displayLength = 3;
        displayDP = true;
        
        // init GUI
        percent = new JLabel(percentIcon);
        decimal = new JLabel(decimalIcon);
        milliVolt = new JLabel(milliVoltIcon);
        volt = new JLabel(voltIcon);
        milliAmp = new JLabel(milliAmpIcon);
        amp = new JLabel(ampIcon);
        
        buildContents();

        // Initially we want to scale the icons to fit the previously saved window size
        scaleImage();
        buildContents();
        
        meter.enable();

        update();

        // request callback to update time
        // Again, adding updates.
        propertyChangeListener = (java.beans.PropertyChangeEvent e) -> {
            update();
        };
        meter.addPropertyChangeListener(NamedBean.PROPERTY_STATE, propertyChangeListener);

        // Add component listener to handle frame resizing event
        this.addComponentListener(
                new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                scaleImage();
            }
        });

    }

    // Added method to scale the clock digit images to fit the
    // size of the display window
    synchronized public void scaleImage() {
        int frameHeight = this.getContentPane().getHeight()
                - meterPane.getInsets().top - meterPane.getInsets().bottom;
        int frameWidth = this.getContentPane().getWidth()
                - meterPane.getInsets().left - meterPane.getInsets().right;
        
        double hscale = ((double)frameHeight)/((double)iconHeight);
        double wscale = ((double)frameWidth)/((double)(iconWidth * digitIcons.size()));
        double scale = hscale < wscale? hscale:wscale;

        for (int i = 0; i < 10; i++) {
            digits[i].scale(scale,this);
        }
        decimalIcon.scale(scale,this);
        milliVoltIcon.scale(scale,this);
        voltIcon.scale(scale,this);
        milliAmpIcon.scale(scale,this);
        ampIcon.scale(scale, this);

        meterPane.revalidate();
        this.getContentPane().revalidate();
    }

    private void buildContents(){
        // clear the contents
        getContentPane().removeAll();

        pane1 = new JPanel();
        pane1.setLayout(new BoxLayout(pane1, BoxLayout.Y_AXIS));
        
        // Create menu bar
        menuBar = new JMenuBar();
        JMenu voltageMetersMenu = new JMenu(Bundle.getMessage("MenuVoltageMeters"));
        menuBar.add(voltageMetersMenu);
        for (Meter m : voltageMeters) {
            voltageMetersMenu.add(new SelectMeterAction(m.getDisplayName(), m));
        }

        JMenu currentMetersMenu = new JMenu(Bundle.getMessage("MenuCurrentMeters"));
        menuBar.add(currentMetersMenu);
        for (Meter m : currentMeters) {
            currentMetersMenu.add(new SelectMeterAction(m.getDisplayName(), m));
        }
        setJMenuBar(menuBar);

        meterPane = new JPanel();
        meterPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder()));
//        meterPane.setBorder(BorderFactory.createTitledBorder(
//            BorderFactory.createEtchedBorder(), Bundle.getMessage("TrackVoltage")));

        // build the actual multimeter display.
        meterPane.setLayout(new BoxLayout(meterPane, BoxLayout.X_AXIS));

        boolean isVoltage = (meter instanceof VoltageMeter);
        
        if (digitIcons == null) {
            digitIcons = new ArrayList<>(displayLength); // 1 decimal place precision.
            for(int i = 0;i<displayLength;i++) {
               digitIcons.add(i,new JLabel(digits[0]));
            }
        }
        
        for(int i=0;i<digitIcons.size()-1;i++){
            meterPane.add(digitIcons.get(i));
        }
        switch (meter.getUnit()) {
            case Milli:
                if (isVoltage) meterPane.add(milliVolt);
                else meterPane.add(milliAmp);
                break;
            case NoPrefix:
                meterPane.add(decimal);
                meterPane.add(digitIcons.get(digitIcons.size()-1));
                
                if (isVoltage) meterPane.add(this.volt);
                else meterPane.add(amp);
                break;
            case Percent:
            default:
                meterPane.add(decimal);
                meterPane.add(digitIcons.get(digitIcons.size()-1));
                meterPane.add(percent);
                break;
        }
//        meterPane.add(decimal);
//        meterPane.add(digitIcons.get(digitIcons.size()-1));
//        meterPane.add(volt);

        pane1.add(meterPane);
        getContentPane().add(pane1);
        
        getContentPane().setPreferredSize(meterPane.getPreferredSize());
        
        pack();
    }

    /**
     * Update the displayed value.
     * 
     * Assumes an integer value has an extra, non-displayed decimal digit.
     */
    synchronized void update() {
        double val = meter.getKnownAnalogValue();
        int value = (int)Math.floor(val *10); // keep one decimal place.
        
        String valStr;
        if (displayDP) valStr = String.format("%1.1f", val);
        else valStr = String.format("%1f", val);
        
        boolean scaleChanged = false;
        while ((digitIcons.size()+1) < valStr.length()) {
           digitIcons.add(0,new JLabel(digits[0]));
           scaleChanged = true;
        }
        
        if (scaleChanged){
            // clear the content pane and rebuild it.
            scaleImage();
            buildContents();
        }

        for(int i = digitIcons.size()-1; i>=0; i--){
            digitIcons.get(i).setIcon(digits[value%10]);
            value = value / 10;
        }
    }

    @Override
    public void dispose() {
        meter.disable();
        meter.removePropertyChangeListener(propertyChangeListener);
        super.dispose();
    }



    public class SelectMeterAction extends AbstractAction {

        private final Meter m;

        public SelectMeterAction(String actionName, Meter meter) {
            super(actionName);
            this.m = meter;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            meter.disable();
            meter.removePropertyChangeListener(NamedBean.PROPERTY_STATE, propertyChangeListener);
            
            meter = m;
            meter.addPropertyChangeListener(NamedBean.PROPERTY_STATE, propertyChangeListener);
            meter.enable();
            
            // Update the display
            digitIcons = null;
            
            buildContents();

            // Initially we want to scale the icons to fit the previously saved window size
            scaleImage();
            buildContents();
            
            if (meter instanceof VoltageMeter) {
                VoltMeterFrame.this.setTitle(Bundle.getMessage("TrackVoltageMeterTitle2", m.getDisplayName()));
            } else {
                VoltMeterFrame.this.setTitle(Bundle.getMessage("TrackCurrentMeterTitle2", m.getDisplayName()));
            }
        }
    }

}
