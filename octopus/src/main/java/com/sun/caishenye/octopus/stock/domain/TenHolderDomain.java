package com.sun.caishenye.octopus.stock.domain;

import com.sun.caishenye.octopus.common.Constants;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 十大股东
 */
@ToString
public class TenHolderDomain {
    // 公司代码
    @Setter
    @Getter
    private String companyCode;

    // 公司简称
    @Setter
    @Getter
    private String companyName;

    // 报告期
    @Setter
    @Getter
    private String time;

//    @Setter
//    @Getter
//    private List<Item> items = new ArrayList<>();

    @Setter
    @Getter
    private Item item1 = new Item();
    @Setter
    @Getter
    private Item item2 = new Item();
    @Setter
    @Getter
    private Item item3 = new Item();
    @Setter
    @Getter
    private Item item4 = new Item();
    @Setter
    @Getter
    private Item item5 = new Item();
    @Setter
    @Getter
    private Item item6 = new Item();
    @Setter
    @Getter
    private Item item7 = new Item();
    @Setter
    @Getter
    private Item item8 = new Item();
    @Setter
    @Getter
    private Item item9 = new Item();
    @Setter
    @Getter
    private Item item10 = new Item();

    @Setter
    @Getter
    public static class Item {

        // 较上期变动
        private String chg;
        // 持股数量
        private String heldNum;
        // 持股比例
        private String heldRatio;
        // 股东名称
        private String holderName;
    }

    public String builder() {
        StringBuilder sb = new StringBuilder();
        sb.append(Constants.DELIMITING_COMMA.getString()).append(companyCode)    // 公司代码
                .append(Constants.DELIMITING_COMMA.getString()).append(companyName) // 公司简称
                .append(Constants.DELIMITING_COMMA.getString()).append(time)
                .append(Constants.DELIMITING_COMMA.getString()).append(item1.getHolderName())   // 股东名称
                .append(Constants.DELIMITING_COMMA.getString()).append(item2.getHolderName())
                .append(Constants.DELIMITING_COMMA.getString()).append(item3.getHolderName())
                .append(Constants.DELIMITING_COMMA.getString()).append(item4.getHolderName())
                .append(Constants.DELIMITING_COMMA.getString()).append(item5.getHolderName())
                .append(Constants.DELIMITING_COMMA.getString()).append(item6.getHolderName())
                .append(Constants.DELIMITING_COMMA.getString()).append(item7.getHolderName())
                .append(Constants.DELIMITING_COMMA.getString()).append(item8.getHolderName())
                .append(Constants.DELIMITING_COMMA.getString()).append(item9.getHolderName())
                .append(Constants.DELIMITING_COMMA.getString()).append(item10.getHolderName())
        ;
        return sb.toString().replaceFirst(Constants.DELIMITING_COMMA.getString(), "");
    }

    public String[] builders() {
        return new String[]{companyCode, companyName, time,
                item1.getHolderName(), item2.getHolderName(),
                item3.getHolderName(), item4.getHolderName(), item5.getHolderName(), item6.getHolderName(),
                item7.getHolderName(), item8.getHolderName(), item9.getHolderName(), item10.getHolderName()};
    }
}
