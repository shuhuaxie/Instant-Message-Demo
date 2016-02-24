package androidLearn.frame.easemobExample.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;


public class PortraitView extends RoundedImageView {


  public PortraitView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init();
  }

  public PortraitView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public PortraitView(Context context) {
    super(context);
    init();
  }


  private void init() {
    setScaleType(ImageView.ScaleType.FIT_XY);
    setOval(true);

    setRoundBackground(true);
//		setBorderColor(0xffd9e5ec);
//		setBorderWidth(2);
//		setBorderColor(getResources().getColor(R.color.photo_border));
//		setBorderWidth((int)getResources().getDimension(R.dimen.border_size));
  }


}
