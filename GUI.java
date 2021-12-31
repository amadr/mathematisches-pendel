import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class GUI extends JComponent implements Runnable, ActionListener
{
	private double x = 0;					//double Wert fuer die x-Koordinate
	private double y = 0;					//double Wert fuer die y-Koordinate
	
	private boolean start = false;			//bool Wert fuer die Start & Stopp Knoepfe
	
	static JSlider length_slider = new JSlider(0,0,50,1);		//Laengen Slider
	static JTextField length_entry = new JTextField("1");		//Laenge Eingabefeld
	static JSlider phi_slider = new JSlider(0,0,180,45);		//Winkel Slider [0 grad -180 grad]
	static JTextField phi_entry = new JTextField("45");			//Winkel Eingabefeld
	static JTextField gravity_entry = new JTextField("9.81");	//Erdbeschleunigung Eingabefeld
	static JTextField friction_entry = new JTextField("0.05");	//Daempfung Eingabefeld
	
	static JButton start_button = new JButton("Start");			//Start Knop
	static JButton stop_button = new JButton("Stopp");			//Stopp Knopf
	static JButton friction_button = new JButton("An");			//Daempfung Knopf
	
	private double l_entry = 1;				//eingegbene Laenge
	private double p_entry = Math.PI/4;		//eingegbener Winkel
	private double g_entry = 9.81;			//eingegebene Erdbeschleunigung
	private double f_entry = 0.05;			//eingegebene Daempfungskonstante
	
	private boolean entry_changed = false;			//bool Wert um zu ueberpruefen, ob Eingaben abgeaendert wurden
	private boolean friction_changed = false;		//bool Wert um zu ueberpruefen, ob der Daempfungswert geaendert wurde
	private boolean friction_button_on = false;		//bool Wert um zu ueberpruefen, ob der Daempfungs Knopf gedruckt wurde
	private boolean friction = false;				//bool Wert um die Daempfung zu toggeln
	
	Pendel p;
	
	public GUI()
	{
		p = new Pendel ();	
		
		//Armlaenge Slider und Textfeld Listener
    	length_slider.addChangeListener(new event());
    	length_entry.getDocument().addDocumentListener((SimpleDocumentListener) e -> {
    		try {
					l_entry = Double.parseDouble(length_entry.getText());
					entry_changed |= true;
    	      	} 
    		catch (NumberFormatException numberFormatException) {}
    	});
    	
    	//Winkel Slider und Textfeld Listener
    	phi_slider.addChangeListener(new event());			
    	phi_entry.getDocument().addDocumentListener((SimpleDocumentListener) e -> {
    		try {
					double w = Double.parseDouble(phi_entry.getText());
					p_entry = w * (Math.PI/180);
					entry_changed |= true;
    	      	} 
    		catch (NumberFormatException numberFormatException) {}
    	});
    	
    	//Erdbeschleunigung Textfeld Listener		
    	gravity_entry.getDocument().addDocumentListener((SimpleDocumentListener) e -> {
    		try {
					g_entry = Double.parseDouble(gravity_entry.getText());
					entry_changed |= true;
    	      	} 
    		catch (NumberFormatException numberFormatException) {}
    	});
    	
    	//Daempfung/ Reibung Textfeld Listener		
    	friction_entry.getDocument().addDocumentListener((SimpleDocumentListener) e -> {
    		try {
					f_entry = Double.parseDouble(friction_entry.getText());
					friction_changed = true;
    	      	} 
    		catch (NumberFormatException numberFormatException) {}
    	});
    	
    	//Start, Stopp und Daempfung Buttons Listener
    	start_button.addActionListener(this);
    	stop_button.addActionListener(this);
    	friction_button.addActionListener(this);

    	setPreferredSize (new Dimension (600, 600));
    	Thread th = new Thread (this);
        th.start ();
	}
	
	public void run ()
    {
		while (true)
        {
			//Wenn auf dem Start Knop gedruckt wurde, wird geprueft, ob die Daempfung an oder aus ist, bzw. wenn die Daempfung an ist,
			//ob dann der Wert von der Daempfung geaendert wurde, falls ja werden die entsprechenden Werte aktualisiert. Falls nicht 
			//wird einfach nur der neue Winkel berechnet und repaint() aufgerufen.
			if(start)
			{
				if(friction_changed && friction_button_on)
				{
					friction_changed = false;
					friction_button_on = false;
					p.setFrictionConst(f_entry);
					p.newPhi();
					repaint ();
				}
				else
				{
					p.newPhi();
					repaint();
				}
			}
			//Wenn der Stopp Knopf gedruckt wurde, wird geprueft, ob die Parameter veraendert wurden. Falls ja, werden die neuen
			//Parameter uebernommen.
			else if(!start)
			{
				if(entry_changed)
				{
					entry_changed = false;
					friction_changed = false;
					p.setFrictionConst(f_entry);
					p.setGravity(g_entry);
					p.setLength(l_entry);
					p.setAngle(p_entry);
					p.setVelocity(0);
				}
			}
			
			try { 
					Thread.sleep(10); 
				} 
			catch (InterruptedException e) {}
        }
	}
	
	@Override
	public void paintComponent (Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint (RenderingHints.KEY_ANTIALIASING,
                             RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth ();
        int h = getHeight ();
        
        //Hintergrund wird mit einem grauen Rechteck aufgefuellt.
        g.setColor(Color.GRAY);
        g.fillRect(0, 0, w, h);
       
        //Die Methode drawPendlum wird aufgerufen. Sie bekommt die Hoehe und Breite des Fensters & den neu berechneten Winkel uebergeben.
        drawPendlum (g2, w/2, h/2, p.getAngle());
    }
	
	public void drawPendlum (Graphics2D g2, int xm, int ym, double phi)
    {
    	g2.setPaint (Color.BLACK);
    	
    	//Mit rint() werden die Double Werte auf 2 Nachkomma Stellen gerundet.
    	//Berechnung & Anzeigen der Periode
    	g2.setFont(new Font("Arial", Font.PLAIN, 16)); 
    	double T = 2 * Math.PI * Math.sqrt(p.getLength()/p.getGravity());
    	g2.drawString("Periode = " + rint(T,2)+ " s", xm+30, 25);
    	//Umrechnung & Anzeigen des Winkels
    	double phi_grad = phi * 180/Math.PI;
    	g2.drawString("Winkel = " + rint(phi_grad,2) + "ae", xm+30, 45);
    	//Berechnung & Anzeigen der aktuellen Geschwindigkeit
    	g2.drawString("Geschwindigkeit = " + rint((p.getVelocity()),2) + " rad/s", xm+30, 65);

    	//Falls Daempfung an ist, wird ueberprueft, ob das Pendel kurz vor dem Stehen ist.
        if(friction)
        {
        	double x_neu = xm + (ym/2) * Math.sin(phi); 	//oder statt (ym/2), einen festen Wert
        	double y_neu = (ym/2) * Math.cos(phi); 			//oder statt (ym/2), einen festen Wert
        	        	
        	//Mit rint() werden die Double Werte auf 3 Nachkomma Stellen gerundet.
        	//Falls die Differenz zwischen den neu berechneten Koordinaten und den alten Koordinaten 0 ist,
        	//werden die Koordinaten nicht aktualisiert und man behaelt die alten Koordinaten bei. So wird ein "Zappeln"
        	//gegen dem Ende verhindert.
        	if(rint(x_neu,3)-rint(x,3) != 0) 
        	{
        		x = x_neu;
        	}
        	if (rint(y_neu,3) - rint(y,3) != 0)
        	{
        		y = y_neu;
        	}
        }
        //Wenn keine Daempfung an ist, dann werden die Koordinaten ganz normal berechnet.
        else
        {
        	//Die neuen x-Koordinaten werden berechnet.
        	x = xm + (ym/2) * Math.sin(phi); 				//oder statt (ym/2), einen festen Wert
        	
        	//Die neuen y-Koordinaten werden berechnet.
        	y = (ym/2) * Math.cos(phi); 					//oder statt (ym/2), einen festen Wert
        }
        
        //Hier wird der masselose Stab gezeichnet.
    	//g2.drawLine(xm, 5, (int) (x),(int) (y));
        //Line2D.Double line = new Line2D.Double(xm, ym+5, x, ym+y);
        Shape line = new Line2D.Double(xm, ym+5, x, ym+y);
        g2.draw(line);

        //Der Bezugspunkt wird gezeichnet.
    	//g2.fillOval(xm-5, 0, 10, 10);
    	Shape bP = new Ellipse2D.Double(xm-5, ym, 10, 10);
        g2.draw(bP);
    	
        //Die punktfoermige Masse wird gezeichnet. r ist der Radius der punktfoermigen Masse. 
    	int r = 35;
    	//g2.fillOval((int) ((x)-r/2), (int) (y - r/2), r, r);
    	Shape ball = new Ellipse2D.Double(x-r/2, ym+y-r/2, r, r);
        g2.fill(ball);
    }
	
	//Diese Methode rundet einen Double Wert auf eine bestimmte Anzahl von Nachkommastellen ab bzw. auf.
	private double rint(double value, int decimalPoints) 
	{
		double d = Math.pow(10, decimalPoints);
		return Math.rint(value * d) / d;
	}
	
	public static void main(String[] args)
	{
	    GUI_Start();
	}
	 
	public static void GUI_Start()
	{
		 JFrame f = new JFrame ("Mathematisches Pendel");
	     f.setLayout(new BorderLayout());
	     
	     //Erzeugen der GUI
	     GUI g = new GUI();
	      
	     //JPanel fuer alle Eingaben 
	     JPanel input = new JPanel();
	     input.setLayout(new GridLayout(5,3));
	      
	     //JLabel fuer die Laenge
	     JLabel length_label = new JLabel("Laenge [m]:");
	     length_slider.setMajorTickSpacing(1);
	     length_slider.setPaintTicks(true);
	     input.add(length_label);
	     input.add(length_slider);
	     input.add(length_entry);
	      
	     //JLabel fuer die Erdbeschleunigung
	     JLabel gravity_label = new JLabel("Erdbeschleunigung [m/sae]: ");
	     JLabel gravity_label1 = new JLabel("");
	     input.add(gravity_label);
	     input.add(gravity_label1);
	     input.add(gravity_entry);
	      
	     //JLabel fuer den Winkel
	     JLabel phi_label = new JLabel("Anfangswinkel [ae]:");
	     phi_slider.setMajorTickSpacing(45);
	     phi_slider.setPaintTicks(true);
	     input.add(phi_label);
	     input.add(phi_slider);
	     input.add(phi_entry);
	      
	     //JLabel fuer die Daempfung
	     JLabel friction_label = new JLabel("Daempfung:");
	     input.add(friction_label);
	     friction_button.setBackground(Color.GRAY);
	     input.add(friction_button);
	     input.add(friction_entry);
	      
	     //Start & Stopp Knaepfe dem input Panel hinzufaegen
	     start_button.setBackground(Color.GRAY);
	     //JLabel ss_label = new JLabel("<html><div style='text-align: center;'>" + "Mathematisches Pendel" + "</div></html>");
	     JLabel ss_label = new JLabel("Mathematisches Pendel", SwingConstants.CENTER);
	     stop_button.setBackground(Color.GRAY);
	     input.add(start_button);
	     input.add(ss_label);
	     input.add(stop_button );
	      
	     //input Panel dem Frame hinzufaegen
	     f.add(input, BorderLayout.NORTH);
	      
	     //GUI dem Frame hinzufaegen
	     f.add(g, BorderLayout.CENTER);
	      
	     new Thread(g).start();
	     f.pack();
	     f.setVisible(true);
	     f.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
	 }
	 
	//ChangeListener fuer die zwei Slider. Falls der Slider verschoben wird, wird der jeweilige JLabel angepasst.
	public class event implements ChangeListener
	{
		@Override
		public void stateChanged(ChangeEvent e) 
		{
			Object object = e.getSource();
			int val = ((JSlider) object).getValue();
			String value = String.valueOf(val);
			
			//Wenn der Laengen Slider bewegt wurde, dann Laengen Textfeld anpassen & entry_changed true setzen.
			if (object.equals(length_slider)) 
			{
				length_entry.setText(value);
				entry_changed = true;
			}
			//Wenn der Winkel Slider bewegt wurde, dann Winkel Textfeld anpassen & entry_changed true setzen.
			else if(object.equals(phi_slider))
			{
				phi_entry.setText(value);
				entry_changed = true;
			} 
		}
	}

	//actionListener Methoden fuer die Knoepfe.
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		Object obi = e.getSource();
		//Falls der Start Knopf gedruckt wurde, wird die Bool Variable start auf true gesetzt.
		if(obi == start_button)
		{
			start = true;
		}
		//Falls der Stopp Knopf gedruckt wurde, wird die Bool Variable start auf false gesetzt.
		else if(obi == stop_button)
		{
			start = false;
		}
		//Falls der Daempfungs Knopf gedruckt wurde, wird die Bool Variable friction auf true bzw. false gesetzt.
		//Zudem wird auch die Variable friction_button_on auf true bzw. false gesetzt.
		else if(obi == friction_button)
		{
			friction = !friction;
			p.setFriction(friction);
				
			if(friction)
			{
				friction_button.setText("Aus");
				friction_button_on = true;
			}
			else
			{
				friction_button.setText("An");
				friction_button_on = false;
			}
					
		}
	}
		
	//DocumentListener fuer die JTextFields.
	@FunctionalInterface
	public interface SimpleDocumentListener extends DocumentListener 
	{
		void update(DocumentEvent e);
		@Override
		default void insertUpdate(DocumentEvent e) {
			update(e);
		}
		@Override
		default void removeUpdate(DocumentEvent e) {
		    update(e);
		}
		@Override
		default void changedUpdate(DocumentEvent e) {
		    update(e);
		}
	}

}
