
public class Pendel 
{
	private double length;					//Laenge in Meter
	private double gravity;					//Erdbeschleunigung
	private double angle; 					//Anfangswinkel
	private double dt = 0.01;				//dt
	private double v;						//Anfangsgeschwindigkeit
	private double friction_const;			//Daempfungskonstante
	private Boolean friction;				//Bool Wert um Daempfun an & aus zu machen
	
	public Pendel()
	{
    	angle = Math.PI/4;
    	gravity = 9.81;		
    	length = 1;	
    	v = 0;	
    	friction_const = 0.05;
    	friction = false;
	}
	
	//Diese Methode berechnet die Beschleunigung, welche von der Erdbeschleunigung, der Stablaenge und vom Winkel abhaengt.
	public double acceleration (double g, double l, double phi)	
    {
		if(!friction)
			return (-g/l) * Math.sin(phi);						
		else
			return (-g/l) * Math.sin(phi) - friction_const * v;
    }	
	
	//Diese Methode berechnet die neue Geschwindigkeit, welche von der alten Geschwindigkeit abhaengt.
	public double velocity (double phi)			
	{
		double a = acceleration(gravity, length, phi);
		return (v + a * dt);
	}
	
	//Diese Methode berechnet den neuen Winkel, abhaengig vom alten Winkel.
	public void newPhi()		
	{
		v = velocity(angle);
		angle = angle + v * dt;
	}
	
	//Getter & Setter Methoden der Parameter
	public void setLength(double l)
	{
		this.length = l;
	}
	
	public double getLength()
	{
		return length;
	}
	
	public void setGravity(double g)
	{
		this.gravity = g;
	}
	
	public double getGravity()
	{
		return gravity;
	}
	
	public void setAngle(double phi)
	{
		this.angle = phi;
	}
	
	public double getAngle()
	{
		return angle;
	}

	public void setVelocity(double vel)
	{
		this.v = vel;
	}
	
	public double getVelocity()
	{
		return v;
	}

	public void setFriction(boolean f)
	{
		this.friction = f;
	}	
	
	public void setFrictionConst(double fc)
	{
		this.friction_const = fc;
	}	
}
