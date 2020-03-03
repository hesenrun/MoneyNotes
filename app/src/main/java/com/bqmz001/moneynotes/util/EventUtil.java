package com.bqmz001.moneynotes.util;

import com.bqmz001.moneynotes.bus.RxBus;
import com.bqmz001.moneynotes.entity.EventBean;

public class EventUtil {
    public static void postEvent(int id, String msg, String para) {
        RxBus.getInstance().post(new EventBean(id, msg, para));
    }

    public static void postEvent(EventBean eventBean) {
        RxBus.getInstance().post(eventBean);
    }
}
