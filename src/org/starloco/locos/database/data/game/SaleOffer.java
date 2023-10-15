package org.starloco.locos.database.data.game;

import org.starloco.locos.game.world.World;
import org.starloco.locos.object.ObjectTemplate;

import java.util.StringJoiner;

public final class SaleOffer {
    public final long unitPrice;
    public final ObjectTemplate itemTemplate;
    public final Currency currency;

    public SaleOffer(ObjectTemplate itemTemplate) {
        this(itemTemplate, itemTemplate.getPrice());
    }

    public SaleOffer(ObjectTemplate itemTemplate, long unitPrice) {
        this(itemTemplate, unitPrice, Currency.KAMAS);
    }

    public SaleOffer(ObjectTemplate itemTemplate, long unitPrice, Currency currency) {
        this.itemTemplate = itemTemplate;
        this.unitPrice = unitPrice;
        this.currency = currency;
    }

    public String encode() {
        // [itemID];[stats];[currency];[price];;
        StringJoiner sj = new StringJoiner(";");
        sj.add(String.valueOf(itemTemplate.getId()));
        sj.add(itemTemplate.getStrTemplate());
        sj.add(currency.isItem()?String.valueOf(currency.item().getId()):"");
        sj.add(Long.toString(unitPrice));
        sj.add(""); // Unknown
        sj.add(""); // Unknown

        return sj.toString();
    }

    public static final class Currency {
        public static final Currency KAMAS = new Currency(1);
        public static final Currency POINTS = new Currency(2);
        private final Object cur;

        private Currency(Object cur) { this.cur = cur; }

        public boolean isItem() { return this.cur instanceof ObjectTemplate; }

        public static Currency nonItemCurrency(int n) {
            switch(n) {
                case 1: return KAMAS;
                case 2: return POINTS;
                default:
                    throw new RuntimeException(String.format("unknown non-item currency #%d", n));
            }
        }

        public static Currency itemCurrency(int templateID) {
            ObjectTemplate t = World.world.getObjTemplate(templateID);
            if(t==null) throw new IllegalArgumentException(String.format("unknown item template #%d", templateID));
            return new Currency(t);
        }

//        int nonItem() {
//            if(isItem()) throw new RuntimeException("currency is a item");
//            return (int)this.cur;
//        }

        public ObjectTemplate item() {
            if(!isItem()) throw new RuntimeException("currency is not an item");
            return (ObjectTemplate) cur;
        }
    }
}
