package androidLearn.frame.easemobexample.utils;


public class PriceUtils {
  public static String getPriceString(float price) {
    if (price == 0) {
      return "义诊";
    } else if (price == -1) {
      return "停诊";
    }
    String priceString = String.valueOf(price);
    if (priceString.endsWith(".0") || priceString.endsWith(".00")) {
      return priceString.replace(".0", "").replace(".00", "");
    } else {
      return priceString;
    }
  }

  public static String getPriceStringWithZZ(float price) {
    String priceString = String.valueOf(price);
    if (priceString.endsWith(".0") || priceString.endsWith(".00")) {
      return priceString.replace(".0", "").replace(".00", "") + ".00";
    } else {
      return priceString;
    }
  }
}
