package androidLearn.frame.easemobexample.im.message.entity;

import androidLearn.frame.easemobexample.App;
import androidLearn.frame.easemobexample.R;
import androidLearn.frame.easemobexample.im.message.ImMessageType;
import android.text.TextUtils;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class ImProblemMessage extends ImMessage {

  private List<String> problems;

  protected ImProblemMessage(ImMessageInternalInterface internalMessage) {
    super(internalMessage);
  }

  @Override
  public String getTitle(){
    return App.getInstance().getString(R.string.medical_record);
  }

  public List<String> getProblems(){
    if(problems == null){
      String content = getContentString();
      if(TextUtils.isEmpty(content)){
        content = getText();
      }
      if(!TextUtils.isEmpty(content)){
        problems = new Gson().fromJson(content,
            new TypeToken<List<String>>() {
            }.getType());
      }
    }

    return problems;
  }

  @Override
  public ImMessageType getType() {
    return ImMessageType.notice_problem;
  }
}
