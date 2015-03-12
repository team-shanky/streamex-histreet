package com.example.histree;

/**import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class LocateActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_locate);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.locate, menu);
		return true;
	}

}*/
import android.os.Bundle;
import android.app.Activity;
import android.graphics.PointF;
import android.view.Menu;

public class LocateActivity extends Activity {

	private static final String COL_Y = null;
	private static final String COL_X = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_locate);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.locate, menu);
		return true;
	}


/**
* Calculates the end-point from a given source at a given range (meters)
* and bearing (degrees). This methods uses simple geometry equations to
* calculate the end-point.
* 
* @param point
*            Point of origin
* @param range
*            Range in meters
* @param bearing
*            Bearing in degrees
* @return End-point from the source given the desired range and bearing.
*/
public static PointF calculateDerivedPosition(PointF point,
            double range, double bearing)
    {
        double EarthRadius = 6371000; // m

        double latA = Math.toRadians(point.x);
        double lonA = Math.toRadians(point.y);
        double angularDistance = range / EarthRadius;
        double trueCourse = Math.toRadians(bearing);

        double lat = Math.asin(
                Math.sin(latA) * Math.cos(angularDistance) +
                        Math.cos(latA) * Math.sin(angularDistance)
                        * Math.cos(trueCourse));

        double dlon = Math.atan2(
                Math.sin(trueCourse) * Math.sin(angularDistance)
                        * Math.cos(latA),
                Math.cos(angularDistance) - Math.sin(latA) * Math.sin(lat));

        double lon = ((lonA + dlon + Math.PI) % (Math.PI * 2)) - Math.PI;

        lat = Math.toDegrees(lat);
        lon = Math.toDegrees(lon);

        PointF newPoint = new PointF((float) lat, (float) lon);

        return newPoint;

    }
public int x,y;
public int radius=6317000;
PointF center = new PointF(x, y);
final double mult = 1; // mult = 1.1; is more reliable
PointF p1 = calculateDerivedPosition(center, mult * radius, 0);
PointF p2 = calculateDerivedPosition(center, mult * radius, 90);
PointF p3 = calculateDerivedPosition(center, mult * radius, 180);
PointF p4 = calculateDerivedPosition(center, mult * radius, 270);
String strWhere =  " Where "
        + COL_X + " > " + String.valueOf(p3.x) + " And "
        + COL_X + " < " + String.valueOf(p1.x) + " And "
        + COL_Y + " < " + String.valueOf(p2.y) + " And "
        + COL_Y + " > " + String.valueOf(p4.y);
public static boolean pointIsInCircle(PointF pointForCheck, PointF center,
        double radius) {
    if (getDistanceBetweenTwoPoints(pointForCheck, center) <= radius)
        return true;
    else
        return false;
}

public static double getDistanceBetweenTwoPoints(PointF p1, PointF p2) {
    double R = 6371000; // m
    double dLat = Math.toRadians(p2.x - p1.x);
    double dLon = Math.toRadians(p2.y - p1.y);
    double lat1 = Math.toRadians(p1.x);
    double lat2 = Math.toRadians(p2.x);

    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2)
            * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    double d = R * c;

    return d;
}
}
