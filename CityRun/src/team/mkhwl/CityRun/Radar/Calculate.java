package team.mkhwl.CityRun.Radar;

import android.util.Log;

//import android.util.Log;

public class Calculate {
	private final static double EARTH_RADIUS = 6378137.0;
	public static final double Rc = 6378137;  // 赤道半径
	public static final double Rj = 6356725;  // 极半径 
	/**
	 * 计算两个GPS坐标的夹角
	 * @param lat_a
	 * @param lng_a
	 * @param lat_b
	 * @param lng_b
	 * @return
	 */
/*	public static double getAngle(double lat_a, double lng_a, double lat_b, double lng_b) {

	       double d = 0;

	       lat_a=lat_a*Math.PI/180;

	       lng_a=lng_a*Math.PI/180;

	       lat_b=lat_b*Math.PI/180;

	       lng_b=lng_b*Math.PI/180;

	       

	        d=Math.sin(lat_a)*Math.sin(lat_b)+Math.cos(lat_a)*Math.cos(lat_b)*Math.cos(lng_b-lng_a);

	       d=Math.sqrt(1-d*d);

	       d=Math.cos(lat_b)*Math.sin(lng_b-lng_a)/d;

	       d=Math.asin(d)*180/Math.PI;//将弧度转化成角度 

	       

//	     d = Math.round(d*10000);
	     Log.v("GPS转换角度",d+"");

	       return d;

	    }*/
	
	public static double getAngle(double lat_a, double lng_a, double lat_b, double lng_b) {

		double m_RadLng_a, m_RadLat_a,m_RadLng_b, m_RadLat_b;
		double Ec_a, Ed_a;

		 
		m_RadLng_a = lng_a * Math.PI/180.;//m_RadLo, m_RadLa;
		m_RadLat_a = lat_a * Math.PI/180.;
		m_RadLng_b = lng_b * Math.PI/180.;
		m_RadLat_b = lat_b * Math.PI/180.;
		
		Ec_a = Rj + (Rc - Rj) * (90.- lat_a) / 90.;
		Ed_a = Ec_a * Math.cos(m_RadLat_a);
		
		double dx = (m_RadLng_b - m_RadLng_a) * Ed_a;
		  double dy = (m_RadLat_b - m_RadLat_a) * Ec_a;
		  
//		  double out = Math.sqrt(dx * dx + dy * dy);
		  double angle = 0.0;
		   angle = Math.atan(Math.abs(dx/dy))*180./Math.PI;
		   // 判断象限
		   double dLo = lng_b - lng_a;
		   double dLa = lat_b - lat_a;
		  
		   if(dLo > 0 && dLa <= 0) {
		     angle = (90. - angle) + 90.;
		    }
		   else if(dLo <= 0 && dLa < 0) {
		     angle = angle + 180.;
		    }
		   else if(dLo < 0 && dLa >= 0) {
		     angle = (90. - angle) + 270;
		    }
		   Log.v("calculate",""+angle);
		  return angle;
		  

	    }
	/**计算两个GPS坐标之间的距离
	 * @param lat_a
	 * @param lng_a
	 * @param lat_b
	 * @param lng_b
	 * @return
	 */
	public static double getDistance(double lat_a, double lng_a, double lat_b, double lng_b) {
   


		double radLat1 = lat_a * Math.PI / 180;
		double radLat2 = lat_b  * Math.PI / 180;
		double a = radLat1 - radLat2;
		double b = lng_a  * Math.PI / 180 - lng_b  * Math.PI
				/ 180;
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
				+ Math.cos(radLat1) * Math.cos(radLat2)
				* Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;// 取WGS84标准参考椭球中的地球长半径(单位:m)
		s = Math.round(s * 10000) / 10000;

		return (int) s;

     }
	  
}
