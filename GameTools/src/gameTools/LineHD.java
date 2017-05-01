package gameTools;

import java.util.Comparator;

import gameTools.map.Tile;

public class LineHD {
	public PointHD A; //start
	public PointHD B; //end
	public static final Comparator<LineHD> COMPARATOR = new Comparator<LineHD>() {

		@Override
		public int compare(LineHD o1, LineHD o2) {
			int kisebbek = o1.A.compareTo(o2.A); 
			if(kisebbek != 0) {
				return kisebbek;
			} else {
				return o1.B.compareTo(o2.B);
			}
		}
		
	};
	
	public LineHD(PointHD a, PointHD b) {
		int felt= a.compareTo(b);
		if( felt < 0) {
			A = a;
			B = b;
		} else {
			B = a;
			A = b;
		}
	}
	public LineHD(PointHD b) {
		A=new PointHD(0,0);
		B=b;
	}
	public LineHD() {
		A=new PointHD(0,0);
		B=new PointHD(0,0);
	}
	
	@Override
    public boolean equals(Object o) {
        if (o != null && o instanceof LineHD){
        	LineHD l = (LineHD) o;
//        	System.out.println("THIS: " + this);
//        	System.out.println("-LINE: " + l);
//        	System.out.println("-Dist-A: " +  A.distance(l.A));
//        	System.out.println("-Dist-B: " +  B.distance(l.B));
            return COMPARATOR.compare(this, l) == 0;
        } else {
//        	System.out.println("helloooo");
            return false;
        }
    }
	
	@Override
	public String toString(){
		return "LineHD(" + A + ", " + B + ")"; 
	}
}
