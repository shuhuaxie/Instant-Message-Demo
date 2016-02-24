package androidLearn.frame.easemobExample.im.conversation.ImUi;

import android.content.Context;
import androidLearn.frame.easemobExample.App;
import androidLearn.frame.easemobExample.R;
import androidLearn.frame.easemobExample.utils.BitmapLoader;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

public class ConsultImageAdapter extends
    RecyclerView.Adapter<ConsultImageAdapter.ViewHolder> {

  private LayoutInflater mInflater;
  private List<String> mThumbnails;
  private List<String> mBigImages;
  private Context mContext;

  public ConsultImageAdapter(Context context, List<String> thumbnails, List<String> bigImages)
  {
    mInflater = LayoutInflater.from(context);
    mThumbnails = thumbnails;
    mBigImages = bigImages;
    mContext = context;
  }

  public static class ViewHolder extends RecyclerView.ViewHolder
  {
    public ViewHolder(View arg0)
    {
      super(arg0);
    }

    ImageView mImg;
  }

  @Override
  public ConsultImageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = mInflater.inflate(R.layout.listitem_consult_image,
        parent, false);
    ViewHolder viewHolder = new ViewHolder(view);

    viewHolder.mImg = (ImageView) view
        .findViewById(R.id.img);
    return viewHolder;
  }

  @Override
  public void onBindViewHolder(ConsultImageAdapter.ViewHolder holder, final int position) {
    BitmapLoader.displayImage(App.getInstance(), mThumbnails.get(position), holder.mImg, R.drawable.icon);
    holder.mImg.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String[] arr = (String[]) mBigImages.toArray(new String[mBigImages.size()]);
//        ShowAllImageActivity.openActivity(mContext, arr,null, position);
      }
    });
  }

  @Override
  public int getItemCount() {
    return mThumbnails.size();
  }
}
