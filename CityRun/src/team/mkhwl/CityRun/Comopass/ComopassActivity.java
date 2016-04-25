


/***************************************************
* Compass V4.0
*                      美化了界面
*                      
*                             8/13
*                              
***************************************************/
/**************************************************                                
* Compass V3.1
* 遇到的问题：①在添加字符串或者图片资源时 R文件不能自动生成id 
* 
*         ②系统分不清application 和 activity 的label 
*                               8/4/2012 
***************************************************/





package team.mkhwl.CityRun.Comopass;



import team.mkhwl.CityRun.R;
import android.R.string;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class ComopassActivity extends Activity {
   private ImageView imageView;
  // private TextView textview;
   private SensorManager sensorManager;//传感器管理对象
   private SensorListener listener = new SensorListener();
   private float mDegree = 0;
   private string dString;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Transparent);
        setContentView(R.layout.comopass_activity);
        
        imageView = (ImageView)this.findViewById(R.id.imageView);
        sensorManager=(SensorManager)getSystemService(Context.SENSOR_SERVICE);//获得传感器 （传感器是系统内置服务）
        imageView.setKeepScreenOn(true);//别让手机锁屏
        
        //textview = (TextView)this.findViewById(R.id.textView);
        //textview.setText("北偏东 ： "+Float.toString(mDegree));
    }
    //when Activity run into the front status
	@Override
	protected void onResume() {
		Sensor sensor =sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);//type 传感器类型     TYPE_ORIENTATION是方向传感器
		//得到传感器测量后的值 对其进行监听
		sensorManager.registerListener(listener, sensor,SensorManager.SENSOR_DELAY_GAME);//SENSOR_DELAY_GAME是一种传输速率
		
		super.onResume();
	}
	@Override
	protected void onPause() {
		sensorManager.unregisterListener(listener);
		super.onPause();
	}
	private final class  SensorListener implements SensorEventListener{
		private float preDegree = 0;
		public void onSensorChanged(SensorEvent event) {
			
			float degree = event.values[0];//存放方向值90
			
			RotateAnimation animation = new RotateAnimation(preDegree, -degree, Animation.RELATIVE_TO_SELF,0.5f, Animation.RELATIVE_TO_SELF,0.5f);//后两个值是相对的中心点
			animation.setDuration(200);//设置动画持续时间
			imageView.startAnimation(animation);//旋转
			preDegree = -degree;//-逆时针
			mDegree = degree;

		}
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
			
			
		}
	}

    
}