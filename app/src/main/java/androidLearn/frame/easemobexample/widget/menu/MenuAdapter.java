
package androidLearn.frame.easemobExample.widget.menu;


import android.content.Context;
import androidLearn.frame.easemobExample.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;


public class MenuAdapter extends BaseAdapter {

  private Context context;

  private LayoutInflater inflater;

  private List<MenuDialog.MenuItem> mList = new ArrayList<>();


  public MenuAdapter(Context context) {
    this.context = context;
    inflater = LayoutInflater.from(this.context);

  }

  public void setMenu(List<MenuDialog.MenuItem> list) {
    mList.clear();
    mList.addAll(list);
  }

  public void addMenu(MenuDialog.MenuItem item){
    mList.add(item);
  }

  public void clearMenu(){
    mList.clear();
  }

  @Override
  public int getCount() {
    return mList.size();
  }

  @Override
  public MenuDialog.MenuItem getItem(int position) {
    return mList.get(position);
  }

  @Override
  public long getItemId(int position) {
    return mList.get(position).id;
  }

  @Override
  public View getView(final int position, View convertView, ViewGroup parent) {
    ViewHolder holder;

    MenuDialog.MenuItem item = getItem(position);

    if (convertView == null) {
      convertView = inflater.inflate(R.layout.layout_menu_item, null);
      holder = new ViewHolder(convertView);
      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }

    holder.text.setText(item.name);

    return convertView;
  }

  static class ViewHolder {
    TextView text;

    public ViewHolder(View view) {
      text = (TextView) view.findViewById(R.id.text);
    }
  }

}
