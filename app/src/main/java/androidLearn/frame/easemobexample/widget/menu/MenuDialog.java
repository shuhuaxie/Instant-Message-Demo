package androidLearn.frame.easemobexample.widget.menu;


import android.app.Dialog;
import android.content.Context;
import androidLearn.frame.easemobexample.R;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MenuDialog extends Dialog implements OnClickListener{
	
	public class MenuItem{
		public MenuItem(int id, String name, Object obj){
			super();
			this.id = id;
			this.name = name;
			this.obj = obj;
		}
		
		public int id;
		public String name;
		public Object obj;
	}
	
	private Context context;
	private ListView listview;
	private List<MenuItem> menu;
	private MenuAdapter adapter;
	
	public interface OnMenuClickListener{
		public void OnMenuItemClick(int id, Object obj);
	}
	
	private OnMenuClickListener onClickListenr;
	
	public MenuDialog(Context context) {
		super(context, R.style.Theme_Menu);
		this.context = context;
		
		try {
			init();
		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public MenuDialog setMenu(List<MenuItem> menu){
		adapter.setMenu(menu);
		return this;
	}

	public MenuDialog clearMenu(){
		adapter.clearMenu();
		return this;
	}

	public MenuDialog addMenuItem(int id, String name, Object obj){
		MenuItem item = new MenuItem(id, name, obj);
		adapter.addMenu(item);
		return this;
	}
	
	public MenuDialog setOnClickListener(OnMenuClickListener listener){
		onClickListenr = listener;
		return this;
	}
	
	private void init() throws JsonSyntaxException, IOException{

//		View view = LayoutInflater.from(context).inflate(R.layout.layout_menu,
//				null);

		setContentView(R.layout.layout_menu);
		Window window = getWindow();
		WindowManager.LayoutParams params = window.getAttributes();
		params.width = -1;
		params.height = -2;
		params.gravity = Gravity.CENTER;
		window.setAttributes(params);
		
		menu = new ArrayList<>();
		adapter = new MenuAdapter(context);
		
		listview = (ListView) findViewById(R.id.listView);
		listview.setAdapter(adapter);
		
		listview.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(android.widget.AdapterView<?> parent,
					View view, int position, long id) {
				MenuItem item = (MenuItem) parent.getItemAtPosition(position);
				if(onClickListenr != null)
					onClickListenr.OnMenuItemClick(item.id, item.obj);
				dismiss();
			}
			
		});
				
	}
	

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
}
