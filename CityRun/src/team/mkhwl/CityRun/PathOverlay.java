package team.mkhwl.CityRun;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.Overlay;
import com.baidu.mapapi.Projection;

public class PathOverlay extends Overlay {
	private ArrayList<GeoPoint> algeopoints = null;
	public PathOverlay(ArrayList<GeoPoint> algeopoints) {
		super();
		this.algeopoints = algeopoints;
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, shadow);


		// 经度转像素
		Projection projection = mapView.getProjection();
		Point p = projection.toPixels(algeopoints.get(0), null);
		// 第一个画笔 画圆
		Paint fillPaint = new Paint();
		fillPaint.setColor(Color.RED);
		fillPaint.setAntiAlias(true);
		fillPaint.setStyle(Style.FILL);//实心填充

		// 第二个画笔 画线
		Paint paint = new Paint();
		paint.setColor(Color.BLUE);
		paint.setDither(true);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setStrokeWidth(14);

		// 连接
		int size = algeopoints.size();//得到坐标的个数
		Path path = new Path();//定义绘制路径
		path.moveTo(p.x+6, p.y+6);
		//canvas.drawCircle(p.x, p.y, 5.0f, fillPaint);
		//将绘制路径连接
		for (int i = 1; i < size; i++) {
			Point pt = projection.toPixels(algeopoints.get(i), null);
		//	canvas.drawCircle(pt.x, pt.y, 5.0f, fillPaint);// 将图画到上层画出实心圆
			path.lineTo(pt.x+6, pt.y+6);
		}
		// 画出路径
		canvas.drawPath(path, paint);
	}
}